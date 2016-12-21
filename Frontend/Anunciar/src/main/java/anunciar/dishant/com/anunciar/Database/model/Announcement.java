package anunciar.dishant.com.anunciar.Database.model;

import android.content.ContentValues;
import android.database.Cursor;

import anunciar.dishant.com.anunciar.Database.database.table.AnnouncementTable;

import java.util.ArrayList;
import java.util.List;

public class Announcement {
    private long mRowId;
    private String mId;
    private String mTitle;
    private String mDescription;
    private String mDeadline;
    private String mTags;
    private String mCreatedAt;
    private String mUpdatedAt;


    private ContentValues mValues = new ContentValues();

    public Announcement() {}

    public Announcement(final Cursor cursor) {
        this(cursor, false);
    }

    public Announcement(final Cursor cursor, boolean prependTableName) {
        String prefix = prependTableName ? AnnouncementTable.TABLE_NAME + "_" : "";
        setRowId(cursor.getLong(cursor.getColumnIndex(prefix + AnnouncementTable._ID)));
        setId(cursor.getString(cursor.getColumnIndex(prefix + AnnouncementTable.ID)));
        setTitle(cursor.getString(cursor.getColumnIndex(prefix + AnnouncementTable.TITLE)));
        setDescription(cursor.getString(cursor.getColumnIndex(prefix + AnnouncementTable.DESCRIPTION)));
        setDeadline(cursor.getString(cursor.getColumnIndex(prefix + AnnouncementTable.DEADLINE)));
        setTags(cursor.getString(cursor.getColumnIndex(prefix + AnnouncementTable.TAGS)));
        setCreatedAt(cursor.getString(cursor.getColumnIndex(prefix + AnnouncementTable.CREATED_AT)));
        setUpdatedAt(cursor.getString(cursor.getColumnIndex(prefix + AnnouncementTable.UPDATED_AT)));

    }

    public ContentValues getContentValues() {
        return mValues;
    }

    public Long getRowId() {
        return mRowId;
    }

    public void setRowId(long _id) {
        mRowId = _id;
        mValues.put(AnnouncementTable._ID, _id);
    }
    public void setId(String id) {
        mId = id;
        mValues.put(AnnouncementTable.ID, id);
    }

    public String getId() {
            return mId;
    }


    public void setTitle(String title) {
        mTitle = title;
        mValues.put(AnnouncementTable.TITLE, title);
    }

    public String getTitle() {
            return mTitle;
    }


    public void setDescription(String description) {
        mDescription = description;
        mValues.put(AnnouncementTable.DESCRIPTION, description);
    }

    public String getDescription() {
            return mDescription;
    }


    public void setDeadline(String deadline) {
        mDeadline = deadline;
        mValues.put(AnnouncementTable.DEADLINE, deadline);
    }

    public String getDeadline() {
            return mDeadline;
    }


    public void setTags(String tags) {
        mTags = tags;
        mValues.put(AnnouncementTable.TAGS, tags);
    }

    public String getTags() {
            return mTags;
    }


    public void setCreatedAt(String created_at) {
        mCreatedAt = created_at;
        mValues.put(AnnouncementTable.CREATED_AT, created_at);
    }

    public String getCreatedAt() {
            return mCreatedAt;
    }


    public void setUpdatedAt(String updated_at) {
        mUpdatedAt = updated_at;
        mValues.put(AnnouncementTable.UPDATED_AT, updated_at);
    }

    public String getUpdatedAt() {
            return mUpdatedAt;
    }




    public static List<Announcement> listFromCursor(Cursor cursor) {
        List<Announcement> list = new ArrayList<Announcement>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(new Announcement(cursor));
            } while (cursor.moveToNext());
        }

        return list;
    }
}