package cn.pfms.liust.pfms;
/*
* 全局主要类
* 控制每个碎片
 * Created by liust on 17-10-28.
 */

import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;

import cn.pfms.liust.pfms.instore.InMain;
import cn.pfms.liust.pfms.outstore.fragmentView.OutMain;
import cn.pfms.liust.pfms.outstore.util.OutSqlite;
import cn.pfms.liust.pfms.query.main.InQueryMain;
import cn.pfms.liust.pfms.query.main.OutQueryMain;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView instore;
    private ImageView outstore;
    private ImageView inflow;
    private ImageView outflow;
    private FrameLayout ly_content;
    private OutMain outMainActivity;
    private InQueryMain inQueryMain;
    private OutQueryMain outQueryMain;
    private InMain inMain;
    private OutSqlite outSqlite;
    private SharedPreferences user_data;
    Menu menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        outSqlite = new OutSqlite(this);
        bindView();
    }

    public String getUserData() {
        user_data = getSharedPreferences("userInfo", this.MODE_PRIVATE);
        String user = user_data.getString("USER", "");
        return user;
    }

    //UI组件初始化与事件绑定
    private void bindView() {
        instore = (ImageView) this.findViewById(R.id.instore);
        outstore = (ImageView) this.findViewById(R.id.outstore);
        inflow = (ImageView) this.findViewById(R.id.inflow);
        outflow = (ImageView) this.findViewById(R.id.outflow);
        ly_content = (FrameLayout) findViewById(R.id.fragment_container);

        instore.setOnClickListener(this);
        outstore.setOnClickListener(this);
        outflow.setOnClickListener(this);
        inflow.setOnClickListener(this);

    }

    //重置所有文本的选中状态
    public void selected() {
        instore.setSelected(false);
        outstore.setSelected(false);
        inflow.setSelected(false);
        outflow.setSelected(false);
    }

    //隐藏所有Fragment
    public void hideAllFragment(FragmentTransaction transaction) {
        if (outMainActivity != null) {
            transaction.hide(outMainActivity);
        }
        if (inMain != null) {
            transaction.hide(inMain);
        }
        if (inQueryMain != null) {
            transaction.hide(inQueryMain);
        }
        if (outQueryMain != null) {
            transaction.hide(outQueryMain);
        }
    }

    @Override
    public void onClick(View v) {
        String user = getUserData();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        switch (v.getId()) {
            case R.id.outstore:
                menu.findItem(R.id.menu).setTitle("出库管理");
                selected();
                outstore.setSelected(true);
                if (outMainActivity == null) {
                    SQLiteDatabase db = outSqlite.getWritableDatabase();
                    db.execSQL("DELETE FROM out_mdd;");
                    db.execSQL("DELETE FROM out_fhkh;");
                    db.execSQL("DELETE FROM out_stored;");
                    db.close();
                    outMainActivity = new OutMain(MainActivity.this, outSqlite, user);
                    transaction.add(R.id.fragment_container, outMainActivity);
                } else {
                    transaction.show(outMainActivity);
                }
                break;
            case R.id.instore:
                selected();
                menu.findItem(R.id.menu).setTitle("入库管理");
                instore.setSelected(true);
                if (inMain == null) {
                    inMain = new InMain(MainActivity.this, user);
                    transaction.add(R.id.fragment_container, inMain);
                } else {
                    transaction.show(inMain);
                }
                break;
            case R.id.inflow:
                selected();
                inflow.setSelected(true);
                menu.findItem(R.id.menu).setTitle("入库查询");
                if (inQueryMain == null) {
                    inQueryMain = new InQueryMain(MainActivity.this, user);
                    transaction.add(R.id.fragment_container, inQueryMain);
                } else {
                    transaction.show(inQueryMain);
                }

                break;
            case R.id.outflow:
                selected();
                menu.findItem(R.id.menu).setTitle("出库查询");
                outflow.setSelected(true);
                if (outQueryMain == null) {
                    outQueryMain = new OutQueryMain(MainActivity.this, user);
                    transaction.add(R.id.fragment_container, outQueryMain);
                } else {
                    transaction.show(outQueryMain);
                }

                break;
        }
        transaction.commit();
    }

}