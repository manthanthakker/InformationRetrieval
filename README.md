# SearchEngine
Web Application fetching real time news by crawling the top news websites. 
An overview of the architecture / Process is as follows: 
![alt text](https://github.com/manthanthakker/NewsEngine/blob/master/Red%20Blue%20Simple%20Stripes%20Flowchart.png)

![image](https://user-images.githubusercontent.com/19961100/185777585-52b9f872-5a9b-4efd-a7af-ebd47f5a6de5.png)
![image](https://user-images.githubusercontent.com/19961100/185777598-f602e4e2-b5a7-42aa-9b7e-b7c2aa01528e.png)


## Information Retrieval & Search Engine
The above techniques are explained as follows, including TF-IDF, Cosine Distance, Lucene, BM25 Algorithm, BM25+StopWords, BM25+PseudoRelevance feedback, and BM25+StopWords+PseudoRelevance feedback.

### TF-IDF
TF-IDF, or Term Frequency–Inverse Document Frequency, is a numerical statistic used to measure the importance of a word in a document or corpus. It is calculated as the product of two terms—the term frequency and the inverse document frequency. The term frequency is the number of times a word appears in a document, and the inverse document frequency is the number of documents in the corpus divided by the number of documents containing the word. The higher the TF-IDF value, the more important the word is in the document.

### Cosine Distance
Cosine distance is a measurement of similarity between two vectors. It is calculated as the cosine of the angle between the two vectors, and ranges from 0 (no similarity) to 1 (exact match). It is commonly used to measure the similarity between documents.

### Lucene
Lucene is an open-source search engine library written in Java. It enables users to quickly and efficiently search large volumes of text. It is used in many popular search engines, including Elasticsearch and Apache Solr.

### BM25 Algorithm
The BM25 algorithm is a ranking function used in information retrieval. It is used to rank documents based on their relevance to a given query. The BM25 algorithm uses several parameters, including a document's term frequency, inverse document frequency, and the length of the query.

### BM25+StopWords
BM25+StopWords is an extension of the BM25 algorithm that takes into account the words in the query that are considered "stop words". Stop words are words that are common and are not likely to be relevant to the query. By taking stop words into account, the BM25+StopWords algorithm can more accurately rank documents for a given query.

### BM25+PseudoRelevance feedback
PseudoRelevance feedback is a technique used in information retrieval to improve the accuracy of a search query by taking into account the relevance of documents that are already retrieved. BM25+PseudoRelevance
