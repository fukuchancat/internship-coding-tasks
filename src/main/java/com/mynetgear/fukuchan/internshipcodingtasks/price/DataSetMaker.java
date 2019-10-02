package com.mynetgear.fukuchan.internshipcodingtasks.price;

import libsvm.svm_scale;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * csvからデータセットを作成する
 */
public class DataSetMaker {
	public static void main(String[] args) throws IOException {
		// csv2つを読み込む
		List<House> houses = House.load(Paths.get("src/main/resources/Price.csv"));
		Map<String, ZipData> zipMap = ZipData.load(Paths.get("src/main/resources/uszips.csv"))
				.stream()
				.collect(Collectors.toMap(ZipData::getZip, z -> z));

		// LIBSVM形式に加工する
		List<String> lines = houses.stream().map(house -> {
			// 対応する郵便番号情報を取得
			ZipData zip = zipMap.get(house.getZipcode());

			// ラベルを書き込む
			StringBuilder builder = new StringBuilder();
			builder.append(house.getPrice());
			builder.append(" ");

			// 特徴量を書き込む
			double[] features = {house.secSaled(), house.getBedrooms(), house.getBathrooms(), house.getSqftLiving(), house.getSqftLot(), house.getFloors(), house.getView(), house.getCondition(), house.getGrade(), house.getSqftAbove(), house.getSqftBasement(), house.getYrBuilt(), house.yrElapsed(), house.getSqftLiving15(), house.getSqftLot15(), zip.getPopulation(), zip.getDensity()};
			for (int i = 0; i < features.length; i++) {
				builder.append(i);
				builder.append(":");
				builder.append(features[i]);
				builder.append(" ");
			}

			return builder.toString();
		}).collect(Collectors.toList());

		// ファイルに出力
		String out = "src/main/resources/input";
		Files.write(Paths.get(out), lines);

		// スケーリングする
		PrintStream output = new PrintStream(Files.newOutputStream(Paths.get("src/main/resources/input.scale")));
		System.setOut(output);
		svm_scale.main(new String[]{out});
	}
}
