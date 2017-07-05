package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;

public class ConceptMap extends BaseOpenmrsObject{

	@Expose
	private ConceptReferenceTerm conceptReferenceTerm;

	public ConceptReferenceTerm getConceptReferenceTerm() {
		return conceptReferenceTerm;
	}

	public void setConceptReferenceTerm(ConceptReferenceTerm conceptReferenceTerm) {
		this.conceptReferenceTerm = conceptReferenceTerm;
	}
}
