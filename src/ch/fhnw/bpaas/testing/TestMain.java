package ch.fhnw.bpaas.testing;

import java.util.HashSet;
import java.util.Set;

import ch.fhnw.bpaas.model.cloudservice.CloudService;

public class TestMain {

	public static void main(String[] args) {
		
		test();
		Set<CloudService> csList = new HashSet<>();
		
		
		CloudService c1 = new CloudService("http://cloudsocket.eu/data#obj.24620", "WD - Open Source Billing (Invoicing System)");
		c1.addSupportCoverage("bpaas:Friday");
		c1.addSupportCoverage("bpaas:Monday");
		c1.addSupportCoverage("bpaas:Saturday");
		c1.addSupportCoverage("bpaas:Sunday");
		c1.addSupportCoverage("bpaas:Thursday");
		c1.addSupportCoverage("bpaas:Tuesday");
		c1.addSupportCoverage("bpaas:Wednesday");
		c1.setWFDhasEncryptionType("bpaas:None");
		c1.setMaxSimultanousConnections(40);
		c1.setAvailabilityInPercent(99.789);
		c1.setDataStorage(200.0);
		c1.setPaymentPlan("bpaas:TryFreeFirst");
		c1.setResponseTimeInHrs("02:00:00");
		c1.setRetentionTimeInWeeks(12);
		c1.setSimultaneousUsers(10);
		c1.setTargetMarket("bpaas:GrowingTeams");
		c1.setServiceSupportHrsStart("23:59:59");
		c1.setServiceSupportHrsEnd("00:00:00");
		csList.add(c1);
		
		CloudService c2 = new CloudService("http://cloudsocket.eu/data#obj.24614", "WD - YMENS CRM");
		c2.addSupportCoverage("bpaas:Friday");
		c2.addSupportCoverage("bpaas:Monday");
		c2.addSupportCoverage("bpaas:Thursday");
		c2.addSupportCoverage("bpaas:Tuesday");
		c2.addSupportCoverage("bpaas:Wednesday");
		c2.setWFDhasEncryptionType("bpaas:AES");
		c2.setMaxSimultanousConnections(20);
		c2.setAvailabilityInPercent(99.6);
		c2.setDataStorage(1000);
		c2.setPaymentPlan("bpaas:TryFreeFirst");
		c2.setResponseTimeInHrs("02:00:00");
		c2.setRetentionTimeInWeeks(20);
		c2.setSimultaneousUsers(30);
		c2.setTargetMarket("bpaas:GrowingTeams");
		c2.setServiceSupportHrsStart("07:00:00");
		c2.setServiceSupportHrsEnd("20:00:00");
		csList.add(c2);
		
		
		CloudService c3 = new CloudService("http://cloudsocket.eu/data#obj.24628", "WD - Ninja (Invoicing System)");
		c3.addSupportCoverage("bpaas:Friday");
		c3.addSupportCoverage("bpaas:Monday");
		c3.addSupportCoverage("bpaas:Saturday");
		c3.addSupportCoverage("bpaas:Sunday");
		c3.addSupportCoverage("bpaas:Thursday");
		c3.addSupportCoverage("bpaas:Tuesday");
		c3.addSupportCoverage("bpaas:Wednesday");
		c3.setWFDhasEncryptionType("bpaas:Blowfish");
		c3.setMaxSimultanousConnections(10);
		c3.setAvailabilityInPercent(95);
		c3.setDataStorage(200.0);
		c3.setPaymentPlan("bpaas:TryFreeFirst");
		c3.setResponseTimeInHrs("02:00:00");
		c3.setRetentionTimeInWeeks(99);
		c3.setSimultaneousUsers(34);
		c3.setTargetMarket("bpaas:GrowingTeams");
		c3.setServiceSupportHrsStart("23:59:59");
		c3.setServiceSupportHrsEnd("00:00:00");
		csList.add(c3);
		
		
		CloudService c4 = new CloudService("http://cloudsocket.eu/data#obj.24623", "WD - Simple Invoices (Invoicing System)");
		c4.addSupportCoverage("bpaas:Monday");
		c4.addSupportCoverage("bpaas:Saturday");
		c4.addSupportCoverage("bpaas:Sunday");
		c4.addSupportCoverage("bpaas:Thursday");
		c4.addSupportCoverage("bpaas:Tuesday");
		c4.setWFDhasEncryptionType("bpaas:Sha1");
		c4.setMaxSimultanousConnections(40);
		c4.setAvailabilityInPercent(50);
		c4.setDataStorage(30.0);
		c4.setPaymentPlan("bpaas:TryFreeFirst");
		c4.setResponseTimeInHrs("02:00:00");
		c4.setRetentionTimeInWeeks(12);
		c4.setSimultaneousUsers(17);
		c4.setTargetMarket("bpaas:SmallTeams");
		c4.setServiceSupportHrsStart("23:59:59");
		c4.setServiceSupportHrsEnd("00:00:00");
		csList.add(c4);
		
	}

	private static Set<String> test() {
		Set<String> a = new HashSet<String>();
		
		a.add("hallo");
		return a;
	}

}
