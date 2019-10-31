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


    public static void main (String[] args){
        getIdf(postingPath);
        getItf(postingPath);
        getWeight(dictionaryPath);
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

}