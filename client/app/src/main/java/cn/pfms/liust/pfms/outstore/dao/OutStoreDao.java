package cn.pfms.liust.pfms.outstore.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pfms.liust.pfms.MainActivity;
import cn.pfms.liust.pfms.outstore.bean.ListviewOutStored;
import cn.pfms.liust.pfms.outstore.bean.OutFhkh;
import cn.pfms.liust.pfms.outstore.bean.OutMdd;
import cn.pfms.liust.pfms.outstore.bean.Users;
import cn.pfms.liust.pfms.outstore.util.AsyncTask;
import cn.pfms.liust.pfms.outstore.util.Constants;
import cn.pfms.liust.pfms.outstore.util.GsonUtil;
import cn.pfms.liust.pfms.outstore.util.OutSqlite;


/**
 * Created by liust on 17-9-25.
 * 出库后台处理
 */
public class OutStoreDao implements Constants {
    OutSqlite outSqlite;
    MainActivity mainActivity;
    Gson gson = new Gson();

    public OutStoreDao(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        outSqlite = new OutSqlite(mainActivity);
    }

    public OutStoreDao() {
        super();
    }

    /*获取初始数据（发货客户，目的地），并存储到数据库中*/
    public boolean saveInitData() {
        String updateUrl = STORE_URL + UPDATEFAM;
        try {
            String UpdateFhkhAndMDD = new AsyncTask(mainActivity,"get").execute(updateUrl).get();
            //  String UpdateFhkhAndMDD = "{'mdd':'[{\"kcdid\":\"99900W0406B-000\",\"kcdmc\":\"外销件\"},{\"kcdid\":\"BJZXKCENTR\",\"kcdmc\":\"备件中心库 Centr\"}]', 'fhkh':'[{\"khid\":\"ZL0001\",\"khmc\":\"武汉中人瑞众\"},{\"khid\":\"ZL0002\",\"khmc\":\"重庆立达奥特\"}]'}";
            Map<String, String> map = gson.fromJson(UpdateFhkhAndMDD, new TypeToken<Map<String, String>>() {
            }.getType());
            //获取Palletpart并储存在数据库中
            int state = Integer.parseInt(map.get("state"));
            if (state == 1) {
                String mdd = map.get("mdd");
                String fhkh = map.get("fhkh");
                long i = insertMdd(mdd);
                long j = insertFhkh(fhkh);
                if (i <= 0 || j <= 0) {
                    //报错
                    return false;
                }
            } else {
                Toast.makeText(mainActivity, "数据更新失败，请重试！", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }
    //获取listview界面上的所有值

    public long insertMdd(String mdd) {
        long row = 0;
        GsonUtil gsonUtil = new GsonUtil();
        ArrayList<OutMdd> listMdd = gsonUtil.jsonToArrayList(mdd, OutMdd.class);
        outSqlite = new OutSqlite(mainActivity);
        SQLiteDatabase db = outSqlite.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (OutMdd outMdd : listMdd) {
            cv.put("kcdid", outMdd.getKcdid());
            cv.put("kcdmc", outMdd.getKcdmc());
            row = row + db.insert("out_mdd", null, cv);
        }
        return row;
    }

    public long insertFhkh(String fhkh) {
        long row = 0;
        GsonUtil gsonUtil = new GsonUtil();
        ArrayList<OutFhkh> listMdd = gsonUtil.jsonToArrayList(fhkh, OutFhkh.class);
        outSqlite = new OutSqlite(mainActivity);
        SQLiteDatabase db = outSqlite.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (OutFhkh outFhkh : listMdd) {
            cv.put("khid", outFhkh.getKhid());
            cv.put("khmc", outFhkh.getKhmc());
            row = row + db.insert("out_fhkh", null, cv);
        }
        return row;
    }

    /*取出目的地的显示数据*/
    public List<String> selectMdd() {
        outSqlite = new OutSqlite(mainActivity);
        SQLiteDatabase db = outSqlite.getWritableDatabase();
        List<String> list = new ArrayList<>();
        Cursor cursor = db.query("out_mdd", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String kcdmc = cursor.getString(cursor.getColumnIndex("kcdmc"));
                list.add(kcdmc);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /*取出发货客户的数据*/
    public List<String> selectFhkh() {
        outSqlite = new OutSqlite(mainActivity);
        SQLiteDatabase db = outSqlite.getWritableDatabase();
        List<String> list = new ArrayList<>();
        Cursor cursor = db.query("out_fhkh", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String khmc = cursor.getString(cursor.getColumnIndex("khmc"));
                list.add(khmc);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /*条件查询
    *
    */
    /*根据名称取客户编号*/
    public String fhkhToKhid(String khmc) {
        String khid = null;
        outSqlite = new OutSqlite(mainActivity);
        SQLiteDatabase db = outSqlite.getWritableDatabase();
        Cursor cursor = db.rawQuery("select khid from out_fhkh where khmc = '" + khmc + "'", null);
        while (cursor.moveToNext()) {
            khid = cursor.getString(cursor.getColumnIndex("khid"));
        }
        cursor.close();
        return khid;
    }

    /*根据名称取目的地编号*/
    public String mddToCkdid(String kcdmc) {
        outSqlite = new OutSqlite(mainActivity);
        SQLiteDatabase db = outSqlite.getWritableDatabase();
        Cursor cursor = db.rawQuery("select kcdid from out_mdd where kcdmc = '" + kcdmc + "'", null);
        String ckdid = null;
        while (cursor.moveToNext()) {
            ckdid = cursor.getString(cursor.getColumnIndex("kcdid"));
        }
        cursor.close();
        return ckdid;
    }

    /*以下为处理 out_stored的数据并准备发送出去的处理
出库头部资源 发货客户，目的地，出库性质数据库*/
/*查询listview所有数据*/
    public List<ListviewOutStored> selectOutStored() {
        outSqlite = new OutSqlite(mainActivity);
        SQLiteDatabase db = outSqlite.getWritableDatabase();
        List<ListviewOutStored> list = new ArrayList<>();
        Cursor cursor = db.query("out_stored", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                // 遍历Cursor对象，取出数据并打印
                ListviewOutStored listviewOutStored = new ListviewOutStored();
                String rqys = cursor.getString(cursor.getColumnIndex("rqys"));
                String rqxh = cursor.getString(cursor.getColumnIndex("rqxh"));
                String wpid = cursor.getString(cursor.getColumnIndex("wpid"));
                String sign = cursor.getString(cursor.getColumnIndex("sign"));
                listviewOutStored.setRqxh(rqxh);
                listviewOutStored.setWpid(wpid);
                listviewOutStored.setRqys(rqys);
                listviewOutStored.setSign(Integer.parseInt(sign));
                list.add(listviewOutStored);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }


    /*插入listview数据*/
    public long insertListviewOutStored(ListviewOutStored listviewOutStored) {
        long row = 0;
        SQLiteDatabase db = outSqlite.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("rqxh", listviewOutStored.getRqxh());
        cv.put("rqys", listviewOutStored.getRqys());
        cv.put("wpid", listviewOutStored.getWpid());
        cv.put("sign", String.valueOf(listviewOutStored.getSign()));
        row = db.insert("out_stored", null, cv);
        return row;
    }

    /*查询listview数据*/
    public String selectWpid(String sign) {
        String wpid = null;
        SQLiteDatabase db = outSqlite.getWritableDatabase();
        Cursor cursor = db.rawQuery("select wpid from out_stored where sign = ? ", new String[]{sign});
        while (cursor.moveToNext()) {
            wpid = cursor.getString(cursor.getColumnIndex("wpid"));
        }
        cursor.close();
        return wpid;
    }

    //根据sign更新OutStored
    public int listviewOutStoredUpdate(String sign, String wpid, String rqxh, String rqys) {
        outSqlite = new OutSqlite(mainActivity);
        SQLiteDatabase db = outSqlite.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("wpid", wpid);
        cv.put("rqxh", rqxh);
        cv.put("rqys", rqys);
        String[] args = {String.valueOf(sign)};
        int a = db.update("out_stored", cv, "sign=?", args);
        db.close();
        return a;
    }

    //根据sign删除outstored
    public int DeleteOutStored(int sign) {
        outSqlite = new OutSqlite(mainActivity);
        SQLiteDatabase db = outSqlite.getWritableDatabase();
        String[] args = {String.valueOf(sign)};
        return db.delete("out_stored", "sign=?", args);
    }

    /*出库性质，名称变为编号*/
    public String ckxzMCtoID(String ckxz) {
        String wpid = "";
        if (ckxz.equals("负载流转")) {
            wpid = "01";
        } else if (ckxz.equals("空载流转")) {
            wpid = "02";
        } else if (ckxz.equals("维修出库")) {
            wpid = "03";
        }
        return wpid;
    }

    //通过json解析user
    public Users jsonToUser(String user) {
        Gson gson = new Gson();
        Users users = gson.fromJson(user, Users.class);
        return users;
    }

    /*将所有准备发送出去的数据为json格式*/
    public String allDateToJson(String khid, String tokcdid, String ckxz, String cph, String user, List<ListviewOutStored> listOutStored) {
        String outStored = wpidArqxhBychxz(ckxz, listOutStored);//listview数据
        Users users = jsonToUser(user);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("kcdid", users.getKcdid());
        map.put("khid", fhkhToKhid(khid));
        map.put("tokcdid", mddToCkdid(tokcdid));
        map.put("jbr", users.getUserid());
        map.put("ckxz", ckxz);
        map.put("cph", cph);
        map.put("outstored", outStored);
        String allDate = gson.toJson(map);
        return allDate;
    }

    //根据出库性质的不同而改变
    public String wpidArqxhBychxz(String chxz, List<ListviewOutStored> listOutStored) {
        if (chxz == "01") {
            return gson.toJson(listOutStored);
        } else {
            List<ListviewOutStored> listviewOutStored = new ArrayList<>();
            for (ListviewOutStored list : listOutStored) {
                ListviewOutStored viewOutStored = new ListviewOutStored();
                String wpid = list.getWpid();
                String rqys = list.getRqys();
                viewOutStored.setRqxh(wpid.toUpperCase());
                viewOutStored.setRqys(rqys);
                listviewOutStored.add(viewOutStored);
            }
            return gson.toJson(listviewOutStored);
        }
    }

    //检查是否存在重复零件号或者容器数存在重复返回1，存在空返回2，否则返,0
    public int checkWpid(List<ListviewOutStored> list) {
        if (list.get(list.size() - 1).getWpid().equals("")) {
            return 2;
        } else {
            for (int i = 0; i < list.size() - 1; i++) {
                if (list.get(i).getWpid().equals("")) {
                    return 2;
                }
                for (int j = i + 1; j < list.size(); j++) {
                    if (list.get(i).getWpid().equals(list.get(j).getWpid())) {
                        return 1;
                    }
                }

            }
        }
        return 0;
    }

    public int checkRqys(List<ListviewOutStored> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getRqys().equals("0") || list.get(i).getRqys().equals("")) {
                return 3;
            }

        }
        return 0;
    }
}
