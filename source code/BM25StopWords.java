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
import java.util.ArrayList;
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
public class BM25StopWords {
    public static HashMap < String, HashMap > index = new HashMap();
    public static HashMap<String,Long> doc_length=new HashMap();
    public static double avg_dl;
    public static HashMap<String,Double> rankings=new HashMap<String,Double>();
    public static HashMap < String, ArrayList > relevance = new HashMap();
    public static int N;
    public static HashMap<String,String> test_queries;
    public static ArrayList<String> stop_words=new ArrayList();
    // contains document_id and the score
    public static void run() throws IOException {
        get_inverted_index();
        get_stop_words();
      
        set_avg_dl();
        test_queries=get_test_queries();
        
        get_relevant_documents("inputforBM25StopWords\\cacm.txt");
        Scanner sc=new Scanner(System.in);
     
        N=doc_length.size();
   
        for(String query:test_queries.keySet())
        {
            
            rankings=new HashMap();
       
        String query_term=remove_stop_words(removePunctuations(test_queries.get(query)).toLowerCase().trim());//removePunctuations(test_queries.get(query)).toLowerCase();
      
        compute_BM25_score_for(query_term,"Q"+query);
        }
        
        
        
        
        
        
        
    }
    
    
      public static void get_stop_words() throws IOException
        {
            File f=new File("inputforBM25StopWords\\common_words.txt");
            Scanner sc=new Scanner(f);
            while(sc.hasNext())
            {
                stop_words.add(sc.nextLine());
            }
           
            
        }
        public static String remove_stop_words(String text)
        {
           String terms[]=text.split(" ");
            String processed="";
            for(String term:terms)
            {
                if(!stop_words.contains(term))
                processed+=term+" ";
            }
            return processed;
           // return text;
        }
    public static void compute_BM25_score_for(String query,String q_id) throws IOException
    {
      
        String query_terms[]=query.split(" ");
        for(String query_term: query_terms)
        {
          
            HashMap<String,Long> doc_to_rank=index.get(query_term);
            if(doc_to_rank==null)
            {
               
                continue;
            }
            ArrayList<String> rel_docs=relevance.get(q_id);
            double ni=doc_to_rank.size();
             if(rel_docs==null)
            {
                
                continue;
            }
            double R=rel_docs.size();
            double ri=0.0;
            // compute ri
            for(String rel_doc_id: rel_docs)
            {
                if(doc_to_rank.get(rel_doc_id)!=null)
                    ri=ri+1.0;
            }
             double score=0.0;
             double score_num=(ri+0.5)/(R-ri+0.5);
             double score_denom=(ni-ri+0.5)/(N-ni-R+ri+0.5);
             double score_partA=score_num/score_denom;
             score_partA=Math.log(score_partA);
             double score_partB=0;
            for(String doc_id:doc_to_rank.keySet())
            {
               double fi=Double.parseDouble(doc_to_rank.get(doc_id)+" ");
               double K=compute_k(doc_length.get(doc_id));
               score_partB=((1.2+1)*fi)/(K+fi);
               score=score_partA*score_partB;
               if(rankings.get(doc_id)==null)
                   rankings.put(doc_id,score);
               else
               {
                   Double initial_score=rankings.get(doc_id);
                 
                   rankings.put(doc_id,(initial_score+score));
               }
                
                
            }
           
            
        }
        Map<String,Double> sorted_docs=sortByValue(rankings);
      
        File f=new File("CACMResultsForBM25+StopWords\\Top100\\Q"+q_id.substring(1)+"-top100.txt");
        FileWriter fw=new FileWriter(f);
        String to_file=to_TREC_format(sorted_docs,q_id);
        fw.write(to_file);
        
       fw.close();
          
       
       
           
        
        
        
        
        
    }
    public static String to_TREC_format(Map < String, Double > t, String Q_ID) {
   int rank = 1;
   String result = "";
   for (String doc: t.keySet()) {
    /*query_id Q0 doc_id rank CosineSim_score system_name*/ // <-----format 
    result+=" "+Q_ID+" "+doc+" "+rank+++" "+t.get(doc)+" GOOGLE_SERVER_#234\n";
    if(rank>100)
        break;
    
   }
   return result;
  }
    
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
        
    public static void set_avg_dl()
    {
        double total_dl=0.0;
        for(String doc_id:doc_length.keySet())
        {
            total_dl+=doc_length.get(doc_id);
        }
        avg_dl=total_dl/doc_length.size();
    }
    
    public static double compute_k(Long dl)
    {
        double K=0.0;
        double B=0.75;
        double k1=0.2;
        K=(1-B);
        K+=(B*dl/avg_dl)*k1;
        return K;
    }
    
    
    public static void get_inverted_index()
    {
     try (FileInputStream fileIn = new FileInputStream("inputforBM25StopWords\\stop-words-index.ser"); ObjectInputStream in = new ObjectInputStream(fileIn)) {
         index = (HashMap) in.readObject();
       
         FileInputStream fileIn1 = new FileInputStream("inputforBM25StopWords\\doc_len.ser");
         ObjectInputStream in1 = new ObjectInputStream(fileIn1);
         doc_length=(HashMap)in1.readObject();
      }catch(IOException | ClassNotFoundException i) {
          System.out.println(" FILES NOT PROPERLY IMPORTED");
      }
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
      public static HashMap<String,String> get_test_queries()
    {
        HashMap test_queries=null;
         
      try {
         FileInputStream fileIn = new FileInputStream("inputforBM25StopWords\\query-test.ser");
         ObjectInputStream in = new ObjectInputStream(fileIn);
         test_queries = (HashMap<String,String>) in.readObject();
         in.close();
         fileIn.close();
      }catch(IOException i) {
         i.printStackTrace();
         
      }catch(ClassNotFoundException c) {
         System.out.println("query-test.ser  not found");
         c.printStackTrace();
         
      }
      return test_queries;
    }
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
    
    
}
