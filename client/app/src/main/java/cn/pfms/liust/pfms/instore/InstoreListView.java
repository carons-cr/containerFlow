package cn.pfms.liust.pfms.instore;
import android.content.Context;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.pfms.liust.pfms.R;


/**
 * Created by caron on 10/12/17.
 */

public class InstoreListView extends ListView implements AbsListView.OnScrollListener {
        private View instore_header;//头布局
        private int instore_header_height;//头布局自身的高度

        private int scrollState;//当前滚动状态
        private int firstVisibleItem;//当前可见的第一个item
        private int startY;//刚开始触摸屏幕时的Y值

        private int curState = 0;//当前header状态，默认为0
        private final int NORMAL = 0;//正常状态
        private final int PULL = 1;//状态下拉
        private final int RELEASE = 2;//提示刷新状态
        private final int RELEASING = 3;//状态正在刷新
        private boolean canPull = false;//是否可以执行下拉操作

        private refresfListener mRefresfListener;//回调接口

     public InstoreListView(Context context) {
         super(context);
         initView( context);
     }
     public InstoreListView(Context context, AttributeSet attrs) {
                 super(context, attrs);
                 initView( context);
     }
     public InstoreListView(Context context, AttributeSet attrs, int defStyle) {
                 super(context, attrs, defStyle);
                 initView( context);
     }

    //定义回调接口
    public interface refresfListener{
        void refresh();
    }
    public void setOnRefreshListener(refresfListener listener){
        this.mRefresfListener = listener;
    }


    public void initView(Context context){
         instore_header = LayoutInflater.from(context).inflate(R.layout.instore_header, null);
         //将头布局加进去
         //这里需要注意，在获取高度时必须首先通知父布局header要占多大高和宽。
         //因为此时父布局还不知道header的尺寸呢，否则你获取的高度只能为0.
         notifyView(instore_header);
         instore_header_height = instore_header.getMeasuredHeight();//获取header的高度
         paddingTop(-instore_header_height);
         //将头布局加进去
         this.addHeaderView(instore_header);
         this.setOnScrollListener(this);
     }

     //通知父布局，子布局view的宽度和高度
     private void notifyView(View view){
         ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
         if(layoutParams == null){
             layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

         }

         int width = ViewGroup.getChildMeasureSpec(0, 0, layoutParams.width);

         int height;
         int tempHeight = layoutParams.height;
         if(tempHeight>0){
                 //子布局高度不为空，需要填充这个布局
                 height = MeasureSpec.makeMeasureSpec(tempHeight, MeasureSpec.EXACTLY);
             }else{
                 //高度为0，则不需要填充
                 height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
             }

         //然后告诉父布局，子布局的高度和宽度
         view.measure(width, height);
     }

    //设定header的paddingTop
     private void paddingTop(int paddingTop){
         instore_header.setPadding(instore_header.getPaddingLeft(),paddingTop, instore_header.getPaddingRight(), instore_header.getPaddingBottom());
         instore_header.invalidate();
     }

     //监听当前滚动状态
     public void onScrollStateChanged(AbsListView view, int scrollState) {
         this.scrollState = scrollState;
     }

    // 监听当前滚动的item
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;
    }

    //触屏事件
     public boolean onTouchEvent(MotionEvent ev) {
         switch(ev.getAction()){

             //手指落到屏幕上时
             case MotionEvent.ACTION_DOWN:
                 //如果当前可见的第一个item为第0号，说明ListView位于顶端，可以执行下拉刷新
                 if(firstVisibleItem == 0){
                     canPull = true;
                     startY = (int) ev.getY();
                 }
                 break;

             //手指在屏幕上拖动时
             case MotionEvent.ACTION_MOVE:
                 if(canPull){
                     touchMove(ev);
                 }
                 break;

             //手指离开屏幕时
             case MotionEvent.ACTION_UP:
                 canPull = false;
                 if(curState == RELEASE){
                     curState = RELEASING;
                     refreshHeaderByState();
                     //这里添加刷新数据的逻辑
                     mRefresfListener.refresh();
                 }else{
                     curState = NORMAL;
                     refreshHeaderByState();
                     paddingTop(-instore_header_height);
                 }
                 break;
            }

         return super.onTouchEvent(ev);
     }

     //根据触摸屏幕滑动来改变当前状态
     private void touchMove(MotionEvent ev) {

             int tempY = (int) ev.getY();
             int space = tempY -startY;//移动的距离
             int topdding = space-instore_header_height;
             paddingTop(topdding);//即时设定头布局的隐藏高度
             if(space>instore_header_height&&space<instore_header_height+50&&scrollState == SCROLL_STATE_TOUCH_SCROLL){
                      curState = PULL;//设定为下拉状态
                      refreshHeaderByState();
             }
             if(space>instore_header_height+50){
                     curState = RELEASE;//设定为提示刷新状态
                      refreshHeaderByState();
             }

             if(space<instore_header_height){
                     curState = NORMAL;//设定为正常状态
                      refreshHeaderByState();
             }

     }

     // 根据当前状态更改header的显示界面
     private void refreshHeaderByState( ){
             ProgressBar progressBar = (ProgressBar) instore_header.findViewById(R.id.progress_bar);
             ImageView imgView = (ImageView) instore_header.findViewById(R.id.img_arrow);
             TextView textView = (TextView) instore_header.findViewById(R.id.textinfo);

             switch(curState){

                     case NORMAL:
                         progressBar.setVisibility(View.GONE);
                         imgView.setVisibility(View.VISIBLE);
                         imgView.setImageResource(R.drawable.down_arrow);
                         textView.setText("下拉刷新");
                         break;
                     case PULL:
                         progressBar.setVisibility(View.GONE);
                         imgView.setVisibility(View.VISIBLE);
                         imgView.setImageResource(R.drawable.down_arrow);
                         textView.setText("下拉刷新");
                         break;
                    case RELEASE:
                        progressBar.setVisibility(View.GONE);
                        imgView.setVisibility(View.VISIBLE);
                        imgView.setImageResource(R.drawable.up_arrow);
                        textView.setText("松开刷新");
                        break;
                    case RELEASING:
                        progressBar.setVisibility(View.VISIBLE);
                        imgView.setVisibility(View.GONE);
                        textView.setText("正在刷新");
                        break;

                 }

     }

    //数据刷新完成后的操作
     public void refreshFinish(){
             curState = NORMAL;
             paddingTop(-instore_header_height);
             refreshHeaderByState();
     }
}
