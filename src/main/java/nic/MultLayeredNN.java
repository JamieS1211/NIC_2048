package nic;

public class MultLayeredNN {
	
	float input_nodes;
	float output_nodes;
	float [] hidden_nodes;
	
	Matrix[] weights;
	Matrix[] biases;
	float learning_rate;
	
	
	
	MultLayeredNN(float[] layers){
		//layers will define the shape of the NN
		this.input_nodes = layers[0];
		this.output_nodes = layers[layers.length-1];
		this.hidden_nodes = new float[layers.length-2];
		
		
		
		for (int i=1;i<(layers.length)-1;i++) {
			hidden_nodes[i-1] = layers[i];	
		}
		
		this.weights = new Matrix[layers.length-1];
		this.biases = new Matrix[layers.length-1];
		for (int i=0;i<this.weights.length;i++) {
			this.weights[i] = new Matrix(layers[i+1],layers[i]);
			this.weights[i].randomize();
			
			this.biases[i] = new Matrix(layers[i+1],1);
			this.biases[i].randomize();
		}
		
		this.learning_rate = (float) 0.1;
		
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
		Matrix current_gen = null;
		
		//need to add biases
		for (int m=0;m<this.weights.length;m++) {
			if (m == 0) {
				current_gen = Matrix.multiply(this.weights[m], inputs);
				current_gen = Matrix.add(current_gen, this.biases[m]);
				//current_gen.print();
				
				for (int i=0; i<current_gen.rows;i++) {
					for (int j=0; j<current_gen.cols;j++) {
						current_gen.data[i][j] = sigmoid(current_gen.data[i][j]);
					}
				}
			}
			else {
				current_gen = Matrix.multiply(this.weights[m],current_gen);
				current_gen = Matrix.add(current_gen, this.biases[m]);
				for (int i=0; i<current_gen.rows;i++) {
					for (int j=0; j<current_gen.cols;j++) {
						current_gen.data[i][j] = sigmoid(current_gen.data[i][j]);
					}
				}
			
			}
		}
		return current_gen.toArray();
	}
	
	

}
