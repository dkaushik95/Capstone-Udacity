package anunciar.dishant.com.anunciar.UI;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import anunciar.dishant.com.anunciar.R;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Transition enterT = new Slide(Gravity.TOP);
        enterT.setInterpolator(new FastOutSlowInInterpolator());
        getWindow().setEnterTransition(enterT);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Toast toast = Toast.makeText(getApplicationContext(), connectionResult.toString(), Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(Login.this, new Pair<View, String>(findViewById(R.id.Icon), "splash1"));
            SharedPreferences sp = getSharedPreferences(getString(R.string.pref_package), MODE_PRIVATE);
            sp.edit().putBoolean(getString(R.string.is_signed_in), true).apply();
            sp.edit().putString(getString(R.string.user_displayName), acct.getDisplayName()).apply();
            sp.edit().putString(getString(R.string.user_email), acct.getEmail()).apply();
            sp.edit().putString(getString(R.string.user_ID), acct.getId()).apply();
            sp.edit().putString(getString(R.string.user_photo), acct.getPhotoUrl().toString()).apply();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Log.e(TAG, "handleSignInResult: " + result.toString());
            Toast toast = Toast.makeText(getApplicationContext(), R.string.sign_in_error, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
