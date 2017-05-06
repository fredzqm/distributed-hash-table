package util;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

import util.Sha256;

public class Sha256Test {

	@Test
	public void test() {
		Sha256 x = new Sha256("afeaf");
		Sha256 y = new Sha256("afeafd");

		assertNotEquals(0, x.compareTo(y));
	}

	@Test
	public void testMiddle() {
		for (int i = 0; i < 100000; i++) {
			testMiddle(getRandomStr(), getRandomStr());
		}
	}

	private void testMiddle(String a, String b) {
		Sha256 x = new Sha256(a);
		Sha256 y = new Sha256(b);
		Sha256 mid = Sha256.middle(x, y);
		assertTrue(Sha256.inOrder(x, mid, y));
	}

	@Test
	public void testMiddle2() {
		for (int i = 0; i < 100000; i++) {
			testMiddle2(getRandomStr());
		}
	}

	private void testMiddle2(String a) {
		Sha256 x = new Sha256(a);
		Sha256 mid = Sha256.middle(x, x);
		assertNotEquals(x, mid);
		Sha256 y = Sha256.middle(mid, mid);
		assertEquals(x, y);
	}

	@Test
	public void testMaxShaNum() {
		BigInteger max = Sha256.getMaxPosSha();
		BigInteger same = max.add(max).shiftRight(1);
		assertEquals(max, same);
	}

	private static String getRandomStr() {
		return "" + (Math.random() * 1000000);
	}

}