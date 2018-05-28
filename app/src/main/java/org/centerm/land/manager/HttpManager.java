package org.centerm.land.manager;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.centerm.land.manager.api.KtbAPI;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpManager {

    private static HttpManager instance;
    private final String BASE_URL = "http://imsu.co/u/13570368/Eldercare_v2/";
    public static HttpManager getInstance() {
        if (instance == null)
            instance = new HttpManager();
        return instance;
    }
    private Context mContext;
    private KtbAPI service;

    private HttpManager() {
        mContext = Contextor.getInstance().getContext();

        /*OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .header("key", "38e610f8a15dfb13adb5d0929a7a3108")
                        .header("lang", "th")
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });
        OkHttpClient client = httpClient.build();*/
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(client)
                .build();
        service = retrofit.create(KtbAPI.class);
    }

    public KtbAPI getService() {
        return service;
    }

}
