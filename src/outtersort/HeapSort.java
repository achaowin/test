package outtersort;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class HeapSort {

	public String[] sort(String[] buffer, int len) {
		
		for (int i = (len-2)/2; i >= 0; i--) {
			filterDown(buffer, i, len-1);
		}
		
		for (int i = len-1; i > 0; i--) {
			String tmp = buffer[0];
			buffer[0] = buffer[i];
			buffer[i] = tmp;
			filterDown(buffer, 0, i-1);
		}
		
		return buffer;
	}

	private void filterDown(String[] buffer, int i, int len) {
		
		while (i < len) {
			int l = 2*i + 1;
			int r = 2*i + 2;
			int max = i;
			
			if (l > len) break;
			if (l <= len) max = l;
			if (r <= len && buffer[r].compareTo(buffer[l]) > 0) max = r;
			
			if (buffer[i].compareTo(buffer[max]) >= 0) {
				break;
			}
			
			String tmp = buffer[i];
			buffer[i] = buffer[max];
			buffer[max] = tmp;
			i = max;
		}
	}
	
	public static void main(String[] args) {
		List<String> list = new LinkedList<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("bigdata.dat"));
			String line;
			while ((line = br.readLine()) != null) {
				list.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("===== 内存直接爆掉,所以这句话不会打印 =====");
		HeapSort hs = new HeapSort();
		String[] buffer = list.toArray(new String[list.size()]);
		hs.sort(buffer, buffer.length);
	}

}
