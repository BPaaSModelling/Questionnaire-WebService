package ch.fhnw.bpaas.webservice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import com.google.gson.Gson;

import ch.fhnw.bpaas.model.cloudservice.CloudServiceElementModel;
import ch.fhnw.bpaas.model.cloudservice.CloudServiceModel;
import ch.fhnw.bpaas.model.questionnaire.Answer;
import ch.fhnw.bpaas.model.search.SearchResult;
import ch.fhnw.bpaas.model.search.SearchResultsModel;
import ch.fhnw.bpaas.webservice.exceptions.NoResultsException;
import ch.fhnw.bpaas.webservice.ontology.NAMESPACE;
import ch.fhnw.bpaas.webservice.ontology.OntologyManager;
import ch.fhnw.bpaas.webservice.persistence.GlobalVariables;

@Path("/cloudservice")
public class Insert {
	private Gson gson = new Gson();
	private OntologyManager ontology = OntologyManager.getInstance();
	private boolean debug_properties = false;
	private String class_type = "questionnaire:CloudServiceElement";

	@GET
	@Path("/cselements")
	public Response getCSParameters() {
		System.out.println("\n####################<start>####################");
		System.out.println("/requested parameters to generate CloudService" );
		System.out.println("####################<end>####################");
		ArrayList<CloudServiceElementModel> result = new ArrayList<CloudServiceElementModel>();
		
		try {
				result = queryRawCsElements();
				if (debug_properties){
				for (int index = 0; index < result.size(); index++){
				System.out.println("Element "+index+": ");
				System.out.println("PropertyURI -->" + result.get(index).getPropertyURI());
				System.out.println("Propertylabel -->" + result.get(index).getPropertyLabel());
				System.out.println("AnswerList -->" + result.get(index).getAnswerList().size());
				System.out.println("AnswerDatatype -->" + result.get(index).getAnswerDatatype());
				System.out.println("GivenAnswerList -->" + result.get(index).getGivenAnswerList().size());
				System.out.println("SearchNamespace -->" + result.get(index).getSearchNamespace());
				System.out.println("ComparisonOperationAnswer -->" + result.get(index).getComparisonOperationAnswer().size());
				System.out.println("ComparisonAnswer -->" + result.get(index).getComparisonAnswer());
				System.out.println("TypeOfAnswer -->" + result.get(index).getTypeOfAnswer());
				System.out.println("Domain -->" + result.get(index).getDomain());
				System.out.println("");
				}
				}
		} catch (NoResultsException e) {
			e.printStackTrace();
		}
		
		
		String json = gson.toJson(result);
		System.out.println("\n####################<start>####################");
		System.out.println("/search genereated json: " +json);
		System.out.println("####################<end>####################");
		return Response.status(Status.OK).entity(json).build();
	}
	
	private ArrayList<CloudServiceElementModel> queryRawCsElements() throws NoResultsException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
		ArrayList<CloudServiceElementModel> allCloudServiceElements = new ArrayList<CloudServiceElementModel>();
		
		queryStr.append("SELECT ?field ?label ?qType ?searchnamespace ?searchType ?datatype ?lblDomain WHERE {");
		queryStr.append("?field rdf:type* " + class_type + " .");
		queryStr.append("?field rdfs:label ?label .");
		queryStr.append("?field rdf:type ?qType .");
		queryStr.append("?qType rdfs:subClassOf* questionnaire:AnswerType .");
		queryStr.append("OPTIONAL{");
		queryStr.append("?field questionnaire:valueInsertAnswerTypeHasDatatype ?datatype .");
		queryStr.append("}");
		queryStr.append("OPTIONAL{");
		queryStr.append("?field questionnaire:searchSelectionHasSearchNamespace ?searchnamespace .");
		queryStr.append("}");
		queryStr.append("OPTIONAL{");
		queryStr.append("?field questionnaire:cloudServiceElementHasCloudServiceDomain ?domain .");
		queryStr.append("?domain rdfs:label ?lblDomain .");
		queryStr.append("}");
		queryStr.append("OPTIONAL{");
		queryStr.append("?field questionnaire:searchSelectionOnClassesInsteadOfInstances ?searchType .");
		queryStr.append("}");
		queryStr.append("}");
		queryStr.append("ORDER BY ?domain ?field");

		QueryExecution qexec = ontology.query(queryStr);
		ResultSet results = qexec.execSelect();
		//I query comparison operators only one time and I store them in a temp array
		ArrayList<Answer> comparisonOperators;
		comparisonOperators = new ArrayList<Answer>(getComparisonOperations());
		
		if (results.hasNext()) {
			while (results.hasNext()) {
				CloudServiceElementModel tempCSElement = new CloudServiceElementModel();
				
				QuerySolution soln = results.next();
				tempCSElement.setPropertyURI(soln.get("?field").toString());
				tempCSElement.setPropertyLabel(soln.get("?label").toString());
				tempCSElement.setTypeOfAnswer(soln.get("?qType").toString());
				if (soln.get("?qType").toString().equals(GlobalVariables.ANSWERTYPE_SINGLE_SELECTION) || 
						soln.get("?qType").toString().equals(GlobalVariables.ANSWERTYPE_MULTI_SELECTION)){
					//Call for the answers
					tempCSElement.setAnswerList(new ArrayList<Answer>(getAnswerList(soln.get("?field").toString())));
				}else if (soln.get("?qType").toString().equals(GlobalVariables.ANSWERTYPE_SEARCH_SELECTION)){
					//Call for the namespace
					tempCSElement.setSearchNamespace(soln.get("?searchnamespace").toString());
					System.out.println("=====================SEARCHTYPE: " + soln.get("?searchType"));
					//Call for the searchType (Classes of Instances)
					if (soln.get("?searchType").toString() == null ||
						soln.get("?searchType").toString().equals("") ||
						soln.get("?searchType").toString().equals(GlobalVariables.BOOLEAN_FALSE_URI)){
						tempCSElement.setSearchOnClassesInsteadOfInstances(false);
					} else {
						tempCSElement.setSearchOnClassesInsteadOfInstances(true);
					}
					
				}else if (soln.get("?qType").toString().equals(GlobalVariables.ANSWERTYPE_VALUEINSERT)){
					//Call for the Datatype
					tempCSElement.setAnswerDatatype(soln.get("?datatype").toString());
					tempCSElement.setComparisonOperationAnswer(comparisonOperators);
				}
				
				tempCSElement.setDomain(soln.get("?lblDomain").toString());
				allCloudServiceElements.add(tempCSElement);
			}
		} else {
			throw new NoResultsException("nore more results");
		}
		qexec.close();
		return allCloudServiceElements;
	}
	
	private Set<Answer> getComparisonOperations() {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
		queryStr.append("SELECT ?operation ?label WHERE {");
		queryStr.append("?operation rdf:type ?type .");
		queryStr.append("?type rdfs:subClassOf* questionnaire:LogicalOperators .");
		queryStr.append("?operation rdfs:label ?label .");
		queryStr.append("}");
		
		QueryExecution qexec = ontology.query(queryStr);
		ResultSet results = qexec.execSelect();
		
		Set<Answer> comparisonOps = new HashSet<Answer>();

		while (results.hasNext()) {
			QuerySolution soln = results.next();
			comparisonOps.add(new Answer(soln.get("?operation").toString(), soln.get("?label").toString()));
		}
		qexec.close();
		return comparisonOps;
	}
	
	private Set<Answer> getAnswerList(String element_URI) {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
		queryStr.append("SELECT ?answer ?label WHERE {");
		queryStr.append("<"+element_URI+">" + " questionnaire:questionHasAnswers ?answer .");
		queryStr.append("?answer rdfs:label ?label .");
		queryStr.append("}");
		
		QueryExecution qexec = ontology.query(queryStr);
		ResultSet results = qexec.execSelect();
		
		Set<Answer> answers = new HashSet<Answer>();

		while (results.hasNext()) {
			QuerySolution soln = results.next();
			answers.add(new Answer(soln.get("?answer").toString(), soln.get("?label").toString()));
		}
		qexec.close();
		return answers;
	}
	
	@POST
	@Path("/addcs")
	public Response getMsg(String json) {
		
		System.out.println("/csModel received: " +json);
		
		Gson gson = new Gson();
		CloudServiceModel csm = gson.fromJson(json, CloudServiceModel.class);
		
		String id = UUID.randomUUID().toString();
		
		//Here i remove all the elements (attributes) that does not have a valid value
		csm.setProperties(removeEmptyElements(csm.getProperties()));
		
		//Here i set all the AnnotationRelation properties
		csm.setProperties(addAnnotationRelations(csm.getProperties()));
		
		ParameterizedSparqlString querStr = new ParameterizedSparqlString();
		
		querStr.append("INSERT DATA{");
		querStr.append("bpaas:CloudService" + "CloudService"+id  +" rdf:type bpaas:CloudService ;");
		System.out.println("    CloudService ID: "+ "CloudService"+id);
		querStr.append("rdfs:label \"" + csm.getLabel() +"\" ;");
		System.out.println("    CloudService Label: "+ csm.getLabel());
		for (int i = 0; i < csm.getProperties().size(); i++){
			if (csm.getProperties().get(i).getTypeOfAnswer().equals(GlobalVariables.ANSWERTYPE_VALUEINSERT)){
				querStr.append("<" + csm.getProperties().get(i).getAnnotationRelation() +"> \"" + csm.getProperties().get(i).getComparisonAnswer().trim() + "\"^^<" + csm.getProperties().get(i).getAnswerDatatype() + ">" +" ;");
				System.out.println("    "+csm.getProperties().get(i).getPropertyLabel() + ": " + csm.getProperties().get(i).getComparisonAnswer().trim());
			} else {
				if (csm.getProperties().get(i).getGivenAnswerList() != null){
				for (int j = 0; j < csm.getProperties().get(i).getGivenAnswerList().size(); j++){
					querStr.append("<" + csm.getProperties().get(i).getAnnotationRelation() +"> " + "<" + csm.getProperties().get(i).getGivenAnswerList().get(j).getAnswerID().trim() +"> ;");
					System.out.println("    "+csm.getProperties().get(i).getPropertyLabel() + ": " + csm.getProperties().get(i).getGivenAnswerList().get(j).getAnswerLabel().trim());
				}
				}
			}
		}
	
		querStr.append("}");
		//Model modelTpl = ModelFactory.createDefaultModel();
		ontology.insertQuery(querStr);
	
		return Response.status(Status.OK).entity("{}").build();

	}
	
	private ArrayList<CloudServiceElementModel> addAnnotationRelations(ArrayList<CloudServiceElementModel> elements){
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
		queryStr.append("SELECT ?relation ?element WHERE {");
		queryStr.append("?element rdf:type " + class_type + ".");
		queryStr.append("?element questionnaire:questionHasAnnotationRelation ?relation .");
		queryStr.append("}");
		
		QueryExecution qexec = ontology.query(queryStr);
		ResultSet results = qexec.execSelect();

		while (results.hasNext()) {
			QuerySolution soln = results.next();
			for (int i = 0; i < elements.size(); i++){
				if (elements.get(i).getPropertyURI().equals(soln.get("?element").toString())){
					elements.get(i).setAnnotationRelation(soln.get("?relation").toString());
				}
			}
		}
		qexec.close();
		return elements;
	}
	
	private ArrayList<CloudServiceElementModel> removeEmptyElements(ArrayList<CloudServiceElementModel> elements){
	ArrayList<CloudServiceElementModel> temp = new ArrayList<CloudServiceElementModel>();
	
		for (int i = 0; i < elements.size(); i++){
			if (elements.get(i).getTypeOfAnswer().equals(GlobalVariables.ANSWERTYPE_VALUEINSERT) &&
					elements.get(i).getComparisonAnswer() != null &&
					!elements.get(i).getComparisonAnswer().trim().equals("")){
				temp.add(elements.get(i));
			}
			else if (elements.get(i).getGivenAnswerList().size() != 0){
				if (elements.get(i).getGivenAnswerList().size() == 1 &&
					!elements.get(i).getGivenAnswerList().get(0).getAnswerID().equals("")){
					temp.add(elements.get(i));
					//System.out.println("Validated - "+elements.get(i).getPropertyLabel());
				}
			}
		}
		
		return temp;
	}
	
}
