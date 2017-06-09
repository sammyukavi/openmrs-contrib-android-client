package org.openmrs.mobile.models;

import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.openmrs.mobile.data.db.AppDatabase;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;

@Table(database = AppDatabase.class)
public class VisitPhoto extends BaseOpenmrsObject {
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

	private Bitmap downloadedImage;

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

	public Bitmap getDownloadedImage() {
		return downloadedImage;
	}

	public void setDownloadedImage(Bitmap downloadedImage) {
		this.downloadedImage = downloadedImage;
	}

	public Observation getObservation() {
		return observation;
	}

	public void setObservation(Observation observation) {
		this.observation = observation;
	}
}
