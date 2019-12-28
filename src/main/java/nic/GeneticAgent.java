package nic;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

import com.google.common.base.Preconditions;

import put.game2048.Action;
import put.game2048.Agent;
import put.game2048.Board;

public class GeneticAgent implements Agent {
	public RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
	public static Action[] ACTIONS = { Action.UP, Action.RIGHT, Action.DOWN, Action.LEFT };
	public static final int NSTATES = 13;
	public static final int N = NSTATES * NSTATES * NSTATES * NSTATES;
	
	public float[] lineMap;
	
	
	public GeneticAgent() {
		// this.lineMap = new float[NSTATES * NSTATES * NSTATES * NSTATES];
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream("lineMap.bin");
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			this.lineMap = (float[]) objectInputStream.readObject();
			objectInputStream.close(); 
		} catch (IOException | ClassNotFoundException e) {
			this.lineMap = new float[NSTATES * NSTATES * NSTATES * NSTATES];
			e.printStackTrace();
		}
	}

	public GeneticAgent(float[] lineMap) {
		this.lineMap = lineMap;
	}
	
	public static GeneticAgent makeRandom(RandomDataGenerator rand) {
		float[] lineMap = new float[N];
		for(int j = 0; j < N; j++) {
			lineMap[j] = (float) rand.nextGaussian(0, 1);
		}
		return new GeneticAgent(lineMap);
	}

	public static void mutate(GeneticAgent individual, RandomDataGenerator rand) {
		int mutationPoint = rand.nextInt(0, N-1);
		individual.lineMap[mutationPoint] += rand.nextGaussian(0, 1);
	}
	
	
	public static GeneticAgent crossover(float[] mom, float[] dad, RandomDataGenerator rand) {
		float[] child = new float[N];
		// Use binomial distribution to have most of the crossover points close to the middle
		int crossoverPoint = rand.nextBinomial(N-1, 0.5);
		System.arraycopy(mom, 0, child, 0, crossoverPoint);
		System.arraycopy(dad, crossoverPoint, child, crossoverPoint, N-crossoverPoint);
		
		return new GeneticAgent(child);
	}
	
	public float evaluate(int[][] board) {
		float score = 0;
		// Left-to-right and up to down
		for(int i = 0; i < 4; i++) {
			int key1 = 0;
			int key2 = 0;
			int key3 = 0;
			int key4 = 0;

			for(int j = 0; j < 4; j++) {
				key1 = NSTATES * key1 + board[i][j];
				key2 = NSTATES * key2 + board[j][i];
				key3 = NSTATES * key3 + board[3-i][j];
				key4 = NSTATES * key4 + board[j][3-j];
			}
			score += lineMap[key1] + lineMap[key2] + lineMap[key3] + lineMap[key4];
		}
		return score;
	}

	public Action chooseAction(Board board, List<Action> possibleActions, Duration maxTime) {
		Preconditions.checkArgument(0 < possibleActions.size());
		
		int[][] boardState = board.get();

		// For some weird reason, possibleActions tends to contain duplicates...
		ArrayList<Action> uniquePossibleActions = new ArrayList<Action>(4);
		if(possibleActions.contains(Action.UP)) {    uniquePossibleActions.add(Action.UP); }
		if(possibleActions.contains(Action.DOWN)) {  uniquePossibleActions.add(Action.DOWN); }
		if(possibleActions.contains(Action.LEFT)) {  uniquePossibleActions.add(Action.LEFT); }
		if(possibleActions.contains(Action.RIGHT)) { uniquePossibleActions.add(Action.RIGHT); }

		boolean noScoreYet = true;
		double bestScore   = 0;
		Action bestAction  = null;

		for(Action action : uniquePossibleActions) {
			int[][] simulatedState = Utils.simulateMove(boardState, action);
			double score = 0;
			score += evaluate(simulatedState);
			if(noScoreYet || score > bestScore) {
				noScoreYet = false;
				bestScore  = score;
				bestAction = action;
			}
		}
		
		return bestAction;
	}
}
