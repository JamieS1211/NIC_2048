package nic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import put.game2048.Action;

public class NNTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
				
		float[] nnShape = new float[]{16,16,16,16,16,4};
		float lr = (float)0.001;
		MultLayeredNN player = new MultLayeredNN(nnShape, lr);
		
		List<Action> possibleActions = Arrays.asList(Action.UP,Action.DOWN,Action.LEFT,Action.RIGHT);
		
		
		try {
			Data data = AccessingGameData.get_game_data("src//main//java//nic//training27_data.txt");
			float[][] train_states = data.states;
			float[][] train_actions = data.actions;
			
			float[][] test_states = Arrays.copyOfRange(train_states, 100, 200);
			float[][] test_actions = Arrays.copyOfRange(train_actions, 100, 200);
			
			train_states = Arrays.copyOfRange(train_states,0,500);
			train_actions = Arrays.copyOfRange(train_actions,0,500);
			
			
			//for (int j=0; j<train_states.length;j++) {
			//	System.out.println(Utils.guess2action(train_actions[j], possibleActions));
			//}
			
		
			
			
			float item;
			String anim = "|/-\\";
			int total = 1000000;
			for (int i=0;i<2;i++) {
			//System.out.println(Arrays.deepToString(x_train[j]))
				//System.out.println(i);
				int batch_begin = (int)(i*0.5*(train_states.length-1));
				int batch_end = (int)(i*0.5*(train_states.length-1) + 0.5*(train_states.length-1));
				float[][] batch =  Arrays.copyOfRange(train_states,batch_begin,batch_end);
				
				System.out.println(batch.length + " " + batch_end + " " + batch_begin);
				  
				//item = Utils.randomInt(0,train_states.length);
				for (int j=0;j<100000;j++) {
					//player.train(train_states[i], train_actions[i]);
					item = Utils.randomInt(0,batch_end-batch_begin);
					player.train(train_states[(int)item], train_actions[(int)item]);
				}
				
				System.out.println(i);
				//if(i%(total*0.1) == 0) {
				//System.out.println(i + "/" + total);
				//}
				
				//}
			}
			
			int correct_guesses = 0;
			for (int i=0; i<train_states.length;i++) {
				
				float[] guess = player.feedforward(train_states[i]);
				Action guessed_action = Utils.guess2action(guess, possibleActions);
				Action action = Utils.guess2action(train_actions[i], possibleActions);
				
				if (guessed_action == action) {
					System.out.println(guessed_action + " " + action);
					correct_guesses += 1;
				}
				
			}
			System.out.println(correct_guesses + "/" + train_states.length);
			
			//System.out.println("Training complete");
			 
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		

	}

}
