package nic;

import java.util.Arrays;

public class NeuralNet {
	
	static float randomInt(float a,float b) {
		float rand = (float)(Math.random() * ((b - a)+1))+a ;
		return rand;
		
	}
	
	static NeuralNetwork setup() {
		NeuralNetwork nn = new NeuralNetwork(2,5,2);
		
		//XOR dataset -> aim to replace with game data
		float[][] x_train = {{0,0},{0,1},{1,0},{1,1}};
		float[][] y_train = {{0,1},{1,0},{1,0},{0,1}};
		
		//where [0,1] -> false and [1,0] -> is true one_hot_encoding
		
		//float[] input = {0,0};
		//float[] target = {0,1};
		//nn.train(input,target);
		float item;
		for (int i=0;i<10000;i++) {
			//System.out.println(Arrays.deepToString(x_train[j]))
			item = randomInt(0,x_train.length-1);
			nn.train(x_train[(int)item], y_train[(int)item]);
		}
		
		return nn;
	}
	
	public static void main(String[] args) {
		
		NeuralNetwork nn = setup();
		float[] input = {0,0};
		float[] guess = nn.feedforward(input);
		System.out.println(Arrays.toString(guess));

		

	}

}
