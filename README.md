# MorganAssignments
Contains solutions for finding longest line in a very large file and form a's and b's pairs in a linkedlist 

Steps to run :
1-	FileSearchString 
Find the line containing maximum number of words. 

1.	Run Util.java -> It creates the 1GB input file. 
2.	Run any of the four files stated below:

There are 2 solutions for this problem.
I.	SingleThreadSearch - It uses only 1 thread to search for the longest line.
II.	MultipleThreadSearch - It uses 4 threads to process the file.
Also, there can be multiple lines with the maximum no. of words.
To get all the lines with the max words these are the corresponding files : 
iii.  SingleThreadMultipleSearch 
iv.  MultipleThreadMultipeSearch


2- Modify the LinkedList to get paired list of a's and b's
Given a singly linkedlist containing a's and b's in any order,  output a linkedlist which contains pairs of shortest a mapped to shortest b.
for eg: 
Input : a1->b3->a5->a7->b2->b45
Output : a1->b2->a5->b3->a7->b45

I have given two solutions:
1-  List Pair
This takes the input list and stores all the a's into a TreeSet and all the b's in another TreeSet.
Then it merges these two TreeSets into a LinkedList.

2- ListPairsBySort (Inplace pair forming)
This first sorts the input list to contain all the sorted  a's first and then the sorted b's.
Then it creates the pairs by swapping the nodes inplace.
