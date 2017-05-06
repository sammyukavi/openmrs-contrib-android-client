package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;

import okhttp3.MultipartBody;

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
    private MultipartBody.Part request;

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

    public MultipartBody.Part getRequest() {
        return request;
    }

    public void setRequest(MultipartBody.Part request) {
        this.request = request;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
