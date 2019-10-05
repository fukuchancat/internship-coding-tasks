package com.mynetgear.fukuchan.internshipcodingtasks.price;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * グリッドサーチを行う
 */
@Getter
@Setter
public class GridSearcher {
	private String input;

	public GridSearcher(String input) {
		this.input = input;
	}

	public GridSearcherResult search() {
		double c = 10;
		int order = 0;
		List<GridSearcherResult> results = new ArrayList<>();

		while (-15 <= c && c <= 32 && order <= 32) {
			// 既に計算した値の場合は計算せず反転
			List<Double> list = new ArrayList<>();
			for (GridSearcherResult result : results) {
				list.add(result.getP());
			}
			if (list.contains(c)) {
				order++;
			} else {
				// スコアが減ったら反転、スコアが変わらなくなったら終了
				GridSearcherResult result = search(c);
				double current = result.getMse();
				double last = results.size() > 0 ? results.get(results.size() - 1).getMse() : -1;

				results.add(result);
				if (current == last)
					break;
			}

			c += 1 * Math.pow(-0.5, order);
		}

		// 最もスコアの良かったものを返す
		return results.stream().min(Comparator.comparing(GridSearcherResult::getMse)).orElse(results.get(0));
	}

	private GridSearcherResult search(double c) {
		double g = -10;
		int order = 0;
		List<GridSearcherResult> results = new ArrayList<>();

		while (-25 <= g && g <= 0 && order <= 32) {
			// 既に計算した値の場合は計算せず反転
			List<Double> list = new ArrayList<>();
			for (GridSearcherResult result : results) {
				list.add(result.getP());
			}
			if (list.contains(g)) {
				order++;
			} else {
				// スコアが減ったら反転、スコアが変わらなくなったら終了
				GridSearcherResult result = search(c, g);
				double current = result.getMse();
				double last = results.size() > 0 ? results.get(results.size() - 1).getMse() : -1;

				results.add(result);
				if (current == last)
					break;
			}

			g += 1 * Math.pow(-0.5, order);
		}

		// 最もスコアの良かったものを返す
		return results.stream().min(Comparator.comparing(GridSearcherResult::getMse)).orElse(results.get(0));
	}

	private GridSearcherResult search(double c, double g) {
		double e = -10;
		int order = 0;
		List<GridSearcherResult> results = new ArrayList<>();

		while (-25 <= e && e <= 15 && order <= 32) {
			// 既に計算した値の場合は計算せず反転
			List<Double> list = new ArrayList<>();
			for (GridSearcherResult result : results) {
				list.add(result.getP());
			}
			if (list.contains(e)) {
				order++;
			} else {
				// スコアが減ったら反転、スコアが変わらなくなったら終了
				GridSearcherResult result = search(c, g, e);
				double current = result.getMse();
				double last = results.size() > 0 ? results.get(results.size() - 1).getMse() : -1;

				results.add(result);
				if (current == last)
					break;
			}

			e += 1 * Math.pow(-0.5, order);
		}

		// 最もスコアの良かったものを返す
		return results.stream().min(Comparator.comparing(GridSearcherResult::getMse)).orElse(results.get(0));
	}

	private GridSearcherResult search(double c, double g, double p) {
		// thundersvmで3交差検証
		List<String> commands = new ArrayList<>();
		commands.add("thundersvm-train");
		commands.add("-s");
		commands.add("3");
		commands.add("-v");
		commands.add("3");
		commands.add("-c");
		commands.add(Double.toString(Math.pow(2, c)));
		commands.add("-g");
		commands.add(Double.toString(Math.pow(2, g)));
		commands.add("-p");
		commands.add(Double.toString(Math.pow(2, p)));
		commands.add(input);

		// MSEの出力に一致する正規表現
		Pattern msePattern = Pattern.compile("Cross Mean Squared Error = (\\S+)");

		// thundersvmの出力をファイルに転送
		ProcessBuilder builder = new ProcessBuilder(commands);
		Path output = Paths.get("src/main/resources/out.txt");
		builder.redirectOutput(output.toFile());

		double mse = 0.0;
		try {
			// thundersvmの実行結果からMSEを読み取る
			Process process = builder.start();
			process.waitFor();
			mse = Files.lines(output)
					.map(msePattern::matcher)
					.filter(Matcher::find)
					.map(m -> m.group(1))
					.mapToDouble(Double::parseDouble)
					.reduce((a, b) -> b)
					.orElse(0.0);
		} catch (InterruptedException | IOException err) {
			err.printStackTrace();
		}

		return new GridSearcherResult(c, g, p, mse);
	}

	public static void main(String[] args) {
		// グリッドサーチを行う
		GridSearcher searcher = new GridSearcher("src/main/resources/input.scale");
		GridSearcherResult result = searcher.search();

		// 一番良い結果を出力
		System.out.println(result);
	}
}
