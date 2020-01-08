package nic;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.game2048.Agent;
import put.game2048.Game;
import put.game2048.GameResult;
import put.game2048.MultipleGamesResult;

import java.io.IOException;
import java.time.Duration;
import java.util.function.Supplier;

public class AgentEvaluation {
	public static void main(String[] args) {
		int action_time_limit_ms = 1000;
		int num_games = 1;
		String random_seed = "1234";

		final Supplier<Agent> AGENT = createAgentFactoryByReflection();
		final int REPEATS = num_games;
		final Duration ACTION_TIME_LIMIT = Duration.ofNanos(action_time_limit_ms * 1000 * 1000);
		final long RANDOM_SEED = Long.parseLong(random_seed);

		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(RANDOM_SEED));
		
		float[] nnShape = new float[]{16,32,8,32,4};
		float lr = (float)0.001;
		MultLayeredNN player = new MultLayeredNN(nnShape, lr);
		
		
		try {
			Data data = AccessingGameData.get_game_data("src//main//java//nic//training27_data.txt");
			float[][] train_states = data.states;			
			float[][] train_actions = data.actions;
			
			player.batch_train(train_states, train_actions, (float)0.2, 100000);
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Game game = new Game(ACTION_TIME_LIMIT);
		game.player = player;
		MultipleGamesResult result = game.playMultiple(AGENT, REPEATS, random);
		System.out.println(result.toCvsRow());
		System.out.println(result.getScore());
		
		
			//GameResult result;
			//for (int i=0;i<REPEATS;i++) {
				//result = new Game(ACTION_TIME_LIMIT).playSingle(AGENT, random);
				//if (result.getScore() > 3000) {
				//System.out.println(result.getScore());
				//}
			//}

	}

	private static Supplier<Agent> createAgentFactoryByReflection() {
		return () -> new CustomAgent();
	}
}
