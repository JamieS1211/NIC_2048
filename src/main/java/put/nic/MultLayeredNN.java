package put.nic;

import java.util.Arrays;
import java.util.Random;

public class MultLayeredNN {
	
	int input_nodes;
	int output_nodes;
	int [] hidden_nodes;
	
	Matrix[] weights;
	Matrix[] biases;
	float learning_rate;

	public MultLayeredNN(MultLayeredNN networkToClone) {
		this.input_nodes = networkToClone.input_nodes;
		this.output_nodes = networkToClone.output_nodes;
		this.hidden_nodes = networkToClone.hidden_nodes;

		this.weights = networkToClone.weights.clone();
		this.biases = networkToClone.biases.clone();
		this.learning_rate = networkToClone.learning_rate;
	}

	public MultLayeredNN(int[] layers, float learningRate) {
		//layers will define the shape of the NN
		this.input_nodes = layers[0];
		this.output_nodes = layers[layers.length - 1];
		this.hidden_nodes = new int[layers.length - 2];

		for (int i = 1; i < layers.length - 1; i++) {
			hidden_nodes[i - 1] = layers[i];
		}
		
		this.weights = new Matrix[layers.length-1];
		this.biases = new Matrix[layers.length-1];

		for (int i = 0; i < this.weights.length; i++) {
			this.weights[i] = new Matrix(layers[i+1],layers[i]);
			this.weights[i].randomize();
			
			this.biases[i] = new Matrix(layers[i+1],1);
			this.biases[i].randomize();
		}
		
		this.learning_rate = learningRate;
	}
	
	float sigmoid(float x) {
		return (float) (1 / (1+Math.exp(-x)));
	}
	
	void setLearningRate(float lr) {
		this.learning_rate = lr;
	}
	
	float dsigmoid(float y) {
		return (float) (y*(1-y));
	}

	float[] feedforward(float[] input_array) {
		Matrix inputs = Matrix.fromArray(input_array);
		Matrix current_gen = inputs;

		//need to add biases
		for (int m = 0; m < this.weights.length; m++) {
			current_gen = Matrix.multiply(this.weights[m], current_gen);
			current_gen = Matrix.add(current_gen, this.biases[m]);


			for (int i = 0; i < current_gen.rows; i++) {
				for (int j = 0; j < current_gen.cols; j++) {
					current_gen.data[i][j] = sigmoid(current_gen.data[i][j]);
				}
			}
		}

		return current_gen.toArray();
	}

	public void mutate() {
		mutate(0.05);
	}

	public void mutate(double chance) {
		for (int layer = 0; layer < this.weights.length; layer++) {
			for (int neuron = 0; neuron < this.weights[layer].data.length; neuron++) {
				for (int connection = 0; connection < this.weights[layer].data[neuron].length; connection++) {
					if (Math.random() < chance) {
						this.weights[layer].data[neuron][connection] += (new Random().nextGaussian() / 5);

						if (this.weights[layer].data[neuron][connection] > 1) {
							this.weights[layer].data[neuron][connection] = 1;
						} else if (this.weights[layer].data[neuron][connection] < -1) {
							this.weights[layer].data[neuron][connection] = -1;
						}
					}
				}
			}
		}

		return;
	}
}
