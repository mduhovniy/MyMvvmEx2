package info.duhovniy.mymvvmex.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import info.duhovniy.mymvvmex.MyApplication;
import info.duhovniy.mymvvmex.R;
import info.duhovniy.mymvvmex.model.GitService;
import info.duhovniy.mymvvmex.model.Repo;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;


public class MainViewModel implements ViewModel {

    private static final String TAG = "MainViewModel";

    public final ObservableInt infoMessageVisibility;
    public final ObservableInt progressVisibility;
    public final ObservableInt recyclerViewVisibility;
    public ObservableInt searchButtonVisibility;
    public final ObservableField<String> infoMessage;

    private Context context;
    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    private List<Repo> repositories;
    private DataListener dataListener;
    private String editTextUsernameValue;

    public MainViewModel(Context context, DataListener dataListener) {
        this.context = context;
        this.dataListener = dataListener;
        progressVisibility = new ObservableInt(View.INVISIBLE);
        searchButtonVisibility = new ObservableInt();

        editTextUsernameValue = MyApplication.get(context).getSearchHint();

/*        if (editTextUsernameValue == null || editTextUsernameValue.equals(""))
            searchButtonVisibility = new ObservableInt(View.GONE);
        else
            searchButtonVisibility = new ObservableInt(View.VISIBLE);*/

        repositories = MyApplication.get(context).getRepoList();
        if (repositories != null) {
            infoMessageVisibility = new ObservableInt(View.INVISIBLE);
            recyclerViewVisibility = new ObservableInt(View.VISIBLE);
        } else {
            infoMessageVisibility = new ObservableInt(View.VISIBLE);
            recyclerViewVisibility = new ObservableInt(View.INVISIBLE);
        }

        infoMessage = new ObservableField<>(context.getString(R.string.default_info_message));
    }

    public List<Repo> getRepositories() {
        return repositories;
    }

    @Override
    public void destroy() {
        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions())
            mCompositeSubscription.unsubscribe();
        MyApplication.get(context).saveSearchHint(editTextUsernameValue);
        editTextUsernameValue = null;
        mCompositeSubscription = null;
        context = null;
        dataListener = null;
    }

    public boolean onSearchAction(TextView view, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            if (editTextUsernameValue.length() > 0)
                loadGithubRepos(editTextUsernameValue);
            return true;
        }
        return false;
    }

/*
    public void onClickSearch(View view) {
        loadGithubRepos(editTextUsernameValue);
    }
*/

    public void setObservers(EditText editText, ImageButton imageButton) {
        mCompositeSubscription.add(RxTextView.textChanges(editText)
                .subscribe(s -> {
                    editTextUsernameValue = s.toString();
                    searchButtonVisibility.set(s.length() > 0 ? View.VISIBLE : View.GONE);
                }));
        mCompositeSubscription.add(RxTextView.textChanges(editText)
                .map(String::valueOf)
                .filter(s -> (s.length() > 1))
                .debounce(1000, TimeUnit.MILLISECONDS)
                .subscribe(this::loadGithubRepos));
        mCompositeSubscription.add(RxView.clicks(imageButton)
                .subscribe(v -> loadGithubRepos(editTextUsernameValue)));
    }
/*    public TextWatcher getUsernameEditTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                editTextUsernameValue = editable.toString();
                searchButtonVisibility.set(editable.length() > 0 ? View.VISIBLE : View.GONE);
            }
        };
    }*/

    private void loadGithubRepos(String username) {
        progressVisibility.set(View.VISIBLE);
        recyclerViewVisibility.set(View.INVISIBLE);
        infoMessageVisibility.set(View.INVISIBLE);
//        if (mCompositeSubscription != null && !mCompositeSubscription.isUnsubscribed())
//            mCompositeSubscription.unsubscribe();
        final MyApplication application = MyApplication.get(context);
        GitService githubService = application.getGitService();
        mCompositeSubscription.add(githubService.publicRepositories(username)
                .subscribeOn(application.getDefaultScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Repo>>() {
                    @Override
                    public void onCompleted() {
                        if (dataListener != null)
                            dataListener.onRepositoriesChanged(repositories);
                        progressVisibility.set(View.INVISIBLE);
                        if (!repositories.isEmpty()) {
                            recyclerViewVisibility.set(View.VISIBLE);
                            MyApplication.get(context).saveRepoList(repositories);
                        } else {
                            infoMessage.set(context.getString(R.string.text_empty_repos));
                            infoMessageVisibility.set(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "Error loading GitHub repos ", error);
                        progressVisibility.set(View.INVISIBLE);
                        if (isHttp404(error)) {
                            infoMessage.set(context.getString(R.string.error_username_not_found));
                        } else {
                            infoMessage.set(context.getString(R.string.error_loading_repos));
                        }
                        infoMessageVisibility.set(View.VISIBLE);
                    }

                    @Override
                    public void onNext(List<Repo> repositories) {
                        Log.i(TAG, "Repos loaded " + repositories);
                        MainViewModel.this.repositories = repositories;
                    }
                }));
    }

    private static boolean isHttp404(Throwable error) {
        return error instanceof HttpException && ((HttpException) error).code() == 404;
    }

    public interface DataListener {
        void onRepositoriesChanged(List<Repo> repositories);
    }
}
