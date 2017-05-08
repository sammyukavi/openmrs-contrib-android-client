package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;

public class VisitPhoto extends BaseOpenmrsObject{

    @Expose
    private Visit visit;

    @Expose
    private Patient patient;

    @Expose
    private Provider provider;

    @Expose
    private String fileCaption;

    @Expose
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
