package com.woodyhi;

import com.woodyhi.bean.User;
import com.woodyhi.retrofit.converter.composite.RequestConverter;
import com.woodyhi.retrofit.converter.composite.ResponseConverter;

import io.reactivex.Observable;
import retrofit2.converter.jaxb.JaxbConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
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
    @GET("/hello")
    Observable<String> hello();

    @ResponseConverter(ScalarsConverterFactory.class)
    @POST("uploadJson")
    Observable<String> sendUserJson(@Body User user);

    @RequestConverter(JaxbConverterFactory.class)
    @ResponseConverter(ScalarsConverterFactory.class)
    @POST("uploadXml")
    Observable<String> sendUserXml(@Body User user);

}
