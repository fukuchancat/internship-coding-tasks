package com.mynetgear.fukuchan.internshipcodingtasks.sample;

import org.deeplearning4j.clustering.algorithm.Distance;
import org.deeplearning4j.clustering.cluster.Cluster;
import org.deeplearning4j.clustering.cluster.ClusterSet;
import org.deeplearning4j.clustering.cluster.Point;
import org.deeplearning4j.clustering.kmeans.KMeansClustering;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * クラスタリングを行い、cluster.csvに出力する
 */
public class SampleClusteringTask {
	public static void main(String[] args) throws IOException {
		// ファイルから文書ベクトルを復元
		Path input = Paths.get("src/main/resources/points.csv");
		List<Point> points = DataSetMaker.load(input);

		// クラスタリングを行う
		KMeansClustering clustering = KMeansClustering.setup(9, 32, Distance.COSINE_DISTANCE);
		ClusterSet clusterSet = clustering.applyTo(points);
		List<Cluster> clusterList = clusterSet.getClusters();

		// クラスタリング結果をファイル出力する
		List<String> lines = new ArrayList<>();
		for (int i = 0; i < clusterList.size(); i++) {
			Cluster cluster = clusterList.get(i);
			for (Point point : cluster.getPoints()) {
				String features = Arrays.stream(point.getArray().toDoubleVector())
						.mapToObj(Double::toString)
						.collect(Collectors.joining(","));
				String line = i + "," + point.getId() + "," + features;
				lines.add(line);
			}
		}
		Files.write(Paths.get("src/main/resources/cluster.csv"), lines);
	}
}
