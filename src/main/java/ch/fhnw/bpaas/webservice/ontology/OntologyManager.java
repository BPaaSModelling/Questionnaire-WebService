package ch.fhnw.bpaas.webservice.ontology;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.update.UpdateAction;

import ch.fhnw.bpaas.webservice.persistence.GlobalVariables;
import ch.fhnw.bpaas.webservice.persistence.RuleParser;

public final class OntologyManager {

	private static OntologyManager INSTANCE;

	private Model rdfModel;

	public static synchronized OntologyManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new OntologyManager();
		}
		return INSTANCE;
	}

	public OntologyManager() {
		rdfModel = ModelFactory.createDefaultModel();
		setNamespaces(rdfModel);
		loadOntologyiesToModel();
		applyReasoningRulesToMainModel(GlobalVariables.REASONING_RULESET);
		printModel(rdfModel, GlobalVariables.LOG_01_AFTER_REASONING);
	}

	private void applyReasoningRulesToMainModel(String ruleFile) {
		List<String> ruleSet = null;
		try {
			ruleSet = RuleParser.parseRules(this.getClass().getClassLoader().getResourceAsStream(ruleFile));
			for (String rule : ruleSet) {
				rdfModel = performConstructRule(rdfModel, new ParameterizedSparqlString(rule));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		printCurrentModel(GlobalVariables.MAIN_MODEL_EXPORT);
	}

	public Model applyReasoningRulesToTempModel(Model tempModel, ParameterizedSparqlString constructQuery) {
		// System.out.println("### applyReasoningRulesToTempModel: "
		// +constructQuery.toString());
		return performConstructRule(tempModel, constructQuery);
	}

	public void setNamespaces(Model model) {
		for (NAMESPACE ns : NAMESPACE.values()) {
			model.setNsPrefix(ns.getPrefix(), ns.getURI());
		}
	}

	private void loadOntologyiesToModel() {
		for (ONTOLOGY ontology : ONTOLOGY.values()) {
			// RDFDataMgr.read(rdfModel,
			// this.getClass().getClassLoader().getResourceAsStream(ontology.getLoadURL()),
			// Lang.TTL);
			ONTOLOGY temp = ontology;
			try {
				System.out.println("load: " + temp.getLoadURL());
				rdfModel.read(temp.getLoadURL(), temp.getFormat());
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("error loading " +temp.getLoadURL());
			}
		}
	}

	public ResultSet query(ParameterizedSparqlString queryStr) {
		addNamespacesToQuery(queryStr);
		System.out.println("***Performed query***\n" + queryStr.toString() + "***Performed query***\n");
		Query query = QueryFactory.create(queryStr.toString());
		QueryExecution qexec = QueryExecutionFactory.create(query, rdfModel);
		return qexec.execSelect();
		
	}

	private void addNamespacesToQuery(ParameterizedSparqlString queryStr) {
		for (NAMESPACE ns : NAMESPACE.values()) {
			queryStr.setNsPrefix(ns.getPrefix(), ns.getURI());
		}
	}

	public Model performConstructRule(Model model, ParameterizedSparqlString query) {
		// System.out.println("### performConstructRule: " +query.toString());
		Model temp = ModelFactory.createOntologyModel();
		addNamespacesToQuery(query);
		System.out.println("### performConstructRule: " + query.toString());
		QueryExecution qexec = QueryExecutionFactory.create(query.toString(), model);
		temp = qexec.execConstruct();
		model = model.union(temp);
		return model;
	}

	public void printModel(Model model, String fileName) {
		// RDFDataMgr.write(System.out, model, Lang.TURTLE);

		try {
			RDFDataMgr.write(new FileOutputStream(fileName), model, Lang.TURTLE);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		System.out.println("****************************************************");
	}

	public void printCurrentModel(String filename) {
		this.printModel(this.rdfModel, filename);
	}

	public ResultSet query(Model model, ParameterizedSparqlString queryStr) {
		addNamespacesToQuery(queryStr);
		System.out.println(queryStr.toString());
		Query query = QueryFactory.create(queryStr.toString());
		QueryExecution qexec = QueryExecutionFactory.create(query, model);

		return qexec.execSelect();
	}

	public void insertQuery(Model model, ParameterizedSparqlString queryStr) {
		addNamespacesToQuery(queryStr);
		System.out.println(queryStr.toString());
		UpdateAction.parseExecute(queryStr.toString(), model);
		
	}

}
