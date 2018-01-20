/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package irproject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author manth
 */
public class IRProject {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, FileNotFoundException, InterruptedException, Exception {
        Scanner sc=new Scanner(System.in);
        boolean quit=false;
        
        while(quit!=true)
        {
        System.out.println("Information Retrieval Project \n  Made By -> Manthan Thakker (thakker.m@husky.neu.edu)\n"+"         -> Vineet trivedi (trivedi.v@husky.neu.edu)");
        System.out.println("SELECT THE SEARCH ENGINE: ");
        System.out.println("1.	BM25 with Pseudo Relevance Feedback and Stopping\n" +
"2.	BM25 with Pseudo Relevance Feedback\n" +
"3.	BM25 with Stopping\n" +
"4.	BM25\n" +

"5.	Cosine Similarity\n" +
"6.	tf-idf");
        //sc.nextLine();
        String choice=sc.next();
        switch(choice)
        {
            case "1": BM25StopWordsPseudoRelSearchEngine.run();
                break;
            case "2":  BM25PseudoRelSearchEngine.run();
                break;
            case "3": BM25StopWords.run();
                break;
            case "4":  BM25SearchEngine.run();
                break;
           
            case "5": // CosineSimSearchEngine.run();
                break;
            case "6":  TFIDFSearchEngine.run();
                break;
            default: System.out.println(" Please enter a valid choice :) ");
            break;
                      
        }
        EvaluationEngine.run(choice);
        System.out.print("Do you want to continue? Press q to quit  Press any key to continue");
        String user_choice=sc.next();
        if(user_choice.equals("q"))
            quit=true;
        
        }


      
       
      
      
       
       
        
        
    }
    
}
