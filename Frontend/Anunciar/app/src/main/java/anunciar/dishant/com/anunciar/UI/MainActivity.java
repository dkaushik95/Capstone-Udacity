package anunciar.dishant.com.anunciar.UI;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidessence.recyclerviewcursoradapter.RecyclerViewCursorAdapter;
import com.androidessence.recyclerviewcursoradapter.RecyclerViewCursorViewHolder;
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

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    SharedPreferences prefs = null;

    private AnnouncementAdapter mAnnouncementAdapter;

    private static final int ANNOUNCEMENT_LOADER = 0;


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
        prefs = getSharedPreferences(getString(R.string.pref_package)
                , MODE_PRIVATE);
        //Transistions
        Transition exitTrans = new Slide(Gravity.LEFT);
        exitTrans.setInterpolator(new FastOutSlowInInterpolator());
        getWindow().setExitTransition(exitTrans);

        Transition reenterTrans = new Slide(Gravity.LEFT);
        reenterTrans.setInterpolator(new FastOutSlowInInterpolator());
        getWindow().setReenterTransition(reenterTrans);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        //Account display_pic

        try {
            Picasso.with(getApplicationContext()).load(prefs.getString(getString(R.string.user_photo_pref), ""))
                    .resize(200, 200)
                    .transform(new CircleTransform())
                    .centerCrop()
                    .into((ImageView) findViewById(R.id.account_photo));
        } catch (Exception e) {
            Toast.makeText(this, R.string.failed_to_get_profile_pic, Toast.LENGTH_SHORT).show();
        }

        //Creating Announcements

        RecyclerView mAnnouncementRecyclerView = (RecyclerView) findViewById(R.id.announcement_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mAnnouncementAdapter = new AnnouncementAdapter(this);
        mAnnouncementRecyclerView.setLayoutManager(linearLayoutManager);
        mAnnouncementRecyclerView.setAdapter(mAnnouncementAdapter);
        RelativeLayout emptyView = (RelativeLayout) findViewById(R.id.empty_view_main);
        if (mAnnouncementRecyclerView.getAdapter().getItemCount() == 0) {
            mAnnouncementRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        } else {
            mAnnouncementRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        getSupportLoaderManager().initLoader(ANNOUNCEMENT_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ANNOUNCEMENT_LOADER:
                return new CursorLoader(
                        this,
                        AnnouncementTable.CONTENT_URI,
                        null,
                        null,
                        null,
                        AnnouncementTable.FIELD_CREATED_AT + " DESC"
                );
            default:
                throw new UnsupportedOperationException(getString(R.string.unknown_id_log) + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case ANNOUNCEMENT_LOADER:
                mAnnouncementAdapter.swapCursor(data);
                break;
            default:
                throw new UnsupportedOperationException(getString(R.string.unknown_id_log) + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case ANNOUNCEMENT_LOADER:
                mAnnouncementAdapter.swapCursor(null);
                break;
            default:
                throw new UnsupportedOperationException(getString(R.string.unknown_id_log) + loader.getId());
        }
    }

    public void showAccount(View view) {
        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, new Pair<>(findViewById(R.id.account_photo), "accountT"));
        Intent intent = new Intent(MainActivity.this, ShowAccount.class);
        startActivity(intent, activityOptions.toBundle());

    }

    public class AnnouncementAdapter extends RecyclerViewCursorAdapter<AnnouncementAdapter.AnnouncementViewHolder> {


        private static final int NAME_INDEX = 1;

        public AnnouncementAdapter(Context context) {
            super(context);

            setupCursorAdapter(null, 0, R.layout.announcement_list_item, false);
        }


        @Override
        public AnnouncementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View mView = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent);
            final AnnouncementViewHolder vh = new AnnouncementViewHolder(mView);

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, new Pair<View, String>(findViewById(R.id.card), "titleT"));
                    Intent list_item = new Intent(MainActivity.this, AnnouncementDetail.class);
                    int itempos = vh.getLayoutPosition();
                    mCursorAdapter.getCursor().moveToPosition(itempos);
                    list_item.putExtra(AnnouncementTable.FIELD__ID, mCursorAdapter.getCursor().getInt(mCursorAdapter.getCursor().getColumnIndex(AnnouncementTable.FIELD__ID)));
                    startActivity(list_item, options.toBundle());
                }
            });

            return vh;
        }

        @Override
        public void onBindViewHolder(AnnouncementViewHolder holder, int position) {
            mCursorAdapter.getCursor().moveToPosition(position);

            setViewHolder(holder);

            mCursorAdapter.bindView(null, mContext, mCursorAdapter.getCursor());

        }

        public class AnnouncementViewHolder extends RecyclerViewCursorViewHolder {
            public final TextView mTitle;

            public final TextView mCreated;

            public AnnouncementViewHolder(View view) {
                super(view);

                mTitle = (TextView) view.findViewById(R.id.card_title);
                mCreated = (TextView) view.findViewById(R.id.card_created);

            }

            @Override
            public void bindCursor(Cursor cursor) {
                mTitle.setText(cursor.getString(cursor.getColumnIndex(AnnouncementTable.FIELD_TITLE)));
                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date past = format.parse(cursor.getString(cursor.getColumnIndex(AnnouncementTable.FIELD_CREATED_AT)).substring(0, 10));
                    Date now = new Date();
                    String days = getString(R.string.days_ago_in_words, TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()));
                    if (TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) == 0) {
                        mCreated.setText(R.string.day_today);

                    } else {
                        mCreated.setText(days);
                    }
                } catch (Exception j) {
                    j.printStackTrace();
                }
            }
        }
    }
}