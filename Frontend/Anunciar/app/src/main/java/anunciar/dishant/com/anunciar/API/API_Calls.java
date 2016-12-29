package anunciar.dishant.com.anunciar.API;

/**
 * Created by dishantkaushik on 12/18/16.
 */

public class API_Calls {
    public static final String GET_ALL_ANNOUNCMENT = "https://anunciar-backend.herokuapp.com/api/v1/announcements";
    public static final String GET_COUNT = "https://anunciar-backend.herokuapp.com/api/v1/counters/count";

    public static String GET_ANNOUNCEMENT(int key) {
        return GET_ALL_ANNOUNCMENT + "/" + key;
    }
}
