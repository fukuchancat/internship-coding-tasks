package com.mynetgear.fukuchan.internshipcodingtasks.sample;

import org.deeplearning4j.clustering.cluster.Point;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.text.tokenization.tokenizerfactory.JapaneseTokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文書をベクトル化してpoints.csvに出力する
 */
public class DataSetMaker {
	public static List<Point> load(Path path) throws IOException {
		return Files.lines(path).map(line -> line.split(",")).map(split -> {
			String id = split[0];
			float[] floats = new float[split.length - 1];
			for (int i = 0; i < floats.length; i++)
				floats[i] = Float.parseFloat(split[i + 1]);
			return new Point(id, Nd4j.create(floats));
		}).collect(Collectors.toList());
	}

	public static void save(List<Point> points) throws IOException {
		List<String> lines = points.stream().map(point -> {
			String features = Arrays.stream(point.getArray().toDoubleVector())
					.mapToObj(Double::toString)
					.collect(Collectors.joining(","));
			return point.getId() + "," + features;
		}).collect(Collectors.toList());
		Files.write(Paths.get("src/main/resources/points.csv"), lines);
	}

	public static void main(String[] args) throws IOException {
		Path input = Paths.get("src/main/resources/Sample.csv");
		JapaneseTokenizerFactory factory = new JapaneseTokenizerFactory(true);
		CSVLabelAwareIterator iterator = new CSVLabelAwareIterator(input);

		ParagraphVectors paragraph = new ParagraphVectors.Builder().minWordFrequency(1)
				.epochs(1)
				.learningRate(0.025)
				.iterate(iterator)
				.trainWordVectors(false)
				.tokenizerFactory(factory)
				.build();
		paragraph.fit();

		List<Point> points = new ArrayList<>();
		for (iterator = new CSVLabelAwareIterator(input); iterator.hasNext(); ) {
			INDArray matrix = paragraph.inferVector(iterator.nextSentence());
			Point point = new Point(iterator.currentLabel(), matrix);
			points.add(point);
		}
		save(points);
	}
}
