package ch.fhnw.bpaas.testing;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.update.UpdateAction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.fhnw.bpaas.model.questionnaire.QuestionnaireModel;
import ch.fhnw.bpaas.model.questionnaire.QuestionnaireItem;
import ch.fhnw.bpaas.webservice.ontology.NAMESPACE;
import ch.fhnw.bpaas.webservice.persistence.GlobalVariables;
import ch.fhnw.bpaas.webservice.persistence.RuleParser;

public class Discover {

	public Discover() {

		try (Reader reader = new InputStreamReader(Discover.class.getResourceAsStream("qinput.json"), "UTF-8")) {
			Gson gson = new GsonBuilder().create();
			QuestionnaireModel q = gson.fromJson(reader, QuestionnaireModel.class);

			Model tempModel = unmarshalAndGenerateRDFModel(q);
			tempModel = generateCloudServiceProfile(tempModel);
			discoverServices(tempModel);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void discoverServices(Model tempModel) {
		getProperties(tempModel);
	}

	private void getProperties(Model tempModel) {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
		queryStr.append("SELECT * WHERE{");
		queryStr.append(GlobalVariables.TEMPCLOUDSERVICE + " ?x ?y .");
		queryStr.append("}");

		performQuery(tempModel, queryStr);
		
	}

	private Model generateCloudServiceProfile(Model tempModel) {
		createTempCloudService(tempModel);
		printModel(tempModel);
		tempModel = performAndExecuteRules(tempModel);
		printModel(tempModel);
		return tempModel;
	}

	private Model performAndExecuteRules(Model tempModel) {
		System.out.println("****** * " +"performAndExecuteRules");
		List<String> ruleSet = null;
		try {
			ruleSet = RuleParser.parseRules(
					this.getClass().getClassLoader().getResourceAsStream(GlobalVariables.QUESTIONNAIRE_INTERPRETATION_RULESET));
			for (String rule : ruleSet) {
				System.out.println("****** * " +new ParameterizedSparqlString(rule).toString());
				tempModel = performConstructRule(tempModel, new ParameterizedSparqlString(rule));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tempModel;
	}

	private void createTempCloudService(Model tempModel) {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
		queryStr.append("INSERT DATA{");
		queryStr.append(GlobalVariables.TEMPCLOUDSERVICE + " rdf:type bpaas:CloudService .");
		queryStr.append("}");

		performQuery(tempModel, queryStr);
	}

	private void performQuery(Model tempModel, ParameterizedSparqlString queryStr) {
		addNamespacesToQuery(queryStr);
		UpdateAction.parseExecute(queryStr.toString(), tempModel);
	}

	private Model unmarshalAndGenerateRDFModel(QuestionnaireModel q) {
		Model tempModel = prepareAndReturnRDFModel();

		for (QuestionnaireItem item : q.getQuestionItemList()) {
			for (String answer : item.getGivenAnswerList()) {
				ParameterizedSparqlString constructQuery = generateAnswerTranslationConstructQuery(item, answer);
				System.out.println(constructQuery.toString());
				tempModel = performConstructRule(tempModel, constructQuery);
			}
		}
		return tempModel;
	}

	private Model prepareAndReturnRDFModel() {
		Model tempModel = ModelFactory.createDefaultModel();
		setNamespaces(tempModel);
		loadOntologyiesToModel(tempModel);
		printModel(tempModel);
		return tempModel;
	}

	private ParameterizedSparqlString generateAnswerTranslationConstructQuery(QuestionnaireItem item, String answer) {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
		queryStr.append("CONSTRUCT{");
		switch (item.getAnswerType()) {
		case GlobalVariables.ANSWERTYPE_VALUEINSERT:
			queryStr.append("?questionID questionnaire:questionIsAnswerdByValue \"" + answer + "\"^^xsd:string .");
			break;
		default:
			queryStr.append("?questionID questionnaire:questionIsAnswerdByAnswer <" + answer + "> .");
			break;
		}
		queryStr.append("}");

		queryStr.append("WHERE{");
		queryStr.append("?questionID rdf:type questionnaire:Question .");
		queryStr.append("FILTER (?questionID = " + item.getQuestionURI() + ").");
		queryStr.append("}");
		return queryStr;
	}

	private void printModel(Model model) {
		RDFDataMgr.write(System.out, model, Lang.TURTLE);
		System.out.println("****************************************************");
	}

	public Model performConstructRule(Model tempModel, ParameterizedSparqlString queryStr) {
		Model temp = ModelFactory.createOntologyModel();
		addNamespacesToQuery(queryStr);
		QueryExecution qexec = QueryExecutionFactory.create(queryStr.toString(), tempModel);
		temp = qexec.execConstruct();
		tempModel = tempModel.union(temp);
		return tempModel;
	}

	public void addNamespacesToQuery(ParameterizedSparqlString queryStr) {
		for (NAMESPACE ns : NAMESPACE.values()) {
			queryStr.setNsPrefix(ns.getPrefix(), ns.getURI());
		}
	}

	private void loadOntologyiesToModel(Model tempModel) {
		tempModel.read("questiondata.ttl", "TTL");
//		tempModel.read("bpaas.ttl", "TTL");
	}

	private void setNamespaces(Model tempModel) {
		for (NAMESPACE ns : NAMESPACE.values()) {
			tempModel.setNsPrefix(ns.getPrefix(), ns.getURI());
		}
	}

	public static void main(String[] args) {
		new Discover();
	}

}
