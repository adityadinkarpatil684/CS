import java.util.*;

public class SAES {

    static String[][] sbox = {
        {"1001", "0100", "1010", "1011"},
        {"1101", "0001", "1000", "0101"},
        {"0110", "0010", "0000", "0011"},
        {"1100", "1110", "1111", "0111"}
    };

    static String[][] inversesbox = {
        {"1010", "0101", "1001", "1011"},
        {"0001", "0111", "1000", "1111"},
        {"0110", "0000", "0010", "0011"},
        {"1100", "0100", "1101", "1110"}
    };

    static String[][] mixColumnTable = {
        {"2", "4", "6", "8", "A", "C", "E", "3", "1", "7", "5", "B", "9", "F", "D"},
        {"4", "8", "C", "3", "7", "B", "F", "6", "2", "E", "A", "5", "1", "D", "9"},
        {"9", "1", "8", "2", "B", "3", "A", "4", "D", "5", "C", "6", "F", "7", "E"}
    };

    static String k0 = "", k1 = "", k2 = "";
    static String rcon1 = "10000000";
    static String rcon2 = "00110000";

    static String XOR(String a, String b) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < a.length(); i++) {
            result.append(a.charAt(i) == b.charAt(i) ? '0' : '1');
        }
        return result.toString();
    }


static String binaryToHex(String binary) {
    int decimal = Integer.parseInt(binary, 2);  
    String hex = Integer.toHexString(decimal);  
    return hex.toUpperCase();                   
}

static String hexToBinary(String hex) {
    int decimal = Integer.parseInt(hex, 16);
    String binary = Integer.toBinaryString(decimal);
    while (binary.length() < 4) {
        binary = "0" + binary;
    }
    return binary;
}

    static void keyGen(String key) {
        String w0 = key.substring(0, 8);
        String w1 = key.substring(8, 16);

        String w2 = XOR(XOR(w0, rcon1), subnib(rotnib(w1)));
        String w3 = XOR(w2, w1);

        String w4 = XOR(XOR(w2, rcon2), subnib(rotnib(w3)));
        String w5 = XOR(w4, w3);

        k0 = w0 + w1;
        k1 = w2 + w3;
        k2 = w4 + w5;
    }

    static String rotnib(String input) {
        int len = input.length();
        return input.substring(len / 2) + input.substring(0, len / 2);
    }

    static String subnib(String input) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i += 4) {
            int row = Integer.parseInt(input.substring(i, i + 2), 2);
            int col = Integer.parseInt(input.substring(i + 2, i + 4), 2);
            result.append(sbox[row][col]);
        }
        return result.toString();
    }

    static String inversesubnib(String input) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i += 4) {
            int row = Integer.parseInt(input.substring(i, i + 2), 2);
            int col = Integer.parseInt(input.substring(i + 2, i + 4), 2);
            result.append(inversesbox[row][col]);
        }
        return result.toString();
    }

    static String shiftRow(String input) {
        return input.substring(0, 4) + input.substring(12, 16) + input.substring(8, 12) + input.substring(4, 8);
    }

    static String mixcol(String input) {
        String[][] a = new String[2][2];
        a[0][0] = input.substring(0, 4);
        a[1][0] = input.substring(4, 8);
        a[0][1] = input.substring(8, 12);
        a[1][1] = input.substring(12, 16);

        String s1 = XOR(a[0][0], hexToBinary(mixColumnTable[1][Integer.parseInt(a[0][1], 2) - 1]));
        String s2 = XOR(a[1][0], hexToBinary(mixColumnTable[1][Integer.parseInt(a[0][0], 2) - 1]));
        String s3 = XOR(a[0][1], hexToBinary(mixColumnTable[1][Integer.parseInt(a[1][1], 2) - 1]));
        String s4 = XOR(a[1][1], hexToBinary(mixColumnTable[1][Integer.parseInt(a[0][1], 2) - 1]));

        return s1 + s2 + s3 + s4;
    }

    static String inverse_mixcol(String input) {
        String[][] a = new String[2][2];
        a[0][0] = input.substring(0, 4);
        a[1][0] = input.substring(4, 8);
        a[0][1] = input.substring(8, 12);
        a[1][1] = input.substring(12, 16);

        String s1 = XOR(hexToBinary(mixColumnTable[2][Integer.parseInt(a[0][0], 2) - 1]),
                        hexToBinary(mixColumnTable[0][Integer.parseInt(a[1][0], 2) - 1]));
        String s2 = XOR(hexToBinary(mixColumnTable[2][Integer.parseInt(a[1][0], 2) - 1]),
                        hexToBinary(mixColumnTable[0][Integer.parseInt(a[0][0], 2) - 1]));
        String s3 = XOR(hexToBinary(mixColumnTable[2][Integer.parseInt(a[0][1], 2) - 1]),
                        hexToBinary(mixColumnTable[0][Integer.parseInt(a[1][1], 2) - 1]));
        String s4 = XOR(hexToBinary(mixColumnTable[2][Integer.parseInt(a[1][1], 2) - 1]),
                        hexToBinary(mixColumnTable[0][Integer.parseInt(a[0][1], 2) - 1]));

        return s1 + s2 + s3 + s4;
    }

    static String encryption(String plain, String key) {
        keyGen(key);

        plain = XOR(plain, k0);

        plain = mixcol(shiftRow(subnib(plain)));
        plain = XOR(k1, plain);

        plain = XOR(k2, shiftRow(subnib(plain)));

        return plain;
    }

    static String decryption(String cipher) {
        cipher = XOR(cipher, k2);
        cipher = inverse_mixcol(XOR(k1, inversesubnib(shiftRow(cipher))));
        cipher = XOR(k0, inversesubnib(shiftRow(cipher)));
        return cipher;
    }

    public static void main(String[] args) {
        String plain = "1101011100101000"; 
        String key = "0100101011110101";  

        System.out.println("Plain:  " + plain);
        System.out.println("Key:    " + key);

        if (plain.length() != 16 || key.length() != 16) {
            System.out.println("Input lengths are invalid.");
            return;
        }

        String cipher = encryption(plain, key);
        System.out.println("Cipher: " + cipher);

        String decrypted = decryption(cipher);
        System.out.println("Decrypted: " + decrypted);
    }
}
