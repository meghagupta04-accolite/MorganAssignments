
package com.accolite.test.morgan.stanley.list;

import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;


/**
 * The Class Node.
 */
class Node implements Comparable{
	/** The data. */
	String data;

	/** The next. */
	Node next;

	/**
	 * Instantiates a new node.
	 *
	 * @param data
	 *            the data
	 */
	public Node(String data) {
		super();
		this.data = data;
		this.next = null;
	}


	public int compareTo(Object o)
	{
		int num1 = Integer.parseInt(this.data.substring(1,this.data.length()));
		int num2 = Integer.parseInt(((Node)o).data.substring(1,((Node)o).data.length()));
		if(num1 == num2) return 0;
		else if(num1 > num2) return 1;
		else return -1;
	}
}

/**
 * The Class ListPairs.
 */

public class ListPairs {
	Node head;

	/**
	 *add new node.
	 *
	 * @param data
	 *            the data
	 */
	private void addNewNode(String data){
		Node newNode = new Node(data);
		if(head!=null){
			Node temp = head;
			while(temp.next!=null)
				temp=temp.next;
			temp.next = newNode;
		}
		else
			head=newNode;
	}

	/**
	 *add node.
	 *
	 * @param node
	 *            the node
	 */
	private void addNode(Node node){
		node.next=null;
		if(head!=null){
			Node temp = head;
			while(temp.next!=null)
				temp=temp.next;
			temp.next = node;
		}
		else
			head=node;
	}


	/**
	 *print list.
	 */
	private void printList(){
		if(head == null) return;
		Node temp = head;
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
	private  void createPairs(ListPairs list) {
		Node temp = head;

		/*
		 * Divide the input list into 2 tree sets - a and b. 
		 * TreeSet chosen so that data is entered in a sorted form only
		 * 
		 */
		Set<Node> aSet = new  TreeSet();
		Set<Node> bSet = new  TreeSet();

		/* adding data to respective sets */
		while(temp!=null){
			if(temp.data.startsWith("a"))
				aSet.add(temp);
			else  bSet.add(temp);
			temp = temp.next;
		}

		/* creating an output paired list from two sets formed above*/		
		ListPairs outputList = new ListPairs();

		Iterator<Node> aIterator = aSet.iterator();
		Iterator<Node> bIterator = bSet.iterator();

		while(aIterator.hasNext() && bIterator.hasNext()){
			outputList.addNode(aIterator.next());
			outputList.addNode(bIterator.next());
		}
		System.out.println("The output paired list is : ");
		outputList.printList();

	}

	/**
	 * @param args
	 */
	public static void main(String args[]){
		ListPairs list = new ListPairs();
		System.out.println("Enter space separated input values for a list(Assumption even number of inputs  - a's  and b's pairs)");
		Scanner scan = new Scanner(System.in);
		String input = scan.nextLine();
		String[] listData = input.split(" ");

		for(String data : listData)
			list.addNewNode(data);

		/*print input*/
		System.out.println("The input list entered is : ");
		list.printList();

		list.createPairs(list);	

	}

}
