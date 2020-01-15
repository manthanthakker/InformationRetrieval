TOOLS USED:

1. NETBEANS
2. LUCENE 
3. JAVA 

HOW TO RUN IR PROJECT?

1. extract the zip folder SUBMISSION_IR_PROJECT
2. OPEN NETBEANS
3. CLICK ON THE OPEN PROJECT
4. NAVIGATE TO THE SUBMISSION_IR_PROJECT//source code of all search engines+ results//      
5. Select the IR project
6. NAVIGATE TO irproject.java from the project window
7. Run the project
8. You get a set of options to select the search engine
9. Select any search engine for running cacm 64 queries
10. After the search engine has completed processing it displays precision @ 5 and 10 for each query as well as MAP,MRR and number of documents whose relevance are taken into consideration for evaluation.
11. Along, with the metrics, a path is displayed where all the results query by query will be stored in a text file for viewing it. 'results-for-entire.txt' represents all the metrics that are particular to the entire run.

NOTE: 

1. The program only runs CACM_TEST_QUERIES.
IF USER WISHES TO ENTER A QUERY A 7 th option is made available 
'" SNIPPET GENERATION WITH BM25 " '
Top 5 results with their snippets (keywords highlighted by <b> </b>) are displayed to the user.

2. Lucene has not been included as it causes the user to import lucence libraries for a single search engine. Increasing overhead and more memory consumption. Therfore excecution of lucence search engine can be done by the code provided in the /SUBMISSION_IR_PROJECT//LUCENE_RECORD (DESIGN-CHOICE)

3. STEMMED-CORPUS : The code for Stemmed corpus is provided /SUBMISSION_IR_PROJECT/. Results are made available using BM25 engine. Since no evaluation not included in the user interface. 
