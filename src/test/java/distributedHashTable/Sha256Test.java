package distributedHashTable;

import static org.junit.Assert.*;

import org.junit.Test;

public class Sha256Test {

	@Test
	public void test() {
		Sha256 x = new Sha256("afeaf");
		Sha256 y = new Sha256("afeafd");
		
		assertNotEquals(0, x.compareTo(y));
	}

}
