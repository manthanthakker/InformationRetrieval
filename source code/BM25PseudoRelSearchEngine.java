/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package irproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 *
 * @author manth
 */
public class BM25PseudoRelSearchEngine {
     public static String[] stopwords = new String[450];
    public static HashMap < String, HashMap > index = new HashMap();
    public static HashMap<String,Long> doc_length=new HashMap();
    public static double avg_dl;
    public static HashMap<String,Double> rankings=new HashMap<String,Double>();
    public static HashMap < String, ArrayList > relevance = new HashMap();
    public static int N;
    public static HashMap<String,String> test_queries;
    // contains document_id and the score
    public static void run() throws IOException, Exception {
      fillStopwords();
        get_inverted_index();
      
        set_avg_dl();
        test_queries=get_test_queries();
      
        get_relevant_documents("inoutforBM25pseudRel\\cacm.txt");
        Scanner sc=new Scanner(System.in);
     
        N=doc_length.size();
    
        for(String query:test_queries.keySet())
        {
            rankings=new HashMap();
     
        String query_term=removePunctuations(test_queries.get(query)).toLowerCase().trim();//removePunctuations(test_queries.get(query)).toLowerCase();
     
        Map<String,Double> top_docs=compute_BM25_score_for(query_term,"Q"+query);
        top_docs=sortByValue(top_docs);
     
        String [] queryExpansion = new String[1000];
        String [] temp = new String [1000];
        String newQuery="";
        int doc_to_consider=0;
        
        for(String doc_id:top_docs.keySet())
        {
            if(doc_to_consider>10)
                break;
            
                    temp = pseudoRelevanceFeedback(doc_id, queryExpansion);
		    int l=0;
		    for(int k=0;queryExpansion[k]!=null;k++)
		        l++;
		    for(int k=0;temp[k]!=null;k++)
		        queryExpansion[l++]=temp[k]; 
                    doc_to_consider++;
        }
        
        for(int k=0;queryExpansion[k]!=null;k++)
            newQuery=newQuery+" "+queryExpansion[k];
        
         newQuery+=query_term;   
        Map<String,Double> final_result=compute_BM25_score_for(newQuery,"Q"+query);
        
        final_result=sortByValue(final_result);
        
        
        
        File f3=new File("CACMResultsForBM25+PseudoRel\\Top100\\Q"+query+"-top100.txt");
        FileWriter fw=new FileWriter(f3);
        
        
        String to_file=to_TREC_format(final_result,"Q"+query);
        fw.write(to_file);
        
       fw.close();
        }
        
        
        
        
        
    }
    public static Map<String,Double> compute_BM25_score_for(String query,String q_id) throws IOException
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
        
        
          
       
       
           
        
        
        
        return sorted_docs;
        
    }
    public static String to_TREC_format(Map < String, Double > t, String Q_ID) {
   int rank = 1;
   String result = "";
   for (String doc: t.keySet()) {
    /*query_id Q0 doc_id rank CosineSim_score system_name*/ // <-----format 
    result+=" "+Q_ID+" "+doc+" "+rank+++" "+t.get(doc)+" GOOGLE_SERVER_#234\n";
    if(rank>100)
        break;
    //result += (rank++) + " " + t.get(doc) + " " + doc + "\n";
   }
   return result;
  }
    
    // STEP#4: SOrt The DOCUMENTS BY DOC-SCORE IN DESCENDING ORDER.
public static <K, V extends Comparable<? super V>> 
        Map<K, V> sortByValue(Map<K, V> map) {
    return map.entrySet().stream().sorted(Map.Entry.comparingByValue(Collections.reverseOrder())).collect(Collectors.toMap(
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
     try (FileInputStream fileIn = new FileInputStream("inoutforBM25pseudRel\\index.ser"); ObjectInputStream in = new ObjectInputStream(fileIn)) {
         index = (HashMap) in.readObject();
         FileInputStream fileIn1 = new FileInputStream("inoutforBM25pseudRel\\doc_len.ser");
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
         FileInputStream fileIn = new FileInputStream("inoutforBM25pseudRel\\query-test.ser");
         ObjectInputStream in = new ObjectInputStream(fileIn);
         test_queries = (HashMap<String,String>) in.readObject();
         in.close();
         fileIn.close();
      }catch(IOException i) {
         i.printStackTrace();
         
      }catch(ClassNotFoundException c) {
         System.out.println("query-test not found");
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
public static void fillStopwords()throws Exception{
		int counter=0;
		FileReader fr = new FileReader("inoutforBM25pseudRel\\common_words.txt");
		BufferedReader br = new BufferedReader(fr);
		Scanner sc = new Scanner(br);
		while(sc.hasNext())
			stopwords[counter++] = sc.next();
		sc.close();
	}

public static String[] pseudoRelevanceFeedback(String doc, String[] queryExpansion){
		HashMap <String, Long> termFrequency = new HashMap<String, Long>();
		String[] temp = new String[1000]; 
		int tempCounter=0;
		Iterator it1 = index.entrySet().iterator();
		while(it1.hasNext()){
			Map.Entry pair1 = (Map.Entry)it1.next();
			String term = (String)pair1.getKey();
			HashMap<String, Long> value = (HashMap<String, Long>)pair1.getValue();
			if(value.get(doc)==null) continue;
			else
				termFrequency.put(term, value.get(doc));
		}
		int i=0;
		termFrequency = (HashMap<String, Long>) sortByValue(termFrequency);
		Iterator it2 = termFrequency.entrySet().iterator();
		while(it2.hasNext() && tempCounter<100){
			int duplicate = 0;
			i++;
			Map.Entry pair2 = (Map.Entry)it2.next();
			String term2 = (String)pair2.getKey();
			for(int j=0;queryExpansion[j]!=null;j++)
				if(term2.equals(queryExpansion[j]))
					duplicate=1;
			for(int j=0;stopwords[j]!=null;j++)
				if(term2.equals(stopwords[j]))
					duplicate=1;
			if(duplicate==0)
				temp[tempCounter++]=term2;
		}
		return temp;
	}
    
    
}
