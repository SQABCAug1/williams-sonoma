package com.sqa.jf;

public class PriceCalculator {
	public static double calcSum(double[] prices, double salesTax) {
		double total = 0;
		for (int i = 0; i < prices.length; i++) {
			total += prices[i];
		}
		total += (total * salesTax);
		System.err.println("Test");
		return total;
	}
}
