package ch.fhnw.bpaas.webservice.discover;

public class CloudServicePropery {

	private String object;
	private String predicate;
	
	public CloudServicePropery(String predicate, String object) {
		setPredicate(predicate);
		setObject(object);
		System.out.println("### CloudServiceProperty: p: " +getPredicate() +" - o: " +getObject());
	}

	private void setObject(String object) {
		if(object.contains("http://www.w3.org/2001/XMLSchema#decimal")){
			this.object = "" + parseRange(object) +"";
		}else if(object.contains("http://www.w3.org/2001/XMLSchema#time")){
			this.object = "\"" + parseRange(object) +"\"";
		}else{
			this.object = "<" + object +">";
		}
	}

	private void setPredicate(String predicate) {
		this.predicate = predicate;
	}

	public String getPredicate() {
		return predicate;
	}

	public String getObject() {
		return object;
	}
	
	public String parseRange(String string) {
		return string.split("\\^\\^")[0];
		}
}
