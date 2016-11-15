package info.duhovniy.mymvvmex.viewmodel;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import info.duhovniy.mymvvmex.MyApplication;
import info.duhovniy.mymvvmex.R;
import info.duhovniy.mymvvmex.model.GitService;
import info.duhovniy.mymvvmex.model.Repo;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;


public class RepoViewModel implements ViewModel {

    private static final String TAG = "RepositoryViewModel";

    private Repo repository;
    private Context context;
    private Subscription subscription;

    public final ObservableField<String> ownerName;
    public final ObservableField<String> ownerEmail;
    public final ObservableInt ownerEmailVisibility;
    public final ObservableInt ownerLayoutVisibility;

    public RepoViewModel(Context context, final Repo repository) {
        this.repository = repository;
        this.context = context;
        this.ownerName = new ObservableField<>();
        this.ownerEmail = new ObservableField<>();
        this.ownerLayoutVisibility = new ObservableInt(View.INVISIBLE);
        this.ownerEmailVisibility = new ObservableInt(View.VISIBLE);
        // Trigger loading the rest of the user data as soon as the view model is created.
        // It's odd having to trigger this from here. Cases where accessing to the data model
        // needs to happen because of a change in the Activity/Fragment lifecycle
        // (i.e. an activity created) don't work very well with this MVVM pattern.
        // It also makes this class more difficult to test. Hopefully a better solution will be found
        loadFullUser(repository.owner.url);
    }

    public String getDescription() {
        return repository.description;
    }

    public String getOwnerAvatarUrl() {
        return repository.owner.avatarUrl;
    }

    @Override
    public void destroy() {
        this.context = null;
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
    }

    @BindingAdapter("bind:imageUrl")
    public static void loadImage(ImageView view, String imageUrl) {
        Picasso.with(view.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .into(view);
    }

    private void loadFullUser(String url) {
        MyApplication application = MyApplication.get(context);
        GitService githubService = application.getGitService();
        subscription = githubService.userFromUrl(url)
                .subscribeOn(application.getDefaultScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    Log.i(TAG, "Full user data loaded " + user);
                    ownerName.set(user.name);
                    ownerEmail.set(user.email);
                    ownerEmailVisibility.set(user.hasEmail() ? View.VISIBLE : View.GONE);
                    ownerLayoutVisibility.set(View.VISIBLE);
                });
    }
}
