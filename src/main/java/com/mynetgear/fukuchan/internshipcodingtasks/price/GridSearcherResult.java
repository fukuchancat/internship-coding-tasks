package com.mynetgear.fukuchan.internshipcodingtasks.price;

import lombok.Getter;
import lombok.Setter;

/**
 * グリッドサーチのスコア
 */
@Getter
@Setter
public class GridSearcherResult {
	private double c;
	private double g;
	private double p;
	private double mse;

	public GridSearcherResult(double c, double g, double p, double mse) {
		this.c = c;
		this.g = g;
		this.p = p;
		this.mse = mse;
	}

	@Override
	public String toString() {
		return "-c " + c + " -g " + g + " -p " + p + " | MSE = " + mse;
	}
}
