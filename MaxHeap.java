/* File MaxHeap.java
 * Author: Wayne Snyder (most of it, Article edits made by Justin Ingwersen justini@bu.edu)
 * Date: 4/20/2016
 */ 

class MaxHeap {
   
   private final int SIZE = 10;       // initial length of array
   private int next = 0;              // limit of elements in array
   private Article[] A = new Article[SIZE];   // implements tree by storing elements in level order
   
   // standard resize to avoid overflow
   
   private void resize() {
      Article[] B = new Article[A.length*2];
      for(int i = 0; i < A.length; ++i)
         B[i] = A[i];
      A = B; 
   }
   
   // methods to move up and down tree as array
   
   private int parent(int i) { return (i-1) / 2; }
   private int lchild(int i) { return 2 * i + 1; }
   private int rchild(int i) { return 2 * i + 2; }
   
   private boolean isLeaf(int i) { return (lchild(i) >= next); }
   private boolean isRoot(int i) { return i == 0; }
   
   // standard swap, using indices in array
   private void swap(int i, int j) {
      Article temp = A[i];
      A[i] = A[j];
      A[j] = temp;
   }
   
   // basic data structure methods
   
   public boolean isEmpty() {
      return (next == 0);
   }
   
   public int size() {
      return (next);
   }
   
   // insert an integer into array at next available location
   //    and fix any violations of heap property on path up to root
   
   public void insert(Article a) {
      if(size() == A.length) resize(); 
      A[next] = a; 
      
      int i = next;
      int p = parent(i); 
      while(!isRoot(i) && A[i].getCS() > A[p].getCS()) {
         swap(i,p);
         i = p;
         p = parent(i); 
      }
      
      ++next;
   }
   
   
   // Remove top (maximum) element, and replace with last element in level
   //    order; fix any violations of heap property on a path downwards
   
   public Article getMax() {
      --next;
      swap(0,next);                // swap root with last element
      int i = 0;                   // i is location of new key as it moves down tree
 
      // while there is a maximum child and element out of order, swap with max child
      int mc = maxChild(i);
      while(!isLeaf(i) && A[i].getCS() < A[mc].getCS()) { 
         swap(i,mc);
         i = mc; 
         mc = maxChild(i);
      }
      
 ///     printHeapAsTree(); 
      
      return A[next];
   }
   
   // return index of maximum child of i or -1 if i is a leaf node (no children)
   
   int maxChild(int i) {
      if(lchild(i) >= next)
         return -1;
      if(rchild(i) >= next)
         return lchild(i);
      else if(A[lchild(i)].getCS() > A[rchild(i)].getCS())
         return lchild(i);
      else
         return rchild(i); 
   }
   
   // Apply heapsort to the array A. To use, fill A with keys and then call heapsort
   
   public  void heapSort() {
      next = 0;
      for(int i = 0; i < A.length; ++i)      // turn A into a heap
         insert(A[i]);
      for(int i = 0; i < A.length; ++i)      // delete root A.length times, which swaps max into
         getMax();                           //  right side of the array
   }
   
   // debug method
   
   private void printHeap() {
      for(int i = 0; i < A.length; ++i)
         System.out.print(A[i] + " ");
      System.out.println("\t next = " + next);
   }
   
   private void printHeapAsTree() {
      printHeapTreeHelper(0, ""); 
      System.out.println(); 
   }
   
   private void printHeapTreeHelper(int i, String indent) {
      if(i < next) {
         
         printHeapTreeHelper(rchild(i), indent + "   "); 
         System.out.println(indent + A[i]);
         printHeapTreeHelper(lchild(i), indent + "   "); 
      }
   }
   
}