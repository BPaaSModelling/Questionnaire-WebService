package ch.fhnw.bpaas.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import com.google.gson.Gson;
import ch.fhnw.bpaas.model.search.SearchResult;
import ch.fhnw.bpaas.model.search.SearchResultsModel;
import ch.fhnw.bpaas.webservice.exceptions.NoResultsException;
import ch.fhnw.bpaas.webservice.ontology.NAMESPACE;
import ch.fhnw.bpaas.webservice.ontology.OntologyManager;

@Path("/search")
public class Search {

	private Gson gson = new Gson();
	private OntologyManager ontology = OntologyManager.getInstance();

	@GET
	public Response search(@QueryParam("ns") String namespace, @QueryParam("search") String search) {
		System.out.println("\n####################<start>####################");
		System.out.println("/search received values: ns " + namespace + " :: " + search);
		System.out.println("####################<end>####################");

		// split keywords spaces spaces
		String[] searchItems = search.split("\\s+");

		SearchResultsModel searchResults = null;
		try {
			if (namespace.contains(NAMESPACE.APQC.getURI())) {
				searchResults = queryAPQC(namespace, searchItems);
			} else {
				searchResults = query(namespace, searchItems);
			}
		} catch (NoResultsException e) {
			e.printStackTrace();
		}
		
		String json = gson.toJson(searchResults);
		System.out.println("\n####################<start>####################");
		System.out.println("/search genereated json: " +json);
		System.out.println("####################<end>####################");
		return Response.status(Status.OK).entity(json).build();
	}

	private SearchResultsModel queryAPQC(String namespace, String[] searchItems) throws NoResultsException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();

		queryStr.append("SELECT ?subclass ?label ?hir WHERE {");
		queryStr.append("?subclass rdfs:subClassOf* <" + namespace + "> .");
		queryStr.append("?subclass rdfs:label ?label .");
		queryStr.append("?subclass apqc:hasHierarchyID ?hir .");
		queryStr.append("FILTER(?subclass!=<" + namespace +">)");
		

		for (String param : searchItems) {
			queryStr.append("FILTER (regex(str(?label), \"" + param + "\", \"i\") || regex(str(?hir), \"" + param + "\", \"i\"))");
		}
		queryStr.append("}");
		
		ResultSet results = ontology.query(queryStr);
		SearchResultsModel sr = new SearchResultsModel();
		if (results.hasNext()) {
			while (results.hasNext()) {
				QuerySolution soln = results.next();
				sr.add(new SearchResult(soln.get("?subclass").toString(), soln.get("?hir").toString() +" " +soln.get("?label").toString()));
			}
		} else {
			throw new NoResultsException("nore more results");
		}
		return sr;
	}

	private SearchResultsModel query(String namespace, String[] searchItems) throws NoResultsException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();

		queryStr.append("SELECT ?subclass ?label WHERE {");
		queryStr.append("?subclass rdfs:subClassOf* <" + namespace + "> .");
		queryStr.append("?subclass rdfs:label ?label .");
		queryStr.append("FILTER(?subclass!=<" + namespace +">)");

		for (String param : searchItems) {
			queryStr.append("FILTER regex(str(?label), \"" + param + "\", \"i\")");
		}
		queryStr.append("}");

		ResultSet results = ontology.query(queryStr);
		SearchResultsModel sr = new SearchResultsModel();
		if (results.hasNext()) {
			while (results.hasNext()) {
				QuerySolution soln = results.next();
				sr.add(new SearchResult(soln.get("?subclass").toString(), soln.get("?label").toString()));
			}
		} else {
			throw new NoResultsException("nore more results");
		}
		return sr;
	}

}
