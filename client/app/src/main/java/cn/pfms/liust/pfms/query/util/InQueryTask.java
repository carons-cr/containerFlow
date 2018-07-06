package cn.pfms.liust.pfms.query.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import java.io.IOException;
//
///**
// * 发送所有收集的数据到服务器端
// * 定义一个类，让其继承AsyncTask这个类
// * Params: String类型，表示传递给异步任务的参数类型是String，通常指定的是URL路径
// * Progress: Integer类型，进度条的单位通常都是Integer类型
// * Result：String类型，表示我们下载好的图片以JSON返回
// */
//public class InQueryTask extends AsyncTask<String, Integer, String> {
//    private Context context;
//    private String input_keyWord;
//
//
//    public InQueryTask(Context context, String input_keyWord) {
//        this.context = context;
//        this.input_keyWord = input_keyWord;
//    }
//    //onPreExecute方法用于在执行后台任务前做一些UI操作
//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//        //   在onPreExecute()中让ProgressDialog显示出来
//    }
//    //doInBackground方法内部执行后台任务,不可在此方法内修改UI
//    //post方法中  new LoginTask(context).execute(post/get,url,json)
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    @Override
//    protected String doInBackground(String... params) {
//        System.out.println("doInBackground");
//        HttpUtil httpUtil = new HttpUtil();
//
//        String resultJson = null;
//        try {
//
//            resultJson =  httpUtil.post( "http://fandongfang.free.ngrok.cc/mpfm/OutQueryByTokcdid1", input_keyWord);
//            System.out.println(resultJson);
//// json = httpUtil.post(params[0], allDate);
//            return resultJson;
//        } catch (IOException e) {
//           // Toast.makeText(context, "系统错误，请联系管理员", Toast.LENGTH_SHORT).show();
//        }
//        return null;
//    }
//    //onProgressUpdate方法用于更新进度信息
//    @Override
//    protected void onProgressUpdate(Integer... progresses) {
//
//    }
//
//    //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
//    @Override
//    protected void onPostExecute(String result){
//        // 更新listview
//
//    }
//
//    //onCancelled方法用于在取消执行中的任务时更改UI
//    @Override
//    protected void onCancelled(){
//    }
//}

//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.support.annotation.RequiresApi;
//import android.widget.Toast;
//import java.io.IOException;

/**
 * 发送所有收集的数据到服务器端
 * * 出库与登录部分所有state中1代表正常，0代表获取数据为空，-1代表出错
 * Created by liust on 17-9-23.
 */
public class InQueryTask extends AsyncTask<String, Integer, String> {
    private Context context;
    private String input_keyWord;
    private String url;
    private ProgressDialog dialog;

    String serverTag;

    public InQueryTask(Context context, String input_keyWord, String url, ProgressDialog dialog) {
        this.context = context;
        this.input_keyWord = input_keyWord;
        this.url = url;
        this.dialog = dialog;
    }
    //onPreExecute方法用于在执行后台任务前做一些UI操作
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setTitle("提示") ;
        dialog.setMessage("正在查询，请稍后···");
        dialog.show();

    }
    //doInBackground方法内部执行后台任务,不可在此方法内修改UI
    //post方法中  new LoginTask(context).execute(post/get,url,json)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... params) {
        HttpUtil httpUtil = new HttpUtil();
        String json;
        try {
            json = httpUtil.post(url, input_keyWord);
        } catch (IOException e) {
            return "{\"state\": -1}";
        }
     return json;
    }

    //onProgressUpdate方法用于更新进度信息
    @Override
    protected void onProgressUpdate(Integer... progresses) {

    }

    //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
    @Override
    protected void onPostExecute(String serverTag){
        super.onPostExecute(serverTag);
        dialog.dismiss();
    }

    //onCancelled方法用于在取消执行中的任务时更改UI
    @Override
    protected void onCancelled(){
    }
}
