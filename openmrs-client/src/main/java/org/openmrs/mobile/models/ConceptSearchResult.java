package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.openmrs.mobile.utilities.ApplicationConstants;

public class ConceptSearchResult extends BaseOpenmrsObject {
	@SerializedName("concept")
	@Expose
	private Concept concept;
	@SerializedName("conceptName")
	@Expose
	private ConceptName conceptName;
	@SerializedName("value")
	@Expose
	private String value;

	public Concept getConcept() {
		return this.concept;
	}

	public void setConcept(Concept concept) {
		this.concept = concept;
	}

	public ConceptName getConceptName() {
		return this.conceptName;
	}

	public void setConceptName(ConceptName conceptName) {
		this.conceptName = conceptName;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		if (concept != null) {
			String conceptCode = ApplicationConstants.EMPTY_STRING;
			for (ConceptMap conceptMap : concept.getConceptMappings()) {
				if (conceptMap.getConceptReferenceTerm().getConceptSource().getName().equalsIgnoreCase(
						ApplicationConstants.ConceptSource.ICD_10_WHO)) {
					conceptCode = conceptMap.getConceptReferenceTerm().getCode();
				}
			}

			return conceptCode + " - " + conceptName.getName();
		} else {
			return "Non-Coded " + getDisplay();
		}
	}
}
