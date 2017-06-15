package org.openmrs.mobile.models;

import java.io.Serializable;

public class ConceptSearchResult extends BaseOpenmrsObject{

	private Concept concept;
	private ConceptName conceptName;
	private String word;

	public ConceptSearchResult() {
	}

	public ConceptSearchResult(String word, Concept concept, ConceptName conceptName) {
		this.concept = concept;
		this.conceptName = conceptName;
		this.word = word;
	}

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

	public String getWord() {
		return this.word;
	}

	public void setWord(String word) {
		this.word = word;
	}
}
