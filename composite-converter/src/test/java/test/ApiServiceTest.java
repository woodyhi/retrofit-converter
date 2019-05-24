package test;

import com.woodyhi.ApiService;
import com.woodyhi.Util;
import com.woodyhi.bean.User;
import com.woodyhi.retrofit.converter.composite.CompositeConverterFactory;

import org.junit.Assert;
import org.junit.Test;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author June.C
 * @date 2019-05-23
 */

public class ApiServiceTest {

    MockWebServer mockWebServer;
    ApiService apiService;

    String nameinfo;
    String helloInfo;
    String sendResult;

    public ApiServiceTest() {
        Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest recordedRequest) throws InterruptedException {
                switch (recordedRequest.getPath()) {
                    case "/user/linkin":
                        return new MockResponse().setHeader("Content-Type", "application/json")
                                .setBody(Util.readSources("/user.txt"));

                    case "/text/hello":
                        return new MockResponse().setBody("Hello World!");

                    case "/send":
                        return new MockResponse().setBody("send success");

                    case "/xml":
                        return new MockResponse().setBody("receive xml");
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

    @Test
    public void testGetUser() {
        // 测试转换 响应结果 json -> javabean
        apiService.getUser("linkin")
                .subscribe(user -> nameinfo = user.getName());
        Assert.assertEquals("Linkin Park", nameinfo);
    }

    @Test
    public void testHello() {
        apiService.hello()
                .subscribe(s -> helloInfo = s);
        Assert.assertEquals("Hello World!", helloInfo);
    }

    @Test
    public void testSendUser() {
        User user = new User();
        user.setName("Mark Twain");
        user.setSex("male");
        user.setAge(1000);

        apiService.sendUserJson(user)
                .subscribe(s -> sendResult = s);

        try {
            RecordedRequest recordedRequest = mockWebServer.takeRequest();
            System.out.println(recordedRequest.getRequestUrl().toString());
            System.out.println(recordedRequest.getHeader("Content-Type"));
            String body = new String(recordedRequest.getBody().readByteArray());
            System.out.println(body);
            Assert.assertEquals("{\"name\":\"Mark Twain\",\"age\":1000,\"sex\":\"male\"}", body);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals("send success", sendResult);
    }

    @Test
    public void testUserXml() {
        User user = new User();
        user.setName("Mark Twain");
        user.setSex("male");
        user.setAge(1000);

        apiService.sendUserXml(user)
                .subscribe(s -> sendResult = s);

        try {
            RecordedRequest recordedRequest = mockWebServer.takeRequest();
            System.out.println(recordedRequest.getRequestUrl().toString());
            System.out.println(recordedRequest.getHeader("Content-Type"));
            String body = new String(recordedRequest.getBody().readByteArray());
            System.out.println(body);
            Assert.assertEquals("<user>\n" +
                    "   <name>Mark Twain</name>\n" +
                    "   <age>1000</age>\n" +
                    "   <sex>male</sex>\n" +
                    "</user>", body);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals("receive xml", sendResult);
    }

}
