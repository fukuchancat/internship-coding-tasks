package com.mynetgear.fukuchan.internshipcodingtasks.sample;

import lombok.Getter;
import lombok.Setter;
import org.deeplearning4j.clustering.algorithm.Distance;
import org.deeplearning4j.clustering.cluster.Cluster;
import org.deeplearning4j.clustering.cluster.ClusterSet;
import org.deeplearning4j.clustering.cluster.Point;
import org.deeplearning4j.clustering.kmeans.KMeansClustering;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * エルボー法。クラスタ数ごとのSSEを計算してelbow.csvに出力する
 */
@Getter
@Setter
public class ElbowMethodResult {
	private int count;
	private double sse;

	public ElbowMethodResult(int count, double sse) {
		this.count = count;
		this.sse = sse;
	}

	public static void main(String[] args) throws IOException {
		// ファイルから文書ベクトルを復元
		Path input = Paths.get("src/main/resources/points.csv");
		List<Point> points = DataSetMaker.load(input);

		// クラスタ数1から64で試行
		List<ElbowMethodResult> results = IntStream.rangeClosed(1, 64).mapToObj(count -> {
			// クラスタリングを行う
			KMeansClustering clustering = KMeansClustering.setup(count, 32, Distance.COSINE_DISTANCE);
			ClusterSet clusterSet = clustering.applyTo(points);
			List<Cluster> clusterList = clusterSet.getClusters();

			// 各クラスタリング結果について、SSEを求める
			double sse = clusterList.stream().flatMapToDouble(cluster -> {
				INDArray center = cluster.getCenter().getArray();
				return cluster.getPoints()
						.stream()
						.map(Point::getArray)
						.map(center::sub)
						.mapToDouble(arr -> (double) arr.norm2Number())
						.map(d -> Math.pow(d, 2));
			}).sum();

			System.out.println(count + "," + sse);
			return new ElbowMethodResult(count, sse);
		}).collect(Collectors.toList());

		// 結果をファイル出力
		List<String> lines = results.stream()
				.map(result -> result.getCount() + "," + result.getSse())
				.collect(Collectors.toList());
		Files.write(Paths.get("src/main/resources/elbow.csv"), lines);
	}
}
