package put.nic;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.game2048.*;

import java.time.Duration;
import java.util.function.Supplier;

public class NeatEvaluation {
	public static void main(String[] args) {
		int action_time_limit_ms = 1000;
		String random_seed = "1234";
		final Duration ACTION_TIME_LIMIT = Duration.ofNanos(action_time_limit_ms * 1000 * 1000);
		final long RANDOM_SEED = Long.parseLong(random_seed);
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(RANDOM_SEED));

		//Setup constants
		final int generations = 250;
		final int generationSize = 250;
		final int gamesPerBrain = 50;
		final int[] brainLayerSizes = {16, 32, 4}; // Rule of thump, input size * output size / 2

		//Running values of best brain
		int[] bestBrain = new int[2];
		double bestBrainFitness = 0;
		double[] bestBrainRealScore = new double[generations];


		//Running tally of population
		Brain[][] brains = new Brain[generations][generationSize];
		double[][] averageScores = new double[generations][generationSize];

		//Global values
		double[] generationalAverages = new double[generations];

		//Generate first generation of brains
		for (int brainIndex = 0; brainIndex < generationSize; brainIndex++) {
			brains[0][brainIndex] = new Brain(brainLayerSizes);
		}

		//Simulate
		for (int generation = 0; generation < generations; generation++) {
			int maxMoves = 100;
			int totalFitness = 0;

			//Simulate game
			for (int brainIndex = 0; brainIndex < generationSize; brainIndex++) {
				//Setup agent with the brain
				Brain currentBrain = brains[generation][brainIndex];
				final Supplier<Agent> AGENT = () -> new CustomAgent(currentBrain);

				//Average and save the score
				double tally = 0;
				for (int game = 0; game < gamesPerBrain; game++) {
					GameResult result = new GameSimulation(ACTION_TIME_LIMIT).playSingle(AGENT, random, maxMoves);
					tally += result.getScore();
				}

				averageScores[generation][brainIndex] = tally / gamesPerBrain;
				totalFitness += averageScores[generation][brainIndex];

				if (averageScores[generation][brainIndex] > bestBrainFitness) {
					bestBrainFitness = averageScores[generation][brainIndex];
					bestBrain[0] = generation;
					bestBrain[1] = brainIndex;
				}
			}

			if (generation < generations - 1) {
				//Chose what brains to keep and mutate (breeding)
				for (int childBrainIndex = 0; childBrainIndex < generationSize; childBrainIndex++) {
					double position = Math.random() * totalFitness;

					int parentBrainIndex;
					double cumulativeFitness = 0;
					for (parentBrainIndex = 0; parentBrainIndex < generationSize; parentBrainIndex++) {
						cumulativeFitness += averageScores[generation][parentBrainIndex];

						if (cumulativeFitness > position) {
							brains[generation + 1][childBrainIndex] = new Brain(brains[generation][parentBrainIndex]);
							brains[generation + 1][childBrainIndex].mutate();
							break;
						}
					}
				}
			}

			generationalAverages[generation] = 0;
			for (int i = 0; i < averageScores[generation].length; i++) {
				generationalAverages[generation] += averageScores[generation][i] / averageScores[generation].length;
			}

			//Run current best brain for some games
			double tally = 0;
			final Supplier<Agent> AGENT = () -> new CustomAgent(brains[bestBrain[0]][bestBrain[1]]);
			for (int game = 0; game < gamesPerBrain; game++) {
				GameResult result = new GameSimulation(ACTION_TIME_LIMIT).playSingle(AGENT, random, -1);
				tally += result.getScore();
			}
			bestBrainRealScore[generation] = tally / gamesPerBrain;
		}


		System.out.println("Done");
	}
}
