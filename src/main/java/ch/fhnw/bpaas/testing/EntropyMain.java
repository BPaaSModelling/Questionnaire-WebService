package ch.fhnw.bpaas.testing;

import ch.fhnw.bpaas.model.questionnaire.QuestionnaireModel;
import ch.fhnw.bpaas.webservice.Questionnaire;
import ch.fhnw.bpaas.webservice.exceptions.NoDomainQuestionLeftException;

public class EntropyMain {
	
	Questionnaire qe = new Questionnaire();
	
	public EntropyMain(){
		QuestionnaireModel q = new QuestionnaireModel();
		
		String domainURI = "http://ikm-group.ch/archiMEO/questiondata#DataSecurity";
		
		try {
			String qURI = qe.selectQuestionFromDomain(domainURI, q);
			
			System.out.println("Found Question: " +qURI);
			
			
		} catch (NoDomainQuestionLeftException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new EntropyMain();

	}

}
