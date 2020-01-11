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


public class GeneticAgent implements Agent, Runnable {

	public RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
	public static Action[] ACTIONS = { Action.UP, Action.RIGHT, Action.DOWN, Action.LEFT };
	private ArrayList<Tuple> tuples;
	private int myId;
	private static int id=0;
	public static final int[] stateMax = {16384, 8192, 4096, 2048, 1024};
	public static final int[] stateIterations = {25, 20, 15, 10, 5};
	//public static final int[] stateIterations = {45, 35, 25, 15, 5};
	private static FileWriter fileWriter ;
  private static  PrintWriter printWriter ;
	private static final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

	/**
	 *
	 */
	public GeneticAgent() {
		this.tuples = new ArrayList<Tuple>();
		FileInputStream fileInputStream;

		try {
			fileInputStream = new FileInputStream("tuples.bin");
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			this.tuples = (ArrayList<Tuple>) objectInputStream.readObject();
			objectInputStream.close();
		} catch (IOException | ClassNotFoundException e) {
			//CHOOSE WHICH OF THE TUPLE SETUPS YOU WANT.
			makeTestTuples(); // - This is the 2 6-tuples and 	2 4-tuples setup
			//makeTestTuples2(); // - This is the 4 horisontal, 4 vertical and 9 squares setup
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
	*/
	public GeneticAgent(ArrayList<Tuple> tuples) {
		this.tuples=tuples;
		this.myId=id;
		id+=1;
	}
	/**
	 * the set of vertical, horisontal and square tuples of length 4
	 */
	public void makeTestTuples2() {
		System.out.println("Making test tuples");
		ArrayList<Tuple> tuples = new ArrayList<Tuple>();
		double[] lookup_table1 = new double[(int) Math.pow(15, 4)];

		for (int i = 0; i < 4; i++) {
			//Map<String, Double> lookup_table1 = new HashMap<String, Double>();

			ArrayList<Pair<Integer, Integer>> t1_cells = new ArrayList<>();
			t1_cells.add(new Pair<>(0, i));
			t1_cells.add(new Pair<>(1, i));
			t1_cells.add(new Pair<>(2, i));
			t1_cells.add(new Pair<>(3, i));

			Tuple t1 = new Tuple(lookup_table1, t1_cells);
			tuples.add(t1);
		}

		double[] lookup_table2 = new double[(int) Math.pow(15, 4)];

		for (int i = 0; i < 4; i++) {
			//Map<String, Double> lookup_table2 = new HashMap<String, Double>();

			ArrayList<Pair<Integer, Integer>> t1_cells = new ArrayList<>();
			t1_cells.add(new Pair<>(i, 0));
			t1_cells.add(new Pair<>(i, 1));
			t1_cells.add(new Pair<>(i, 2));
			t1_cells.add(new Pair<>(i, 3));

			Tuple t1 = new Tuple(lookup_table2, t1_cells);
			tuples.add(t1);
		}

		double[] lookup_table3 =  new double[(int) Math.pow(15, 4)];

		for (int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				//Map<String, Double> lookup_table3 = new HashMap<String, Double>();
				ArrayList<Pair<Integer, Integer>> t1_cells = new ArrayList<>();
				t1_cells.add(new Pair<>(i, j));
				t1_cells.add(new Pair<>(i + 1, j));
				t1_cells.add(new Pair<>(i + 1, j + 1));
				t1_cells.add(new Pair<>(i, j + 1));


				Tuple t1 = new Tuple(lookup_table3, t1_cells);
				tuples.add(t1);
			}
		}

		this.tuples = tuples;
	}

	/**
	 * Set of 2 rectangular tuples of length 6 and 2 tuples of length 4
	 */
	public void makeTestTuples() {

		System.out.println("Making test tuples");
		ArrayList<Tuple> tuples = new ArrayList<Tuple>();
		double[] lookup_table1 = new double[(int) Math.pow(15, 6)];
		double[] lookup_table2 = new double[(int) Math.pow(15, 6)];
		double[] lookup_table3 = new double[(int) Math.pow(15, 4)];
		double[] lookup_table4 = new double[(int) Math.pow(15, 4)];

		ArrayList<Pair<Integer, Integer>> t1_cells = new ArrayList<>();
		t1_cells.add(new Pair<>(0, 0));
		t1_cells.add(new Pair<>(0, 1));
		t1_cells.add(new Pair<>(1, 1));
		t1_cells.add(new Pair<>(2, 1));
		t1_cells.add(new Pair<>(2, 0));
		t1_cells.add(new Pair<>(1, 0));
		Tuple t1 = new Tuple(lookup_table1, t1_cells);

		ArrayList<Pair<Integer, Integer>> t2_cells = new ArrayList<>();
		t2_cells.add(new Pair<>(0, 1));
		t2_cells.add(new Pair<>(0, 2));
		t2_cells.add(new Pair<>(1, 2));
		t2_cells.add(new Pair<>(2, 2));
		t2_cells.add(new Pair<>(2, 1));
		t2_cells.add(new Pair<>(1, 1));
		Tuple t2 = new Tuple(lookup_table2, t2_cells);

		ArrayList<Pair<Integer, Integer>> t3_cells = new ArrayList<>();
		t3_cells.add(new Pair<>(0, 2));
		t3_cells.add(new Pair<>(1, 2));
		t3_cells.add(new Pair<>(2, 2));
		t3_cells.add(new Pair<>(3, 2));
		Tuple t3 = new Tuple(lookup_table3, t3_cells);

		ArrayList<Pair<Integer, Integer>> t4_cells = new ArrayList<>();
		t4_cells.add(new Pair<>(0, 3));
		t4_cells.add(new Pair<>(1, 3));
		t4_cells.add(new Pair<>(2, 3));
		t4_cells.add(new Pair<>(3, 3));
		Tuple t4 = new Tuple(lookup_table4, t4_cells);

		tuples.add(t1);
		tuples.add(t2);
		tuples.add(t3);
		tuples.add(t4);

		this.tuples = tuples;
	}

	/**
	 *
	 */
	public void makeTestTuples3() {
		System.out.println("Making test tuples");
		ArrayList<Tuple> tuples= new ArrayList<Tuple>();
		double[] lookup_table1 = new double[(int) Math.pow(15, 6)];
		double[] lookup_table2 = new double[(int) Math.pow(15, 6)];
		double[] lookup_table3 = new double[(int) Math.pow(15, 6)];

		double[] lookup_table4 = new double[(int) Math.pow(15, 6)];

		ArrayList<Pair<Integer, Integer>> t1_cells = new ArrayList<>();
		t1_cells.add(new Pair<>(0, 0));
		t1_cells.add(new Pair<>(1, 0));
		t1_cells.add(new Pair<>(1, 1));
		t1_cells.add(new Pair<>(0, 1));
		t1_cells.add(new Pair<>(0, 2));
		t1_cells.add(new Pair<>(0, 3));
		Tuple t1 = new Tuple(lookup_table1, t1_cells);

		ArrayList<Pair<Integer, Integer>> t2_cells = new ArrayList<>();
		t2_cells.add(new Pair<>(1, 0));
		t2_cells.add(new Pair<>(2, 0));
		t2_cells.add(new Pair<>(2, 1));
		t2_cells.add(new Pair<>(1, 1));
		t2_cells.add(new Pair<>(1, 2));
		t2_cells.add(new Pair<>(1, 3));
		Tuple t2 = new Tuple(lookup_table2, t2_cells);

		ArrayList<Pair<Integer, Integer>> t3_cells = new ArrayList<>();
		t3_cells.add(new Pair<>(0, 0));
		t3_cells.add(new Pair<>(0, 1));
		t3_cells.add(new Pair<>(0, 2));
		t3_cells.add(new Pair<>(1, 2));
		t3_cells.add(new Pair<>(1, 1));
		t3_cells.add(new Pair<>(1, 0));
		Tuple t3 = new Tuple(lookup_table3, t3_cells);

		ArrayList<Pair<Integer, Integer>> t4_cells = new ArrayList<>();
		t4_cells.add(new Pair<>(1, 0));
		t4_cells.add(new Pair<>(1, 1));
		t4_cells.add(new Pair<>(1, 2));
		t4_cells.add(new Pair<>(2, 2));
		t4_cells.add(new Pair<>(2, 1));
		t4_cells.add(new Pair<>(2, 0));
		Tuple t4 = new Tuple(lookup_table4, t4_cells);

		tuples.add(t1);
		tuples.add(t2);
		tuples.add(t3);
		tuples.add(t4);

		this.tuples = tuples;
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
	 * @param current_state
	 * @param update (whether to update the last key stored in tuple)
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
	 *
	 * @param board
	 * @param possibleActions
	 * @param maxTime
	 * @return
	 */
	public Action chooseAction(Board board, List<Action> possibleActions, Duration maxTime) {
		Preconditions.checkArgument(0 < possibleActions.size());
		return ACTIONS[argmax(board).id()];
	}

	/**
	 * Plays a several games starting from the given state (and learns them)
	 * @param state - starting state
	 * @param learning_rate
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
	 *
	 * @param numGames
	 * @param learningRate
	 */
	 public void learnAgent(int numGames,double learningRate,boolean verbal) {
 		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister());
 		//RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(random_seed));
 		long startTime = System.nanoTime();
 		int score =0;
 	    for (int i =0 ; i<numGames;i++) {
 	    	Game2048 game= new Game2048();
 	    	State2048 state = game.sampleInitialStateDistribution(random);
 	    	int lastStateMax=0;
 	    	score = 0 ;
 	    	while (!game.isTerminalState(state)) {
 	    		//checks the maximum tile reached for this game and if it is in the list starts to learn from that state.
 	    		//this happens only once for each value in the list.
 	    		int max = state.getMaxTile();
 	    		for (int j=0;j<stateMax.length;j++) {
 	    			if(stateMax[j]==max&&stateMax[j]>lastStateMax) {
 	    				for (int numberOfRepetitions=0;numberOfRepetitions<stateIterations[j];numberOfRepetitions++) {
 	    					learnFromState(new State2048(state),learningRate);
 	    					lastStateMax=stateMax[j];
 	    				}
 	    				break;
 	    			}
 	    		}
 	    		int reward = state.makeMove(argmax(new Board(state.getBoard())));
 	    		double currentActionValue = evaluateAfterstate(state, true);
 	    		state.addRandomTile(random);
 	    		score+=reward;
 	    		if (!state.isTerminal()) {

 	    			Pair<Double,Double> nextActionValue = evaluate(state, argmax(new Board(state.getBoard())), false);
 	    			double error = nextActionValue.second()+nextActionValue.first() -currentActionValue;
 	    			for (Tuple t:tuples) {
 	    				//t.update(error*learning_rate/g.tuples.size());
 	    				t.update(error*learningRate);
 	    			}
 	    		}
 	    	}
 	    	if (verbal) {
 	    		printWriter.println(String.format("%d\t%d", i+1, score));
 				printWriter.flush();

 				if((i+1) % 100 == 0 || (i+1) == numGames) {
 					Date date = new Date();
 					System.out.println(String.format("%s: %3d Games Played, Score: %4d",
 							formatter.format(date), i+1, score));
 				}

 	    	}
 	    }
 		System.out.println("took "+String.valueOf((System.nanoTime()-startTime)/1_000_000_000)+" seconds for "+String.valueOf(numGames)+" games");

 	}
 	/**
 	 *
 	 * @param args
 	 * @throws IOException
 	 */
 	public static void main(String[] args) throws IOException {
 		fileWriter= new FileWriter("run.tsv");
 		printWriter = new PrintWriter(fileWriter);
 		double learning_rate = 0.0025; // to have a better 'immediate' result use 0.01 but seems like in long term 0.0025 is better
 		int action_time_limit_ms = 1000;
 		int numGames = 1000;
 		int random_seed = 123;
 		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister());
 		//RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(random_seed));

 		GeneticAgent g = new GeneticAgent(new ArrayList<Tuple>());
 		g.makeTestTuples();
 		GeneticAgent g1 = new GeneticAgent(new ArrayList<Tuple>());
 		g1.makeTestTuples3();
 		new Thread(g).start();
 		new Thread(g1).start();
 		// FileWriter for score logging

 		//g.learnAgent(numGames,learning_rate,true);
 		printWriter.close();

 	}
 	@Override
 	public void run() {
 		learnAgent(10000,0.0025,true);
 		try {
 			FileOutputStream fileOutputStream;
 			fileOutputStream = new FileOutputStream("tuples"+String.valueOf(this.myId)+".bin");
 			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
 			objectOutputStream.writeObject(tuples);
 			objectOutputStream.flush();
 			objectOutputStream.close();
 		} catch (IOException e) {
 			e.printStackTrace();
 		}

 	}
 }
