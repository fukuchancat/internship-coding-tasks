package com.mynetgear.fukuchan.internshipcodingtasks.mnist;

import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MnistTask {
	private static Logger logger = LoggerFactory.getLogger(MnistTask.class);

	public static void main(String[] args) throws IOException {
		int width = 28, height = 28, channels = 1; // 入力サイズ。28x28x1(=784)
		int nOut = 10; // 出力サイズ。0から9までの10個
		int seed = 123; // ランダムのシード
		int batch = 128; // バッチサイズ
		int numEpochs = 4; // エポック数

		// 学習用とテスト用のDataSetIteratorを定義
		DataSetIterator trainIterator = new MnistDataSetIterator(batch, true, seed);
		DataSetIterator testIterator = new MnistDataSetIterator(batch, false, seed);

		// データセットの前処理を定義
		DataSetPreProcessor preProcessor = (DataSetPreProcessor) dataSet -> {
			INDArray features = null;

			// バッチ内の各画像について
			for (DataSet d : dataSet) {
				// 画像を反転する
				INDArray array = d.getFeatures().dup().subi(1).muli(-1);

				// 元の特徴ベクトルに連結する
				INDArray joined = Nd4j.concat(1, d.getFeatures(), array);
				features = features == null ? joined : Nd4j.concat(0, features, joined);
			}
			dataSet.setFeatures(features);
		};

		// 前処理を適用
		trainIterator.setPreProcessor(preProcessor);
		testIterator.setPreProcessor(preProcessor);

		// CNNの設定
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(seed)
				.l2(0.0005)
				.updater(new Adam())
				.weightInit(WeightInit.XAVIER)
				.list()
				// 畳み込み層
				.layer(new ConvolutionLayer.Builder(5, 5).nIn(channels)
						.stride(1, 1)
						.nOut(8)
						.activation(Activation.IDENTITY)
						.build())
				// プーリング層
				.layer(new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX).kernelSize(2, 2)
						.stride(2, 2)
						.build())
				// 結合層
				.layer(new DenseLayer.Builder().activation(Activation.RELU).nOut(512).build())
				// 出力層
				.layer(new OutputLayer.Builder(LossFunctions.LossFunction.RECONSTRUCTION_CROSSENTROPY).nOut(nOut)
						.activation(Activation.SOFTMAX)
						.build())
				.setInputType(InputType.convolutionalFlat(height * 2, width, channels))
				.build();

		// モデルを定義・初期化
		MultiLayerNetwork model = new MultiLayerNetwork(conf);
		model.init();
		model.setListeners(new ScoreIterationListener(10));

		// 学習開始
		model.fit(trainIterator, numEpochs);

		// 評価を行う
		Evaluation eval = model.evaluate(testIterator);
		logger.info(eval.stats());
	}
}
