package anunciar.dishant.com.anunciar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    SharedPreferences prefs = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("anunciar.dishant.com.anunciar", MODE_PRIVATE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!prefs.getBoolean("isSignedIn", false)){
            //TODO Add Sign In Intent
            Intent intent = new Intent(this,Login.class);
            startActivity(intent);
        }
        else if (prefs.getBoolean("isSignedIn", false)){
            //TODO Show announcements
            ((TextView)findViewById(R.id.status_text)).setText("Signed in as "+ prefs.getString("user_displayName","error"));

        }
    }
}
