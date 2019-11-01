import java.io.File;
import java.io.FileWriter;  
import java.io.FileInputStream;  
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.PrintWriter;


public class Eval{
    private static String queryTextFilePath = "query1.text";
    private static String qrelsFilePath = "qrels.text";
    private static String stopwordPath = "stopwords.txt";
    private static ArrayList<String> stopwordList = new ArrayList<String>();
   // private static Map<String, Integer> query = new TreeMap<String, Integer>();

    private static Map<Integer, ArrayList<String>> queries = new TreeMap<Integer, ArrayList<String>>();


    public static void main(String args[]){
        createStopwordList(stopwordPath);
        createTreeMap(queryTextFilePath);   
        searchQuery() ;
        //System.out.println(queries);    
    }

    private static void createStopwordList(String path){
        try {
			Scanner scanner = new Scanner(new File(path));
			while (scanner.hasNextLine()) {
				stopwordList.add(scanner.nextLine());
			}
            scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}


    private static void createTreeMap(String path){
    	//This removes the blank lines from query.txt
    	Scanner file;
        PrintWriter writer;
        

        try {

            file = new Scanner(new File("query.text"));
            writer = new PrintWriter("query1.text");

            while (file.hasNext()) {
                String line = file.nextLine();
                line = line.trim();
                if (!line.equals("")) {
                    writer.write(line);
                    writer.write("\n");
                }
            }

            file.close();
            writer.close();

        } catch (FileNotFoundException ex) {
            
        }
        
        //
        String tmp = "";
        int docId = 1;
        Boolean stopword = true;

        try{
            Scanner scanner = new Scanner(new File(path));
            while (scanner.hasNextLine()){
                tmp = scanner.nextLine();
                if (tmp.substring(0,2).toLowerCase().equals(".i")) {
                    docId = Integer.parseInt(tmp.substring(3));
                    //System.out.println(docId);
                    tmp = scanner.nextLine();
                    //System.out.println(tmp);
                }
                if (tmp.toLowerCase().equals(".w")) {
                    tmp = scanner.nextLine();
                    // stop scanning if next line is .N
                    ArrayList<String> sentence = new ArrayList<String>();
                    while (!tmp.toLowerCase().equals(".n")) {
                        // remove all chars that are not a-zA-Z
                        //System.out.println(tmp);
                            sentence.add(tmp);
                            queries.put(docId, sentence);
                        //updateTreeMaps(tmp, docId, stopword);
                        tmp = scanner.nextLine();
                    }
                    //System.out.println(sentence);
                if(tmp == ".I 0"){
                    break;
                }

                    
                }
            }

            scanner.close();

        } catch(Exception e){
            System.out.println("End of file reached");
        }
    }

    private static void searchQuery(){
        Search search = new Search();
        search.getIdf("posting.txt");
        search.getItf("posting.txt");
        search.getWeight("dictionary.txt");
        search.normalizeWeight();
        int count = 1;
        List<Double> mapList = new ArrayList<>();
        for(int index: queries.keySet()){
            String toBeSearched ="";
            for(String index1 : queries.get(index)){
                toBeSearched += index1;
            }
            //docId with it's similarity score
            Map<Integer, Double> test = search.getResult(toBeSearched);
            //System.out.println(toBeSearched);
            
            double totalDocsChecked = 0;
            double totalRelDocs = 0;
            double totalRel=0;
            List<Double> apList = new ArrayList<>();
            //System.out.println(toBeSearched);
            for(int st : test.keySet()) {
            	if (!(test.get(st).isNaN())) {
            		if(!(test.get(st)<0.1)) {
            		//System.out.print(st + "\n");
            		totalDocsChecked++;
            		try {
                	Scanner scanner = new Scanner(new File("qrels.text"));
                	String token1 = "";
                	String[] queryID;
                    while (scanner.hasNext()){
                    	//Checks dictionary line by line
                        token1 = scanner.nextLine();
                        queryID = token1.split(" ");
                        //if(Integer.parseInt(queryID[0]) == count) {
                        //System.out.println(count + " " + queryID[0] + " " + queryID[1]);
                        //}
                        if(Integer.parseInt(queryID[0]) == count && st == Integer.parseInt(queryID[1])) {
                        	//System.out.println(toBeSearched + ": "+ st + " matched with "+ queryID[1]);
                        	totalRelDocs++;
                        	apList.add(totalRelDocs/totalDocsChecked);
                        }
                        if (Integer.parseInt(queryID[0]) == count) {
                        	totalRel++;
                        }
                    }
                    //System.out.println(toBeSearched + " --> " + count + " " + queryID[0]);
                    scanner.close();
            		}catch(Exception e) {}
            		}
            	}
            }
            count++;
            //System.out.println("List of precision values from query: "+ toBeSearched + " is: "+ apList + " relevant (#" + totalRel/totalDocsChecked + ") results");
           double temp = 0.0;
            for(double db : apList) {
        	   temp += db;
           }
            System.out.println("AP value for query: " + toBeSearched + " is: " + temp/(totalRel/totalDocsChecked));
            mapList.add(temp/(totalRel/totalDocsChecked));
        }
        double acc=0.0;
        double counter = 0;
        for(double db : mapList) {
        	if(!(Double.isNaN(db))) {
        	acc+=db;
        	counter++;
        	}
        }
        System.out.println("Final MAP value is: " + acc/counter);
        
    }



/*
    private static void updateTreeMaps(String input, int id, Boolean stopword){
        String temp;
        input = input.replaceAll("[^a-zA-Z ]", " ");
        Scanner termScanner = new Scanner(input);
        while(termScanner.hasNext()){
            temp = termScanner.next().toLowerCase();
            if(stopword){
                if(!stopwordList.contains(temp)){
                    query.put(temp,id);
                }
            }else {
                query.put(temp,id);
            }
    
        }
        termScanner.close();
    }
*/
/*
    private static void searchQuery(){
        int docId = 1;
        ArrayList<String> w = new ArrayList<String>();
        //System.out.println(query);
        for (String term : query.keySet()){
            if (query.get(term) == docId){
                w.add(term); 
            } 
        }
        //System.out.println(w);
        
        Search a = new Search();
    }
    */
}