/*
 * Minigoogle.java
 *
 * A client program that uses the DatabaseIterator
 * and Article classes, along with additional data
 * structures, to allow a user to create, modify
 * and interact with a encyclopedia database.
 *
 * Author: Justin Ingwersen (justini@bu.edu)
 * Date: April 20, 2016
 */

import java.util.*;


public class MiniGoogle {
  
  private static Article[] getArticleList(DatabaseIterator db) {
    
    // count how many articles are in the directory
    int count = db.getNumArticles(); 
    
    // now create array
    Article[] list = new Article[count];
    for(int i = 0; i < count; ++i)
      list[i] = db.next();
    
    return list; 
  }
  
  private static DatabaseIterator setupDatabase(String path) {
    return new DatabaseIterator(path);
  }
  
  private static void addArticle(Scanner s, ArticleTable D) {
    System.out.println();
    System.out.println("Add an article");
    System.out.println("==============");
    
    System.out.print("Enter article title: ");
    String title = s.nextLine();
    
    System.out.println("You may now enter the body of the article.");
    System.out.println("Press return two times when you are done.");
    
    String body = "";
    String line = "";
    do {
      line = s.nextLine();
      body += line + "\n";
    } while (!line.equals(""));
    
    D.insert(new Article(title, body));
  }
  
  
  private static void removeArticle(Scanner s, ArticleTable D) {
    System.out.println();
    System.out.println("Remove an article");
    System.out.println("=================");
    
    System.out.print("Enter article title: ");
    String title = s.nextLine();
    
    
    D.delete(title);
  }
  

  private static void titleSearch(Scanner s, ArticleTable D) {
    System.out.println();
    System.out.println("Search by article title");
    System.out.println("=======================");
    
    System.out.print("Enter article title: ");
    String title = s.nextLine();
    
    Article a = D.lookup(title);
    if(a != null)
      System.out.println(a);
    else {
      System.out.println("Article not found!"); 
      return; 
    }
    
    System.out.println("Press return when finished reading.");
    s.nextLine();
  }
  
  private static void phraseSearcher(Scanner s, ArticleTable D) {
      System.out.println();
      System.out.println("Search by article content");
      System.out.println("=========================");
      
      System.out.println("Enter search phrase: ");
      String phrase = s.nextLine();
      
      System.out.println(phraseSearch(phrase, D));
      
  }
  
  //Searches through the database and returns the top 3 matches in articles based on cosine similarity
  public static String  phraseSearch(String phrase, ArticleTable T) {
      
      String text = "";
      MaxHeap heap = new MaxHeap();
      
      T.reset();
      while(T.hasNext()) {
          Article a = T.next();
          
          double sim = getCosineSimilarity(phrase, a.getBody());
          
          if (sim > 0.001) {
              a.putCS(sim);
              heap.insert(a);
          }
      }
      
      if (heap.size() == 0) {
          text = "No matching articles found!";
      }
      else {
          if (heap.size() == 1) {
              text += "Top Match:\n\n";
              text += "Match 1 with cosine similarity of ";
              Article a = heap.getMax();
              text += a.getCS();
              text += ":\n\n";
              text += a;   
          }
          else if (heap.size() == 2) {
              text += "Top 2 Matches:\n\n";
              text += "Match 1 with cosine similarity of ";
              Article a = heap.getMax();
              text += a.getCS();
              text += ":\n\n";
              text += a; 
              text += "Match 2 with cosine similarity of ";
              a = heap.getMax();
              text += a.getCS();
              text += ":\n\n";
              text += a; 
          }
          else {
              text += "Top 3 Matches:\n\n";
              text += "Match 1 with cosine similarity of ";
              Article a = heap.getMax();
              text += a.getCS();
              text += ":\n\n";
              text += a; 
              text += "Match 2 with cosine similarity of ";
              Article b = heap.getMax();
              text += b.getCS();
              text += ":\n\n";
              text += b; 
              text += "Match 3 with cosine similarity of ";
              Article c = heap.getMax();
              text += c.getCS();
              text += ":\n\n";
              text += c; 
          }
      }
      return text;
  }
  
  //creates two tokenizer classes and returns the cosine similarity between the two strings
  private static double getCosineSimilarity(String s, String t) {
      
      String fixedS = preprocess(s);
      String fixedT = preprocess(t);
      
      StringTokenizer sTerms = new StringTokenizer(fixedS);
      StringTokenizer tTerms = new StringTokenizer(fixedT);
      
      TermFrequencyTable termFreq = new TermFrequencyTable();
      
      //insert s's terms into the frequency table
      while(sTerms.hasMoreTokens()) {
          String word = sTerms.nextToken();
          if (!blacklisted(word)) {
              termFreq.insert(word, 0);
          }
      }
      
      //insert t's terms into the frequency table
      while(tTerms.hasMoreTokens()) {
          String word = tTerms.nextToken();
          if (!blacklisted(word)) {
              termFreq.insert(word, 1);
          }
      }
      
      return termFreq.cosineSimilarity();
      
  }
  
  //processes the string and makes all letters lowercase, keeps the digits, and keeps whitespace
  
  private static String preprocess(String s) {
      String newWord = "";
      for (int i = 0; i < s.length(); i++) {
          //is it lowercase
          if (Character.isLowerCase(s.charAt(i))) {
              newWord += s.charAt(i);
          }
          //is it whitespace
          else if (Character.isWhitespace(s.charAt(i))) {
              newWord += s.charAt(i);
          }
          //is it a digit
          else if ((int)s.charAt(i) >= 47 && (int)s.charAt(i) <= 57) {
              newWord += s.charAt(i);
          }
          //check if it was a capital letter, if so make lowercase and add, if not don't add anything
          else {
              int charNumber = (int)s.charAt(i);
              if (charNumber >= 65 && charNumber <= 90) {
                  newWord += Character.toLowerCase(s.charAt(i));
              }
          }
      }
      return newWord;
  }
  
  private static final String [] blackList = { "the", "of", "and", "a", "to", "in", "is", 
    "you", "that", "it", "he", "was", "for", "on", "are", "as", "with", 
    "his", "they", "i", "at", "be", "this", "have", "from", "or", "one", 
    "had", "by", "word", "but", "not", "what", "all", "were", "we", "when", 
    "your", "can", "said", "there", "use", "an", "each", "which", "she", 
    "do", "how", "their", "if", "will", "up", "other", "about", "out", "many", 
    "then", "them", "these", "so", "some", "her", "would", "make", "like", 
    "him", "into", "time", "has", "look", "two", "more", "write", "go", "see", 
    "number", "no", "way", "could", "people",  "my", "than", "first", "water", 
    "been", "call", "who", "oil", "its", "now", "find", "long", "down", "day", 
    "did", "get", "come", "made", "may", "part" }; 
  
  //returns true if the key is in the black list of words not able to be added to the table
  private static boolean blacklisted(String s) {
      for (int i = 0; i < blackList.length; i++) {
          if (blackList[i].compareTo(s.toLowerCase()) == 0) {
              return true;
          }
      }
      return false;
  }
  

  public static void main(String[] args) {
    Scanner user = new Scanner(System.in);
    
    String dbPath = "articles/";
    
    DatabaseIterator db = setupDatabase(dbPath);
    
    System.out.println("Read " + db.getNumArticles() + 
                       " articles from disk.");
    
    ArticleTable L = new ArticleTable();
    Article[] A = getArticleList(db);
    L.initialize(A);
    
    int choice = -1;
    do {
      System.out.println();
      System.out.println("Welcome to Mini-Google!");
      System.out.println("=====================");
      System.out.println("Make a selection from the " +
                         "following options:");
      System.out.println();
      System.out.println("Manipulating the database");
      System.out.println("-------------------------");
      System.out.println("    1. add a new article");
      System.out.println("    2. remove an article");
      System.out.println();
      System.out.println("Searching the database");
      System.out.println("----------------------");
      System.out.println("    3. search by exact article title");
      System.out.println("    4. search by phrase (list of keywords)");
      System.out.println();
      
      System.out.print("Enter a selection (1-4, or 0 to quit): ");
      
      choice = user.nextInt();
      user.nextLine();
      
      switch (choice) {
        case 0:
          return;
          
        case 1:
          addArticle(user, L);
          break;
          
        case 2:
          removeArticle(user, L);
          break;
          
        case 3:
          titleSearch(user, L);
          break;
         
        case 4:
          phraseSearcher(user, L);
          break;
              
        default:
          break;
      }
      
      choice = -1;
      
    } while (choice < 0 || choice > 4);
    
  }
  
  
}
