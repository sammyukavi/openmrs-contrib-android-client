package org.openmrs.mobile.activities.visitphoto.download;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.data.DataService;

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
		String obsUuid = items.get(position);
		if (obsUuid == null)
			return;

		view.downloadImage(obsUuid, new DataService.GetCallback<Bitmap>() {
			@Override
			public void onCompleted(Bitmap entity) {
				holder.image.setImageBitmap(entity);
				holder.image.invalidate();
			}

			@Override
			public void onError(Throwable t) {
				holder.image.setVisibility(View.GONE);
			}
		});
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
