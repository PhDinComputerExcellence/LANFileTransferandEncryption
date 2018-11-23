package com.files4Dayz.security;

import java.io.UnsupportedEncodingException;


public class AsciiArmor
{
	public static final char[] encoding = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
											'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
											'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 
											'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
											'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 
											't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1',
											'2', '3', '4', '5', '6', '7', '8', '9', '+',
											'/'};
	
	public static byte[] armor(byte[] input) throws UnsupportedEncodingException {
		int counter = 0;
		byte[] fixedInput = new byte[input.length + 2];
		System.arraycopy(input, 0, fixedInput, 0, input.length);
		String twentyFourBitChunk = "";
		String chunkText = "";
		String armoredText = "";
		
		for (int i = 0; i < fixedInput.length; i++)
        {
            twentyFourBitChunk += String.format("%8s", Integer.toBinaryString(fixedInput[i] & 0xFF)).replace(' ','0');
            
            if (counter < 2)
            {
                counter++;
                continue;
            }
            chunkText = getArmoredText(chunkText, twentyFourBitChunk);
            armoredText += chunkText;
            chunkText = "";
            twentyFourBitChunk = "";
            counter = 0;
        }
		
		byte[] armoredBytes = armoredText.getBytes("UTF-8");
		return armoredBytes;
	}
	
	private static String getArmoredText(String input, String eightBitChunks)
	{
		String[] sixBitChunks = eightBitChunks.split("(?<=\\G.{6})");
		char[] asciiChar = new char[sixBitChunks.length];
		int asciiVal = 0;
		for (int j = 0; j < sixBitChunks.length; j++)
		{
			if (sixBitChunks[j].equals("000000"))
			{
				if (j - 1 <= 0)
				{
					asciiVal = Integer.parseInt(sixBitChunks[j], 2);
					asciiChar[j] = encoding[asciiVal];
				}
				else if (j >= sixBitChunks.length - 2)
				{
					asciiChar[j] = '=';
				}
				else
				{
					if (sixBitChunks[j-1].equals("000000"))
					{
						asciiChar[j] = '=';
					}
					else
					{
						asciiVal = Integer.parseInt(sixBitChunks[j], 2);
						asciiChar[j] = encoding[asciiVal];
					}					
				}
			}
			else
			{
				asciiVal = Integer.parseInt(sixBitChunks[j], 2);
				asciiChar[j] = encoding[asciiVal];
			}
			input += asciiChar[j];
		}
		return input;
	}
	
	public static byte[] removeArmor(byte[] input) throws UnsupportedEncodingException
	{
		String armoredText = new String(input, "UTF-8");
		char[] asciiText = armoredText.toCharArray();
		String wholeChunk = "";
		String smallChunk = "";
		
		for(int i = 0; i < asciiText.length; i++)
		{
			switch (asciiText[i])
			{
				case 'A':
				{
					smallChunk = "000000";
					break;
				}
				case 'B':
				{
					smallChunk = "000001";
					break;
				}
				case 'C':
				{
					smallChunk = "000010";
					break;
				}
				case 'D':
				{
					smallChunk = "000011";
					break;
				}
				case 'E':
				{
					smallChunk = "000100";
					break;
				}
				case 'F':
				{
					smallChunk = "000101";
					break;
				}
				case 'G':
				{
					smallChunk = "000110";
					break;
				}
				case 'H':
				{
					smallChunk = "000111";
					break;
				}
				case 'I':
				{
					smallChunk = "001000";
					break;
				}
				case 'J':
				{
					smallChunk = "001001";
					break;
				}
				case 'K':
				{
					smallChunk = "001010";
					break;
				}
				case 'L':
				{
					smallChunk = "001011";
					break;
				}
				case 'M':
				{
					smallChunk = "001100";
					break;
				}
				case 'N':
				{
					smallChunk = "001101";
					break;
				}
				case 'O':
				{
					smallChunk = "001110";
					break;
				}
				case 'P':
				{
					smallChunk = "001111";
					break;
				}
				case 'Q':
				{
					smallChunk = "010000";
					break;
				}
				case 'R':
				{
					smallChunk = "010001";
					break;
				}
				case 'S':
				{
					smallChunk = "010010";
					break;
				}
				case 'T':
				{
					smallChunk = "010011";
					break;
				}
				case 'U':
				{
					smallChunk = "010100";
					break;
				}
				case 'V':
				{
					smallChunk = "010101";
					break;
				}
				case 'W':
				{
					smallChunk = "010110";
					break;
				}
				case 'X':
					smallChunk = "010111";
				{
					break;
				}
				case 'Y':
				{
					smallChunk = "011000";
					break;
				}
				case 'Z':
				{
					smallChunk = "011001";
					break;
				}
				case 'a':
				{
					smallChunk = "011010";
					break;
				}
				case 'b':
				{
					smallChunk = "011011";
					break;
				}
				case 'c':
				{
					smallChunk = "011100";
					break;
				}
				case 'd':
				{
					smallChunk = "011101";
					break;
				}
				case 'e':
				{
					smallChunk = "011110";
					break;
				}
				case 'f':
				{
					smallChunk = "011111";
					break;
				}
				case 'g':
				{
					smallChunk = "100000";
					break;
				}
				case 'h':
				{
					smallChunk = "100001";
					break;
				}
				case 'i':
				{
					smallChunk = "100010";
					break;
				}
				case 'j':
				{
					smallChunk = "100011";
					break;
				}
				case 'k':
				{
					smallChunk = "100100";
					break;
				}
				case 'l':
				{
					smallChunk = "100101";
					break;
				}
				case 'm':
				{
					smallChunk = "100110";
					break;
				}
				case 'n':
				{
					smallChunk = "100111";
					break;
				}
				case 'o':
				{
					smallChunk = "101000";
					break;
				}
				case 'p':
				{
					smallChunk = "101001";
					break;
				}
				case 'q':
				{
					smallChunk = "101010";
					break;
				}
				case 'r':
				{
					smallChunk = "101011";
					break;
				}
				case 's':
				{
					smallChunk = "101100";
					break;
				}
				case 't':
				{
					smallChunk = "101101";
					break;
				}
				case 'u':
				{
					smallChunk = "101110";
					break;
				}
				case 'v':
				{
					smallChunk = "101111";
					break;
				}
				case 'w':
				{
					smallChunk = "110000";
					break;
				}
				case 'x':
				{
					smallChunk = "110001";
					break;
				}
				case 'y':
				{
					smallChunk = "110010";
					break;
				}
				case 'z':
				{
					smallChunk = "110011";
					break;
				}
				case '0':
				{
					smallChunk = "110100";
					break;
				}
				case '1':
				{
					smallChunk = "110101";
					break;
				}
				case '2':
				{
					smallChunk = "110110";
					break;
				}
				case '3':
				{
					smallChunk = "110111";
					break;
				}
				case '4':
				{
					smallChunk = "111000";
					break;
				}
				case '5':
				{
					smallChunk = "111001";
					break;
				}
				case '6':
				{
					smallChunk = "111010";
					break;
				}
				case '7':
				{
					smallChunk = "111011";
					break;
				}
				case '8':
				{
					smallChunk = "111100";
					break;
				}
				case '9':
				{
					smallChunk = "111101";
					break;
				}
				case '+':
				{
					smallChunk = "111110";
					break;
				}
				case '/':
				{
					smallChunk = "111111";
					break;
				}
				case '=':
				{
					smallChunk = "000000";
					break;
				}
			}
			wholeChunk += smallChunk;
		}
		String[] eightBitChunks = wholeChunk.split("(?<=\\G.{8})");
		byte[] originalBytes = new byte[1024];

		for (int i = 0; i < originalBytes.length; i++)
		{
			originalBytes[i] = (byte) Integer.parseInt(eightBitChunks[i], 2);
		}
		return originalBytes;
	}
}