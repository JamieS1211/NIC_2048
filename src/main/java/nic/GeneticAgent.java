package nic;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

import com.google.common.base.Preconditions;

import put.game2048.Action;
import put.game2048.Agent;
import put.game2048.Board;

public class GeneticAgent implements Agent, Serializable {
	private static final long serialVersionUID = 1L;
	public RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
	public static Action[] ACTIONS = { Action.UP, Action.RIGHT, Action.DOWN, Action.LEFT };
	public static final int NSTATES = 13;
	public static final int N = NSTATES * NSTATES * NSTATES * NSTATES;
	
	public float[] scoreMap; // 0..N-1: horizontal, N..2*N-1: vertical, 2*N..3*N-1: squares
	public int[] keysUsed;
	public int totalKeysUsed = 0;
	
	public GeneticAgent() {
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream("ruleset.bin");
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			GeneticAgent readAgent = (GeneticAgent) objectInputStream.readObject();
			this.scoreMap = readAgent.scoreMap;
			objectInputStream.close(); 
		} catch (IOException | ClassNotFoundException e) {
			this.scoreMap = new float[3 * N];
			e.printStackTrace();
		}
		resetUsedMap();
	}

	public GeneticAgent(float[] scoreMap) {
		this.scoreMap = scoreMap;
		resetUsedMap();
	}

	public GeneticAgent(float[] scoreMap, int[] keysUsed, int totalKeysUsed) {
		this.scoreMap = scoreMap;
		this.keysUsed = keysUsed;
		this.totalKeysUsed = totalKeysUsed;
	}

	public void resetUsedMap() {
		this.keysUsed = new int[3 * N];
		for(int j = 0; j < 3 * N; j++) {
			keysUsed[j] = 0;
		}
	}

	private static float randomValue(RandomDataGenerator rand) {
		return (float) rand.nextGaussian(0, 1);
	}
	
	public static GeneticAgent makeRandom(RandomDataGenerator rand) {
		float[] scoreMap = new float[3 * N];
		for(int j = 0; j < 3 * N; j++) {
			scoreMap[j] = randomValue(rand);
		}
		return new GeneticAgent(scoreMap);
	}

	public static void mutate(GeneticAgent individual, RandomDataGenerator rand) {
		// Select mutation point based on frequency of use
		int mutationRand = rand.nextInt(0, individual.totalKeysUsed-1);
		for(int j = 0; j < 3 * N; j++) {
			mutationRand -= individual.keysUsed[j];
			if(mutationRand < 0) {
				individual.scoreMap[j] += randomValue(rand);
				break;
			}
		}
	}
	
	public static GeneticAgent crossover(GeneticAgent mom, GeneticAgent dad, RandomDataGenerator rand) {
		float[] childScoreMap = new float[3*N];
		int[] childKeysUsed = new int[3*N];
		int childTotalKeys = 0;
		int crossoverPoint = rand.nextInt(0, 3*N-1);
		for(int j = 0; j < 3 * N; j++) {
			if(j < crossoverPoint) {
				childScoreMap[j] = mom.scoreMap[j];
				childKeysUsed[j] = mom.keysUsed[j];
				childTotalKeys  += mom.keysUsed[j];
			} else {
				childScoreMap[j] = dad.scoreMap[j];
				childKeysUsed[j] = dad.keysUsed[j];
				childTotalKeys  += dad.keysUsed[j];
			}
		}
		GeneticAgent child = new GeneticAgent(childScoreMap, childKeysUsed, childTotalKeys);
		return child;

	}
	
	public float evaluate(int[][] board) {
		float score = 0;
	
		// horizontal and vertical
		for(int i = 0; i < 4; i++) {
			int key_h = 0;
			int key_v = 0;

			for(int j = 0; j < 4; j++) {
				key_h = NSTATES * key_h + board[i][j];
				key_v = NSTATES * key_v + board[j][i];
			}
			key_v += N;

			score += scoreMap[key_h] + scoreMap[key_v];
			keysUsed[key_h]++;
			keysUsed[key_v]++;
			totalKeysUsed += 2;
		}
		
		// squares
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				int key = 0;
				key = NSTATES * key + board[i][j];
				key = NSTATES * key + board[i][j+1];
				key = NSTATES * key + board[i+1][j];
				key = NSTATES * key + board[i+1][j+1];
				key += 2 * N;
				score += scoreMap[key];
				keysUsed[key]++;
			}
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
	
	public void save(ObjectOutputStream stream) throws IOException {
		stream.writeObject(this);
	}
}
