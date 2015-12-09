import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Chris Mendoza
 *
 */
public class HuffmanDecode {

	FileInputStream input;
	long numOfBytes;
	int numOfSymbols;
	int codeLength;

	/**
	 * Constructor
	 * @param encodedFileName
	 */
	public HuffmanDecode(String encodedFileName) throws FileNotFoundException {
		input = new FileInputStream(encodedFileName);
	}

	/**
	 * This method accepts a compressed file and needs: Read the encoded
	 * bit stream and decode the information for each matching bit sequence. Hint:
	 * Since the encoded bit stream does not and cannot have separators, you need
	 * to test each single sub-sequence against the existing codes!
	 * 
	 * @param decodedFileName
	 */
	public void readFromFile(String decodedFileName) throws IOException {

		// Pull out the long value for the number of bytes of the original file
		byte[] firstLong = new byte[8];
		input.read(firstLong);
		numOfBytes = bytesToLong(firstLong);
		System.out.println(numOfBytes);

		// Pull out the integer value for the number of symbols from the original file
		byte[] numSym = new byte[1];
		input.read(numSym);
		numOfSymbols = numSym[0];
		System.out.println(numOfSymbols);

		// Read through the encoded data and rebuild the huffman Tree
		for (int i = 0; i < numOfSymbols; i++) {

			byte[] symbEncode;

			byte[] symbol = new byte[1];
			input.read(symbol);
			byte symbolValue = symbol[0];

			byte[] length = new byte[1];
			input.read(length);
			byte bCodeLength = length[0];
			codeLength = (int) bCodeLength;

			symbEncode = new byte[codeLength];

			for(int j = 0; j < codeLength; j++) {
				symbEncode[j] = (byte) input.read();
			}

			

		}

		input.close();
	}

	/**
	 * Converts a byte array into an int
	 * @param b the byte array to convert
	 * @return the int value
     */
	public static int byteArrayToInt(byte[] b)
	{
		return   b[3] & 0xFF |
				(b[2] & 0xFF) << 8 |
				(b[1] & 0xFF) << 16 |
				(b[0] & 0xFF) << 24;
	}

	/**
	 * Convert byte to long
	 *
	 * @param b the byte[] array to convert
	 * @return the long value
	 */
	public static long bytesToLong(byte[] b) {
		long result = 0;
		for (int i = 0; i < 8; i++) {
			result <<= 8;
			result |= (b[i] & 0xFF);
		}
		return result;
	}

}
