import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

import static java.lang.Integer.toBinaryString;

public class HuffmanEncode {

    CharNode overallRoot;
    FileReader output;
    FileInputStream byteOutput;
    FileInputStream readOutput;
    private int leafCount;
    private int bitCounter;
    private int charCount;
    String thisFile;
    Map<Integer, String> encodeMap;

    /**
     * @param object The text file to read
     * @throws IOException Throws exception if it's not readable
     */
//    public HuffmanEncode(File object) throws IOException {
//        output = new FileReader(object);
//    }

    /**
     * Reads the filename and creates a File Object which will be used in a
     * FileInputStream. This changes the program to encode in bytes rather than
     * characters.
     *
     * @param fileName name of the text file to open
     * @throws FileNotFoundException throw exception if the file does not exist
     */
    public HuffmanEncode(String fileName) throws FileNotFoundException {
        byteOutput = new FileInputStream(fileName);
        thisFile = fileName;
    }

    /**
     * Reads the FileInputStream and counts the occurrence of every byte. Fills
     * the PriorityQueue with the nodes that you create out of bytes and
     * occurrences. Builds the Huffman Tree and Traverses the tree.
     *
     * @throws IOException
     */
    public void encodeByteStream() throws IOException {
        // The traversal method should store the following info into a
        // HashMap<K, V> for every leaf node
        // Bytes as K (key) of type Integer (don't use Byte because of a signed
        // problem)
        // Huffman code as V (value) of type String

        // Generate the initial HashMap of integer bytes and integer occurrences
        HashMap<Integer, Integer> byteMap = new HashMap<>();
        int c;
        while ((c = byteOutput.read()) != -1) {
            byteMap.putIfAbsent(c, 0);
            byteMap.replace(c, byteMap.get(c) + 1);
            //System.out.println("(" + c + ", " + byteMap.get(c) + ")");
        }
        byteOutput.close();

        // Push the HashMap values into a PriorityQueue as new character Nodes
        // (CharNode)
        PriorityQueue<CharNode> byteQueue = new PriorityQueue<>();
        for (Map.Entry<Integer, Integer> item : byteMap.entrySet()) {
            byteQueue.add(new CharNode(item.getKey(), null, null, item.getValue()));
        }

        // Combine the Nodes in the queue until only 1 remains. That is the
        // Huffman Tree
        while (byteQueue.size() > 1) {
            CharNode temp1 = byteQueue.remove(); // pop out the two smallest value
            // nodes
            CharNode temp2 = byteQueue.remove(); // and create a new node from the
            // two
            CharNode newNode = new CharNode(null, temp1, temp2, temp1.weight + temp2.weight);
            temp1.parent = temp2.parent = newNode;
            byteQueue.add(newNode); // add it back into the pile
        }

        // Remove the last remaining Node from the PriorityQueue and save it as overallRoot
        overallRoot = byteQueue.remove();

        // Create a byte map and generate it's binary values from the HuffmanTree
        encodeMap = genMap(overallRoot);

        // Output the generated values from the generated byte map
        //System.out.println(encodeMap.toString());

        writeToFile("output.hzip");
    }

    /**
     * Create the compressed file by writing the compressed values to a file
     *
     * @param fileName the name of the original file
     */
    public void writeToFile(String fileName) throws IOException {

        // Write the file header info (FHI). The FHI will be written byte by byte during creation to file.
        long numberOfBytes = overallRoot.getWeight(); // Number of bytes in the original file
        // The long cannot be written directly. Split that number into bytes.
        // REQUIREMENT: The highest significant byte first, continue with less
        // significant bytes, least significant, etc.
        int numberOfSymbols = leafCount; // # of different Bytes(Symbols) found in the original file that got encoded.
        // You cannot write to int directly. Split that number into bytes.
        // REQUIREMENT: The highest significant byte first, continue with less
        // significant bytes, least significant, for each symbol you write.

        System.out.println("\nNumber of Symbols: " + numberOfSymbols + "\n");
        byte symbolValue;
        byte codeLength; // the length of the "01010111" codeString for this symbol
        //byte(s) codebits; // for each '0', a bit 0 and for each '1', a bit 1 set. The # of codebits determines
        // the codeLength (<= one byte, >8 <= 16 two bytes, etc

        // ----------- START FILE OUTPUT ------------------------------------------
        // ------------------- START HEADER ---------------------------------------

        FileOutputStream outFile = new FileOutputStream(fileName); // open a new output stream

        System.out.println("The number of bytes in the original file: " + numberOfBytes + "\n");

        // ------------------- TEST OF BYTEBUFFER FOR GETTING BYTES -------------------

//        long testLong = 2000;
//        ByteBuffer bz = ByteBuffer.allocate(8);
//        bz.putLong(testLong);
//
//		byte[] result = bz.array();
//
//        result.toString();
//
//		for (byte b : result) {
//            if (b < 0) {
//                b = (byte) (b + 256);
//            }
//			System.out.println(b);
//		}

        // outFile.write(result);
        // -------------- END OF BYTEBUFFER TEST ------------------------------------

        // The first part of the header needs to be a byte representation of a long file
        // Do a manual conversion of a long into a byte representation.
        outFile.write(longToBytes(numberOfBytes));

        // Now write the byte information of the numberOfSymbols to the file
        byte bNumOfSym = (byte) numberOfSymbols;
//        System.out.println("The Number Of Symbols Signed Byte Value: " + bNumOfSym);
//        int b2 = bNumOfSym & 0xFF;
//        System.out.println("The corrected Number of Symbols Byte Value: " + b2);

        outFile.write(bNumOfSym);

        // ------------------- WRITE ENCODED BYTES TO OUTPUT -----------------------------
        // --- The order of insertion is the symbol value, it's length, and it's encoding ---
        int shift = 15;
        char b = 0;

        // Open the original file, read the contents and encode the file's contents
        readOutput = new FileInputStream(thisFile);
        int r;

            while ((r = readOutput.read()) != -1) {

                charCount++;

                // Read the current byte, and determine it's encoding from the ENCODEMAP
                // and write the output file based on the current symbol

                String code = encodeMap.get(r);
                codeLength = (byte) code.length();

                // OUTPUT THE SYMBOL BYTE VALUE
                symbolValue = (byte) r;
                outFile.write(symbolValue); // can only handle small character values. Needs to be updated
                                // to correct for signed bytes

                // OUTPUT THE SYMBOL LENGTH
                outFile.write(codeLength);

                // OUTPUT THE ENCODED VALUE
                // The for loop to write the binary of each binary path
                for (int i = 0; i < code.length(); i++) {
                    if (shift < 0) {
                        outFile.write(b); // Write the modified char b to the output file
                        shift = 15; // reset the shift to 7
                        b = 0; // reset the char to 0
                    }
                    char d = code.charAt(i); // get the character at the current index of the string
                    if (d == '1') { // if the 'bit' value is a 1,
                        b = (char) (b + (1 << shift)); // modify the b value by bit shifting in the 1
                    }
                    shift--; // increment down

                    System.out.println("The byte value of symbolValue: " + r);
                    System.out.println("Byte Code Length: " + codeLength);
                    System.out.println("The String output at this key: " + code);
                    System.out.println();
                    bitCounter += codeLength;

                } // END OF FOR LOOP

            }

        outFile.close();
        System.out.println("The number of bytes in the original file: " + numberOfBytes);
        System.out.println("The bytes from the encoding: " + bitCounter / 8);
        System.out.println("The number of characters encoded: " + charCount);


        // ----------------------- END HEADER -----------------------------
        // ----------- END FILE OUTPUT --------------------------------------------

        // After the header, the bit stream of the encoded bytes follow. Here is an example:
        // Assuming you have the following mapping (I use chars as symbols for simplicity)
        // 'T' '110100', 'h' '10111', 'e' '1001', ' ' '111', 'b' '011100' the foxtext.hzip file would start
        // right after the above described File Header Info with the following 3 bytes (Each block represents
        // a byte in the file):
        // 11010010   11110011   11011100

		/*
		Possible code

		 */

    }

    /**
     * Converts int into a byte array
     * @param a the int value to convert
     * @return the byte array
     */
    public static byte[] intToByteArray(int a)
    {
        return new byte[] {
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    /**
     * Converts long to byte in bitwise operations
     *
     * @param l the long to convert
     * @return the byte[] conversion of the long value
     */
    public static byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (l & 0xFF);
            l >>= 8;
        }
        return result;
    }

    /**
     * @param root The current node the method is generating from
     * @return The updated map with added character and binary path
     */
    private Map<Integer, String> genMap(CharNode root) {
        Map<Integer, String> map = new HashMap<>();
        traversal(map, root, "");
        return map;
    }

    /**
     * @param map  The map of characters and it's binary path in a String
     * @param root The current node this method is traversing
     * @param path The String for storing the binary path
     */
    private void traversal(Map<Integer, String> map, CharNode root, String path) {
        if (root.isLeaf()) {
            leafCount++;
            map.put(root.symbolValue, path);
        } else {
            traversal(map, root.leftChild, path + '0');
            traversal(map, root.rightChild, path + '1');
        }
    }

    /**
     * @author Chris Mendoza
     *         <p>
     *         Custom Character Node class for building the Huffman Tree.
     *         Implements Comparable for the priorityQueue to use in
     *         HuffmanEncode
     */
    private class CharNode implements Comparable<CharNode> {

        CharNode leftChild;
        CharNode rightChild;
        CharNode parent;
        Integer weight; // occurrence # of this char in the text
        // private Character symbol; // char to be encoded, empty if combined node
        private Integer symbolValue;


//		public CharNode(Character symbol, CharNode leftChild, CharNode rightChild, Integer weight) {
//			this.symbol = symbol;
//			this.leftChild = leftChild;
//			this.rightChild = rightChild;
//			this.weight = weight;
//		}

        /**
         * Constructor
         *
         * @param byteValue  The character (a, b, c, etc)
         * @param leftChild  The pointer to the left branch - NULL by default
         * @param rightChild The pointer to the right branch - NULL by default
         * @param weight     The number of occurrences of this character
         */
        public CharNode(Integer byteValue, CharNode leftChild, CharNode rightChild, Integer weight) {
            this.symbolValue = byteValue;
            this.leftChild = leftChild;
            this.rightChild = rightChild;
            this.weight = weight;
        }

        /**
         * @return Whether this node is a leaf by checking the left and right
         * nodes
         */
        public boolean isLeaf() {
            return leftChild == null && rightChild == null;
        }

        /**
         * @return The "weight" of the node, which should be the total number of occurrences at that particular node
         */
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
