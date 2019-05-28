[ ![Download](https://api.bintray.com/packages/june/woodyhi/composite-converter/images/download.svg?version=0.1.3) ](https://bintray.com/june/woodyhi/composite-converter/0.1.3/link)

# retrofit-converter


## module composite-converter 说明

#### Gradle依赖:

```
dependencies {
    implementation 'com.github.woodyhi.retrofit:composite-converter:0.1.3'
}
```


#### 示例
创建retrofit，并且使用了RxJava，CompositeConverterFactory.create(Factory f)设置默认转换工厂
```
Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(CompositeConverterFactory.create(GsonConverterFactory.create()))
                .client(okHttpClient)
                .build();
```

假设接口服务
```

public interface ApiService {
    // 使用默认转换工厂
    @GET("user/{username}")
    Observable<User> getUser(@Path("username") String username);

    // 动态指定结果转换工厂
    @ResponseConverter(ScalarsConverterFactory.class)
    @GET("text/hello")
    Observable<String> hello();

    // 响应结果动态设置转换工厂
    @ResponseConverter(ScalarsConverterFactory.class)
    @POST("send")
    Observable<String> sendUserJson(@Body User user);

    // 请求体、响应结果 都动态指定转换工厂
    @RequestConverter(SimpleXmlConverterFactory.class)
    @ResponseConverter(ScalarsConverterFactory.class)
    @POST("xml")
    Observable<String> sendUserXml(@Body User user);
}
```
`@RequestConverter` 指定请求体的转换工厂，`@ResponseConverter` 指定结果的转换工厂，可以不加注释使用默认转换工厂

注：注解中设置的XxxFactory.class，CompositeConverterFactory会调用其XxxFactory.create()方法。