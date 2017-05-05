package distributedHashTable;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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

	private static String getRandomStr() {
		return "" + (Math.random() * 1000000);
	}

	private void testMiddle(String a, String b) {
		Sha256 x = new Sha256(a);
		Sha256 y = new Sha256(b);
		Sha256 mid = Sha256.middle(x, y);
		assertTrue(Sha256.inOrder(x, mid, y));
	}

}