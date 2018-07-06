package cn.pfms.liust.pfms.query.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import cn.pfms.liust.pfms.R;
import cn.pfms.liust.pfms.query.bean.OutQueryListView;

/**
 * Created by easter on 17-10-28.
 */

public class OqueryAdapter extends BaseAdapter{

    private Context context;//用于接收传递过来的Context对象
    List<OutQueryListView> list;
    private int resourceid;


    public OqueryAdapter(Context context, int textViewResourceid, List<OutQueryListView> list) {
        this.context = context;
        this.list = list;
        resourceid = textViewResourceid;
    }

    // 接受服务器端数据的adapter，将数据放到listview中
    // [{rkdh:123,ly:1123,rqxh:123,rqss:123}]
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // 存放控件
        public final class Packages {

            public TextView OutQuery_rkdh;
            public TextView OutQuery_ly;
            public TextView OutQuery_rqxh;
            public TextView OutQuery_rks;

        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            OutQueryListView outQueryListView = (OutQueryListView) getItem(position);
            Packages packages = null;
            if (null == convertView) {
                packages = new Packages();
                //将layout的xml布局文件实例化为View类对象。
                convertView = LayoutInflater.from(context).inflate(resourceid, parent, false);
                packages.OutQuery_rkdh = (TextView) convertView.findViewById(R.id.outQuery_rkdh);
                packages.OutQuery_ly =  (TextView) convertView.findViewById(R.id.outQuery_ly);
                packages.OutQuery_rqxh =  (TextView)  convertView.findViewById(R.id.outQuery_rqxh);
                packages.OutQuery_rks =  (TextView) convertView.findViewById(R.id.outQuery_rks);
                convertView.setTag(packages);
            } else {
                packages = (Packages) convertView.getTag();
            }

            packages.OutQuery_rkdh.setText(check(outQueryListView.getOutQuery_rkdh()));
            packages.OutQuery_ly.setText(check(outQueryListView.getOutQuery_ly()));
            packages.OutQuery_rqxh.setText(check(outQueryListView.getOutQuery_rqxh()));
            packages.OutQuery_rks.setText(check(outQueryListView.getOutQuery_rks()));

            return convertView;
        }

        public String check(String str){
            if(!TextUtils.isEmpty(str)){
                return str;
            }else {
                return "";
            }
        }
    }

