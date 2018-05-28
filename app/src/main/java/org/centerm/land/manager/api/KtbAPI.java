package org.centerm.land.manager.api;

import com.google.gson.JsonElement;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface KtbAPI {
    @FormUrlEncoded
    @POST("member/login")
    Observable<Response<JsonElement>> getStatusLogin(@Field("brand") String brand,
                                                     @Field("model") String model,
                                                     @Field("os") String os,
                                                     @Field("version") String version,
                                                     @Field("app_version") String app_version,
                                                     @Field("device token") String device_token,
                                                     @Field("username") String username,
                                                     @Field("password") String password,
                                                     @Field("lat_no") String latNo,
                                                     @Field("long_no") String longNo,
                                                     @Field("user_type") String userType);
}
