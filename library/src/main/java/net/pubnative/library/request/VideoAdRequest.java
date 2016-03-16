package net.pubnative.library.request;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.google.gson.Gson;

import net.pubnative.library.model.APIV3Response;
import net.pubnative.library.model.APIV3VideoAd;
import net.pubnative.library.task.AsyncHttpTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davidmartin on 30/11/15.
 */
public class VideoAdRequest extends AdRequest {

    private final static String VIDEO_STATIC = "<VAST version=\"2.0\"><Ad id=\"128a6.44d74.46b3\"><InLine><AdTitle>SpotX Integration Test Secure</AdTitle><Description>SpotX Integration Test Secure</Description><Error>http://search.spotxchange.com/exception?_a=85394&amp;_p=128a6.44d74.46b3&amp;_z=1&amp;_x=eNqtVNtu2zgQFfZT9AEyr6IkYx-yaAosEHsLJI2bvhAUSdtsZUkrymmSNt_eHVKyk14elwIoDjlzdObMUCxDKEkS-6BtP7quTfQ2z1WOSluXFmPLS0MwVjUnWysQQnmi98q1STfsVOv0q0CeYMYLzBERKNnyUhlL9SsU9AMKhofHOSk4LRksLq8TJrKcZBixDAuS1MfWNLbChDKei6JMMClUnjFmBMtYXtNfNwADJcaqBvBUhauvrqJLVbHqq69oldbOpEtTIUgao58GQQXO8wILDN_GecExJSWjqKRC5AKRokAFB4MvfZVXaa8eu-N4QvsVjomSYEFzRCiEkZKXBcUAiKkoC8YxIzRnHOGSw3bA5FXqe9v-PwRxCQyPdeO0hArtVbuzcmttOnEH-ByhdPn8HARbv7-6SmaNIOzo7SB3x6AUaEaqdMsVLrhSv6vlWxjBD-M50HnZ2i9xq0pxfIPuuus-OytNd4DeCZsEcL1Vg95nvu_GmWKmu8MJTR2Ns622ciKCqhT4nrjywDV41VbprpXjYx9zA_rnhgx2ASo06nF0h3gMH8U8LoDTvTO2k_eqcUZu3eBHuR3U5Bc_FtGUkf7RjxZoxXaKXTSMdTDPJHQDTEfpATBdugoto9d979Kp9aYtqO_tu4u_3wQXPLms3rFgkcl6e3UbLAoWq9LN5V8ryPinHCB07j-v93ZiG4Avrm_InDCU2cjj0ARTQAr7cex9tVho085awwKEXnyxtXejXbh2tLtBBc3kaP24OFjj1AKKzOW_dOGtPg72w-pK4uzh0ISumdKe6iehafVZHYzCzQg6jnbw523ga-y90zbs0FlG-zCeHUjAC-Tk5CdV389VXQcRIMdDZ2wTzFjHzscTGFNPxCnB8W8W1wj-ANeA-GFqrSQRvGRkOvsO44_v4PAS-jLheDytX8W8TKdUoUih20E1Y_3nsevTZQ2VfQYHEhyA87Y7hgtdx4KDv1GjOksQLkZIc_rNzX0uoEXhhlPO57saPKYjkAjFNwCdY-Re-f3pptKy5lgIW5CCF1ZrnudYESREDabRNN74kMO39ZsL-vFG8_WTJnefVg-rmx3-Z_P-abW5be6eTLPefPy0ejKHu83ln_8B-KfBxw%7E%7E&amp;beacon_type=exception&amp;exception%5Bid%5D=%24EXCEPTION_ID&amp;exception%5Bdata%5D=%24EXCEPTION_DATA&amp;exception%5Btrace%5D=%24EXCEPTION_TRACE&amp;syn%5Btiming%5D=%24TIMING_DATA</Error><AdSystem version=\"1.0\">PubNative</AdSystem><Impression>https://cdn.spotxcdn.com/website/integration_test/beacons/tracking.html?vast_impression</Impression><Impression>https://cdn.spotxcdn.com/website/integration_test/beacons/tracking.html?vast_start</Impression><Impression>http://rtd.tubemogul.com/upi/pid/h0r58thg?redir=http%3A%2F%2Fsync.search.spotxchange.com%2Fpartner%3Fadv_id%3D6409%26uid%3D%24%7BUSER_ID%7D%26img%3D1</Impression><Impression>http://pm.w55c.net/ping_match.gif?ei=SPOTX&amp;rurl=http%3A%2F%2Fsync.search.spotxchange.com%2Fpartner%3Fadv_id%3D6465%26uid%3D_wfivefivec_%26img%3D1</Impression><Impression>http://ad.turn.com/r/cs?pid=16</Impression><Impression>http://sync.tidaltv.com/Spotx.ashx</Impression><Impression>http://log.adap.tv/spotx_sync</Impression><Impression>http://pix04.revsci.net/J13421/a3/0/3/0.302?matchId=spotx</Impression><Impression>http://p.rfihub.com/cm?in=1&amp;pub=935</Impression><Impression>http://search.spotxchange.com/beacon?_a=85394&amp;_p=128a6.44d74.46b3&amp;_z=1&amp;_x=eNqtVNtu2zgQFfZT9AE2r6IkYx-ycAoEiL1dxJu2eREokbbZyJJXpJO43Xx7d0jKjnt5XBqgOaOZ4ZwzM2QThJIkqbVs-i5p1lkmM1ToutAYa14ogrGsOVlrgRDKkmYrTZf0w0Z2pjl5oQQznmOOiEDJmhdSadpchEDfhcDw42FPck4LBofru4SJSUYmGLEJFiSpD51qdYkJZTwTeZFgkstswpgSbMKymv6sgBgoUVq2EE-WuPxqSjqTJSu_2pKWaW1UOlMlArgY_bAIynGW5VhguBtnOceUFIyiggqRCUTyHOUcBD6zZVame3nsD-4U7edwTBQEC5ohQsGNFLzIKYaAmIoiZxwzQjPGES44qH1MXqZ2r7v_J0FcQIaHujVNpV-gXN1GV2ut05g7hM8QSmevr56w5d-3t8nIEbgdrB6qzcEzBZyRMl1ziXMu5a9q-Q6Wt8N4dDS26vRzUJUpDv_Ae9P3j0ZXqt9B43glgbhWy6HZTuy-d2OKk6bfnaLJgzK6a3QVE0FlCvlCmtynCRg2bV_LNp2ZEs1Cbc1OeokyIVAOKuGvPXRuOIYLyzTLR_iD3pg-pAGcU-hG5M8M7I07XqjxxY0-pdjolTvu9WhlnRycP-dAdyuPzuz0eBvm4QCJPRml--pJtkZVazNYV60HGe0CqsC6VJU9WqcBf-jbAGlwtRfPCTQtUOIqCwEvgD_tTRp7PKogr_v3Vzdzb4KjyeI98xKJ0rvb-8BUBP3h-o8FAP0BA7iOjW6brd6dAN9f3a3ICBj6SVWHofWiAAhb5_a2nE4b1Y1FhQNUdPqsa2ucnprO6c0gnfEkauumO62MnEI38eofOrW6OQz64-K2wpOXXevbM8KOjVLBdDRndjDyI-h5dHqwZzXkq_STabTX0JFG_eLOBsTH88lV0a6S-_1YUc_Pa0C565U-txbUsrdReIUVxyVsCQ7PZjjD65fcQdyPsZOTRPCCkfjtG6zfvoHBm-vbhsPneL7wedtOgKFUfriAO6Xto-v36ayG-voOJd4A8l5Dv6uoDqVV0skzEX4OPdj4qo5jBUMCrwmmnI-z4S3iJyAqzIW_-OxTbaXdnh4GWtQcC6FzkvNcNw3PMiwJEqIGUTU0PDAew7-fVldoOf-L_Ll6eHyYf2LL3c3zcq4-P8w37GF19WWxWprlh2u8-Hzz-39TzNvY&amp;beacon_type=start&amp;syn%5Btiming%5D=%24TIMING_DATA</Impression><Impression>http://flash.quantserve.com/pixel.swf?media=ad&amp;event=played&amp;title=spotxchange&amp;videoId=spotxchange&amp;pageURL=&amp;url=&amp;publisherId=p%2D04HdaWoZepDyg&amp;labels=85394</Impression><Impression>http://pixel.quantserve.com/seg/r;a=p-04HdaWoZepDyg;redirect=http://search.spotxchange.com/track/bt/1/img?segs=!qcsegs</Impression><Impression>http://tags.bluekai.com/site/363</Impression><Impression>http://ad.crwdcntrl.net/5/pe=y/c=4914?http://search.spotxchange.com/track/bt/7/img?segs=${aud_ids}</Impression><Impression>http://b.scorecardresearch.com/b?c1=1&amp;c2=6272977&amp;c3=85394&amp;cv=1.3&amp;cj=1</Impression><Impression>http://t.pubnative.net/tracking/imp?aid=1007964&amp;t=u7eynuLyoWkpcPx7qYo9lwIYEjoFPy7PzrEAo157zlgpjWzC15Yb5FXdrIuhhDVObgYq9KsGd42Lx3DK9AQXNwjU70f7MSbN184U16T9RedBq_yRwsaCRimLSqCPfciY-XvrwcPTL9ji7U-DQ0Yp54cmm6zYOEGYZG5CNv77Z4nOhVVha-1Za67pi8ehN2rUmKUdkq4MzkViEY4DTlu66RMjbzYsn69r_vD9TmC410XPE_vcaFUAAISQnfxi_MpxRSe72_mg4Rwo8FlwYnpKYeG9Zm5uoE-9jhlzm5Ri38lN_3Uc9wjDX3Uml_Kga8GlsrF9Nrf8bKr2RyDf3waHMVCXnMFlxBKonw5yUz4tam8k7cpZi-t1gdTvoRgNCEI</Impression><Creatives><Creative sequence=\"1\"><Linear><Duration>00:00:15</Duration><TrackingEvents><Tracking event=\"firstQuartile\">https://cdn.spotxcdn.com/website/integration_test/beacons/tracking.html?vast_firstQuartile</Tracking><Tracking event=\"midpoint\">https://cdn.spotxcdn.com/website/integration_test/beacons/tracking.html?vast_midpoint</Tracking><Tracking event=\"thirdQuartile\">https://cdn.spotxcdn.com/website/integration_test/beacons/tracking.html?vast_thirdQuartile</Tracking><Tracking event=\"complete\">https://cdn.spotxcdn.com/website/integration_test/beacons/tracking.html?vast_complete</Tracking><Tracking event=\"complete\">http://search.spotxchange.com/beacon?_a=85394&amp;_p=128a6.44d74.46b3&amp;_z=1&amp;_x=eNqtU9ty2zYQxbfwAyRcCIKEpg_O2J3JjKVkJk7S-gUDApAFhyIZAnLsuP71JgtQUlr3tXgAd5cHO7vn7JYLjBFCrdNm6JHZVpWucOPaxhHieGMpIbrldOsExrhCZqd9j4bpTvfenF4RREpeE46pwGjLG20dM_9Igf-VAtCI5xvVnDUlGFcfoASMCK11tShLK8pFWbUsxTGyTneA0ZLIZy_ZSstSPgfJZNF6W6ysxNACwa8OxTWpqpoIQgQlVc0Jo03JcMOEqASmdY1rDg5fBVnJYtRPwyGesv03XSkaSgSrMGXwjDa8qRmBhISJpi45KSmrSo5JwyGccnJZhNH1_0-BpIEKD23njXKPIEF_59TWuWKuHdJXGBerl5dE2Obj9TWQheXzy8nmiS9CgLCsl4pPY35by8IM-7Fz8eSOnX6Kfp9dKgvCswFUP3jrBvWgO2_V1k8hqu2kZxyWRfpAidqq8BSi2xerrFYWaYptcs81mM67PqoACYuVl3iVUQ-jL2Zl5xDQ9-n9xdvLBCEzZP2-TB6dvd-vPyWPgVfK4vPVmzUQ8KoHeHqUN5idm6tNiS8-3NBjw8CiVYepS66AFnYxjkEul8b2izAO8TEZQNLym2uDj27p--juJh194tGFuNw76_USZpyrr2wZnDlM7o_1tSKLx32XRJnbDk5PZqdgJsyZHYLT4CUeo5vCOQz1WvfgjUsRdqTRPcYzgKZ8qTg145Qex6Oom0QC9LgfrOuSm3UcQv4DZx6JfCGSVz_bOG0jErwpKUJ_5wOxX9hfF0E_4Mz2jH8FOPUGqvigeqDJuvAlDmOxakHKFwDQBIAit8MhLUibFQa81VGfeybQdOqrBUznVFp1kAhmEjaGcX6c_YSYfwEnOH8h0fmN2umwS1EGLLCm5UQIV9Oa184YXlVEUyxEC641LG9Q6uGvzec121y--XJ7v7tf07f4z5u7b-8uL_jmu72_vb_CG7rGt5fm-7ubq99-AjNzfAA%7E&amp;beacon_type=complete</Tracking><Tracking event=\"firstQuartile\">http://search.spotxchange.com/beacon?_a=85394&amp;_p=128a6.44d74.46b3&amp;_z=1&amp;_x=eNqtU11z4yYU1W_RD7C5IIQkzz6ks2lnZ-J0t_uRzb4wCHDMRJZUgbN20vz1thfkeNv0tXgGcS-H43vPgWJBSJZlrVV66DO9KUtVktq2tQWwvDYUQLWcbqwghJSZ3irXZ8N0p3qnX07RDApeASdUkGzDa2Us0_-gIP-iAPzxNGcVZ3WBi8uPWALJgFaqXBSFEcWiKFsW8yQzVnWIUQ00T65hK9UUzZNvWJO3zuQr0xBsAcirQUkFZVmBABAUyooDo3XBSM2EKAWhVUUqjgFf-aZs8lEdh314YfsvXSFqCoKVhDI8RmteVwyQEJioq4JDQVlZcAI1x3Tk5E3uR9v_PwVCjRXu285paQ9oQX9n5cbafK4d6UtC8tXzcxTs-vPVFYpFmqfnlzWPegGgYMkvGY5jOousk9X7aXL9XYwr_JdOHYPbpW3a5MDTArV-cMYO8kF1zsiNm3yQm0nNONLkJzZlpD_6YHf5KtmVXJpCG8NzEbpztg_SI2G-cg1ZJdTD6PLZ2jmF-n15f_HubYTADFm_L2JE5-jnqy8xYhgVTX5z-dMaFXjVAx49-ev11s7VRuKLj5_oqWGU0cj91MVQYAvbEEbfLJfa9As_DuEQF3rYLb_b1rtgl64P9m5SwUUhrQ_LnTVOLfGSc_k7W_ooqP26vpKwOOy66Mrctrdq0luJl0Kf1QESb17UMdjJn9NYr7EPTtuYYScZ7SGcATTyxeLkjJNqHE-uXkcRsMfdYGwXw-Tj4NMOjvlOpCmD9PbTmsTnmAleFzTL_kwDcz-wPybI_sIxr2f8K8BLb-iK87JHmYz192EY81WLVj4jgEYAFrkZ9vGFtMlhxBsV1LlnwKZjXy1iOivjW0eL8E7ik2Gcny5_RMxbqAlJXyQ6n5Fb5bcxy1AFVrcchLAVrXhlteZlCYoSIVoMjWbpCcUe_ri9-cBvH6_dLf1te7v7cFi_vYf148X368ft_bfd5XH9y2e6_vSt-_Xm3Zu_AVinfZY%7E&amp;beacon_type=recurring&amp;view_percent=25</Tracking><Tracking event=\"midpoint\">http://search.spotxchange.com/beacon?_a=85394&amp;_p=128a6.44d74.46b3&amp;_z=1&amp;_x=eNqtU11v2zYU1W_RD7D5KUo0-tBhbjEgyYrVTZe8EBRJx1xkSRPp1EGav771knLcLX0tDVC8l4fH955DsgVCRVG0TpuhL8y2qnSFGtc2DmPHG0sw1i0nWycQQlVhdtr3xTDd6d6bl1O0wIzXmCMiULHljbaOmv9QoP9RYPjxPBc1pw2DxfojlIAKTGpdLRizgi1Y1dKUR4V1ugOMllg-eUlXWjL5FCSVZettubISQQsYvRoE1biqaiwwFgRXNceUNIyihgpRCUTqGtUcAr4KspLlqB-HQ3xh-5GOiYZgQStEKBwjDW9qioEQU9HUjGNGaMU4wg2HdOLksgyj639OgbiBCg9t541yR7Cgv3Nq61w51w70FULl6vk5CXb16eICxELy6fllzZNeGINg2S8VH8d8FlgnZw7T5Pu7FNfwL51-jH6ft4ksMc8L0PrBWzeoB915q7Z-ClFtJz3jkCxPbNqq8Bii25erbFd2aYptCs9FmM67PqoAhOXKS7TKqIfRl7O1cwr0u_7w9rdfEwTPkMsPLEVkjt5dXKeIQsRk-Xn9yyUo8KoHOHryN5idm6tNxG8_bsipYZDRqsPUpVBAC7sYxyCXS2P7RRiHeEwLM-yXX1wbfHRL30d3N-nok5AuxOXeWa-XcMm5-psuQxLU_Xl5ofDiuO-SK3PbwenJ7BRcCnNWB6N085KO0U3hnIZ6rXvwxqUMPcnojvEMIIkvFadmnNLjeHL1KokAPe4H67oUZh-HkHdgzHciTwXObz-vUXqOheANI0XxTx6Q-479PuHiXxjzesa_Arz0Bq74oHqQybpwH4exXLVg5TMASAJAkdvhkF5Imx0GvNVRn3vG0HTqqwVM51R662AR3El4MpTz0-VPiHkLNEH5C0TnM2qnwy5lKahAm5ZjIVxNal47Y3hVYU2QEC2E1tD8hFIPX282n9jtX_fH289rcrN5539_f31_u1mTq43dXW7WX67e_-FvNjf8dnP_5htQvn0S&amp;beacon_type=recurring&amp;view_percent=50</Tracking><Tracking event=\"thirdQuartile\">http://search.spotxchange.com/beacon?_a=85394&amp;_p=128a6.44d74.46b3&amp;_z=1&amp;_x=eNqtU9tu2zgQ5bfoA2wOL6Ikow8tki4KxEYXdS_xi0CJtE1ElrQinY2b5td3O5Qcp82-Lg1QnOHh8cw5pJhRSgiprK67ltTbNNUpzW2VWwArc8MAdCXZ1ipKaUrqvXYt6Yadbl39fEoQEDIDSZmiZCtzbSyvf6Ggv1EA_uQ4k0zyHA-T609YAiXAMp3OhDBKzERa8ZinxFjdIEYXUDy6gi90IYpHX_AiqZxJFqag2ALQV4PRDNI0AwWgGKSZBM5ywWnOlUoVZVlGM4mBXPgiLZJen7pjeGb7L51QOQPFU8o4HmO5zDMOSAhc5ZmQIBhPhaSQS0xHTlkkvrft_1Mg5FjhsWpcXdoHtKDd2XJrbTLVjvQppcni6SkKtvp8c4Ni0eLx6Xkto14AKNjoVxlO_XgWWQdbH4fBtbsYZ_gvjT4Fdxi3WZGAHBeo9b0ztivvdeNMuXWDD-V20BOOFsmZTZvSn3ywh2Qx2jW6NIQqhpci6sbZNpQeCZOFK-hiRN33LpmsnVKo35ePbz9cRQhMkOVHESM2Re9vvsSIYySK5Ov1uyUq8KoHPHr219d7O1Ubid9-WrNzwyijKY9DE0OFLexD6H0xn9emnfm-Cw9xUXeH-d-28i7YuWuD3Q06uCik9WF-sMbpOV5yWf7F5z4Kar8tb0qYPRya6MrUtrd6qPclXor6og7QePOijsEO_pLGeo29d7WNGX6W0T6EC4BFvlhcOeFK3fdnV1dRBOzx0BnbxHD0sfPjDo7pTowTgfHtj2sanyNRMheMkH_GgbkX7MsE5F8c03rCvwI894auOF-2KJOx_i50fbKo0MonBLAIwCK33TG-kGp0GPFGB33pGbDp2FeFmMaW8a2jRXgn8clwKc-XPyKmLdSEjl8kupwp99rvY5ajCjyvJChlM5bJzNa1TFPQjCpVYWhqPj6h2MOP1Xpzd7vefV9eLWH59U-2WX-AzXrjlt8_nzaHpVj98f5udXUrNle3b34CUpl8xA%7E%7E&amp;beacon_type=recurring&amp;view_percent=75</Tracking></TrackingEvents><VideoClicks><ClickThrough>http://search.spotxchange.com/click?_a=85394&amp;_p=128a6.44d74.46b3&amp;_z=1&amp;_x=eNqtVNtS2zAQ1fRT_AFBq7vl6RtlpgyBB6B08uKRZSUxSexM7DSkLd9OV3aSUuCxyowirVZnd89ZWYwoJYT4ZeUXxE-VcoqmoUgDQJBpyQBcIdk0aEqpIn7uqpo0m5mrK3-4RAkIaUBSpimZytSVgftXCPQfBMCf7GdiJE8FLr7cEqFHio2AihFoRoptXS6DBcaFVNqkBJhxaiREqcVIqIK_NyAGJWVwS8RzFuyvyvLMWWF_tZbbpKjKJCstxWKBvhmMGlDKgAaMDcpI4CwVnKZca6UpM4YaiRuZtVbZZO32zbY7or2HEzploLmijOM1lsrUcEBA4Do1QoJgXAlJIZVojpjSJu061P8nQUgxw22BwuThCdWqZyGfhpAMuSO8ojTJnp8jYdf3V1fkwBFe27Zhk8-2kSnkjNlkKh0Y6dxHWl7giH4Ah4tVm9dh15tsAv0_8u6bZlGFvGxW2DfRyBC3DW7j56N23XSHFEe-WR3R3LasQu1DPiRCbYL5Ypoypok1zJZN4ZZJVlma9dpWKxd3XGhNDZp0DLutu82-D2gTZQ7lb8Ksavo0kHOO3UjjWqB_1e1fmWGICDFiTGlIOEeVfJId7TS2wsatQhc27cmMUcrwo_IhWvih-8JTd3JgES9Wng9-uVuv826_DrEGlj33SayaMpxKxAqadtg84yDzrlu39uxst9u95fBs0JRAfNR-6rBBtPzoMUf5Bl98veQWUb4PKIRomQo2nL3g-PSCDv3uzQT98bB-defvdCQK-Y3N0dRYcbvomnWSFRZ6hll0wHqnqFc5mHs9Ste5E4GxjyJJw1fh0BYoMr4G4FIetI0ewxES3OsaA5_u5HPXzo-NzdNCgtbBMCNN8F4qBY5RrQvclp73DyTW8Pvm4XI5ebycX98tV5Pzi_nN3bfV9cNkPmbj_fhxAeOf93x8_hUmj_7zHw86d7A%7E</ClickThrough><ClickTracking>https://cdn.spotxcdn.com/website/integration_test/beacons/tracking.html?vast_VideoClickTracking</ClickTracking></VideoClicks><MediaFiles><MediaFile delivery=\"progressive\" type=\"video/mp4\" bitrate=\"2461\" width=\"960\" height=\"540\">https://cdn.spotxcdn.com/website/integration_test/media/2015_q3/Spotx_red.mp4</MediaFile></MediaFiles></Linear></Creative></Creatives><Extensions><Extension type=\"PN-Postview-Banner\"><Banner id=\"2\">http://cdn.pubnative.net/static/custom_preview_banners/uplike2.jpg</Banner></Extension></Extensions></InLine></Ad></VAST>";
    public VideoAdRequestListener videoListener;
    public APIV3Response          response;
    public int parsingAds = 0;

    /**
     * Creates a new ad request object
     *
     * @param context valid Context object
     */
    public VideoAdRequest(Context context) {

        super(context);
    }

    public void start(VideoAdRequestListener listener) {

        this.parsingAds = 0;
        this.videoListener = listener;
        APIV3VideoAd ad = new APIV3VideoAd();
        ad.revenue_model = "cpm";
        ad.points = 70;
        ad.vast_xml = VIDEO_STATIC;
        List<APIV3VideoAd> ads = new ArrayList<APIV3VideoAd>();
        ads.add(ad);
        invokeOnVideoAdRequestFinished(ads);
//        super.start(Endpoint.VIDEO, this.videoListener);

    }

    @Override
    protected String getBaseURL(){
        return "http://api.pubnative.net/api/partner/v3/promotions";
    }

    @Override
    public String toString() {

        Uri.Builder uriBuilder = Uri.parse(this.getBaseURL()).buildUpon();
        uriBuilder.appendPath(this.VIDEO_ENDPOINT_URL);

        for (String key : requestParameters.keySet()) {
            String value = requestParameters.get(key);
            if (value != null) {
                uriBuilder.appendQueryParameter(key, value);
            }
        }
        return uriBuilder.build().toString();
    }

    @Override
    public void onAsyncHttpTaskFinished(AsyncHttpTask task, String result) {

        if (!TextUtils.isEmpty(result)) {

            try {

                APIV3Response response = new Gson().fromJson(result, APIV3Response.class);

                if(response.status.equals(APIV3Response.Status.OK)){

                    invokeOnVideoAdRequestFinished(response.ads);

                } else {

                    this.invokeOnAdRequestFailed(new Exception(response.error_message));
                }

            } catch (Exception e) {

                this.invokeOnAdRequestFailed(e);
            }

        } else {

            this.invokeOnAdRequestFailed(new Exception("Pubnative - Error: empty response"));
        }
    }

    protected void invokeOnVideoAdRequestFinished(List<APIV3VideoAd> ads) {

        if (this.videoListener != null) {
            this.videoListener.onVideoAdRequestFinished(this, ads);
        }
    }
}
