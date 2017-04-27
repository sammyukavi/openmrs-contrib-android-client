package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConceptAnswer extends BaseOpenmrsObject {

    // Fields
    private Integer conceptAnswerId;

    /**
     * The question concept that this object is answering
     */
    @SerializedName("concept")
    @Expose
    private Concept concept;

    /**
     * The answer to the question
     */
    @SerializedName("conceptClass")
    @Expose
    private Concept answerConcept;

    public Integer getConceptAnswerId() {
        return conceptAnswerId;
    }

    public void setConceptAnswerId(Integer conceptAnswerId) {
        this.conceptAnswerId = conceptAnswerId;
    }

    public Concept getConcept() {
        return concept;
    }

    public void setConcept(Concept concept) {
        this.concept = concept;
    }

    public Concept getAnswerConcept() {
        return answerConcept;
    }

    public void setAnswerConcept(Concept answerConcept) {
        this.answerConcept = answerConcept;
    }

    @Override
    public String toString() {
        return getDisplay();
    }
}
