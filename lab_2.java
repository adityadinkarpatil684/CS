import java.io.*;

public class lab_2 {
    int rawKey[] = {1, 0, 1, 0, 0, 0, 0, 0, 1, 0};
    int P10[] = {3, 5, 2, 7, 4, 10, 1, 9, 8, 6};
    int P8[] = {6, 3, 7, 4, 8, 5, 10, 9};

    int k1[] = new int[8];
    int k2[] = new int[8];

    int[] IP = {2, 6, 3, 1, 4, 8, 5, 7};
    int[] EP = {4, 1, 2, 3, 2, 3, 4, 1};
    int[] P4 = {2, 4, 3, 1};
    int[] IP_inv = {4, 1, 3, 5, 7, 2, 8, 6};

    int[][] S0 = {
        {1, 0, 3, 2},
        {3, 2, 1, 0},
        {0, 2, 1, 3},
        {3, 1, 3, 2}
    };

    int[][] S1 = {
        {0, 1, 2, 3},
        {2, 0, 1, 3},
        {3, 0, 1, 0},
        {2, 1, 0, 3}
    };

    void generateKeys() {
        int tempKey[] = new int[10];
        for (int i = 0; i < 10; i++) {
            tempKey[i] = rawKey[P10[i] - 1];
        }

        int left[] = new int[5];
        int right[] = new int[5];

        for (int i = 0; i < 5; i++) {
            left[i] = tempKey[i];
            right[i] = tempKey[i + 5];
        }

        int[] ls1 = rotate(left, 1);
        int[] rs1 = rotate(right, 1);
        for (int i = 0; i < 5; i++) {
            tempKey[i] = ls1[i];
            tempKey[i + 5] = rs1[i];
        }
        for (int i = 0; i < 8; i++) {
            k1[i] = tempKey[P8[i] - 1];
        }

        int[] ls2 = rotate(ls1, 2);
        int[] rs2 = rotate(rs1, 2);
        for (int i = 0; i < 5; i++) {
            tempKey[i] = ls2[i];
            tempKey[i + 5] = rs2[i];
        }
        for (int i = 0; i < 8; i++) {
            k2[i] = tempKey[P8[i] - 1];
        }

        System.out.println("Key 1:");
        for (int i = 0; i < 8; i++) System.out.print(k1[i] + " ");
        System.out.println("\nKey 2:");
        for (int i = 0; i < 8; i++) System.out.print(k2[i] + " ");
    }

    int[] rotate(int[] arr, int n) {
        while (n > 0) {
            int temp = arr[0];
            for (int i = 0; i < arr.length - 1; i++) {
                arr[i] = arr[i + 1];
            }
            arr[arr.length - 1] = temp;
            n--;
        }
        return arr;
    }

    int[] encrypt(int[] pt) {
        int[] reordered = new int[8];
        for (int i = 0; i < 8; i++) {
            reordered[i] = pt[IP[i] - 1];
        }

        int[] first = complexfn(reordered, k1);
        int[] swapped = swapNibbles(first, 4);
        int[] second = complexfn(swapped, k2);
        int[] ct = new int[8];

        for (int i = 0; i < 8; i++) {
            ct[i] = second[IP_inv[i] - 1];
        }
        return ct;
    }

    String toBinary(int val) {
        return switch (val) {
            case 0 -> "00";
            case 1 -> "01";
            case 2 -> "10";
            default -> "11";
        };
    }

    int[] complexfn(int[] bits, int[] subKey) {
        int[] left = new int[4], right = new int[4];
        for (int i = 0; i < 4; i++) {
            left[i] = bits[i];
            right[i] = bits[i + 4];
        }

        int[] expanded = new int[8];
        for (int i = 0; i < 8; i++) {
            expanded[i] = right[EP[i] - 1];
        }

        for (int i = 0; i < 8; i++) {
            expanded[i] ^= subKey[i];
        }

        int[] l1 = new int[4], r1 = new int[4];
        for (int i = 0; i < 4; i++) {
            l1[i] = expanded[i];
            r1[i] = expanded[i + 4];
        }

        int row, col, val;
        row = Integer.parseInt("" + l1[0] + l1[3], 2);
        col = Integer.parseInt("" + l1[1] + l1[2], 2);
        val = S0[row][col];
        String s_l = toBinary(val);

        row = Integer.parseInt("" + r1[0] + r1[3], 2);
        col = Integer.parseInt("" + r1[1] + r1[2], 2);
        val = S1[row][col];
        String s_r = toBinary(val);

        int[] combined = new int[4];
        for (int i = 0; i < 2; i++) {
            combined[i] = Character.getNumericValue(s_l.charAt(i));
            combined[i + 2] = Character.getNumericValue(s_r.charAt(i));
        }

        int[] permuted = new int[4];
        for (int i = 0; i < 4; i++) {
            permuted[i] = combined[P4[i] - 1];
        }

        for (int i = 0; i < 4; i++) {
            left[i] ^= permuted[i];
        }

        int[] result = new int[8];
        for (int i = 0; i < 4; i++) {
            result[i] = left[i];
            result[i + 4] = right[i];
        }
        return result;
    }

    int[] swapNibbles(int[] arr, int n) {
        int[] l = new int[n], r = new int[n];
        for (int i = 0; i < n; i++) {
            l[i] = arr[i];
            r[i] = arr[i + n];
        }
        int[] res = new int[2 * n];
        for (int i = 0; i < n; i++) {
            res[i] = r[i];
            res[i + n] = l[i];
        }
        return res;
    }

    int[] decrypt(int[] ct) {
        int[] temp = new int[8];
        for (int i = 0; i < 8; i++) {
            temp[i] = ct[IP[i] - 1];
        }

        int[] first = complexfn(temp, k2);
        int[] swapped = swapNibbles(first, 4);
        int[] second = complexfn(swapped, k1);
        int[] pt = new int[8];

        for (int i = 0; i < 8; i++) {
            pt[i] = second[IP_inv[i] - 1];
        }
        return pt;
    }

    public static void main(String[] args) {
        lab_2 obj = new lab_2();
        obj.generateKeys();

        int[] input = {1, 0, 0, 1, 0, 1, 1, 1};

        System.out.println("\nPlain Text:");
        for (int i : input) System.out.print(i + " ");

        int[] enc = obj.encrypt(input);

        System.out.println("\nEncrypted Text:");
        for (int i : enc) System.out.print(i + " ");

        int[] dec = obj.decrypt(enc);

        System.out.println("\nDecrypted Text:");
        for (int i : dec) System.out.print(i + " ");
    }
}
