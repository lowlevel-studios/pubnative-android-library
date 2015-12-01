package net.pubnative.library.request;



import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import junit.framework.Assert;

import net.pubnative.library.BuildConfig;
import net.pubnative.library.utilities.UtilityFunction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,sdk = 19)
public class PubNativeRequestTest {
    private PubNativeRequest mPubNativeRequest;
    private PubNativeRequest.PubNativeException mPubNativeException;
    private boolean mIsLogging;
    private HashMap<String, String> mRequestParameters;
    private UtilityFunction.AndroidAdvertisingIDTask.AndroidAdvertisingIDTaskListener advertisingListener;
    private PubNativeRequest.AdType mType;
    private Response.Listener<String> reponseListener;
    private Response.ErrorListener errorListener;
    private String nativeSuccessUrl = "http://api.applift.com/api/partner/v2/promotions/native?app_token=bed5dfea7feb694967a8755bfa7f67fdf1ceb9c291ddd7b8825983a103c8b266&icon_size=150x150&country=DE&os=android&device_model=iphone&os_version=3&ad_count=1";
    private String videoSuccessUrl = "http://api.pubnative.net/api/partner/v2/promotions/native/video?android_advertiser_id=7e746627-ebae-4c87-aea6-e7554798f0fe&os=android&device_resolution=768x1184&device_type=phone&icon_size=200x200&locale=en&android_advertiser_id_sha1=6f2b84ee5bef420082986d9d476f26312430cdb1&ad_count=1&os_version=4.3&device_model=Google%20Nexus%204%20-%204.3%20-%20API%2018%20-%20768x1280&banner_size=1200x627&bundle_id=net.pubnative.library.demo&app_token=6651a94cad554c30c47427cbaf0b613a967abcca317df325f363ef154a027092&android_advertiser_id_md5=ee165be757d905e2e36ca9b927ca5ae2";
    private String failureUrl = "http://api.applift.com/api/partner/v2/promotions/native?icon_size=150x150&country=DE&os=android&device_model=iphone&os_version=3&ad_count=1";
    private String nativeSuccessResponse = "{\n" +
            "  \"status\": \"ok\",\n" +
            "  \"ads\": [\n" +
            "    {\n" +
            "      \"click_url\": \"http://t.applift.com/tracking/click?aid=4&t=KD2pIk1vO4emOBY-y4pZ3DdVPo1dWqqMBlWML0W6PzzY0QSSLx1-nJXgj4j7RACwqtVzw3hEi0EdhKVwXYm0JDbKqRHtOhaveKr7e7QgW_JAe-NHZEaS8FBlY8e_pqqOLK9Xz6K7SxbNpn0i6f3yqofV4VAwkYeanuxAH_NyfaGcEx0fKGydowpm6zRUoOkC51AP3LPi8axH_TQCpaTMyCYTiA4hCHW68qbe3jZ3FUxYixwjEqsNAkFz2owqXbWUO1wpYOXSS-EtgAW33eY9qfGmzjbykkvabzcwf7e30zqKLsd449QjGc2yXb7tQ2ET8P20plZ51bIvVsLTvseQ-vBWiFG4dYLvGAn9nWkiH8d7HpKRe6id7uA429ryYJmvOuy6AJh8Zj292iu6OUp4xehH4DMiv0iLDv-W0b6VfAymB-0E_XIbq2Uflv8H29xi9zbbX339z4PWrALm_YqyKmlooBmBLUjBfQP7t1UTFS_gBR7mOOq5BiySRxTbuDn-5FQOgx2HwuhrbJT7eh3PM3xT_fyjk5F1rtyFDrHayNbAsnScJZ_6eOfLslNc-ispB55p5lbu75vxEoesFwDwIjBtYgrkWA0u1KvV6XhdxYg0jrOu2_5-__nyzaeILplX3ZPdcT-rdcRuFSorbc4pO-QV4-YtpqcZ4WgCi85tTma6CyLqdrjagzbKHRajOw\",\n" +
            "      \"points\": 280,\n" +
            "      \"revenue_model\": \"cpa\",\n" +
            "      \"cta_text\": \"Install\",\n" +
            "      \"type\": \"native\",\n" +
            "      \"title\": \"UC Browser Mini-Fast Download\",\n" +
            "      \"description\": \"Get a great browsing experience- get it now for FREE!\",\n" +
            "      \"banner_url\": \"http://cdn.applift.com/games/promo_images_landscape/001/006/292/dimension_1200x627.jpg?20151130110026\",\n" +
            "      \"portrait_banner_url\": \"http://cdn.applift.com/games/promo_images_portrait/001/006/292/dimension_640x960.jpg?20151130110020\",\n" +
            "      \"icon_url\": \"http://cdn.applift.com/games/icons/001/006/292/dimension_150x150.jpg?20151201024706\",\n" +
            "      \"app_details\": {\n" +
            "        \"name\": \"UC Browser Mini-Fast Download\",\n" +
            "        \"publisher\": \"UCWeb Inc.\",\n" +
            "        \"developer\": \"UCWeb Inc.\",\n" +
            "        \"store_rating\": 4.5,\n" +
            "        \"category\": \"Multimedia\",\n" +
            "        \"platform\": \"Android\",\n" +
            "        \"review\": \"\",\n" +
            "        \"review_url\": null,\n" +
            "        \"review_pros\": [],\n" +
            "        \"review_cons\": [],\n" +
            "        \"version\": \"10.5.0\",\n" +
            "        \"size\": \"1.4M\",\n" +
            "        \"age_rating\": \"EveryoneLearn more\",\n" +
            "        \"store_description\": \"\",\n" +
            "        \"store_url\": null,\n" +
            "        \"release_date\": \"2015-11-19\",\n" +
            "        \"total_ratings\": 5,\n" +
            "        \"installs\": \"50,000,000 - 100,000,000\",\n" +
            "        \"store_categories\": [\n" +
            "          \"Communication\"\n" +
            "        ],\n" +
            "        \"sub_category\": null,\n" +
            "        \"store_id\": \"com.uc.browser.en\",\n" +
            "        \"url_scheme\": null\n" +
            "      },\n" +
            "      \"beacons\": [\n" +
            "        {\n" +
            "          \"type\": \"impression\",\n" +
            "          \"url\": \"http://t.applift.com/tracking/imp?aid=4&t=p54CflS_zpfqMoCZxfdXxO3-lKelX2_FV2sG8xoPc3RBBy0_p-bv9Qg6_7QGR-OQObKj3M4sTk4SpIJGxtc9daNwe0zJVZ0QEepLJBcPdO_qtBgIxt9qBb4kjzTecVxNKoIRnidmqRg4y1rBUPfaGsMqdnV1ErSqjoqB0fnQ4ZHqHs-53TZ6e5IYnJhwq84K6AY_-4E_UT5PaoCFHOZ8ADyRiJg67U1jv7m9QiG3oOi4DixbHBWECqbHLTwNRj8RW42wcpb0NTB344KCD3azdbvqoGuUVTENM7vfIvF27dJyCi6DxKeMg2xr6PEIk-fGSOE5Q6SWS0oKLdUeDpETJe--OQFyyKX_yShmScdtetvIEmPLXyx-GlhUBg\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    private String videoSuccessResponse = "{\n" +
            "  \"status\": \"ok\",\n" +
            "  \"ads\": [\n" +
            "    {\n" +
            "      \"click_url\": \"http://t.pubnative.net/tracking/click?aid=1008804&t=GD919O_KDs7hG67bsdEtCw3aD19KFjGTtDSgLGj3fgJWdhJQY7j0kH9rn5y6HyyPACMi8HS0QaVmWt5ZnRylJ--26FZIZ0hPNHZKLPC-1ysRBObFGR4rv8sf0rFeo0Oqm7lueswzjVw5uxsw4d6m6FsHXXNag_rQyEdAWahlYxS9BhruUXbz_uz8ANtIJbinBWzxxjNmwKPStSLXqOkCEzZar_ste65BGpJCTizxSQyJ4z64MZpX3O2ABV5CURPYiNirPsorMO9u6k44mIJfZMVO9XouEEGuHHheSH2ndRO1__v2Qh4dmtbSm5ZfirDksgaSkZh-punnzQrN7B8OyuzYmhcDPdHpebHs-YYawhr0DjN47fOPPpKdlE5tsSaXwUj7dPjrXgXuwixgAQvo1pj2DbKuNw0AWk1JxAd8LYw4xjDOW78Xglx9dfLfF3Q47q2xhk20l0YK7dlnsNP1-_LOJZMAttJANvXmGJY2K-UBUMj1f0PRlq0z3afuhbhb8VlgwQi1HniRDnHZbx0ReTUMy6O50qoOKXDBWF3cCTczoif__N5AJGy2f-qklncLVnVwkfYneu7WlMThqnl5l7rWUKsI6GFyr9zHxiFSV8r4otjiEJLeLMtwwA7BEcvDP9bGMnB6ifGqTEJNiNU1pgJY0nupphBYPXtWroy7gZMKirGpUIBJahmyXswBf1mozocdHdF6O8WXnUwxZ40RjQ72lq7FFJdRAbsb6tpYmaS9x5tPqk4lT4t0ceIIdB9UuhKbr2RX8QqnwazWaIA-DonR0TnMa26HwT-LXSAqnthRHunyXF9Vp_z0PeRyBsUplKa5vqfFk_3vZ6ufSxsHneuTK0xdG76WdB2GER-FQx8sSCpL0n_ck-F6PQx3X7_mZIrJo08IdAB54I3YxUmY4ItTsqibBuABAP6i7yF5o-nkoko_xC71wANIylYu7o-uRrmqCklYPXjS09m0gUbnJHgO-x7Vet0jNUSxeH7bDM31kxXKJ8SBSVIT\",\n" +
            "      \"points\": 190,\n" +
            "      \"revenue_model\": \"cpa\",\n" +
            "      \"cta_text\": \"Install\",\n" +
            "      \"type\": \"native\",\n" +
            "      \"title\": \"Slots Paradise™\",\n" +
            "      \"description\": \"\",\n" +
            "      \"banner_url\": \"http://cdn.pubnative.net/games/promo_images_landscape/001/003/708/dimension_1200x627.jpg?20150703082031\",\n" +
            "      \"portrait_banner_url\": \"http://cdn.pubnative.net/games/promo_images_portrait/001/003/708/dimension_640x960.jpg?20150703082028\",\n" +
            "      \"icon_url\": \"http://cdn.pubnative.net/games/icons/001/003/708/dimension_200x200.jpg?20151130231534\",\n" +
            "      \"app_details\": {\n" +
            "        \"name\": \"Slots Paradise™\",\n" +
            "        \"publisher\": \"ARC PLAY LTD CO.\",\n" +
            "        \"developer\": \"ARC PLAY LTD CO.\",\n" +
            "        \"store_rating\": 4.3,\n" +
            "        \"category\": \"Games - Casino\",\n" +
            "        \"platform\": \"Android\",\n" +
            "        \"review\": \"\",\n" +
            "        \"review_url\": null,\n" +
            "        \"review_pros\": [],\n" +
            "        \"review_cons\": [],\n" +
            "        \"version\": \"1.4.7.1\",\n" +
            "        \"size\": \"60M\",\n" +
            "        \"age_rating\": \"TeenSimulated GamblingLearn more\",\n" +
            "        \"store_description\": \"\",\n" +
            "        \"store_url\": null,\n" +
            "        \"release_date\": \"2015-11-26\",\n" +
            "        \"total_ratings\": 5,\n" +
            "        \"installs\": \"1,000,000 - 5,000,000\",\n" +
            "        \"store_categories\": [\n" +
            "          \"Casino\"\n" +
            "        ],\n" +
            "        \"sub_category\": null,\n" +
            "        \"store_id\": \"com.igs.slotsparadise\",\n" +
            "        \"url_scheme\": null\n" +
            "      },\n" +
            "      \"beacons\": [\n" +
            "        {\n" +
            "          \"type\": \"impression\",\n" +
            "          \"url\": \"http://t.pubnative.net/tracking/imp?aid=1008804&t=JMrTC7XrKS5QDC64FBNYuHxo0BgnK7IYMl2gF58_TaTZgnEfAePCspOjMHJ41DWstdGfpFKz1N1BpqnsGYUGSfo8J8zECwdcfjROUBeLO2On__50K30ezTB2u2GaqEy_uRNVBcXbgggXq_VNtagynjNZKtUFYIhfXCTj1dBX093hHHjNszJwNZhoSa_Flq3psS4d9cGuXxygCBhYDioLQBez4geNw_Mom9l2ow9fj26gqWwbtyBX_4Mt05QUUKyvMmvFL3y_JAuQBb7txWnc19l3X5E8aRv786i-SPAfFoa1chjcRIPB5OoqbqhGY1uIShNtB1DZ4jttrNvR_-rHktyyIJTAnfNlak8PVcRMaHFMeaMAVCml3XQ4N7Dfj7xBTS6hSYV2WjcYobgitPkp0xdOErcTcRSJSV0CFrJhP5xqkoGF4OFEiyoxnFpqX815JoaaAtldsJVANYgx8fVIdNhf_gZOu4d5hEXjP2mC9bm7T-k6Fg4aSMY399hiQoPWjcfJIuuY7GQJSnTSD5_EcWJc7OM0Jcn--YKFnKN-wUWrAc0H63v1yD1mg1YE7foGKArTo8c\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"vast\": [\n" +
            "        {\n" +
            "          \"ad\": \"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?><VAST version=\\\"2.0\\\"><Ad id=\\\"2455253\\\"><InLine><AdSystem><![CDATA[PubNative]]></AdSystem><AdTitle><![CDATA[Slots Paradise™]]></AdTitle><Description><![CDATA[Million Downloads; Excellent game on global top charts! World-famous slot game!\\nFrequent updates with amazing content make global players excited. Rank on global top charts boosted and became a popular game among countries. This is the best slot game you don’t want to miss!\\n 【Features】\\n- High quality vision and sounds built by excellent development team\\n- Tens of slot game with various themes\\n- Unique bonus games\\n- 24HRS global tournament games; spin for your country\\n- Innovative Jackpot lottery; you are the next winner\\n- Social network feature; chat or send gifts\\n- Gem center mini games; bet small to win big \\n\\n Visit facebook page to share with us and get more free coins!\\nhttps://www.facebook.com/SlotsParadise]]></Description><Impression id=\\\"Impression\\\"><![CDATA[http://t.pubnative.net/tracking/imp?aid=1008804&t=JMrTC7XrKS5QDC64FBNYuHxo0BgnK7IYMl2gF58_TaTZgnEfAePCspOjMHJ41DWstdGfpFKz1N1BpqnsGYUGSfo8J8zECwdcfjROUBeLO2On__50K30ezTB2u2GaqEy_uRNVBcXbgggXq_VNtagynjNZKtUFYIhfXCTj1dBX093hHHjNszJwNZhoSa_Flq3psS4d9cGuXxygCBhYDioLQBez4geNw_Mom9l2ow9fj26gqWwbtyBX_4Mt05QUUKyvMmvFL3y_JAuQBb7txWnc19l3X5E8aRv786i-SPAfFoa1chjcRIPB5OoqbqhGY1uIShNtB1DZ4jttrNvR_-rHktyyIJTAnfNlak8PVcRMaHFMeaMAVCml3XQ4N7Dfj7xBTS6hSYV2WjcYobgitPkp0xdOErcTcRSJSV0CFrJhP5xqkoGF4OFEiyoxnFpqX815JoaaAtldsJVANYgx8fVIdNhf_gZOu4d5hEXjP2mC9bm7T-k6Fg4aSMY399hiQoPWjcfJIuuY7GQJSnTSD5_EcWJc7OM0Jcn--YKFnKN-wUWrAc0H63v1yD1mg1YE7foGKArTo8c]]></Impression><Creatives><Creative><Linear><Duration><![CDATA[00:01:00]]></Duration><TrackingEvents><Tracking event=\\\"start\\\"><![CDATA[http://t.pubnative.net/tracking/imp?aid=1008804&t=JMrTC7XrKS5QDC64FBNYuHxo0BgnK7IYMl2gF58_TaTZgnEfAePCspOjMHJ41DWstdGfpFKz1N1BpqnsGYUGSfo8J8zECwdcfjROUBeLO2On__50K30ezTB2u2GaqEy_uRNVBcXbgggXq_VNtagynjNZKtUFYIhfXCTj1dBX093hHHjNszJwNZhoSa_Flq3psS4d9cGuXxygCBhYDioLQBez4geNw_Mom9l2ow9fj26gqWwbtyBX_4Mt05QUUKyvMmvFL3y_JAuQBb7txWnc19l3X5E8aRv786i-SPAfFoa1chjcRIPB5OoqbqhGY1uIShNtB1DZ4jttrNvR_-rHktyyIJTAnfNlak8PVcRMaHFMeaMAVCml3XQ4N7Dfj7xBTS6hSYV2WjcYobgitPkp0xdOErcTcRSJSV0CFrJhP5xqkoGF4OFEiyoxnFpqX815JoaaAtldsJVANYgx8fVIdNhf_gZOu4d5hEXjP2mC9bm7T-k6Fg4aSMY399hiQoPWjcfJIuuY7GQJSnTSD5_EcWJc7OM0Jcn--YKFnKN-wUWrAc0H63v1yD1mg1YE7foGKArTo8c]]></Tracking></TrackingEvents><MediaFiles><MediaFile height=\\\"250\\\" delivery=\\\"progressive\\\" scalable=\\\"true\\\" type=\\\"video/mp4\\\" width=\\\"300\\\"><![CDATA[http://cdn.pubnative.net/games/videos/1003708/en.mp4]]></MediaFile></MediaFiles></Linear><CompanionAds><Companion width=\\\"\\\" heigth=\\\"\\\"><StaticResource creativeType=\\\"image/jpeg\\\"><![CDATA[http://cdn.pubnative.net/games/promo_images_portrait/001/003/708/dimension_640x960.jpg?20150703082028]]></StaticResource><TrackingEvents><Tracking event=\\\"creativeView\\\"><![CDATA[http://t.pubnative.net/tracking/imp?aid=1008804&t=JMrTC7XrKS5QDC64FBNYuHxo0BgnK7IYMl2gF58_TaTZgnEfAePCspOjMHJ41DWstdGfpFKz1N1BpqnsGYUGSfo8J8zECwdcfjROUBeLO2On__50K30ezTB2u2GaqEy_uRNVBcXbgggXq_VNtagynjNZKtUFYIhfXCTj1dBX093hHHjNszJwNZhoSa_Flq3psS4d9cGuXxygCBhYDioLQBez4geNw_Mom9l2ow9fj26gqWwbtyBX_4Mt05QUUKyvMmvFL3y_JAuQBb7txWnc19l3X5E8aRv786i-SPAfFoa1chjcRIPB5OoqbqhGY1uIShNtB1DZ4jttrNvR_-rHktyyIJTAnfNlak8PVcRMaHFMeaMAVCml3XQ4N7Dfj7xBTS6hSYV2WjcYobgitPkp0xdOErcTcRSJSV0CFrJhP5xqkoGF4OFEiyoxnFpqX815JoaaAtldsJVANYgx8fVIdNhf_gZOu4d5hEXjP2mC9bm7T-k6Fg4aSMY399hiQoPWjcfJIuuY7GQJSnTSD5_EcWJc7OM0Jcn--YKFnKN-wUWrAc0H63v1yD1mg1YE7foGKArTo8c]]></Tracking></TrackingEvents></Companion><Companion width=\\\"\\\" heigth=\\\"\\\"><StaticResource creativeType=\\\"image/jpeg\\\"><![CDATA[http://cdn.pubnative.net/games/promo_images_landscape/001/003/708/dimension_1200x627.jpg?20150703082031]]></StaticResource><TrackingEvents><Tracking event=\\\"creativeView\\\"><![CDATA[http://t.pubnative.net/tracking/imp?aid=1008804&t=JMrTC7XrKS5QDC64FBNYuHxo0BgnK7IYMl2gF58_TaTZgnEfAePCspOjMHJ41DWstdGfpFKz1N1BpqnsGYUGSfo8J8zECwdcfjROUBeLO2On__50K30ezTB2u2GaqEy_uRNVBcXbgggXq_VNtagynjNZKtUFYIhfXCTj1dBX093hHHjNszJwNZhoSa_Flq3psS4d9cGuXxygCBhYDioLQBez4geNw_Mom9l2ow9fj26gqWwbtyBX_4Mt05QUUKyvMmvFL3y_JAuQBb7txWnc19l3X5E8aRv786i-SPAfFoa1chjcRIPB5OoqbqhGY1uIShNtB1DZ4jttrNvR_-rHktyyIJTAnfNlak8PVcRMaHFMeaMAVCml3XQ4N7Dfj7xBTS6hSYV2WjcYobgitPkp0xdOErcTcRSJSV0CFrJhP5xqkoGF4OFEiyoxnFpqX815JoaaAtldsJVANYgx8fVIdNhf_gZOu4d5hEXjP2mC9bm7T-k6Fg4aSMY399hiQoPWjcfJIuuY7GQJSnTSD5_EcWJc7OM0Jcn--YKFnKN-wUWrAc0H63v1yD1mg1YE7foGKArTo8c]]></Tracking></TrackingEvents></Companion></CompanionAds></Creative></Creatives></InLine></Ad></VAST>\",\n" +
            "          \"video_skip_time\": 5,\n" +
            "          \"skip_video_button\": \"Skip\",\n" +
            "          \"mute\": \"Mute\",\n" +
            "          \"learn_more_button\": \"Learn more\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";
    private String failureResponse = "{\n" +
            "  \"status\": \"error\",\n" +
            "  \"error_message\": \"Invalid app token\"\n" +
            "}";


    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        mPubNativeRequest = new PubNativeRequest(RuntimeEnvironment.application);
        mPubNativeException = new PubNativeRequest.PubNativeException();

        Field field = PubNativeRequest.class.getDeclaredField("isLogging");
        field.setAccessible(true);
        mIsLogging = (Boolean) field.get(mPubNativeRequest);

        field = PubNativeRequest.class.getDeclaredField("requestParameters");
        field.setAccessible(true);
        field.set(mPubNativeRequest, new HashMap<String, String>());
        mRequestParameters = (HashMap<String, String>) field.get(mPubNativeRequest);

        field = PubNativeRequest.class.getDeclaredField("advertisingListener");
        field.setAccessible(true);
        advertisingListener = (UtilityFunction.AndroidAdvertisingIDTask.AndroidAdvertisingIDTaskListener) field.get(mPubNativeRequest);

        field = PubNativeRequest.class.getDeclaredField("responseListener");
        field.setAccessible(true);
        reponseListener = (Response.Listener<String>) field.get(mPubNativeRequest);

        field = PubNativeRequest.class.getDeclaredField("errorListener");
        field.setAccessible(true);
        errorListener = (Response.ErrorListener) field.get(mPubNativeRequest);

    }

    @After
    public void tearDown(){
        mPubNativeRequest = null;
    }

    @Test
    public void testPubNativeRequestIsNotNull(){
        Assert.assertNotNull("Pub Native Request is not null",mPubNativeRequest);
    }

    @Test
    public void testPubNativeExceptionIsNotNull(){
        Assert.assertNotNull("Pub Native Exception is not null",mPubNativeException);
    }

    @Test
    public void testIsLoggingNotNull() {
        Assert.assertNotNull("IsLogging is not null", mIsLogging);
    }


    @Test
    public void tesSetAndGetIsLogging(){
        mPubNativeRequest.setIsLogging(true);
        boolean isLog = mPubNativeRequest.isLogging();
        Assert.assertSame("IsLogged true", isLog, true);
    }

    @Test
    public void testSetAndGetErrorMessage(){
        mPubNativeException.setErrMsg("Error Message");
        String msg = mPubNativeException.getErrMsg();

        Assert.assertSame("Error Message is same", msg, "Error Message");
    }

    @Test
    public void testSetAndGetStatus(){
        mPubNativeException.setStatus("ok");
        String status = mPubNativeException.getStatus();

        Assert.assertSame("Status is same", status, "ok");
    }

    @Test
    public void testSetAndGetStatusCode(){
        mPubNativeException.setStatusCode(100);
        int statusCode = mPubNativeException.getStatusCode();

        Assert.assertSame("Status Code is same", statusCode, 100);
    }

    @Test
    public void testSetParameters() {
        mPubNativeRequest.setParameter(PubNativeRequest.Parameters.AD_COUNT, "2");

        Assert.assertSame("Request Parameter size should be 1", mRequestParameters.size(), 1);

        mPubNativeRequest.setParameter(PubNativeRequest.Parameters.ICON_SIZE, "200x200");

        Assert.assertSame("Request Parameter size should be 2", mRequestParameters.size(), 2);

        mPubNativeRequest.setParameter(PubNativeRequest.Parameters.ICON_SIZE, null);

        Assert.assertSame("Request Parameter does not have Icon Size Key", !mRequestParameters.containsKey(PubNativeRequest.Parameters.ICON_SIZE), true);
    }

    @Test
    public void testStart() {
        PubNativeRequest.PubNativeRequestListener requestListener = Mockito.mock(PubNativeRequest.PubNativeRequestListener.class);
//        AdvertisingIdClient advertisingIdClient = Mockito.mock(AdvertisingIdClient.class);
//        AdvertisingIdClient.Info adInfo = Mockito.mock(AdvertisingIdClient.Info.class);
//       // Mockito.when()

        mPubNativeRequest.setParameter(PubNativeRequest.Parameters.ANDROID_ADVERTISER_ID, "7e746627-ebae-4c87-aea6-e7554798f0fe");
        mPubNativeRequest.start(PubNativeRequest.AdType.NATIVE, requestListener);

        Assert.assertNotNull(advertisingListener);
        Assert.assertSame("Request Parameter size should be 8", mRequestParameters.size(), 8);
    }

    @Test
    public void testOptionalParameters() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = PubNativeRequest.class.getDeclaredMethod("setOptionalParameters");
        method.setAccessible(true);
        method.invoke(mPubNativeRequest);

        Assert.assertSame("Request Parameter size should be 7", mRequestParameters.size(), 7);

    }

    @Test
    public void testCreateNetworkRequest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Field field = PubNativeRequest.class.getDeclaredField("type");
        field.setAccessible(true);
        mType = (PubNativeRequest.AdType) field.get(mPubNativeRequest);
        field.set(mPubNativeRequest, PubNativeRequest.AdType.NATIVE);

        Method method = PubNativeRequest.class.getDeclaredMethod("createNetworkRequest");
        method.setAccessible(true);
        method.invoke(mPubNativeRequest);

        field.set(mPubNativeRequest, PubNativeRequest.AdType.VIDEO);

        method = PubNativeRequest.class.getDeclaredMethod("createNetworkRequest");
        method.setAccessible(true);
        method.invoke(mPubNativeRequest);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testDefaultNetworkRequest() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Field field = PubNativeRequest.class.getDeclaredField("type");
        field.setAccessible(true);
        mType = (PubNativeRequest.AdType) field.get(mPubNativeRequest);
        field.set(mPubNativeRequest, "abc");

        Method method = PubNativeRequest.class.getDeclaredMethod("createNetworkRequest");
        method.setAccessible(true);
        method.invoke(mPubNativeRequest);

    }

    @Test
    public void testCreateNativeRequest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = PubNativeRequest.class.getDeclaredMethod("createNativeRequest");
        method.setAccessible(true);
        method.invoke(mPubNativeRequest);
    }

    @Test
    public void testCreateVideoRequest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = PubNativeRequest.class.getDeclaredMethod("createVideoRequest");
        method.setAccessible(true);
        method.invoke(mPubNativeRequest);
    }

    @Test
    public void testSendNetworkRequestSuccessResponse() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = PubNativeRequest.class.getDeclaredMethod("sendNetworkRequest", String.class);
        method.setAccessible(true);
        method.invoke(mPubNativeRequest, nativeSuccessUrl);

        reponseListener.onResponse(nativeSuccessResponse);

        method = PubNativeRequest.class.getDeclaredMethod("sendNetworkRequest", String.class);
        method.setAccessible(true);
        method.invoke(mPubNativeRequest, videoSuccessUrl);

        reponseListener.onResponse(videoSuccessResponse);
    }

    @Test
    public void testSendNetworkRequestErrorResponse() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        VolleyError volleyError = new VolleyError();
//        NetworkResponse networkResponse = volleyError.networkResponse;
//        volleyError.networkResponse.statusCode = 422;
//        Method method = PubNativeRequest.class.getDeclaredMethod("sendNetworkRequest", String.class);
//        method.setAccessible(true);
//        method.invoke(mPubNativeRequest, failureUrl);
//
//        errorListener.onErrorResponse(volleyError);
    }
}
