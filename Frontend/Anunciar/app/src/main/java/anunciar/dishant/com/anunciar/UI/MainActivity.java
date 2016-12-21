package anunciar.dishant.com.anunciar.UI;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import anunciar.dishant.com.anunciar.API.API_Calls;
import anunciar.dishant.com.anunciar.Database.Announcement;
import anunciar.dishant.com.anunciar.Database.AnnouncementTable;
import anunciar.dishant.com.anunciar.Database.AnnouncementTableDefinition;
import anunciar.dishant.com.anunciar.Internet.VolleySingleton;
import anunciar.dishant.com.anunciar.R;

public class MainActivity extends AppCompatActivity {
    SharedPreferences prefs = null;
    RecyclerView mRecyclerView;
    private String TAG = "Dishant";
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.announcement_list);
        prefs = getSharedPreferences("anunciar.dishant.com.anunciar"
                , MODE_PRIVATE);
        cursor = getContentResolver().query(AnnouncementTable.CONTENT_URI
                , null
                , null
                , null
                , null);
        if (cursor.getCount() == 0) {
            //TODO add all the announcement to the database
            Log.e(TAG, "onCreate: Empty database, but working :)"
                    , null);
            Announcement.getmAnnouncements().clear();
            JsonArrayRequest mJsonObjectRequest = new JsonArrayRequest(Request.Method.GET
                    , API_Calls.GET_ALL_ANNOUNCMENT
                    , null
                    , new Response.Listener < JSONArray > () {
                @Override
                public void onResponse(JSONArray response) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject mJsonObject = response.getJSONObject(i);
                            ContentValues announcement = new ContentValues();
                            announcement.put(AnnouncementTable.FIELD_ID, mJsonObject.getInt("id"));
                            announcement.put(AnnouncementTable.FIELD_TITLE, mJsonObject.getInt("title"));
                            announcement.put(AnnouncementTable.FIELD_DESCRIPTION, mJsonObject.getInt("description"));
                            announcement.put(AnnouncementTable.FIELD_DEADLINE, mJsonObject.getInt("deadline"));
                            announcement.put(AnnouncementTable.FIELD_TAGS, mJsonObject.getInt("tags"));
                            announcement.put(AnnouncementTable.FIELD_CREATED_AT, mJsonObject.getInt("created_at"));
                            announcement.put(AnnouncementTable.FIELD_UPDATED_AT, mJsonObject.getInt("updated_at"));

                            Uri mUri = getContentResolver().insert(AnnouncementTable.CONTENT_URI,
                                    announcement);
                            Announcement ann = new Announcement(
                                    mJsonObject.getInt("id")
                                    , mJsonObject.getString("title")
                                    , mJsonObject.getString("description")
                                    , mJsonObject.getString("deadline")
                                    , mJsonObject.getString("tags")
                                    , mJsonObject.getString("created_at")
                                    , mJsonObject.getString("updated_at"));
                            ann.addToList(ann);
                        } catch (Exception e) {

                        }
                    }
                    cursor = getContentResolver().query(AnnouncementTable.CONTENT_URI,
                            null,
                            null,
                            null,
                            null );
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    mRecyclerView.setAdapter(new AnnouncementAdapter(cursor));

                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse: "+error.getMessage(),null );
                }
            });
            VolleySingleton.getInstance(this).addToRequestQueue(mJsonObjectRequest);


        } else {
            //TODO add only the new announcement
        }

    }

    public class AnnouncementAdapter extends RecyclerView.Adapter < AnnouncementAdapter.MyViewHolder > {
        Cursor mCursor;
        LayoutInflater layoutInflater;
        Context context;
        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView title, createAt;
            public MyViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.card_title);
                createAt = (TextView) itemView.findViewById(R.id.card_created);
            }
        }

        public AnnouncementAdapter(Cursor aCursor) {
            mCursor = aCursor;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder vh = new MyViewHolder(((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.announcement_list_item, null));
            return vh;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            cursor.moveToPosition(position);
            holder.title.setText(cursor.getString(cursor.getColumnIndex(AnnouncementTable.FIELD_TITLE)));
            holder.createAt.setText(cursor.getString(cursor.getColumnIndex(AnnouncementTable.FIELD_CREATED_AT)));
        }

        @Override
        public int getItemCount() {
            return cursor.getCount();
        }
    }
}