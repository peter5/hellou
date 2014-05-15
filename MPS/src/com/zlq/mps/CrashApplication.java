package com.zlq.mps;

import com.zlq.renmaitong.CrashHandler;

import android.app.Application;

public class CrashApplication extends Application {  
    @Override  
    public void onCreate() {  
        super.onCreate();  
        CrashHandler crashHandler = CrashHandler.getInstance();  
        crashHandler.init(this);  
    }  
}  