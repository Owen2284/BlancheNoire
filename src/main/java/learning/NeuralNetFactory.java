package learning;

import java.io.File;
import java.io.IOException;

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

	private static Logger log = LoggerFactory.getLogger(NeuralNetFactory.class);

	public static void main(String[] args) {

		final String FILE_EXT = ".zip";
		
		// Checks and retrieves command line args.
		String trainDir = "";
		String testDir = "";
		String netPath = "ann/othello_net_";
		String netName = "blank";
		if (args.length >= 3) {
			trainDir = args[0];
			testDir = args[1];
			netName = args[2];
			netPath += netName+FILE_EXT;

			System.out.println("Using \"" + trainDir + "\" as training data, and \"" + testDir + "\" as testing data.");
			System.out.println("Created NN will be saved to: " + netPath);
			
			// Running Neural Network creator.
			System.out.println("Launching Neural Network creator....");
			MultiLayerNetwork net = createNeuralNetwork(trainDir, testDir);
			System.out.println("Closing Neural Network creator.");		
			
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
		} else {
			System.out.println("Invalid number of arguments.");
		}
		
		System.out.println("Ending program...");
		
	}
	
	public static MultiLayerNetwork createNeuralNetwork(String trainDir, String testDir) {
		
		// Initialising key variables.
		int epochCount = 10;
		int inputCount = 128;
		int outputCount = 2;
		int batchSize = 1;
		
		// Formating the input data for use by the NN.
		System.out.println("Loading in data sets...");
		DataSetIterator trainIter = createDataSetIterator(trainDir, batchSize, 0, 2);
		DataSetIterator testIter = createDataSetIterator(testDir, batchSize, 0, 2);
		
		// Creating the NN object.
		System.out.println("Creating the initial network configuration...");
		MultiLayerConfiguration conf;
		conf = nnConf1(inputCount, outputCount);
				
		// Training the network.
		System.out.println("Creating the initial network model...");
		MultiLayerNetwork model = new MultiLayerNetwork(conf);
		model.init();
		model.setListeners(new ScoreIterationListener(batchSize));
		
		System.out.println("Running the model fitting function...");
		for (int i = 0; i < epochCount; ++i) {
			model.fit(trainIter);
			System.out.println(" Epoch " + (i+1) + "/" + epochCount + " complete.");
		}
		
		// Evaluating the network.
		System.out.println("Evaluating the created network...");
		Evaluation eval = new Evaluation(outputCount);
		boolean test = true;
		while (testIter.hasNext()) {
			DataSet t = testIter.next();
			// Retrieves the data, the true result and the networks prediction.
			INDArray features = t.getFeatureMatrix();
			INDArray labels = t.getLabels();
			INDArray predicted = model.output(features, false);
			if (test) {
				System.out.println(labels);
				System.out.println();
				System.out.println(features);
				System.out.println(features.rank());
				System.out.println(features.shape()[0] + "," + features.shape()[1]);
				System.out.println();
				System.out.println(predicted);
				System.out.println(predicted.rank());
				System.out.println(predicted.shape()[0] + "," + predicted.shape()[1]);
				test = false;
			}
			eval.eval(labels, predicted);
		}

		System.out.println(eval.stats());

		// Returning the finished model.
		return model;
		
	}
	
	private static DataSetIterator createDataSetIterator(String dir, int batchSize, int labelIndex, int numPossibleLabels) {
		try {
			String tempFilePath = dir + "full.csv";
			FileTools.mergeDirNoDelete(dir, tempFilePath);
			File tempFile = new File(tempFilePath);
			RecordReader rr = new CSVRecordReader();
			rr.initialize(new FileSplit(tempFile));
			DataSetIterator dsi = new RecordReaderDataSetIterator(rr, batchSize, labelIndex, numPossibleLabels);
			tempFile.deleteOnExit();
			System.out.println(" Data from " + dir + " has been loaded.");
			return dsi;
		} catch (IOException e) {
			throw new IllegalArgumentException(" Directory \"" + dir + "\" not found.");
		} catch (InterruptedException e) {
			throw new IllegalArgumentException(" Reading from \"" + dir + "\" resulted in an interruption.");
		}
	}
	
	//TODO
	private static MultiLayerConfiguration nnConf1(int inputCount, int outputCount) {
		int seed = 123;
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

}
