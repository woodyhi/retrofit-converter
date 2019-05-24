package com.woodyhi;

import com.woodyhi.bean.User;
import com.woodyhi.retrofit.converter.composite.RequestConverter;
import com.woodyhi.retrofit.converter.composite.ResponseConverter;

import io.reactivex.Observable;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * @author June.C
 * @date 2019-05-23
 */
public interface ApiService {

    @GET("user/{username}")
    Observable<User> getUser(@Path("username") String username);

    @ResponseConverter(ScalarsConverterFactory.class)
    @GET("text/hello")
    Observable<String> hello();

    @ResponseConverter(ScalarsConverterFactory.class)
    @POST("send")
    Observable<String> sendUserJson(@Body User user);

    @RequestConverter(SimpleXmlConverterFactory.class)
    @ResponseConverter(ScalarsConverterFactory.class)
    @POST("xml")
    Observable<String> sendUserXml(@Body User user);

}
