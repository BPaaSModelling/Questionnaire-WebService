package ch.fhnw.bpaas.model.cloudservice;

import java.util.ArrayList;

public class CloudServiceModel {
private String URI;
private String label;
private ArrayList<CloudServiceElementModel> properties;
public String getURI() {
	return URI;
}
public void setURI(String URI) {
	this.URI = URI;
}
public String getLabel() {
	return label;
}
public void setLabel(String label) {
	this.label = label;
}
public ArrayList<CloudServiceElementModel> getProperties() {
	return properties;
}
public void setProperties(ArrayList<CloudServiceElementModel> properties) {
	this.properties = properties;
}
public CloudServiceModel() {
	URI = "";
	label = "";
	properties = new ArrayList<CloudServiceElementModel>();
}

}
