import java.io.FileNotFoundException;
import java.io.IOException;

public class HuffmanDecodeTest {

	public static void main(String[] args) throws IOException {
		HuffmanEncode huff = new HuffmanEncode("foxtext.txt");
		huff.encodeByteStream();
	}
}
