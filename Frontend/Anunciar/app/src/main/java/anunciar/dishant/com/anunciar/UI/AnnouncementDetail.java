package anunciar.dishant.com.anunciar.UI;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.v4.view.ViewGroupCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import anunciar.dishant.com.anunciar.Database.AnnouncementTable;
import anunciar.dishant.com.anunciar.R;


public class AnnouncementDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_detail);
        Intent intent = getIntent();
        int id = intent.getIntExtra(AnnouncementTable.FIELD_ID,0);
        Cursor cursor = getContentResolver().query(AnnouncementTable.CONTENT_URI, null, AnnouncementTable.FIELD_ID + " = "+id, null, null);
        if (cursor == null ){
            Log.e("ERROR", "onCreate: No Content", null);
        }
        else {
            DatabaseUtils.dumpCursor(cursor);
            cursor.moveToFirst();
            TextView title, description, deadline, created;

            title = (TextView) findViewById(R.id.announcement_title);
            if (title == null){
                Log.e("Dishant", "onCreate: title is not working", null);
            }
            description = (TextView) findViewById(R.id.announcement_description);
            deadline = (TextView) findViewById(R.id.announcement_deadline);
            created = (TextView) findViewById(R.id.announcement_created);
            title.setText(cursor.getString(cursor.getColumnIndex(AnnouncementTable.FIELD_TITLE)));
            description.setText(cursor.getString(cursor.getColumnIndex(AnnouncementTable.FIELD_DESCRIPTION)));
            deadline.setText(cursor.getString(cursor.getColumnIndex(AnnouncementTable.FIELD_DEADLINE)));
            created.setText(cursor.getString(cursor.getColumnIndex(AnnouncementTable.FIELD_CREATED_AT)));
        }
    }

}
