package org.centerm.land.manager.api;

import com.google.gson.JsonElement;

import org.centerm.land.model.Check;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface KtbAPI {
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json;charset=utf-8",
            "Cache-Control: max-age=640000"
    })
    @POST("biller/gov/check")
    Observable<Response<JsonElement>> checkQr(@Body Check check);
}
