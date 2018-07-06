package cn.pfms.liust.pfms.outstore.util;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.IOException;

/**
 * 所有state中1代表正常，0代表获取数据为空，-1代表出错
 * Created by liust on 17-11-11.
 */

public class AsyncTask extends android.os.AsyncTask<String, Integer, String> {
    private Context context;
    private String temp;
    private String http;

    public AsyncTask(Context context, String temp, String http) {
        this.context = context;
        this.temp = temp;
        this.http = http;
    }

    public AsyncTask(Context context, String http) {
        this.context = context;
        this.http = http;
    }

    //onPreExecute方法用于在执行后台任务前做一些UI操作
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    //doInBackground方法内部执行后台任务,不可在此方法内修改UI
    //post方法中  new LoginTask(context).execute(post/get,url,json)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... params) {
        HttpUtil httpUtil = new HttpUtil();
        String json = null;
        try {
            if (http.equals("post")) {
                json = httpUtil.post(params[0], temp);
            } else if (http.equals("get")) {
                json = httpUtil.get(params[0]);
            }
            return json;
        } catch (IOException e) {
            return "{\"state\": -1}";
        }
    }

    //onProgressUpdate方法用于更新进度信息
    @Override
    protected void onProgressUpdate(Integer... progresses) {
    }

    //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
    @Override
    protected void onPostExecute(String result) {

    }

    //onCancelled方法用于在取消执行中的任务时更改UI
    @Override
    protected void onCancelled() {

    }
}