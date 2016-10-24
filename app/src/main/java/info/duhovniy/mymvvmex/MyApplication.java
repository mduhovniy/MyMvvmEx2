package info.duhovniy.mymvvmex;

import android.app.Application;
import android.content.Context;

import java.util.List;

import info.duhovniy.mymvvmex.model.GitService;
import info.duhovniy.mymvvmex.model.Repo;
import rx.Scheduler;
import rx.schedulers.Schedulers;


public class MyApplication extends Application {


    private Scheduler defaultScheduler;
    private GitService gitService;
    private List<Repo> repositories;
    private String searchHint;

    public static MyApplication get(Context contex) {
        return (MyApplication) contex.getApplicationContext();
    }

    public Scheduler getDefaultScheduler() {
        if(defaultScheduler == null)
            defaultScheduler = Schedulers.io();
        return defaultScheduler;
    }

    public GitService getGitService() {
        if(gitService == null)
            gitService = GitService.Factory.create(getDefaultScheduler());
        return gitService;
    }

    public void saveRepoList(List<Repo> repos) {
        repositories = repos;
    }

    public List<Repo> getRepoList() {
        return repositories;
    }

    public String getSearchHint() {
        return searchHint;
    }

    public void saveSearchHint(String hint) {
        searchHint = hint;
    }
}
