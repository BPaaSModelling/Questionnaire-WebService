package ch.fhnw.bpaas.webservice;

import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import com.google.gson.Gson;

import ch.fhnw.bpaas.model.cloudservice.CloudServiceModel;
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
	
	@POST
	@Path("/addcs")
	public Response getMsg(String json) {
		
		System.out.println("/insert received: " +json);
		
		Gson gson = new Gson();
		CloudServiceModel csm = gson.fromJson(json, CloudServiceModel.class);
		
		String id = UUID.randomUUID().toString();
		
		ParameterizedSparqlString querStr = new ParameterizedSparqlString();
		
		querStr.append("INSERT DATA{");
		querStr.append("bpaas:CloudService" +id  +" rdf:type bpaas:CloudService ;");
		querStr.append("rdfs:label \"" + csm.getCsName() +"\" ;");
		querStr.append("bpaas:cloudServiceHasPhysicalID \"" + csm.getCsUri() +"\" ;");
		querStr.append("bpaas:cloudServiceHasAvailabilityInPercent \"" + csm.getCsAvailability() +"\" ;");
		querStr.append("}");
		//Model modelTpl = ModelFactory.createDefaultModel();
		ontology.insertQuery(querStr);
		
		
//		String newJson = gson.toJson(jobOfferModel);
		
		return Response.status(Status.OK).entity("{}").build();

	}
}
