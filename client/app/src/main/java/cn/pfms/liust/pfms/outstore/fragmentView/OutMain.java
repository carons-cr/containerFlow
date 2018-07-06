package cn.pfms.liust.pfms.outstore.fragmentView;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.AdapterView.OnItemLongClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.pfms.liust.pfms.MainActivity;
import cn.pfms.liust.pfms.R;
import cn.pfms.liust.pfms.outstore.bean.ListviewOutStored;
import cn.pfms.liust.pfms.outstore.dao.OutStoreDao;
import cn.pfms.liust.pfms.outstore.util.Constants;
import cn.pfms.liust.pfms.outstore.util.HttpUtil;
import cn.pfms.liust.pfms.outstore.util.OutSqlite;
import cn.pfms.liust.pfms.outstore.util.Util;

/**
 * Created by liust on 17-10-28.
 * 出库管理
 */
public class OutMain extends Fragment implements OnItemLongClickListener, Constants {
    private Spinner out_khid, out_ckmd, out_ckxz;
    private EditText out_cph;
    private ListView listView;
    private ImageView out_delete, out_add, out_save;
    private MainActivity mainActivity;
    private List<ListviewOutStored> list = new ArrayList<ListviewOutStored>();
    private Util util = new Util();
    private OutStoreDao outStoreDao;
    private OutSqlite outSqlite;
    private int longSign = -1;//长安记录
    private OutListViewAdapter outListViewAdapter;
    private String user;
    private TextView textView01, textView03;
    private ProgressDialog waitingDialog;

    @SuppressLint("ValidFragment")
    public OutMain(MainActivity mainActivity, OutSqlite outSqlite, String user) {
        this.mainActivity = mainActivity;
        this.outSqlite = outSqlite;
        this.user = user;
    }

    public OutMain() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.out_main, container, false);
        //UI组件初始化与事件绑定
        outStoreDao = new OutStoreDao(mainActivity);
        init(view);
        dataInit();
        clear_all_list();
        listmain();
        return view;
    }

    //初始化控件
    public void init(View view) {
        listView = (ListView) view.findViewById(R.id.main);
        out_khid = (Spinner) view.findViewById(R.id.out_khid);
        out_ckmd = (Spinner) view.findViewById(R.id.out_ckmd);
        out_ckxz = (Spinner) view.findViewById(R.id.out_ckxz);
        out_cph = (EditText) view.findViewById(R.id.out_cph);
        out_add = (ImageView) view.findViewById(R.id.out_add);
        out_save = (ImageView) view.findViewById(R.id.out_save);
        out_delete = (ImageView) view.findViewById(R.id.out_delete);
        textView01 = view.findViewById(R.id.view01);
        textView03 = view.findViewById(R.id.view03);
        listView.setOnItemLongClickListener(this);
        out_add.setOnClickListener(new OutListener());
        out_save.setOnClickListener(new OutListener());
        out_delete.setOnClickListener(new OutListener());
        out_ckxz.setOnItemSelectedListener(new SpinnerListener());
        out_delete.setEnabled(false);
    }

    //初始化数据
    public void dataInit() {
        boolean bool = outStoreDao.saveInitData();
        if (bool) {
            //将数据更新到界面
            kmxAsapter();
            //  Toast.makeText(mainActivity, "数据更新成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mainActivity, "数据更新失败，请联系管理员", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    //监控出库性质变化
    private class SpinnerListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            String data = (String) out_ckxz.getItemAtPosition(position);
            if (data.equals("负载流转")) {
                textView01.setText("零件号");
                textView03.setText("容器型号");
            } else if (data.equals("空载流转") || data.equals("维修出库")) {
                textView01.setText("容器型号");
                textView03.setText("容器名");
            }
            clear_all_list();
            listmain();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    //点击了长按长按事件
    @Override
    public boolean onItemLongClick(AdapterView arg0, View vie, int position, long arg3) {
        int first = listView.getFirstVisiblePosition();
        for (int i = 0; i < listView.getChildCount(); i++) {
            View view = listView.getChildAt(i);
            view.setBackgroundColor(getResources().getColor(R.color.white));
        }
        View view = listView.getChildAt(position - first);
        TextView tv_sign = (TextView) view.findViewById(R.id.sign);
        //记录具体每行数据查找点
        String sign = tv_sign.getText().toString();
        longSign = Integer.parseInt(sign);
        view.setBackgroundColor(getResources().getColor(R.color.green));
        out_delete.setEnabled(true);
        return true;
    }

    /*删除 添加 保存按钮*/
    private class OutListener implements View.OnClickListener {
        int sign = 1;

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.out_add:
                    listView.clearFocus();
                    ListviewOutStored listviewOutStored = new ListviewOutStored("", "", "", sign);
                    add(listviewOutStored);
                    sign++;
                    out_delete.setEnabled(false);
                    break;
                case R.id.out_save:
                    listView.clearFocus();
                    out_delete.setEnabled(false);
                    String ckxz = outStoreDao.ckxzMCtoID(out_ckxz.getSelectedItem().toString());
                    String khid = out_khid.getSelectedItem().toString();
                    String tokcdid = out_ckmd.getSelectedItem().toString();
                    String cph = out_cph.getText().toString();
                    save(ckxz, khid, tokcdid, cph, user);
                    break;
                case R.id.out_delete:
                    listView.clearFocus();
                    delete(longSign);
                    out_delete.setEnabled(false);
                    break;
            }

        }
    }

    private Handler handler = new Handler() {
        @Override
        //当有消息发送出来的时候就执行Handler的这个方法
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int state = msg.what;
            if (state == 1) {
                delete(0);
                clear_all_list();
                out_cph.setText("");
                prompt("出库成功！");
                out_delete.setEnabled(true);

                // Toast.makeText(getActivity(), , Toast.LENGTH_SHORT).show();
            } else {
                prompt("出库失败，请重试或联系管理员！");
                out_delete.setEnabled(true);
                //Toast.makeText(getActivity(), "出库失败，请重试！", Toast.LENGTH_SHORT).show();
            }
            //只要执行到这里就关闭对话框
            waitingDialog.dismiss();
            listmain();
        }
    };

    private void processThread(final String ckxz, final String khid, final String tokcdid, final String cph, final String user, final List<ListviewOutStored> list) {
        //构建一个下载进度条
        waitingDialog = ProgressDialog.show(mainActivity, "出库提示!", "出库中,请稍等!");
        new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void run() {
                //在新线程里执行长耗时方法
                String result = saveResult(ckxz, khid, tokcdid, cph, user, list);
                int state = 0;
                try {
                    state = new JSONObject(result).getInt("state");
                } catch (JSONException e) {
                    state = 0;
                }
                //执行完毕后给handler发送一个空消息
                handler.sendEmptyMessage(state);
            }
        }.start();
    }

    //保存按钮
    private void save(String ckxz, String khid, String tokcdid, String cph, String user) {
        list = outStoreDao.selectOutStored();
        int save_state = 0;
        if (outStoreDao.checkRqys(list) == 3) {
            Toast.makeText(mainActivity, "存在容器数为0！", Toast.LENGTH_SHORT).show();
            save_state = 1;
        } else if (ckxz.equals("01")) {
            if (outStoreDao.checkWpid(list) == 1) {
                Toast.makeText(mainActivity, "存在重复的零件号添加！", Toast.LENGTH_SHORT).show();
                save_state = 1;
            } else if (outStoreDao.checkWpid(list) == 2) {
                Toast.makeText(mainActivity, "存在未添加的零件号！", Toast.LENGTH_SHORT).show();
                save_state = 1;
            }
        } else {
            if (outStoreDao.checkWpid(list) == 1) {
                Toast.makeText(mainActivity, "存在重复的容器添加！", Toast.LENGTH_SHORT).show();
                save_state = 1;
            } else if (outStoreDao.checkWpid(list) == 2) {
                Toast.makeText(mainActivity, "存在未添加的容器号！", Toast.LENGTH_SHORT).show();
                save_state = 1;
            }
        }
        if (save_state == 0) {
            processThread(ckxz, khid, tokcdid, cph, user, list);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String saveResult(String ckxz, String khid, String tokcdid, String cph, String user, List<ListviewOutStored> list) {
        String allDate = outStoreDao.allDateToJson(khid, tokcdid, ckxz, cph, user, list);
        String result = null;
        try {
            result = new HttpUtil().post(STORE_URL + SEND_OUT_STORE, allDate);
        } catch (IOException e) {
            return "{\"state\": -1}";
        }
        return result;
    }

    public void prompt(String prompt) {
        new AlertDialog.Builder(mainActivity)
                .setTitle("出库提示框").setMessage(prompt)
                .setPositiveButton("确定", null).show();
    }

    //删除按钮
    private void delete(int sign) {
        outStoreDao.DeleteOutStored(sign);
        listmain();
    }
    //添加按钮
    private void add(ListviewOutStored listviewOutStored) {
        outStoreDao.insertListviewOutStored(listviewOutStored);
        listmain();
        listView.setStackFromBottom(true);
    }

    /*设置添加出库记录的适配器*/
    public void listmain() {
        list.clear();
        list = outStoreDao.selectOutStored();
        outListViewAdapter = new OutListViewAdapter(mainActivity, R.layout.out_list, list, out_ckxz, user);
        listView.setAdapter(outListViewAdapter);
    }

    //清除数据库中listview数据
    public void clear_all_list() {
        SQLiteDatabase db = outSqlite.getWritableDatabase();
        db.execSQL("DELETE FROM out_stored;");
        db.close();
        outStoreDao.insertListviewOutStored(new ListviewOutStored("", "", "", 0));
    }

    /*设置客户，目的地，性质的适配器*/
    public void kmxAsapter() {
        List<String> khmc = outStoreDao.selectFhkh();
        ArrayAdapter<String> khmcadapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_spinner_item, khmc);
        khmcadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        out_khid.setAdapter(khmcadapter);
        //设置性质
        List<String> chxz = Arrays.asList(CKXZ);
        ArrayAdapter<String> chxzadapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_spinner_item, chxz);
        chxzadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        out_ckxz.setAdapter(chxzadapter);
        //设置目的地
        List<String> mmd = outStoreDao.selectMdd();
        ArrayAdapter<String> mmdadapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_spinner_item, mmd);
        mmdadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        out_ckmd.setAdapter(mmdadapter);
    }


}


/*  private void save(String ckxz, String khid, String tokcdid, String cph, String user) {
          if (TextUtils.isEmpty(cph)) {
              Toast.makeText(getActivity(), "请填写车牌号", Toast.LENGTH_SHORT).show();
          } else {
              int save_state = 0;
              list.clear();
              for (int i = 0; i < listView.getCount(); i++) {
                  View view = listView.getChildAt(i);
                  ListviewOutStored listviewOutStored = new ListviewOutStored();
                  EditText et_wpid = (EditText) view.findViewById(R.id.out_wpid);
                  EditText et_rqys = (EditText) view.findViewById(R.id.out_rqys);
                  EditText et_rqxh = (EditText) view.findViewById(R.id.out_rqxh);
                  String str_rqxh = et_rqxh.getText().toString();
                  String str_rqys = et_rqys.getText().toString();
                  String str_wpid = et_wpid.getText().toString();
                  if (0 == Integer.parseInt(util.NullTo0(str_rqys))) {
                      Toast.makeText(mainActivity, "容器数量不可为零哦！", Toast.LENGTH_SHORT).show();
                      util.showSoftInputFromWindow(mainActivity, et_rqys);
                      save_state = 1;
                      break;
                  }
                  if (ckxz.equals("01")) {
                      if (TextUtils.isEmpty(str_rqxh)) {
                          Toast.makeText(mainActivity, "还有数据未添加成功哦！", Toast.LENGTH_SHORT).show();
                          util.showSoftInputFromWindow(mainActivity, et_rqys);
                          save_state = 1;
                          break;
                      }
                      listviewOutStored.setRqxh(isNull(str_rqxh));
                      listviewOutStored.setWpid(isNull(str_wpid));
                  } else {
                      listviewOutStored.setRqxh(isNull(str_wpid));
                  }
                  listviewOutStored.setRqys(isNull(str_rqys));
                  list.add(listviewOutStored);
              }
              if (ckxz.equals("01")) {
                  if (outStoreDao.checkWpid(list) == 1) {
                      Toast.makeText(mainActivity, "存在重复的零件号添加！", Toast.LENGTH_SHORT).show();
                      save_state = 1;
                  }
              } else {
                  if (outStoreDao.checkRqxh(list) == 1) {
                      Toast.makeText(mainActivity, "存在重复的容器添加！", Toast.LENGTH_SHORT).show();
                      save_state = 1;
                  }
              }
              //是否存在未填写数据，存在的时候值为1，否则为0；
              if (save_state == 0) {
                  dialog = ProgressDialog.show(mainActivity, "出库提示", "正在出库，请稍等...", false);
                  String allDate = outStoreDao.allDateToJson(khid, tokcdid, ckxz, cph, user, list);
                  try {
                      String ruselt = new SendOutTask(mainActivity, allDate).execute(STORE_URL + SEND_OUT_STORE).get();
                      int state = new JSONObject(ruselt).getInt("state");
                      if (state == 1) {
                          clear_all_list();
                          listmain();
                          Toast.makeText(getActivity(), "出库成功！", Toast.LENGTH_SHORT).show();
                      } else {
                          Toast.makeText(getActivity(), "出库失败，请重试！", Toast.LENGTH_SHORT).show();
                      }
                  } catch (Exception e) {
                      Toast.makeText(getActivity(), "系统错误，请联系管理员", Toast.LENGTH_SHORT).show();
                  }
                  util.dialogDismiss(dialog);
              }

          }
      }
  */