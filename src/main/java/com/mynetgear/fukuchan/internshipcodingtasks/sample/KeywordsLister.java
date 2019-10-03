package com.mynetgear.fukuchan.internshipcodingtasks.sample;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 各クラスタごとにユニークなキーワードの出現回数を調べ、keywords.csvに出力する
 */
@Getter
@Setter
public class KeywordsLister {

	public static void main(String[] args) throws IOException {
		Path clusterPath = Paths.get("src/main/resources/cluster.csv");
		Path samplePath = Paths.get("src/main/resources/Sample.csv");

		// ドキュメントIDからクラスタIDのマップを作る
		Map<String, String> clusterMap = Files.lines(clusterPath)
				.map(line -> line.split(","))
				.collect(Collectors.toMap(split -> split[1], split -> split[0]));

		// キーワードごとの出現回数をクラスタごとに数えるマップを作る
		Map<String, Map<String, Integer>> countMap = new HashMap<>();

		CSVLabelAwareIterator iterator = new CSVLabelAwareIterator(samplePath);
		Tokenizer tokenizer = new Tokenizer();
		while (iterator.hasNext()) {
			// クラスタIDを取得
			String cluster = clusterMap.get(iterator.currentLabel());

			// 該当クラスタのキーワード出現回数マップを取得、なければ作る
			Map<String, Integer> map;
			if (countMap.containsKey(cluster)) {
				map = countMap.get(cluster);
			} else {
				map = new HashMap<>();
				countMap.put(cluster, map);
			}

			// 形態素解析
			for (Token token : tokenizer.tokenize(iterator.nextSentence())) {
				// 原形を取得し、出現回数を加算
				String baseForm = token.getBaseForm();
				map.merge(baseForm, 1, Integer::sum);
			}
		}

		// ユニーク化のため、全てのキーワードを取得してからグルーピングする
		Map<String, List<String>> grouping = countMap.values()
				.stream()
				.map(Map::keySet)
				.flatMap(Set::stream)
				.collect(Collectors.groupingBy(s -> s));

		// グルーピング結果の長さが2以上になったものが重複している
		Set<String> duplicates = grouping.entrySet()
				.stream()
				.filter(e -> e.getValue().size() > 1)
				.map(Map.Entry::getKey)
				.collect(Collectors.toSet());

		// 重複したキーワードを削除
		for (Map<String, Integer> map : countMap.values()) {
			for (String duplicate : duplicates) {
				map.remove(duplicate);
			}
		}

		// ファイル出力
		List<String> lines = countMap.entrySet()
				.stream()
				.flatMap(map -> map.getValue()
						.entrySet()
						.stream()
						.map(e -> map.getKey() + "," + e.getKey() + "," + e.getValue()))
				.collect(Collectors.toList());
		Files.write(Paths.get("src/main/resources/keywords.csv"), lines);
	}
}
