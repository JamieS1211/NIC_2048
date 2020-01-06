package nic;

import java.io.IOException;
import java.time.Duration;
import nic.Utils;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.util.RandomUtils;
import put.game2048.Action;
import put.game2048.Agent;
import put.game2048.Board;

public class CustomAgent implements Agent {
	
	float[] nnShape;
	float lr;
	MultLayeredNN nn;
	
	float[][] states;
	float[][] actions;

	
	CustomAgent(){
		this.nnShape = new float[]{16,32,4};
		this.lr = (float)0.001;
		this.nn = new MultLayeredNN(nnShape, lr);
		
		
		try {
			Data data = AccessingGameData.get_game_data("src//main//java//nic//training_data.txt");
			this.states = Data.states;
			this.actions = Data.actions;
			
			//System.out.println(states.length);
			//System.out.println(actions.length);
			
			
			float item;
			//System.out.println("training");
			for (int i=0;i<(0.5)*states.length;i++) {
				//System.out.println(Arrays.deepToString(x_train[j]))
				item = Utils.randomInt(0,states.length-1);
						
				this.nn.train(states[(int)item], actions[(int)item]);
			}
			//System.out.println("Training complete");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));

	public Action chooseAction(Board board, List<Action> possibleActions, Duration maxTime) {

		
		
		Preconditions.checkArgument(0 < possibleActions.size());

		//return (Action)RandomUtils.
		int[] f_board = Utils.flatten_board(board.get());
		//float[] nf_board = Utils.normalize_data(f_board);
		float[] nf_board = Utils.int2float(f_board);
		
		float[] guess = this.nn.feedforward(nf_board);
		Action action = Utils.guess2action(guess, possibleActions);
		
		
		//Action act = (Action)RandomUtils.pickRandom(possibleActions.toArray(), random);
		//System.out.println(action);
		//System.out.println(act);
		
		return action; 
	}
}
