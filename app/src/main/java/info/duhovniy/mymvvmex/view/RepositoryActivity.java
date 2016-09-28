package info.duhovniy.mymvvmex.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import info.duhovniy.mymvvmex.R;
import info.duhovniy.mymvvmex.databinding.RepositoryActivityBinding;
import info.duhovniy.mymvvmex.model.Repo;
import info.duhovniy.mymvvmex.viewmodel.RepoViewModel;

public class RepositoryActivity extends AppCompatActivity {

    private static final String EXTRA_REPOSITORY = "EXTRA_REPOSITORY";

    private RepositoryActivityBinding binding;
    private RepoViewModel repositoryViewModel;

    public static Intent newIntent(Context context, Repo repository) {
        Intent intent = new Intent(context, RepositoryActivity.class);
        intent.putExtra(EXTRA_REPOSITORY, repository);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.repository_activity);
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Repo repository = getIntent().getParcelableExtra(EXTRA_REPOSITORY);
        repositoryViewModel = new RepoViewModel(this, repository);
        binding.setViewModel(repositoryViewModel);

        //Currently there is no way of setting an activity title using data binding
        setTitle(repository.name);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        repositoryViewModel.destroy();
    }
}
