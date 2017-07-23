package ch.fhnw.bpaas.webservice.discover;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import ch.fhnw.bpaas.model.cloudservice.CloudService;
import ch.fhnw.bpaas.model.questionnaire.QuestionnaireModel;
import ch.fhnw.bpaas.model.questionnaire.QuestionnaireItem;
import ch.fhnw.bpaas.webservice.ontology.ONTOLOGY;
import ch.fhnw.bpaas.webservice.ontology.OntologyManager;
import ch.fhnw.bpaas.webservice.persistence.GlobalVariables;
import ch.fhnw.bpaas.webservice.persistence.RuleParser;

public final class CloudServiceFinder {
	
	private static CloudServiceFinder INSTANCE;

	private QuestionnaireModel questionnaire;
	private OntologyManager ontology;
	private Model tempModel;
	
	/**
	 * Singleton
	 * @return instance
	 */
	public static synchronized CloudServiceFinder getInstance() {
        if (INSTANCE == null) {
        	INSTANCE = new CloudServiceFinder();
        }
        return INSTANCE;
    }
	
	public Set<CloudService> calcAndReturnServiceResultSet(OntologyManager ontology,
			QuestionnaireModel questionnaire) {
		this.ontology = ontology;
		this.questionnaire = questionnaire;

		Set<CloudServicePropery> properties = null;
		HashMap<String, String> propertyOperations = null;

		if (!questionnaire.getQuestionItemList().isEmpty()) {
			setupTempModelAndLoadNamespaceData();
			translateQuestionnaireDataAndAddToRDFTempModel();
			createTemporaryCloudServiceProfile();
			applyQuestionnaireInterpretationRuleset();
			properties = receiveAllPropertiesFromTempCloudServiceProfile();
			propertyOperations = receiveAllPropertyAnnotations();
		}
		ParameterizedSparqlString query = generateServiceFinderQuery(properties, propertyOperations);

		return performQueryOnRepositoryAndCreateCloudServiceResultSet(query);
	}
	//take the questions and answers that are coming from the client and put them in a temporary model in RDF 
	private ParameterizedSparqlString generateAnswerTranslationConstructQuery(QuestionnaireItem item, String answer) {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
		queryStr.append("CONSTRUCT{");
		
		switch (item.getAnswerType()) {
		case GlobalVariables.ANSWERTYPE_VALUEINSERT:
			queryStr.append("?questionID questionnaire:questionIsAnsweredByValue \"" + answer + "\"^^xsd:decimal .");
			queryStr.append("?questionID questionnaire:questionHasComparisonOperation <" + item.getComparisonAnswer() + "> .");
			break;
		default:
			queryStr.append("?questionID questionnaire:questionIsAnsweredByAnswer <" + answer + "> .");
			break;
		}
		queryStr.append("} WHERE {");
//		queryStr.append("?questionID rdf:type questionnaire:Question .");
		queryStr.append("?questionID rdf:type ?class .");
		queryStr.append("?class rdfs:subClassOf* questionnaire:Question .");
		queryStr.append("FILTER (?questionID = " + item.getQuestionURI() + ").");
		queryStr.append("}");
		return queryStr;
	}

	private Set<CloudServicePropery> receiveAllPropertiesFromTempCloudServiceProfile() {
		Set<CloudServicePropery> map = new HashSet<CloudServicePropery>();
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
		queryStr.append("SELECT * WHERE{");
		queryStr.append(GlobalVariables.TEMPCLOUDSERVICE + " ?x ?y ."); //?x is the relationship and ?y is the value
//		queryStr.append("FILTER(?x!=rdf:type) .");
		queryStr.append("}");
		ResultSet results = ontology.query(tempModel, queryStr);

		while (results.hasNext()) {
			QuerySolution soln = results.next();
			map.add(new CloudServicePropery(soln.get("?x").toString(), soln.get("?y").toString()));
		}
		System.out.println("===============NUMBER OF PROPERTIES: "+ map.size()+" ====================");
		return map;
	}
	
	private HashMap<String, String> receiveAllPropertyAnnotations() {
		HashMap<String,String> map = new HashMap<String, String>();
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
		queryStr.append("SELECT ?property ?ops WHERE {");
		queryStr.append("?property questionnaire:propertyHasComparisonOperation ?ops .");
		queryStr.append("}");
		
		ResultSet results = ontology.query(tempModel, queryStr);
		while (results.hasNext()) {
			QuerySolution soln = results.next();
			map.put(soln.get("?property").toString(), soln.get("?ops").toString());
		}
		return map;
	}

	private void applyQuestionnaireInterpretationRuleset() {
		List<String> ruleSet = null;
		
		try {
			ruleSet = RuleParser.parseRules(
					this.getClass().getClassLoader().getResourceAsStream(GlobalVariables.QUESTIONNAIRE_INTERPRETATION_RULESET));
			for (String rule : ruleSet) {
				tempModel = ontology.applyReasoningRulesToTempModel(tempModel, new ParameterizedSparqlString(rule));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (ontology.isLocalOntology()){
			ontology.printModel(tempModel, GlobalVariables.LOG_05_TEMPMODEL);
		}
		
	}

	private void createTemporaryCloudServiceProfile() {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
		queryStr.append("INSERT DATA{");
		queryStr.append(GlobalVariables.TEMPCLOUDSERVICE + " rdf:type bpaas:CloudService .");
		queryStr.append("}");
		
		ontology.insertQuery(tempModel, queryStr);
	}
	
	private void translateQuestionnaireDataAndAddToRDFTempModel() {
		for (QuestionnaireItem item : questionnaire.getQuestionItemList()) {
			for (String answer : item.getGivenAnswerList()) {
				ParameterizedSparqlString constructQuery = generateAnswerTranslationConstructQuery(item, answer);
				tempModel = ontology.applyReasoningRulesToTempModel(tempModel, constructQuery);
			}
		}
		if (ontology.isLocalOntology()){
			ontology.printModel(tempModel, GlobalVariables.LOG_03_TEMPMODEL_02);
		}
	}

	private void setupTempModelAndLoadNamespaceData() {
		tempModel = ModelFactory.createDefaultModel();
		ontology.setNamespaces(tempModel);
		loadNamespaceContentsToModel();
		if (ontology.isLocalOntology()){
			ontology.printModel(tempModel, GlobalVariables.LOG_02_TEMPMODEL_01);
		}
	}

	private void loadNamespaceContentsToModel() {
		if (ontology.isLocalOntology()){
			System.out.println("loading: " +ONTOLOGY.questiondata.getLoadURL());
			tempModel.read(ONTOLOGY.questiondata.getLoadURL(), ONTOLOGY.questiondata.getFormat());
			
			System.out.println("loading: " +ONTOLOGY.BPAAS.getLoadURL());
			tempModel.read(ONTOLOGY.BPAAS.getLoadURL(), ONTOLOGY.BPAAS.getFormat());
			
			System.out.println("loading: " +ONTOLOGY.questionnaire.getLoadURL());
			tempModel.read(ONTOLOGY.questionnaire.getLoadURL(), ONTOLOGY.questionnaire.getFormat());	
		}
		else {
			tempModel.read(OntologyManager.getREADENDPOINT());
		}

	}

	private Set<CloudService> performQueryOnRepositoryAndCreateCloudServiceResultSet(
			ParameterizedSparqlString query) {
		ResultSet results = ontology.query(query);

		Set<CloudService> csrs = new HashSet<CloudService>();

		while (results.hasNext()) {
			QuerySolution soln = results.next();
			csrs.add(new CloudService(soln.get("?cserviceID").toString(), soln.get("?label").toString()));
		}
		return csrs;
	}
	
	
	private ParameterizedSparqlString generateServiceFinderQuery(Set<CloudServicePropery> properties, HashMap<String, String> propertyOperations) {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
		queryStr.append("SELECT ?cserviceID ?label WHERE{");
		queryStr.append("?cserviceID rdf:type bpaas:CloudService .");
		queryStr.append("?cserviceID rdfs:label ?label .");
		if(properties != null && !properties.isEmpty()){
			for(CloudServicePropery prop : properties){
				if(propertyOperations.containsKey(prop.getPredicate())){
					queryStr.append(getPropertyFilter(prop, propertyOperations.get(prop.getPredicate())));
				}else{
					queryStr.append("?cserviceID <" +prop.getPredicate() +"> " +prop.getObject() +" .");
				}
			}
		}
		queryStr.append("}");
		return queryStr;
	}

	private String getPropertyFilter(CloudServicePropery prop, String operation) {
		String varName = UUID.randomUUID().toString().replace("-", "");
		String operator = null;
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		System.out.println("prop" +prop.getPredicate() +" :: " +prop.getObject());
		System.out.println("operation" +operation);
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
		queryStr.append("?cserviceID <" +prop.getPredicate() +"> ?" +varName +" .");
		queryStr.append("FILTER(");
		switch(operation){
		case GlobalVariables.OPERATION_GREATEREQUALSTHAN:
			operator = ">=";
			break;
		case GlobalVariables.OPERATION_GREATERTHAN:
			operator = ">";
			break;
		case GlobalVariables.OPERATION_LESSEQUALSTHAN:
			operator = "<=";
			break;
		case GlobalVariables.OPERATION_LESSTHAN:
			operator = "<";
			break;
		case GlobalVariables.OPERATION_NOTEQUAL:
			operator = "!=";
			break;
		default:
			operator = "=";
			break;
		}
		queryStr.append(" ?" +varName +" " +operator  +" " +prop.getObject());
		queryStr.append(")");
		return queryStr.toString();
	}
}