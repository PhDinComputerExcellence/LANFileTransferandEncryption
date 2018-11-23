package com.files4Dayz.security;

import java.io.*;
import java.util.Iterator;

public class XorCipher {

    public static String encryptDecrypt(String input, File pass) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(pass));
        StringBuilder keyBuilder = new StringBuilder();
        String current;
        while ((current = br.readLine()) != null) {
            keyBuilder.append(current);
        }
        br.close();
        char[] key = (new String(keyBuilder)).toCharArray();
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            output.append((char) (input.charAt(i) ^ key[i % key.length]));
        }

        return output.toString();
    }

    public static byte[] encryptDecrypt(byte[] inputx, File key) throws IOException {
    	InputStream is = new FileInputStream(key);
    	DataInputStream ds = new DataInputStream(is);
    	byte[] password = new byte[inputx.length];
    	ds.read(password);
    	ds.close();
    	
    	ByteIterator bi1 = new ByteIterator(inputx);
    	ByteIterator bi2 = new ByteIterator(password);
    	
    	String first = "";
    	String second = "";
    	
    	for (boolean b : bi1) {
    		if (b) {
    			first+="1";
    		} else {
    			first+="0";
    		}
    		
    	}
    	for (boolean b : bi2) {
    		if (b) {
    			second+="1";
    		} else {
    			second+="0";
    		}
    		
    	}
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < first.length(); i++) {
            if (first.charAt(i) == second.charAt(i)) {
            	output.append("0");
            } else {
            	output.append("1");
            }
        }
        System.out.println(output.toString());
        return getBytes(output.toString());
    }
    
    private static byte[] getBytes(String x) {
    	byte[] newbit = new byte[x.length()/8];
    	for (int i = 0; i < newbit.length; i++ ) {
    		for (int j = 0; j <= 7; j++) {
    			newbit[i] |= (byte)((x.charAt(i*8+j) == '1' ? 1 : 0) << (7 - j));
    		}
    	}
    	return newbit;
    }
    
    static class ByteIterator implements Iterable<Boolean> {
        private final byte[] check;

        public ByteIterator(byte[] array) {
            check = array;
        }

        @Override
        public Iterator<Boolean> iterator() {
            return new Iterator<Boolean>() {
                private int curIndex = 0;
                private int bitIndex = 0;

                public boolean hasNext() {
                    return ((curIndex < check.length) && (bitIndex < 8));
                }

                public Boolean next() {
                    Boolean value = (check[curIndex] >> (7 - bitIndex) & 1) == 1;
                    bitIndex++;
                    if (bitIndex == 8) {
                        curIndex++;
                        bitIndex = 0;
                    }
                    return value;
                }
            };
        }
    }
}


