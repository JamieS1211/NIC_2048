package nic;


//import src.Matrix;

public class MultLayeredNN {
	
	float input_nodes;
	float output_nodes;
	float [] hidden_nodes;
	
	Matrix[] weights;
	Matrix[] biases;
	float learning_rate;
	
	
	
	MultLayeredNN(float[] layers, float lr){
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
		
		this.learning_rate = lr;
		
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
	
	
void train(float[] input_array, float[] target_array) {
	
		//System.out.println("training....");

		
		///// FEEDFORWARD START //////
		Matrix inputs = Matrix.fromArray(input_array);
		Matrix current_gen = null;
		Matrix[] hiddens = new Matrix[this.weights.length];
		//hiddens[0] = inputs;
		
		//need to add biases
		for (int m=0;m<this.weights.length;m++) {
			if (m == 0) {
				current_gen = Matrix.multiply(this.weights[m], inputs);
				current_gen = Matrix.add(current_gen, this.biases[m]);
				//activation function
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
			hiddens[m] = current_gen;
			//hiddens[m].print();
			//System.out.println(m);
		}
		//Matrix outputs = current_gen;
		////FEEDFORWARD END ///////////
	
		Matrix outputs = current_gen;
		//Convert array to matrix object
		//Matrix outputs_ = Matrix.fromArray(outputs);
		Matrix targets_ = Matrix.fromArray(target_array);
		
		//Calculate the error
		//Error = TARGETS - OUTPUTS
		Matrix[] errors = new Matrix[this.weights.length];
		errors[this.weights.length-1] = Matrix.subtract(targets_, outputs); 
		
		
		//Calculate the hidden errors
		//For each hidden layer we calculate the hidden errors and update the weights
		Matrix wh_t = null;
		Matrix gradients = null;
		Matrix hidden_T = null;
		Matrix c_weight_deltas = null;
		for (int m=this.weights.length-1; m > -1; m--) {
			if (m == this.weights.length-1) {
				gradients = new Matrix(outputs.rows,outputs.cols);
				for (int i=0; i<outputs.rows;i++) {
					for (int j=0; j<outputs.cols;j++) {
						gradients.data[i][j] = dsigmoid(outputs.data[i][j]);
					}
				}
				gradients.multiply(errors[m]);
				gradients.multiply(this.learning_rate);
				
				//calculate Deltas
				hidden_T = Matrix.transpose(hiddens[m-1]);
				c_weight_deltas = Matrix.multiply(gradients,hidden_T);
								
				
				this.weights[m] = Matrix.add(this.weights[m],c_weight_deltas);
				this.biases[m] = Matrix.add(this.biases[m],gradients);
				
			}
			
			else if (m==0) {
				wh_t = Matrix.transpose(this.weights[m+1]);
				errors[m] = Matrix.multiply(wh_t,errors[m+1]);
				
				gradients = new Matrix(hiddens[m].rows,hiddens[m].cols);
				for (int i=0; i<hiddens[m].rows;i++) {
					for (int j=0; j<hiddens[m].cols;j++) {
						gradients.data[i][j] = dsigmoid(hiddens[m].data[i][j]);
					}
				}
				
				gradients.multiply(errors[m]);
				gradients.multiply(this.learning_rate);
				
				Matrix inputs_T = Matrix.transpose(inputs);
				Matrix weight_ih_deltas = Matrix.multiply(gradients, inputs_T);
				
				this.weights[m] = Matrix.add(this.weights[m],weight_ih_deltas);
				//Adjust the bias by its deltas (which is just the gradient)
				this.biases[m] = Matrix.add(this.biases[m], gradients);
				
			}
		
			else {
				//Find the error of the hidden layer
				wh_t = Matrix.transpose(this.weights[m+1]);
				errors[m] = Matrix.multiply(wh_t, errors[m+1]);
				
				//Calculate the gradient
				gradients = new Matrix(hiddens[m].rows,hiddens[m].cols);
				for (int i=0; i<hiddens[m].rows;i++) {
					for (int j=0; j<hiddens[m].cols;j++) {
						gradients.data[i][j] = dsigmoid(hiddens[m].data[i][j]);
					}
				}
				gradients.multiply(errors[m]);
				gradients.multiply(this.learning_rate);
				
				//Calculate hidden deltas
				hidden_T = Matrix.transpose(hiddens[m-1]);
				c_weight_deltas = Matrix.multiply(gradients,hidden_T);
								
				//Update weights
				this.weights[m] = Matrix.add(this.weights[m],c_weight_deltas);
				this.biases[m] = Matrix.add(this.biases[m],gradients);
			}
		}
	}


	
	

}
