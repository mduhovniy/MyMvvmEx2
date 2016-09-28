package info.duhovniy.mymvvmex.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.view.View;

import info.duhovniy.mymvvmex.model.Repo;
import info.duhovniy.mymvvmex.view.RepositoryActivity;


public class ItemRepoViewModel extends BaseObservable implements ViewModel {
    private Repo repository;
    private Context context;

    public ItemRepoViewModel(Context context, Repo repository) {
        this.repository = repository;
        this.context = context;
    }

    public String getName() {
        return repository.name;
    }

    public String getDescription() {
        return repository.description;
    }

    public void onItemClick(View view) {
        context.startActivity(RepositoryActivity.newIntent(context, repository));
    }

    // Allows recycling ItemRepoViewModels within the recyclerview adapter
    public void setRepository(Repo repository) {
        this.repository = repository;
        notifyChange();
    }

    @Override
    public void destroy() {

        //In this case destroy doesn't need to do anything because there is not async calls
    }

}
