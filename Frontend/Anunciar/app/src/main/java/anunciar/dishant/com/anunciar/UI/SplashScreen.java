package anunciar.dishant.com.anunciar.UI;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Pair;
import android.view.View;

import anunciar.dishant.com.anunciar.R;

/**
 * Created by dishantkaushik on 12/16/16.
 */
public class SplashScreen extends Activity {
    SharedPreferences mSharedPreferences = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Transition exitTransistion = new Fade();
        exitTransistion.setDuration(1000);
        exitTransistion.setInterpolator(new FastOutSlowInInterpolator());

        getWindow().setExitTransition(exitTransistion);

        mSharedPreferences = getSharedPreferences("anunciar.dishant.com.anunciar", MODE_PRIVATE);
        Thread timerThread = new Thread(){
            public void run(){
                try {
                    //TODO change the default to false
                    if (mSharedPreferences.getBoolean("isSignedIn", false)){
                        sleep(1000);
                    }
                    else{
                        sleep(3000);
                    }
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                finally {
                    if (mSharedPreferences.getBoolean("isSignedIn", false)){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this, new Pair<View, String>((View)findViewById(R.id.imageView), "splash1"));
                                Intent intent = new Intent(SplashScreen.this, MainActivity.class);

                                startActivity(intent, activityOptions.toBundle());
                            }
                        });

                    }
                    else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this, new Pair<View, String>((View)findViewById(R.id.imageView), "splash1"));
                                Intent intent = new Intent(SplashScreen.this, Login.class);
                                startActivity(intent, activityOptions.toBundle());
                            }
                        });
                    }
                }
            }
        };
        timerThread.start();
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}