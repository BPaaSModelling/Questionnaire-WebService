package ch.fhnw.bpaas.model.questionnaire;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
public class QuestionnaireItem {

	private String questionLabel;
	private String questionURI;
	private ArrayList<Answer> answerList;
	private String answerType;
	private ArrayList<String> givenAnswerList;
	private String answerDatatype;
	private int questionID;
	private String searchNamespace;
	private Boolean searchOnClassesInsteadOfInstances;
	private Set<Answer> comparisonOperationAnswers;
	private String comparisonAnswer;
	
	public QuestionnaireItem(){
		answerList = new ArrayList<Answer>();
		givenAnswerList = new ArrayList<String>();
		comparisonOperationAnswers = new HashSet<Answer>();
	}

	public void setQuestionLabel(String questionLabel) {
		this.questionLabel = questionLabel;
	}

	public void setQuestionURI(String questionURI) {
		this.questionURI = questionURI;
	}

	public void addAnswer(Answer answer) {
		answerList.add(answer);
	}

	public void setAnswerType(String answerType) {
		this.answerType = answerType;
	}
	
	public String getQuestionURI() {
		return questionURI;
	}
	
	public String getQuestionLabel() {
		return questionLabel;
	}
	
	public String getAnswerType() {
		return answerType;
	}
	
	public ArrayList<Answer> getAnswerList() {
		return answerList;
	}
	
	public void setAnswerList(ArrayList<Answer> answerList) {
		this.answerList = answerList;
	}
	
	
	public Boolean getSearchOnClassesInsteadOfInstances() {
		return searchOnClassesInsteadOfInstances;
	}

	public void setSearchOnClassesInsteadOfInstances(Boolean searchOnClassesInsteadOfInstances) {
		this.searchOnClassesInsteadOfInstances = searchOnClassesInsteadOfInstances;
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("==QuestionnaireItem==\n");
		sb.append("QuestionURI: \t" +getQuestionURI()+"\n");
		sb.append("questionID: \t" +getQuestionID()+"\n");
		sb.append("QuestionLabel: \t" +getQuestionLabel()+"\n");
		sb.append("AnswerType: \t" +getAnswerType()+"\n");
		sb.append("AnswerDataType: \t" +getAnswerDatatype()+"\n");
		sb.append("AnswerDataType: \t" +getAnswerDrilldownNamespace() +"\n");
		sb.append("comparisonOperationsAnswers: \t" +comparisonOperationAnswers.toString() +"\n");
		sb.append("comparisonAnswer: \t" +comparisonAnswer);
		
		for (Answer answerItem : answerList) {
			sb.append(answerItem.toString());
		}
//		for (String gAnswer : givenAnswerList) {
			sb.append("given answers: " +givenAnswerList.toString() +"\n");
//		}
		return sb.toString();
	}

	public void addGivenAnswer(String answerID) {
		System.out.println("added " +answerID);
		givenAnswerList.add(answerID);
		System.out.println("new " +givenAnswerList.toString());
	}

	public void removeGivenAnswer(String answerID) {
		System.out.println("removed " +answerID);
		givenAnswerList.remove(answerID);
		System.out.println("new " +givenAnswerList.toString());
	}

	public void addSingleSelectAnswer(String answerID) {
		givenAnswerList.clear();
		addGivenAnswer(answerID);
	}

	public String getAnswerDatatype() {
		return answerDatatype;
	}
	
	public void setAnswerDatatype(String answerDatatype) {
		this.answerDatatype = answerDatatype;
	}

	public ArrayList<String> getGivenAnswerList() {
		return givenAnswerList;
	}

	public void setQuestionID(int questionNumber) {
		this.questionID = questionNumber;
	}
	
	public int getQuestionID() {
		return questionID;
	}

	public void setAnswerSearchNamespace(String searchNamespace) {
		this.searchNamespace = searchNamespace;
	}
	
	private String getAnswerDrilldownNamespace() {
		return searchNamespace;
	}

	public void setValueInsertComparisonOperationAnswers(Set<Answer> comparisonOperations) {
		this.comparisonOperationAnswers = comparisonOperations;
	}

	public String getComparisonAnswer() {
		return comparisonAnswer;
	}
}
