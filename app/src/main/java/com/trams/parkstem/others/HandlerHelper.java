package com.trams.parkstem.others;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Noverish on 2016-08-04.
 */
public class HandlerHelper {
    public static void setVisibilityHandler(android.os.Handler handler, final View view, final int visibility) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(visibility);
            }
        });
    }

    public static void addViewHandler(android.os.Handler handler, final ViewGroup viewGroup, final View view) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                viewGroup.addView(view);
            }
        });
    }

    public static void removeAllViewsHandler(android.os.Handler handler, final ViewGroup viewGroup) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                viewGroup.removeAllViews();
            }
        });
    }

    public static void setTextHandler(android.os.Handler handler, final TextView textView, final String text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
            }
        });
    }
}
