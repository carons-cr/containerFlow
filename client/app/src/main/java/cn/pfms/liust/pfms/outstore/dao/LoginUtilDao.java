package cn.pfms.liust.pfms.outstore.dao;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cn.pfms.liust.pfms.outstore.util.Constants;
import cn.pfms.liust.pfms.outstore.util.HttpUtil;


/**
 * Created by liust on 17-9-17.
 * 登录的操作
 */

public class LoginUtilDao implements Constants {
    //登录处理
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String findUserByNameAndPwd(Context context, String userid, String password) {
        String loginUrl = STORE_URL + LOGIN;
        String json = idAndpwdToJson(userid, password);
        try {
            String result =new HttpUtil().post(loginUrl,json);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"state\": -1}";
        }
    }

    /*
        密码加密并转json
       */
    public String idAndpwdToJson(String userid, String password) {
        String json = "{\"userid\":\"" + userid + "\",\"password\":\"" + EncoderByMd5(password) + "\"}";
        return json;
    }

    public String EncoderByMd5(String str) {
//确定计算方法
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(str.getBytes());
            byte[] b = md5.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return str;
    }
}
