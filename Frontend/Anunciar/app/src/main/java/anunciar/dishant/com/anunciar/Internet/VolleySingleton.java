package anunciar.dishant.com.anunciar.Internet;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by dishantkaushik on 12/17/16.
 */


public class VolleySingleton {

    private static VolleySingleton instance;
    private RequestQueue requestQueue;


    private VolleySingleton(Context context) {
        requestQueue = Volley.newRequestQueue(context);

    }


    public static VolleySingleton getInstance(Context context) {
        if (instance == null) {
            instance = new VolleySingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req) {

        getRequestQueue().add(req);
    }

}