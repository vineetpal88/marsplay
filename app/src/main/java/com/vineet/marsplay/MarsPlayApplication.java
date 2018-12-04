package com.vineet.marsplay;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.cloudinary.android.MediaManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MarsPlayApplication extends Application {
    public static final String TAG = MarsPlayApplication.class.getSimpleName();
    public static ConnectivityManager cm;
    private static Context context = null;
    public RequestManager requestManagerGlide;
    private static MarsPlayApplication mInstance;
    private List<Activity> weakReferenceList = new ArrayList<>();
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Glide.get(this).trimMemory(level);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //MultiDex.install(this);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        MediaManager.init(this);
        mInstance = this;
        context = getApplicationContext();
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                WeakReference<Activity> mActivity = new WeakReference<Activity>(activity);
                weakReferenceList.add(activity);
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                WeakReference<Activity> mActivity = new WeakReference<Activity>(activity);
                if (weakReferenceList.contains(activity))
                    weakReferenceList.remove(activity);
            }

            /** Unused implementation **/
            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }
        });
    }
    public static synchronized MarsPlayApplication getInstance() {
        return mInstance;
    }
    public static MarsPlayApplication getApp(Context ctx) {
        return (MarsPlayApplication) ctx.getApplicationContext();
    }
    public static Context getContext() {
        return context;
    }
    public Activity getCurrentActivity() {
        Activity activityWeakReference = weakReferenceList.get(weakReferenceList.size() - 1);
        return activityWeakReference;
    }
    public RequestManager getRequestManagerGlide() {
        if (requestManagerGlide == null) {
            requestManagerGlide = Glide.with(getContext());
        }
        return requestManagerGlide;
    }
}
