package edu.uga.cs4810;


import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class Coder {
	
	private static int messageLength;

	//public BufferedImage encoder() {
	public static void main(String[] args) {

		int length = 0;
		try {

			//Convert Image to Bytes
			
			BufferedImage original = ImageIO.read(new File("/Users/yu/Desktop/rose.jpg"));
			WritableRaster raster   = original.getRaster();
			DataBufferByte buffer = (DataBufferByte)raster.getDataBuffer();
			byte[] imageBytes = buffer.getData();
			
			
			String text = "Heetae";
			byte[] textBytes = text.getBytes();  
			length = text.length()*8;
			
			//byte[] textLength = ByteBuffer.allocate(4).putInt(length).array();
			//System.out.println();
			
			int[] bits = new int[length];
			
			int counter = 0;
			for(int i=0; i<textBytes.length; i++) {
				for(int j=7; j>=0; j--) {
					int bit = ((textBytes[i] >> j) & 1);
					bits[counter] = bit;
					counter++;
				}
			}
			
			byte[] messageLength = new byte[]{0,0,0, (byte)((length/8) & 0x000000FF)};

			
			int[] lengthBits = new int[32];
			Arrays.fill(lengthBits, 0);

			int counter2 = 24;
			for(int j=7; j>=0; j--) {
				int bit = ((messageLength[3] >> j) & 1);
				lengthBits[counter2] = bit;
				counter2++;
			}
			
			for(int i=0; i<32; i++) {
				if(lengthBits[i] == 1) {
					imageBytes[i] = (byte) (imageBytes[i] | (1 << 0));
				}
				else {
					imageBytes[i] = (byte) (imageBytes[i] & ~(1 << 0));
				}
			}


			for(int i=32; i<length+32; i++) {
				if(bits[i-32] == 1) {
					imageBytes[i] = (byte) (imageBytes[i] | (1 << 0));
				}
				else {
					imageBytes[i] = (byte) (imageBytes[i] & ~(1 << 0));
				}
			}


			ImageIO.write(original, "png", new File("/Users/yu/Desktop/newrose.png"));
			System.out.println("SUCCESS!!!");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Unable to find File");
			e.printStackTrace();
		}

		String answer = decode("/Users/yu/Desktop/newrose.png");
		System.out.println(answer);

	}
	
	public static BufferedImage encode(String filePath, String message) throws IOException {
		
		if(message.length() >254) {
			JOptionPane.showMessageDialog(null,
				    "Message size is too big",
				    "Inane error",
				    JOptionPane.ERROR_MESSAGE);
			return null;
		}
	
			BufferedImage original = ImageIO.read(new File(filePath));
			WritableRaster raster   = original.getRaster();
			DataBufferByte buffer = (DataBufferByte)raster.getDataBuffer();
			byte[] imageBytes = buffer.getData();
			
			byte[] textBytes = message.getBytes();  
			int length = message.length()*8;
			
			int[] bits = new int[length];
			
			//Array of bits of hidden message
			int counter = 0;
			for(int i=0; i<textBytes.length; i++) {
				for(int j=7; j>=0; j--) {
					int bit = ((textBytes[i] >> j) & 1);
					bits[counter] = bit;
					counter++;
				}
			}

			//32-bit to hold length of message
			byte[] messageLength = new byte[]{0,0,0, (byte)((length/8) & 0x000000FF)};

			
			int[] lengthBits = new int[32];
			Arrays.fill(lengthBits, 0);

			int counter2 = 24;
			for(int j=7; j>=0; j--) {
				int bit = ((messageLength[3] >> j) & 1);
				lengthBits[counter2] = bit;
				counter2++;
			}

			
			
			for(int i=0; i<32; i++) {
				if(lengthBits[i] == 1) {
					imageBytes[i] = (byte) (imageBytes[i] | (1 << 0));
				}
				else {
					imageBytes[i] = (byte) (imageBytes[i] & ~(1 << 0));
				}
			}
			
			for(int i=32; i<length+32; i++) {
				if(bits[i-32] == 1) {
					imageBytes[i] = (byte) (imageBytes[i] | (1 << 0));
				}
				else {
					imageBytes[i] = (byte) (imageBytes[i] & ~(1 << 0));
				}
			}
			
			return original;
			
	}
	


	public static String decode(String filePath) {
		String decodedMessage = null;
		try {
			BufferedImage original = ImageIO.read(new File(filePath));
			WritableRaster raster   = original.getRaster();
			DataBufferByte buffer = (DataBufferByte)raster.getDataBuffer();
			byte[] bytes = buffer.getData();

			byte[] binary = new byte[8];
			

			/*
			byte[] hiddenText = new byte[]{0,0,0,0}; 
			for(int i=24; i<32; i++) {
				hiddenText[3] = (byte) ((bytes[i] >> 0) & 1);
				
		//	String result2 = Byte.toString(messageLength[3]);
		//	int answer = Integer.parseInt(result2);
			}*/
			
			byte[] messageLength = new byte[]{0,0,0,0};
			byte[] lengthBits = new byte[32];
			
			int counter = 7;
			for(int i=24; i<32; i++) {
				if(((byte) ((bytes[i] >> 0) & 1)) == 1) {
					messageLength[3] = (byte) (messageLength[3] | (1 << counter));
				}
				else {
					messageLength[3] = (byte) (messageLength[3] & ~(1 << counter));
				}
				counter--;
			}
			
			int length = ((messageLength[0] & 0xff) << 24) |
				      	 ((messageLength[1] & 0xff) << 16) |
				      	 ((messageLength[2] & 0xff) << 8)  |
				      	  (messageLength[3] & 0xff);
			
			length = length*8;
			

			/*
		 String lengthString = Byte.toString(messageLength);
		 int length = Integer.parseInt(lengthString);
		 length = length *8;
		 */
		 String[] message = new String[length/8];
			
			
			int j=0;
			for (int i=32; i<length+32;) {
				binary[0] = (byte) ((bytes[i] >> 0) & 1);
				binary[1] = (byte) ((bytes[i+1] >> 0) & 1);
				binary[2] = (byte) ((bytes[i+2] >> 0) & 1);
				binary[3] = (byte) ((bytes[i+3] >> 0) & 1);
				binary[4] = (byte) ((bytes[i+4] >> 0) & 1);
				binary[5] = (byte) ((bytes[i+5] >> 0) & 1);
				binary[6] = (byte) ((bytes[i+6] >> 0) & 1);
				binary[7] = (byte) ((bytes[i+7] >> 0) & 1);
				StringBuilder builder = new StringBuilder();
				for (byte value : binary) {
				    builder.append(value);
				}
				String binaryToString = builder.toString();
			
				System.out.println(binaryToString + "   " + Integer.parseInt(binaryToString, 2));
				
				System.out.println(message.length);
				String character = Character.toString((char) Integer.parseInt(binaryToString, 2));
				message[j] = character;
				j++;
				i += 8;
			}
			StringBuilder builder = new StringBuilder();
			for (String value : message) {
			    builder.append(value);
			}
			decodedMessage = builder.toString();
		
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Can't find File");
			e.printStackTrace();
		}
		return decodedMessage;
	}

}





























