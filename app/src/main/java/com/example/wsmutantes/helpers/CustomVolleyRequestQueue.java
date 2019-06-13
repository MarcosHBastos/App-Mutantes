package com.example.wsmutantes.helpers;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

public class CustomVolleyRequestQueue {

    private static CustomVolleyRequestQueue mInstance;
    private static Context mCtx;
    private RequestQueue mRq;

    private CustomVolleyRequestQueue(Context context) {
        mCtx = context;
        mRq = getRequestQueue();
    }

    public static synchronized CustomVolleyRequestQueue getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new CustomVolleyRequestQueue(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if(mRq ==null) {
            Cache cache = new DiskBasedCache(mCtx.getCacheDir(), 10 * 1024 *1024);
            Network network = new BasicNetwork(new HurlStack());
            mRq = new RequestQueue(cache, network);
            mRq.start();
        }
        return mRq;
    }
}
