package org.openmrs.mobile.models;

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

	private ResponseBody responseImage;

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

	public ResponseBody getResponseImage() {
		return responseImage;
	}

	public void setResponseImage(ResponseBody responseImage) {
		this.responseImage = responseImage;
	}
}
