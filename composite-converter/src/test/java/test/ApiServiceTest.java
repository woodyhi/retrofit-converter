package test;

import com.woodyhi.ApiService;
import com.woodyhi.Util;
import com.woodyhi.bean.User;
import com.woodyhi.retrofit.converter.composite.CompositeConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author June.C
 * @date 2019-05-23
 */

public class ApiServiceTest {

    static MockWebServer mockWebServer;
    static ApiService apiService;

    @BeforeClass
    public static void init() {
        Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest recordedRequest) throws InterruptedException {
                switch (recordedRequest.getPath()) {
                    case "/user/linkin":
                        return new MockResponse().setHeader("Content-Type", "application/json")
                                .setBody(Util.readSources("/user.txt"));

                    case "/hello":
                        return new MockResponse().setBody("Hello World!");

                    case "/uploadJson":
                        System.out.println(recordedRequest.getPath());
                        System.out.println(recordedRequest.getHeader("Content-Type"));
                        String body = new String(recordedRequest.getBody().readByteArray());
                        System.out.println(body);
                        Assert.assertEquals(
                                "{\"name\":\"Mark Twain\",\"age\":1000,\"sex\":\"male\"}",
                                body);
                        return new MockResponse().setBody("upload json success");

                    case "/uploadXml":
                        System.out.println(recordedRequest.getPath());
                        System.out.println(recordedRequest.getHeader("Content-Type"));
                        String xmlBody = new String(recordedRequest.getBody().readByteArray());
                        System.out.println(xmlBody);
                        Assert.assertEquals(
                                "<?xml version=\"1.0\" ?><user><age>1000</age><name>Mark Twain</name><sex>male</sex></user>",
                                xmlBody);
                        return new MockResponse().setBody("upload xml success");
                }
                return null;
            }
        };
        mockWebServer = new MockWebServer();
        mockWebServer.setDispatcher(dispatcher);

        // - - - - - - - - - - - - - - - - - -

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("").toString())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(CompositeConverterFactory.create(GsonConverterFactory.create()))
                .client(okHttpClient)
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    String getUserResult;

    @Test
    public void testGetUser() {
        // 测试转换 响应结果 json -> javabean
        apiService.getUser("linkin")
                .subscribe(user -> getUserResult = user.toString());
        Assert.assertEquals(
                "User{name='Linkin Park', age=100, sex='male'}",
                getUserResult);
    }

    String helloResult;

    @Test
    public void testHello() {
        apiService.hello()
                .subscribe(s -> helloResult = s);
        Assert.assertEquals("Hello World!", helloResult);
    }

    String sendUserJsonResult;

    @Test
    public void testSendUserJson() {
        User user = new User();
        user.setName("Mark Twain");
        user.setSex("male");
        user.setAge(1000);

        apiService.sendUserJson(user)
                .subscribe(
                        s -> sendUserJsonResult = s,
                        throwable -> System.out.println("onError:" + throwable.getMessage())
                );

        Assert.assertEquals("upload json success", sendUserJsonResult);
    }

    String sendUserXmlResult;

    @Test
    public void testSendUserXml() {
        User user = new User();
        user.setName("Mark Twain");
        user.setSex("male");
        user.setAge(1000);

        apiService.sendUserXml(user)
                .subscribe(
                        s -> sendUserXmlResult = s,
                        throwable -> System.out.println("onError:" + throwable.getMessage())
                );

        Assert.assertEquals("upload xml success", sendUserXmlResult);
    }

}
