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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class UserPhotosActivity extends AppCompatActivity implements View.OnClickListener,OnTaskComplete {

    TextView welcomeTxtView;
    ArrayList<FlickrInfo> jsonArrayFlickr;
    PhotosUserAPI photosUserAPI;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_photos);

        //check theme
        SharedPreferences mypref1= PreferenceManager.getDefaultSharedPreferences(this);
        String color=mypref1.getString("color", "#1119DF");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(color));
        setSupportActionBar(toolbar);

        Intent comingFromUser= getIntent();

        //checking the owner and name
        String owner= comingFromUser.getExtras().getString("ownercode");
        String ownername= comingFromUser.getExtras().getString("ownername");

        SharedPreferences mypref= PreferenceManager.getDefaultSharedPreferences(this);
        String noOfPosts=mypref.getString("NoOfPosts", "20");

        //sending params to api
        ProgressDialog progress= new ProgressDialog(this);
        photosUserAPI= new PhotosUserAPI(this,progress);
        photosUserAPI.execute(owner,noOfPosts);

        //initializing components
        welcomeTxtView=(TextView)findViewById(R.id.user_welcome_textView);
        welcomeTxtView.setText("More photos from "+ownername);

        jsonArrayFlickr=new ArrayList<FlickrInfo>();

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view1);
        Log.i("line1","1");
        mRecyclerView.setHasFixedSize(true);
        Log.i("line1", "2");
        mRecyclerView.setOnClickListener(this);
        Log.i("line1", "3");
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);
    }

    @Override
    public void onClick(View v) {
        //empty
    }

    //checks connectivity
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //task completed, set adapter if network is fine
    @Override
    public void OnComplete() throws ExecutionException, InterruptedException {

        if (isNetworkAvailable()==false)
            Toast.makeText(this,"Please check your internet connection",Toast.LENGTH_SHORT).show();
        else {
            jsonArrayFlickr = photosUserAPI.get();
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
            holder.goToUser.setImageResource(R.drawable.blankpnj);


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
