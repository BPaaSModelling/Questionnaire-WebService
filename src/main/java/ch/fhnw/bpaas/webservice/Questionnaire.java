package ch.fhnw.bpaas.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import com.google.gson.Gson;
import ch.fhnw.bpaas.model.questionnaire.Answer;
import ch.fhnw.bpaas.model.questionnaire.QuestionnaireItem;
import ch.fhnw.bpaas.model.questionnaire.QuestionnaireModel;
import ch.fhnw.bpaas.webservice.exceptions.DomainSelectionException;
import ch.fhnw.bpaas.webservice.exceptions.NoDomainQuestionLeftException;
import ch.fhnw.bpaas.webservice.ontology.OntologyManager;
import ch.fhnw.bpaas.webservice.persistence.EntropyCalculation;
import ch.fhnw.bpaas.webservice.persistence.GlobalVariables;

@Path("/questionnaire")
public class Questionnaire {
	
	private Gson gson = new Gson();
	private OntologyManager ontology = OntologyManager.getInstance();
	
	/**
	 * 
	 * @param txtQuestionnaire
	 * @return
	 */
	@POST
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	public Response getQuestionnaire(String txtQuestionnaire) {
		System.out.println("\n####################<start>####################");
		System.out.println("/questionnaire received json: " +txtQuestionnaire);
		System.out.println("####################<end>####################");
		//Check questionnaire object, based on the received input
		QuestionnaireModel questionnaire = handleEmptyAndTransformInput(txtQuestionnaire);
		
		System.out.println("######### " +questionnaire.toString());
		//+"\nnextQuestionNumber: " +nextQuestionNumber
		//+"\nquestionURI: " +questionURI);
		
		//evaluate the next related question and return URI
		String questionURI = getNextQuestionURI(questionnaire);

		//extract or define the nextQuestionNumber (control element)
		int nextQuestionNumber = generateNextQuestionNumber(questionnaire);
		
		//Value entering
//		questionURI = "<http://ikm-group.ch/archiMEO/questiondata#Enter_your_availability_preference>";
		
		//Single Selection
//		questionURI = "<http://ikm-group.ch/archiMEO/questiondata#Select_your_backup_frequency>";
		
		
		if(questionURI.equals(GlobalVariables.DOMAIN_SELECTION_QUESTION) && allDomainsHaveBeenCompleted(questionnaire.getCompletedQuestionDomainList())){
			//no further question could be evaluated, set flag to true and send
			questionnaire.setQuestionnaireCompleted(true);
		}else{
			//add evaluated question to questionnaire and ship
			questionnaire.addQuestionItem(getQuestionAndFillQuestionnaireItem("<" +questionURI+ ">", nextQuestionNumber, questionnaire.getCompletedQuestionDomainList()));
			questionnaire.setLastQuestionID(nextQuestionNumber);
		}
		
		System.out.println("##### PRE SENT CHECK " +questionnaire.toString());
		
		String json = gson.toJson(questionnaire);
		System.out.println("\n####################<start>####################");
		System.out.println("/questionnaire genereated json: " +json);
		System.out.println("####################<end>####################");

		return Response.status(Status.OK).entity(json).build();
	}
	
	/**
	 * 
	 * @param completedQuestionDomainList
	 * @return
	 */
	private boolean allDomainsHaveBeenCompleted(ArrayList<String> completedQuestionDomainList) {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
		queryStr.append("SELECT (count(?answerID) AS ?result)  WHERE {");
		queryStr.append("<" +GlobalVariables.DOMAIN_SELECTION_QUESTION +"> questionnaire:questionHasDomainAnswer ?answerID .");
		queryStr.append("}");
		
		ResultSet results = ontology.query(queryStr);
		int result = -1;
		while (results.hasNext()) {
			QuerySolution soln = results.next();
			result = soln.get("?result").asLiteral().getInt();
		}
		
		System.out.println("##### CHECK for end " +result +"::" +completedQuestionDomainList.size());
		
		return result==completedQuestionDomainList.size();
	}

	/**
	 * 
	 * @param questionURI
	 * @param questionNumber
	 * @param completedDomainsList
	 * @return
	 */
	private QuestionnaireItem getQuestionAndFillQuestionnaireItem(String questionURI, int questionNumber, ArrayList<String> completedDomainsList) {
		System.out.println("RECEIVED QUESTION ID " +questionURI);
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
		
//		queryStr.append("SELECT ?answerID ?type ?questionLabel ?answerLabel ?answerDataType ?ddns WHERE {");
//		queryStr.append(questionURI +" questionnaire:questionHasAnswerType ?type .");
//		queryStr.append(questionURI +" rdfs:label ?questionLabel .");
		
		queryStr.append("SELECT * WHERE {");
		//Human readable string
		queryStr.append(questionURI +" rdfs:label ?questionLabel .");
		
		//type of question
		queryStr.append(questionURI +" rdf:type ?qType .");
		queryStr.append("?qType rdfs:subClassOf* questionnaire:AnswerType .");
		
		queryStr.append("OPTIONAL{");
		if(questionURI.replace("<", "").replaceAll(">", "").equals(GlobalVariables.DOMAIN_SELECTION_QUESTION)){
			queryStr.append(questionURI +" questionnaire:questionHasDomainAnswer ?answerID .");
			if(!completedDomainsList.isEmpty()){
				for(String filteredDomain : completedDomainsList){
					queryStr.append("FILTER(?answerID != <" + filteredDomain+">)");
				}
			}
			
		}else{
			queryStr.append(questionURI +" questionnaire:questionHasAnswers ?answerID .");
		}
		queryStr.append("?answerID rdfs:label ?answerLabel .");
		queryStr.append("}");
		
		//Value insert
		queryStr.append("OPTIONAL{");
		queryStr.append(questionURI +" questionnaire:valueInsertAnswerTypeHasDatatype ?answerDatatype .");
		queryStr.append("}");
		
		//Drilldown Answers
		queryStr.append("OPTIONAL{");
		queryStr.append(questionURI +" questionnaire:searchSelectionHasSearchNamespace ?sns .");
		queryStr.append("}");
		
		queryStr.append("}");
		ResultSet results = ontology.query(queryStr);

		QuestionnaireItem qi = new QuestionnaireItem();
		
		while (results.hasNext()) {
			QuerySolution soln = results.next();
			
			qi.setQuestionURI(questionURI);
			qi.setQuestionLabel(soln.get("?questionLabel").toString());
			qi.setAnswerType(soln.get("?qType").toString());
			qi.setQuestionID(questionNumber);
			
			if(qi.getAnswerType().equals(GlobalVariables.ANSWERTYPE_SEARCH_SELECTION)){
				qi.setAnswerSearchNamespace(soln.get("?sns").toString());
			}else if(qi.getAnswerType().equals(GlobalVariables.VALUEINSERT)){
				qi.setAnswerDatatype(soln.get("?answerDatatype").toString());
				qi.setValueInsertComparisonOperationAnswers(getComparisonOperations());
			}else{
				qi.addAnswer(new Answer(soln.get("?answerID").toString(), soln.get("?answerLabel").toString()));
			}
		}
		System.out.println("###################before sending ****" +qi.toString());
		return qi;
	}
	
	
	private Set<Answer> getComparisonOperations() {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
		queryStr.append("SELECT ?operation ?label WHERE {");
		queryStr.append("?operation rdf:type ?type .");
		queryStr.append("?type rdfs:subClassOf* questionnaire:LogicalOperators .");
		queryStr.append("?operation rdfs:label ?label .");
		queryStr.append("}");
		
		ResultSet results = ontology.query(queryStr);
		
		Set<Answer> comparisonOps = new HashSet<Answer>();

		while (results.hasNext()) {
			QuerySolution soln = results.next();
			comparisonOps.add(new Answer(soln.get("?operation").toString(), soln.get("?label").toString()));
		}
		
		return comparisonOps;
	}

	/**
	 * 
	 * @param questionnaire
	 * @return
	 */
	private String getNextQuestionURI(QuestionnaireModel questionnaire) {
		String domainURI = null;
		try {
			domainURI 	= selectDomain(questionnaire);
			String questionURI 	= selectQuestionFromDomain(domainURI, questionnaire);
			
			return questionURI;
		} catch (DomainSelectionException e) {
			System.out.println("######### domain needs to be selected " +domainURI);
			System.out.println(questionnaire.toString());
			return GlobalVariables.DOMAIN_SELECTION_QUESTION;
		} catch (NoDomainQuestionLeftException e) {
//			e.printStackTrace();
			System.out.println("######### NoDomainQuestionLeftException for: " +domainURI);
			
			//remove the domain from current
			questionnaire.setCurrentQuestionDomain("");
			
			//add domain to current one
			questionnaire.getCompletedQuestionDomainList().add(domainURI);
			
			System.out.println(questionnaire.toString());
			
			//check if domain is left
			return GlobalVariables.DOMAIN_SELECTION_QUESTION;
		}
	}
	
	/**
	 * 
	 * @param domainURI
	 * @param questionnaire
	 * @return
	 * @throws NoDomainQuestionLeftException
	 */
	public String selectQuestionFromDomain(String domainURI, QuestionnaireModel questionnaire) throws NoDomainQuestionLeftException {
		
		HashMap<String, ArrayList<String>> attributeMap = queryQuestionsFromDomainAndAttributes(domainURI, questionnaire);
		
		EntropyCalculation e = new EntropyCalculation();
		
		HashMap<String, Float> a = e.getEntropyforAttributes(attributeMap);
		e.displayAttributesWithEntropy(a);

		String question = e.getAttributeOrQuestionWithMaxEntropy(a);
//		System.out.println("\n Question/Attribute with MAX entropy ===> "+ question);
		
		return question;
	}

	private HashMap<String, ArrayList<String>> queryQuestionsFromDomainAndAttributes(String domainURI, QuestionnaireModel questionnaire) throws NoDomainQuestionLeftException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
		queryStr.append("SELECT ?questionID ?val WHERE {");
		queryStr.append("?questionID rdf:type <" +domainURI +"> .");
		for(QuestionnaireItem item : questionnaire.getQuestionItemList()){
			queryStr.append("FILTER (?questionID != " +item.getQuestionURI()  +")");
		}
		queryStr.append("?cs rdf:type bpaas:CloudService .");
		queryStr.append("OPTIONAL {");
		queryStr.append("?questionID questionnaire:questionHasAnnotationRelation ?attr .");
		queryStr.append("?cs ?attr ?val .");
		queryStr.append("}");
		queryStr.append("}");
		
		
		ResultSet results = ontology.query(queryStr);
		HashMap<String, ArrayList<String>> resultMap = new HashMap<String, ArrayList<String>>();
		if(!results.hasNext()){
			throw new NoDomainQuestionLeftException("all domain questions have been answerd; asking for new domain");
		}else{
			
			while(results.hasNext()){
				QuerySolution soln = results.next();
				String key = soln.get("?questionID").toString();
				String value = null;
				try{
					value = soln.get("?val").toString();
				}catch(NullPointerException e){
					value = "";
				}
				
				if(!resultMap.containsKey(key)){
					ArrayList<String> temp = new ArrayList<String>();
					resultMap.put(key, temp);
				}
				
				resultMap.get(key).add(value);
			}
			return resultMap;
		}
	}

	/**
	 * 
	 * @param questionnaire
	 * @return
	 * @throws DomainSelectionException
	 */
	private String selectDomain(QuestionnaireModel questionnaire) throws DomainSelectionException {
		if(!questionnaire.getCompletedQuestionDomainList().contains(GlobalVariables.FUNKTIONAL_URI)){
			return GlobalVariables.FUNKTIONAL_URI;
		}else if(questionnaire.getCurrentQuestionDomain().isEmpty()){
			if(checkDomainSelected(questionnaire)){
				return questionnaire.getCurrentQuestionDomain();
			}else{
				throw new DomainSelectionException("domain is empty and needs to be selected");
			}
		}else{
			return questionnaire.getCurrentQuestionDomain();
		}
	}

	/**
	 * 
	 * @param questionnaire
	 * @return true or false
	 */
	private boolean checkDomainSelected(QuestionnaireModel questionnaire) {
		for(QuestionnaireItem qItem : questionnaire.getQuestionItemList()){
			if(		qItem.getQuestionID() == questionnaire.getLastQuestionID() &&
					qItem.getQuestionURI().replace("<", "").replaceAll(">", "").equals(GlobalVariables.DOMAIN_SELECTION_QUESTION)){
				questionnaire.setCurrentQuestionDomain(qItem.getGivenAnswerList().get(0));
				return true;
			}
		}
		return false;
	}

	/**
	 * Extracts the last question number and increments it.
	 * If the object is empty the value is set to 0
	 * @param questionnaire
	 * @return nextQuestionNumber
	 */
	private int generateNextQuestionNumber(QuestionnaireModel questionnaire) {
		if(questionnaire.getQuestionItemList().isEmpty()){
			questionnaire.setQuestionnaireCompleted(false);
			return 0;
		}else{
			return questionnaire.getLastQuestionID()+1;
		}
	}


	/**
	 * Checks input, creates a new QuestionnaireModel if necessary or transforms the
	 * received json object into a QuestionnaireModel object
	 * @param txtQuestionnaire
	 * @return QuestionnaireModel
	 */
	private QuestionnaireModel handleEmptyAndTransformInput(String txtQuestionnaire) {
		QuestionnaireModel questionnaire = null;
		if(txtQuestionnaire.equals("[]") || txtQuestionnaire.equals("") || txtQuestionnaire.equals("{}")){
			System.out.println("generate new q object");
			questionnaire = new QuestionnaireModel();
		}else{
			System.out.println("parse q object");
			questionnaire = gson.fromJson(txtQuestionnaire, QuestionnaireModel.class);
		}
		return questionnaire;
	}

}
