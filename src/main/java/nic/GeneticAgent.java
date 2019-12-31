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
	
	public float[] horizontal;
	public float[] vertical;
	public float[] squares;
	
	public GeneticAgent() {
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream("ruleset.bin");
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			GeneticAgent readAgent = (GeneticAgent) objectInputStream.readObject();
			this.horizontal = readAgent.horizontal;
			this.vertical   = readAgent.vertical;
			this.squares    = readAgent.squares;
			objectInputStream.close(); 
		} catch (IOException | ClassNotFoundException e) {
			this.horizontal = new float[N];
			this.vertical   = new float[N];
			this.squares    = new float[N];
			e.printStackTrace();
		}
	}

	public GeneticAgent(float[] horizontal, float[] vertical, float[] squares) {
		this.horizontal = horizontal;
		this.vertical   = vertical;
		this.squares    = squares;
	}
	
	public static GeneticAgent makeRandom(RandomDataGenerator rand) {
		float[] horizontal = new float[N];
		float[] vertical   = new float[N];
		float[] squares    = new float[N];
		for(int j = 0; j < N; j++) {
			horizontal[j] = (float) rand.nextGaussian(0, 1);
			vertical[j] = (float) rand.nextGaussian(0, 1);
			squares[j] = (float) rand.nextGaussian(0, 1);
		}
		return new GeneticAgent(horizontal, vertical, squares);
	}

	public static void mutate(GeneticAgent individual, RandomDataGenerator rand) {
		{
			int mutationPoint = rand.nextInt(0, N-1);
			individual.horizontal[mutationPoint] += rand.nextGaussian(0, 1);
		}
		{
			int mutationPoint = rand.nextInt(0, N-1);
			individual.vertical[mutationPoint] += rand.nextGaussian(0, 1);
		}
		{
			int mutationPoint = rand.nextInt(0, N-1);
			individual.squares[mutationPoint] += rand.nextGaussian(0, 1);
		}
	}
	
	
	public static GeneticAgent crossover(GeneticAgent mom, GeneticAgent dad, RandomDataGenerator rand) {
		float[] child_horizontal = new float[N];
		float[] child_vertical   = new float[N];
		float[] child_squares    = new float[N];
		// Use binomial distribution to have most of the crossover points close to the middle
		// int crossoverPoint = rand.nextBinomial(N-1, 0.5);
		int crossoverPointHorizontal = rand.nextInt(0, N-1);
		System.arraycopy(mom.horizontal, 0, child_horizontal, 0, crossoverPointHorizontal);
		System.arraycopy(dad.horizontal, crossoverPointHorizontal,
				child_horizontal, crossoverPointHorizontal, N-crossoverPointHorizontal);

		int crossoverPointVertical = rand.nextInt(0, N-1);
		System.arraycopy(mom.vertical, 0, child_vertical, 0, crossoverPointVertical);
		System.arraycopy(dad.vertical, crossoverPointVertical,
				child_vertical, crossoverPointVertical, N-crossoverPointVertical);
		
		int crossoverPointSquares = rand.nextInt(0, N-1);
		System.arraycopy(mom.squares, 0, child_squares, 0, crossoverPointSquares);
		System.arraycopy(dad.squares, crossoverPointSquares,
				child_squares, crossoverPointSquares, N-crossoverPointSquares);
		return new GeneticAgent(child_horizontal, child_vertical, child_squares);
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
			score += horizontal[key_h] + vertical[key_v];
		}
		
		// squares
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				int key = 0;
				key = NSTATES * key + board[i][j];
				key = NSTATES * key + board[i][j+1];
				key = NSTATES * key + board[i+1][j];
				key = NSTATES * key + board[i+1][j+1];
				score += squares[key];
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
