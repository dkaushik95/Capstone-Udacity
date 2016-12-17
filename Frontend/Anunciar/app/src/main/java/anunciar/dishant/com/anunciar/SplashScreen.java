package anunciar.dishant.com.anunciar;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.widget.ImageView;

/**
 * Created by dishantkaushik on 12/16/16.
 */

public class SplashScreen extends Activity {
    SharedPreferences mSharedPreferences = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Thread timerThread = new Thread(){
            public void run(){
                try {
                    mSharedPreferences = getSharedPreferences("anunciar.dishant.com.anunciar", MODE_PRIVATE);
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
                    Intent intent;
                    if (mSharedPreferences.getBoolean("isSignedIn", false)){
                        intent = new Intent(SplashScreen.this, MainActivity.class);
                    }
                    else {
                        intent = new Intent(SplashScreen.this, Login.class);
                    }
                    startActivity(intent);
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
