package anunciar.dishant.com.anunciar.Database;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import anunciar.dishant.com.anunciar.R;
import anunciar.dishant.com.anunciar.Service.AnunciarSyncService;
import anunciar.dishant.com.anunciar.Service.AnunciarWidgetService;
import anunciar.dishant.com.anunciar.UI.AnnouncementDetail;

/**
 * Created by dishantkaushik on 12/28/16.
 */

public class AnunciarWidgetProvider extends AppWidgetProvider {
    public static final String ACTION_DATA_UPDATED = "anunciar.dishant.com.anunciar";

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_DATA_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.stack_view);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {

            Intent in = new Intent(context, AnunciarSyncService.class);
            context.startService(in);

            Intent intent = new Intent(context, AnunciarWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);

            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.anunciar_appwidget);
            rv.setRemoteAdapter(R.id.stack_view, intent);
            rv.setEmptyView(R.id.stack_view, R.id.empty_view);

            Intent intentOnClick = new Intent(context, AnnouncementDetail.class);

            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentOnClick, PendingIntent.FLAG_UPDATE_CURRENT);

            rv.setPendingIntentTemplate(R.id.stack_view, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);

        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

}
