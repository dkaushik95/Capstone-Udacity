package anunciar.dishant.com.anunciar.Service;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import anunciar.dishant.com.anunciar.Database.AnnouncementTable;
import anunciar.dishant.com.anunciar.R;

/**
 * Created by dishantkaushik on 12/28/16.
 */

public class AnunciarWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private static int mCount;
    private Context mContext;
    private Cursor cursor;

    public StackRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;

        int mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public void onCreate() {

    }

    public int getmCount() {
        return mCount;
    }

    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.stack_item);
        if (cursor == null || cursor.getCount() == 0) {
            rv.setEmptyView(R.id.stack_view, R.id.empty_view);
            return null;
        }
        cursor.moveToPosition(position);
        rv.setTextViewText(R.id.widgetTitle, cursor.getString(cursor.getColumnIndex(AnnouncementTable.FIELD_TITLE)));

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date past = format.parse(cursor.getString(cursor.getColumnIndex(AnnouncementTable.FIELD_CREATED_AT)).substring(0, 10));
            Date now = new Date();
            String days = mContext.getString(R.string.days_ago_in_words, TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()));
            if (TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) == 0) {
                rv.setTextViewText(R.id.widgetCreated, mContext.getString(R.string.day_today));
            } else {
                rv.setTextViewText(R.id.widgetCreated, days);
            }
        } catch (Exception j) {
            j.printStackTrace();
        }

        Intent announcementDetailIntent = new Intent();

        announcementDetailIntent.putExtra(AnnouncementTable.FIELD__ID, cursor.getInt(cursor.getColumnIndex(AnnouncementTable.FIELD__ID)));
        rv.setOnClickFillInIntent(R.id.stackWidgetItem, announcementDetailIntent);
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        final long identityToken = Binder.clearCallingIdentity();

        cursor = mContext.getContentResolver().query(AnnouncementTable.CONTENT_URI, new String[]{AnnouncementTable.FIELD__ID, AnnouncementTable.FIELD_TITLE, AnnouncementTable.FIELD_CREATED_AT}, null, null, AnnouncementTable.FIELD_CREATED_AT);
        Binder.restoreCallingIdentity(identityToken);

        if (cursor != null && cursor.getCount() != 0) {
            mCount = cursor.getCount();
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mCount;
    }
}
