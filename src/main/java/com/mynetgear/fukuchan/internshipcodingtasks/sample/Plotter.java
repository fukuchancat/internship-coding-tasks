package com.mynetgear.fukuchan.internshipcodingtasks.sample;

import org.deeplearning4j.plot.BarnesHutTsne;
import org.math.plot.Plot2DPanel;
import org.math.plot.PlotPanel;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * クラスタリング結果を2次元圧縮してプロットする
 */
public class Plotter {
	public static void main(String[] args) throws IOException {
		Path colorPath = Paths.get("src/main/resources/color.csv");
		Path clusterPath = Paths.get("src/main/resources/cluster.csv");
		Path psnePath = Paths.get("src/main/resources/psne.csv");

		// クラスタごとに色を割り振る
		Map<String, Color> clusterToColor = Files.lines(colorPath)
				.skip(1)
				.map(line -> line.split(","))
				.filter(split -> !split[1].equals("White"))
				.collect(Collectors.toMap(split -> split[0], split -> Color.decode(split[2])));

		// クラスタリング結果を読み込む
		List<String> lines = Files.readAllLines(clusterPath);
		Map<String, Color> idToColor = new HashMap<>();
		List<String> ids = new ArrayList<>();
		INDArray matrix = null;
		for (String line : lines) {
			String[] split = line.split(",");

			// IDごとの色を割り振る
			idToColor.put(split[1], clusterToColor.get(split[0]));

			// 次元圧縮に使うINDArrayを作る
			if (!Files.exists(psnePath)) {
				ids.add(split[1]);
				float[] floats = new float[split.length - 2];
				for (int i = 0; i < floats.length; i++) {
					floats[i] = Float.parseFloat(split[i + 2]);
				}
				INDArray vector = Nd4j.create(floats).reshape(1, floats.length);
				matrix = matrix == null ? vector : Nd4j.concat(0, matrix, vector);
			}
		}

		if (!Files.exists(psnePath)) {
			// t-SNEで次元圧縮する
			BarnesHutTsne tsne = new BarnesHutTsne.Builder().setMaxIter(100)
					.theta(0.5)
					.normalize(true)
					.learningRate(500)
					.useAdaGrad(false)
					.build();
			tsne.fit(matrix);

			// 結果をファイル出力
			tsne.saveAsFile(ids, psnePath.toString());
		}

		// 散布図を作る
		PlotPanel plot = new Plot2DPanel();
		Files.lines(psnePath).map(line -> line.split(",")).forEach(split -> {
			Color color = idToColor.get(split[2]);
			plot.addLabel(split[2], color, Double.parseDouble(split[0]), Double.parseDouble(split[1]));
		});
		plot.setFixedBounds(0, -10000, 10000);
		plot.setFixedBounds(1, -10000, 10000);

		// 散布図を画面に出力する
		JFrame frame = new JFrame("a plot panel");
		frame.setSize(1280, 720);
		frame.setContentPane(plot);
		frame.setVisible(true);
	}
}
