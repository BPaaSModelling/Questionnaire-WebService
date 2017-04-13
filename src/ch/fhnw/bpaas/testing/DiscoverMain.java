package ch.fhnw.bpaas.testing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Set;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.fhnw.bpaas.model.cloudservice.CloudService;
import ch.fhnw.bpaas.model.questionnaire.QuestionnaireModel;
import ch.fhnw.bpaas.webservice.discover.CloudServiceFinder;
import ch.fhnw.bpaas.webservice.ontology.OntologyManager;

public class DiscoverMain {
	
	private CloudServiceFinder serviceFinder = CloudServiceFinder.getInstance();
	private OntologyManager ontology = OntologyManager.getInstance();

	private String path ="/Users/ben/Documents/workspaces/CloudSocket/BPaaS-Questionnaire-WebService/src/ch/fhnw/bpaas/testing/qinput.json";
	
	public DiscoverMain(){
		
		try {
//			Reader reader = new InputStreamReader(this.getClass().getResourceAsStream(path), "UTF-8");
			BufferedReader br = new BufferedReader(new FileReader(path));
			Gson gson = new GsonBuilder().create();
			QuestionnaireModel q = gson.fromJson(br, QuestionnaireModel.class);
			System.out.println(q.toString());
			
			Set<CloudService> csrs = serviceFinder.calcAndReturnServiceResultSet(ontology, q);
			
			for(CloudService i : csrs){
				System.out.println(i.getName());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		
		new DiscoverMain();

	}

}
