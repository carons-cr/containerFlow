package cn.pfms.liust.pfms.outstore.fragmentView;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;

import cn.pfms.liust.pfms.MainActivity;
import cn.pfms.liust.pfms.R;
import cn.pfms.liust.pfms.outstore.bean.ListviewOutStored;
import cn.pfms.liust.pfms.outstore.dao.OutStoreDao;
import cn.pfms.liust.pfms.outstore.util.AsyncTask;
import cn.pfms.liust.pfms.outstore.util.Constants;
import cn.pfms.liust.pfms.outstore.util.Util;


/**
 * listview 适配器
 * Created by liust on 17-10-13.
 */

public class OutListViewAdapter extends BaseAdapter implements Constants {
    private int resourceid;
    private Context context;
    List<ListviewOutStored> data;
    Spinner ckxz;
    String user;
    int index = -1;

    public OutListViewAdapter(Context context, int textViewResourceid, List<ListviewOutStored> data, Spinner ckxz, String user) {
        this.context = context;
        resourceid = textViewResourceid;
        this.data = data;
        this.ckxz = ckxz;
        this.user = user;
    }

    public final class Packages {
        public EditText out_wpid;
        public EditText out_rqys;
        public EditText out_rqxh;
        public TextView out_sign;

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        String ckxzStr = ckxz.getSelectedItem().toString();
        String kcdid = new OutStoreDao().jsonToUser(user).getKcdid();
        ListviewOutStored listviewOutStored = (ListviewOutStored) getItem(position);
        Packages packages = null;
        if (null == view) {
            packages = new Packages();
            //获得组件，实例化组件
            view = LayoutInflater.from(context).inflate(resourceid, parent, false);
            packages.out_wpid = (EditText) view.findViewById(R.id.out_wpid);
            packages.out_rqys = (EditText) view.findViewById(R.id.out_rqys);
            packages.out_rqxh = (EditText) view.findViewById(R.id.out_rqxh);
            packages.out_sign = (TextView) view.findViewById(R.id.sign);
            view.setTag(packages);
        } else {
            packages = (Packages) view.getTag();
        }
        //始终为最后一个item得到焦点
        if (index == -1) {
            packages.out_wpid.setFocusable(true);
            packages.out_wpid.setFocusableInTouchMode(true);
            packages.out_wpid.requestFocus();
            index++;
        }
        isEmpty(listviewOutStored.getRqys(), packages.out_rqys);
        isEmpty(listviewOutStored.getWpid(), packages.out_wpid);
        packages.out_sign.setText(String.valueOf(listviewOutStored.getSign()));
        packages.out_rqxh.setText(listviewOutStored.getRqxh());
        packages.out_rqys.setOnTouchListener(new TouchListener(packages.out_rqys, packages.out_wpid, packages.out_rqxh, listviewOutStored, ckxzStr, kcdid));
        packages.out_rqys.setOnFocusChangeListener(new OnRqysFocusChangeListener(packages.out_rqxh, packages.out_wpid, packages.out_sign, packages.out_rqys));
        packages.out_wpid.setOnFocusChangeListener(new OnWpidFocusChangeListener(packages.out_rqxh, packages.out_wpid, packages.out_sign, packages.out_rqys));
        return view;
    }

    class OnWpidFocusChangeListener implements View.OnFocusChangeListener {
        EditText rqxh;
        EditText wpid;
        TextView sign;
        EditText rqys;

        public OnWpidFocusChangeListener(EditText rqxh, EditText wpid, TextView sign, EditText rqys) {
            this.rqxh = rqxh;
            this.wpid = wpid;
            this.sign = sign;
            this.rqys = rqys;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                String fill_sign = sign.getText().toString();
                String fill_rqys = rqys.getText().toString();
                String fill_rqxh = rqxh.getText().toString();
                String fill_wpid = wpid.getText().toString();
                if (!fill_wpid.equals("")) {
                    String oldwpid = new OutStoreDao((MainActivity) context).selectWpid(fill_sign);
                    try {
                        if (!oldwpid.equals(fill_wpid)) {
                            rqys.setText("");
                            rqxh.setText("");
                            new OutStoreDao((MainActivity) context).listviewOutStoredUpdate(fill_sign, fill_wpid, fill_rqxh, fill_rqys);
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    //绑定焦点事件
    class OnRqysFocusChangeListener implements View.OnFocusChangeListener {
        EditText rqxh;
        EditText wpid;
        TextView sign;
        EditText rqys;

        public OnRqysFocusChangeListener(EditText rqxh, EditText wpid, TextView sign, EditText rqys) {
            this.rqxh = rqxh;
            this.wpid = wpid;
            this.sign = sign;
            this.rqys = rqys;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                String fill_sign = sign.getText().toString();
                String fill_rqys = rqys.getText().toString();
                String fill_rqxh = rqxh.getText().toString();
                String fill_wpid = wpid.getText().toString();
                if (!fill_rqys.equals("")) {
                    int result = new OutStoreDao((MainActivity) context).listviewOutStoredUpdate(fill_sign, fill_wpid, fill_rqxh, fill_rqys);
                }
            }
        }
    }

    public void isEmpty(String string, EditText editText) {
        if (!TextUtils.isEmpty(string)) {
            editText.setText(string);
        } else {
            editText.setText("");
        }
    }

    //绑定触摸事件
    class TouchListener implements View.OnTouchListener {

        EditText ET_rqys, ET_wpid;
        EditText ET_rqxh;
        ListviewOutStored listviewOutStored;
        String ckxzStr, kcdid;

        public TouchListener(EditText ET_rqys, EditText ET_wpid, EditText ET_rqxh, ListviewOutStored listviewOutStored, String ckxzStr, String kcdid) {

            this.ET_rqys = ET_rqys;
            this.ET_rqxh = ET_rqxh;
            this.ET_wpid = ET_wpid;
            this.ckxzStr = ckxzStr;
            this.kcdid = kcdid;
            this.listviewOutStored = listviewOutStored;
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            ((ViewGroup) view.getParent())
                    .setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
            if (event.getAction() == 0) {
                ET_rqys.requestFocus();
                Activity activity = (Activity) context;
                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                try {
                    String result, rqysnum, rqxh, rq, temp, sendData, url;
                    temp = ET_wpid.getText().toString();
                    if (!TextUtils.isEmpty(temp)) {
                        if (ckxzStr.equals("负载流转")) {
                            sendData = "{\"wpid\": \"" + temp + "\",\"kcdid\":\"" + kcdid + "\"}";
                            url = STORE_URL + GETRQBYWPID;
                            rq = "rqxh";
                        } else {
                            sendData = "{\"rqxh\": \"" + temp + "\",\"kcdid\":\"" + kcdid + "\"}";
                            url = STORE_URL + GETRQBYRQXH;
                            rq = "rqmc";
                        }
                        try {
                            result = new AsyncTask(context, sendData, "post").execute(url).get();
                        } catch (Exception e) {
                            result = "{\"state\": -1}";
                        }
                        int state = new JSONObject(result).getInt("state");
                        if (state == 1) {
                            ET_rqys.setFocusable(true);
                            new Util().showSoftInputFromWindow((Activity) context, ET_rqys);
                            rqysnum = String.valueOf(new JSONObject(result).getInt("rqys"));
                            rqxh = new JSONObject(result).getString(rq);
                            isEmpty("", ET_rqys);
                            isEmpty(rqxh, ET_rqxh);
                            ET_rqys.setFilters(new InputFilter[]{new InputFilterMinMax("0", rqysnum)});
                        } else if (state == 0) {
                            ET_rqys.setFocusable(false);
                            new Util().showSoftInputFromWindow((Activity) context, ET_wpid);
                            isEmpty("", ET_rqys);
                            isEmpty("", ET_rqxh);
                            Toast toast = Toast.makeText(context, "本地无对应容器型号！", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        } else if (state == -1) {
                            Toast.makeText(context, "网络链接错误,请重试!", Toast.LENGTH_SHORT).show();
                        } else if (state == 2) {
                            ET_rqys.setFocusable(false);
                            new Util().showSoftInputFromWindow((Activity) context, ET_wpid);
                            isEmpty("", ET_rqys);
                            isEmpty("", ET_rqxh);
                            Toast toast = Toast.makeText(context, "本地无此对应容器型号！", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    } else {
                        new Util().showSoftInputFromWindow((Activity) context, ET_wpid);
                        ET_rqys.setFocusable(false);
                        if (ckxzStr.equals("负载流转")) {
                            Toast.makeText(context, "请填写零件号哦！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "请填写容器号哦！", Toast.LENGTH_SHORT).show();
                        }

                    }
                } catch (Exception e) {
                    Toast.makeText(context, "系统错误请联系管理员!" + e, Toast.LENGTH_SHORT).show();
                }
            }
            return false;

        }
    }
}
/*

控制容器数大小，设置不可输入大于最大值，不能小于0；
*
*/

class InputFilterMinMax implements InputFilter {
    private int min, max;

    public InputFilterMinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterMinMax(String min, String max) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) {
        }
        return "";
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}

