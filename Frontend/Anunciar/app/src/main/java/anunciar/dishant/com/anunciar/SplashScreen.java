package anunciar.dishant.com.anunciar;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Created by dishantkaushik on 12/16/16.
 */

public class SplashScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Thread timerThread = new Thread(){
            public void run(){
                try {
                    SharedPreferences sp = getSharedPreferences("anunciar.dishant.com.anunciar", MODE_PRIVATE);
                    if (sp.getBoolean("firstrun", true)){
                        sleep(3000);
                    }
                    else{
                        sleep(1000);
                    }

                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                finally {
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
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
