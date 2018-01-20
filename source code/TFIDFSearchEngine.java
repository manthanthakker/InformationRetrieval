/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package irproject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 *
 * @author manth
 */
public class TFIDFSearchEngine {
     public static String file_name[] = new String[1000];
 public static HashMap < String, HashMap > index = new HashMap();
 public static HashMap<String,Long> doc_length=new HashMap();
 public static double N = 1000.0;
 
 public static void run() throws FileNotFoundException, IOException, InterruptedException {
  
  System.out.println("Welcome to Toogle Search Engine.\n Kindly Wait the engine is loading... \n");
  //get_inverted_index impoets the inverted index from index.ser and doc_len
  get_inverted_index();
  // compute the TF_IDF for each TF
  index = compute_TF_IDF();
  
  // QUERY-HANDLING
  String query = "q";
  int Q_ID = 1;
  //run_test_queries_cosine(get_test_queries());
  run_test_queries_by_tf(get_test_queries());
  while (!query.equals("q")) {
   System.out.println("Enter search query or press q to quit:");

   Scanner in = new Scanner(System.in);

   query = in .nextLine();
   Long t1= System.currentTimeMillis();
   
   HashMap < String, Double > query_terms = new HashMap();
   String q[] = query.toLowerCase().split(" ");
  
   
   for (String q1: q) {
       try{
    double query_score = 1.0 * Math.log10(N / index.get(q1).size());
    query_terms.put(q1, query_score);
       }
     catch(Exception e){System.out.println("Sorry, No such results exist.");
             }
   }
   

   HashMap<String,Double> temp=compute_document_score_list_by_tf(query_terms);
   Map<String,Double> temp2=sortByValue(temp);
   System.out.println("\n*****Top 100 results ******"); 
   
   System.out.println(to_TREC_format(temp2, Q_ID));
   System.out.println("response-time: "+(System.currentTimeMillis()- t1 )+"milliseconds");
   Q_ID++;
  }
  //END-OF QUERY HANDLING
}
 
 
 public static void run_test_queries_by_tf(HashMap <String,String> test_queries ) throws IOException
 {
      for(String q_id:test_queries.keySet())
        {
            int Q_ID=Integer.parseInt(q_id);
            //System.out.println(q_id);
            File f=new File("CACMResultsForTFIDF\\Top100\\Q"+q_id+"-top100.txt");
            FileWriter fw=new FileWriter(f);
            String query = test_queries.get(q_id);
           
            Long t1= System.currentTimeMillis();
            
            HashMap < String, Double > query_terms = new HashMap();
            String q[] = query.toLowerCase().split(" ");
           // System.out.println("Searching...");
            for (String q1: q) {
                 try{
             double query_score = 1.0;
             query_terms.put(q1, query_score);}
              catch(Exception e){
                  
              }
            }
           

            HashMap<String,Double> temp=compute_document_score_list_by_tf(query_terms);
            Map<String,Double> temp2=sortByValue(temp);
          

           String result=to_TREC_format(temp2, Q_ID);
           fw.write(result);
           fw.close();
          
            Q_ID++;

        }
 }
 
 
 
 public static HashMap<String,Double> compute_document_score_list_by_tf (HashMap<String,Double> query)
 {
      HashMap<String, Double> document_score_list=new HashMap(); 
     //HashMap<String ,Double> score_board =new HashMap();
    
    
     // calculation of denom_part_query
     
    
     for(String  q : query.keySet() )
     {
         try{
          HashMap<String,Double> doc_to_rank=index.get(q);
          for(String doc:doc_to_rank.keySet())
          {
          
              if(document_score_list.get(doc)==null)
              {
           document_score_list.put(doc,doc_to_rank.get(doc));
         
              }
              else
              {
                  double tmpscore=document_score_list.get(doc)+doc_to_rank.get(doc);
                  document_score_list.put(doc,tmpscore);
              }
              
          }}catch(Exception e){
          }
        
     }
     return document_score_list;
 }
 
 
 
////////////////////////////////////////////////////////////////////////////////
 // importing inverted-index of previous assignmnt
public static void get_inverted_index()
{
     try (FileInputStream fileIn = new FileInputStream("inputforTFIDF\\index.ser"); ObjectInputStream in = new ObjectInputStream(fileIn)) {
         index = (HashMap) in.readObject();
         FileInputStream fileIn1 = new FileInputStream("inputforTFIDF\\doc_len.ser");
         ObjectInputStream in1 = new ObjectInputStream(fileIn1);
         doc_length=(HashMap)in1.readObject();
      }catch(IOException | ClassNotFoundException i) {
          System.out.println(" FILES NOT PROPERLY IMPORTED");
      }
}
// new functions////////////////////////////////////////////////////////////////
 // STEP#0: COMPUTE TF_IDF AND MODIFY THE INDEX
 public static HashMap < String, HashMap > compute_TF_IDF() {

  HashMap<String,HashMap> new_index=new HashMap();
 
  for(String term:index.keySet())
  {
      HashMap<String,Double> old_entry=index.get(term);
       HashMap<String,Double> new_entry =new HashMap();
      double d=old_entry.size();
    
      for(String doc: old_entry.keySet())
      {
          double TF_IDF= Math.log10(N/d)*old_entry.get(doc)/doc_length.get(doc);
          new_entry.put(doc, TF_IDF);
      }
      new_index.put(term, new_entry);
  }

  return new_index;
 }
 
 ////////////////////////////////////////////////////////////////////////////////
 // scoring begins
 
 
 
 
 // compute score of doc for q 
 
public static HashMap<String,Double> compute_score_doc_term (
        String doc,String q,double denom_query_part,double query_wt)
{
     double denom_doc_part=0.0;
     double num=0.0;
     double score=0.0;
    // STEP1 : RETRIEVE ALL INVERTED LISTS CORRESPINDING TO TERMS IN QUERY 
     for(String term:index.keySet())                                          
     {
         
         HashMap<String,Double> doc_list=index.get(term);
         if(doc_list.get(doc)!=null)
         {
             denom_doc_part=denom_doc_part+sqr(doc_list.get(doc));
         }
     }
     // numerator calculation
     HashMap<String,Double> documents_with_q=index.get(q);
     double doc_wt=documents_with_q.get(doc);
      num=(doc_wt*query_wt);
      
      
     // score calculation
     double denom=denom_doc_part*denom_query_part;
     denom=Math.sqrt(denom);
    // STEP2 : COMPUTE COSINE SIMILARITY SCORE FOR DOCUMENTS IN THE LIST.   
    score=num/denom;
    
    HashMap<String,Double> final_score_of_doc=new HashMap();
    final_score_of_doc.put(doc, score);
    return final_score_of_doc;
     
     
}
 
//////////////////////////////////////////////////////////////////////////////
 
// STEP#4: SOrt The DOCUMENTS BY DOC-SCORE IN DESCENDING ORDER.
public static <K, V extends Comparable<? super V>> 
        Map<K, V> sortByValue(Map<K, V> map) {
    return map.entrySet()
              .stream()
              .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
              .collect(Collectors.toMap(
                Map.Entry::getKey, 
                Map.Entry::getValue, 
                (e1, e2) -> e1, 
                LinkedHashMap::new
              ));
}
        
////////////////////////////////////////////////////////////////////////////////
        
// STEP#5 : DISPLAY RESULTS IN TREC FORMAT: WHERE SYSTEM_NAME IS GOOGLE_SERVER_#234
        
public static String to_TREC_format(Map < String, Double > t, int Q_ID) {
   int rank = 1;
   String result = "";
   for (String doc: t.keySet()) {
    /*query_id Q0 doc_id rank CosineSim_score system_name*/ // <-----format 
    result+=" Q"+Q_ID+" "+doc+" "+rank+++" "+t.get(doc)+" GOOGLE_SERVER_#234\n";
   
    if(rank>100)
        break;
   
   }
   return result;
  }
 ///////////////////////////////////////////////////////////////////////////////
 
 
 public static double sqr(double num) {
  return num * num;
 }
 
 // GOLDEN RULE : YOU MUST PERFORM SAME REMOVING OF PUNCTUATIONS AS ONE HAS DONE
 // DURING INDEXING. VERY IMPORTANT STEP. FOR DIFFERENT CORPUS DIFFERNT RULES
 // AND DIFFERENT RULES WOULD APPLY
 
 public static String removePunctuations(String s) {
  String res = "";
  Character previousChar = null;
  int i = 0;
  for (Character c: s.toCharArray()) {
   if (i == 0) {
    i = 1;
    previousChar = c;
    continue;
   }
   if (Character.isDigit(c) && !previousChar.equals(null)) {
    if (previousChar.equals('[') || 
            previousChar.equals(']') ||
            previousChar.equals(')') ||
            previousChar.equals('('))
     res += ' ';
    else
     res += previousChar;
   } else if (!previousChar.equals(null) &&
           (Character.isLetterOrDigit(previousChar)
           || previousChar.equals(' ') 
           || previousChar.equals('-')))
    res += previousChar;

   previousChar = c;

  }

  if (!previousChar.equals(null) && (Character.isLetterOrDigit(previousChar) || 
          previousChar.equals(' ') || 
          previousChar.equals('-')))
   res += previousChar;
  return res;
 }
 public static HashMap<String,String> get_test_queries()
    {
        HashMap test_queries=null;
         
      try {
         FileInputStream fileIn = new FileInputStream("inputforTFIDF\\query-test.ser");
         ObjectInputStream in = new ObjectInputStream(fileIn);
         test_queries = (HashMap<String,String>) in.readObject();
         in.close();
         fileIn.close();
      }catch(IOException i) {
         i.printStackTrace();
         
      }catch(ClassNotFoundException c) {
         System.out.println("Employee class not found");
         c.printStackTrace();
         
      }
      return test_queries;
    }

 
 
 // cosine functions
 
 public static void run_test_queries_cosine(HashMap <String,String> test_queries ) throws IOException
 {
      for(String q_id:test_queries.keySet())
        {
            int Q_ID=0;
            File f=new File(q_id+"-results.txt");
            FileWriter fw=new FileWriter(f);
            String query = test_queries.get(q_id);
            Long t1= System.currentTimeMillis();
            query = removePunctuations(query);
            HashMap < String, Double > query_terms = new HashMap();
            String q[] = query.toLowerCase().split(" ");
            System.out.println("Searching...");
           
            for (String q1: q) {
                 try{
             double query_score = 1.0 * Math.log10(N / index.get(q1).size());
             query_terms.put(q1, query_score);}
              catch(Exception e){System.out.println(e+"  Sorry, No such results exist.");}
            }
           

            HashMap<String,Double> temp=compute_document_score_list_by_tf(query_terms);
            Map<String,Double> temp2=sortByValue(temp);
            System.out.println("\n*****Top 100 results ******"); 
                
            //System.out.println(to_TREC_format(temp2, Q_ID));
            String results=to_TREC_format(temp2, Q_ID);
            fw.write(results);
            fw.close();
           // System.out.println("response-time: "+(System.currentTimeMillis()- t1 )+"milliseconds");
            
        }
 }
 // STEP#3: Accumulate score for each document for each query.
public static HashMap < String, Double > merge(HashMap < String, Double > 
        prev, HashMap < String, Double > new_tree_map) {
  for (String term: new_tree_map.keySet()) {
   if (prev.get(term) != null) {
    prev.put(term, prev.get(term) + new_tree_map.get(term));
   } else {
    prev.put(term, new_tree_map.get(term));
   }
  }
  return prev;
 }

///////////////////////////////////////////////////////////////////////////////

public static HashMap<String,Double> compute_document_score_list_by_cosine (HashMap<String,Double> query)
 {
     HashMap<String, Double> document_score_list=new HashMap(); 
     //HashMap<String ,Double> score_board =new HashMap();
     double denom_query_part =0.0;
    
     // calculation of denom_part_query
     
     for(String query_term:query.keySet())
     {
         denom_query_part+=sqr(query.get(query_term));
     }
     for(String  q : query.keySet() )
     {
          HashMap<String,Double> doc_to_rank=index.get(q);
          for(String doc:doc_to_rank.keySet())
          {
           double query_wt=query.get(q);
           HashMap<String,Double> doc_score=compute_score_doc_term(doc,q,denom_query_part,query_wt);
           document_score_list=merge(document_score_list,doc_score);     
          }
        
     }
     return document_score_list;
 }
    
}
