package cn.pfms.liust.pfms;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("cn.pfms.liust.pfms", appContext.getPackageName());
        String temp="12";
        String kcdid="12";
        String sendMessage = "{\"rqxh\": \"" + temp + "\",\"kcdid\":\"" + kcdid + "\"}";
        JSONObject jsonObject=new JSONObject(sendMessage);
        jsonObject.put("rq",temp);
        jsonObject.put("reminder",kcdid);
        String result=jsonObject.toString();
        System.out.print(result);
    }
}
