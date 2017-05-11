package ch.fhnw.bpaas.webservice.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author prajakta
 * This class calculates entropy for input cloud service attributes
 *
 */
public class EntropyCalculation {

	private HashMap <String,Float> entropyAttributes;

	/**
	 * Constructor for initializing the variables
	 */
	public EntropyCalculation() {
		// TODO Auto-generated constructor stub
		//allocation of java beans
		entropyAttributes = new HashMap<String,Float>();
	}
	/**
	 * This function sorts the values HashMap in descending order
	 * @param map
	 * @return
	 */
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static HashMap<String,Float> sortByValues(Map <String,Float> map) {
		
       HashMap sortedHashMap = new LinkedHashMap();

		try {
			List list = new LinkedList(map.entrySet());
		       // Defined Custom Comparator here
		       Collections.sort(list, new Comparator() {
		            public int compare(Object o1, Object o2) {
		               return ((Comparable) ((Map.Entry) (o2)).getValue())
		                  .compareTo(((Map.Entry) (o1)).getValue());
		            }
		       });

		       // Here I am copying the sorted list in HashMap
		       // using LinkedHashMap to preserve the insertion order
		       for (Iterator it = list.iterator(); it.hasNext();) {
		              Map.Entry entry = (Map.Entry) it.next();
		              sortedHashMap.put(entry.getKey(), entry.getValue());
		       } 

		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
		 
	       return sortedHashMap;
	 }

	/**
	 * This method calculates the entropy for the input attributes
	 *  @return
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @author Prajakta
	 * @return 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public HashMap<String, Float> getEntropyforAttributes(HashMap<String, ArrayList<String>> attributeMap){

		Object[] keyValues = attributeMap.keySet().toArray();
		try {
			for (int i = 0; i < attributeMap.size(); i++) {
				Map <String, Integer> probMap = new HashMap<String, Integer>() ;
				ArrayList<String> attributeValues = attributeMap.get((keyValues[i].toString())); 
				for (int j = 0; j < attributeValues.size(); j++) {
					int availabilityCount = 1;
					if (!probMap.containsKey(attributeValues.get(j))) {
						for (int k = 0; k < attributeValues.size(); k++) {
							if(j!=k){
								if(attributeValues.get(j).equals(attributeValues.get(k))){
									availabilityCount++;
								}
							}

						}
						probMap.put(attributeValues.get(j), availabilityCount);
						//System.out.println("\n Availability "+attributeValues.get(j) + " Count "+ availabilityCount);
					}
				}
				entropyAttributes.put(keyValues[i].toString(),calculateEntropy(attributeValues.size(),probMap));
			}
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
		return entropyAttributes;
	}


	/**
	 * This function calculates the entropy for given attributes
	 * @param total
	 * @return
	 */
	public float calculateEntropy(int total, Map < String, Integer> probMap){
		float entropy  = 0 ;

		try {

			Iterator<Entry<String, Integer>> iterator = probMap.entrySet().iterator();
			while (iterator.hasNext()) 
			{
				Entry<String, Integer> mapEntry = iterator.next();
				float prob = ((float) mapEntry.getValue() / total) ;
				float value = - (float)  (prob * Math.log(prob) / Math.log(2.0)) ;
				entropy = (entropy + value) ;
				//System.out.println(") Individual pi*log(pi) -> " + value);

			}
			//System.out.println("Value of entropy for given set of classifiers ->" + entropy);


		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
		return entropy;
	}
	
	/**
	 * Gets the method/attribute with MAX entropy
	 * @param entropyAttributes
	 * @return
	 */
	public String getAttributeOrQuestionWithMaxEntropy(HashMap<String, Float> entropyAttrib ){
		
		HashMap<String, Float> finalMap = EntropyCalculation.sortByValues(entropyAttrib);
		//Return the first value from the Map
		return finalMap.keySet().toArray()[0].toString();
		
	}
	
	/**
	 * Displays attributes / questions and their entropies
	 * @param entropyAttrib
	 */
	
	public void displayAttributesWithEntropy(HashMap<String, Float> entropyAttrib){
		
		Iterator<Entry<String,Float>> iterator = entropyAttrib.entrySet().iterator();
		while (iterator.hasNext()) 
		{
			Entry<String,Float> mapEntry = iterator.next();
			System.out.println("\n *** KEY Attribute ===> " + mapEntry.getKey() +" ¶¶¶ Value Entropy ===>  " + mapEntry.getValue() +" *** "  );

		}	

	}
}
