package com.mynetgear.fukuchan.internshipcodingtasks.sample;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * クラスタリング結果の参照用に、クラスタIDと分類されたテキストの対応表を作ってtable.csvに出力する
 */
public class TableMaker {
	public static void main(String[] args) throws IOException {
		Path clusterPath = Paths.get("src/main/resources/cluster.csv");
		Path samplePath = Paths.get("src/main/resources/sample.csv");
		Path resultPath = Paths.get("src/main/resources/table.csv");

		CSVLabelAwareIterator iterator = new CSVLabelAwareIterator(samplePath);
		PrintWriter writer = new PrintWriter(Files.newBufferedWriter(resultPath));

		// ドキュメントIDからクラスタIDのマップを作る
		Map<String, String> clusterMap = Files.lines(clusterPath)
				.map(line -> line.split(","))
				.collect(Collectors.toMap(split -> split[1], split -> split[0]));

		// ファイル出力
		while (iterator.hasNext()) {
			String cluster = clusterMap.get(iterator.currentLabel());
			String text = iterator.nextSentence().replace("\"", "\\\"");
			writer.println("\"" + cluster + "\",\"" + text + "\"");
		}
	}
}
