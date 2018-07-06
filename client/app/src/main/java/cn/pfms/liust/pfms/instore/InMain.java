package cn.pfms.liust.pfms.instore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import cn.pfms.liust.pfms.MainActivity;
import cn.pfms.liust.pfms.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import android.app.Fragment;
import android.widget.Toast;

public class InMain extends Fragment implements AdapterView.OnItemSelectedListener,InstoreListView.refresfListener {
    //数据的变量命名方式和服务端一样直接中文首字母，以instore连接的变量代表的是界面的组件或其他如适配器
    private Spinner instore_spinner;   //来源单号的下拉列表组件
    public ArrayAdapter<String> instore_spinner_adapter;  //下拉列表的适配器
    public TextView instore_kcdmc;   //库存点文本显示组件
    public TextView instore_rkly;   //入库来源文本显示组件
    private InstoreListView instore_listview;   //容器信息的列表显示组件
    public InstoreListViewAdapter instore_listview_adapter;  //列表显示的适配器
    private Context instore_listview_context;   //列表显示的内容
    private Button instore_save;   //保存按钮
    private String user;
    private static String userid;
    private static String kcdid;
    private static String lyid;    //来源单号
    public static String kcdmc;   //库存点名称
    public static String rkly;   //入库来源
    private static ArrayList<Map<String,Object>> lyidList;  //所有来源单号
    private static ArrayList<String> lyidList_adapter;
    private static ArrayList<Map<String,Object>> rqInfoList;   //所有容器信息
    public static String saveTab;   //保存标记
    private MainActivity mainActivity;
    @SuppressLint("ValidFragment")
    public InMain(MainActivity mainActivity, String user) {
        this.mainActivity = mainActivity;
        this.user = user;
    }
    public InMain(){
    }
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==0){
                kcdmc=(String) msg.obj;
                instore_kcdmc.setText(kcdmc);
                instore_save.setEnabled(false);
                if(instore_spinner_adapter!=null) {
                    instore_spinner_adapter.notifyDataSetChanged();
                }
            }else if(msg.what==1){
                kcdmc=(String) msg.obj;
                instore_kcdmc.setText(kcdmc);
                instore_save.setEnabled(true);
                if(instore_spinner_adapter!=null) {
                    instore_spinner_adapter.notifyDataSetChanged();
                }
            }else if(msg.what==2){
                rkly=(String) msg.obj;
                instore_rkly.setText(rkly);
                if(instore_listview_adapter!=null) {
                    instore_listview_adapter.notifyDataSetChanged();
                   // System.out.println("listviewAdapter不为null");
                }else{
                   // System.out.println("listviewAdapter为null");

                }
            }else if(msg.what==3){
                String savaTab=(String) msg.obj;
                if (savaTab.equals("1")) {
                    Toast.makeText(mainActivity, "入库成功！", Toast.LENGTH_SHORT).show();
                   // savePrompt("入库成功！");
                } else if (savaTab.equals("2")) {
                    Toast.makeText(mainActivity, "此单号已入库，请不要重复保存！", Toast.LENGTH_SHORT).show();
                    //savePrompt("此单号已入库，请不要重复保存");
                } else {
                    Toast.makeText(mainActivity, "入库失败，请重试！", Toast.LENGTH_SHORT).show();
                    //savePrompt("入库失败，请重试");
                }
            }else if(msg.what==4){
                Toast.makeText(mainActivity, "服务器连接错误！", Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }

    };
    private OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(300,TimeUnit.SECONDS)
            .readTimeout(300,TimeUnit.SECONDS).build(); //用来向服务端发送请求
    Gson gson = new Gson();   //进行json数据转换
    public static final MediaType JSON= MediaType.parse("application/json; charset=utf-8");
    public static final Type hashType=new TypeToken<HashMap<String,Object>>(){}.getType();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_instore, container, false);
        //UI组件初始化与事件绑定
        init(view);
        return view;
    }


    //初始化控件和数据
    public void init(View view){
        instore_spinner = (Spinner)  view.findViewById(R.id.instore_spinner);
        instore_kcdmc=(TextView)  view.findViewById(R.id.instore_kcdmc);
        instore_rkly=(TextView)  view.findViewById(R.id.instore_rkly);
        instore_listview=(InstoreListView)  view.findViewById(R.id.instore_listview);
        instore_listview_context=mainActivity;
        instore_save=(Button)  view.findViewById(R.id.instore_save);
        //kcdid="MAF";    //暂时没法获取页面保存的kcdid，先写点死数据
        //userid="zxk";  //暂时没法获取页面保存的userid，先写点死数据
        getKcdidAndUserid(user); /**** 若与登录部分在一起时用此方法获得数据！！！****/
        kcdmc="";
        lyidList = new ArrayList<Map<String,Object>>();
        lyidList_adapter=new ArrayList<String>();
        rkly="";
        rqInfoList = new ArrayList<Map<String,Object>>();
        saveTab="";
        getDataOfSpinner();
        setAdapterOfSpinner();
        setAdapterOfListView();
        instore_listview.setOnRefreshListener(this);
        saveInstoreInfo();
    }

    /***********下拉列表开始**************/
    //加载下拉列表数据
    public void getDataOfSpinner(){
        //输入数据kcdid并存入map然后转为json
        Map<String,Object> map=new HashMap<String,Object>();  //将所需发送给服务端的数据先存入map
        map.put("kcdid",kcdid);
        String data=gson.toJson(map);
        //发送请求
        RequestBody requestBody = RequestBody.create(JSON,data);   //将map转为json发送给服务端
        Request request = new Request.Builder().url("http://116.196.69.87:9999/mpfm/GetLyidListAndKcdmc").post(requestBody).build();

        Call call=okHttpClient.newCall(request);
        //启用多线程，线程排队
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                System.out.println("Unexpected code " + e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call,final Response response) throws IOException {
                final String responseData=response.body().string();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //System.out.println("打印POST响应的数据：" + responseData);
                            Map<String, Object> instoreLyidListAndKcdmc = gson.fromJson(responseData, hashType);  //将服务端传过来的书转为指定格式的map
                            lyidList = (ArrayList<Map<String, Object>>) instoreLyidListAndKcdmc.get("lyidList");
                            kcdmc = ((String) instoreLyidListAndKcdmc.get("kcdmc"));
                            lyidList_adapter.clear();
                            if(lyidList.size()>0) {
                                for (Map<String, Object> map : lyidList) {
                                    lyidList_adapter.add((String) map.get("ckid"));
                                }
                                Message message = handler.obtainMessage(1, kcdmc);
                                handler.sendMessage(message);
                            }else{
                                lyidList_adapter.add("暂无入库来源单");
                                Message message = handler.obtainMessage(0, kcdmc);
                                handler.sendMessage(message);
                            }
                        }catch (Exception e){
                            Message message=handler.obtainMessage(4,null);
                            handler.sendMessage(message);
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });

    }

    //下拉列表设置适配器并绑定监听
    public void setAdapterOfSpinner(){
        //适配器
        instore_spinner_adapter= new ArrayAdapter<String>(mainActivity, android.R.layout.simple_spinner_item, lyidList_adapter);
        //设置样式
        instore_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        instore_spinner.setAdapter(instore_spinner_adapter);

        // 设置监听器
        instore_spinner.setOnItemSelectedListener(this);
    }

    //下拉列表选定某选项时触发
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        lyid=parent.getSelectedItem().toString();
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("lyid",lyid);
        String data=gson.toJson(map);
        RequestBody requestBody = RequestBody.create(JSON,data);
        final Request request = new Request.Builder().url("http://116.196.69.87:9999/mpfm/GetRqInfoAndRkly").post(requestBody).build();

        Call call=okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Unexpected code " + e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseData=response.body().string();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //System.out.println("打印POST响应的数据：" + responseData);
                            Map<String, Object> instoreInfoFromLyid = gson.fromJson(responseData, hashType);
                            ArrayList<Map<String, Object>> rqInfoList1 = (ArrayList<Map<String, Object>>) instoreInfoFromLyid.get("rqInfoList");
                            rqInfoList.clear();
                            for (Map<String, Object> map : rqInfoList1) {
                                Map<String, Object> map1 = new HashMap<String, Object>();
                                map1.put("wpid", (String) map.get("wpid"));
                                map1.put("rqxh", (String) map.get("rqxh"));
                                map1.put("rqys", ((Double) map.get("rqys")).intValue());
                                map1.put("rqss", ((Double) map.get("rqss")).intValue());
                                rqInfoList.add(map1);
                            }
                          //  System.out.println("rqInfoListSize---：" + (String) rqInfoList.get(0).get("rqxh"));
                            rkly = ((String) instoreInfoFromLyid.get("rkly"));
                            Message message = handler.obtainMessage(2, rkly);
                            handler.sendMessage(message);
                        }catch (Exception e){
                            Message message=handler.obtainMessage(4,null);
                            handler.sendMessage(message);
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

    }

    //下拉列表未选定选项时触发
    public void onNothingSelected(AdapterView<?> parent) {
        String instore_lyid=parent.getSelectedItem().toString();
        instore_kcdmc.setText(kcdmc);
        instore_rkly.setText(rkly);
    }
    /***********下拉列表结束**************/


    /***********ListView开始**************/

    //ListView设置适配器并绑定
    public void setAdapterOfListView(){
        //适配器
        instore_listview_adapter= new InstoreListViewAdapter((ArrayList<Map<String,Object>>)rqInfoList,instore_listview_context);
        //加载适配器
        instore_listview.setAdapter(instore_listview_adapter);
    }

    //保存editText有改动的数值********记得加一个判断，如果改动的不符合逻辑提示错误

    //点击保存按钮触发的事件，将实收数据存入数据库
    public void saveInstoreInfo(){

        instore_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> map=new HashMap<String,Object>();
                map.put("kcdid",kcdid);
                map.put("userid",userid);
                map.put("lyid",lyid);
                map.put("kcdmc",kcdmc);
                map.put("rkly",rkly);
                map.put("rqInfoList",rqInfoList);
                String data=gson.toJson(map);

                RequestBody requestBody = RequestBody.create(JSON,data);
                final Request request = new Request.Builder().url("http://116.196.69.87:9999/mpfm/SaveInstoreInfo").post(requestBody).build();


                Call call=okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //System.out.println("Unexpected code " + e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        final String responseData=response.body().string();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                   // System.out.println("打印POST响应的数据3：" + responseData);
                                    Map<String, Object> map = gson.fromJson(responseData, hashType);
                                    String savaTab = (String) map.get("saveTab");
                                    Message message = handler.obtainMessage(3, savaTab);
                                    handler.sendMessage(message);
                                }catch (Exception e){
                                    Message message=handler.obtainMessage(4,null);
                                    handler.sendMessage(message);
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });
            }
        });
    }

    //提供刷新数据
    private void getRefreshData() {
        getDataOfSpinner();
    }

    //刷新数据
    public void refresh() {
        //获得刷新数据
        getRefreshData();
        //刷新后
        instore_listview.refreshFinish();
    }
    /***********ListView结束**************/
    /*public void savePrompt(String prompt){
        new AlertDialog.Builder(mainActivity)
                .setTitle("提示框").setMessage(prompt)
                .setPositiveButton("确定",null).show();
    }*/

    public void getKcdidAndUserid(String user1){
        // SharedPreferences user_data = getSharedPreferences("userInfo", mainActivity.MODE_PRIVATE);
        String userStr =user1;

        Map<String,Object> user=gson.fromJson(userStr,hashType);
        kcdid=(String) user.get("kcdid");
        userid=(String) user.get("userid");
    }
}