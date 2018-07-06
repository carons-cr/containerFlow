package cn.pfms.liust.pfms.query.util;

import android.app.DatePickerDialog;
import android.widget.EditText;



import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2017/11/7.
 */

public class DatePickerUtil {

    private int mYear;
    private int mMonth;
    private int mDay;


    /**
     * 获得当天日期
     */
    public void setDateTime(EditText showTime) {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        updateDateDisplay(showTime);
    }

    /**
     * 日期文本框更新日期显示
     */
    public void updateDateDisplay(EditText showTime) {
        showTime.setText(new StringBuilder().append(mYear).append("-")
                .append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1)).append("-")
                .append((mDay < 10) ? "0" + mDay : mDay));
    }

    /**
     * 检验用户选择的开始日和结束日
     * @param startDate
     * @param endDate
     * @return
     */
    public int judgeDate(EditText startDate,EditText endDate){

        // endDate可能为空
        String end_date = endDate.getText().toString();
        if(end_date.equals("")){
            // 空点查询闪退的根源
            end_date = startDate.getText().toString().replaceAll("-","");
        }else{
            end_date = end_date.replaceAll("-","");
        }

        String start_date = startDate.getText().toString().replaceAll("-","");

        // 获取系统今天日期
        Date day = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String Today = sdf.format(day).replaceAll("-","");

        int start_datei = Integer.parseInt(start_date);
        int end_datei = Integer.parseInt(end_date);
        int todayi = Integer.parseInt(Today);


        if(start_datei > end_datei){
            return 0;
        }else if(start_datei > todayi){
            return 1;
        }else if(end_datei > todayi){
            return 2;
        }else {
            return 3;
        }

    }

    /**
     * 将用户输入的关键字小写转大写
     * @param keyword
     * @return
     */
    public String judgeKeyWord(String keyword){
        String upperKword = "";

        if(keyword.equals("")) {
            upperKword = keyword;
        }else{
            upperKword = keyword.toUpperCase();
//            // 小写转大写
//            StringBuffer sb = new StringBuffer();
//            for (int i = 0; i < keyword.length(); i++) {
//                char c = keyword.charAt(i);
//                if (Character.isLowerCase(c)) {
//                    sb.append(Character.toUpperCase(c));
//                }
//            }
//            upperKword = sb.toString();
        }
        return upperKword;
    }

    public int getmYear() {
        return mYear;
    }

    public void setmYear(int mYear) {
        this.mYear = mYear;
    }

    public int getmMonth() {
        return mMonth;
    }

    public void setmMonth(int mMonth) {
        this.mMonth = mMonth;
    }

    public int getmDay() {
        return mDay;
    }

    public void setmDay(int mDay) {
        this.mDay = mDay;
    }
}
