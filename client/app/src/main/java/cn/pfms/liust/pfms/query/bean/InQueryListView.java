package cn.pfms.liust.pfms.query.bean;

/**
 * Created by easter on 17-10-17.
 */

// 根据传入的json数据，确定bean的结构类型

public class InQueryListView {
    private String  iQuery_rkdh;
    private String iQuery_ly;
    private String iQuery_rqxh;
    private String iQuery_rks;

    public InQueryListView(){

    }

    public InQueryListView(String iQuery_rkdh, String iQuery_ly, String iQuery_rqxh, String iQuery_rks){
        this.setiQuery_rkdh(iQuery_rkdh);
        this.setiQuery_ly(iQuery_ly);
        this.setiQuery_rqxh(iQuery_rqxh);
        this.setiQuery_rks(iQuery_rks);
    }

    public String getiQuery_rkdh() {
        return iQuery_rkdh;
    }

    public void setiQuery_rkdh(String iQuery_rkdh) {
        this.iQuery_rkdh = iQuery_rkdh;
    }

    public String getiQuery_ly() {
        return iQuery_ly;
    }

    public void setiQuery_ly(String iQuery_ly) {
        this.iQuery_ly = iQuery_ly;
    }

    public String getiQuery_rqxh() {
        return iQuery_rqxh;
    }

    public void setiQuery_rqxh(String iQuery_rqxh) {
        this.iQuery_rqxh = iQuery_rqxh;
    }

    public String getiQuery_rks() {
        return iQuery_rks;
    }

    public void setiQuery_rks(String iQuery_rks) {
        this.iQuery_rks = iQuery_rks;
    }

}
