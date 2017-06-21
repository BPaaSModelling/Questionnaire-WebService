package ch.fhnw.bpaas.model.cloudservice;

import java.util.ArrayList;

import ch.fhnw.bpaas.model.questionnaire.Answer;

public class CloudServiceModel {
private String csUri;
private String csName;
private String csAvailability;
public String getCsUri() {
	return csUri;
}
public void setCsUri(String csUri) {
	this.csUri = csUri;
}
public String getCsName() {
	return csName;
}
public void setCsName(String csName) {
	this.csName = csName;
}
public String getCsAvailability() {
	return csAvailability;
}
public void setCsAvailability(String csAvailability) {
	this.csAvailability = csAvailability;
}
public CloudServiceModel(String csUri, String csName, String csAvailability) {
	super();
	this.csUri = csUri;
	this.csName = csName;
	this.csAvailability = csAvailability;
}

}
