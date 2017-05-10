package org.openmrs.mobile.activities.visitphoto.download;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.models.VisitPhoto;

import java.util.ArrayList;
import java.util.List;

public class DownloadVisitPhotoFragment extends ACBaseFragment<DownloadVisitPhotoContract.Presenter>
		implements DownloadVisitPhotoContract.View {

	private LinearLayoutManager layoutManager;
	private RecyclerView recyclerView;
	private DownloadVisitPhotoRecyclerViewAdapter adapter;

	public static DownloadVisitPhotoFragment newInstance() {
		return new DownloadVisitPhotoFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_download_visit_photo, container, false);
		layoutManager = new LinearLayoutManager(this.getActivity());
		recyclerView = (RecyclerView)root.findViewById(R.id.downloadPhotoRecyclerView);
		recyclerView.setLayoutManager(layoutManager);

		return root;
	}

	@Override
	public void updateVisitImages(List<String> imageUrls) {
		if(imageUrls.size() > 0) {
			adapter = new DownloadVisitPhotoRecyclerViewAdapter(this.getActivity(), imageUrls, this);
			recyclerView.setAdapter(adapter);
			RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), imageUrls.size());
			recyclerView.setLayoutManager(layoutManager);
		}
	}
}
