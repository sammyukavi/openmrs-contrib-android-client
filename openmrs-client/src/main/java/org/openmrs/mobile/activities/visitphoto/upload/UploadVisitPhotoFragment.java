package org.openmrs.mobile.activities.visitphoto.upload;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ViewUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class UploadVisitPhotoFragment extends ACBaseFragment<UploadVisitPhotoContract.Presenter>
		implements UploadVisitPhotoContract.View {

	private final static int IMAGE_REQUEST = 1;
	private ImageView visitImageView;
	private FloatingActionButton capturePhoto;
	private Bitmap visitPhoto = null;
	private Button uploadVisitPhotoButton;
	private File output;
	private EditText fileCaption;

	public static Bitmap rotateImage(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}

	public static UploadVisitPhotoFragment newInstance() {
		return new UploadVisitPhotoFragment();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_upload_visit_photo, container, false);
		capturePhoto = (FloatingActionButton)root.findViewById(R.id.capture_photo);
		visitImageView = (ImageView)root.findViewById(R.id.visitPhoto);
		uploadVisitPhotoButton = (Button)root.findViewById(R.id.uploadVisitPhoto);
		fileCaption = (EditText)root.findViewById(R.id.fileCaption);
		addListeners();

		return root;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		UploadVisitPhotoFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
	}

	private void addListeners() {
		capturePhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				UploadVisitPhotoFragmentPermissionsDispatcher.capturePhotoWithCheck(UploadVisitPhotoFragment.this);
			}
		});

		visitImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (output != null) {
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setDataAndType(Uri.fromFile(output), "image/jpeg");
					startActivity(i);
				}
			}
		});

		uploadVisitPhotoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (visitPhoto != null) {
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					visitPhoto.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

					MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("file",
							output.getName(), RequestBody.create(MediaType.parse("image/jpeg"), output));

					mPresenter.getVisitPhoto().setRequestImage(uploadFile);
					mPresenter.getVisitPhoto().setFileCaption(
							StringUtils.notEmpty(
									ViewUtils.getInput(fileCaption)) ?
									ViewUtils.getInput(fileCaption) :
									getString(R.string.default_file_caption_message));
					mPresenter.uploadImage();
				}
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == IMAGE_REQUEST) {
			if (resultCode == Activity.RESULT_OK) {
				visitPhoto = getPortraitImage(output.getPath());
				Bitmap bitmap =
						ThumbnailUtils.extractThumbnail(visitPhoto, visitImageView.getWidth(), visitImageView.getHeight());
				visitImageView.setImageBitmap(bitmap);
				visitImageView.invalidate();
			} else {
				output = null;
			}
		}
	}

	@Override
	public void showPatientDashboard(String patientUuid) {
		Intent intent = new Intent(getContext(), PatientDashboardActivity.class);
		intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
		getContext().startActivity(intent);
	}

	@NeedsPermission({ Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE })
	public void capturePhoto() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
			File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
			output = new File(dir, getUniqueImageFileName());
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
			startActivityForResult(takePictureIntent, IMAGE_REQUEST);
		}
	}

	@OnShowRationale({ Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE })
	public void showRationaleForCamera(final PermissionRequest request) {
		new AlertDialog.Builder(getActivity())
				.setMessage(R.string.permission_camera_rationale)
				.setPositiveButton(R.string.button_allow, (dialog, which) -> request.proceed())
				.setNegativeButton(R.string.button_deny, (dialog, button) -> request.cancel())
				.show();
	}

	@OnPermissionDenied({ Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE })
	public void showDeniedForCamera() {
		createSnackbarLong(R.string.permission_camera_denied)
				.show();
	}

	@OnNeverAskAgain({ Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE })
	public void showNeverAskForCamera() {
		createSnackbarLong(R.string.permission_camera_neverask)
				.show();
	}

	private String getUniqueImageFileName() {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		return timeStamp + "_" + ".jpg";
	}

	private Snackbar createSnackbarLong(int stringId) {
		//Snackbar snackbar = Snackbar.make(linearLayout, stringId, Snackbar.LENGTH_LONG);
		//View sbView = snackbar.getView();
		//TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
		//textView.setTextColor(Color.WHITE);
		//return snackbar;
		return null;
	}

	private Bitmap getPortraitImage(String imagePath) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;
		Bitmap photo = BitmapFactory.decodeFile(output.getPath(), options);
		float rotateAngle;
		try {
			ExifInterface exifInterface = new ExifInterface(imagePath);
			int orientation =
					exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_270:
					rotateAngle = 270;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					rotateAngle = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_90:
					rotateAngle = 90;
					break;
				default:
					rotateAngle = 0;
					break;
			}
			return rotateImage(photo, rotateAngle);
		} catch (IOException e) {
			return photo;
		}
	}
}
