package org.openmrs.mobile.activities.visitphoto.download;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.openmrs.mobile.R;
import org.openmrs.mobile.models.VisitPhoto;

import java.util.List;

public class DownloadVisitPhotoRecyclerViewAdapter
		extends RecyclerView.Adapter<DownloadVisitPhotoRecyclerViewAdapter.DownloadVisitPhotoViewHolder> {

	private Activity context;
	private DownloadVisitPhotoContract.View view;
	private List<String> items;

	public DownloadVisitPhotoRecyclerViewAdapter(Activity context,
			List<String> items, DownloadVisitPhotoContract.View view) {
		this.context = context;
		this.items = items;
		this.view = view;
	}

	@Override
	public DownloadVisitPhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.visit_photo_row, parent, false);
		return new DownloadVisitPhotoViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(DownloadVisitPhotoViewHolder holder, int position) {
		String imageUrl = items.get(position);
		if (imageUrl == null)
			return;

		System.out.println("IMAGE:::::"+items.size() + ":" + position + ":" + imageUrl);
		//holder.fileCaption.setText(visitPhoto.getFileCaption());

		//Bitmap patientPhoto = BitmapFactory.decodeStream(visitPhoto.getResponseImage().byteStream());
		//holder.image.setImageBitmap(patientPhoto);
		//holder.image.invalidate();

		Picasso.Builder builder = new Picasso.Builder(holder.image.getContext());
		builder.listener(new Picasso.Listener() {
			@Override
			public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
				System.out.println("Unable:::" + exception.getMessage() + ":::" + imageUrl);
			}
		});
		builder.build()
				.load(imageUrl)
				.resize(200, 500)
				.into(holder.image);
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	class DownloadVisitPhotoViewHolder extends RecyclerView.ViewHolder {
		private LinearLayout rowLayout;
		private ImageView image;
		private TextView fileCaption;

		public DownloadVisitPhotoViewHolder(View itemView) {
			super(itemView);
			rowLayout = (LinearLayout)itemView;
			fileCaption = (TextView)itemView.findViewById(R.id.visitPhotoFileCaption);
			image = (ImageView)itemView.findViewById(R.id.visitPhoto);
		}
	}
}
