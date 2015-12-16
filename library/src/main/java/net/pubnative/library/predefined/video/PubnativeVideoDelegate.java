package net.pubnative.library.predefined.video;

import android.content.Context;

import net.pubnative.library.PubnativeContract;
import net.pubnative.library.model.APIV3VideoAd;
import net.pubnative.library.model.NativeAdModel;
import net.pubnative.library.predefined.PubnativeActivityDelegate;
import net.pubnative.library.predefined.PubnativeActivityDelegateManager;
import net.pubnative.library.predefined.PubnativeActivityListener;
import net.pubnative.library.request.AdRequest;
import net.pubnative.library.request.VideoAdRequest;
import net.pubnative.library.request.VideoAdRequestListener;

import org.nexage.sourcekit.vast.VASTPlayer;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

/**
 * Created by davidmartin on 20/11/15.
 */

public class PubnativeVideoDelegate extends PubnativeActivityDelegate implements VideoAdRequestListener {

    private int LAUNCH_FLAGS = FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_SINGLE_TOP | FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS;
    private VideoAdRequest request;
    private VASTPlayer player;

    /**
     * Creates enables a new interstitial delegate for showing ad
     *
     * @param context   Context object
     * @param app_token App token provided by Pubnative
     * @param listener  Listener to track ad display events
     */
    public static void Create(Context context, String app_token, PubnativeActivityListener listener) {

        PubnativeVideoDelegate delegate = new PubnativeVideoDelegate(context, app_token, listener);
        PubnativeActivityDelegateManager.addDelegate(delegate);
    }

    public PubnativeVideoDelegate(Context context, String app_token, PubnativeActivityListener listener) {

        super(context, app_token, listener);
//        requestAd();
        requestStaticAd();
    }

    private void requestAd(){
        this.request = new VideoAdRequest(this.context);
        this.request.setParameter(PubnativeContract.Request.APP_TOKEN, this.app_token);
        this.request.setParameter(PubnativeContract.Request.AD_COUNT, "1");
        this.request.setParameter(PubnativeContract.Request.ICON_SIZE, "200x200");
        this.request.setParameter(PubnativeContract.Request.BANNER_SIZE, "1200x627");
        this.request.start(this);
    }

    private void requestStaticAd(){
        String staticAd = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + "<VAST version=\"2.0\"><Ad id=\"128a6.44d74.46b3\"><InLine><AdSystem version=\"1.0\">SpotXchange</AdSystem><AdTitle><![CDATA[SpotX Integration Test Secure]]></AdTitle><Description><![CDATA[SpotX Integration Test Secure]]></Description><Impression><![CDATA[https://cdn.spotxcdn.com/website/integration_test/beacons/tracking.html?vast_impression]]></Impression><Impression><![CDATA[https://cdn.spotxcdn.com/website/integration_test/beacons/tracking.html?vast_start]]></Impression><Impression><![CDATA[http://rtd.tubemogul.com/upi/pid/h0r58thg?redir=http%3A%2F%2Fsync.search.spotxchange.com%2Fpartner%3Fadv_id%3D6409%26uid%3D%24%7BUSER_ID%7D%26img%3D1]]></Impression><Impression><![CDATA[http://pm.w55c.net/ping_match.gif?ei=SPOTX&rurl=http%3A%2F%2Fsync.search.spotxchange.com%2Fpartner%3Fadv_id%3D6465%26uid%3D_wfivefivec_%26img%3D1]]></Impression><Impression><![CDATA[http://ad.turn.com/r/cs?pid=16]]></Impression><Impression><![CDATA[http://sync.tidaltv.com/Spotx.ashx]]></Impression><Impression><![CDATA[http://log.adap.tv/spotx_sync]]></Impression><Impression><![CDATA[http://pix04.revsci.net/J13421/a3/0/3/0.302?matchId=spotx]]></Impression><Impression><![CDATA[http://p.rfihub.com/cm?in=1&pub=935]]></Impression><Impression><![CDATA[http://search.spotxchange.com/beacon?_a=85394&_p=128a6.44d74.46b3&_z=1&_x=eNqtVNtu2zgQ1bfoA2xeRUpGH1o0KQLE3mKTpu2-CJRI20xlySvSubX-9t0hKTtFi31bGpA5w-HhnDNDshlCWZY1RrVDn3EiRVmIVlHKMTa8pJLjUhhC9BohVGTtVtk-G8aN6m172oUyzDjCUjLGMkZZw7nB_wmB4cfjN5OclgwmFzcZlnzG-YxgMeNF1hx63ZkKE8p4IWSZYSJVMWNMCzZjRUN_dwAGyrRRHeCpClffbUUXqmLVd1fRKm-szhe6QkAXo18GQRIXhcQCY0FwAflSUjKKSipEIRCREkngIfjCVUWV79XzcPAntN_hmCiBBi0QAeolKXkpKQZATEUpGceM0CLoVXJwB0xe5W5v-v8nQVxChoems21tnqBc_cbUa2PylDvAFwTli-MxCLb6dH2dTRrBtoMzY705BKVAM1LlQjSyaYs11JLGWhYG0sdMMkovYYQ4jKeN1tW9eQwuVOVxBWRvh-GbNbUedtA3wUkA1hk1ttuZ2w9-ynDWDrsTmDpoa_rW1CmPCHaELGnIEihsuqFRXb6wFVrE0tqdChZlQiAJLhGOPfR-fI4HVnkhEwIPCOGI1Le1f95HXYL-Xo0-zCWo16lnb3dm2o15nMBBD1aboX5QndX12o7O1-tRpbiJMoiodO2enTfAJ7ZhTHH0TTDPCbQdUPS1A8CfiDzsbZ5aNrkgr7uPb6_ehxCcQpYfWbBIsi6v7yJzsFiVf754twSiv3CArVPfunZrdifCd29vbslEGNpD14exC6YAClvv966az1vdT0WCCVRo_mgaZ72Z296bzai8DSIa5-c7o62aE4R5_TedO9MeRvNleV3j2dOuC92WaKfC19Ds7VkdjMKNCjp6M7qzG_LV5sG2JnjoJKN58ucAEvBCcnWKq9V-P1U06HOMLHeDNudWgVoOLhlHGKn74yfD8RWMc3jMshvA_ZI6M8sELxlJa__EAQGvW18_OC6n-U97Xj8nwlCqcFdAO23cNz_s80UD9Q0dSkIA5L2G_tXJHUurlVdnIcK9CmTTIzldE2h6eBww5Xy66SEiLYFQKP4D0HlPvVVue7rntGw4FsJIIrk0bcuLAiuC4PqDqVsa34vA4cfX-6uXvz4sH1cvVy9L8udu9XJ3v7q9ePzj9t092NvVh8tu-X6Dvu4-vfkX9wLNmw%7E%7E&beacon_type=start&syn%5Btiming%5D=%24TIMING_DATA]]></Impression><Error><![CDATA[http://search.spotxchange.com/exception?_a=85394&_p=128a6.44d74.46b3&_z=1&_x=eNqtVNtu2zgQ1bfoA2xeRUrGPnSRFsgi8RabS5u-EBRJx0pkSSvSadw23747pGSnaNG3MgDNIWdOzpyZEVsglGWZezZuCE3fZZxIURbCaEo5xo6XVHJcCkeI3SCEisxsddNl_Xivu8Z8H5hhxhGWkjGWMcpqzh3-JQqGP572THJaMji8vcqw5AvOFwSLBS-yet_Z1lWYUMYLIcsME6mLBWNWsAUravrzBWCgzDrdAp6ucPW1qehKV6z66ita5XVj85WtECSN0Q-LIImLQmKBsSC4AL6UlIyikgpRCESkRBLyEHzlq6LKB33o9-GI9jMcEyWkQQtEIPWSlLyUFAMgpqKUjGNGaBH1KjlcR0xe5X5w3e8hiEtguK_bxiio0FZ3905tnMsn7gBfEJSvXl6iYOubi4ts1gjC9t6N6n4flQLNSJULUcvaFBuoJU21LBzQx0wySt_Bin4Yz4GNV537HK9QlacXkN30_WPjlO130DrxkgCsd3o024Uf-jAzXJh-dwTTe9u4zjg18UhgL0eqPFKNXrXTpu9UOAwpNWB_6sdoSxCh1YfQ7NIz_FPM0wE4PTXW9epJt41Vm2b0QW1GPfnNzAFNW-UPPjiglbopNdEY6mieSJgWmAblATBfNRVaJa-nocmnzpuuoLy379-cn0UXPLlcvmfRIpP17uI2WhQsVuUf3v55CRn_kAOEzu3nzdZNbCPwm6trMicMVbZqP7bRFJDCNoTBV8ulsd2sNRxA6OVnV_smuGXTBXc_6qiZCs6H5c7ZRi8Jwlz9S5femf3oPl5eKLx43rWxaaa0p_op6FlzUgejOBhRx-BGf7oGvtY9NcbFGzrL6J7DyYFEvEhOTX5KD8Nc1XUUAXLc9da10Ux17H16gTX1RNoynD5m6YzgA3AFiB-n1soywUtGprf_0gKH19DXDafn6fxdzOt2TBWKFJsdVLPOP4Z-yFc1VPYFHEh0AM6bfh_nuU4FB3-rgz5JEAcjpjl95eY-F9CiMOCU83lUo8f0BBKh9AtApxi11X57HFRa1hwL4SSRXDpjeFFgTRDML5jW0DTwMYdv6zOD1x_-av--Xu8uv5zT9e7mcPdwjtdf_nn89HD7eHd9wz6dmee7h7s__gcLHL8R&beacon_type=exception&exception%5Bid%5D=%24EXCEPTION_ID&exception%5Bdata%5D=%24EXCEPTION_DATA&exception%5Btrace%5D=%24EXCEPTION_TRACE&syn%5Btiming%5D=%24TIMING_DATA]]></Error><Impression><![CDATA[http://flash.quantserve.com/pixel.swf?media=ad&event=played&title=spotxchange&videoId=spotxchange&pageURL=&url=&publisherId=p%2D04HdaWoZepDyg&labels=85394]]></Impression><Impression><![CDATA[http://pixel.quantserve.com/seg/r;a=p-04HdaWoZepDyg;redirect=http://search.spotxchange.com/track/bt/1/img?segs=!qcsegs]]></Impression><Impression><![CDATA[http://tags.bluekai.com/site/363]]></Impression><Impression><![CDATA[http://ad.crwdcntrl.net/5/pe=y/c=4914?http://search.spotxchange.com/track/bt/7/img?segs=${aud_ids}]]></Impression><Impression><![CDATA[http://b.scorecardresearch.com/b?c1=1&c2=6272977&c3=85394&cv=1.3&cj=1]]></Impression><Creatives><Creative sequence=\"1\"><Linear><Duration>00:00:15</Duration><TrackingEvents><Tracking event=\"firstQuartile\"><![CDATA[https://cdn.spotxcdn.com/website/integration_test/beacons/tracking.html?vast_firstQuartile]]></Tracking><Tracking event=\"midpoint\"><![CDATA[https://cdn.spotxcdn.com/website/integration_test/beacons/tracking.html?vast_midpoint]]></Tracking><Tracking event=\"thirdQuartile\"><![CDATA[https://cdn.spotxcdn.com/website/integration_test/beacons/tracking.html?vast_thirdQuartile]]></Tracking><Tracking event=\"complete\"><![CDATA[https://cdn.spotxcdn.com/website/integration_test/beacons/tracking.html?vast_complete]]></Tracking><Tracking event=\"complete\"><![CDATA[http://search.spotxchange.com/beacon?_a=85394&_p=128a6.44d74.46b3&_z=1&_x=eNqtU9ty2zYQxbfwAyRcCRCaPCRjp5MZK04TN279ggEJyEJMkSwBuXJd_3rTBSkpM-70rXgAscuDnT3nYPkCY4RQ7W3Td0hQJatSNpYxQYgXFVOCVNJT6jYY4xI1Wxs61I_3tgvN6RZBhAtMlOKcI854LYQn_1kC0EhMO1KCVRwOl1-gBYwIVbZccO4kX_CyZjmPkfO2BYzVRD8HzVZWc_0cNdNFHVyxchoDBYJfLYoVKUtFJCGSkhJ6YLTiDFdMylJiqhRW0JsUq6hLXQz2qd-nU7V_l-OyokSyElOgU9FKVIoRKEiYrBQXhFNWZg0qAelcU-giDr77fxokFXS4r9vQGH8AC7p7bzbeF3PvUL6kuFi9vGTBPv5ydQViYf38cjqLrBchINjkl0lPw3RX6aLpd0Pr0ykcWvuUwm4KqS6ImA4g9WNwvjePtg3ObMIYk9mMdsZhXeQPtGidiU8x-V2xmtyaTBpTncNzD00bfJdMhILFKmi8mlCPQyhmZ-cUyPf109sPFxlCZsj6E88RnaP3V19zxCDiuri9fLcGAV5xgKtHe2Oz9XO3ufDbLzf0SBhUdGY_tjmUQGGb0hD1ctm4bhGHPh3yAURa_uHrGJJfhi75-9GmkHX0MS133gW7pJgI8ztbRt_sR__r-sqQxWHXZlNm2tHbsdkaeBPNWR2C88PLOiY_xnMa-nX-MTQ-Z9hRRn9IZwDN9XJzZsYZOwxHUz9mEYDjrne-zeHkYx-nP7DmJzFtiEyjP51xnkYkRcUpQn9PC3I_sD82gr7Dms8z_hXgxA1cCdF0IJPz8SH1Q7GqwcoXANAMgCY3_T4PSD05DHhnkz1zJkA686oB03qTRx0sgjcJE8OEOL79jJh_gSZ4-kKh8x2ztXGbswxUYFUtiJReUSWUbxpRlsRSLGUNoWvYNEGZw193F-vDbzfvt9c_fd5d37j27uLdw_XtB3H37We8_vb5Yf3nA727advr28s3_wCGVHp4&beacon_type=complete]]></Tracking><Tracking event=\"firstQuartile\"><![CDATA[http://search.spotxchange.com/beacon?_a=85394&_p=128a6.44d74.46b3&_z=1&_x=eNqtU9tu2zgQ1bfoA2xeRYpGH1okLVpEbhf1tk1fCEqiYyKypIp01kk2v77bIeW4QBb7VhqgZoaH45lzOGyBUJZltTXN0GecSFEWojGUcowtL6nkuBSWkHaLECqyZmdcnw3Tjeld83yLZJhxhKVkjGWMsppzi_83BYYfT3smOS0ZGJefoQSUYSJNsWCsFWzBiprGOMpaazrAGIXVo1N0ZRRTj15RldeuzVetQtACRi8WQRIXhcQCY0FwATVQUjKKSipEIRCREkmoTfCVV4XKR3M_HMJztv-mY6IkWNACEWinJCUvJcWQEFNRSsYxI7SIHJQcwjEnV7kfbf97CsQlVHioO9doewQJ-hurt9bmc-2QviAoXz09RcLWf15dAVlIPT492zzyhTEQlvTS4X5MdyHrZJvDNLn-JvoS_qUz98Ht0zFROebJAK7vXGsHfWc61-qtm3zQ28nMOKTyUzbTan_vg93nqyRXUmkKdXTPRTSds33QHhLmK6fQKqHuRpfP0s4h4O_Lp9fvLyIEz5DqE4semb23V1-iR8FjKv96-aYCBl70AFdP-vpmZ-dqY-LXnzfk1DDQ2OrD1EVXQAu7EEavlsum7Rd-HMIxGs2wX_5la--CXbo-2JvJBBeJtD4s97Z1ZkkQ5voHXfpIqP1WXWm8OO67qMrctrdmanYaHkVzZgej-PIij8FO_hyGelt75xobI_REoz2GM4DEfLE4PeO0GceTqutIAvS4H1rbRTfpOPh0Amt-E2nLcJr9ZKM4jpngJSNZ9k9aEPuF_bXh7F9Ysz3jXwCeewNVnNc90NRafxuGMV_VIOUTAEgEQJHb4RAnpE4KA741wZx7xtB07KsGTGd1nHWQCN4kjAzl_PT4I2I-Ak5Q-kKi8x29M34XoxRYoGXNsRBWEsmlbRpeFNgQJEQNbtvQNEKxh78_vvuDV5uKXD_courh9vhx856vN-vuev_2dr35vqsuPrjqocHV5vrVT4UyerQ%7E&beacon_type=recurring&view_percent=25]]></Tracking><Tracking event=\"midpoint\"><![CDATA[http://search.spotxchange.com/beacon?_a=85394&_p=128a6.44d74.46b3&_z=1&_x=eNqtU9tu2zgQ1bfoA2xeRYpGH7pYL1AgNoo2bdd9ISiJjpnqtiKdxknz6-0OKccBUuzb0gA1Mzwcz5zDYQuEsiyrrKmHPuNEirIQtaGUY2x5SSXHpbCENHuEUJHVB-P6bJhuTO_q51s0w4wjLCVjLGOUVZxb_J8pMPx42jPJacnAWH-EElCGiTTFgrFGsAUrKhrjKGusaQFjFFaPTtGVUUw9ekVVXrkmXzUKQQsYvVoESVwUEguMBcEF1EBJySgqqRCFQERKJKE2wVdeFSofzWk4hudsv6djoiRY0AIRaKckJS8lxZAQU1FKxjEjtIgclBzCMSdXuR9t__8UiEuo8Fi1rtb2HiTob6zeW5vPtUP6gqB89fQUCdt-uroCspB6fHq2eeQLYyAs6aXDaUx3Ietk6-M0uf4m-hL-pTWn4Lp0TFSOeTKA6zvX2EHfmdY1eu8mH_R-MjMOqfyczTTan3ywXb5KciWVplBF91JE3TrbB-0hYb5yCq0S6m50-SztHAL-Pr9_--7PCMEzZPOeRY_M3l9Xn6NHwWMq_7L-YwMMvOoBrp719fXBztXGxG8_XpNzw0Bjo49TG10BLRxCGL1aLuumX_hxCPfRqIdu-d1W3gW7dH2wN5MJLhJpfVh2tnFmSRDm-h-69JFQ-_fmSuPFfddGVea2vTVTfdDwKOoLOxjFlxd5DHbylzDU29g7V9sYoWca7X24AEjMF4vTM06bcTyruo0kQI_d0Ng2uknHwacTWPObSFuG0-wnG8VxzAQvGcmyn2lB7AX7suHsF6zZnvGvAM-9gSrO6x5oaqz_FoYxX1Ug5RMASARAkfvhGCekSgoDvjHBXHrG0HTsqwJMa3WcdZAI3iSMDOX8_PgjYj4CTlD6QqLLHX0w_hCjFFigZcWxEFYSyaWta14U2BAkRAVuU9M0QrGHH9uHr92OfMKb2_Vpc72mX7sP3eZ2g7YPu9P2yxptr9-h3cM3tCO7N_8Cjw96eQ%7E%7E&beacon_type=recurring&view_percent=50]]></Tracking><Tracking event=\"thirdQuartile\"><![CDATA[http://search.spotxchange.com/beacon?_a=85394&_p=128a6.44d74.46b3&_z=1&_x=eNqtU11v0zAU9W_JD2j9GTuueAAxJKS1gBhffbGcxF1d0iTE7lg39teB66Qr0hBvuJLje318e885Np9hjBAqna26FgmqZJHLyjImCHGiYEqQQjpK6w3GOEfV1voWdcO1bX31eIojwgUmSnHOEWe8FMKRf5Yg8BPjjJRgBRxGF--hBYwIVTafcV5LPuN5yVIeo9rZBjBWE33vNVtYzfV90Exnpa-zRa0xUCD4yaBYkTxXRBIiKcmhB0YLznDBpMwlpkphBb1JsQg611lvj90hPlb7uxyXBSWS5ZgCnYIWolCMQEHCZKG4IJyyPGlQCEinmkJnoXft_2mQFNDhoWx8ZdwtWNBeO7NxLpt6h_I5xdni4SEJtvpweQliYX3_8LgWSS9CQLDRLxOP_XgWqg6uOgyDb69TrOBfGnuMfj9uU50RMS5A6xtfu87c2MbXZuOHEM1msBMO6-xUzdYmHEN0-2wx2jW6NMQyhecmqsa7NpoABbOF13gxom56n03WTinQ7-Pb569fJgiZIMu3PEV0il5dfkwRg4jr7NPFiyUo8IQDHD35G6qtm7pNhZ-_v6InwiBjbQ5Dk0IJFLYx9kHP51XdzkLfxdu0qLr9_Lsrg49u7tvorgcbfRLShTjfu9rbOcVEmG9sHpKg7vPy0pDZ7b5Jrky0g7NDtTVwKaqzOgSnm5d0jG4I5zT0W7sbX7mUYScZ3W08A2iql5ozE87Yvj-5ukoiAMd9V7smhaOPXRh3YEx3YpwQGd_-uMbpOSIpCk4R-jkOyP3B_pkI-gVjWk_4J4BHbuCKD6YFmWoXvsauzxYlWPkAAJoA0OSmO6QXUo4OA7620Z45EyCdeJWAaZxJbx0sgjsJT4YJcbr8CTFtgSZ4_EKh8xmztWGbsgxUYEUpiJROUSWUqyqR58RSLGUJYV2x8QklDj9Wu3ffl3cvtstds3_zcu3Xny7o-mrdrHbNdnV3Qb_s3t2tr74e17svz34DrMB77A%7E%7E&beacon_type=recurring&view_percent=75]]></Tracking></TrackingEvents><VideoClicks><ClickThrough><![CDATA[http://search.spotxchange.com/click?_a=85394&_p=128a6.44d74.46b3&_z=1&_x=eNqtU9ty0zAQ1bf4A1Kt7paHt7YMTBMeShnaF48sK4lJYntihzYt-XZY2UlgWnhDmVGs1ero7DkrMaGUEOLXlV8RyYxOlfaOcwkQZMqNhFQHxso5pVQRv3RVTZrtwtWVPx6iBISkYIwQggguCikD_BMB8CeHmRjJU4EfV7cEjJxIOWGgJ1KRYleX62CBcSGVNikBZpyaCFFqMRGq4G8DiEFJGdwa8ZwF-1JZnjkr7EtnuU2Kqkyy0lIsFuirwagBpQxoAM1AIV_OUsFpyrVWmjJjqME6tMw6q2zSun2z609ob-GETrEMrijD0lOWytRwQEDgOjVCgmBcRb1SieGIKW3StaH-PwQhRYa7Ao3JwxO6VS9CPg8hGbkjvGI0yQ6HKNjs7uaGHDXCY7subPPFLiqFmjGbaF2Ywqs5eskHL1VA-iCM4PwaR8wDOB6surwOjzFEbTLsoOy-aVZVyMtmg20Tgwxhu-C2fjnp2qY_Mpz4ZnMCc7uyCrUP-chjADsgSx5ZYgmLdVO4dZJVlmaDtdXGxRUXWlODIR2v3dX9dj9caBNlRgSICPGKkUCOovskO8VpdHbrNqEP2-4cxgvL8L3yIUb4sZnCU39OYBEvVpKPeblr27zftyFyYtlhsHfTlOFMGRk13bg44CDLvm87e3Hx-Pj4WpOL0SIC8Y1KVpR-Phd_e1nRjTEXHyO5RZSvIwohWqaCjXs_h4EJw-rVBMP2-P3Hmd_TSSiBkqPXTY0Vd6u-aZOssDAozGIC1jtH_csx3GF7JaXr3VnA2BdRpPGRH21G07C5gUt57NSYMW6hwHT4R6DzmXzpuuWpT3laSNA6GGakCd5LpcAxiu2Ly9Lzod9jDT-mz_dsxh5W9-x6eb-52z98-wCz5y-b2aXff3o_FbPL6X56-XE1_Xz17hdmKGfU]]></ClickThrough><ClickTracking><![CDATA[https://cdn.spotxcdn.com/website/integration_test/beacons/tracking.html?vast_VideoClickTracking]]></ClickTracking></VideoClicks><MediaFiles><MediaFile delivery=\"progressive\" bitrate=\"2461\" height=\"540\" width=\"960\" type=\"video/mp4\"><![CDATA[https://cdn.spotxcdn.com/website/integration_test/media/2015_q3/Spotx_red.mp4]]></MediaFile></MediaFiles></Linear></Creative></Creatives><Extensions><Extension type=\"LR-Pricing\"><Price model=\"CPM\" currency=\"USD\" source=\"spotxchange\"><![CDATA[0.1]]></Price></Extension><Extension type=\"SpotX-Count\"><total_available><![CDATA[1]]></total_available></Extension></Extensions></InLine></Ad></VAST>";
        createPlayer(staticAd);
    }

    @Override
    public void onAdRequestStarted(AdRequest request) {
        // Do nothing
    }

    @Override
    public void onAdRequestFinished(AdRequest request, ArrayList<? extends NativeAdModel> ads) {
        // Start the activity

    }

    @Override
    public void onAdRequestFailed(AdRequest request, Exception exception) {

        this.invokeListenerFailed(exception);
    }

    @Override
    public void onVideoAdRequestFinished(VideoAdRequest request, List<APIV3VideoAd> ads) {

        if (ads.size() > 0) {

            APIV3VideoAd ad = (APIV3VideoAd) ads.get(0);
            createPlayer(ad.vast_xml);

        } else {

            this.invokeListenerFailed(new Exception("Pubnative - NO FILL ERROR"));
        }
    }

    private void createPlayer(String xml){
        player = new VASTPlayer(this.context, new VASTPlayer.VASTPlayerListener() {

            @Override
            public void vastReady() {
                PubnativeVideoDelegate.this.invokeListenerStart();
                PubnativeVideoDelegate.this.invokeListenerOpened();
                PubnativeVideoDelegate.this.player.play();
            }

            @Override
            public void vastError(int error) {
                PubnativeVideoDelegate.this.invokeListenerFailed(new Exception("VASTPlayer error: " + error));
            }

            @Override
            public void vastClick() {

            }

            @Override
            public void vastComplete() {
                PubnativeVideoDelegate.this.invokeListenerClosed();
            }

            @Override
            public void vastDismiss() {
                PubnativeVideoDelegate.this.invokeListenerClosed();
            }
        });
        player.loadVideoWithData(xml);
    }
}