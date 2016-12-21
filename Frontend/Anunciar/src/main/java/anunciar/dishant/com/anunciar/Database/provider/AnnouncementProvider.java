package anunciar.dishant.com.anunciar.Database.provider;

import anunciar.dishant.com.anunciar.Database.database.AnnouncementDatabase;

import anunciar.dishant.com.anunciar.Database.database.table.*;

import android.provider.BaseColumns;
import android.text.TextUtils;
import android.content.ContentUris;
import android.database.sqlite.SQLiteQueryBuilder;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementProvider extends ContentProvider {

    public static final String AUTHORITY = "anunciar.dishant.com.anunciar.Database.provider";

    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    public static final Uri ANNOUNCEMENT_CONTENT_URI = Uri.withAppendedPath(AnnouncementProvider.AUTHORITY_URI, AnnouncementContent.CONTENT_PATH);


    private static final UriMatcher URI_MATCHER;
    private AnnouncementDatabase mDatabase;

    private static final int ANNOUNCEMENT_DIR = 0;
    private static final int ANNOUNCEMENT_ID = 1;


    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, AnnouncementContent.CONTENT_PATH, ANNOUNCEMENT_DIR);
        URI_MATCHER.addURI(AUTHORITY, AnnouncementContent.CONTENT_PATH + "/#",    ANNOUNCEMENT_ID);

     }

    public static final class AnnouncementContent implements BaseColumns {
        public static final String CONTENT_PATH = "announcement";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.announcement_database.announcement";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.announcement_database.announcement";
    }


    @Override
    public final boolean onCreate() {
        mDatabase = new AnnouncementDatabase(getContext());
        return true;
    }

    @Override
    public final String getType(final Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case ANNOUNCEMENT_DIR:
                return AnnouncementContent.CONTENT_TYPE;
            case ANNOUNCEMENT_ID:
                return AnnouncementContent.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public final Cursor query(final Uri uri, String[] projection, final String selection, final String[] selectionArgs, final String sortOrder) {
        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        final SQLiteDatabase dbConnection = mDatabase.getReadableDatabase();

        switch (URI_MATCHER.match(uri)) {
            case ANNOUNCEMENT_ID:
                queryBuilder.appendWhere(AnnouncementTable._ID + "=" + uri.getLastPathSegment());
            case ANNOUNCEMENT_DIR:
                queryBuilder.setTables(AnnouncementTable.TABLE_NAME);
                break;

            default :
                throw new IllegalArgumentException("Unsupported URI:" + uri);
        }

        Cursor cursor = queryBuilder.query(dbConnection, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;

    }

    @Override
    public final Uri insert(final Uri uri, final ContentValues values) {
        final SQLiteDatabase dbConnection = mDatabase.getWritableDatabase();

        try {
            dbConnection.beginTransaction();

            switch (URI_MATCHER.match(uri)) {
                case ANNOUNCEMENT_DIR:
                case ANNOUNCEMENT_ID:
                    final long announcementId = dbConnection.insertOrThrow(AnnouncementTable.TABLE_NAME, null, values);
                    final Uri newAnnouncementUri = ContentUris.withAppendedId(ANNOUNCEMENT_CONTENT_URI, announcementId);
                    getContext().getContentResolver().notifyChange(newAnnouncementUri, null);

                    dbConnection.setTransactionSuccessful();
                    return newAnnouncementUri;
                default :
                    throw new IllegalArgumentException("Unsupported URI:" + uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbConnection.endTransaction();
        }

        return null;
    }

    @Override
    public final int update(final Uri uri, final ContentValues values, final String selection, final String[] selectionArgs) {
        final SQLiteDatabase dbConnection = mDatabase.getWritableDatabase();
        int updateCount = 0;
        List<Uri> joinUris = new ArrayList<Uri>();

        try {
            dbConnection.beginTransaction();

            switch (URI_MATCHER.match(uri)) {
                case ANNOUNCEMENT_DIR:
                    updateCount = dbConnection.update(AnnouncementTable.TABLE_NAME, values, selection, selectionArgs);

                    dbConnection.setTransactionSuccessful();
                    break;
                case ANNOUNCEMENT_ID:
                   final long announcementId = ContentUris.parseId(uri);
                   updateCount = dbConnection.update(AnnouncementTable.TABLE_NAME, values,
                       AnnouncementTable._ID + "=" + announcementId + (TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ")"), selectionArgs);

                   dbConnection.setTransactionSuccessful();
                   break;

                default :
                    throw new IllegalArgumentException("Unsupported URI:" + uri);
            }
        } finally {
            dbConnection.endTransaction();
        }

        if (updateCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);

            for (Uri joinUri : joinUris) {
                getContext().getContentResolver().notifyChange(joinUri, null);
            }
        }

        return updateCount;

    }

    @Override
    public final int delete(final Uri uri, final String selection, final String[] selectionArgs) {
        final SQLiteDatabase dbConnection = mDatabase.getWritableDatabase();
        int deleteCount = 0;
        List<Uri> joinUris = new ArrayList<Uri>();

        try {
            dbConnection.beginTransaction();

            switch (URI_MATCHER.match(uri)) {
                case ANNOUNCEMENT_DIR:
                    deleteCount = dbConnection.delete(AnnouncementTable.TABLE_NAME, selection, selectionArgs);

                    dbConnection.setTransactionSuccessful();
                    break;
                case ANNOUNCEMENT_ID:
                    deleteCount = dbConnection.delete(AnnouncementTable.TABLE_NAME, AnnouncementTable.WHERE_ID_EQUALS, new String[] { uri.getLastPathSegment() });

                    dbConnection.setTransactionSuccessful();
                    break;

                default :
                    throw new IllegalArgumentException("Unsupported URI:" + uri);
            }
        } finally {
            dbConnection.endTransaction();
        }

        if (deleteCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);

            for (Uri joinUri : joinUris) {
                getContext().getContentResolver().notifyChange(joinUri, null);
            }
        }

        return deleteCount;
    }
}