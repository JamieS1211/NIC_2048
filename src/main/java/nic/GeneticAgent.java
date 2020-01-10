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
import java.util.Iterator;
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
	public static final Action[] ACTIONS = { Action.UP, Action.RIGHT, Action.DOWN, Action.LEFT };
	private ArrayList<Tuple> tuples;
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
			//CHOOSE WHICH OF THE TUPLE SETUPS YOU WANT.
			//makeTestTuples();// - This is the 2 6-tuples and 2 4-tuples setup
			makeTestTuples2(); // - This is the 4 horisontal, 4 vertical and 9 squares setup
		}
	}

	/**
	 *
	 * @param random
	 */
	public GeneticAgent(RandomDataGenerator random) {
		this.tuples = new ArrayList<>();
		FileInputStream fileInputStream;

		try {
			fileInputStream = new FileInputStream("tuples.bin");
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			this.tuples = (ArrayList<Tuple>) objectInputStream.readObject();
			objectInputStream.close(); 
		} catch (IOException | ClassNotFoundException e) {
			//CHOOSE WHICH OF THE TUPLE SETUPS YOU WANT.
			//makeTestTuples();// - This is the 2 6-tuples and 2 4-tuples setup
			makeTestTuples2(); // - This is the 4 horisontal, 4 vertical and 9 squares setup
		}

		this.random = random;
	}

	//This method would help to initialize the population of random tuples
	// THIS IS NOT USED YET. Mostly useful for mutated tuples approach
	/**
	 *
	 * @param tuple_length
	 * @param random
	 */
	public  void makeRandomTuples(int tuple_length,RandomDataGenerator random) {
		int no_tuples = random.nextInt(1,5);//Not sure if lower bound is 1 or 0.
		ArrayList<Tuple> tuples= new ArrayList<>();

		for (int i = 0; i < no_tuples; i++) {
			//TODO SHARED TABLES ARE NOT YET IMPLEMENTED
			//i is the current tuple up to randomly decided number of them
			ArrayList<Pair<Integer,Integer>> tuple_cells = new ArrayList<>();
			//Map<String, Double> lookup_table = new HashMap<String, Double>();

			for (int j = 0; j < tuple_length; j++) {

				//each i'th tuple is iterated over its length(j)
				//x,y are a random coordinate on the board
				int x;
				int y;

				boolean already_added = false;
				//Add a random cell to a tuple. Check to make sure it is not in the tuple already.
				//If it is in the tuple repeat the loop
				do  {
					x = random.nextInt(0,4);
					y = random.nextInt(0,4);

					for (Pair<Integer,Integer> cell : tuple_cells) {
						if (cell.first() == x && cell.second() == y) {
							already_added = true;
						}
					}

				} while (already_added);

			}
			//tuples.add(new Tuple(lookup_table,tuple_cells));
		}
	}

	/**
	 * the set of vertical, horizontal and square tuples of length 4
	 */
	public void makeTestTuples2() {
		System.out.println("Making test tuples");
		ArrayList<Tuple> tuples= new ArrayList<>();

		double[] lookup_table1 = new double[(int) Math.pow(15, 4)];

		for (int i = 0; i < 4; i++) {
			//Map<String, Double> lookup_table1 = new HashMap<String, Double>();
			ArrayList<Pair<Integer,Integer>> t1_cells = new ArrayList<>();
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
			ArrayList<Pair<Integer,Integer>> t1_cells = new ArrayList<>();
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
				ArrayList<Pair<Integer,Integer>> t1_cells = new ArrayList<>();
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

		ArrayList<Tuple> tuples = new ArrayList<>();
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
	 * Evaluate the given state-action pair 
	 * @param current_state
	 * @param current_action
	 * @param update - tuples keep both V(s') and V(s'_next) if update is true then they are updated. If you only want to evaluate the state
	 * without the future update use false instead.
	 * @return
	 */
	public Pair<Double, Double> evaluate(State2048 current_state, Action2048 current_action, boolean update) {
		// The argument current_state is the state s''. This function calculates the afterstate s'_next from s''.
		// Tuples store previous afterstate value (referred as s')
		double tuple_weights = 0;
		//current_state.printHumanReadable();
		//First calculate afterstate
		State2048 afterState = new State2048(current_state);

		int reward = afterState.makeMove(current_action);

		//Need to obtain sum of all tuple weights from the table
		//pdate=true updates the previous afterstate estimate with the current one
		for (Tuple t : tuples) {
			tuple_weights += t.evaluateBoard(afterState.getBoard(),update);
		}
		//System.out.println((double)reward);
		return new Pair<>((double) reward, tuple_weights);
	}

	/**
	 * Evaluate the afterstate value for current tuples
	 * @param current_state
	 * @param update (whether to update the last key stored in tuple)
	 * @return
	 */
	public double evaluateAfterstate(State2048 current_state, boolean update) {
		// The argument current_state is the state s''. This function calculates the afterstate s'_next from s''.
		// Tuples store previous afterstate value (referred as s')
		double tupleWeights = 0;
		//current_state.printHumanReadable();
		//First calculate afterstate
		State2048 afterState = new State2048(current_state);


		//Need to obtain sum of all tuple weights from the table
		//pdate=true updates the previous afterstate estimate with the current one
		for (Tuple t : tuples) {
			tupleWeights += t.evaluateBoard(afterState.getBoard(), update);
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
		Iterator<Action2048> iterator = possible_actions.iterator();

		while(iterator.hasNext()) {
			Action2048 current_action = iterator.next();
			//Refers to r+V(s')
			Pair<Double, Double> reward_and_estimate = this.evaluate(state, current_action, false);

			if ((reward_and_estimate.second() + reward_and_estimate.first()) > (best_reward_and_estimate.second()+best_reward_and_estimate.first())) {
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
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		double learning_rate = 0.0025; // to have a better 'immediate' result use 0.01 but seems like in long term 0.0025 is better
		int action_time_limit_ms = 1000;
		int num_games = 50000;
		int random_seed = 55;
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(random_seed));
		GeneticAgent g = new GeneticAgent(random);

		long startTime = System.nanoTime();

		// FileWriter for score logging
	    FileWriter fileWriter = new FileWriter("run.tsv");
	    PrintWriter printWriter = new PrintWriter(fileWriter);
	    
	    for (int i = 0; i < num_games; i++) {
	    	Game2048 game = new Game2048();
	    	State2048 state = game.sampleInitialStateDistribution(g.random);
	    	int score = 0;

	    	while (!game.isTerminalState(state)) {   
	    		Action2048 bestAction = g.argmax(new Board(state.getBoard()));
	    		int reward = state.makeMove(bestAction);

	    		score += reward;
	    		Pair<Double, Double> currentActionValue = g.evaluate(state, bestAction, true);
	    		state.addRandomTile(g.random);

	    		if (!state.isTerminal()) {
	    			Pair<Double, Double> nextActionValue = g.evaluate(state, g.argmax(new Board(state.getBoard())), false);
	    			double error = nextActionValue.second() + nextActionValue.first() - currentActionValue.second();

	    			for (Tuple t : g.tuples) {
	    				t.update(error*learning_rate);
	    			}
	    		}
	    	}

			printWriter.println(String.format("%d\t%d", i+1, score));
			printWriter.flush();

			if((i + 1) % 100 == 0 || (i + 1) == num_games) {
				Date date = new Date();
				System.out.println(String.format("%s: %3d Games Played, Score: %4d",
						formatter.format(date), i + 1, score));
			}
	    }

		printWriter.close();
		System.out.println("took " + (System.nanoTime() - startTime) / 1_000_000_000 + " seconds for " + num_games + " games");

		try {
			FileOutputStream fileOutputStream;
			fileOutputStream = new FileOutputStream("tuples.bin");
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(g.tuples);
			objectOutputStream.flush();
			objectOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}