package com.lazy.schedule;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.webkit.JavascriptInterface;

public class ScheduleBridge {
    private final Context context;

    public ScheduleBridge(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void save(String json) {
        SharedPreferences sp = context.getSharedPreferences("schedule", Context.MODE_PRIVATE);
        sp.edit().putString("data", json).apply();
        refreshWidget();
    }

    private void refreshWidget() {
        Intent intent = new Intent(context, ScheduleWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(new ComponentName(context, ScheduleWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }
}
