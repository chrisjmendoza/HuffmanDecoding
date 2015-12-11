import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * @author Chris Mendoza
 *
 */
public class HuffmanDecode {

	FileInputStream input;
	long numOfBytes;
	int numOfSymbols;
	int codeLength;
	static Map<Integer, String> decodeMap;

	/**
	 * Constructor
	 * @param encodedFileName
	 */
	public HuffmanDecode(String encodedFileName) throws IOException {
		input = new FileInputStream(encodedFileName);
		readFromFile("output.txt");
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

		// -------------- START FILEOUTPUTSTREAM -----------------
		FileOutputStream outFile = new FileOutputStream(decodedFileName);

		// Pull out the long value for the number of bytes of the original file
		byte[] firstLong = new byte[8];
		input.read(firstLong);
		numOfBytes = bytesToLong(firstLong);
		System.out.println("The decoded number of bytes: " + numOfBytes);

		// Pull out the integer value for the number of symbols from the original file
		byte[] numSym = new byte[1];
		input.read(numSym);
		numOfSymbols = numSym[0];
		System.out.println("The decoded number of symbols: " + numOfSymbols);

		// Rebuild the Map with the encoded character values

		// Read through the encoded data and rebuild the decoding map
		for (int i = 0; i < numOfSymbols; i++) {

			// Read the integer symbol value from the byte stream
			byte[] intByte = new byte[4];
			input.read(intByte);
			int symVal = byteArrayToInt(intByte);
			System.out.println("The decoded symbol value: " + symVal);

			// Read the length of the character encoding
			input.read(intByte);
			int codeLength = byteArrayToInt(intByte);
			System.out.println("The decoded codeLength: " + codeLength);

			// Build the binary path string from the header
			byte[] symbolEncode = new byte[codeLength];

			// Read the bytes of the given code length from the stream
			for(int j = 0; j < codeLength; j++) {
				symbolEncode[j] = (byte) input.read();
			}

			// Create a string to store the binary path
			String decodePath = "";

			for(byte b : symbolEncode) {
				char d = (char) b;
				decodePath += d;
			}
			decodeMap.put(symVal, decodePath);
			System.out.println("The symbol value: " + symVal + " and the binary path: " + decodePath);

		}
		System.out.println(decodeMap);
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
