package info.duhovniy.mymvvmex.model;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;
import rx.Observable;
import rx.Scheduler;


public interface GitService {


    @GET("users/{username}/repos")
    Observable<List<Repo>> publicRepositories(@Path("username") String username);

    @GET
    Observable<User> userFromUrl(@Url String userUrl);

    class Factory {
        public static GitService create(Scheduler defaultScheduler) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.github.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    // RxJavaCallAdapterFactory.create()  <- changed to async!
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(defaultScheduler))
                    .build();
            return retrofit.create(GitService.class);
        }
    }

}
