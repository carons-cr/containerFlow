package cn.pfms.liust.pfms.outstore.util;

/**
 * Created by liust on 17-10-6.
 */

public interface Constants {
     String STORE_URL ="http://116.196.69.87:9999/mpfm"; //默认域名 //"http://liust.free.ngrok.cc/mpfm"; //
     String LOGIN = "/login";//登录
     String  UPDATEFAM= "/UpdateFAM";//获取初始数据
     String GETRQBYWPID="/Getrqbywpid";//根据零件号获取容器型号和容器数
     String GETRQBYRQXH="/Getrqbyrqxh";//根据容器型号获取容器名称和容器数
     String SEND_OUT_STORE="/getoutstore";//发送数据到服务器
     String[] CKXZ = new String[]{"负载流转", "空载流转", "维修出库"};//出库性质
}
