import java.io.File;
import java.io.FileWriter;  
import java.io.FileInputStream;  
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.lang.Math.*;
import java.util.*;
import java.lang.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class Search {
    private static String dictionaryPath = "cacm.all";
    private static String postingPath = "posting.txt";
    final static Double NUMTOTALDOCS = (double) 3204.0; //total number of document is 3204.
    private static Map<String, Map<Integer, Double>> postingitf = new TreeMap<String, Map<Integer, Double>>();
    private static Map<String, Double> dictionaryidf = new TreeMap<String, Double>();
    private static Map<String, Map<Integer, Double>> weightmap = new TreeMap<String, Map<Integer, Double>>();
    private static Map<Integer, Double> normalizedWeight = new TreeMap<Integer, Double>();
    private static HashMap<Integer, Double> results = new HashMap<Integer, Double>();
   



    public static void main (String[] args){
        getIdf(postingPath);
        getItf(postingPath);
        getWeight(dictionaryPath);
        normalizeWeight();
        userQuary();
        sortResults();
        //System.out.println(posting.get("wrong").get(1112));
        
        
    }

    //get inverted document frequency 
    //@param position list path
    public static void getIdf(String path){
    	String token1 = "";
    	double df = 0.0;
    	double idf = 0.0;
    	
    	try {
    		//Opens and reads dictionary.txt
    	Scanner scanner = new Scanner(new File(path));
        while (scanner.hasNext()){
        	//Checks dictionary line by line
            token1 = scanner.nextLine();
            //Checks how many pairs of numbers (a.k.a. how many docs does this term appear in (df))
            int spaceCount = 0;
            for (char c : token1.toCharArray()) {
                if (c == ' ') {
                     spaceCount++;
                }
            }
            String[] term = token1.split(" ");
            //However many spaces there are (minus 1) divided by 2 is how many docs the term appears in 
            df = ((spaceCount-1)/2);
            //Divides df by total # of docs in collection
            double temp = (NUMTOTALDOCS/df);   
            //Takes log of this number to produce idf
            idf = Math.log10(temp);
            //Adds term and idf value to dictionaryidf map
            dictionaryidf.put(term[0],idf);
        }
        //Prints out final dictionaryidf map
        //System.out.println(dictionaryidf);
        scanner.close();
    	}catch (Exception e) {System.out.println(e);}
    }

    //get inverted term frequency 
    //@param position list path
    public static void getItf(String path){
    	String tokenSplitByLine = "";
    	String[] tokenSplitBySpace;
    	int count = 0;
    	String currentTerm="";
    	try {
    		//Opens and reads posting.txt
        	Scanner scanner = new Scanner(new File(path));
            while (scanner.hasNext()){
            	//Reads posting one line at a time
                tokenSplitByLine = scanner.nextLine();
                
                String[] temp = tokenSplitByLine.split("\n");
                for(int i=0; i<temp.length; i++) {
                	//Creates the inner map that is used inside postingitf<String, Map<Integer, Double>>
                    Map<Integer, Double> innerpostingitf = new TreeMap<Integer, Double>();
                    //Splits the line up via spaces
                	tokenSplitBySpace = temp[i].split(" ");
                	count = tokenSplitBySpace.length-1;
                	for(int j=0; j<count; j++) {
                		if(j==0) {
                			//Creates an empty placeholder inner map for the time being
                			innerpostingitf.put(0, 0.0);
                			//Adds term to postingitf map
                			postingitf.put(tokenSplitBySpace[j], innerpostingitf);
                			//currentTerm is used in following if statement 
                			currentTerm=tokenSplitBySpace[j];
                			}
                		//Basically just extracts the docID from postings list
                		if(!(j%2==0)) {
                			//docID
                			//System.out.println(temp2[j]); 
                			//tf
                			//System.out.println(temp2[j+1]);
                			//Assigns docID to docID variable
                			int docID = Integer.parseInt(tokenSplitBySpace[j]);
                			//Assigns tf to itf variable
                			double itf = Integer.parseInt(tokenSplitBySpace[j+1]);
                			//Performs calculation to produce itf value
                			itf = 1 + Math.log10(itf);
                			//System.out.println(currentTerm + " | " + docID + " | "+ itf + " | ");
                			//Adds result to inner map
                			innerpostingitf.put(docID, itf);
                			//replaces the placeholder map we created above with the new correctly filled map
                			postingitf.put(currentTerm, innerpostingitf);
                		}
                	}
                }
                
            }
            scanner.close();
        	}catch (Exception e) {System.out.println(e);}
    }
    
    //get weight value for each term 
    public static void getWeight(String path){
    	//Create a String array that is as long as there are unique terms
    	String[] token1 = new String[dictionaryidf.size()];
    	int count = 0;
    	try {
    		//Reads dictionary.txt just to extract the unique terms and assign them to token1
        	Scanner scanner = new Scanner(new File(path));
            while (scanner.hasNextLine()){
            	token1[count] = scanner.next();
            	//System.out.println(token1[count]);
            	count++;
            	scanner.nextLine();
            }
            scanner.close();
        }catch (Exception e) {System.out.println(e);}
    		//Iterates through postingitf to extract docID and itf value (repeats for each term)
    		for(Map.Entry<String,Map<Integer, Double>> entry : postingitf.entrySet()) {
    			//System.out.println("Key = " + entry.getKey());
    			Map<Integer, Double> innerweightmap = new TreeMap<Integer, Double>();
    			//Iterates through the inner map that exists in postingitf map
    			for(Map.Entry<Integer, Double> entry1 : entry.getValue().entrySet()) {
    					//System.out.println("docID = " + entry1.getKey() + ", ITF = " + entry1.getValue()); 
    				//Extracts the idf and itf values and multiplies them together = weight
    				Double weight = dictionaryidf.get(entry.getKey())*entry1.getValue();
    					//System.out.println(entry1.getKey());
    					//System.out.println(weight);
    				//Assigns docID and term weight to inner map found within weight map
    				innerweightmap.put(entry1.getKey(), weight);
    				
    			}
    			//Assigns inner map to the weight map using the current term (entry.getKey()) as the key
    			weightmap.put(entry.getKey(), innerweightmap);
    		}//System.out.println(weightmap);

    }

    //normailzed the term weights for each document
    public static void normalizeWeight(){
        int docId = 1; //documentId starts at 1
        //for each docId, we search on weightmap, calculate normalized weight for that docId
        while (docId <= NUMTOTALDOCS){
            double w = 0; 
            for(Map.Entry<String,Map<Integer, Double>> entry : weightmap.entrySet()) {
                for(Map.Entry<Integer, Double> entry1 : entry.getValue().entrySet()) {
                    if (entry1.getKey() == docId){
                        w += (entry1.getValue()) * (entry1.getValue()); //square of the weight for the docId
                        //System.out.println(w);
                    }
                }

            }
            w = Math.sqrt(w); //normailized weight for the docId
            //System.out.println(w);
            normalizedWeight.put(docId, w);
            //System.out.println(normalizedWeight);
            docId++;
        }
       
    }

    //get user to put query, and perform search on the query terms
    private static void userQuary(){
        String token1 = "";
        String stopstem = "";
        double nWeight = 0; //normailized query weight
        Map<String, Integer> userQuery = new TreeMap<String, Integer>();
        Map<String, Double> normalizedUserQuery = new TreeMap<String, Double>();
        
        
        Scanner scan = new Scanner(System.in);
        System.out.println("Stop word removal/stemming? (y/n): ");
        stopstem = scan.nextLine();
        //System.out.println(stopstem);
        System.out.println("Search: ");
        token1 = scan.nextLine();
        token1 = token1.toLowerCase();
        //System.out.println(token1);
        String[] term = token1.split(" ");
        int count = 0;
        if(stopstem.equals("y")) {
        for (String st : term) {
            Stemmer s = new Stemmer();
        	String words[] = st.split(" ");
        	for (int i = 0; i < words.length; i++) {
				for (int j = 0; j < words[i].length(); j++) {
					char c = words[i].charAt(j);
					s.add(c);
				}
				s.stem();
				words[i] = s.toString();
				term[count] = String.join("", words);

        	}
        	
        	count++;
        }
        }

        
        try {
        //get each term term frequency and store them in the map userQuery.
        for (String st : term){
           if(!userQuery.containsKey(st)){
             userQuery.put(st,1);
           }else {
             userQuery.put(st, userQuery.get(st)+1);
           }
        }
        for(String index : userQuery.keySet()){
            double f = userQuery.get(index);
            double tf = 1 + Math.log10(f);
            double idf;
            try {
            	idf = dictionaryidf.get(index);
                double w = tf * idf;
                //System.out.println(index + "f= " + f + " tf= "+ tf + " idf= " + idf + " w= "+ w);
                normalizedUserQuery.put(index, w);
            }catch (Exception e) {}

        }

        //System.out.println(normalizedUserQuery);
        double temp = 0;
        for(double weight : normalizedUserQuery.values()){
            temp += weight * weight;
        }
        nWeight = Math.sqrt(temp);
        //System.out.println(nWeight);
        scan.close();



        double sim = 0;
        int docId = 1;

        while (docId <= NUMTOTALDOCS){
            double top = 0;
            for( String index1 : userQuery.keySet()){
                if(dictionaryidf.containsKey(index1)){
                    if(weightmap.get(index1).get(docId) == null){
                        continue;
                    }
                     top += weightmap.get(index1).get(docId) * normalizedUserQuery.get(index1);
                    
                }
                
                
            }
        
            //System.out.println(top);
            double tmp = normalizedWeight.get(docId) * nWeight;
            sim = (double) top / tmp;
            //System.out.print(sim + " ");
            //System.out.println(docId + "\n");
            results.put(docId, sim);
            docId++;
        }
        } catch(Exception e) {System.out.println("Term not found: " + e);}

    }
    


   

    private static void sortResults(){
    	//Creates a sortedResults map to store the key value pairs once sorted
    	Map<Integer, Double> sortedResults = sortByValue(results);
    	int count = 0;
    	//Prints sorted results map
    	try {
    	for (Map.Entry<Integer, Double> en : sortedResults.entrySet()) { 
            //System.out.printf("docID = " + en.getKey() +  
                          //", Cosine Sim = %.3f %n", en.getValue()); 
    		if(en.getValue() > 0 && count < 50) {
    		printTitle(en.getKey(), en.getValue());
    		count++;
    		}
        } 
    	}catch(Exception e) {System.out.println(e);}
    }

    //sort results map
    //@param hashmap that contains interger as key, double as value
    //@return n/a
    public static HashMap<Integer, Double> sortByValue(HashMap<Integer, Double> hm) {
        // Create a list from elements of HashMap 
        List<Map.Entry<Integer, Double> > list = 
               new LinkedList<Map.Entry<Integer, Double> >(hm.entrySet()); 
  
        // Sort the list 
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double> >() { 
            public int compare(Map.Entry<Integer, Double> o1,  
                               Map.Entry<Integer, Double> o2) 
            { 
                return (o2.getValue()).compareTo(o1.getValue()); 
            } 
        }); 
          
        // put data from sorted list to hashmap  
        HashMap<Integer, Double> temp = new LinkedHashMap<Integer, Double>(); 
        for (Map.Entry<Integer, Double> aa : list) { 
            temp.put(aa.getKey(), aa.getValue()); 
        } 
        return temp;     	
    }

    //Print tile documentId and similarity score for the document
    //@param docId, cosine similarity score
    //@return output the docid, title, and similarity score for each document
    public static void printTitle(Integer docID, Double cosSim) throws FileNotFoundException, IOException {
		int documentID = docID;
		String fileName = "cacm.all" + "";
		// int temp = 1;
		String location = ".I " + documentID + "";
		String line;
		//Reads the file to get and print the title of document with docID
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			while ((line = br.readLine()) != null) {
				if (line.contentEquals(location)) {
					line = br.readLine();
                    line = br.readLine();
                    if(cosSim != 0.0){
                        System.out.println("|DocID= " + documentID + " |Title= " + line + " |Similarity= "+ cosSim);
                    }
					// System.out.println(temp+" "+documentID);
				
					break;
				}
			}
		}
    }

    //get the docID and cosine similarity score for each query term 
    //@param query term from query.text
    //@return a map contains docID and cosine similarity score
   public static Map<Integer, Double> getResult(String a){
    userQuary(a);
    return sortResultsQuery();
}
    

    //function call for constructor 
    //@param String, query string
    //@return n/a
    public static void userQuary(String b){
        String token1 = "";
        String stopstem = "y";
        double nWeight = 0; //normailized query weight
        Map<String, Integer> userQuery = new TreeMap<String, Integer>();
        Map<String, Double> normalizedUserQuery = new TreeMap<String, Double>();


        Scanner scan = new Scanner(b);
        token1 = scan.nextLine();
        token1 = token1.toLowerCase();
        //System.out.println(token1);
        String[] term = token1.split(" ");
        int count = 0;
        if(stopstem.equals("y")) {
        for (String st : term) {
            Stemmer s = new Stemmer();
        	String words[] = st.split(" ");
        	for (int i = 0; i < words.length; i++) {
				for (int j = 0; j < words[i].length(); j++) {
					char c = words[i].charAt(j);
					s.add(c);
				}
				s.stem();
				words[i] = s.toString();
				term[count] = String.join("", words);

        	}
        	
        	count++;
        }
        }

        
        
        try {
        //get each term term frequency and store them in the map userQuery.
        for (String st : term){
           if(!userQuery.containsKey(st)){
             userQuery.put(st,1);
           }else {
             userQuery.put(st, userQuery.get(st)+1);
           }
        }
        for(String index : userQuery.keySet()){
            double f = userQuery.get(index);
            double tf = 1 + Math.log10(f);
            double idf;
            try {
            	idf = dictionaryidf.get(index);
                double w = tf * idf;
                //System.out.println(index + "f= " + f + " tf= "+ tf + " idf= " + idf + " w= "+ w);
                normalizedUserQuery.put(index, w);
            }catch (Exception e) {}

        }

        //System.out.println(normalizedUserQuery);
        double temp = 0;
        for(double weight : normalizedUserQuery.values()){
            temp += weight * weight;
        }
        nWeight = Math.sqrt(temp);
        //System.out.println(nWeight);
        scan.close();



        double sim = 0;
        int docId = 1;

        while (docId <= NUMTOTALDOCS){
            double top = 0;
            for( String index1 : userQuery.keySet()){
                if(dictionaryidf.containsKey(index1)){
                    if(weightmap.get(index1).get(docId) == null){
                        continue;
                    }
                     top += weightmap.get(index1).get(docId) * normalizedUserQuery.get(index1);
                    
                }
                
                
            }
        
            //System.out.println(top);
            double tmp = normalizedWeight.get(docId) * nWeight;
            sim = (double) top / tmp;
            //System.out.print(sim + " ");
            //System.out.println(docId + "\n");
            results.put(docId, sim);
            docId++;
        }
        } catch(Exception e) {System.out.println("Term not found: " + e);}

    }
   
 

    //return the sorted Results for constructor
    public static Map<Integer, Double> sortResultsQuery(){
    	//Creates a sortedResults map to store the key value pairs once sorted
    	Map<Integer, Double> sortedResults = sortByValue(results);
    	int count = 0;
    	//Prints sorted results map
    	return sortedResults;
    }
}