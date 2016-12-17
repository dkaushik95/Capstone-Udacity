package anunciar.dishant.com.anunciar.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import anunciar.dishant.com.anunciar.Database.Announcement;
import anunciar.dishant.com.anunciar.Internet.VolleySingleton;
import anunciar.dishant.com.anunciar.R;

public class MainActivity extends AppCompatActivity {
    SharedPreferences prefs = null;
    final String API_for_get = "https://anunciar-backend.herokuapp.com/api/v1/announcements";
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView)findViewById(R.id.announcement_list) ;
        prefs = getSharedPreferences("anunciar.dishant.com.anunciar", MODE_PRIVATE);
        Announcement.getmAnnouncements().clear();
         JsonArrayRequest mJsonObjectRequest = new JsonArrayRequest(Request.Method.GET, API_for_get,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++){
                    try{
                        JSONObject mJsonObject = response.getJSONObject(i);
                        Announcement ann = new Announcement(
                                mJsonObject.getInt("id")
                                ,mJsonObject.getString("title")
                                ,mJsonObject.getString("description")
                                ,mJsonObject.getString("deadline")
                                ,mJsonObject.getString("tags")
                                ,mJsonObject.getString("created_at")
                                ,mJsonObject.getString("updated_at"));
                        ann.addToList(ann);
                    }
                    catch (Exception e){

                    }
                }
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                mRecyclerView.setAdapter(new AnnouncementAdapter(Announcement.getmAnnouncements()));


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        VolleySingleton.getInstance(this).addToRequestQueue(mJsonObjectRequest);
    }

    public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.MyViewHolder>{
        public ArrayList<Announcement> data;
        public class MyViewHolder extends RecyclerView.ViewHolder{
            TextView title, createAt;
            public MyViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.card_title);
                createAt = (TextView) itemView.findViewById(R.id.card_created);
            }
        }

        public AnnouncementAdapter(ArrayList<Announcement> announcement) {
            data = announcement;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder vh = new MyViewHolder(((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.announcement_list_item,null));
            return vh;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Announcement a = data.get(position);
            holder.title.setText(a.getTitle());
            holder.createAt.setText(a.getCreated_at());
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }
}
