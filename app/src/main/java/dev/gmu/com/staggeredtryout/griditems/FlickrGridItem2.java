package dev.gmu.com.staggeredtryout.griditems;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;


import dev.gmu.com.staggeredtryout.R;
import dev.gmu.com.staggeredtryout.app.PictureActivity;
import dev.gmu.com.staggeredtryout.app.StaggeredDemoApplication;
import dev.gmu.com.staggeredtryout.model.FlickrImage;
import dev.gmu.com.staggeredtryout.views.StaggeredGridViewItem;

public class FlickrGridItem2 extends StaggeredGridViewItem {

	private Context mContext;
	private ImageLoader mImageLoader;
	private FlickrImage mImage;
	private View mView;
	
	public FlickrGridItem2(Context context, FlickrImage image) {
		mImage = image;
		mContext = context;
		mImageLoader= StaggeredDemoApplication.getImageLoader();
	}
	
	@Override
	public View getView(LayoutInflater inflater, ViewGroup parent) {
		mView = inflater.inflate(R.layout.grid_item2, null);
		ImageView image = (ImageView) mView.findViewById(R.id.image);
		TextView title = (TextView) mView.findViewById(R.id.title);
        mImageLoader.get(mImage.getImageUrl(), 
				ImageLoader.getImageListener(image,R.drawable.bg_no_image, android.R.drawable.ic_dialog_alert),parent.getWidth(),0);
		
        image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, PictureActivity.class);
				intent.putExtra(PictureActivity.IMAGE_URL, mImage.getImageUrl());
				mContext.startActivity(intent);
			}
		});
		
		if( mImage.getTitle() != null)
			title.setText(mImage.getTitle());
		return mView;
	}

	@Override
	public int getViewHeight(LayoutInflater inflater, ViewGroup parent) {
		RelativeLayout item_containerFrameLayout = (RelativeLayout) mView.findViewById(R.id.container);
		item_containerFrameLayout.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		int height = item_containerFrameLayout.getMeasuredHeight();
		return height;
	}
}
