package dev.gmu.com.staggeredtryout.app;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import dev.gmu.com.staggeredtryout.R;
import dev.gmu.com.staggeredtryout.griditems.FlickrGridItem1;
import dev.gmu.com.staggeredtryout.griditems.FlickrGridItem2;
import dev.gmu.com.staggeredtryout.griditems.FlickrGridItem3;
import dev.gmu.com.staggeredtryout.griditems.FlickrGridItem4;
import dev.gmu.com.staggeredtryout.model.FlickrGetImagesResponse;
import dev.gmu.com.staggeredtryout.model.FlickrImage;
import dev.gmu.com.staggeredtryout.model.FlickrResponsePhotos;
import dev.gmu.com.staggeredtryout.views.StaggeredGridView;
import dev.gmu.com.staggeredtryout.views.StaggeredGridViewItem;
import dev.gmu.com.staggeredtryout.volley.ClientError;
import dev.gmu.com.staggeredtryout.volley.GsonRequest;


public class MainActivity extends Activity {

	private StaggeredGridView mStaggeredView;
	private RequestQueue mVolleyQueue;
	private ProgressDialog mProgress;
	private int currPage=1;
	GsonRequest<FlickrResponsePhotos> gsonObjRequest;
	
	private RelativeLayout mListFooter;
	private boolean isLoading = false;
	
	private final String TAG_REQUEST = "MY_TAG";
	
	
	private StaggeredGridView.OnScrollListener scrollListener = new StaggeredGridView.OnScrollListener() {
		public void onTop() {
		}
		
		public void onScroll() {

		}

		public void onBottom() {
			loadMoreData();
		}
	};
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_layout);
		
		actionBarSetup();
		
		// Initialise Volley Request Queue. 
		mVolleyQueue = StaggeredDemoApplication.getRequestQueue();
		mListFooter = (RelativeLayout) findViewById(R.id.footer);
		
		mStaggeredView = (StaggeredGridView) findViewById(R.id.staggeredview);
		// Be sure before calling initialize that you haven't initialised from XML 
		//mStaggeredView.initialize(2, StaggeredGridView.Mode.FIXED);
		mStaggeredView.setOnScrollListener(scrollListener);

		showProgress();
		flickerGetImagesRequest();
	}
	
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void actionBarSetup() {
	  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	    ActionBar ab = getActionBar();
	    ab.setTitle("StaggeredGridView Demo");
	  }
	}
	
	public void onStop() {
		super.onStop();
		if(mProgress != null)
			mProgress.dismiss();
	}
	  
	private void loadMoreData() {
		
		if ( isLoading )
			return;

		mListFooter.setVisibility(View.VISIBLE);
		isLoading = true;
		flickerGetImagesRequest();
	}

	
	private void flickerGetImagesRequest() {
		
		String url = "http://api.flickr.com/services/rest";
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("api_key", "5e045abd4baba4bbcd866e1864ca9d7b");
		builder.appendQueryParameter("method", "flickr.interestingness.getList");
		builder.appendQueryParameter("format", "json");
		builder.appendQueryParameter("nojsoncallback", "1");
		builder.appendQueryParameter("per_page", "20");
		builder.appendQueryParameter("page", Integer.toString(currPage));
		
		gsonObjRequest = new GsonRequest<FlickrResponsePhotos>(Request.Method.GET, builder.toString(),
				FlickrResponsePhotos.class, null, new Response.Listener<FlickrResponsePhotos>() {
			@Override
			public void onResponse(FlickrResponsePhotos response) {
				try { 
					if(response != null) {
						//mStaggeredView.onRefreshComplete();
						parseFlickrImageResponse(response);
						currPage++;
					}
				} catch (Exception e) {
					e.printStackTrace();
					showToast("JSON parse error");
				}
				stopProgress();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// Handle your error types accordingly.For Timeout & No connection error, you can show 'retry' button.
				// For AuthFailure, you can re login with user credentials.
				// For ClientError, 400 & 401, Errors happening on client side when sending api request.
				// In this case you can check how client is forming the api and debug accordingly.
				// For ServerError 5xx, you can do retry or handle accordingly.
				if( error instanceof NetworkError) {
				} else if( error instanceof ClientError) {
				} else if( error instanceof ServerError) {
				} else if( error instanceof AuthFailureError) {
				} else if( error instanceof ParseError) {
				} else if( error instanceof NoConnectionError) {
				} else if( error instanceof TimeoutError) {
				}
				//mStaggeredView.onRefreshComplete();
				stopProgress();
				showToast(error.getMessage());
			}
		});
		gsonObjRequest.setTag(TAG_REQUEST);	
		mVolleyQueue.add(gsonObjRequest);
	}
	
	
	private void showProgress() {
		mProgress = ProgressDialog.show(this, "", "Loading...");
	}
	
	private void stopProgress() {
		isLoading = false;
		mListFooter.setVisibility(View.GONE);
		mProgress.cancel();
	}
	
	private void showToast(String msg) {
		Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
	}
	
	private void parseFlickrImageResponse(FlickrResponsePhotos response) {
		
			FlickrGetImagesResponse photos = response.getPhotos();
			for(int index = 0 ; index < photos.getPhotos().size(); index++) {
			
				FlickrImage flkrImage = photos.getPhotos().get(index);
				StaggeredGridViewItem item = null;
				int random = (int) (Math.random() * 4);
				
				if( random ==0) {
					item = new FlickrGridItem1(this, flkrImage);
					mStaggeredView.addItem(item);
					
				} else if( random == 1) {
					item = new FlickrGridItem2(this, flkrImage);
					mStaggeredView.addItem(item);
					
				} else if( random == 2) {
					item = new FlickrGridItem3(this,flkrImage);
					mStaggeredView.addItem(item);
					
				} else if( random == 3) {
					item = new FlickrGridItem4(this,flkrImage);
					mStaggeredView.addItem(item);
				}
			}
	}
	

}
