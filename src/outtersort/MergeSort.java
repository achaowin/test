package outtersort;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;

public class MergeSort {

	private String[] buffer = new String[600000];
	
	int bufLen = buffer.length;
	int bufLen1 = bufLen/4;
	int base1 = 0, base2 = base1 + bufLen1, base3 = base2+bufLen1;
	int bound1 = base1+bufLen1, bound2 = base2+bufLen1, bound3 = bufLen;
	
	public void sort(String source, String target) throws IOException {
		
		// init merge segment
		LinkedList<String> tmpFiles = initMergeSegment(source);
		// merge
		String fn = merge(tmpFiles);
		// rename to target;
		new File(fn).renameTo(new File(target));
	}

	private String merge(LinkedList<String> tmpFiles) throws IOException {
		
		LinkedList<String> tmpFiles2 = new LinkedList<String>();
		LinkedList<String> list = tmpFiles;
		LinkedList<String> list2 = tmpFiles2;
		
		int i = 0;
		while (list.size() != 1) {
			i = 0;
			
			while (!list.isEmpty()) {
				
				String fn1 = list.remove();
				
				
				if (list.isEmpty()) {
					list2.add(fn1);
					break;
				}
				
				String fn = fn1 + "." + i++;
				list2.add(fn);
				String fn2 = list.remove();
				
				merge(fn1, fn2, fn);
				
				// 合并完成后, 删除临时文件
				new File(fn1).delete();
				new File(fn2).delete();
			}
			LinkedList<String> tmp = list;
			list = list2;
			list2 = tmp;
		}
		
		return list.remove();
	}
	
	private void merge(String fn1, String fn2, String fn) throws IOException {
		
		RandomAccessFile raf1 = null;
		RandomAccessFile raf2 = null;
		BufferedWriter bw = null;
		
		try {
			raf1 = new RandomAccessFile(new File(fn1), "r");
			raf2 = new RandomAccessFile(new File(fn2), "r");
			bw = new BufferedWriter(new FileWriter(fn));
			
			int i1 = base1, i2 = base2, i3 = base3;
			int len1 = read(buffer, i1, bufLen1, raf1);
			int len2 = read(buffer, i2, bufLen1, raf2);
			int bb1 = base1 + len1, bb2 = base2 + len2;
			while (len1 > 0 && len2 > 0) {
				while (i1 < bb1 && i2 < bb2) {
					if (buffer[i1].compareTo(buffer[i2]) < 0) {
						buffer[i3++] = buffer[i1++];
					} else {
						buffer[i3++] = buffer[i2++];
					}
					if (i3 == bufLen) {
						// 写入文件
						for (int i = base3; i < i3; i++) {
							bw.write(buffer[i] + "\r\n");
						}
						i3 = base3;
					}
				}
				if (i1 == bb1) {
					i1 = base1;
					len1 = read(buffer, base1, bufLen1, raf1);
					bb1 = base1 + len1;
				}
				if (i2 == bb2) {
					i2 = base2;
					len2 = read(buffer, base2, bufLen1, raf2);
					bb2 = base2 + len2;
				}
			}
			
			while (len1 > 0) {
				while (i1 < bb1) {
					buffer[i3++] = buffer[i1++];
					if (i3 == bufLen) {
						// 写入文件
						for (int i = base3; i < i3; i++) {
							bw.write(buffer[i] + "\r\n");
						}
						i3 = base3;
					}
				}
				i1 = base1;
				len1 = read(buffer, base1, bufLen1, raf1);
				bb1 = base1 + len1;
			}
			
			while (len2 > 0) {
				while (i2 < bb2) {
					buffer[i3++] = buffer[i2++];
					if (i3 == bufLen) {
						// 写入文件
						for (int i = base3; i < i3; i++) {
							bw.write(buffer[i] + "\r\n");
						}
						i3 = base3;
					}
				}
				i2 = base2;
				len2 = read(buffer, base2, bufLen1, raf2);
				bb2 = base2 + len2;
			}
			
			for (int i = base3; i < i3; i++) {
				bw.write(buffer[i] + "\r\n");
			}
			
		} catch (IOException e) {
			throw e;
		} finally {
			try { raf1.close(); } catch (IOException e) { e.printStackTrace(); }
			try { raf2.close(); } catch (IOException e) { e.printStackTrace(); }
			try { bw.close(); } catch (IOException e) { e.printStackTrace(); }
		}
	}

	private LinkedList<String> initMergeSegment(String source) throws IOException {
		
		LinkedList<String> list = new LinkedList<String>();
		
		int len;
		int i = 0;
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(new File(source), "r");
			while (true) {
				len = read(buffer, 0, buffer.length, raf);
				if (len == 0) break;
				
				// 排序
				heapSort(buffer, len);
				
				// 写入临时文件
				String fn = source + "." + i++;
				write(fn, buffer, len);
				list.add(fn);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	private void write(String fn, String[] buffer, int len) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(fn));
			for (int i = 0; i < len; i++) {
				bw.write(buffer[i] + "\r\n");
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

	private void heapSort(String[] buffer, int len) {
		new HeapSort().sort(buffer, len);
	}

	private int read(String[] buffer, int offset, int len, RandomAccessFile raf) throws IOException {
		int i = 0;
		byte[] tmp = new byte[20];
		while (i < len && raf.read(tmp) != -1) {
			buffer[offset+i++] = new String(tmp);
			raf.skipBytes(2);
		}
		
		return i;
	}
	
	public static void main(String[] args) throws IOException {
		MergeSort ms = new MergeSort();
		ms.sort("bigdata.dat", "target.dat");
	}
}
