package com.main.maybe.simplepulltorefresh;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    private float mLastMotionX, mLastMotionY; //  记录手指触摸的位置x,y坐标
    private float mDeltax, mDeltay; // 记录当前手指拉动的x和y偏移量
    // 当前处于什么状态
    private enum State{
        REFRESHING, // 正在刷新
        PULLING_HORIZONTAL, // 水平拉动
        PULLING_VERTICAL, // 垂直拉动
        NORMAL // 正常状态
    };
    // 记录拉动的方向
    private enum Orientation{
        HORIZONTAL, // 水平
        VERTICAL // 垂直
    }
    private State mState; // 当前状态
    private Orientation mOrientation; // 当前拉动方向

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
