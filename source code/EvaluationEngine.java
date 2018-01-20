/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package irproject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author manth
 */
public class EvaluationEngine {
    public static HashMap < String, ArrayList > relevance = new HashMap();
    // QUERY-ID,List_Of_RelevantDocuments

    public static ArrayList < String > relevant_docs = new ArrayList();
    // List_Of_RelevantDocuments
    public static HashMap < String, ArrayList > results_list = new HashMap();
    public static ArrayList < Double > mean_average_precision = new ArrayList();
    public static double n = 64.0;
    public static ArrayList < Double > reciprocal_rank = new ArrayList();
    public static String results_for_entire_run="";
    public static HashMap<Double,Double> avg_precision_for_plot=new HashMap();
    public static HashMap<Double,Double> avg_recall_for_plot=new HashMap();
    // rank, precision


    public static void run(String choice) throws FileNotFoundException, IOException, InterruptedException {
       
       relevance=new HashMap();
       relevant_docs = new ArrayList();
       results_list = new HashMap();
       mean_average_precision = new ArrayList();
       n = 64.0;
       reciprocal_rank = new ArrayList();
       results_for_entire_run="";
       avg_precision_for_plot=new HashMap();
       avg_recall_for_plot=new HashMap();
       
        
         String PATH = "";//sc.nextLine();
        PATH = "inputForEvaluationEngine\\cacm.txt";
         get_relevant_documents(PATH);
       
        String to_evaluate_path = "";//sc.nextLine();
     
      
       String dest="";
        switch(choice)
        {
            case "1": to_evaluate_path="CACMResultsForBM25+StopWords+PseudoRel\\Top100";
                        dest="CACMResultsForBM25+StopWords+PseudoRel\\EvaluationResults";
                break;
            case "2":to_evaluate_path="CACMResultsForBM25+PseudoRel\\Top100";
                        dest="CACMResultsForBM25+PseudoRel\\EvaluationResults";
                break;
            case "3":to_evaluate_path="CACMResultsForBM25+StopWords\\Top100";
                    dest="CACMResultsForBM25+StopWords\\EvaluationResults";
                break;
            case "4":to_evaluate_path="CACMResultsForBM25\\Top100";
                     dest="CACMResultsForBM25\\EvaluationResults";
                break;
           
            case "5":to_evaluate_path="CACMResultsForCosineSim\\Top100";
                    dest="CACMResultsForCosineSim\\EvaluationResults";
                break;
            case "6": to_evaluate_path="CACMResultsForTFIDF\\Top100";
                    dest="CACMResultsForTFIDF\\EvaluationResults";
                break;
                
                       
        }
        
        
         Scanner sc = new Scanner(System.in);
         FileWriter fw1=new FileWriter(new File(dest+"\\results-for-entire-run.txt"));
       
        get_results(to_evaluate_path);
        // System.out.println("part2       "+results_list.keySet());
        
        
        String patk="";
        for (String q_id: results_list.keySet()) {
            patk="";
            boolean relevant = false;
            boolean first_relevant = false;
            File evaluation__for_query = new File(dest+"\\"+q_id + ".txt");
            FileWriter fw = new FileWriter(evaluation__for_query);
            ArrayList < String > docs_to_evalute = results_list.get(q_id);
            ArrayList < Double > precision_list = new ArrayList();
           // System.out.println("docs_to_evaluate  "+ results_list.keySet());
            double rank = 1.0;
            String table = "";
           
            
            double no_of_relevant_docs_retrieved = 0.0;
            ArrayList < String > relevant_documents = relevance.get(q_id);
            if (relevant_documents == null) {
                table = "";
               n = n - 1.0;
               
                continue;
            }

            int total_relevant = relevant_documents.size();
           
            for (String doc_id: docs_to_evalute) {
              relevant = false;
                
                if (relevant_documents.contains(doc_id)) {
                    relevant = true;
                    no_of_relevant_docs_retrieved = no_of_relevant_docs_retrieved + 1.0;
                    if (first_relevant == false) {
                        first_relevant = true;
                        reciprocal_rank.add(rank);
                     
                    }
                  
                }
                double precision = no_of_relevant_docs_retrieved / rank;
                double recall = no_of_relevant_docs_retrieved / total_relevant;
                if (relevant == true)
                {
                    precision_list.add(precision);
                    
                    table += rank + " " + doc_id + " R " + " " + precision + " " + recall + "\n";
                }
                else
                {
                    table += rank + " " + doc_id + " NR " + " " + precision + " " + recall + "\n";
                }
                 if(rank==5.0||rank==10.0)
                 {
                     patk+="precision at "+rank+" is "+precision+"\n";
                 }
                 try{
                 compute_file_for_plot(rank,precision,recall);
                 }catch(Exception e){}
                rank = rank + 1.0;
            }
            double precision_acc = 0;
            for (int i = 0; i < precision_list.size(); i++) {
                precision_acc += precision_list.get(i);
            }
            double avg_precision = 0.0;
            avg_precision = precision_acc / total_relevant;
          
            table += "average precision for " + q_id + " is " + avg_precision;
            results_for_entire_run+="average precision for " + q_id + " is " + avg_precision+"\n"+patk+"\n";
           
            fw.write(table);
            fw.close();
            mean_average_precision.add(avg_precision);
            System.out.println("PRECISION FOR QUERY_ID: "+q_id+"\n"+patk);
        }
       
        calculate_map();
        calculate_mrr();
        calculate_pk();
        make_plot_file(dest);
        
        fw1.write(results_for_entire_run);
        fw1.close();
        System.out.println("\n\n The precision and recall tables are now available at location : "+dest+" \n ");

    }
    public static void make_plot_file(String dest) throws IOException
    {
        File f=new File(dest+"\\plot2.txt");
        FileWriter fw=new FileWriter(f);
        String to_file="";
        
        for(double rank=1.0;rank<=100;rank=rank+1.0)
        {
            
            double precision=avg_precision_for_plot.get(rank);
            double recall=avg_recall_for_plot.get(rank);
            double avg_precsion_for_n_queries=precision/n;
            double avg_recall_for_n_queries=recall/n;
            to_file+="rank "+avg_precsion_for_n_queries+" "+avg_recall_for_n_queries+"\n";
           
        }
        
        fw.write(to_file);
        fw.close();
    }
    public static void compute_file_for_plot(Double rank, Double precision,Double recall)
    {
                if(avg_precision_for_plot.get(rank)==null)
                 avg_precision_for_plot.put(rank,precision);
                 else
                 {
                     double partial_precision=avg_precision_for_plot.get(rank);
                     avg_precision_for_plot.put(rank,precision+partial_precision);
                 }
                if(avg_recall_for_plot.get(rank)==null)
                 avg_recall_for_plot.put(rank,precision);
                 else
                 {
                     double partial_recall=avg_recall_for_plot.get(rank);
                     avg_recall_for_plot.put(rank,recall+partial_recall);
                 }
    }
    public static void calculate_map() {
        double total_avg_precision = 0.0;
        for (Double avg_precision: mean_average_precision) {
            total_avg_precision += avg_precision;
        }
        double mean_average_precisions = total_avg_precision / mean_average_precision.size();
        results_for_entire_run+="MEAN AVERAGE PRECISION:" + mean_average_precisions + "\n RELEVANCE AVAILABLE: " + mean_average_precision.size()+" DOCUMENTS\n";
        System.out.println("\n\n******************** RUN STATISTICS***********************************"
                + " \n MEAN AVERAGE PRECISION: " + mean_average_precisions + " \n RELEVANCE AVAILABLE: " + mean_average_precision.size()+" DOCUMENTS");
        
    }
    public static void calculate_mrr() {
        Double mean_reciprocal_rank = 0.0;
        for (Double rank: reciprocal_rank) {
            mean_reciprocal_rank += rank;
        }
        mean_reciprocal_rank = mean_reciprocal_rank / reciprocal_rank.size();
   
        System.out.println("MEAN RECIPROCAL RANK :" + (1/mean_reciprocal_rank)+"\n\n");
        results_for_entire_run+="MEAN RECIPROCAL RANK :" +(1/ mean_reciprocal_rank)+"\n";
    }
    public static void calculate_pk() {

    }

    public static void get_results(String PATH) throws FileNotFoundException {

        for (int i = 1; i <= 64; i++) {
            String m_PATH = PATH + "/Q" + i + "-top100.txt";

            File file_evaluate = new File(m_PATH);
            Scanner results=null;
           try
           {
            results= new Scanner(file_evaluate);
           }catch(Exception e)
           {
               //System.out.println(" not found");
               continue;
           }
           
            while (results.hasNext()) {
                String[] entry = results.nextLine().split(" ");
                String q_id = entry[1];
              
                String doc_to_check = entry[2];
               
                if (results_list.get(q_id) != null) {
                    ArrayList < String > ranked_documents = results_list.get(q_id);
                    ranked_documents.add(doc_to_check);
                    results_list.put(q_id, ranked_documents);
                } else {
                    ArrayList < String > ranked_documents = new ArrayList();
                    ranked_documents.add(doc_to_check);
                    results_list.put(q_id, ranked_documents);
                }

            }
        }
        // System.out.println("results lisu   "+results_list.keySet());
    }

    public static void get_relevant_documents(String PATH) {
        File relvance_file = new File(PATH);
        try {
            Scanner sc_r = new Scanner(relvance_file);
            while (sc_r.hasNext()) {
                String entry = sc_r.nextLine();
                String entry_array[] = entry.split(" ");
                String q_id = entry_array[0];
                String doc_id = entry_array[2];
                if (relevance.get("Q" + q_id) != null) {
                    ArrayList < String > relevant_docs = relevance.get("Q" + q_id);
                    relevant_docs.add(doc_id);
                    relevance.put("Q" + q_id, relevant_docs);

                } else {
                    ArrayList < String > relevant_docs = new ArrayList();
                    relevant_docs.add(doc_id);
                    relevance.put("Q" + q_id, relevant_docs);
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Please enter a valid path ....");
        }
      
    }
    
    
}
