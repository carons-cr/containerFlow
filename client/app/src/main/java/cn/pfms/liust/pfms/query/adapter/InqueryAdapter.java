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
import cn.pfms.liust.pfms.query.bean.InQueryListView;


/**
 * Created by easter on 17-10-28.
 */

public class InqueryAdapter extends BaseAdapter{

    private Context context;//用于接收传递过来的Context对象
    List<InQueryListView> list;
    private int resourceid;


    public InqueryAdapter(Context context,int textViewResourceid, List<InQueryListView> list) {
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
            public TextView  iQuery_rkdh;
            public TextView iQuery_ly;
            public TextView iQuery_rqxh;
            public TextView iQuery_rks;

        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            InQueryListView inQueryListView = (InQueryListView) getItem(position);
            Packages packages = null;
            if (null == convertView) {
                packages = new Packages();
                //将layout的xml布局文件实例化为View类对象。
                convertView = LayoutInflater.from(context).inflate(resourceid, parent, false);
                packages.iQuery_rkdh = (TextView) convertView.findViewById(R.id.iQuery_rkdh);
                packages.iQuery_ly = (TextView) convertView.findViewById(R.id.iQuery_ly);
                packages.iQuery_rqxh = (TextView) convertView.findViewById(R.id.iQuery_rqxh);
                packages.iQuery_rks = (TextView) convertView.findViewById(R.id.iQuery_rks);
                convertView.setTag(packages);
            } else {
                packages = (Packages) convertView.getTag();
            }

            packages.iQuery_rkdh.setText(check(inQueryListView.getiQuery_rkdh()));
            packages.iQuery_ly.setText(check(inQueryListView.getiQuery_ly()));
            packages.iQuery_rqxh.setText(check(inQueryListView.getiQuery_rqxh()));
            packages.iQuery_rks.setText(check(inQueryListView.getiQuery_rks()));
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

