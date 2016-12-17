package anunciar.dishant.com.anunciar.UI;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
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
                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(SplashScreen.this, Login.class);
                                ImageView imageView = (ImageView)findViewById(R.id.imageView);
                                ActivityOptionsCompat mActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(SplashScreen.this, imageView, "splash1");
                                startActivity(intent, mActivityOptionsCompat.toBundle());
                            }
                        });

                    }
                }
            }
        };
        timerThread.start();
//        Intent intent;
//        if (mSharedPreferences.getBoolean("isSignedIn", false)){
//            intent = new Intent(SplashScreen.this, MainActivity.class);
//            startActivity(intent);
//        }
//        else {
//            intent = new Intent(SplashScreen.this, Login.class);
//            ImageView imageView = (ImageView)findViewById(R.id.imageView);
//            ActivityOptionsCompat mActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, imageView, "splash1");
//            startActivity(intent, mActivityOptionsCompat.toBundle());
//        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
