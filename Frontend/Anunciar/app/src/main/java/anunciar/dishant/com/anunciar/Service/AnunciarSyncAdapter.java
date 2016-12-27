package anunciar.dishant.com.anunciar.Service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
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
import anunciar.dishant.com.anunciar.R;
import anunciar.dishant.com.anunciar.UI.MainActivity;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by dishantkaushik on 12/28/16.
 */

public class AnunciarSyncAdapter extends AbstractThreadedSyncAdapter {

    String TAG = "Anunciar Service";

    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    int localCount;
    SharedPreferences prefs;

    public final String LOG_TAG = AnunciarSyncAdapter.class.getSimpleName();
    public AnunciarSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }


    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync Called.");

        prefs = getContext().getSharedPreferences("anunciar.dishant.com.anunciar"
                , MODE_PRIVATE);
        Cursor cursor = getContext().getContentResolver().query(AnnouncementTable.CONTENT_URI
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
                            getContext().getContentResolver()
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
            VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonArrayRequest);


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
                                            getContext().getContentResolver()
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
                            VolleySingleton.getInstance(getContext().getApplicationContext()).addToRequestQueue(jsonArrayRequest);
                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext())
                                    .setSmallIcon(R.drawable.ic_stat_anunciar_icon_notification)
                                    .setContentTitle("Anunciar calls!")
                                    .setContentText("You have "+ difference + " new Announcements! Click to see the announcements.");
                            Intent resultIntent = new Intent(getContext(), MainActivity.class);

                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
                            stackBuilder.addParentStack(MainActivity.class);

                            stackBuilder.addNextIntent(resultIntent);
                            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                            mBuilder.setContentIntent(resultPendingIntent);

                            NotificationManager managerCompat = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            int mId = 1;
                            managerCompat.notify(mId, mBuilder.build());

                            Toast.makeText(getContext().getApplicationContext(), difference+" new notifications!", Toast.LENGTH_SHORT).show();
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
            VolleySingleton.getInstance(getContext()).addToRequestQueue(mStringRequest);

        }


    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }
    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        AnunciarSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
