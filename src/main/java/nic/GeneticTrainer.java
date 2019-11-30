package nic;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import put.game2048.Agent;
import put.game2048.Game;
import put.game2048.MultipleGamesResult;

public class GeneticTrainer {
	public static final int EVO_RANDOM_SEED = 27;
	public static final int GAME_RANDOM_SEED = 123;
	
	public static final int POPULATION_SIZE = 100;
	public static final int NUM_GENERATIONS = 100;
	public static final int GAMES_PLAYED = 50;
	public static final int INITIAL_NUM_RULES = 200;
	
	public static final int PATTERN_SIZE = 2;
	
	public static final Duration ACTION_TIME_LIMIT = Duration.ofNanos(1000 * 1000);
	
	public static DecimalFormat df = new DecimalFormat("#.##");
	
	public static void main(String[] args) throws IOException {
		RandomDataGenerator randomEvo  = new RandomDataGenerator(new MersenneTwister(EVO_RANDOM_SEED));
		RandomDataGenerator randomGame = new RandomDataGenerator(new MersenneTwister(GAME_RANDOM_SEED));
		
	    FileWriter fileWriter = new FileWriter("run.tsv");
	    PrintWriter printWriter = new PrintWriter(fileWriter);
		
		GeneticAgent[] population = new GeneticAgent[POPULATION_SIZE];
		// Initialize population
		for(int i = 0; i < POPULATION_SIZE; i++) {
			ArrayList<Rule> rules = new ArrayList<Rule>(INITIAL_NUM_RULES);
			for(int j = 0; j < INITIAL_NUM_RULES; j++) {
				rules.add(Rule.randomRule(PATTERN_SIZE, 4, randomEvo));
			}
			population[i] = new GeneticAgent();
			population[i].rules = rules;
		}
		
		Game game = new Game(ACTION_TIME_LIMIT);
		
		for(int generation = 0; generation < NUM_GENERATIONS; generation++) {
			int populationSize = population.length;
			
			// Evaluation
			double[] fitness = new double[populationSize];
			SummaryStatistics summary      = new SummaryStatistics();
			SummaryStatistics summaryScore = new SummaryStatistics();
			SummaryStatistics summaryTime  = new SummaryStatistics();
			
			for(int i = 0; i < populationSize; i++) {
				MultipleGamesResult result = game.playMultiple(constantSupplier(population[i]), GAMES_PLAYED, randomGame);
				
				double score = result.getScore().getMean();
				double currentFitness = Math.exp(score / 2000 - 1);
				
				fitness[i] = currentFitness;
				summary.addValue(currentFitness);
				summaryScore.addValue(score);
				summaryTime.addValue(result.getActionDurationNanos().getMean() / 1e6);
			}
			
			System.out.println(String.format("Generation %3d, Max Score: %.2f, Average Score %.2f Average Time: %.4fms",
					generation, summaryScore.getMax(), summaryScore.getMean(), summaryTime.getMean()));
			
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
				
				nextGen[i] = new GeneticAgent();
				nextGen[i].rules = crossover(mom.rules, dad.rules, randomEvo);
				mutate(nextGen[i].rules, randomEvo);
			}
			population = nextGen;
		}
		printWriter.close();
	}
	
	public static List<Rule> crossover(List<Rule> dad, List<Rule> mom, RandomDataGenerator random) {
		// RandomUtils.shuffle(dad, random);
		// RandomUtils.shuffle(mom, random);
		// Keep at least 10 rules from each parent
		int crossover_point = random.nextInt(10, mom.size()-10);
		// int mom_crossover_point = random.nextInt(mom.size()-10, mom.size()-10);
		
		List<Rule> child = new ArrayList<Rule>(mom.size());
		
		child.addAll(dad.subList(0, crossover_point));
		child.addAll(mom.subList(crossover_point, mom.size()));
		
		return child;
	}
	
	public static void mutate(List<Rule> original, RandomDataGenerator random) {
		int mutationPoint = random.nextInt(0,  original.size()-1);
		original.set(mutationPoint, Rule.randomRule(PATTERN_SIZE, 4, random));
	}
	
	private static Supplier<Agent> constantSupplier(Agent agent) {
		/** A Supplier that always returns the same agent, works only for stateless agents */
		return () -> agent;
	}
}
