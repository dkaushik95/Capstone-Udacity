package anunciar.dishant.com.anunciar.Service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import anunciar.dishant.com.anunciar.API.API_Calls;
import anunciar.dishant.com.anunciar.Database.AnnouncementTable;
import anunciar.dishant.com.anunciar.Internet.VolleySingleton;
import anunciar.dishant.com.anunciar.UI.MainActivity;

/**
 * Created by dishantkaushik on 12/27/16.
 */

public class AnunciarService extends IntentService {
    String TAG = "Anunciar Service";
    int localCount;
    SharedPreferences prefs;


    public AnunciarService() {
        super("AnunciarService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        prefs = getSharedPreferences("anunciar.dishant.com.anunciar"
                , MODE_PRIVATE);
        Cursor cursor = getContentResolver().query(AnnouncementTable.CONTENT_URI
                , null
                , null
                , null
                , AnnouncementTable.FIELD_CREATED_AT);
        if (cursor.getCount() == 0) {
            Log.e(TAG, "onCreate: Empty database, but working :)"
                    , null);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET
                    , API_Calls.GET_ALL_ANNOUNCMENT
                    , null
                    , new Response.Listener <JSONArray> () {
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
                            final int difference = prefs.getInt("GlobalCount",0) - localCount;
                            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET
                                    , API_Calls.GET_ALL_ANNOUNCMENT
                                    , null
                                    , new Response.Listener < JSONArray > () {
                                @Override
                                public void onResponse(JSONArray response) {
                                    for (int i = 0; i < difference; i++) {
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
                                }

                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e(TAG, "onErrorResponse: "+error.getMessage(),null );
                                }
                            });
                            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
                            Toast.makeText(getApplicationContext(), difference+" new notifications!", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onCreate: GETTING NEW DATA",null);
                        }
                        else {
                            Log.e(TAG, "onCreate: SHOWING OFFLINE DATA :) " + localCount+ " :: " + prefs.getInt("GlobalCount", 0),null );
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
    static public class AlarmReciever extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Intent sendIntent = new Intent(context, AnunciarService.class);
            context.startService(sendIntent);
        }
    }
}
