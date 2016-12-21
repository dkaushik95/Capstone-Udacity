package anunciar.dishant.com.anunciar.Database.database;

import anunciar.dishant.com.anunciar.Database.database.table.*;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AnnouncementDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "announcement_database.db";
    private static final int DATABASE_VERSION = 1;

    public AnnouncementDatabase(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public final void onCreate(final SQLiteDatabase db) {
        db.execSQL(AnnouncementTable.SQL_CREATE);

    }

    @Override
    public final void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        upgrade(db, oldVersion, newVersion);
    }

    private final void dropTablesAndCreate(final SQLiteDatabase db) {
        db.execSQL(AnnouncementTable.SQL_DROP);


        onCreate(db);
    }

    private void upgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        dropTablesAndCreate(db);
    }
}