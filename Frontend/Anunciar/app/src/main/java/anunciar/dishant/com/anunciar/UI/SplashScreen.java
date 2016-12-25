package anunciar.dishant.com.anunciar.UI;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.transition.Scene;
import android.support.transition.TransitionManager;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewGroupCompat;
import android.view.ViewGroup;
import android.widget.ImageView;

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
        mSharedPreferences = getSharedPreferences("anunciar.dishant.com.anunciar", MODE_PRIVATE);
        Thread timerThread = new Thread(){
            public void run(){
                try {
                    //TODO change the default to false
                    if (mSharedPreferences.getBoolean("isSignedIn", true)){
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
                    if (mSharedPreferences.getBoolean("isSignedIn", true)){
                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(SplashScreen.this, Login.class);
                                startActivity(intent);
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