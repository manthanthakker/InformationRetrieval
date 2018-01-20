/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package makeinvertedindex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 *
 * @author manth
 */
/////// UNIGRAM

public class MakeInvertedIndex {

    /**
     * @param args the command line arguments
     */
    public static String file_name[]=new String[1000];
    public static HashMap<String,HashMap> index=new HashMap();
    public static HashMap<String,Integer> entry_in_index=new HashMap(); 
    public static HashMap<String,Integer> word_tf=new HashMap();
    public static HashMap<String,HashMap> modified_index=new HashMap();
    public static HashMap<String,Integer> entry_in_modified_index=new HashMap();
    public static ArrayList<String> vocab=new ArrayList();
    
    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
                
               
                get_file_names();
                int d=0;
                
                File tab2=new File("table2-unigram.txt");
                FileWriter table2=new FileWriter(tab2);
               
                for(int i=0;i<1000;i++)
                {
                    File f1=new File("corpus\\"+file_name[i]+".txt");
                    Scanner sc1=new Scanner(f1);
                    while(sc1.hasNext())
                    {
                        String s4=sc1.next();
                        String process_word=s4;
                        if(index.get(process_word)==null)
                        {
                            HashMap new_entry=new HashMap();
                            new_entry.put(file_name[i],1);
                            index.put(process_word,new_entry);
                            d++;  
                        }
                        else
                        {
                            HashMap<String,Integer> inverted_list=index.get(process_word);
                            if(inverted_list.get(file_name[i])==null)
                            {
                                
                                inverted_list.put(file_name[i],1);
                            }
                            else
                            {
                                inverted_list.put(file_name[i],(inverted_list.get(file_name[i])+1));
                            }    
                        }
                    }
                }
                // new code
                 for(int i=0;i<1000;i++)
                {
                    File f1=new File("corpus\\"+file_name[i]+".txt");
                    Scanner sc1=new Scanner(f1);
                   // modified_index.put();
                   HashMap<String,Integer> vector=new HashMap();
                    while(sc1.hasNext())
                    {
                        String s4=sc1.next();
                        String process_word=s4;
                        if(vector.get(process_word)==null)
                        {
                            HashMap new_entry=new HashMap();
                            //new_entry.put(file_name[i],1);
                            vector.put(process_word,1);
                              
                        }
                        else
                        {
                            int count=vector.get(process_word);
                            vector.put(process_word,count+1);
                            
                        }
                    }
                    modified_index.put(file_name[i],vector);
                }
                 System.out.println(modified_index.toString());
                // end of new code
                
                   String to_file="";
                 Object[] keys = index.keySet().toArray();
                 Arrays.sort(keys);
               
                 HashMap<String,Integer> temp;
                    for(Object name1 : keys) {
                         String name=name1.toString();

                      
                     
                           temp=index.get(name);
                            int count=0;
                            //System.out.print("TERM: "+name+" ");
                            vocab.add(name);
                            String docs="";
                  /*for(String doc:temp.keySet())
                     {
                         int count_of=temp.get(doc);
                            count+=count_of;
                       
                        
                                //System.out.print(doc+" || ");
                               
                               // File f3=new File("C:\\Users\\manth\\Desktop\\hw4\\corpus\\"+doc+".txt");
                
                                //int TF_in_this_doc=number_of_occurences(name,f3);
               
                                 //docs+=doc+" "+TF_in_this_doc+" ";
            
                      }*/
                  //System.out.println(temp);
                  count=temp.size();
                 // System.out.print(" DF: "+temp.size());
                 // System.out.print(" "+ docs );
                //  System.out.println();
                    
                       word_tf.put(name,count);

              } 
              System.out.println("vocab size is: "+vocab.size());
               to_file=index.toString();
                
               
               // System.out.println(to_file);
                table2.write(to_file);
                sort_and_display();
                 
                System.out.println(d+" words");
        
    }
    public static void make_modified_index()
    {
        for(int i=0;i<1000;i++)
        {
            
        }
    }
    public static int number_of_occurences(String term, File f) throws FileNotFoundException
    {
        Scanner sc=new Scanner(f);
        int count=0;
        while(sc.hasNext())
        {
            if(sc.next().equals(term))
                count++;
        }
        return count;
    }
    public static String merge(String arg1 , String arg2, String arg3)
    {
        return arg1+arg2+arg3;
    }
    public static void get_file_names() throws FileNotFoundException
    {
        File f=new File("new_files1.txt");
        Scanner sc=new Scanner(f);
        
        int p=0;
        while(sc.hasNext())
        {
            file_name[p]=sc.next();
           
            p++;
        }
    }
     public static void sort_and_display() throws IOException, InterruptedException
    {
      
        ValueComparator bvc = new ValueComparator(word_tf);
        TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);
        File tab1=new File("table1-unigram.txt");
        FileWriter table1=new FileWriter(tab1);
        sorted_map.putAll(word_tf);
        String to_file="";
        
       /*for (String key : sorted_map.keySet()) {
        System.out.println(" TERM : "+ key +"  DF: "+word_tf.get(key)+" ");
      
       
        }*/ 
       
        to_file=sorted_map.toString();
        table1.write(to_file);
        Thread.sleep(5000);
        table1.close();
    }
    
}

 class ValueComparator implements Comparator<String> {
    Map<String, Integer> base;

    public ValueComparator(Map<String, Integer> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with
    // equals.
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}

