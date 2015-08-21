package outtersort;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class DataMaker {

	private static final char[] ALPHA_NUM = new char[] {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'A', 'B', 'C', 'D', 'E', 'F', 'G',
		'H', 'I', 'J', 'K', 'L', 'M', 'N',
		'O', 'P', 'Q', 'R', 'S', 'T',
		'U', 'V', 'W', 'X', 'Y', 'Z',
		'a', 'b', 'c', 'd', 'e', 'f', 'g',
		'h', 'i', 'j', 'k', 'l', 'm', 'n',
		'o', 'p', 'q', 'r', 's', 't',
		'u', 'v', 'w', 'x', 'y', 'z',
	};
	
	public static void main(String[] args) {
		
		char[] buf = new char[20];
		Random rand = new Random();
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter("bigdata.dat"));
			for (long i = 0; i < 20650320; i++) {
				for (int j = 0; j < 20; j++) {
					buf[j] = ALPHA_NUM[rand.nextInt(ALPHA_NUM.length)];
				}
				
				bw.write(new String(buf) + "\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
