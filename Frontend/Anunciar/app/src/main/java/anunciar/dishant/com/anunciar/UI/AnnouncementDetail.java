package anunciar.dishant.com.anunciar.UI;


import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import anunciar.dishant.com.anunciar.Database.AnnouncementTable;
import anunciar.dishant.com.anunciar.R;


public class AnnouncementDetail extends AppCompatActivity {
    Cursor cursor;
    Date dead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_detail);
        Transition enterTrans = new Slide(Gravity.RIGHT);
        enterTrans.setDuration(300);
        enterTrans.setInterpolator(new FastOutSlowInInterpolator());
        getWindow().setEnterTransition(enterTrans);

        Transition returnTrans = new Slide(Gravity.RIGHT);
        returnTrans.setDuration(300);
        returnTrans.setInterpolator(new FastOutSlowInInterpolator());
        getWindow().setReturnTransition(returnTrans);

        Intent intent = getIntent();
        int id = intent.getIntExtra(AnnouncementTable.FIELD_ID, 0);
        cursor = getContentResolver().query(AnnouncementTable.CONTENT_URI, null, AnnouncementTable.FIELD_ID + " = " + id, null, null);
        if (cursor == null) {
            Log.e("ERROR", "onCreate: No Content", null);
        } else {
            DatabaseUtils.dumpCursor(cursor);
            cursor.moveToFirst();
            TextView title, description, created;
            Button deadline;

            title = (TextView) findViewById(R.id.announcement_title);
            Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            setTitle("");
            description = (TextView) findViewById(R.id.announcement_description);
            deadline = (Button) findViewById(R.id.announcement_deadline);
            created = (TextView) findViewById(R.id.announcement_created);

            title.setText(cursor.getString(cursor.getColumnIndex(AnnouncementTable.FIELD_TITLE)));
            title.setContentDescription(title.getText());
            description.setText(cursor.getString(cursor.getColumnIndex(AnnouncementTable.FIELD_DESCRIPTION)));
            description.setContentDescription(description.getText());

            if (cursor.getString(cursor.getColumnIndex(AnnouncementTable.FIELD_DEADLINE)) == null) {
                deadline.setVisibility(View.INVISIBLE);
                deadline.setClickable(false);
            } else {
                try {
                    Date now = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    dead = format.parse(cursor.getString(cursor.getColumnIndex(AnnouncementTable.FIELD_DEADLINE)));
                    if (TimeUnit.MILLISECONDS.toDays(now.getTime() - dead.getTime()) == 0) {
                        deadline.setText(R.string.today_deadline);
                        deadline.setTextColor(Color.RED);
                        deadline.setClickable(false);
                    } else if ((dead.getTime() > now.getTime())) {
                        deadline.setText(TimeUnit.MILLISECONDS.toDays(dead.getTime() - now.getTime()) + " days left");
                        deadline.setTextColor(Color.parseColor("#F8876300"));
                    } else if ((dead.getTime() < now.getTime())) {
                        deadline.setText("Overdue " + TimeUnit.MILLISECONDS.toDays(now.getTime() - dead.getTime()) + " days ago");
                        deadline.setTextColor(Color.RED);
                        deadline.setClickable(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date past = format.parse(cursor.getString(cursor.getColumnIndex(AnnouncementTable.FIELD_CREATED_AT)).substring(0, 10));
                Date now = new Date();
                String days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + " days ago";
                if (TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) == 0) {
                    created.setText(R.string.day_today);

                } else {
                    created.setText(days);

                }
                created.setContentDescription(created.getText());
            } catch (Exception j) {
                j.printStackTrace();
            }
        }
    }

    public void addCalendar(View view) {
        if (cursor.getString(cursor.getColumnIndex(AnnouncementTable.FIELD_DEADLINE)).equals("null")) {
            Toast.makeText(getApplicationContext(), "Deadline is empty", Toast.LENGTH_SHORT).show();
        } else {
            String date = cursor.getString(cursor.getColumnIndex(AnnouncementTable.FIELD_DEADLINE));
            String[] days = date.split("-");
            Calendar begintime = Calendar.getInstance();
            begintime.set(Integer.parseInt(days[0]), Integer.parseInt(days[1]) - 1, Integer.parseInt(days[2]), 7, 30);
            Calendar endtime = Calendar.getInstance();
            endtime.set(Integer.parseInt(days[0]), Integer.parseInt(days[1]) - 1, Integer.parseInt(days[2]), 8, 30);
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begintime.getTimeInMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endtime.getTimeInMillis())
                    .putExtra(CalendarContract.Events.TITLE, cursor.getString(cursor.getColumnIndex(AnnouncementTable.FIELD_TITLE)))
                    .putExtra(CalendarContract.Events.DESCRIPTION, cursor.getString(cursor.getColumnIndex(AnnouncementTable.FIELD_DESCRIPTION)))
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, "College")
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
            startActivity(intent);
        }
    }
}
