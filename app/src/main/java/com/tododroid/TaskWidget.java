package com.tododroid;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import java.util.ArrayList;

public class TaskWidget extends AppWidgetProvider {
    
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, widgetId);
        }
    }
    
    private void updateWidget(Context context, AppWidgetManager manager, int widgetId) {
        GlobalData.getInstance().loadFromFile(context);
        ArrayList<TodoItem> tasks = GlobalData.getInstance().getTasks();
        
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_tasks);
        
        // Set task texts
        views.setTextViewText(R.id.widget_task1, tasks.size() > 0 ? "• " + tasks.get(0).getTitle() : "");
        views.setTextViewText(R.id.widget_task2, tasks.size() > 1 ? "• " + tasks.get(1).getTitle() : "");
        views.setTextViewText(R.id.widget_task3, tasks.size() > 2 ? "• " + tasks.get(2).getTitle() : "");
        
        // Open app on tap
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pending = PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_title, pending);
        
        manager.updateAppWidget(widgetId, views);
    }
}
