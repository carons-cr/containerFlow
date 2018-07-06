package cn.pfms.liust.pfms.query.main;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.pfms.liust.pfms.MainActivity;
import cn.pfms.liust.pfms.R;
import cn.pfms.liust.pfms.query.adapter.OqueryAdapter;
import cn.pfms.liust.pfms.query.bean.OutQueryListView;
import cn.pfms.liust.pfms.query.util.DatePickerUtil;
import cn.pfms.liust.pfms.query.util.GsonUtil;
import cn.pfms.liust.pfms.query.util.InQueryTask;


public class OutQueryMain extends Fragment {

    private EditText show_keyword;
    private EditText showStartTime;
    private EditText showEndTime;
    private Button btn_submit;

    private ProgressDialog dialog;
    private ListView listView;

    private List<OutQueryListView> lists_result = new ArrayList<>() ;

    OqueryAdapter outqueryAdapter;
    DatePickerUtil datePickerUtil;

    private ButtonLisener myButtonListener;
    private TimeListener myTimeListener;
    private String user;

    // 初始化MainActivity
    private MainActivity mainActivity;
    @SuppressLint("ValidFragment")
    public OutQueryMain(MainActivity mainActivity, String user) {
        this.user = user;
        this.mainActivity=mainActivity;
    }

    public OutQueryMain() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.out_query, container, false);
        //UI组件初始化与事件绑定
        initView(view);

        // 监听日期和查询按钮
        setListener();

        // 默认显示当天日期
        datePickerUtil.setDateTime(showStartTime);

        // 默认显示当天出库数据
        initData();
        return view;
    }


    /**
     * 初始化控件及工具类实例化
     */
    private void initView(View view) {
        show_keyword = (EditText) view.findViewById(R.id.keyword_out);
        showStartTime= (EditText) view.findViewById(R.id.start_date_out);
        showEndTime = (EditText) view.findViewById(R.id.end_date_out);
        btn_submit = (Button) view.findViewById(R.id.btn_submit_out);
        listView = (ListView) view.findViewById(R.id.listView_item_out);

        dialog = new ProgressDialog(mainActivity);
        datePickerUtil = new DatePickerUtil();
    }

    private void setListener() {
        // 时间日期监听
        myTimeListener = new TimeListener();
        showStartTime.setOnClickListener(myTimeListener);
        showEndTime.setOnClickListener(myTimeListener);

        // 查询按钮监听
        myButtonListener = new ButtonLisener();
        btn_submit.setOnClickListener(myButtonListener);
    }

    /**
     * 打开出库界面，默认显示当天出库信息
     */
    public void initData(){
        lists_result.clear();
        String end_date = showEndTime.getText().toString();

        Gson gs = new  Gson();
        Map<String,String> map = gs.fromJson(user, Map.class);
        String kcdid_user = map.get("kcdid");

        // 测试数据==测试数据库中只有171107的数据
//        String kcdid_user = "99900W0406B-000";

        String keywordJson  = stringtoJson("","",end_date,kcdid_user);
        String resultJson = getJsonResult(keywordJson);
        List<OutQueryListView> list_result = gsontoList(resultJson);
        if (list_result == null) {
            Toast.makeText(mainActivity, "网络中断，请求发送失败", Toast.LENGTH_SHORT).show();
        } else if(list_result.size() == 0){
            Toast.makeText(mainActivity, "当天没有出库信息", Toast.LENGTH_SHORT).show();
        }else {
            // 添加到listview
            listmain(list_result);
        }
    }

    // 按钮监听
    class ButtonLisener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // 清除上一次数据
            lists_result.clear();
            listmain(lists_result);

            String start_date = showStartTime.getText().toString();
            String input_keyWord = show_keyword.getText().toString();
            String end_date ;
            int tag = datePickerUtil.judgeDate(showStartTime,showEndTime);
            if (tag == 0){
                end_date = "";
                showEndTime.setText("");
                Toast.makeText(mainActivity, "结束日填写错误，已将结束日清零", Toast.LENGTH_LONG).show();

            }else if(tag == 1){
                Date day = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                start_date = sdf.format(day);
                showStartTime.setText(start_date);
                end_date = "";
                Toast.makeText(mainActivity, "开始日超前，已将开始日置为今日", Toast.LENGTH_LONG).show();


            }else if(tag == 2){
                Date day = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                end_date = sdf.format(day);
                showEndTime.setText(end_date);
                Toast.makeText(mainActivity, "结束日超前，已将结束日置为今日", Toast.LENGTH_LONG).show();

            }else {
                end_date = showEndTime.getText().toString();
            }
            // 获取user的kcdid
            Gson gs = new  Gson();
            Map<String,String> map = gs.fromJson(user, Map.class);
            String kcdid_user = map.get("kcdid");

            // 测试数据
//            String kcdid_user = "99900W0406B-000";

            // string==json  json==arrylist
            String keywordJson  = stringtoJson(input_keyWord,start_date,end_date,kcdid_user);
            String resultJson = getJsonResult(keywordJson);
            List<OutQueryListView> list_result = gsontoList(resultJson);
            if (list_result == null) {
                Toast.makeText(mainActivity, "网络中断，请求发送失败", Toast.LENGTH_SHORT).show();
            } else if(list_result.size() == 0){
                Toast.makeText(mainActivity, "数据库中没有相关信息", Toast.LENGTH_SHORT).show();
            }else {
                // 添加到listview
                listmain(list_result);
            }
        }
    }


    // 文本框监听
    class TimeListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.start_date_out:
                    DatePickerDialog datePickerDialog = new DatePickerDialog(mainActivity, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                            showStartTime.setText(new StringBuilder().append(year).append("-")
                                    .append((monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : (monthOfYear + 1)).append("-")
                                    .append((dayOfMonth < 10) ? "0" + dayOfMonth : dayOfMonth));
                        }
                    },datePickerUtil.getmYear(), datePickerUtil.getmMonth(),datePickerUtil.getmDay());
                    datePickerDialog.show();
                    break;

                case R.id.end_date_out:
                    DatePickerDialog datePickerDialog2 = new DatePickerDialog(mainActivity, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                            showEndTime.setText(new StringBuilder().append(year).append("-")
                                    .append((monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : (monthOfYear + 1)).append("-")
                                    .append((dayOfMonth < 10) ? "0" + dayOfMonth : dayOfMonth));
                        }
                    },datePickerUtil.getmYear(), datePickerUtil.getmMonth(),datePickerUtil.getmDay());
                    datePickerDialog2.show();
                    break;
            }
        }
    }

    /**
     * 将用户输入的关键字和日期转为json---测试通过
     * @param keyword
     * @param start_date
     * @param end_date
     * @param kcdid
     * @return {"keyword":"","date":"2017-08-01","kcdid":"MAF"}
     */
    public String stringtoJson(String keyword,String start_date,String end_date,String kcdid){
        String json_keyword="{\'keyword':'" + keyword + "\',\'start_date\':\'" + start_date+ "\',\'end_date\':\'" + end_date + "\',\'kcdid\':\'" + kcdid + "\'}" ;
        json_keyword = json_keyword.replaceAll("\'","\"");
        return json_keyword;
    }
    /**
     * 向服务端发送关键字和日期的json，得到返回结果
     * @param {"keyword":"","date":"2017-08-01","kcdid":"MAF"}
     * @return [{ 'OutQuery_rkdh': '123', 'OutQuery_ly':'jjha', 'OutQuery_rqxh': 'aaaa','OutQuery_rks':'12'},{ 'OutQuery_rkdh': '123', 'OutQuery_ly':'jjha', 'OutQuery_rqxh': 'aaaa','OutQuery_rks':'12'}]
     */
    public String getJsonResult(String input_keyWord) {
        String resultJson = null;
        String url = "http://116.196.69.87:9999/mpfm/OutQueryByTokcdid";
        try {
            // 获取返回json数据
            resultJson = new InQueryTask(mainActivity, input_keyWord,url,dialog).execute().get();
        } catch (Exception e) {
            resultJson = "请求发送失败";
        }
        return resultJson;
    }

    /**
     * 返回的json可能是state = -1（请求发送失败--造成闪退），或者list_result（获取成功）
     *
     * @return list_result 或 null
     */
    public ArrayList<OutQueryListView> gsontoList(String ResultJson){
        ArrayList<OutQueryListView> list_result1 = new ArrayList<>();
        // 解析数据
        GsonUtil gsonUtil = new GsonUtil();
        try {
            int state = new JSONObject(ResultJson).getInt("state");
            // 网络错误，请求发送失败
            if(state == -1){
                return null;
            }else{

            }
        } catch (JSONException e) {
            // 请求发送成功
            ArrayList<OutQueryListView> list_result = gsonUtil.jsonToArrayList(ResultJson, OutQueryListView.class);
            list_result1 = list_result;
            return list_result;
        }
        return list_result1;
    }

    /**
     * 将还原成list的list_result放入listview中
     * @param lists_result
     */
    public void listmain(List<OutQueryListView> lists_result) {
        outqueryAdapter = new OqueryAdapter(mainActivity, R.layout.outquery_listview, lists_result);
        listView.setAdapter(outqueryAdapter);
    }
}
