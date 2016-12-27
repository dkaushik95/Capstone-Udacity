package anunciar.dishant.com.anunciar.UI;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import anunciar.dishant.com.anunciar.API.API_Calls;
import anunciar.dishant.com.anunciar.Database.Announcement;
import anunciar.dishant.com.anunciar.Database.AnnouncementTable;
import anunciar.dishant.com.anunciar.Database.AnnouncementTableDefinition;
import anunciar.dishant.com.anunciar.Internet.VolleySingleton;
import anunciar.dishant.com.anunciar.R;
import anunciar.dishant.com.anunciar.Service.AnunciarService;
import anunciar.dishant.com.anunciar.Service.CircleTransform;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class MainActivity extends AppCompatActivity {
    SharedPreferences prefs = null;
    RecyclerView mRecyclerView;
    private String TAG = "Dishant";
    Cursor cursor;
    int localCount;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();
        System.exit(0);
    }
    public void getData(){
        Intent alarmIntent = new Intent(this, AnunciarService.AlarmReciever.class);

        PendingIntent pi = PendingIntent.getBroadcast(this,0, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pi);
//
//        Intent serviceIntent = new Intent(this, AnunciarService.class);
//        startService(serviceIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("anunciar.dishant.com.anunciar"
                , MODE_PRIVATE);
        //Transistions
        Transition exitTrans = new Slide(Gravity.LEFT);
        exitTrans.setDuration(1000);
        exitTrans.setInterpolator(new FastOutSlowInInterpolator());
        getWindow().setExitTransition(exitTrans);

        Transition reenterTrans = new Slide(Gravity.LEFT);
        reenterTrans.setDuration(1000);
        reenterTrans.setInterpolator(new FastOutSlowInInterpolator());
        getWindow().setReenterTransition(reenterTrans);

        //Account display_pic

        Picasso.with(getApplicationContext()).load(prefs.getString("user_photo", ""))
                .resize(200,200)
                .transform(new CircleTransform())
                .centerCrop()
                .into((ImageView)findViewById(R.id.account_photo));

        //Creating Announcements
        mRecyclerView = (RecyclerView) findViewById(R.id.announcement_list);
        mRecyclerView.setItemAnimator(new SlideInDownAnimator());
        getData();

        cursor = getContentResolver().query(AnnouncementTable.CONTENT_URI,
                null,
                null,
                null,
                AnnouncementTable.FIELD_CREATED_AT);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.setAdapter(new AnnouncementAdapter(cursor));

    }

    public void showAccount(View view) {
        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, new Pair<View, String>(findViewById(R.id.account_photo), "accountT"));
        Intent intent = new Intent(MainActivity.this, ShowAccount.class);
        startActivity(intent, activityOptions.toBundle());

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
        public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            View mView = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.announcement_list_item,null);
            final MyViewHolder vh = new MyViewHolder(mView);
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
                    Intent list_item = new Intent(MainActivity.this, AnnouncementDetail.class);
                    int itempos = vh.getLayoutPosition();
                    mCursor.moveToPosition(itempos);
                    Log.e(TAG, "onClick: Position: "+itempos,null);
                    Log.e(TAG, "onClick: ID IS "+ mCursor.getInt(mCursor.getColumnIndex(AnnouncementTable.FIELD_ID)),null );
                    list_item.putExtra(AnnouncementTable.FIELD_ID, mCursor.getInt(mCursor.getColumnIndex(AnnouncementTable.FIELD_ID)));

                    startActivity(list_item, options.toBundle());
                }
            });
            return vh;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            mCursor.moveToPosition(position);
            holder.title.setText(mCursor.getString(mCursor.getColumnIndex(AnnouncementTable.FIELD_TITLE)));
            //TODO fix the data format issues
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date past = format.parse(mCursor.getString(mCursor.getColumnIndex(AnnouncementTable.FIELD_CREATED_AT)).substring(0,10));
                Date now = new Date();
                String days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + " days ago";
                if (TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) == 0){
                    holder.createAt.setText("Today");
                }
                else {
                    holder.createAt.setText(days);
                }
                notifyItemInserted(position);
            }
            catch (Exception j){
                j.printStackTrace();
            }
            //holder.createAt.setText(mCursor.getString(mCursor.getColumnIndex(AnnouncementTable.FIELD_CREATED_AT)));
        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }
    }
}