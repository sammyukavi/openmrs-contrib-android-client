package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.data.Blob;

import org.openmrs.mobile.data.db.AppDatabase;

import okhttp3.MultipartBody;

@Table(database = AppDatabase.class)
public class VisitPhoto extends BaseOpenmrsEntity {
	@Expose
	@ForeignKey(stubbedRelationship = true)
	private Visit visit;

	@Expose
	@ForeignKey(stubbedRelationship = true)
	private Patient patient;

	@Expose
	@ForeignKey(stubbedRelationship = true)
	private Provider provider;

	@Expose
	@Column
	private String fileCaption;

	@Expose
	@Column
	private String instructions;

	@Expose
	private MultipartBody.Part requestImage;

	@Column
	private Blob imageColumn;

	private byte[] downloadedImage;

	private Observation observation;

	public Visit getVisit() {
		return visit;
	}

	public void setVisit(Visit visit) {
		this.visit = visit;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public String getFileCaption() {
		return fileCaption;
	}

	public void setFileCaption(String fileCaption) {
		this.fileCaption = fileCaption;
	}

	public MultipartBody.Part getRequestImage() {
		return requestImage;
	}

	public void setRequestImage(MultipartBody.Part requestImage) {
		this.requestImage = requestImage;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public byte[] getDownloadedImage() {
		return downloadedImage;
	}

	public void setDownloadedImage(byte[] downloadedImage) {
		this.downloadedImage = downloadedImage;
		if (downloadedImage != null) {
			this.imageColumn = new Blob(downloadedImage);
		} else {
			this.imageColumn = null;
		}
	}

	public Observation getObservation() {
		return observation;
	}

	public void setObservation(Observation observation) {
		this.observation = observation;
	}

	public Blob getImageColumn() {
		return imageColumn;
	}

	public void setImageColumn(Blob imageColumn) {
		this.imageColumn = imageColumn;
	}
}
