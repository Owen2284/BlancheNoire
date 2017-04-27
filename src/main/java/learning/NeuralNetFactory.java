package learning;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
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
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileTools;

/**
 * Class containing all of the code necessary for creating a neural network from the provided Othello data.
 * Programmed with aid of the official DL4J tutorial (https://deeplearning4j.org/tutorials) 
 * and associated videos (e.g. https://www.youtube.com/watch?v=8EIBIfVlgmU).
 * @author Owen
 */
public class NeuralNetFactory {

	private static Logger log;

	/**
	 * Main method that creates an ANN and stores it in a .zip file. Ensure data is formatted using
	 * NeuralNetDataHandler first.
	 */
	public static void main(String[] args) {

		final String FILE_EXT = ".zip";
		
		// Variables to store command line args.
		String trainDir = "";
		String testDir = "";
		int labelCount = 0;
		String netPath = "ann/othello_net_";
		String binDir = "dat/csv/bin/";
		boolean detailedLogs = true;
		int epochCount = 10;
		int nnConfig = 2;

		// Ensuring required args are present.
		if (args.length >= 4) {

			// Stores required args.
			trainDir = args[0];
			testDir = args[1];
			labelCount = Integer.parseInt(args[2]);
			netPath += args[3] + FILE_EXT;

			// Checks for optional args.
			if (args.length >= 5) {detailedLogs = Boolean.parseBoolean(args[4]);}
			if (args.length >= 6) {epochCount = Integer.parseInt(args[5]);}
			if (args.length >= 7) {nnConfig = Integer.parseInt(args[6]);}

			if (detailedLogs) {
				log = LoggerFactory.getLogger(NeuralNetFactory.class);
			} else {
				log = null;
			}

			System.out.println("Using \"" + trainDir + "\" as training data, and \"" + testDir + "\" as testing data. Expecting " + labelCount + " labels in the data.");
			System.out.println("Created NN will be saved to: " + netPath);
			if (!detailedLogs) {
				System.out.println("Detailed logs have been turned off.");
			}
			System.out.println("Using NN Config " + nnConfig + " with " + epochCount + " epochs to train the network.");
			
			// Running Neural Network creator.
			long startTimestamp = System.currentTimeMillis();
			System.out.println("Launching Neural Network creator....");
			MultiLayerNetwork net = createNeuralNetwork(trainDir, testDir, binDir, netPath.substring(0, netPath.length()-4), labelCount, epochCount, nnConfig, detailedLogs);
			System.out.println("Closing Neural Network creator.");
			System.out.println("Time taken: " + ((float)(System.currentTimeMillis() - startTimestamp) / (1000 * 60 * 60)) + " hours.");
			
			// Saving the trained network.
			System.out.println("Saving the created NN to \"" + netPath + "\"...");
			File location = new File(netPath);
			boolean furtherUpdates = false;
			try {
				ModelSerializer.writeModel(net, location, furtherUpdates);
				System.out.println("File saved, process complete!");
			} catch (Exception e) {
				System.out.println("An error prevented the file from being saved:");
				e.printStackTrace();
			}

			// Clear bin folder.
			File binCheck = new File(binDir);
			if (binCheck.exists()) {
				for (String binPath : FileTools.getAllFilePathsInDir(binDir)) {
					File binFile = new File(binPath);
					binFile.delete();
				}
				System.out.println("Bin folder cleared.");
			}

		} else {
			System.out.println("Invalid number of arguments. Please provide a training data directory, a testing " +
					"data directory, the number of labels in the data sets, and a name for the neural network.");
		}
		
		System.out.println("Ending program...");
		
	}

	/**
	 * Ties together the network config selection and the data loading methods to create, train and test
	 * the ANN.
	 */
	public static MultiLayerNetwork createNeuralNetwork(String trainDir, String testDir, String binDir, String statPath, int labelCount, int epochCount, int configToUse, boolean showScores) {
		
		// Initialising key variables.
		int inputCount = 128;
		int batchSize = 50;
		int SEED = 2017;
		
		// Loading in the input data for use by the NN.
		System.out.println("Loading in training data...");
		DataSetIterator trainIter = createDataSetIterator(trainDir, binDir, batchSize, 0, labelCount);
		
		// Creating the NN object.
		System.out.println("Creating the initial network configuration...");
		MultiLayerConfiguration conf;
		if (configToUse == 1) {
			conf = configClassification1(inputCount, labelCount, SEED);
		} else if (configToUse == 2) {
			conf = configClassification2(inputCount, labelCount, SEED);
		} else if (configToUse == 3) {
			conf = configClassification3(inputCount, labelCount, SEED);
		} else {
			throw new IllegalArgumentException("Invalid NN config number.");
		}
				
		// Training the network.
		System.out.println("Creating the initial network model...");
		MultiLayerNetwork model = new MultiLayerNetwork(conf);
		model.init();
		if (showScores) {
			model.setListeners(new ScoreIterationListener(batchSize));
		}
		
		System.out.println("Running the model fitting function...");
		for (int i = 0; i < epochCount; ++i) {
			long epochStart = System.currentTimeMillis();
			model.fit(trainIter);
			System.out.println(" Epoch " + (i+1) + "/" + epochCount + " complete. Time taken:- " + ((float)(System.currentTimeMillis() - epochStart) / (1000 * 60)) + " minutes.");
		}

		// Memory management followed by getting testing data.
		System.out.println("Loading in testing data...");
		trainIter = null;
		System.gc();
		DataSetIterator testIter = createDataSetIterator(testDir, binDir, batchSize, 0, labelCount);
		
		// Evaluating the network.
		System.out.println("Evaluating the created network...");
		Evaluation eval = new Evaluation(labelCount);
		while (testIter.hasNext()) {
			DataSet t = testIter.next();
			// Retrieves the data, the true result and the networks prediction.
			INDArray features = t.getFeatureMatrix();
			INDArray labels = t.getLabels();
			INDArray predicted = model.output(features, false);
			eval.eval(labels, predicted);
		}

		// Show the mode's stats, and save them to an additional file.
		System.out.println(eval.stats());
		ArrayList<String> evalStats = new ArrayList<String>();
		evalStats.add(eval.stats());
		evalStats.add("Program Parameters:");
		evalStats.add(" Number of labels: " + labelCount);
		evalStats.add(" Number of epochs: " + epochCount);
		evalStats.add(" NN Config used: " + configToUse);
		FileTools.writeFile(statPath + "_stats.txt", evalStats);

		// Clear out the memory of the other iterator.
		testIter = null;
		System.gc();

		// Returning the finished model.
		return model;
		
	}

	/**
	 * Creates a DataSetIterator for the provided csv file(s), which is used by the trainer/tester to feed
	 * the data to the ANN.
	 */
	private static DataSetIterator createDataSetIterator(String readDir, String binDir, int batchSize, int labelIndex, int numPossibleLabels) {
		try {
			File binCheck = new File(binDir);
			if (!binCheck.exists()) {
				binCheck.mkdirs();
			}
			String tempFilePath = binDir + "full" + System.currentTimeMillis() + ".csv";
			FileTools.mergeDirNoDelete(readDir, tempFilePath);
			File tempFile = new File(tempFilePath);
			RecordReader rr = new CSVRecordReader();
			rr.initialize(new FileSplit(tempFile));
			DataSetIterator dsi = new RecordReaderDataSetIterator(rr, batchSize, labelIndex, numPossibleLabels);
			System.out.println(" Data from " + readDir + " has been loaded.");
			return dsi;
		} catch (IOException e) {
			throw new IllegalArgumentException(" Directory \"" + readDir + "\" not found.");
		} catch (InterruptedException e) {
			throw new IllegalArgumentException(" Reading from \"" + readDir + "\" resulted in an interruption.");
		}
	}

	/**
	 * A basic network layout with 1 hidden layer.
	 */
	private static MultiLayerConfiguration configClassification1(int inputCount, int outputCount, int seed) {
		double learningRate = 0.01;
		int hiddenNodeCount = 100;
		
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
						.activation(Activation.RELU)
						.build()
				)
				.layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
						.weightInit(WeightInit.XAVIER)
							.activation(Activation.SOFTMAX)
							.weightInit(WeightInit.XAVIER)
							.nIn(hiddenNodeCount)
							.nOut(outputCount)
							.build()
				)
				.pretrain(false).backprop(true).build();
		
		return conf;
	}

	/*
	 * Creates a NN with many more hidden nodes and layers than the previous config.
	 */
	private static MultiLayerConfiguration configClassification2(int inputCount, int outputCount, int seed) {
		double learningRate = 0.01;
		int hiddenNodeCount = 128;

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
						.activation(Activation.RELU)
						.build()
				)
				.layer(1, new DenseLayer.Builder()
						.nIn(hiddenNodeCount)
						.nOut(hiddenNodeCount)
						.weightInit(WeightInit.XAVIER)
						.activation(Activation.RELU)
						.build()
				)
				.layer(2, new DenseLayer.Builder()
						.nIn(hiddenNodeCount)
						.nOut(hiddenNodeCount)
						.weightInit(WeightInit.XAVIER)
						.activation(Activation.RELU)
						.build()
				)
				.layer(3, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
						.weightInit(WeightInit.XAVIER)
						.activation(Activation.SOFTMAX)
						.weightInit(WeightInit.XAVIER)
						.nIn(hiddenNodeCount)
						.nOut(outputCount)
						.build()
				)
				.pretrain(false).backprop(true).build();

		return conf;
	}

	/*
	 * Creates a NN wthat regresses the data to a single float output. (Doesn't do that at the moment though)
	 */
	private static MultiLayerConfiguration configClassification3(int inputCount, int outputCount, int seed) {
		double learningRate = 0.01;
		int hiddenNodeCount = 256;

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
						.activation(Activation.RELU)
						.build()
				)
				.layer(1, new DenseLayer.Builder()
						.nIn(hiddenNodeCount)
						.nOut(hiddenNodeCount)
						.weightInit(WeightInit.XAVIER)
						.activation(Activation.RELU)
						.build()
				)
				.layer(2, new DenseLayer.Builder()
						.nIn(hiddenNodeCount)
						.nOut(hiddenNodeCount)
						.weightInit(WeightInit.XAVIER)
						.activation(Activation.RELU)
						.build()
				)
				.layer(3, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
						.weightInit(WeightInit.XAVIER)
						.activation(Activation.IDENTITY)
						.nIn(hiddenNodeCount)
						.nOut(outputCount)
						.build()
				)
				.pretrain(false).backprop(true).build();

		return conf;
	}

}
