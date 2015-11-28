import java.io.*;
import java.util.*;

public class HuffmanEncode {

	CharNode overallRoot;
	FileReader output;

	/**
	 * @param object
	 *            The Text file to read
	 * @throws IOException
	 *             Throws exception if it's not readable
	 */
	public HuffmanEncode(File object) throws IOException {
		output = new FileReader(object);
	}

	/**
	 * Reads the filename and creates a File Object which will be used in a
	 * FileInputStream. This changes the program to encode in bytes rather than
	 * characters.
	 * 
	 * @param fileName
	 */
	public HuffmanEncode(String fileName) {

	}

	/**
	 * Reads the FileInputStream and counts the occurrence of every byte. Fills
	 * the PriorityQueue with the nodes that you create out of the bytes and
	 * occurrences. Builds the Huffman Tree and Traverses the Huffman Tree.
	 */
	public void encodeByteStream() {

		// The Traversal method should store the following info into a
		// HashMap<K, V> for every leaf node
		// Bytes as K (key) of type Integer (don't use Byte because of signed
		// problem)
		// Huffman Code as V (value) of type String
	}

	/**
	 * Create the compressed file
	 * 
	 * @param fileName
	 * @throws IOException 
	 */
	public void writeToFile(String fileName) throws IOException {
		long numberOfBytes; // Number of bytes in original file.
		// You cannot write long directly, you need to split that number into
		// bytes. Requirement: The highest significant byte first
		int numberOfSymbols; // Bytes found in the original file that got
								// encoded for each symbol you write
		byte symbolValue;
		byte codeLength; // the length of the "01010111" codeString for this
							// symbol
		// byte(s) codeBits // for each '0' a bit 0 and for each '1' a bit 1
		// set. The # of codeBits is determined by the codeLength (<=8 one byte,
		// >8 <=16 two bytes
		// etc.)
		
		// Create a type FileInputStream from the passed name of the file
		FileInputStream file = new FileInputStream(fileName);
		
		// Create the HashMap of the bytes of each character and their occurrences
		HashMap<byte[], Integer> valFrq = new HashMap<byte[], Integer>();
		int c;
		// While file has characters to read, convert the character into a byte[] and add to the HashMap while adding to the occurrences
		while((c = file.read()) != -1) {
			valFrq.putIfAbsent(byte[] c, 0);
			valFrq.replace(byte[] c, valFrq.get(byte[] c) + 1);
		}
		// Close the file reading
		file.close();
		
		// Push all the HashMap values into a priority Queue as new Character Nodes (CharNode)
		PriorityQueue<CharNode> queue = new PriorityQueue<CharNode>();
		
		// For each loop, every item in the HashMap, push into the priority Queue
		for (Map.Entry<byte[], Integer> item : valFrq.entrySet()) {
			
		}
		
		
	}

	/**
	 * A possible algorithm to fill up a byte bit by bit. When you call this
	 * method you need to keep track of how many bits are already filled in the
	 * first argument that you call that method with and make sure that the
	 * second argument’s length will not go over the 8 bits (hint: use
	 * substrings).
	 * 
	 * @param byteContainer
	 * @param code
	 * @return
	 */
	private int pushCharCodeIntoContainer(int byteContainer, String code) {

		int newContainer = byteContainer;

		for (int i = 0; i <= code.length() - 1; i++) {
			newContainer *= 2;
			if ('1' == code.charAt(i)) {
				newContainer += 1;
			}
		}
		return newContainer;
	}

	public void encode() throws IOException {
		HashMap<Character, Integer> valFreq = new HashMap<Character, Integer>();
		int c;
		while ((c = output.read()) != -1) {
			valFreq.putIfAbsent((char) c, 0);
			valFreq.replace((char) c, valFreq.get((char) c) + 1);
			// System.out.println(c + " " + valFreq.get((char) c));
		}
		output.close();

		// Push them all into a priority queue as new Character Nodes (CharNode)
		PriorityQueue<CharNode> queue = new PriorityQueue<CharNode>();
		for (Map.Entry<Character, Integer> item : valFreq.entrySet()) {
			queue.add(new CharNode(item.getKey(), null, null, item.getValue()));
		}

		// Go Highlander on the nodes in the priority queue by combining nodes
		// until there is only 1 (that will be the HuffTree)
		// THERE CAN BE ONLY ONE
		while (queue.size() > 1) {
			CharNode temp1 = queue.remove(); // pop out the two smallest value
												// nodes
			CharNode temp2 = queue.remove(); // and create a new node from the
												// two
			CharNode newNode = new CharNode(null, temp1, temp2, temp1.weight + temp2.weight);
			temp1.parent = temp2.parent = newNode;
			queue.add(newNode); // add it back into the pile
		}

		// Kick the last remaining node (the generated HuffmanTree) out of the
		// priorityQueue and save it as overallRoot
		CharNode overallRoot = queue.remove();

		// Create a character map and generate it's values from the HuffmanTree
		Map<Character, String> charMap = genMap(overallRoot);

		// Output the values from the generated character map
		// System.out.println(charMap.toString());

		// NEED TO PRINT OUT THE CHARACTER _ BINARYPATH _ # OF OCCURRENCES

		for (Character letter : charMap.keySet()) {
			System.out.print(letter + " " + charMap.get(letter) + " " + valFreq.get(letter));
			System.out.println();
		}
	}

	/**
	 * @param root
	 *            The current node the method is generating from
	 * @return The updated map with added character and binary path
	 */
	private Map<Character, String> genMap(CharNode root) {
		Map<Character, String> map = new HashMap<Character, String>();
		traversal(map, root, "");
		return map;
	}

	/**
	 * @param map
	 *            The map of characters and it's binary path in a String
	 * @param root
	 *            The current node this method is traversing
	 * @param path
	 *            The String for storing the binary path
	 */
	private void traversal(Map<Character, String> map, CharNode root, String path) {
		if (root.isLeaf()) {
			map.put(root.symbol, path);
		} else {
			traversal(map, root.leftChild, path + '0');
			traversal(map, root.rightChild, path + '1');
		}
	}

	/**
	 * @author Chris Mendoza
	 * 
	 *         Custom Character Node class for building the Huffman Tree.
	 *         Implements Comparable for the priorityQueue to use in
	 *         HuffmanEncode
	 *
	 */
	private class CharNode implements Comparable<CharNode> {

		private Character symbol; // char to be encoded, empty if combined node
		CharNode leftChild;
		CharNode rightChild;
		CharNode parent;
		Integer weight; // occurrence # of this char in the text

		/**
		 * Constructor
		 * 
		 * @param symbol
		 *            The character (a, b, c, etc)
		 * @param leftChild
		 *            The pointer to the left branch - NULL by default
		 * @param rightChild
		 *            The pointer to the right branch - NULL by default
		 * @param weight
		 *            The number of occurrences of this character
		 */
		public CharNode(Character symbol, CharNode leftChild, CharNode rightChild, Integer weight) {
			this.symbol = symbol;
			this.leftChild = leftChild;
			this.rightChild = rightChild;
			this.weight = weight;
		}

		/**
		 * @return Whether this node is a leaf by checking the left and right
		 *         nodes
		 */
		public boolean isLeaf() {
			return leftChild == null && rightChild == null;
		}

		public int getWeight() {
			return weight;
		}

		/*
		 * Checks the occurrences of this node against the passed node
		 */
		public int compareTo(CharNode o) {
			return this.weight - o.weight;
		}

	}
}
