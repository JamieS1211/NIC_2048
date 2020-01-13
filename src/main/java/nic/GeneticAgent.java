package nic;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

import com.google.common.base.Preconditions;

import put.ci.cevo.games.game2048.Action2048;
import put.ci.cevo.games.game2048.Game2048;
import put.ci.cevo.games.game2048.State2048;
import put.ci.cevo.util.Pair;
import put.game2048.Action;
import put.game2048.Agent;
import put.game2048.Board;


public class GeneticAgent implements Agent {
	public RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
	public static Action[] ACTIONS = { Action.UP, Action.RIGHT, Action.DOWN, Action.LEFT };
	private ArrayList<Tuple> tuples;
	public static final int[] stateMax = {16384, 8192, 4096, 2048, 1024};
	public static final int[] stateIterations = {25, 20, 15, 10, 5};

	private static final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

	/**
	 *
	 */
	public GeneticAgent() {
		this.tuples = new ArrayList<>();
		FileInputStream fileInputStream;

		try {
			fileInputStream = new FileInputStream("tuples.bin");
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			this.tuples = (ArrayList<Tuple>) objectInputStream.readObject();
			objectInputStream.close(); 
		} catch (IOException | ClassNotFoundException e) {
			RandomDataGenerator rand = new RandomDataGenerator(new MersenneTwister());
			for (int i = 0; i < 4; i++) {
				int size = rand.nextInt(2, 6);
				double[] lookup_table = new double[(int) Math.pow(15, size)];
				this.tuples.add(new Tuple(lookup_table, new TupleGenotype(size)));
			}
		}
	}

	/**
	 *
	 * @param genotypes
	 */
	public GeneticAgent(TupleGenotype[] genotypes) {
		this.tuples = new ArrayList<>();

		for (TupleGenotype individual : genotypes) {
			double[] lookup_table = new double[(int) Math.pow(15, individual.turns.length + 1)];
			this.tuples.add(new Tuple(lookup_table, individual));
		}
	}

	/**
	 *
	 * @param random
	 */
	public GeneticAgent(RandomDataGenerator random) {
		this();
		this.random = random;
	}

	/**
	 *
	 * Evaluate the given state-action pair 
	 * @param current_state
	 * @param current_action
	 * @param update - tuples keep both V(s') and V(s'_next) if update is true then they are updated. If you only want to evaluate the state
	 * without the future update use false instead.
	 * @return
	 */
	public Pair<Double, Double> evaluate(State2048 current_state, Action2048 current_action, boolean update){
		// The argument current_state is the state s''. This function calculates the afterstate s'_next from s''.
		// Tuples store previous afterstate value (referred as s')
		double tupleWeights = 0;
		//current_state.printHumanReadable();
		//First calculate afterstate
		State2048 afterState = new State2048(current_state);

		int reward = afterState.makeMove(current_action);

		//Need to obtain sum of all tuple weights from the table
		//pdate=true updates the previous afterstate estimate with the current one
		for (int i = 0; i < 4; i++) {
			for (Tuple t :tuples) {
				tupleWeights += t.evaluateBoard(afterState.getBoard(), update);
				tupleWeights += t.evaluateBoardReflection(afterState.getBoard(), update);
			}
			afterState.rotateBoard();
		}
		//System.out.println((double)reward);
		return new Pair<>((double) reward, tupleWeights);
	}

	/**
	 * Evaluate the afterstate value for current tuples
	 * @param current_state - current board state
	 * @param update - whether to update the last key stored in tuple
	 * @return
	 */
	public double evaluateAfterstate(State2048 current_state, boolean update){
		// The argument current_state is the state s''. This function calculates the afterstate s'_next from s''.
		// Tuples store previous afterstate value (referred as s')
		double tupleWeights = 0;

		//Need to obtain sum of all tuple weights from the table
		//pdate=true updates the previous afterstate estimate with the current one
		for (int i = 0; i < 4; i++) {
			for (Tuple t :tuples) {
				tupleWeights += t.evaluateBoard(current_state.getBoard(), update);
				tupleWeights += t.evaluateBoardReflection(current_state.getBoard(), update);
			}
			current_state.rotateBoard();
		}
		//System.out.println((double)reward);
		return tupleWeights;
	}

	/**
	 * Choose the best action from the current board state (based on values from lookup table)
	 * @param current_state - any given board state (NOT AFTERSTATE). The function uses this.evaluate (which calculates the afterstate)
	 * @return
	 */
	public Action2048 argmax(Board current_state) {

		//Turn board into state. From the state s calculate the action that result in the best s' based on the table values
		State2048 state = new State2048(current_state.get());
		ArrayList<Action2048> possible_actions = state.getPossibleMoves();
		Action2048 a_next = possible_actions.get(0);
		Pair<Double, Double> best_reward_and_estimate = new Pair<>(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);

		for (Action2048 current_action : possible_actions) {
			//Refers to r+V(s')
			Pair<Double, Double> reward_and_estimate = this.evaluate(state, current_action,false);

			if ((reward_and_estimate.second() + reward_and_estimate.first()) > (best_reward_and_estimate.second() + best_reward_and_estimate.first())) {
				a_next = current_action;
				best_reward_and_estimate = reward_and_estimate;
			}
		}
		//a_next is the best predicted action
		return a_next;
	}

	/**
	 * Chose the most promising move
	 * @param board - Current board state
	 * @param possibleActions - Possible actions given the board state
	 * @param maxTime - Maximum time to make a decision
	 * @return - Move to make
	 */
	public Action chooseAction(Board board, List<Action> possibleActions, Duration maxTime) {
		Preconditions.checkArgument(0 < possibleActions.size());
		return ACTIONS[argmax(board).id()];
	}

	/**
	 * Plays a several games starting from the given state (and learns them)
	 * @param state - Starting state
	 * @param learning_rate - Rate of errors to adjust tuple values
	 */
	public void learnFromState(State2048 state, double learning_rate) {
		
		while (!state.isTerminal()) {   
    		int reward = state.makeMove(argmax(new Board(state.getBoard())));
    		double currentActionValue = evaluateAfterstate(state, true);
    		state.addRandomTile(random);

    		if (!state.isTerminal()) {
    			Pair<Double, Double> nextActionValue = evaluate(state, argmax(new Board(state.getBoard())), false);
    			double error = nextActionValue.second() + nextActionValue.first() - currentActionValue;
    			for (Tuple t : tuples) {
    				//t.update(error*learning_rate/g.tuples.size());
    				t.update(error * learning_rate);
    			}
    		}
    	}
	}

	/**
	 * Perform a tuple learning operation with the current agent
	 * @param numGames - Number of gains to train on
	 * @param learningRate - Rate of errors to adjust tuple values
	 */
	public void learnAgent(int numGames, double learningRate) {
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister());
		//RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(random_seed));

	    for (int i = 0; i < numGames; i++) {
	    	Game2048 game = new Game2048();
	    	State2048 state = game.sampleInitialStateDistribution(random);
	    	int lastStateMax = 0;

	    	while (!game.isTerminalState(state)) {
	    		//checks the maximum tile reached for this game and if it is in the list starts to learn from that state.
	    		//this happens only once for each value in the list.
	    		int max = state.getMaxTile();

	    		for (int j = 0; j < stateMax.length; j++) {
	    			if (stateMax[j] == max && stateMax[j] > lastStateMax) {
	    				for (int numberOfRepetitions = 0; numberOfRepetitions < stateIterations[j]; numberOfRepetitions++) {
	    					learnFromState(new State2048(state), learningRate);
	    					lastStateMax = stateMax[j];
	    				}

	    				break;
	    			}
	    		}

	    		int reward = state.makeMove(argmax(new Board(state.getBoard())));
	    		double currentActionValue = evaluateAfterstate(state, true);
	    		state.addRandomTile(random);
	    		
	    		if (!state.isTerminal()) {
	    			Pair<Double, Double> nextActionValue = evaluate(state, argmax(new Board(state.getBoard())), false);
	    			double error = nextActionValue.second() + nextActionValue.first() - currentActionValue;
	    			for (Tuple t : tuples) {
	    				//t.update(error*learning_rate/g.tuples.size());
	    				t.update(error * learningRate);
	    			}
	    		}
	    	}
	    }

	}

	/**
	 * Store tuples to a tuples.bin file
	 */
	public void storeTuples() {
		try {
			FileOutputStream fileOutputStream;
			fileOutputStream = new FileOutputStream("tuples.bin");
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(tuples);
			objectOutputStream.flush();
			objectOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		double learning_rate = 0.0025; // to have a better 'immediate' result use 0.01 but seems like in long term 0.0025 is better
		int action_time_limit_ms = 1000;
		int numGames = 1000;
		int random_seed = 123;
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister());
		//RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(random_seed));

		GeneticAgent g = new GeneticAgent(random);
		long startTime = System.nanoTime();
		// FileWriter for score logging
	    FileWriter fileWriter = new FileWriter("run.tsv");
	    PrintWriter printWriter = new PrintWriter(fileWriter);
	    
	    for (int i = 0; i < numGames; i++) {

	    	Game2048 game = new Game2048();
	    	State2048 state = game.sampleInitialStateDistribution(g.random);
	    	int score = 0;
	    	int lastStateMax = 0;

	    	while (!game.isTerminalState(state)) {
	    		//checks the maximum tile reached for this game and if it is in the list starts to learn from that state.
	    		//this happens only once for each value in the list.
	    		int max = state.getMaxTile();
	    		for (int j = 0; j < stateMax.length; j++) {
	    			if (stateMax[j] == max && stateMax[j] > lastStateMax) {
	    				System.out.println("learning from state");
	    				state.printHumanReadable();

	    				for (int numberOfRepetitions = 0; numberOfRepetitions<stateIterations[j]; numberOfRepetitions++) {
	    					g.learnFromState(new State2048(state), learning_rate);
	    					lastStateMax = stateMax[j];
	    				}

	    				break;
	    			}
	    		}
	    		
	    		int reward = state.makeMove(g.argmax(new Board(state.getBoard())));

	    		score += reward;
	    		double currentActionValue = g.evaluateAfterstate(state, true);
	    		state.addRandomTile(g.random);
	    		
	    		if (!state.isTerminal()) {
	    			Pair<Double, Double> nextActionValue = g.evaluate(state, g.argmax(new Board(state.getBoard())), false);
	    			double error = nextActionValue.second() + nextActionValue.first() - currentActionValue;
	    			for (Tuple t : g.tuples) {
	    				//t.update(error*learning_rate/g.tuples.size());
	    				t.update(error * learning_rate);
	    			}
	    		}
	    	}
	    	
			printWriter.println(String.format("%d\t%d", i + 1, score));
			printWriter.flush();

			if ((i + 1) % 100 == 0 || (i + 1) == numGames) {
				Date date = new Date();
				System.out.println(String.format("%s: %3d Games Played, Score: %4d", formatter.format(date), i + 1, score));
			}
	    }
		g.storeTuples();
		printWriter.close();
		System.out.println("took " + (System.nanoTime() - startTime) / 1_000_000_000 + " seconds for " + numGames + " games");
	}
}

