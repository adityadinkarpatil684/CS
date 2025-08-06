import java.util.Scanner;

public class SimpleRSA {

    static boolean isPrime(int num) {
        if (num <= 1) return false;
        if (num <= 3) return true;
        if (num % 2 == 0 || num % 3 == 0) return false;

        for (int i = 5; i * i <= num; i += 6) {
            if (num % i == 0 || num % (i + 2) == 0)
                return false;
        }
        return true;
    }

    static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    static int findE(int phi) {
        for (int e = 2; e < phi; e++) {
            if (gcd(e, phi) == 1)
                return e;
        }
        throw new RuntimeException("No valid e found.");
    }

    static int findD(int e, int phi) {
        for (int d = 1; d < phi; d++) {
            if ((d * e) % phi == 1)
                return d;
        }
        throw new RuntimeException("No valid d found.");
    }

    static long modPow(long base, long exp, long mod) {
        long result = 1;
        while (exp > 0) {
            if (exp % 2 == 1) {
                result = (result * base) % mod;
            }
            base = (base * base) % mod;
            exp /= 2;
        }
        return result;
    }

    static void rsaOperation(int message, int e, int d, int n) {
        long encrypted = modPow(message, e, n);
        long decrypted = modPow(encrypted, d, n);

        System.out.println("Original message: " + message);
        System.out.println("Encrypted message: " + encrypted);
        System.out.println("Decrypted message: " + decrypted);

        if (message == decrypted) {
            System.out.println("Test passed!");
        } else {
            System.out.println("Test failed.");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int p, q;

        // Input prime numbers p and q
        do {
            System.out.print("Enter prime number p: ");
            p = scanner.nextInt();
            System.out.print("Enter prime number q: ");
            q = scanner.nextInt();

            if (!isPrime(p) || !isPrime(q) || p == q) {
                System.out.println("Both numbers must be distinct prime numbers.");
            }
        } while (!isPrime(p) || !isPrime(q) || p == q);

        int n = p * q;
        int phi = (p - 1) * (q - 1);

        int e = findE(phi);
        int d = findD(e, phi);

        System.out.println("Public Key: (" + e + ", " + n + ")");
        System.out.println("Private Key: (" + d + ", " + n + ")");

        System.out.print("Enter the message (integer less than " + n + "): ");
        int message = scanner.nextInt();

        if (message >= n) {
            System.out.println("Message must be less than " + n + ".");
            return;
        }

        rsaOperation(message, e, d, n);

        scanner.close();
    }
}
