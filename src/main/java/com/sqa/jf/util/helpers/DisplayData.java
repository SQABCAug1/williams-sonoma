package com.sqa.jf.util.helpers;

public class DisplayData {
	// Convert the data and return the converted array with new type
	public static Object[][] convertIntArray(int[][] data) {
		// TODO Return Object[][] based on int[][] being passed
		return null;
	}

	// Display the data passed to the method to the console
	public static void objectArray(Object[][] data) {
		// TODO Use system out calls to display each element inside the
		// Object[][] array being passed in/
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				System.out.print("\t" + data[i][j]);
			}
			System.out.println();
		}

	}
}
