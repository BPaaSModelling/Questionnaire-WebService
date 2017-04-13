package ch.fhnw.bpaas.webservice;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import com.google.gson.Gson;
import ch.fhnw.bpaas.model.cloudservice.CloudService;
import ch.fhnw.bpaas.model.questionnaire.QuestionnaireModel;
import ch.fhnw.bpaas.webservice.discover.CloudServiceFinder;
import ch.fhnw.bpaas.webservice.ontology.OntologyManager;

@Path("/discover")
public class Discover {
	
	private OntologyManager ontology = OntologyManager.getInstance();
	private CloudServiceFinder serviceFinder = CloudServiceFinder.getInstance();
	
	@POST
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	public Response getCloudService(String txtQuestionnaire) {
		System.out.println("\n####################<start>####################");
		System.out.println("/discover received json: " +txtQuestionnaire);
		System.out.println("####################<end>####################");
		
		Gson gson = new Gson();
		QuestionnaireModel questionnaire = gson.fromJson(txtQuestionnaire, QuestionnaireModel.class);
		
		Set<CloudService> csrs = serviceFinder.calcAndReturnServiceResultSet(ontology, questionnaire);
		
		String json = gson.toJson(csrs);
		System.out.println("\n####################<start>####################");
		System.out.println("/discover genereated json: " +json);
		System.out.println("####################<end>####################");
		return Response.status(Status.OK).entity(json).build();
	}
}
