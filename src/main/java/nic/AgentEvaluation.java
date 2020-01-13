package nic;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.game2048.Agent;
import put.game2048.Game;
import put.game2048.MultipleGamesResult;

import java.time.Duration;
import java.util.function.Supplier;

public class AgentEvaluation {
	public static void main(String[] args) {
		int action_time_limit_ms = 1000;
		int num_games = 1000;
		String random_seed = "3753121565";

		final Supplier<Agent> AGENT = createAgentFactoryByReflection();
		final int REPEATS = num_games;
		final Duration ACTION_TIME_LIMIT = Duration.ofNanos(action_time_limit_ms * 1000 * 1000);
		final long RANDOM_SEED = Long.parseLong(random_seed);

		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(RANDOM_SEED));
		MultipleGamesResult result = new Game(ACTION_TIME_LIMIT).playMultiple(AGENT, REPEATS, random);
		System.out.println(result.toCvsRow());
	}
	public static double evaluateAgent(GeneticAgent g){
		int action_time_limit_ms = 1000;
		int num_games = 1000;

		final Supplier<Agent> AGENT = new Supplier<Agent>() {
			@Override
			public Agent get() {
				return g;
			}
		};
		final int REPEATS = num_games;
		final Duration ACTION_TIME_LIMIT = Duration.ofNanos(action_time_limit_ms * 1000 * 1000);

		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister());
		MultipleGamesResult result = new Game(ACTION_TIME_LIMIT).playMultiple(AGENT, REPEATS, random);
		return result.getScore().getMean();
	}
	private static Supplier<Agent> createAgentFactoryByReflection() {
		GeneticAgent agent = new GeneticAgent();
		return () -> agent;
	}
}
