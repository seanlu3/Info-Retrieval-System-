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
import java.lang.Math.*;

public class Search {
    private static String dictionaryPath = "dictionary.txt";
    private static String postingPath = "posting.txt";
    final static Double NUMTOTALDOCS = (double) 3204.0; //total number of document is 3204.
    private static Map<String, Map<Integer, Double>> posting = new TreeMap<String, Map<Integer, Double>>();
    private static Map<String, Integer> dictionary = new TreeMap<String, Integer>();

    private static Map<String, Double>dictionaryIdf = new TreeMap<String, Double>();

    public static void main (String[] args){
        getIdf(dictionaryPath);
        getItf(postingPath);
        
    }

    private static void getIdf(String path){
        String token1 = "";
        int token2 = 0;
        try{
            Scanner scanner = new Scanner(new File(path));
            while (scanner.hasNext()){
                token1 = scanner.next();
                token2 = scanner.nextInt();
                dictionary.put(token1, token2);
            }
            scanner.close();

            for (String index : dictionary.keySet()){
                String term = index.toString();
                double df = dictionary.get(index);
                double idf = Math.log(NUMTOTALDOCS/df);
                dictionaryIdf.put(term, idf);
            }
        } catch(FileNotFoundException e ){
            e.printStackTrace();
        }
    }

    private static void getItf(String path){

    }

}