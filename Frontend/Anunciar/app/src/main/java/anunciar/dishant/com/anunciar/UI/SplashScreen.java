package anunciar.dishant.com.anunciar.UI;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Pair;

import anunciar.dishant.com.anunciar.R;
import anunciar.dishant.com.anunciar.Service.AnunciarSyncAdapter;

/**
 * Created by dishantkaushik on 12/16/16.
 */
public class SplashScreen extends Activity {
    SharedPreferences mSharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        AnunciarSyncAdapter.initializeSyncAdapter(this);

        Transition exitTransistion = new Fade();
        exitTransistion.setDuration(1000);
        exitTransistion.setInterpolator(new FastOutSlowInInterpolator());

        getWindow().setExitTransition(exitTransistion);

        mSharedPreferences = getSharedPreferences("anunciar.dishant.com.anunciar", MODE_PRIVATE);
        int time = 0;
        if (mSharedPreferences.getBoolean("isSignedIn", false)) {
            time = 1000;
        } else {
            time = 3000;
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSharedPreferences.getBoolean("isSignedIn", false)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this, new Pair<>(findViewById(R.id.imageView), "splash1"));
                            Intent intent = new Intent(SplashScreen.this, MainActivity.class);

                            startActivity(intent, activityOptions.toBundle());
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this, new Pair<>(findViewById(R.id.imageView), "splash1"));
                            Intent intent = new Intent(SplashScreen.this, Login.class);
                            startActivity(intent, activityOptions.toBundle());
                        }
                    });
                }
            }
        }, time);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}