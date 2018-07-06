package cn.pfms.liust.pfms;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.Toast;

import cn.pfms.liust.pfms.outstore.dao.LoginUtilDao;
import cn.pfms.liust.pfms.outstore.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by liust on 17-9-17.
 */

public class LoginActivity extends Activity {
    private EditText user_id, password;
    private Button login;
    private SharedPreferences login_sp;
    private ProgressDialog waitingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        //通过id找到相应的控件
        user_id = (EditText) findViewById(R.id.userid);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        //信息储存
        login_sp = getSharedPreferences("userInfo", 0);
        String name = login_sp.getString("USER_ID", "");
        String pwd = login_sp.getString("PASSWORD", "");
        user_id.setText(name);
        password.setText(pwd);
        login.setOnClickListener(listener);
    }

    OnClickListener listener = new OnClickListener() {
        public void onClick(View v) {
            try {
                login();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private Handler handler = new Handler() {
        @Override
        //当有消息发送出来的时候就执行Handler的这个方法
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;
            int state = 0;
            try {
                state = new JSONObject(result).getInt("state");
            } catch (JSONException e) {
                state = 0;
            }
            SharedPreferences.Editor editor = login_sp.edit();
            if (state == 1) {                                             //返回1说明用户名和密码均正确
                //保存用户名和密码
                try {
                    String userId = user_id.getText().toString();    //获取当前输入的用户名和密码信息
                    String userPwd = password.getText().toString();
                    String user = null;
                    user = new JSONObject(result).getString("users");
                    editor.putString("USER_ID", userId);
                    editor.putString("PASSWORD", userPwd);
                    editor.putString("USER", user);
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);    //切换Login Activity至User Activity
                startActivity(intent);
                finish();
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();//登录成功提示
            } else if (state == 0) {
                Toast toast = Toast.makeText(LoginActivity.this, "登录失败,请检查账号密码是否正确！", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else if (state == -1) {
                Toast toast = Toast.makeText(LoginActivity.this, "网络错误！", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            //只要执行到这里就关闭对话框
            waitingDialog.dismiss();
        }
    };

    private void processThread(final String userId, final String userPwd) {
        //构建一个下载进度条
        waitingDialog = ProgressDialog.show(LoginActivity.this, "登录提醒", "正在登录，请稍等...", false);
        new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void run() {
                //在新线程里执行长耗时方法
                LoginUtilDao loginUtilDao = new LoginUtilDao();
                String result = loginUtilDao.findUserByNameAndPwd(LoginActivity.this, userId, userPwd);
                //执行完毕后给handler发送一个空消息
                Message message = Message.obtain();
                message.obj = result;
                handler.sendMessage(message);
            }
        }.start();
    }

    public void login() throws ExecutionException, InterruptedException, UnsupportedEncodingException, NoSuchAlgorithmException, JSONException, TimeoutException {
        String userId = user_id.getText().toString();    //获取当前输入的用户名和密码信息
        String userPwd = password.getText().toString();
        if (TextUtils.isEmpty(userId) || userId.length() < 1) {
            new Util().showSoftInputFromWindow(this, user_id);
            Toast toast = Toast.makeText(this, "账号格式不正确", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            if (TextUtils.isEmpty(userPwd) || userPwd.length() < 1) {
                new Util().showSoftInputFromWindow(this, password);
                Toast toast = Toast.makeText(this, "密码格式不正确", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                processThread(userId, userPwd);
            }
        }
    }


}

