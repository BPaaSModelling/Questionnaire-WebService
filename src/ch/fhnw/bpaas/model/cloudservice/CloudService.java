package ch.fhnw.bpaas.model.cloudservice;

import java.util.HashSet;
import java.util.Set;

public class CloudService {

	private String id;
	private String name;
	private String apqc;
	
	private Set<String> coverage;
	private String encryptionType;
	private int maxSimultanousConnections;
	private double availabilityInPercent;
	private double dataStorage;
	private String paymentPlan;
	private String responseTimeInHrs;
	private int weeks;
	private int simultaneousUsers;
	private String targetMarket;
	private String serviceSupportHrsStart;
	private String serviceSupportHrsEnd;

	public CloudService(String id, String name) {
		setID(id);
		setName(name);
		coverage = new HashSet<String>();
	}

	private void setName(String name) {
		this.name = name;
	}

	private void setID(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public String getID() {
		return id;
	}

	public void setAPQC(String apqc) {
		this.apqc = apqc;
	}

	public void addSupportCoverage(String coverage) {
		this.coverage.add(coverage);
	}

	public void setWFDhasEncryptionType(String encryptionType) {
		this.encryptionType = encryptionType;
	}

	public void setMaxSimultanousConnections(int maxSimultanousConnections) {
		this.maxSimultanousConnections = maxSimultanousConnections;
	}

	public void setAvailabilityInPercent(double availabilityInPercent) {
		this.availabilityInPercent = availabilityInPercent;
	}

	public void setDataStorage(double dataStorage) {
		this.dataStorage = dataStorage;
	}

	public void setPaymentPlan(String paymentPlan) {
		this.paymentPlan = paymentPlan;
	}

	public void setResponseTimeInHrs(String responseTimeInHrs) {
		this.responseTimeInHrs = responseTimeInHrs;
	}

	public void setRetentionTimeInWeeks(int weeks) {
		this.weeks = weeks;
	}

	public void setSimultaneousUsers(int simultaneousUsers) {
		this.simultaneousUsers = simultaneousUsers;
	}

	public void setTargetMarket(String targetMarket) {
		this.targetMarket = targetMarket;
	}

	public void setServiceSupportHrsStart(String serviceSupportHrsStart) {
		this.serviceSupportHrsStart = serviceSupportHrsStart;
	}

	public void setServiceSupportHrsEnd(String serviceSupportHrsEnd) {
		this.serviceSupportHrsEnd = serviceSupportHrsEnd;
		
	}
}
