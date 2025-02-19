import java.io.File;
import java.io.FileWriter;  
import java.io.FileInputStream;  
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.lang.Math.*;

public class Search {
    private static String dictionaryPath = "dictionary.txt";
    private static String postingPath = "posting.txt";
    final static Double NUMTOTALDOCS = (double) 4.0; //total number of document is 3204.
    private static Map<String, Map<Integer, Double>> postingitf = new TreeMap<String, Map<Integer, Double>>();
    private static Map<String, Double> dictionaryidf = new TreeMap<String, Double>();
    private static Map<String, Map<Integer, Double>> weightmap = new TreeMap<String, Map<Integer, Double>>();
    private static Map<Integer, Double> normalizedWeight = new TreeMap<Integer, Double>();
   
   



    public static void main (String[] args){
        getIdf(postingPath);
        getItf(postingPath);
        getWeight(dictionaryPath);
        normalizeWeight();
        userQuary();
        //System.out.println(posting.get("wrong").get(1112));
        
        
    }

    private static void getIdf(String path){
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

    private static void getItf(String path){
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
    
    private static void getWeight(String path){
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
    		}System.out.println(weightmap);

    }


    private static void normalizeWeight(){
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

    private static void userQuary(){
        String token1 = "";
        double nWeight = 0; //normailized query weight
        Map<String, Integer> userQuery = new TreeMap<String, Integer>();
        Map<String, Double> normalizedUserQuery = new TreeMap<String, Double>();
        Scanner scan = new Scanner(System.in);
        System.out.println("Search: ");
        token1 = scan.nextLine();
        token1 = token1.toLowerCase();
        //System.out.println(token1);
        String[] term = token1.split(" ");
        
        

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
            double idf = dictionaryidf.get(index);
            //System.out.println(idf);
            double w = tf * idf;
            normalizedUserQuery.put(index, w);
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
            System.out.print(sim + " ");
            docId++;
        }
        

        }

    }



}