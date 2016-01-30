package dwt.pixel;

import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

public final class Shamir {

	public static String data = "";
	public static String store = "";

	public final class SecretShare {

		public SecretShare(){
			this.num = 0;
			this.share = null;
		}

		public SecretShare(final int num, final BigInteger share) {
			this.num = num;
			this.share = share;
		}

		public int getNum() {
			return num;
		}

		public BigInteger getShare() {
			return share;
		}

		@Override
		public String toString() {
			data += "["+ num + "," + share + "]";
			store += share+" ";
			//return "SecretShare" +num +" [num=" + num + ", share=" + share + "]";
			return store;
		}

		private final int num;
		private final BigInteger share;
	}

	public Shamir(final int k, final int n) {
		this.k = k;
		this.n = n;

		random = new Random();
	}

	public SecretShare[] split(final BigInteger secret) {
		final int modLength = secret.bitLength() + 1;

		prime = new BigInteger(modLength, CERTAINTY, random);
		prime = BigInteger.valueOf(17);
		final BigInteger[] coeff = new BigInteger[k - 1];

		//	System.out.println("Prime Number: " + prime);

		for (int i = 0; i < k - 1; i++) {
			coeff[i] = randomZp(prime);
			//	System.out.println("a" + (i + 1) + ": " + coeff[i]);
		}

		final SecretShare[] shares = new SecretShare[n];
		for (int i = 1; i <= n; i++) {
			BigInteger accum = secret;

			for (int j = 1; j < k; j++) {
				final BigInteger t1 = BigInteger.valueOf(i).modPow(BigInteger.valueOf(j), prime);
				final BigInteger t2 = coeff[j - 1].multiply(t1).mod(prime);

				accum = accum.add(t2).mod(prime);
			}
			shares[i - 1] = new SecretShare(i - 1, accum);
			System.out.println("Shamir Shares " + shares[i - 1]);
		}

		return shares;
	}

	public BigInteger getPrime() {
		return prime;
	}

	public BigInteger combine(final SecretShare[] shares, final BigInteger primeNum) {
		BigInteger accum = BigInteger.ZERO;
		for (int i = 0; i < k; i++) {
			BigInteger num = BigInteger.ONE;
			BigInteger den = BigInteger.ONE;

			for (int j = 0; j < k; j++) {
				if (i != j) {
					num = num.multiply(BigInteger.valueOf(-j - 1)).mod(primeNum);
					den = den.multiply(BigInteger.valueOf(i - j)).mod(primeNum);
				}
			}

				System.out.println("den: " + den + ", num: " + den + ", inv: " + den.modInverse(primeNum));
			final BigInteger value = shares[i].getShare();

			final BigInteger tmp = value.multiply(num).multiply(den.modInverse(primeNum)).mod(primeNum);
			accum = accum.add(primeNum).add(tmp).mod(primeNum);

			 	System.out.println("value: " + value + ", tmp: " + tmp + ", accum: " + accum);
		}

			 System.out.println("The secret is: " + accum);

		return accum;
	}

	private BigInteger randomZp(final BigInteger p) {
		while (true) {
			final BigInteger r = new BigInteger(p.bitLength(), random);
			if (r.compareTo(BigInteger.ZERO) > 0 && r.compareTo(p) < 0) {
				return r;
			}
		}
	}

	private final int k;
	private final int n;
	private BigInteger prime;


	private final Random random;

	private static final int CERTAINTY = 50;


	// Fahad
	public static String getShares(int k,int n,BigInteger secret) {

		final Shamir shamir = new Shamir(k, n);

		// final BigInteger secret = new BigInteger("1234567890123456789012345678901234567890");
		// final BigInteger secret = new BigInteger(secret1);
		final SecretShare[] shares = shamir.split(secret);
		final BigInteger prime = shamir.getPrime();

		//  final Shamir shamir2 = new Shamir(11, 20);
		final Shamir shamir2 = new Shamir(k, n);
		final BigInteger result = shamir2.combine(shares, prime);

		return store;
	}
	
	public static BigInteger combined(final SecretShare[] shares, final BigInteger primeNum, int k1) {
		BigInteger accum = BigInteger.ZERO;
		for (int i = 0; i < k1; i++) {
			BigInteger num = BigInteger.ONE;
			BigInteger den = BigInteger.ONE;

			for (int j = 0; j < k1; j++) {
				if (i != j) {
					num = num.multiply(BigInteger.valueOf(-j - 1)).mod(primeNum);
					den = den.multiply(BigInteger.valueOf(i - j)).mod(primeNum);
				}
			}

				System.out.println("den: " + den + ", num: " + den + ", inv: " + den.modInverse(primeNum));
			final BigInteger value = shares[i].getShare();

			final BigInteger tmp = value.multiply(num).multiply(den.modInverse(primeNum)).mod(primeNum);
			accum = accum.add(primeNum).add(tmp).mod(primeNum);

			 	System.out.println("value: " + value + ", tmp: " + tmp + ", accum: " + accum);
		}

			 System.out.println("The secret is: " + accum);

		return accum;
	}


	public static String createShares(int k,int n,BigInteger secret) {

		final Shamir shamir = new Shamir(k, n);

		// final BigInteger secret = new BigInteger("1234567890123456789012345678901234567890");
		// final BigInteger secret = new BigInteger(secret1);
		final SecretShare[] shares = shamir.split(secret);
		final BigInteger prime = shamir.getPrime();

		//  final Shamir shamir2 = new Shamir(11, 20);
		final Shamir shamir2 = new Shamir(k, n);
		final BigInteger result = shamir2.combine(shares, prime);

		//	System.out.println("shares bro"+data);
		return data;
	}

	public static BigInteger reConstruct(int k,int n,BigInteger secret) {

		final Shamir shamir = new Shamir(k, n);
		final SecretShare[] shares = shamir.split(secret);
		final BigInteger prime = shamir.getPrime();

		final Shamir shamir2 = new Shamir(k, n);
		final BigInteger result = shamir2.combine(shares, prime);
		return result;
	}

	public static void main(final String[] args) {

		Scanner keyboard = new Scanner(System.in);
		System.out.println("Enter value of k:");
		int k = keyboard.nextInt();
		System.out.println("Enter value of n:");
		int n = keyboard.nextInt();
		final Shamir shamir = new Shamir(k, n);

		//final BigInteger secret = new BigInteger("1234567890123456789012345678901234567890");
		final BigInteger secret = new BigInteger("9");
		final SecretShare[] shares = shamir.split(secret);
		final BigInteger prime = shamir.getPrime();

		//  final Shamir shamir2 = new Shamir(11, 20);
		final Shamir shamir2 = new Shamir(k, n);
		final BigInteger result = shamir2.combine(shares, prime);
		
		final BigInteger original = reConstruct(k, n, secret);
		System.out.println("Result :"+result);
	}
}