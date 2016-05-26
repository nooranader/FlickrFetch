package com.noorhan.flickrfetch;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class PhotosSearchAPI extends AsyncTask<String, Void, ArrayList<FlickrInfo>> {

	private OnTaskComplete listener;
	private ProgressDialog loginProgress;

    public PhotosSearchAPI(OnTaskComplete listener, ProgressDialog dialog)
    {
        this.listener=listener;
        loginProgress = dialog;
    }
    
    protected void onPreExecute() 
    {
    	loginProgress.setMessage("Loading...");

    	loginProgress.setCancelable(false);

    	loginProgress.show();
    };

	@Override
	protected ArrayList<FlickrInfo> doInBackground(String... arg0)
	{
		//returned array
		ArrayList<FlickrInfo> allPhotos=new ArrayList<FlickrInfo>();

		//get tag and no of posts
		String searchWord=arg0[0];
		String noOfPosts=arg0[1];


		try
		{
			//send request and get result
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet("https://api.flickr.com/services/rest/?method=flickr.photos.search&tag_mode=any&extras=owner_name&per_page="+noOfPosts+"&nojsoncallback=1&format=json&tags="+searchWord+"&api_key=0e5cda91c2f84110cd75e8f7562b351e");

            String authorizationString = "Basic " + Base64.encodeToString(
                    ("user name tester" + ":" + "tm-sdktest").getBytes(),
                    Base64.NO_WRAP); 
            request.setHeader("Authorization", authorizationString);

            HttpResponse response = client.execute(request); //on enter browser will execute the url request

			HttpEntity entity = response.getEntity(); //get the body
            String result = EntityUtils.toString(entity); //turn it to string

			int statusCode=response.getStatusLine().getStatusCode();
			if(statusCode==200) { //this means that url has returned

				JSONObject basicObject = new JSONObject(result);

				JSONObject photosObject=basicObject.getJSONObject("photos");
				JSONArray photosArray = photosObject.getJSONArray("photo");

				//parse json
				for(int count=0;count<photosArray.length();count++)
				{
					JSONObject jsonObject=photosArray.getJSONObject(count);
					//parsing the data by key
					String id = jsonObject.getString("id");
					String owner = jsonObject.getString("owner");
					String secret = jsonObject.getString("secret");
					String server=jsonObject.getString("server");
					String farm=jsonObject.getString("farm");
					String title=jsonObject.getString("title");
					String ispublic=jsonObject.getString("ispublic");
					String isfriend=jsonObject.getString("isfriend");
					String isfamily=jsonObject.getString("isfamily");
					String ownername=jsonObject.getString("ownername");


					//new flickr object
					FlickrInfo returnPhotos=new FlickrInfo(id, owner,secret, server, farm,title, ispublic,isfriend, isfamily,ownername);
					//add object to returned array
					allPhotos.add(returnPhotos);
				}
			}
		}
		catch(Exception e)
		{
			Log.d("LoginAPI", "Exception");
			Log.d("Message", e.getMessage());
		}
		return allPhotos;
	}



	@Override
	protected void onPostExecute(ArrayList<FlickrInfo> result)
	{
		loginProgress.dismiss();
		try {
			listener.OnComplete();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		super.onPostExecute(result);
	}
}
