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

@Path("/insert")
public class Insert {
	private Gson gson = new Gson();
	private OntologyManager ontology = OntologyManager.getInstance();

	@GET
	public Response insert(@QueryParam("object") String object) {
		System.out.println("\n####################<start>####################");
		System.out.println("/parameters for cloud service requested " + object );
		System.out.println("####################<end>####################");

		

		SearchResultsModel searchResults = null;
		try {
			
				searchResults = query(object);
			
		} catch (NoResultsException e) {
			e.printStackTrace();
		}
		
		String json = gson.toJson(searchResults);
		System.out.println("\n####################<start>####################");
		System.out.println("/search genereated json: " +json);
		System.out.println("####################<end>####################");
		return Response.status(Status.OK).entity(json).build();
	}
	
	private SearchResultsModel query(String object) throws NoResultsException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();

		queryStr.append("SELECT ?field ?label WHERE {");
		queryStr.append("?field rdf:type* <" + object + "> .");
		queryStr.append("?field rdfs:label ?label .");
		queryStr.append("}");

		ResultSet results = ontology.query(queryStr);
		SearchResultsModel sr = new SearchResultsModel();
		if (results.hasNext()) {
			while (results.hasNext()) {
				QuerySolution soln = results.next();
				sr.add(new SearchResult(soln.get("?field").toString(), soln.get("?label").toString()));
			}
		} else {
			throw new NoResultsException("nore more results");
		}
		return sr;
	}
}
