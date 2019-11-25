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


public class Pagerank{
    private static String path = "test.txt";
    final private static Integer numCollection = 3204;
    private static double[][] array = new double [numCollection][numCollection];

    public static void main (String[] args){
        createMatrix(path);
        normalizeMatrix();
        //System.out.print(array[669][1]);
    }

    private static void createMatrix(String path){
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
    }

    private static void normalizeMatrix(){
        for (int i = 1; i < array[0].length; i++ ){
            double sum = 0;
            for(int j = 1; j <  array[0].length; j++){
                sum += array[i][j];
            }
            //if there is no document referencing the current document, we change every element in entire row to 1/3204. 
            if (sum == 0){
                for(int j = 1; j <  array[0].length; j++){
                    array[i][j] = 1/numCollection;
                    
                }
            }
        }
    }




}

