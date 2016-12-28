package anunciar.dishant.com.anunciar.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import anunciar.dishant.com.anunciar.R;

public class ShowAccount extends AppCompatActivity {
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_account);

        Transition transition = new Slide(Gravity.TOP);
        getWindow().setEnterTransition(transition);
        preferences = getSharedPreferences("anunciar.dishant.com.anunciar", MODE_PRIVATE);

        ((TextView)findViewById(R.id.account_name)).setText("Name : "+ preferences.getString("user_displayName", ""));
        ((TextView)findViewById(R.id.account_mail)).setText("Email : "+ preferences.getString("user_email", ""));
        ((TextView)findViewById(R.id.userid)).setText("UserId : "+ preferences.getString("user_ID", ""));
        (findViewById(R.id.account_name)).setContentDescription("Name : "+ preferences.getString("user_displayName", ""));
        (findViewById(R.id.account_mail)).setContentDescription("Email : "+ preferences.getString("user_email", ""));
        (findViewById(R.id.userid)).setContentDescription("UserId : "+ preferences.getString("user_ID", ""));

        Picasso.with(getApplicationContext())
                .load(preferences.getString("user_photo", ""))
                .into((ImageView)findViewById(R.id.account_photo2));

    }

    public void logOut(View view) {
        preferences.edit().putBoolean("isSignedIn", false).apply();
        Intent intent = new Intent(ShowAccount.this, SplashScreen.class);
        startActivity(intent);
    }
}
