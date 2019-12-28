package nic;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import put.game2048.Agent;
import put.game2048.Game;
import put.game2048.MultipleGamesResult;

public class GeneticTrainer {
	public static final int EVO_RANDOM_SEED = 27;
	public static final int GAME_RANDOM_SEED = 123;
	
	public static final int POPULATION_SIZE = 300;
	public static final int NUM_MUTATIONS = 128;
	public static final int NUM_GENERATIONS = 5000;
	public static final int GAMES_PLAYED = 50;
	
	public static final int PATTERN_SIZE = 2;
	
	public static final Duration ACTION_TIME_LIMIT = Duration.ofNanos(1000 * 1000);
	
	public static DecimalFormat df = new DecimalFormat("#.##");
	
	public static void main(String[] args) throws IOException {
		RandomDataGenerator randomEvo  = new RandomDataGenerator(new MersenneTwister(EVO_RANDOM_SEED));

		final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		
	    FileWriter fileWriter = new FileWriter("run.tsv");
	    PrintWriter printWriter = new PrintWriter(fileWriter);
		
		GeneticAgent[] population = new GeneticAgent[POPULATION_SIZE];
		// Initialize population
		for(int i = 0; i < POPULATION_SIZE; i++) {
			population[i] = GeneticAgent.makeRandom(randomEvo);
		}

		{
			Date date = new Date();
			System.out.println(String.format("%s Starting Training", formatter.format(date)));
		}
		
		double bestScoreYet = 0;
		
		for(int generation = 0; generation < NUM_GENERATIONS; generation++) {
			int populationSize = population.length;

			// Evaluation
			double[] fitness = new double[populationSize];
			SummaryStatistics summary      = new SummaryStatistics();
			SummaryStatistics summaryScore = new SummaryStatistics();
			SummaryStatistics summaryTime  = new SummaryStatistics();
			
			GeneticAgent[] currentPopulation = population;
			IntStream.range(0, populationSize).parallel().forEach( i -> {
				Game game = new Game(ACTION_TIME_LIMIT);
				RandomDataGenerator randomGame = new RandomDataGenerator(new MersenneTwister());

				MultipleGamesResult result = game.playMultiple(Utils.constantSupplier(currentPopulation[i]), GAMES_PLAYED, randomGame);
				
				// double score = result.getScore().getMean();
				double consistentScore = result.getScore().getMean() -
						1.96 * result.getScore().getStandardDeviation() / Math.sqrt(GAMES_PLAYED);
				
				double currentFitness = Math.pow(consistentScore / 2000, 12);
				
				fitness[i] = currentFitness;
				summary.addValue(currentFitness);
				summaryScore.addValue(consistentScore);
				summaryTime.addValue(result.getActionDurationNanos().getMean() / 1e6);
			});
			
			if(summaryScore.getMax() > bestScoreYet) {
				bestScoreYet = summary.getMax();
				double max = fitness[0];
				int argmax = 0;
				for(int i = 1; i < fitness.length; i++) {
					if(fitness[i] > max) {
						max = fitness[i];
						argmax = i;
					}
				}
				FileOutputStream fileOutputStream = new FileOutputStream("ruleset.bin");
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
				objectOutputStream.writeObject(population[argmax].lineMap);
				objectOutputStream.flush();
				objectOutputStream.close();
			}
			
			Date date = new Date();

			System.out.println(String.format("%s Generation %3d, Max Score: %.2f, Average Score %.2f Average Time: %.4fms",
					formatter.format(date), generation, summaryScore.getMax(), summaryScore.getMean(), summaryTime.getMean()));
			
		    printWriter.println(String.format("%d\t%f\t%f\t%f\t",
		    		generation, summaryScore.getMax(), summaryScore.getMean(), summaryTime.getMean()));
		    printWriter.flush();
			
			// Selection
			double normalization = summary.getSum();
			GeneticAgent[] nextGen = new GeneticAgent[POPULATION_SIZE];
			for(int i = 0; i < POPULATION_SIZE; i++) {
				// Make some children!
				double momR = randomEvo.nextUniform(0, normalization);
				double dadR = randomEvo.nextUniform(0, normalization);
				GeneticAgent mom = null;
				GeneticAgent dad = null;
				for(int j = 0; j < POPULATION_SIZE; j++) {
					if(mom == null && fitness[j] > momR) {
						mom = population[j];
					} else {
						momR -= fitness[j];
					}
					
					if(dad == null && fitness[j] > dadR) {
						dad = population[j];
					} else {
						dadR -= fitness[j];
					}
				}
				if(dad == null) {
					System.out.println("this shouldn't happen");
					dad = population[POPULATION_SIZE-1];
				}
				if(mom == null) {
					System.out.println("this shouldn't happen");
					mom = population[POPULATION_SIZE-1];
				}
				
				nextGen[i] = GeneticAgent.crossover(mom.lineMap, dad.lineMap, randomEvo);
				for(int k = 0; k < NUM_MUTATIONS; k++) {
					GeneticAgent.mutate(nextGen[i], randomEvo);
				}
			}
			population = nextGen;
		}
		printWriter.close();
	}
	
}
