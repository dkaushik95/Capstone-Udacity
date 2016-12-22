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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

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
    int localCount;

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
            Log.e(TAG, "onCreate: Empty database, but working :)"
                    , null);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET
                    , API_Calls.GET_ALL_ANNOUNCMENT
                    , null
                    , new Response.Listener < JSONArray > () {
                @Override
                public void onResponse(JSONArray response) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject mJsonObject = response.getJSONObject(i);
                            ContentValues announcement = new ContentValues();
                            announcement.put(AnnouncementTable.FIELD_ID, mJsonObject.getString("id"));
                            announcement.put(AnnouncementTable.FIELD_TITLE, mJsonObject.getString("title"));
                            announcement.put(AnnouncementTable.FIELD_DESCRIPTION, mJsonObject.getString("description"));
                            announcement.put(AnnouncementTable.FIELD_DEADLINE, mJsonObject.getString("deadline"));
                            announcement.put(AnnouncementTable.FIELD_TAGS, mJsonObject.getString("tags"));
                            announcement.put(AnnouncementTable.FIELD_CREATED_AT, mJsonObject.getString("created_at"));
                            announcement.put(AnnouncementTable.FIELD_UPDATED_AT, mJsonObject.getString("updated_at"));
                            getContentResolver()
                                    .insert(AnnouncementTable.CONTENT_URI,
                                            announcement);
                        } catch (Exception e) {
                            Log.e(TAG, "onResponse: "+e.getMessage(), null);
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
            VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);


        } else {
            Log.e(TAG, "onCreate: Data exists", null);
            localCount = cursor.getCount();
            StringRequest mStringRequest = new StringRequest(Request.Method.GET, API_Calls.GET_COUNT, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        prefs.edit().putInt("GlobalCount", Integer.parseInt(response)).apply();
                    }
                    catch (Exception e){
                        Log.e(TAG, "onResponse: Couldn't GET count",null);
                    }
                    finally {
                        if (localCount<prefs.getInt("GlobalCount",0)){
                            //TODO add only new announcement here
                            Log.e(TAG, "onCreate: GETTING NEW DATA",null);
                        }
                        else {
                            Log.e(TAG, "onCreate: SHOWING OFFLINE DATA :) " + localCount+ " :: " + prefs.getInt("GlobalCount", 0),null );
                            cursor = getContentResolver().query(AnnouncementTable.CONTENT_URI,
                                    null,
                                    null,
                                    null,
                                    null );
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            mRecyclerView.setAdapter(new AnnouncementAdapter(cursor));
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse: " +error.getMessage(), null);
                }
            });
            VolleySingleton.getInstance(this).addToRequestQueue(mStringRequest);

        }

    }

    public class AnnouncementAdapter extends RecyclerView.Adapter < AnnouncementAdapter.MyViewHolder > {
        Cursor mCursor;
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
            mCursor.moveToFirst();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder vh = new MyViewHolder(((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.announcement_list_item,null));
            return vh;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            mCursor.moveToPosition(position);
            holder.title.setText(mCursor.getString(mCursor.getColumnIndex(AnnouncementTable.FIELD_TITLE)));
            holder.createAt.setText(mCursor.getString(mCursor.getColumnIndex(AnnouncementTable.FIELD_CREATED_AT)));
        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }
    }
}