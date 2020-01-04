package nic;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.util.Arrays;
import java.util.function.Supplier;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

import put.game2048.Agent;
import put.game2048.Game;
import put.game2048.GameResult;

public class TrainingData {
	///Class used to generate Training Data by playing game numerous times

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int action_time_limit_ms = 1000;
		int num_games = 1;
		String random_seed = "1234";

			final Supplier<Agent> AGENT = createAgentFactoryByReflection();
			final int REPEATS = num_games;
			final Duration ACTION_TIME_LIMIT = Duration.ofNanos(action_time_limit_ms * 1000 * 1000);
			final long RANDOM_SEED = Long.parseLong(random_seed);

			RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(RANDOM_SEED));
			
			///Playing Multiple Games using loop instead of playMultiple
			GameResult result;
			int NUM = 500;
			float[][] game_data = null;
			float[][] action_data = null;
			
			//
			for (int i=0;i<NUM;i++) {
				result = new Game(ACTION_TIME_LIMIT).playSingle(AGENT, random);
				
				if (result.getScore() > 2000) {
					
					//System.out.println(result.getScore());
					int[] actions = result.get_actions();
					int [][][] board_states = result.get_states();
				
					for (int b=0;b<board_states.length;b++) {
						float[] oh_action = Utils.one_hot_action(actions[b]);
						int[] flattened_board = Utils.flatten_board(board_states[b]);
						float[] normed_board = Utils.normalize_data(flattened_board);
					
						if (game_data == null) {
							game_data = new float[1][normed_board.length];
							game_data[0] = normed_board;
							
							action_data = new float[1][oh_action.length];
							action_data[0] = oh_action;
							
						}
						else {
							float[][] n_game_data = new float[game_data.length+1][game_data[0].length];
							float[][] n_action_data = new float[action_data.length+1][action_data[0].length];
							
							for (int k=0;k<game_data.length;k++) {
								n_game_data[k] = game_data[k];	
							}
							for (int k=0;k<action_data.length;k++) {
								n_action_data[k] = action_data[k];	
							}
							n_game_data[n_game_data.length-1] = normed_board;
							n_action_data[n_action_data.length-1] = oh_action;
							game_data = n_game_data;
							action_data = n_action_data;
						}


					}	
				}
							
			}
			
			
			try {
				AccessingGameData.save_game_data(game_data, action_data, "src//main//java//nic//test.txt");
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			System.out.println(game_data.length);
			System.out.println(action_data.length);

	}

	private static Supplier<Agent> createAgentFactoryByReflection() {
		return () -> new CustomAgent();
	}
}


