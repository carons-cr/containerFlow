package cn.pfms.liust.pfms.query.bean;

/**
 * listview
 * Created by easter on 17-10-17.
 */

// 根据传入的json数据，确定bean的结构类型

public class OutQueryListView {
    private String  OutQuery_rkdh;
    private String OutQuery_ly;
    private String OutQuery_rqxh;
    private String OutQuery_rks;

    public OutQueryListView(){

    }

    public OutQueryListView(String OutQuery_rkdh, String OutQuery_ly, String OutQuery_rqxh, String OutQuery_rks){
        this.setOutQuery_rkdh(OutQuery_rkdh);
        this.setOutQuery_ly(OutQuery_ly);
        this.setOutQuery_rqxh(OutQuery_rqxh);
        this.setOutQuery_rks(OutQuery_rks);
    }

    public String getOutQuery_rkdh() {
        return OutQuery_rkdh;
    }

    public void setOutQuery_rkdh(String outQuery_rkdh) {
        OutQuery_rkdh = outQuery_rkdh;
    }

    public String getOutQuery_ly() {
        return OutQuery_ly;
    }

    public void setOutQuery_ly(String outQuery_ly) {
        OutQuery_ly = outQuery_ly;
    }

    public String getOutQuery_rqxh() {
        return OutQuery_rqxh;
    }

    public void setOutQuery_rqxh(String outQuery_rqxh) {
        OutQuery_rqxh = outQuery_rqxh;
    }

    public String getOutQuery_rks() {
        return OutQuery_rks;
    }

    public void setOutQuery_rks(String outQuery_rks) {
        OutQuery_rks = outQuery_rks;
    }
}
