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
    private static String path = "cacm.all";
	final private static Integer numCollection = 3204;
	final private static Double dampingFactor = 0.85;
	private static double[][] array = new double [numCollection+1][numCollection+1];
	public static double[] probVector = new double [numCollection+1]; // this will be the final Probability distribution vector that contains normalized pagerank for each document.
    private static Map<Integer, double[][]> listMap = new TreeMap<Integer, double[][]>();
    public static void main (String[] args){
    	iterateFile(path);
        //createMatrix(path);
    	//System.out.println(array[1982][1]);
        normalizeMatrix();
        //System.out.println(array[1982][1]);
		probabilityStep();
		getFinalMatrixP();
		iterateMatrixP();
		System.out.println(array[1982][1]);
		System.out.println(probVector[3204]);
    }

    //
    //
    //As of right now array stores the correct normalized matrix
    //We now need to implement the probability value (can use a similar
    //function as the normalization function we used)
    //
    //
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
    
    //Multiplies each non-zero entry by (1-(0.85))
    private static void probabilityStep() {
    	for (int i = 1; i < array.length; i++) {
    		for (int j = 1; j < array.length; j++) {
    			//checks for all entries in the array above 0 
    			if(array[i][j]>0) {
    				//1 - 0.85 = 0.15
    				array[i][j]=(array[i][j]* (1-dampingFactor));
    			}
    		}

    	}
	}
	
	private static void getFinalMatrixP() {
		for (int i = 1; i < array.length; i++) {
			for (int j = 1; j < array.length; j++) {
    			//damping factor/number of document in this collection
    			array[i][j]=(array[i][j]+ (dampingFactor/numCollection));
    			
    		}
		}
	}

	private static void iterateMatrixP(){
		int iteration = 15;
		double[] temp = new double[numCollection];
		temp[1] = 1;
		while (iteration > 0){
			for(int i =1; i <array.length; i++){
				double sum = 0;
				for (int j = 1; j < array.length; j++) {
					sum += temp[j] * array[j][i];
				}
				probVector[i] = sum;
			}
			temp = probVector;
			iteration--;
		}
		//normalize probability distribution vector by multiplying every element with 10000
		for (int i=1; i<array.length; i++){
			probVector[i] = probVector[i] * 1000;
		}
		/*checking if there is an element is greater than 1
		for (int i=1; i<array.length; i++){
			if(probVector[i] > 1){
				System.out.println(probVector[i]);
			} */
		}
	

	public double[] parseArray(){
		iterateFile(path);
        normalizeMatrix();
		probabilityStep();
		getFinalMatrixP();
		iterateMatrixP();
		return probVector;
	}



}
