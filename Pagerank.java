import java.io.File;
import java.io.FileWriter;  
import java.io.FileInputStream;  
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.PrintWriter;


public class Pagerank{
    private static String path = "test.txt";
    final private static Integer numCollection = 3204;
    private static double[][] array = new double [numCollection][numCollection];
    private static Map<Integer, double[][]> listMap = new TreeMap<Integer, double[][]>();
    public static void main (String[] args){
    	iterateFile(path);
        //createMatrix(path);
        normalizeMatrix();
        //System.out.print(array[1982][1]);
    }

    	//***This is a variation of your createMatrix code***
    
    private static void iterateFile(String path) {
    	//Variable assignment
    	String tmp = "";
    	int docId = 0;
    	int count;
    	int left = 0;
    	int middle = 0;
    	int right = 0; 
    	try{
    		//Reads text file
            Scanner scanner = new Scanner(new File(path));
            while ( scanner.hasNextLine()){
                tmp = scanner.nextLine();
                //Checks if following section is .X section
                if (tmp.toLowerCase().equals(".x")) {
                	docId++;
                	
                	tmp = scanner.nextLine();
                	//System.out.println("Document #" + docId);
                	//Checks if the end of section .X has been reached
                		while (!(tmp.substring(0,2).toLowerCase().equals(".i"))) {
                			//System.out.println(tmp);
                			//If not split each line under .X by "	"
                			count = 0;
                			String[] arr = tmp.split("	");    

                			for ( String ss : arr) {
                				count++;
                				if (count == 1) {
                					//System.out.println("left:" + ss);
                					left = Integer.parseInt(ss);
                				}
                				if (count == 2) {
                					//System.out.println("mid:" + ss);
                					middle = Integer.parseInt(ss);
                				}
                				if (count ==3) {
                					//System.out.println("center:" +ss);
                					right = Integer.parseInt(ss);
                				}
                			}	
                			if(middle == 5){
                                if(left > right) array[left][right] =1;
                                else if (right > left) array[right][left]=1;
                            }
                        	tmp = scanner.nextLine();
                	}
                }
            }       
            scanner.close();

        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /*private static void createMatrix(String path){
        String tmp = "";
        int left, middle, right = 0; 
        try{
            Scanner scanner = new Scanner (new File(path));
            while (scanner.hasNextLine()){
                tmp = scanner.nextLine();
                if(tmp.toLowerCase().equals(".x")){
                    //isolating referece numbers
                    scanner.nextLine();
                    while(scanner.hasNextInt()){
                        //isolating three numbers for each line. 
                        left = scanner.nextInt();
                        middle = scanner.nextInt();
                        right = scanner.nextInt();
                        System.out.println(left + " " + middle + " " + right);

                        //if the middle number is 5, it means that the referencing happens. 
                        //if left documnet is greater than the right document, it means the left doc has a reference in right doc.  Vise versa. 
                        if(middle == 5){
                            if(left > right) array[left][right] = 1;
                            else if (right > left) array[right][left] = 1;
                        }
                    }
                    scanner.nextLine();
                }
            }
            scanner.close();
        }catch(Exception e){
            System.out.println("File end reached");
        }
    }*/

    //Iterates through array and keeps track of total number of 1's on each
    //row and afterwards will normalize each row
    //Example: If two 1's exist in one row then it will normalize each to 0.5
    private static void normalizeMatrix(){
    	int count;
    	for (int i = 1; i < array.length; i++) {
    		count = 0;
    		for (int j = 1; j < array.length; j++) {
    			if(array[i][j]==1) {
    				count++;
    			}
    		}
    		//System.out.println("DocId: "+i + " points to a total of "+count+" docs ");
    		for (int x = 1; x<array.length; x++) {
    			if(array[i][x]==1) {
    				array[i][x]=(double)1/count;
    				//System.out.println(array[i][x]);
    			}
    		}
    	}
    }




}
