package org.openmrs.mobile.activities.visitphoto.download;

import android.graphics.Bitmap;
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
import org.openmrs.mobile.data.DataService;

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
	public void updateVisitImageUrls(List<String> urls) {
		if(urls.size() == 0)
			return;

		if (adapter == null) {
			adapter = new DownloadVisitPhotoRecyclerViewAdapter(this.getActivity(), urls, this);
		}

		RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), urls.size());
		recyclerView.setLayoutManager(layoutManager);

		recyclerView.setAdapter(adapter);
	}

	@Override
	public void downloadImage(String obsUuid, DataService.GetSingleCallback<Bitmap> callback) {
		mPresenter.downloadImage(obsUuid, callback);
	}
}
