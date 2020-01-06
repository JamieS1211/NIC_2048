package nic;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

import com.google.common.base.Preconditions;

import put.ci.cevo.games.game2048.Action2048;
import put.ci.cevo.games.game2048.Game2048;
import put.ci.cevo.games.game2048.State2048;
import put.ci.cevo.rl.environment.Transition;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.RandomUtils;
import put.game2048.Action;
import put.game2048.Agent;
import put.game2048.Board;
import put.game2048.Game;
import put.game2048.MultipleGamesResult;

public class GeneticAgent implements Agent {
	public RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
	public static Action[] ACTIONS = { Action.UP, Action.RIGHT, Action.DOWN, Action.LEFT };
	private  ArrayList<Tuple> tuples;

	public GeneticAgent() {
		this.tuples = new ArrayList<Tuple>();
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream("tupes.bin");
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			this.tuples = (ArrayList<Tuple>) objectInputStream.readObject();
			objectInputStream.close(); 
		} catch (IOException | ClassNotFoundException e) {
			//CHOOSE WHICH OF THE TUPLE SETUPS YOU WANT.
			//makeTestTuples();// - This is the 2 6-tuples and 2 4-tuples setup
			makeTestTuples2(); // - This is the 4 horisontal, 4 vertical and 9 squares setup
		}
		int q=0;
		for (Tuple t:tuples) {
			q+=1;
			if (q<6) {
			//System.out.println(t.getHashTable());
				// THIS IS FOR DEBUG. THE CORRECT VERSION HAS NEGATIVE VALUES BUT I DON'T GET ANY NEGATIVES FOR SOME REASON. IF YOU GET LOTS OF PRINTS DELETE THIS.
				Iterator<Double> i = t.getHashTable().values().iterator();
				//The tuples should be negative sometimes. It does not happen in my implementation.
				while (i.hasNext()) {
					double d = i.next();
					if (d<0) {
						System.out.println(d);
					}
				}
			}
		}
		
}

	//This method would help to initialize the population of random tuples
	// THIS IS NOT USED YET. Mostly useful for mutated tuples approach
	public  void makeRandomTuples(int tuple_length,RandomDataGenerator random) {
		int no_tuples = random.nextInt(1,5);//Not sure if lower bound is 1 or 0.
		ArrayList<Tuple> tuples= new ArrayList<Tuple>();
		for (int i = 0; i<no_tuples;i++) {
			//TODO SHARED TABLES ARE NOT YET IMPLEMENTED
			//i is the current tuple up to randomly decided number of them
			ArrayList<Pair<Integer,Integer>> tuple_cells = new ArrayList<Pair<Integer,Integer>> ();
			Map<String, Double> lookup_table = new HashMap<String, Double>();
			for (int j =0; j<tuple_length;j++) {
				
				//each i'th tuple is iterated over its length(j)
				//x,y are a random coordinate on the board
				int x ;
				int y ;
			
				boolean already_added = false;
				//Add a random cell to a tuple. Check to make sure it is not in the tuple already.
				//If it is in the tuple repeat the loop
				do  {
				x = random.nextInt(0,4);
				y = random.nextInt(0,4);
					for (Pair<Integer,Integer> cell :tuple_cells) {
						if (cell.first()==x && cell.second()==y) {
							already_added=true;
						}
					}
				
				}while (already_added);
				
			}
			tuples.add(new Tuple(lookup_table,tuple_cells));
		}
		
		
	}
	public void makeTestTuples2() {
		
		// the set of vertical, horisontal and square tuples
		System.out.println("Making test tuples");
		ArrayList<Tuple> tuples= new ArrayList<Tuple>();
		for (int i =0 ;i<4;i++) {
			Map<String, Double> lookup_table1 = new HashMap<String, Double>();
			ArrayList<Pair<Integer,Integer>> t1_cells = new ArrayList<Pair<Integer,Integer>> ();
			t1_cells.add(new Pair<Integer,Integer>(0,i));	
			t1_cells.add(new Pair<Integer,Integer>(1,i));
			t1_cells.add(new Pair<Integer,Integer>(2,i));
			t1_cells.add(new Pair<Integer,Integer>(3,i));
	
			Tuple t1 =new Tuple(lookup_table1,t1_cells);
			tuples.add(t1);
		}
		for (int i =0 ;i<4;i++) {
			Map<String, Double> lookup_table1 = new HashMap<String, Double>();
			ArrayList<Pair<Integer,Integer>> t1_cells = new ArrayList<Pair<Integer,Integer>> ();
			t1_cells.add(new Pair<Integer,Integer>(i,0));	
			t1_cells.add(new Pair<Integer,Integer>(i,1));
			t1_cells.add(new Pair<Integer,Integer>(i,2));
			t1_cells.add(new Pair<Integer,Integer>(i,3));
	
			Tuple t1 =new Tuple(lookup_table1,t1_cells);
			tuples.add(t1);
		}
		for (int i =0 ;i<3;i++) {
			for(int j=0;j<3;j++) {
				Map<String, Double> lookup_table1 = new HashMap<String, Double>();
				ArrayList<Pair<Integer,Integer>> t1_cells = new ArrayList<Pair<Integer,Integer>> ();
				t1_cells.add(new Pair<Integer,Integer>(i,j));	
				t1_cells.add(new Pair<Integer,Integer>(i+1,j));
				t1_cells.add(new Pair<Integer,Integer>(i+1,j+1));
				t1_cells.add(new Pair<Integer,Integer>(i,j+1));

		
				Tuple t1 =new Tuple(lookup_table1,t1_cells);
				tuples.add(t1);
			}
		}
		this.tuples=tuples;
	}
	public void makeTestTuples() {
		
		// the four tuples as given in the article 
		System.out.println("Making test tuples");
		ArrayList<Tuple> tuples= new ArrayList<Tuple>();
		Map<String, Double> lookup_table1 = new HashMap<String, Double>();
		Map<String, Double> lookup_table2 = new HashMap<String, Double>();
		Map<String, Double> lookup_table3 = new HashMap<String, Double>();

		Map<String, Double> lookup_table4 = new HashMap<String, Double>();

		ArrayList<Pair<Integer,Integer>> t1_cells = new ArrayList<Pair<Integer,Integer>> ();
		t1_cells.add(new Pair<Integer,Integer>(0,0));	
		t1_cells.add(new Pair<Integer,Integer>(0,1));
		t1_cells.add(new Pair<Integer,Integer>(1,1));
		t1_cells.add(new Pair<Integer,Integer>(2,1));
		t1_cells.add(new Pair<Integer,Integer>(2,0));
		t1_cells.add(new Pair<Integer,Integer>(1,0));
		Tuple t1 =new Tuple(lookup_table1,t1_cells);
		ArrayList<Pair<Integer,Integer>> t2_cells = new ArrayList<Pair<Integer,Integer>> ();
		t2_cells.add(new Pair<Integer,Integer>(0,1));
		t2_cells.add(new Pair<Integer,Integer>(0,2));
		t2_cells.add(new Pair<Integer,Integer>(1,2));
		t2_cells.add(new Pair<Integer,Integer>(2,2));
		t2_cells.add(new Pair<Integer,Integer>(2,1));
		t2_cells.add(new Pair<Integer,Integer>(1,1));
		Tuple t2 =new Tuple(lookup_table2,t2_cells);

		ArrayList<Pair<Integer,Integer>> t3_cells = new ArrayList<Pair<Integer,Integer>> ();
		t3_cells.add(new Pair<Integer,Integer>(0,2));
		t3_cells.add(new Pair<Integer,Integer>(1,2));
		t3_cells.add(new Pair<Integer,Integer>(2,2));
		t3_cells.add(new Pair<Integer,Integer>(3,2));

		Tuple t3 =new Tuple(lookup_table3,t3_cells);
		
		ArrayList<Pair<Integer,Integer>> t4_cells = new ArrayList<Pair<Integer,Integer>> ();
		t4_cells.add(new Pair<Integer,Integer>(0,3));
		t4_cells.add(new Pair<Integer,Integer>(1,3));
		t4_cells.add(new Pair<Integer,Integer>(2,3));
		t4_cells.add(new Pair<Integer,Integer>(3,3));
		Tuple t4 =new Tuple(lookup_table4,t4_cells);
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
	public Pair<Double, Double> evaluate(State2048 current_state,Action2048 current_action,boolean update){
		// The argument current_state is the state s''. This function calculates the afterstate s'_next from s''.
		// Tuples store previous afterstate value (referred as s')
		double tuple_weights=0;
		//current_state.printHumanReadable();
		//First calculate afterstate
		State2048 afterState = new State2048(current_state);

		int reward = afterState.makeMove(current_action);
		//Need to obtain sum of all tuple weights from the table
		int[][] board_state = afterState.getBoard();
		//pdate=true updates the previous afterstate estimate with the current one
		for (Tuple t :tuples) {
			tuple_weights+=t.evaluateBoard(board_state,update);
		}
		//System.out.println((double)reward);
		return new Pair<Double,Double>((double)reward ,tuple_weights);
	}
	/**
	 * Learns from playing game, can also use exploration. I keep learning rate inside the function for now but will move it to a parameter later.
	 * @param current_state - state of the game
	 * @param explorationRate - chance of picking a random action
	 * @return
	 */
	public Action2048 learnEvaluation( Board current_state,double explorationRate) {
		
		double learning_rate = 0.01;
		//Make board into state
		State2048 state = new State2048(current_state.get());
		ArrayList<Action2048> possible_actions = state.getPossibleMoves();
		Action2048 a_next =possible_actions.get(0);
		Pair<Double,Double> best_reward_and_estimate = new Pair<Double, Double>(Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY);
		
		//System.out.println("Choosing best action from state");
		//state.printHumanReadable();
		//find the best of all available actions. Exploration may be used as well to try unlikely actions
		if (random.nextUniform(0, 1) < explorationRate) {
			a_next = possible_actions.get((int)random.nextUniform(0, possible_actions.size()));
			best_reward_and_estimate = this.evaluate(state, a_next,false);
		}
		
		else {
			//find the best action and calculate its value
			a_next = argmax(current_state);
			best_reward_and_estimate = this.evaluate(state, a_next,false);
		}
		//System.out.println("The best action is "+a_next.toString());
		//Evaluate runs once again for the best selected action to store tuple calculations.
		this.evaluate(state, a_next,true);
		for (Tuple t:tuples) {
			t.update(best_reward_and_estimate.first(), learning_rate);
		}

		return a_next;
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
		Action2048 a_next =possible_actions.get(0);
		Pair<Double,Double> best_reward_and_estimate = new Pair<Double, Double>(Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY);
		Iterator <Action2048> iterator = possible_actions.iterator();

		while(iterator.hasNext()) {
		Action2048 current_action = iterator.next();
		//Refers to r+V(s')
		Pair<Double,Double> reward_and_estimate = this.evaluate(state, current_action,false);
		
		if ((reward_and_estimate.second()+reward_and_estimate.first())>(best_reward_and_estimate.second()+best_reward_and_estimate.first())) {
			a_next =current_action;
			best_reward_and_estimate = reward_and_estimate;
		}
		}
		//a_next is the best predicted action
		return a_next;
	}
	public Action chooseAction(Board board, List<Action> possibleActions, Duration maxTime) {
		Preconditions.checkArgument(0 < possibleActions.size());

		return ACTIONS[learnEvaluation(board,0).id()];


		}
	
	public static void main(String[] args) {
		int action_time_limit_ms = 1000;
		int num_games = 5000;
		String random_seed = "37551565";
		GeneticAgent g = new GeneticAgent();
		double explorationRate = 0.001;
		for (int i =0 ; i<num_games;i++) {
			Game2048 game= new Game2048();
			State2048 state = game.sampleInitialStateDistribution(g.random);
		while (!game.isTerminalState(state)) {
			Transition<State2048, Action2048> transition = game.computeTransition(state, g.learnEvaluation(new Board(state.getBoard()), explorationRate));
			state = game.getNextState(transition.getAfterState(), g.random);

		}

	}
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
	
