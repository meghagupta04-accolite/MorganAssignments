
package com.accolite.test.morgan.stanley.list;

import java.util.Scanner;

/**
 * The Class LLNode.
 */
class LLNode {
	/** The data. */
	String data;

	/** The next. */
	LLNode next;

	/**
	 * Instantiates a new node.
	 *
	 * @param data
	 *            the data
	 */
	public LLNode(String data) {
		super();
		this.data = data;
		this.next = null;
	}


	public int compareTo(Object o)
	{   
		char s1 = this.data.charAt(0);
		char s2 = ((LLNode)o).data.charAt(0);
		if(s1 == s2){
			int num1 = Integer.parseInt(this.data.substring(1,this.data.length()));
			int num2 = Integer.parseInt(((LLNode)o).data.substring(1,((LLNode)o).data.length()));
			if(num1 == num2) return 0;
			else if(num1 < num2) return 1;
			else  return -1;	
		}
		else if(s1 < s2) return 1;
		else return -1;

	}
}

/**
 * The Class ListPairs.
 */

public class ListPairsBySort {
	LLNode head;

	/**
	 *add new LLNode.
	 *
	 * @param data
	 *            the data
	 */
	private void addNewNode(String data){
		LLNode newNode = new LLNode(data);
		if(head!=null){
			LLNode temp = head;
			while(temp.next!=null)
				temp=temp.next;
			temp.next = newNode;
		}
		else
			head = newNode;
	}

	/**
	 *print list.
	 */
	private void printList(){
		if(head == null) return;
		LLNode temp = head;
		while(temp.next!=null){
			System.out.print(temp.data+"->");
			temp=temp.next;
		}
		System.out.print(temp.data);
		System.out.println();
	}

	/**
	 *create pairs.
	 *
	 * @param list
	 *            the list
	 */
	private  LLNode createPairs(ListPairsBySort list) {
		LLNode temp = head;
		/* adding data to respective sets */

		LLNode mid = getMiddle(head);

		LLNode head1 = head;
		LLNode head2 = mid.next;
		LLNode temp1 = null;
		LLNode temp2 = null;

		mid.next=null;
		do{
			temp1 = head1.next;
			temp2 = head2.next;
			head1.next = head2;
			head2.next = temp1;
			head1 = temp1;
			head2 = temp2;
		}
		while(head1 != null );

		return head;
	}

	/**
	 * @param args
	 */
	public static void main(String args[]){
		ListPairsBySort list = new ListPairsBySort();
		System.out.println("Enter space separated input values for a list(Assumption even number of inputs  - a's  and b's pairs)");
		Scanner scan = new Scanner(System.in);
		String input = scan.nextLine();
		String[] listData = input.split(" ");

		for(String data : listData)
			list.addNewNode(data);

		/*print input*/
		System.out.println("The input list entered is : ");
		list.printList();

		/*sort the linked list to contain consecutive a's and then consecutive b's*/
		list.head = list.mergeSort(list.head);
		System.out.println("The sorted list is : ");
		list.printList();

		/* output list*/
		list.head = list.createPairs(list);
		System.out.println("The sorted pair ist is : ");
		list.printList();

	}



	LLNode mergeSort(LLNode h) 
	{
		// Base case : if head is null
		if (h == null || h.next == null)
		{
			return h;
		}

		// get the middle of the list
		LLNode middle = getMiddle(h);
		LLNode nextofmiddle = middle.next;

		// set the next of middle Node to null
		middle.next = null;

		// Apply mergeSort on left list
		LLNode left = mergeSort(h);

		// Apply mergeSort on right list
		LLNode right = mergeSort(nextofmiddle);

		// Merge the left and right lists
		LLNode sortedlist = sortedMerge(left, right);
		return sortedlist;
	}

	LLNode getMiddle(LLNode h) 
	{
		//Base case
		if (h == null)
			return h;
		LLNode fastptr = h.next;
		LLNode slowptr = h;

		// Move fastptr by two and slow ptr by one
		// Finally slowptr will point to middle node
		while (fastptr != null)
		{
			fastptr = fastptr.next;
			if(fastptr!=null)
			{
				slowptr = slowptr.next;
				fastptr=fastptr.next;
			}
		}
		return slowptr;
	}

	LLNode sortedMerge(LLNode a, LLNode b) 
	{
		LLNode result = null;
		/* Base cases */
		if (a == null)
			return b;
		if (b == null)
			return a;

		/* Pick either a or b, and recur */
		if (a.compareTo(b) == 1) 
		{
			result = a;
			result.next = sortedMerge(a.next, b);
		} 
		else
		{
			result = b;
			result.next = sortedMerge(a, b.next);
		}
		return result;

	}

}
