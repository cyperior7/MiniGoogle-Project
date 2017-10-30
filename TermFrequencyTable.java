public class TermFrequencyTable {
    
    private class Node {
        String term;
        int[] termFreq = new int[2];
        Node next;
        Node next2;
        
        Node(String term, Node n, Node p) {
            this.term = term;
            this.next = n;
            this.next2 = p;
        }
    }
    
       private final int SIZE = 101;
       public Node[] T = new Node[SIZE];
       private int length = 0;
       private Node head = null;
       
       //inserts a term into the TermFrequencyTable if it is not there, or increments the termFreq array otherwise
       public void insert (String term, int docNum) {
           if (!member(term)) {
               Node newTerm = new Node(term, null, head);
               head = newTerm;
               newTerm.termFreq[docNum] = 1;
               T[hash(term, SIZE)] = insertHelper(T[hash(term, SIZE)], newTerm);  
               length += 1;
           }
           else {
               lookup(term).termFreq[docNum] += 1;
           }
       }
       
       private Node insertHelper (Node n, Node newTerm) {
           if (n == null) {
               return newTerm;
           }
           else if (n.next == null) {
               n.next = newTerm;
               return n;
           }
           else {
               return insertHelper(n.next, newTerm);
           }         
       }
       
       //Checks if the term is a member
       public boolean member(String key) {
           return (lookup(key) != null); 
       }
       
       // returns the node with the given terme or null if not found
       public Node lookup(String key) {  
           return lookupHelper(T[hash(key, SIZE)], key);
       }
   
       private Node lookupHelper(Node n, String key) {
           if (n == null) {
               return null;
           }
           else if (n.term.compareTo(key) == 0) {
               return n;
           }
           else {
               return lookupHelper(n.next, key);
           }
       }
       
       //returns the cosine similarity between two documents
       double cosineSimilarity() {
           
           int[] A = new int[length];
           int[] B = new int[length];
           
           A = insertTermFreqs(A, 0);
           B = insertTermFreqs(B, 1);

           double similarity= 0;
           similarity = (dotProduct(A, B)) / ((Math.sqrt(dotProduct(A, A))) * (Math.sqrt(dotProduct(B,B))));
           
           return similarity;
       }
       
       //fills an array with its termFrequency values
       private int[] insertTermFreqs(int[] freqArray, int docNum) {
           Node n = head;
           for (int i = 0; i < freqArray.length; i++) {
               freqArray[i] += n.termFreq[docNum];
               n = n.next2;
           }
           return freqArray;
       }
       
       //returns the dot product between two arrays
       private double dotProduct(int[] A, int[] B) {
           double sum = 0;
           for (int i = 0; i < A.length; i++) {
               sum += (B[i] * A[i]);
           }
           return sum;
       }
       
       //Instructed to use from the website http://research.cs.vt.edu/AVresearch/hashing/strings.php
       //nFolding method to hash a string
       private static int hash(String s, int M) {
       
           int intLength = s.length() / 4;
           int sum = 0;
           for (int j = 0; j < intLength; j++) {
               char c[] = s.substring(j * 4, (j * 4) + 4).toCharArray();
               long mult = 1;
               for (int k = 0; k < c.length; k++) {
                   sum += c[k] * mult;
                   mult *= 256;
               }
           }
           char c[] = s.substring(intLength * 4).toCharArray();
           long mult = 1;
           for (int k = 0; k < c.length; k++) {
               sum += c[k] * mult;
               mult *= 256;
           }
           
           return(Math.abs(sum) % M);
       }
       
       public static void main(String[] args) {

           //Testing cosineSimilarity
           TermFrequencyTable A = new TermFrequencyTable();

           A.insert("A", 0);
           A.insert("B", 0);
           A.insert("A", 1);
           A.insert("A", 1);
           A.insert("B", 1);
           A.insert("B", 1);

           System.out.println("Testing cosineSimilarity. Should print:\n1.0");
           System.out.println(A.cosineSimilarity());
           
           TermFrequencyTable B = new TermFrequencyTable();

           B.insert("A", 0);
           B.insert("B", 0);
           B.insert("C", 1);
           B.insert("D", 1);

           System.out.println("\nTesting cosineSimilarity. Should print:\n0.0");
           System.out.println(B.cosineSimilarity());
           
           TermFrequencyTable C = new TermFrequencyTable();

           C.insert("CS112", 0);
           C.insert("HW10", 0);
           C.insert("CS112", 1);
           C.insert("HW10", 1);
           C.insert("HW10", 1);

           System.out.println("\nTesting cosineSimilarity. Should print:\n0.9487");
           System.out.println(C.cosineSimilarity());
           
                
       }
                                  
}