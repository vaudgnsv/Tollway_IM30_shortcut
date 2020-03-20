package org.centerm.Tollway.manager.api;

import com.google.gson.JsonElement;

import org.centerm.Tollway.model.Check;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface KtbAPI {
    String billerKey = "gov";
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json;charset=utf-8",
            "Cache-Control: max-age=640000"
    })
    @POST("biller/"+billerKey+"/check")
    Observable<Response<JsonElement>> checkQr(@Body Check check);
}
