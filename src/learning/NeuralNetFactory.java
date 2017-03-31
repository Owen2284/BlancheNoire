package learning;

import java.util.ArrayList;

import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import game.GameLoader;
import util.FileTools;

/**
 * Class containing all of the code necessary for creating a neural network from the provided Othello data.
 * Programmed with aid of the official DL4J tutorial (https://deeplearning4j.org/tutorials) 
 * and associated videos (e.g. https://www.youtube.com/watch?v=8EIBIfVlgmU&ab_channel=Deeplearning4j-Skymind).
 * @author Owen
 */
public class NeuralNetFactory {

	public static void main(String[] args) {
		
		// Checks and retrieves file name arg.
		String netPath = "nn/";
		if (args.length > 0) {
			netPath += args[1];
		} else {
			netPath += System.currentTimeMillis();
		}
		
		// Convert and split the data. (Uses same random seed so results can be reproduced.)
		String startDir = "games/extracted/";
		String convertedDir = "games/scripts/";
		String trainDir = "games/training/";
		String testDir = "games/test/";
		GameLoader.convertExtractedToScripts(startDir, convertedDir);
		GameLoader.splitScripts(convertedDir, trainDir, testDir, 123);
		
		// Loading in all data.
		System.out.println("-----");
		System.out.println("Begining Deep Learning process.");
		System.out.println("Loading in training data...");
		ArrayList<String> trainData = new ArrayList<String>();
		for (String trainFile : FileTools.getAllFilePathsInDir(trainDir)) {
			ArrayList<String> file = FileTools.readFile(trainFile);
			trainData.addAll(file);
		}
		System.out.println(trainData.size() + " training game scripts loaded in.");
		System.out.println("Loading in testing data...");
		ArrayList<String> testData = new ArrayList<String>();
		for (String testFile : FileTools.getAllFilePathsInDir(testDir)) {
			ArrayList<String> file = FileTools.readFile(testFile);
			testData.addAll(file);
		}
		System.out.println(testData.size() + " testing game scripts loaded in.");
		
		// Running Neural Network creator.
		System.out.println("Launching Neural Network creator.");
		MultiLayerNetwork net = createNeuralNetwork(trainData, testData);
		
		// Saving the trained network.
		System.out.println("Saving the created NN to" + netPath + ".");
		
	}
	
	@SuppressWarnings("deprecation")
	public static MultiLayerNetwork createNeuralNetwork(ArrayList<String> trainData, ArrayList<String> testData) {
		
		// Initialising key variables.
		int seed = 123;
		double learningRate = 0.01;
		int batchSize = 50;
		int epochCount = 10;
		int inputCount = trainData.size();
		int outputCount = 1;
		int hiddenNodeCount = 100;
		
		// TODO: Formating the input data for use by the NN.
		DataSetIterator trainIter = null;
		DataSetIterator testIter = null;
		
		// Creating the NN object.
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
				.seed(seed)
				.iterations(1)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.learningRate(learningRate)
				.updater(Updater.NESTEROVS).momentum(0.9)
				.list()
				.layer(0, new DenseLayer.Builder()
					.nIn(inputCount)
					.nOut(hiddenNodeCount)
					.weightInit(WeightInit.XAVIER)
						.activation("relu")
						.build()
				)
				.layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
						.weightInit(WeightInit.XAVIER)
							.activation("softmax")
							.weightInit(WeightInit.XAVIER)
							.nIn(hiddenNodeCount)
							.nOut(outputCount)
							.build()
				)
				.pretrain(false).backprop(true).build();
				
		// Training the network.
		MultiLayerNetwork model = new MultiLayerNetwork(conf);
		model.init();
		model.setListeners(new ScoreIterationListener(10));
		
		for (int i = 0; i < epochCount; ++i) {
			model.fit(trainIter);		// TODO: Provide data.
		}
		
		// Evaluating the network.
		Evaluation eval = new Evaluation(outputCount);
		while (testIter.hasNext()) {
			DataSet t = testIter.next();
			// Retrieves the data, the true result and the networks prediction.
			INDArray features = t.getFeatureMatrix();
			INDArray labels = t.getLabels();
			INDArray predicted = model.output(features, false);
			eval.eval(labels, predicted);
		}
		
		// Returning the finished model.
		return model;
		
	}

}
