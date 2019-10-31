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

public class Invert{
    
    private static String path = "testing.txt";
    private static String dictionaryFilePath = "dictionary.txt";
    private static String postingFilePath = "posting.txt";
    private static String stopwordPath = "stopwords.txt";
    private static ArrayList<String> stopwordList = new ArrayList<String>();
    private static Map<String, Integer> dictionary = new TreeMap<String, Integer>();//dictionary list
    //posting stores term, documentID and term frequency.
    private static Map<String, Map<Integer, Integer>>posting = new TreeMap<String, Map<Integer,Integer>>();


    public static void main(String[] args){
        createStopwordList(stopwordPath);
        createTreeMap(path);
        createDictionaryList(dictionary);
        createPostingList(posting);
        
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
        int docId = 0;
        Boolean stopword= false;

        System.out.println("Enter y to use stopword removal and stemming algorithm\nEnter n to continue without stopwords removal and stemming algorithm");
        Scanner scan = new Scanner(System.in);
        if (scan.nextLine().toLowerCase().equals("y")){
            stopword = true;
        }
        scan.close();

        try{
            Scanner scanner = new Scanner(new File(path));
            while ( scanner.hasNextLine()){
                tmp = scanner.nextLine();
                if (tmp.substring(0,2).toLowerCase().equals(".i")) {
					docId = Integer.parseInt( tmp.substring(3));
					tmp = scanner.nextLine();
                }
                if (tmp.toLowerCase().equals(".t")) {
					tmp = scanner.nextLine();
					// stop scanning if next line is .B or .W
					while (!tmp.toLowerCase().equals(".w") && !tmp.toLowerCase().equals(".b")) {
						// remove all chars that are not a-zA-Z
						updateTreeMaps(tmp, docId, stopword);
						tmp = scanner.nextLine();
					}
                }

                if(tmp.toLowerCase().equals(".w")){
                    tmp = scanner.nextLine();
                    while(!tmp.toLowerCase().equals(".b")){
                        updateTreeMaps(tmp,docId,stopword);
                        tmp = scanner.nextLine();
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
                update(temp,id);
            }
        }else {
            update(temp,id);
        }

    }
    termScanner.close();
}

public static void update(String temp, int id){

    //for dictionary list that does not have the term
    if(!dictionary.containsKey(temp)){
        dictionary.put(temp,1);
    } else {
        dictionary.replace(temp, dictionary.get(temp)+1);
    }
    
    //for posting list does not have the term
    Map<Integer, Integer> map = new TreeMap<Integer,Integer>();
    if(!posting.containsKey(temp)){
        map.put(id, 1);
        posting.put(temp,map);
    } else{
        map = posting.get(temp);
        if(!map.containsKey(id)){
            map.put(id,1);
        } else {
            map.put(id, map.get(id)+1);
        }
        posting.replace(temp, map);
    }
}

private static void createDictionaryList(Map<String, Integer> map){
    try{
        FileWriter fw = new FileWriter(dictionaryFilePath);
        for(String index : map.keySet()){
            String key = index.toString();
            String value = map.get(index).toString();
            fw.write(key+" "+ value + "\n");
        }
        fw.close();
    } catch(Exception e){
        System.out.println(e);
    }
}

private static void createPostingList(Map<String, Map<Integer, Integer>> map){
    String posting = "";
    try{
        FileWriter fw = new FileWriter (postingFilePath);

        for(String term: map.keySet()){
            posting = "";
            for(int id : map.get(term).keySet()){
                posting += id + " " + map.get(term).get(id) + " ";
            }
            fw.write(term+ " "+posting + "\n");
        }
        fw.close();
    }catch(Exception e){System.out.println(e);}
}



}