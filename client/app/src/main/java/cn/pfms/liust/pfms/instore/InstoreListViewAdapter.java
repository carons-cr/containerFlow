
package cn.pfms.liust.pfms.instore;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import cn.pfms.liust.pfms.R;


/**
 * Created by caron on 10/2/17.
 */

public class InstoreListViewAdapter extends BaseAdapter {

    private ArrayList<Map<String,Object>> rqInfoList;
    private Context instore_listview_context;

    public InstoreListViewAdapter() {
    }

    public InstoreListViewAdapter(ArrayList<Map<String,Object>> rqInfoList, Context listview_context_instore) {
       // System.out.println(rqInfoList.size()+"hhhhhhhadapter");
        this.rqInfoList = rqInfoList;
        this.instore_listview_context = listview_context_instore;
    }
    @Override
    public int getCount() {
        return rqInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //加载listview中的项item
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(instore_listview_context).inflate(R.layout.instore_item_list,parent,false);
            holder = new ViewHolder(convertView, position);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.instore_wbpm.setText((String)rqInfoList.get(position).get("wpid"));
        //System.out.println(holder.instore_wbpm.getText());
        holder.instore_rqxh.setText((String)rqInfoList.get(position).get("rqxh"));
        holder.instore_ys.setText((int)rqInfoList.get(position).get("rqys")+"");
        holder.instore_ss.setText((int)rqInfoList.get(position).get("rqss")+"");
        return convertView;
    }
    private class ViewHolder{
        TextView instore_wbpm;
        TextView instore_rqxh;
        TextView instore_ys;
        TextView instore_ss;

        public ViewHolder(View convertView,int position) {
            instore_wbpm = (TextView) convertView.findViewById(R.id.instore_wbpm);
            instore_rqxh = (TextView) convertView.findViewById(R.id.instore_rqxh);
            instore_ys = (TextView) convertView.findViewById(R.id.instore_ys);
            instore_ss = (EditText) convertView.findViewById(R.id.instore_ss);
            instore_ss.setTag(position);
            //为editText设置监听，只要值改变就动态保存在instore_listview_data_list中
            instore_ss.addTextChangedListener(new TextSwitcher(this));
        }
    }

    //自定义内部类对editText进行监听
    class TextSwitcher implements TextWatcher {
        private ViewHolder mHolder;

        public TextSwitcher(ViewHolder mHolder) {
            this.mHolder = mHolder;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int position = (int) mHolder.instore_ss.getTag();
            String textRqss=mHolder.instore_ss.getText().toString().trim();
            String textRqys=mHolder.instore_ys.getText().toString().trim();
            if(textRqss.length()!=0&&!textRqss.equals("")&&textRqys.length()!=0&&!textRqys.equals("")) {
                try {
                    int rqssNum=Integer.parseInt(textRqss);
                    int rqysNum=Integer.parseInt(textRqys);
                    if(rqssNum<0){
                        Toast.makeText(instore_listview_context, "实收不能为负数!", Toast.LENGTH_SHORT).show();
                        //editPrompt("实收不能为负数!");
                    }else if(rqssNum>rqysNum){
                        Toast.makeText(instore_listview_context, "实收不能大于应收!", Toast.LENGTH_SHORT).show();
                        //editPrompt("实收不能大于应收!");
                    }else {
                        saveEditData(position, rqssNum);
                    }
                }catch (Exception e){
                    Toast.makeText(instore_listview_context, "请勿输入非法字符!", Toast.LENGTH_SHORT).show();
                    //editPrompt("请勿输入非法字符");
                }

            }else{
                Toast.makeText(instore_listview_context, "实收不能为空!", Toast.LENGTH_SHORT).show();
             // editPrompt("实收不能为空!");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
        /*public void editPrompt(String prompt){
            new AlertDialog.Builder(instore_listview_context)
                    .setTitle("编辑提示框").setMessage(prompt)
                    .setPositiveButton("确定",null).show();
        }*/
        public void saveEditData(int position, int num) {
            rqInfoList.get(position).put("rqss", num);
            if(new InstoreListViewAdapter()!=null) {
                new InstoreListViewAdapter().notifyDataSetChanged();
            }
        }


    }



}


