package ch.fhnw.bpaas.model.questionnaire;

import java.util.ArrayList;

public class QuestionnaireModel {
	
	private int lastQuestionID;
	private boolean completed;
	
	private ArrayList<QuestionnaireItem> questionItemList;
	private String currentQuestionDomain;
	private ArrayList<String> completedQuestionDomainList;
	
	public QuestionnaireModel(){
		questionItemList = new ArrayList<QuestionnaireItem>();
		completedQuestionDomainList = new ArrayList<String>();
	}
	
	public ArrayList<QuestionnaireItem> getQuestionItemList() {
		return questionItemList;
	}
	
	public void addQuestionItem(QuestionnaireItem item) {
		this.questionItemList.add(item);
	}
	
	public void setLastQuestionID(int lastQuestionID) {
		this.lastQuestionID = lastQuestionID;
	}

	public int getLastQuestionID() {
		return lastQuestionID;
	}

	public void setQuestionnaireCompleted(boolean completed) {
		this.completed = completed;
	}
	public boolean getQuestionnaireCompleted() {
		return completed;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("\n==QuestionnaireItem==\n");
		sb.append("lastQuestionNumber: \t" +getLastQuestionID()+"\n");
		sb.append("completed: \t" +getQuestionnaireCompleted()+"\n");
		sb.append("currentQuestionDomain: \t" +getCurrentQuestionDomain()+"\n");
		sb.append("****Start questionItemList****\n");
		for (QuestionnaireItem item : questionItemList) {
			sb.append(item.toString() +"\n");
		}
		sb.append("****End questionItemList****\n");
		
		sb.append("****Start completedQuestionDomainList****\n");
		for (String item : completedQuestionDomainList) {
			sb.append(item.toString() +"\n");
		}
		sb.append("****End completedQuestionDomainList****\n");
		return sb.toString();
	}

	public String getCurrentQuestionDomain() {
		return currentQuestionDomain;
	}

	public ArrayList<String> getCompletedQuestionDomainList() {
		return completedQuestionDomainList;
	}

	public void setCurrentQuestionDomain(String string) {
		this.currentQuestionDomain = string;
	}

}
