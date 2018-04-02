/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lsb;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author locnqt LSB 24bit
 */
public class LSB {

    /**
     * @param args the command line arguments
     */
    private static int HEADER_SIZE = 54;

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        int n = 1;
        while (n != 3) {
            try {
                System.out.println("---------------------------------------------------------------");
                System.out.print(" 1.Encode \n 2.Decode \n 3.Exit \n Chọn 1-3:");
                n = sc.nextInt();
                switch (n) {
                    case 1:
                        Encode();
                        break;
                    case 2:
                        Decode();
                        break;
                    case 3:
                        break;
                    default:
                        System.out.println("Nhập sai!");
                }
            } catch (java.util.InputMismatchException ioe) {
                System.err.println("!!Yêu cầu nhập số!!");
                break;
            }
        }
    }

    public static String messtobin(String mess) {
        String result = "";
        for (char c : mess.toCharArray()) {
//            System.out.println(c);
//            System.out.println(Integer.toBinaryString(Character.getNumericValue(c))); 
//            System.out.println(Character.getNumericValue(c));
            String bin = Integer.toBinaryString(c);
            while (bin.length() < 8) {
                bin = "0" + bin;
            }
            result += bin;
        }
        return result;
    }

    public static char bintomess(String bin) {
        byte r = 0;
        int n = bin.length();
        int i = 0;
        int pow = 1;
        for (char c : bin.toCharArray()) {
            if (c == '1') {
                i = 0;
                pow = 1;
                while (i < n - 1) {
                    pow *= 2;
                    i++;
                }
                r += pow;
            }
            n--;
        }
        return (char) r;
    }

    public static void Encode() throws IOException {
        System.out.print("Nhập message: ");
        sc.nextLine();
        String mess = sc.nextLine();
        String bin = messtobin(mess)+"00000000";
        char[] b = bin.toCharArray();
//        String databin = "";
//        System.out.println(bin);

        File f = new File("sample-pic.bmp");
        BufferedImage bufferedImage = ImageIO.read(f);
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "bmp", bos);
        byte[] arr = bos.toByteArray();
//        StringBuilder sb = new StringBuilder();
//        for (byte by : arr) {
//            sb.append(Integer.toBinaryString(by & 0xFF));
//        }
//        System.out.println(sb);
//        can chuyen thanh binary
        int data = HEADER_SIZE+1;
        char temp;
//        String newdata = "";
//        String newdataimage = "";
//        String header = "";
        int x = 0;
        if (bin.length() > (arr.length - 1)) {
            System.out.println("message quá lớn!");
        } else {
            for (int i = data; x < bin.length(); i++) {
//                temp = bin.charAt(x);
                
                arr[i] >>= 1;
                arr[i] <<= 1;
                arr[i] += b[x];x++;
//                databin = Integer.toBinaryString((arr[i] + 256) % 256);
//                temp = bin.charAt(x);
//                x++;
//                newdata = databin.substring(0, 7) + temp;
//                newdataimage += newdata;
//                arr[i] = newdata.getBytes();
//                System.out.println(databin);
//                System.out.println(temp);
//                System.out.println(newdata);
//                System.out.println(newdataimage);
//                System.out.println(arr[i]);
            }
        }
        File output = new File("encrypted.bmp");
        bufferedImage = ImageIO.read(new ByteArrayInputStream(arr));
//        ImageIO.write(bufferedImage, "bmp",new File("encrypted.bmp" + ".bmp"));
        if (ImageIO.write(bufferedImage, "bmp",output)) {
            System.out.println("Save file OK!");
        } else {
            System.out.println("ERROR!! Không save duoc!!");
        }
    }

    public static void Decode() throws IOException {
        File f = new File("encrypted.bmp");
        BufferedImage bufferedImage = ImageIO.read(f);
        FileInputStream fis = new FileInputStream(f);
        byte[] buf = new byte[1024];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        for (int readNum; (readNum = fis.read(buf)) != -1;) {
            bos.write(buf, 0, readNum);
        }
        byte[] arr = bos.toByteArray();

        int data = HEADER_SIZE + 1;
        String databin = "";
        String mess = "";
        int ktra8bit0 = 0;
        int count = 0;
        while (true) {
            if (arr[data] % 2 == 0) {
                databin += "0";
                ktra8bit0 += 1;
                count++;
            } else {
                databin += "1";
                ktra8bit0 = 0;
                count++;
            }
            data++;
            if (count == 8) {
                if (ktra8bit0 == 8) {
                    break;
                }
                ktra8bit0 = 0;
                count = 0;
            }
        }
        databin = databin.substring(0, databin.length() - 8);
//        System.out.println(databin);
        for (int i = 0; i < databin.length(); i = i + 8) {
            mess += bintomess(databin.substring(i, i + 8));
        }
//        int charCode = Integer.parseInt(databin, 2);
//        String mess = new Character((char) charCode).toString();
        System.out.println("Message: " + mess);
    }
}
