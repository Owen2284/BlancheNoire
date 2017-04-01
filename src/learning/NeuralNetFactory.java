package learning;

import java.io.File;
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
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import util.FileTools;

/**
 * Class containing all of the code necessary for creating a neural network from the provided Othello data.
 * Programmed with aid of the official DL4J tutorial (https://deeplearning4j.org/tutorials) 
 * and associated videos (e.g. https://www.youtube.com/watch?v=8EIBIfVlgmU&ab_channel=Deeplearning4j-Skymind).
 * @author Owen
 */
public class NeuralNetFactory {

	public static void main(String[] args) {
		
		final String FILE_EXT = ".zip";
		
		// Checks and retrieves file name arg.
		String netPath = "nn/";
		String netName = "blank";
		double trainDataUsePercent = 100.0;
		if (args.length >= 1) {
			netName = args[0];
			if (args.length >= 2) {
				trainDataUsePercent = Double.parseDouble(args[1]);
			}
		}
		netPath += "net-oth-"+netName+"-"+trainDataUsePercent+FILE_EXT;
		
		long t = System.currentTimeMillis();
		while(t + 10000 > System.currentTimeMillis()) {
			
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
		System.out.println("Closing Neural Network creator.");		
		
		// Saving the trained network.
		System.out.println("Saving the created NN to \"" + netPath + "\".");
		File loc = new File(netPath);
		try {
			ModelSerializer.writeModel(net, loc, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Process complete! Ending program...");
		
	}
	
	@SuppressWarnings("deprecation")
	public static MultiLayerNetwork createNeuralNetwork(ArrayList<String> trainData, ArrayList<String> testData) {
		
		// Initialising key variables.
		int seed = 123;
		double learningRate = 0.01;
		int batchSize = 50;
		int epochCount = 10;
		int inputCount = 64;
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
