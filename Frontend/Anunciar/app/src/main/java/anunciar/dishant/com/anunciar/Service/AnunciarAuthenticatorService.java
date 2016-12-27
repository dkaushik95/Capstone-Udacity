package anunciar.dishant.com.anunciar.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by dishantkaushik on 12/28/16.
 */

public class AnunciarAuthenticatorService extends Service {
    private AnunciarAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new AnunciarAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
