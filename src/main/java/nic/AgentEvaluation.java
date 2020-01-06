package nic;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.game2048.Agent;
import put.game2048.Game;
import put.game2048.GameResult;
import put.game2048.MultipleGamesResult;

import java.time.Duration;
import java.util.function.Supplier;

public class AgentEvaluation {
	public static void main(String[] args) {
		int action_time_limit_ms = 1000;
		int num_games = 100;
		String random_seed = "1234";

			final Supplier<Agent> AGENT = createAgentFactoryByReflection();
			final int REPEATS = num_games;
			final Duration ACTION_TIME_LIMIT = Duration.ofNanos(action_time_limit_ms * 1000 * 1000);
			final long RANDOM_SEED = Long.parseLong(random_seed);

			RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(RANDOM_SEED));
			MultipleGamesResult result = new Game(ACTION_TIME_LIMIT).playMultiple(AGENT, REPEATS, random);
			System.out.println(result.toCvsRow());
			System.out.println(result.getScore());
			//GameResult result;
			//for (int i=0;i<REPEATS;i++) {
			//	result = new Game(ACTION_TIME_LIMIT).playSingle(AGENT, random);
			//	System.out.println(result.getScore());
			//}

	}

	private static Supplier<Agent> createAgentFactoryByReflection() {
		return () -> new CustomAgent();
	}
}
