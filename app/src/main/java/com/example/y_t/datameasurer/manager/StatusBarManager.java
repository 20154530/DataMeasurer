package com.example.y_t.datameasurer.manager;

import android.app.Activity;
import android.view.View;
import android.view.Window;

import java.lang.ref.WeakReference;

/**
 * 管理状态栏的单例类
 */
public class StatusBarManager {
    //单例
    private static StatusBarManager sInstance = new StatusBarManager();
    //保存当前Activity的引用
    private WeakReference<Activity> sCurrentActivityWeakRef;
    //只读属性判断是否为状态栏深色模式
    private boolean dark = false;

    public StatusBarManager() { }

    public static StatusBarManager getInstance() {
        return sInstance;
    }

    public Activity getCurrentActivity() {
        /**
         *@Description: 获取当前Activity
         *@Param: []
         *@return: android.app.Activity
         *@Author: Y_Theta
         *@date: 2019/1/1
         */
        Activity currentActivity = null;
        if (sCurrentActivityWeakRef != null) {
            currentActivity = sCurrentActivityWeakRef.get();
        }
        return currentActivity;
    }

    public int GetHeight(){
        int result = 0;
        int resourceId = sCurrentActivityWeakRef.get().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = sCurrentActivityWeakRef.get().getResources().getDimensionPixelSize(resourceId);
        }
       return result;
    }

    public void setCurrentActivity(Activity activity) {
        /**
         *@Description: 设置当前Activity
         *@Param: [activity] 将要显示的Activity
         *@return: void
         *@Author: Y_Theta
         *@date: 2019/1/1
         */
        sCurrentActivityWeakRef = new WeakReference<Activity>(activity);
    }

    public boolean isDark() {
        /**
         *@Description: 判断状态栏是否为深色模式
         *@Param: []
         *@return: boolean
         *@Author: Y_Theta
         *@date: 2019/1/1
         */
        return this.dark;
    }

    public void SetStatusBarMode(boolean lightStatusBar) {
        /**
         *@Description: 设置状态栏模式
         *@Param: [lightStatusBar] 状态栏模式 true 深色 /false 浅色
         *@return: void
         *@Author: Y_Theta
         *@date: 2019/1/1
         */
        // 设置浅色状态栏时的界面显示
        Window window = getCurrentActivity().getWindow();
        View decor = window.getDecorView();
        int ui = decor.getSystemUiVisibility();
        if (lightStatusBar) {
            //使系统识别当前为浅色任务栏背景
            dark = true;
            ui |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        } else {
            dark = false;
            ui &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        decor.setSystemUiVisibility(ui);

    }
}
