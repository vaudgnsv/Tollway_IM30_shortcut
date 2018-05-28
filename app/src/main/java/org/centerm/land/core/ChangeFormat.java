package org.centerm.land.core;

import java.io.IOException;
import java.io.UTFDataFormatException;

/**
 * Created by 29622 on 2018/1/16.
 */

public  class ChangeFormat {
    public static final byte[] EBCDIC2ASCII = new byte[] {
            (byte)0x0,  (byte)0x1,  (byte)0x2,  (byte)0x3,
            (byte)0x9C, (byte)0x9,  (byte)0x86, (byte)0x7F,
            (byte)0x97, (byte)0x8D, (byte)0x8E, (byte)0xB,
            (byte)0xC,  (byte)0xD,  (byte)0xE,  (byte)0xF,
            (byte)0x10, (byte)0x11, (byte)0x12, (byte)0x13,
            (byte)0x9D, (byte)0xA,  (byte)0x8,  (byte)0x87,
            (byte)0x18, (byte)0x19, (byte)0x92, (byte)0x8F,
            (byte)0x1C, (byte)0x1D, (byte)0x1E, (byte)0x1F,
            (byte)0x80, (byte)0x81, (byte)0x82, (byte)0x83,
            (byte)0x84, (byte)0x85, (byte)0x17, (byte)0x1B,
            (byte)0x88, (byte)0x89, (byte)0x8A, (byte)0x8B,
            (byte)0x8C, (byte)0x5,  (byte)0x6,  (byte)0x7,
            (byte)0x90, (byte)0x91, (byte)0x16, (byte)0x93,
            (byte)0x94, (byte)0x95, (byte)0x96, (byte)0x4,
            (byte)0x98, (byte)0x99, (byte)0x9A, (byte)0x9B,
            (byte)0x14, (byte)0x15, (byte)0x9E, (byte)0x1A,
            (byte)0x20, (byte)0xA0, (byte)0xE2, (byte)0xE4,
            (byte)0xE0, (byte)0xE1, (byte)0xE3, (byte)0xE5,
            (byte)0xE7, (byte)0xF1, (byte)0xA2, (byte)0x2E,
            (byte)0x3C, (byte)0x28, (byte)0x2B, (byte)0x7C,
            (byte)0x26, (byte)0xE9, (byte)0xEA, (byte)0xEB,
            (byte)0xE8, (byte)0xED, (byte)0xEE, (byte)0xEF,
            (byte)0xEC, (byte)0xDF, (byte)0x21, (byte)0x24,
            (byte)0x2A, (byte)0x29, (byte)0x3B, (byte)0x5E,
            (byte)0x2D, (byte)0x2F, (byte)0xC2, (byte)0xC4,
            (byte)0xC0, (byte)0xC1, (byte)0xC3, (byte)0xC5,
            (byte)0xC7, (byte)0xD1, (byte)0xA6, (byte)0x2C,
            (byte)0x25, (byte)0x5F, (byte)0x3E, (byte)0x3F,
            (byte)0xF8, (byte)0xC9, (byte)0xCA, (byte)0xCB,
            (byte)0xC8, (byte)0xCD, (byte)0xCE, (byte)0xCF,
            (byte)0xCC, (byte)0x60, (byte)0x3A, (byte)0x23,
            (byte)0x40, (byte)0x27, (byte)0x3D, (byte)0x22,
            (byte)0xD8, (byte)0x61, (byte)0x62, (byte)0x63,
            (byte)0x64, (byte)0x65, (byte)0x66, (byte)0x67,
            (byte)0x68, (byte)0x69, (byte)0xAB, (byte)0xBB,
            (byte)0xF0, (byte)0xFD, (byte)0xFE, (byte)0xB1,
            (byte)0xB0, (byte)0x6A, (byte)0x6B, (byte)0x6C,
            (byte)0x6D, (byte)0x6E, (byte)0x6F, (byte)0x70,
            (byte)0x71, (byte)0x72, (byte)0xAA, (byte)0xBA,
            (byte)0xE6, (byte)0xB8, (byte)0xC6, (byte)0xA4,
            (byte)0xB5, (byte)0x7E, (byte)0x73, (byte)0x74,
            (byte)0x75, (byte)0x76, (byte)0x77, (byte)0x78,
            (byte)0x79, (byte)0x7A, (byte)0xA1, (byte)0xBF,
            (byte)0xD0, (byte)0x5B, (byte)0xDE, (byte)0xAE,
            (byte)0xAC, (byte)0xA3, (byte)0xA5, (byte)0xB7,
            (byte)0xA9, (byte)0xA7, (byte)0xB6, (byte)0xBC,
            (byte)0xBD, (byte)0xBE, (byte)0xDD, (byte)0xA8,
            (byte)0xAF, (byte)0x5D, (byte)0xB4, (byte)0xD7,
            (byte)0x7B, (byte)0x41, (byte)0x42, (byte)0x43,
            (byte)0x44, (byte)0x45, (byte)0x46, (byte)0x47,
            (byte)0x48, (byte)0x49, (byte)0xAD, (byte)0xF4,
            (byte)0xF6, (byte)0xF2, (byte)0xF3, (byte)0xF5,
            (byte)0x7D, (byte)0x4A, (byte)0x4B, (byte)0x4C,
            (byte)0x4D, (byte)0x4E, (byte)0x4F, (byte)0x50,
            (byte)0x51, (byte)0x52, (byte)0xB9, (byte)0xFB,
            (byte)0xFC, (byte)0xF9, (byte)0xFA, (byte)0xFF,
            (byte)0x5C, (byte)0xF7, (byte)0x53, (byte)0x54,
            (byte)0x55, (byte)0x56, (byte)0x57, (byte)0x58,
            (byte)0x59, (byte)0x5A, (byte)0xB2, (byte)0xD4,
            (byte)0xD6, (byte)0xD2, (byte)0xD3, (byte)0xD5,
            (byte)0x30, (byte)0x31, (byte)0x32, (byte)0x33,
            (byte)0x34, (byte)0x35, (byte)0x36, (byte)0x37,
            (byte)0x38, (byte)0x39, (byte)0xB3, (byte)0xDB,
            (byte)0xDC, (byte)0xD9, (byte)0xDA, (byte)0x9F
    };
    public static final byte[] ASCII2EBCDIC = new byte[] {
            (byte)0x0,  (byte)0x1,  (byte)0x2,  (byte)0x3,
            (byte)0x37, (byte)0x2D, (byte)0x2E, (byte)0x2F,
            (byte)0x16, (byte)0x5,  (byte)0x15, (byte)0xB,
            (byte)0xC,  (byte)0xD,  (byte)0xE,  (byte)0xF,
            (byte)0x10, (byte)0x11, (byte)0x12, (byte)0x13,
            (byte)0x3C, (byte)0x3D, (byte)0x32, (byte)0x26,
            (byte)0x18, (byte)0x19, (byte)0x3F, (byte)0x27,
            (byte)0x1C, (byte)0x1D, (byte)0x1E, (byte)0x1F,
            (byte)0x40, (byte)0x5A, (byte)0x7F, (byte)0x7B,
            (byte)0x5B, (byte)0x6C, (byte)0x50, (byte)0x7D,
            (byte)0x4D, (byte)0x5D, (byte)0x5C, (byte)0x4E,
            (byte)0x6B, (byte)0x60, (byte)0x4B, (byte)0x61,
            (byte)0xF0, (byte)0xF1, (byte)0xF2, (byte)0xF3,
            (byte)0xF4, (byte)0xF5, (byte)0xF6, (byte)0xF7,
            (byte)0xF8, (byte)0xF9, (byte)0x7A, (byte)0x5E,
            (byte)0x4C, (byte)0x7E, (byte)0x6E, (byte)0x6F,
            (byte)0x7C, (byte)0xC1, (byte)0xC2, (byte)0xC3,
            (byte)0xC4, (byte)0xC5, (byte)0xC6, (byte)0xC7,
            (byte)0xC8, (byte)0xC9, (byte)0xD1, (byte)0xD2,
            (byte)0xD3, (byte)0xD4, (byte)0xD5, (byte)0xD6,
            (byte)0xD7, (byte)0xD8, (byte)0xD9, (byte)0xE2,
            (byte)0xE3, (byte)0xE4, (byte)0xE5, (byte)0xE6,
            (byte)0xE7, (byte)0xE8, (byte)0xE9, (byte)0xAD,
            (byte)0xE0, (byte)0xBD, (byte)0x5F, (byte)0x6D,
            (byte)0x79, (byte)0x81, (byte)0x82, (byte)0x83,
            (byte)0x84, (byte)0x85, (byte)0x86, (byte)0x87,
            (byte)0x88, (byte)0x89, (byte)0x91, (byte)0x92,
            (byte)0x93, (byte)0x94, (byte)0x95, (byte)0x96,
            (byte)0x97, (byte)0x98, (byte)0x99, (byte)0xA2,
            (byte)0xA3, (byte)0xA4, (byte)0xA5, (byte)0xA6,
            (byte)0xA7, (byte)0xA8, (byte)0xA9, (byte)0xC0,
            (byte)0x4F, (byte)0xD0, (byte)0xA1, (byte)0x7,
            (byte)0x20, (byte)0x21, (byte)0x22, (byte)0x23,
            (byte)0x24, (byte)0x25, (byte)0x6,  (byte)0x17,
            (byte)0x28, (byte)0x29, (byte)0x2A, (byte)0x2B,
            (byte)0x2C, (byte)0x9,  (byte)0xA,  (byte)0x1B,
            (byte)0x30, (byte)0x31, (byte)0x1A, (byte)0x33,
            (byte)0x34, (byte)0x35, (byte)0x36, (byte)0x8,
            (byte)0x38, (byte)0x39, (byte)0x3A, (byte)0x3B,
            (byte)0x4,  (byte)0x14, (byte)0x3E, (byte)0xFF,
            (byte)0x41, (byte)0xAA, (byte)0x4A, (byte)0xB1,
            (byte)0x9F, (byte)0xB2, (byte)0x6A, (byte)0xB5,
            (byte)0xBB, (byte)0xB4, (byte)0x9A, (byte)0x8A,
            (byte)0xB0, (byte)0xCA, (byte)0xAF, (byte)0xBC,
            (byte)0x90, (byte)0x8F, (byte)0xEA, (byte)0xFA,
            (byte)0xBE, (byte)0xA0, (byte)0xB6, (byte)0xB3,
            (byte)0x9D, (byte)0xDA, (byte)0x9B, (byte)0x8B,
            (byte)0xB7, (byte)0xB8, (byte)0xB9, (byte)0xAB,
            (byte)0x64, (byte)0x65, (byte)0x62, (byte)0x66,
            (byte)0x63, (byte)0x67, (byte)0x9E, (byte)0x68,
            (byte)0x74, (byte)0x71, (byte)0x72, (byte)0x73,
            (byte)0x78, (byte)0x75, (byte)0x76, (byte)0x77,
            (byte)0xAC, (byte)0x69, (byte)0xED, (byte)0xEE,
            (byte)0xEB, (byte)0xEF, (byte)0xEC, (byte)0xBF,
            (byte)0x80, (byte)0xFD, (byte)0xFE, (byte)0xFB,
            (byte)0xFC, (byte)0xBA, (byte)0xAE, (byte)0x59,
            (byte)0x44, (byte)0x45, (byte)0x42, (byte)0x46,
            (byte)0x43, (byte)0x47, (byte)0x9C, (byte)0x48,
            (byte)0x54, (byte)0x51, (byte)0x52, (byte)0x53,
            (byte)0x58, (byte)0x55, (byte)0x56, (byte)0x57,
            (byte)0x8C, (byte)0x49, (byte)0xCD, (byte)0xCE,
            (byte)0xCB, (byte)0xCF, (byte)0xCC, (byte)0xE1,
            (byte)0x70, (byte)0xDD, (byte)0xDE, (byte)0xDB,
            (byte)0xDC, (byte)0x8D, (byte)0x8E, (byte)0xDF
    };
    public static byte[] ebcdicToAsciiBytes(byte[] e, int offset, int len) {
        byte[] a = new byte[len];
        for (int i=0; i<len; i++){
            a[i] = EBCDIC2ASCII[e[offset+i]&0xFF];
        }
        return a;
    }
    public static void asciiToEbcdic(String s, byte[] e, int offset) {
        int len = s.length();
        for (int i=0; i<len; i++){
            e[offset + i] = ASCII2EBCDIC[s.charAt(i)&0xFF];
        }
    }

    /**
     *
     * @param b
     * @return
     */
    public static String bcd2Str(byte[] b) {
        if (b==null) {
            return null;
        }
        char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; ++i) {
            sb.append(HEX_DIGITS[((b[i] & 0xF0) >>> 4)]);
            sb.append(HEX_DIGITS[(b[i] & 0xF)]);
        }

        return sb.toString();
    }


    public static String binStrToIntHexStr(String inStr){
        String outStr = "";
        if(inStr.equals("0000")){
            outStr = "0";
        }else if(inStr.equals("0001")){
            outStr = "1";
        }else if(inStr.equals("0010")){
            outStr = "2";
        }else if(inStr.equals("0011")){
            outStr = "3";
        }else if(inStr.equals("0100")){
            outStr = "4";
        }else if(inStr.equals("0101")){
            outStr = "5";
        }else if(inStr.equals("0110")){
            outStr = "6";
        }else if(inStr.equals("0111")){
            outStr = "7";
        }else if(inStr.equals("1000")){
            outStr = "8";
        }else if(inStr.equals("1001")){
            outStr = "9";
        }else if(inStr.equals("1010")){
            outStr = "A";
        }else if(inStr.equals("1011")){
            outStr = "B";
        }else if(inStr.equals("1100")){
            outStr = "C";
        }else if(inStr.equals("1101")){
            outStr = "D";
        }else if(inStr.equals("1110")){
            outStr = "E";
        }else if(inStr.equals("1111")){
            outStr = "F";
        }else{
            System.out.println("Change data fail in binStrToHexStr");
        }
        return outStr;
    }

    public static String IntHexStrTobinStr(String inStr){
        String outStr = "";
        if(inStr.equals("0")){
            outStr = "0000";
        }else if(inStr.equals("1")){
            outStr = "0001";
        }else if(inStr.equals("2")){
            outStr = "0010";
        }else if(inStr.equals("3")){
            outStr = "0011";
        }else if(inStr.equals("4")){
            outStr = "0100";
        }else if(inStr.equals("5")){
            outStr = "0101";
        }else if(inStr.equals("6")){
            outStr = "0110";
        }else if(inStr.equals("7")){
            outStr = "0111";
        }else if(inStr.equals("8")){
            outStr = "1000";
        }else if(inStr.equals("9")){
            outStr = "1001";
        }else if(inStr.equals("A")||inStr.equals("a")){
            outStr = "1010";
        }else if(inStr.equals("B")||inStr.equals("b")){
            outStr = "1011";
        }else if(inStr.equals("C")||inStr.equals("c")){
            outStr = "1100";
        }else if(inStr.equals("D")||inStr.equals("d")){
            outStr = "1101";
        }else if(inStr.equals("E")||inStr.equals("e")){
            outStr = "1110";
        }else if(inStr.equals("F")||inStr.equals("f")){
            outStr = "1111";
        }else{
            System.out.println("Change data fail in hexStrToBinStr");
        }
        return outStr;
    }


    public static String strNumToHexStr(String s){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<s.length();i++){
            char temp = s.charAt(i);
            int asciiNum = temp;
            int hexNum = asciiNum - 18;
            stringBuilder.append(hexNum);
        }
        return stringBuilder.toString();
    }


    public static byte[] writeUTFSpecial(String str) throws IOException {
        str = str.toUpperCase();
        int strlen = str.length();
        int utflen = 0;
        int c, count = 0;

        /* use charAt instead of copying String to char array */
        for (int i = 0; i < strlen; i++) {
            c = str.charAt(i);
            if(c>=65){
                c = c - 55;
            }else{
                c = c - 48;
            }
            //System.out.println(i+"汪："+c);

            if ((c >= 0x0001) && (c <= 0x007F)) {
                utflen++;
            } else if (c > 0x07FF) {
                utflen += 3;
                //System.out.println("+3");
            } else {
                utflen += 2;
                //System.out.println("+2");
            }
        }

        if (utflen > 65535)
            throw new UTFDataFormatException(
                    "encoded string too long: " + utflen + " bytes");

        byte[] bytearr = null;
        //System.out.println("utflen:"+utflen);
        int halfLength = strlen/2;
        int newLength = halfLength+2;
        bytearr = new byte[newLength];//这个+1的逻辑是因为拓展了两位，除以2变成只拓展1位，因此补上1位


        bytearr[count++] = (byte) ((halfLength >>> 8) & 0xFF);
        //System.out.println("位置1："+bytearr[0]);
        bytearr[count++] = (byte) ((halfLength >>> 0) & 0xFF);
        System.out.println("位置2："+bytearr[1]);
        System.out.println("长度："+strlen);

        int i=0;
        for (i=0; i<strlen; i=i+2) {
            int c1 = str.charAt(i);
            int c2 = str.charAt(i+1);
            if(c1>=65){
                c1 = c1 - 55;
            }else{
                c1 = c1-48;
            }
            if(c2>=65){
                c2 = c2-55;
            }else{
                c2 = c2 - 48;
            }
            bytearr[count++] = (byte) (c1*16+c2);
        }

//        for (;i < strlen; i=i+2){
//            int c3 = str.charAt(i);
//            int c4 = str.charAt(i+1);
//            c3 = c3 - 48;
//            c4 = c4 - 48;
//            c = c3*16+c4;
//            if ((c >= 0x0001) && (c <= 0x007F)) {
//                System.out.println("执行此处");
//                bytearr[count++] = (byte) c;
//            } else if (c > 0x07FF) {
//                bytearr[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
//                bytearr[count++] = (byte) (0x80 | ((c >>  6) & 0x3F));
//                bytearr[count++] = (byte) (0x80 | ((c >>  0) & 0x3F));
//            } else {
//                bytearr[count++] = (byte) (0xC0 | ((c >>  6) & 0x1F));
//                bytearr[count++] = (byte) (0x80 | ((c >>  0) & 0x3F));
//            }
//        }


        //out.write(bytearr, 0, utflen+2);

        return bytearr;
    }
}
