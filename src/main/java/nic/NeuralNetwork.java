package nic;

public class NeuralNetwork {
	float input_nodes;
	float hidden_nodes;
	float output_nodes;
	Matrix weights_ih;
	Matrix weights_ho;
	Matrix bias_h;
	Matrix bias_o;
	float learning_rate;
	
	
	NeuralNetwork(float numI, float numH, float num0){
		this.input_nodes = numI;
		this.hidden_nodes = numH;
		this.output_nodes = num0;
		
		this.weights_ih = new Matrix(this.hidden_nodes,this.input_nodes);
		this.weights_ih.randomize();
		this.weights_ho = new Matrix(this.output_nodes, this.hidden_nodes);
		this.weights_ho.randomize();
		
		this.bias_h = new Matrix(this.hidden_nodes,1);
		this.bias_h.randomize();
		this.bias_o = new Matrix(this.output_nodes,1);
		this.bias_o.randomize();
		
		this.learning_rate = (float) 0.1;
		
	}
	
	void setLearningRate(float lr) {
		this.learning_rate = lr;
	}
	
	float sigmoid(float x) {
		return (float) (1 / (1+Math.exp(-x)));
	}
	
	float dsigmoid(float y) {
		return (float) (y*(1-y));
	}
	
	float[] feedforward(float[] input_array) {
		
		//Generating the hidden Outputs
		Matrix inputs = Matrix.fromArray(input_array);
		Matrix hidden = Matrix.multiply(this.weights_ih, inputs);
		hidden = Matrix.add(hidden,this.bias_h);
		//activation function!
		for (int i=0; i<hidden.rows;i++) {
			for (int j=0; j<hidden.cols;j++) {
				hidden.data[i][j] = sigmoid(hidden.data[i][j]);
			}
		}
		
		Matrix output = Matrix.multiply(this.weights_ho, hidden);
		output = Matrix.add(output, this.bias_o);
		
		for (int i=0; i<output.rows;i++) {
			for (int j=0; j<output.cols;j++) {
				output.data[i][j] = sigmoid(output.data[i][j]);
			}
		}
		
		return output.toArray();
	}
	
	void train(float[] input_array, float[] target_array) {
		
		/////////Feedforward start
		//Generating the hidden Outputs
		Matrix inputs = Matrix.fromArray(input_array);
		Matrix hidden = Matrix.multiply(this.weights_ih, inputs);
		hidden = Matrix.add(hidden,this.bias_h);
		//activation function!
		for (int i=0; i<hidden.rows;i++) {
			for (int j=0; j<hidden.cols;j++) {
				hidden.data[i][j] = sigmoid(hidden.data[i][j]);
			}
		}
				
		Matrix outputs = Matrix.multiply(this.weights_ho, hidden);
		outputs = Matrix.add(outputs, this.bias_o);
				
		for (int i=0; i<outputs.rows;i++) {
			for (int j=0; j<outputs.cols;j++) {
				outputs.data[i][j] = sigmoid(outputs.data[i][j]);
			}
		}
		////////Feedforward end
		
		//Convert array to matrix object
		//Matrix outputs_ = Matrix.fromArray(outputs);
		Matrix targets_ = Matrix.fromArray(target_array);
		
		//Calculate the error
		//Error = TARGETS - OUTPUTS
		Matrix output_errors = Matrix.subtract(targets_, outputs);
		
		// let gradient = outputs * (1 - outputs)
		//Calculate gradient
		//outputs.map(dsigmoid)
		Matrix gradients = new Matrix(outputs.rows,outputs.cols);
		for (int i=0; i<outputs.rows;i++) {
			for (int j=0; j<outputs.cols;j++) {
				gradients.data[i][j] = dsigmoid(outputs.data[i][j]);
			}
		}
		gradients.multiply(output_errors);
		gradients.multiply(this.learning_rate);
		
		
		//Calculate deltas
		Matrix hidden_T = Matrix.transpose(hidden);
		Matrix weight_ho_deltas = Matrix.multiply(gradients,hidden_T);
		 
		// Adjust the weights by deltas
		this.weights_ho = Matrix.add(this.weights_ho, weight_ho_deltas);
		//Adjust the bias by its deltas (which is jus the gradient)
		this.bias_o = Matrix.add(this.bias_o,gradients);

		
		//Calculate the hidden layer errors
		Matrix who_t = Matrix.transpose(this.weights_ho);
		Matrix hidden_errors = Matrix.multiply(who_t,output_errors);
		
		
		//Calculate hidden gradient
		//Matrix hidden_gradient = Matrix.map(hidden, dsignmoid)
		Matrix hidden_gradient = new Matrix(hidden.rows,hidden.cols);
		for (int i=0; i<hidden.rows;i++) {
			for (int j=0; j<hidden.cols;j++) {
				hidden_gradient.data[i][j] = dsigmoid(hidden.data[i][j]);
			}
		}
		hidden_gradient.multiply(hidden_errors);
		hidden_gradient.multiply(this.learning_rate);
		
		//Calculate input -> hidden deltas
		Matrix inputs_T = Matrix.transpose(inputs);
		Matrix weight_ih_deltas = Matrix.multiply(hidden_gradient, inputs_T);
		
		this.weights_ih = Matrix.add(this.weights_ih,weight_ih_deltas);
		//Adjust the bias by its deltas (which is just the gradient)
		this.bias_h = Matrix.add(this.bias_h, hidden_gradient);
		
		
		
		
		
		
		//outputs.print();
		//targets_.print();
		//output_errors.print();
		
		
		
	
		
		
		
	}
	
	
}
