package ch.fhnw.bpaas.model.cloudservice;

import java.util.ArrayList;

import ch.fhnw.bpaas.model.questionnaire.Answer;

public class CloudServiceElementModel {
private String propertyURI;
private String propertyLabel;
private ArrayList<Answer> answerList;
private String answerDatatype;
private ArrayList<Answer> givenAnswerList;
private String searchNamespace;
private Boolean searchOnClassesInsteadOfInstances;
private ArrayList<Answer> comparisonOperationAnswer;
private String comparisonAnswer;
private String typeOfAnswer;
private String domain;
private String annotationRelation;

public Boolean getSearchOnClassesInsteadOfInstances() {
	return searchOnClassesInsteadOfInstances;
}

public void setSearchOnClassesInsteadOfInstances(Boolean searchOnClassesInsteadOfInstances) {
	this.searchOnClassesInsteadOfInstances = searchOnClassesInsteadOfInstances;
}

public String getPropertyURI() {
	return propertyURI;
}

public void setPropertyURI(String propertyURI) {
	this.propertyURI = propertyURI;
}

public String getPropertyLabel() {
	return propertyLabel;
}

public void setPropertyLabel(String propertyLabel) {
	this.propertyLabel = propertyLabel;
}

public String getTypeOfAnswer() {
	return typeOfAnswer;
}

public void setTypeOfAnswer(String typeOfAnswer) {
	this.typeOfAnswer = typeOfAnswer;
}

public ArrayList<Answer> getAnswerList() {
	return answerList;
}

public void setAnswerList(ArrayList<Answer> answerList) {
	this.answerList = answerList;
}

public String getAnswerDatatype() {
	return answerDatatype;
}

public void setAnswerDatatype(String answerDatatype) {
	this.answerDatatype = answerDatatype;
}

public ArrayList<Answer> getGivenAnswerList() {
	return givenAnswerList;
}

public void setGivenAnswerList(ArrayList<Answer> givenAnswerList) {
	this.givenAnswerList = givenAnswerList;
}

public String getSearchNamespace() {
	return searchNamespace;
}

public void setSearchNamespace(String searchNamespace) {
	this.searchNamespace = searchNamespace;
}

public ArrayList<Answer> getComparisonOperationAnswer() {
	return comparisonOperationAnswer;
}

public void setComparisonOperationAnswer(ArrayList<Answer> comparisonOperationAnswer) {
	this.comparisonOperationAnswer = comparisonOperationAnswer;
}

public String getComparisonAnswer() {
	return comparisonAnswer;
}

public void setComparisonAnswer(String comparisonAnswer) {
	this.comparisonAnswer = comparisonAnswer;
}


public String getDomain() {
	return domain;
}

public void setDomain(String domain) {
	this.domain = domain;
}

public String getAnnotationRelation() {
	return annotationRelation;
}

public void setAnnotationRelation(String annotationRelation) {
	this.annotationRelation = annotationRelation;
}

public CloudServiceElementModel() {
	answerList = new ArrayList<Answer>();
	givenAnswerList = new ArrayList<Answer>();
	comparisonOperationAnswer = new ArrayList<Answer>();
	
	
}



}
