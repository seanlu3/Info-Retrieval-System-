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


public class Eval{
    private static String queryTextFilePath = "query.text";
    private static String qrelsFilePath = "qrels.text";
    private static String stopwordPath = "stopwords.txt";
    private static ArrayList<String> stopwordList = new ArrayList<String>();
    private static Map<String, Integer> query = new TreeMap<String, Integer>();


    public static void main(String args[]){
        createStopwordList(stopwordPath);
        createTreeMap(queryTextFilePath);    
        System.out.println(query);    
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
        String tmp = "";
        int docId = 1;
        Boolean stopword = true;

        try{
            Scanner scanner = new Scanner(new File(path));
            while (scanner.hasNextLine()){
                tmp = scanner.nextLine();
                if (tmp.substring(0,2).toLowerCase().equals(".i")) {
                    docId = Integer.parseInt(tmp.substring(3));
                    System.out.println(docId);
                    tmp = scanner.nextLine();
                    //System.out.println(tmp);
                }
                if (tmp.toLowerCase().equals(".w")) {
                    tmp = scanner.nextLine();
                    System.out.println(tmp);
                    // stop scanning if next line is .N
                    while (!tmp.toLowerCase().equals(".n")) {
                        // remove all chars that are not a-zA-Z
                        updateTreeMaps(tmp, docId, stopword);
                        tmp = scanner.nextLine();
                    }
                if(tmp == ".I 0"){
                    break;
                }

                    
                }
            }

            scanner.close();

        } catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }




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
}