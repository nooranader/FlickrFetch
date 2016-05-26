package com.noorhan.flickrfetch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,OnTaskComplete {

    Button searchbtn;
    EditText searchEdtTxt;
    ArrayList<FlickrInfo> jsonArrayFlickr;
    PhotosSearchAPI photosSearchAPI;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //method to set toolbar color according to user preference
        SharedPreferences mypref= PreferenceManager.getDefaultSharedPreferences(this);
        String color=mypref.getString("color", "#1119DF");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(color));
        setSupportActionBar(toolbar);

        //initializing components
        searchbtn = (Button)findViewById(R.id.main_search_button);
        searchbtn.setOnClickListener(this);
        searchbtn.setBackgroundColor(Color.parseColor(color));

        searchEdtTxt= (EditText)findViewById(R.id.main_search_editText);

        jsonArrayFlickr=new ArrayList<FlickrInfo>();

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setOnClickListener(this);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);


    }


    @Override
    public void onClick(View view) {

        if (view==searchbtn)
        {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            //check user prefered number of posts
            SharedPreferences mypref= PreferenceManager.getDefaultSharedPreferences(this);
            String noOfPosts=mypref.getString("NoOfPosts", "20");

            //send tag and no of posts to api
            ProgressDialog progress= new ProgressDialog(this);
            photosSearchAPI = new PhotosSearchAPI(this,progress);
            photosSearchAPI.execute(searchEdtTxt.getText().toString(),noOfPosts);
        }
    }

    //checks if internet connection is available
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public void OnComplete() throws ExecutionException, InterruptedException { //task is complete

        if (isNetworkAvailable()==false) //no internet connection
            Toast.makeText(this,"Please check your internet connection",Toast.LENGTH_SHORT).show();
        else { //set recycler view adapter with recieved data
            jsonArrayFlickr = photosSearchAPI.get();
            if (jsonArrayFlickr.size() == 0)
                Toast.makeText(this, "Tag not found", Toast.LENGTH_SHORT).show();
            else {
                mAdapter = new MyRecyclerViewAdapterTags(jsonArrayFlickr);
                mRecyclerView.setAdapter(mAdapter);
            }
        }
    }

    //custom recycler adapter
    public class MyRecyclerViewAdapterTags extends RecyclerView.Adapter<MyRecyclerViewAdapterTags.DataObjectHolder> {
        private  String LOG_TAG = "MyRecyclerViewAdapterTags";
        private ArrayList<FlickrInfo> mDataset;
        private  MyClickListener myClickListener;

        public class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView title;
            ImageView photo;
            ImageView goToUser;

            public DataObjectHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.flickritem_title_textView);
                photo = (ImageView) itemView.findViewById(R.id.flickritem_image_imageView);
                goToUser = (ImageView) itemView.findViewById(R.id.flickritem_go_imageView2);

                Log.i(LOG_TAG, "Adding Listener");
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getPosition();
                String owner = jsonArrayFlickr.get(position).getOwner();
                String ownername = jsonArrayFlickr.get(position).getOwnername();
                Intent intent = new Intent(MainActivity.this,UserPhotosActivity.class);
                intent.putExtra("ownercode",owner);
                intent.putExtra("ownername",ownername);
                startActivity(intent);
            }
        }

        public void setOnItemClickListener(MyClickListener myClickListener) {
            this.myClickListener = myClickListener;
        }

        public MyRecyclerViewAdapterTags(ArrayList<FlickrInfo> myDataset) {
            mDataset = myDataset;
        }

        @Override
        public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.flickr_item, parent, false);

            DataObjectHolder flickrObjectHolder = new DataObjectHolder(view);
            return flickrObjectHolder;
        }

        @Override
        public void onBindViewHolder(DataObjectHolder holder, int position) {
            holder.title.setText(mDataset.get(position).getTitle());
            Context context = holder.photo.getContext();
            Picasso.with(context)
                    .load("https://farm" + mDataset.get(position).getFarm() + ".staticflickr.com/" + mDataset.get(position).getServer() + "/" + mDataset.get(position).getId() + "_" + mDataset.get(position).getSecret() + ".jpg")
                    .resize(250, 250)
                    .into(holder.photo);


        }

        public void addItem(FlickrInfo dataObj, int index) {
            mDataset.add(dataObj);
            notifyItemInserted(index);
        }

        public void deleteItem(int index) {
            mDataset.remove(index);
            notifyItemRemoved(index);
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }


    }
    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}





