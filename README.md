# HuffmanDecoding
Extension of Huffman Encoding Project.
Assignment 7

Huffman File

Now that you have finished Project #5 (Huffman Coding) on page 1092, you are ready to extend this project in a way that you write the encoded information to a file and read the encoded information from a file and decode it.

Here are some details and interface requirements as discussed in class:

Extend your public class HuffmanEncode

with an overloaded Constructor

public HuffmanEncode (String fileName)

and additional public methods

public void encodeByteStream()

public void writeToFile(String fileName)

 

The former does the following:

Reads the filename and creates a File Object which will be used in a FileInputStream.  
This enables your program from encoding characters to bytes. You will notice that a newline when you read “foxtext.txt” as  FileInputStream is coded in 2 bytes (carriage return and newline). As a result the Huffman Tree will look different from the one that you create when you treat this file as a text file.
The latter does the following:

Reads the FileInputStream and counts the occurrence of every byte.
Fills the Priority Queue with the nodes that you create out of the bytes and occurrences.
Builds the Huffman Tree
Traverses the Huffman Tree 
The Traversal method should store for every leaf node the following info in a HashMap<K, V>:

Byte as K (key) of type Integer (we have discussed in class why we don’t use Byte (signed problem))
Huffman Code as V (value) of type String

With this preparation you will create the compressed file when writeToFile(String fileName) is called.

In order for the file to be decoded you need to write the coding information (Huffman Tree) to the compressed file as well. Here is the required approach (because I want to make sure that every student can decode a File that another student coded):

File Header Info:

long numberOfBytes;  // Number of bytes in original file. 

// You cannot write long directly, you need to split that number into

// bytes. Requirement: The highest significant byte first

int numberOfSymbols;  // Bytes found in the original file that got encoded
for each symbol you write

byte symbolValue;

byte codeLength; // the length of the “01010111” codeString for this symbol

byte(s) codeBits // for each ‘0’ a bit 0 and for each ‘1’ a bit 1 set. The # of codeBits

// is determined by the codeLength (<=8 one byte, >8 <=16 two bytes etc.)

After this header the bit stream of the encoded bytes follow. Here is an example:

Assuming you have the following mapping (I use chars as symbols for simplicity)
'T' ‘110100’, 'h' ‘10111’, 'e' ‘1001’, ' ' ‘111’,  'b' ‘011100’ the foxtext.hzip file would start right after the above described File Header Info with the following 3 bytes (Each block represents a byte in the file):

11010010   11110011   11011100

The challenge is that you need to fill up the bytes bit by bit. A possible algorithm:

 

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

When you call this method you need to keep track of how many bits are already filled in the first argument that you call that method with and make sure that the second argument’s length will not go over the 8 bits (hint: use substrings).

Create an additional Class

public class HuffmanDecode

with a Constructor

public HuffmanDecode ( String encodedFileName)

This constructor reads the File Header Info from the file (encodedFileName)
and stores that information appropriately (HashMap, rebuild Huffman Tree)

and public method

public void readFromFile(String decodedFileName)

This method accepts a compressed file and needs:

Read the encoded bitstream and decode the information for each matching bitsequence.
Hint: Since the encoded bitstream does not and cannot have separators, you need to test each single sub-sequence against the existining codes! For the example above (11010010   11110011   11011100 ), you test 
1
11
110
1101
11010
110100  => here you finally find the first match, you write the first found byte ‘T” to your decoded file (decodedFileName). 
