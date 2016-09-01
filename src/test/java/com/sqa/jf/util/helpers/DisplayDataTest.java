package com.sqa.jf.util.helpers;

import org.testng.annotations.Test;

public class DisplayDataTest {

	@Test(enabled = false)
	public void convertIntArray() {
		Object[][] newData = DisplayData.convertIntArray(intData());
		DisplayData.objectArray(newData);
	}

	// public Object[][] data() {
	// // TODO Return a Object[][], not null
	// return null;
	// }
	public Object[][] data() {
		return new Object[][] { new Object[] { "Sample Test 1", 3.25, 6, 9L },
				new Object[] { "Sample Test 2", 15.75, 4, 19L }, new Object[] { "Sample Test 3", -15.0, -5, -20L },
				new Object[] { "Sample Test 4", 100.5, -100, 0L }, new Object[] { "Sample Test 5", -100.75, 200, 100L },
				new Object[] { "Sample Test 6", 300.9, 900000, 910000L } };
	}

	// public int[][] intData() {
	// // TODO Return a int[][], not null
	// return null;
	// }
	public int[][] intData() {
		return new int[][] { new int[] { 3, 6, 9 }, new int[] { 15, 4, 19 }, new int[] { -15, -5, -20 },
				new int[] { 100, -100, 0 }, new int[] { -100, 200, 100 }, new int[] { 10000, 900000, 910000 } };
	}

	@Test
	public void objectArray() {
		DisplayData.objectArray(data());
	}
}
