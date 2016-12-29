package anunciar.dishant.com.anunciar.UI;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import anunciar.dishant.com.anunciar.Database.AnnouncementTable;
import anunciar.dishant.com.anunciar.R;
import anunciar.dishant.com.anunciar.Service.AnunciarSyncAdapter;
import anunciar.dishant.com.anunciar.Service.CircleTransform;
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;

public class MainActivity extends AppCompatActivity {
    SharedPreferences prefs = null;
    RecyclerView mRecyclerView;
    Cursor cursor;
    int localCount;
    private AdView mAdView;

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

    public void getData() {
        AnunciarSyncAdapter.syncImmediately(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("anunciar.dishant.com.anunciar"
                , MODE_PRIVATE);
        //Transistions
        Transition exitTrans = new Slide(Gravity.LEFT);
        exitTrans.setDuration(300);
        exitTrans.setInterpolator(new FastOutSlowInInterpolator());
        getWindow().setExitTransition(exitTrans);

        Transition reenterTrans = new Slide(Gravity.LEFT);
        reenterTrans.setDuration(300);
        reenterTrans.setInterpolator(new FastOutSlowInInterpolator());
        getWindow().setReenterTransition(reenterTrans);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        //Account display_pic

        try {
            Picasso.with(getApplicationContext()).load(prefs.getString("user_photo", ""))
                    .resize(200, 200)
                    .transform(new CircleTransform())
                    .centerCrop()
                    .into((ImageView) findViewById(R.id.account_photo));
        } catch (Exception e) {
            Toast.makeText(this, "Failed to get profile picture", Toast.LENGTH_SHORT).show();
        }

        //Creating Announcements
        mRecyclerView = (RecyclerView) findViewById(R.id.announcement_list);
        mRecyclerView.setItemAnimator(new SlideInDownAnimator());
        getData();

        cursor = getContentResolver().query(AnnouncementTable.CONTENT_URI,
                null,
                null,
                null,
                AnnouncementTable.FIELD_CREATED_AT + " DESC");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.setAdapter(new AnnouncementAdapter(cursor));
        RelativeLayout emptyView = (RelativeLayout) findViewById(R.id.empty_view_main);
        if (cursor.getCount() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

    }

    public void showAccount(View view) {
        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, new Pair<>(findViewById(R.id.account_photo), "accountT"));
        Intent intent = new Intent(MainActivity.this, ShowAccount.class);
        startActivity(intent, activityOptions.toBundle());

    }


    public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.MyViewHolder> {
        Cursor mCursor;

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView title, createAt;

            MyViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.card_title);
                createAt = (TextView) itemView.findViewById(R.id.card_created);
            }
        }

        AnnouncementAdapter(Cursor aCursor) {
            mCursor = aCursor;
            mCursor.moveToFirst();
        }

        @Override
        public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            View mView = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.announcement_list_item, null);
            final MyViewHolder vh = new MyViewHolder(mView);
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
                    Intent list_item = new Intent(MainActivity.this, AnnouncementDetail.class);
                    int itempos = vh.getLayoutPosition();
                    mCursor.moveToPosition(itempos);
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
            holder.title.setContentDescription(holder.title.getText());
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date past = format.parse(mCursor.getString(mCursor.getColumnIndex(AnnouncementTable.FIELD_CREATED_AT)).substring(0, 10));
                Date now = new Date();
                String days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + " days ago";
                if (TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) == 0) {
                    holder.createAt.setText(R.string.day_today);

                } else {
                    holder.createAt.setText(days);
                }
                holder.createAt.setContentDescription(holder.createAt.getText());
                notifyItemInserted(position);
            } catch (Exception j) {
                j.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }
    }
}