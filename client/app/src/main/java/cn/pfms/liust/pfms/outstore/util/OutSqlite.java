package cn.pfms.liust.pfms.outstore.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * 数据库创建
 * Created by liust on 17-9-25.
 */

public class OutSqlite extends SQLiteOpenHelper {
    //设置出库头部资源 发货客户，目的地
    public static final String CREATE_OUT_FHKH = "create table out_fhkh(" +
            //primary key 将id列设为主键    autoincrement表示id列是自增长的
            "id integer primary key autoincrement," +
            "khid text," +
            "khmc text )";
    public static final String CREATE_OUT_MDD = "create table out_mdd(" +
            //primary key 将id列设为主键    autoincrement表示id列是自增长的
            "id integer primary key autoincrement," +
            "kcdid text," +
            "kcdmc text )";
    //发送出库详细信息数据库
    public static final String CREATE_OUT_STORE_D = "create table out_stored(" +
            //primary key 将id列设为主键    autoincrement表示id列是自增长的
            "id integer primary key autoincrement," +
            "wpid text," +
            "rqxh text," +
            "rqys text," +
            "sign text UNIQUE)";


    private Context context;

    public OutSqlite(Context context) {
        // 创建一个名为出库的数据库
        super(context, "store.db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 执行时，若表不存在，则创建之，注意SQLite数据库中必须有一个_id的字段作为主键，否则查询时将报错
        db.execSQL(CREATE_OUT_FHKH);
        db.execSQL(CREATE_OUT_MDD);
        db.execSQL(CREATE_OUT_STORE_D);
        Toast.makeText(context, "create success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 数据库被改变时，将原先的表删除，然后建立新表
        db.execSQL("DELETE FROM CREATE_OUT_STORE_D;");
        onCreate(db);
    }
}