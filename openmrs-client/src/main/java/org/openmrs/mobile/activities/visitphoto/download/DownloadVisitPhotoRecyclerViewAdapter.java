package org.openmrs.mobile.activities.visitphoto.download;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.visitdetails.VisitDetailsContract;
import org.openmrs.mobile.data.DataService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadVisitPhotoRecyclerViewAdapter
		extends RecyclerView.Adapter<DownloadVisitPhotoRecyclerViewAdapter.DownloadVisitPhotoViewHolder> {

	private Activity context;
	private VisitDetailsContract.VisitDownloadPhotoView view;
	private List<String> items;
	private Map<ImageView, Bitmap> map = new HashMap<>();

	public DownloadVisitPhotoRecyclerViewAdapter(Activity context,
			List<String> items, VisitDetailsContract.VisitDownloadPhotoView view) {
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
				map.put(holder.image, entity);
			}

			@Override
			public void onError(Throwable t) {
				holder.image.setVisibility(View.GONE);
			}
		});

		holder.image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View imageView) {
				if (map.containsKey(imageView)) {
					Dialog settingsDialog = new Dialog(context);
					settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
					RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(1000, 800);
					lp.addRule(RelativeLayout.CENTER_IN_PARENT);
					ImageView expandImage = new ImageView(context);
					expandImage.setLayoutParams(lp);
					expandImage.setImageResource(R.drawable.ic_male);
					expandImage.setImageBitmap(map.get(imageView));
					settingsDialog.addContentView(expandImage, lp);
					settingsDialog.show();
				}
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
