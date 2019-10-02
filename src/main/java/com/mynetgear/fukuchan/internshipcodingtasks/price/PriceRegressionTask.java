package com.mynetgear.fukuchan.internshipcodingtasks.price;

import libsvm.svm_train;

import java.io.IOException;

public class PriceRegressionTask {
	public static void main(String[] args) throws IOException {
		// パラメータを設定
		String[] arguments = {"-s", "3", "-v", "3", "-c", "33554432", "-g", "0.125", "-e", "0.0009765625", "src/main/resources/input.scale"};
		svm_train.main(arguments);
	}
}
