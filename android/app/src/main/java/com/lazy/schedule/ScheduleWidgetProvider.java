package com.lazy.schedule;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds) {
        for (int id : appWidgetIds) {
            updateWidget(context, manager, id);
        }
    }

    static void updateWidget(Context context, AppWidgetManager manager, int id) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_schedule);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_root, pi);

        SimpleDateFormat fmt = new SimpleDateFormat("M月d日 E", Locale.CHINA);
        views.setTextViewText(R.id.widget_date, fmt.format(new Date()));

        SharedPreferences sp = context.getSharedPreferences("schedule", Context.MODE_PRIVATE);
        List<Task> tasks = parseTasks(sp.getString("data", "[]"));

        int[] rows = {R.id.row1, R.id.row2, R.id.row3, R.id.row4};
        int[] dots = {R.id.dot1, R.id.dot2, R.id.dot3, R.id.dot4};
        int[] titles = {R.id.title1, R.id.title2, R.id.title3, R.id.title4};
        int[] times = {R.id.time1, R.id.time2, R.id.time3, R.id.time4};

        int shown = Math.min(4, tasks.size());
        for (int k = 0; k < 4; k++) {
            if (k < shown) {
                Task t = tasks.get(k);
                views.setViewVisibility(rows[k], View.VISIBLE);
                views.setTextViewText(titles[k], t.title);
                views.setTextViewText(times[k], t.timeLabel);
                views.setTextColor(dots[k], urgencyColor(t));
                views.setTextColor(titles[k], Color.parseColor("#1b1a17"));
            } else {
                views.setViewVisibility(rows[k], View.GONE);
            }
        }
        views.setViewVisibility(R.id.widget_empty, shown == 0 ? View.VISIBLE : View.GONE);

        manager.updateAppWidget(id, views);
    }

    static int urgencyColor(Task t) {
        double daysLeft = (t.endMs - System.currentTimeMillis()) / 86400000.0;
        if (daysLeft < 0) return Color.parseColor("#b8322f");
        if (daysLeft <= 1) return Color.parseColor("#c7403a");
        if (daysLeft <= 3) return Color.parseColor("#d1762f");
        if (daysLeft <= 7) return Color.parseColor("#c49a2e");
        if (daysLeft <= 14) return Color.parseColor("#4e8a5f");
        return Color.parseColor("#7f9b8a");
    }

    static List<Task> parseTasks(String json) {
        List<Task> list = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                if (o.optBoolean("done", false)) continue;
                String title = o.optString("title", "");
                String end = o.optString("end", "");
                String endTime = o.optString("endTime", "");
                list.add(new Task(title, parseTime(end, endTime), timeLabel(end, endTime)));
            }
        } catch (Exception ignored) {}
        Collections.sort(list, new Comparator<Task>() {
            @Override
            public int compare(Task a, Task b) {
                return Long.compare(a.endMs, b.endMs);
            }
        });
        return list;
    }

    static long parseTime(String date, String time) {
        try {
            if (time == null || time.isEmpty()) {
                Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date);
                return d.getTime() + 23L * 3600000L + 59L * 60000L;
            }
            return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).parse(date + " " + time).getTime();
        } catch (ParseException e) {
            return System.currentTimeMillis();
        }
    }

    static String timeLabel(String date, String time) {
        try {
            String[] p = date.split("-");
            int m = Integer.parseInt(p[1]);
            int d = Integer.parseInt(p[2]);
            if (time == null || time.isEmpty()) return m + "/" + d + " 全天";
            return m + "/" + d + " " + time;
        } catch (Exception e) {
            return date;
        }
    }

    static class Task {
        final String title;
        final long endMs;
        final String timeLabel;

        Task(String title, long endMs, String timeLabel) {
            this.title = title;
            this.endMs = endMs;
            this.timeLabel = timeLabel;
        }
    }
}
