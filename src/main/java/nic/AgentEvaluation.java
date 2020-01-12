package nic;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.game2048.Agent;
import put.game2048.Game;
import put.game2048.MultipleGamesResult;

import java.time.Duration;
import java.util.ArrayList;
import java.util.function.Supplier;

public class AgentEvaluation {
	public static int id = 0 ;
	public static void main(String[] args) {
		int action_time_limit_ms = 1000;
		int num_games = 1000;
		String random_seed = "33121565";

		 Supplier<Agent> AGENT = createAgentFactoryByReflection();
		final int REPEATS = num_games;
		final Duration ACTION_TIME_LIMIT = Duration.ofNanos(action_time_limit_ms * 1000 * 1000);
		final long RANDOM_SEED = Long.parseLong(random_seed);
		ArrayList<Tuple> allTuples = new ArrayList<Tuple>();
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister());

		//RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(RANDOM_SEED));
		MultipleGamesResult result = null;
		
 		for (int i =0 ; i<11;i++) {
			AGENT = createAgentFactoryByReflection();
			GeneticAgent g = (GeneticAgent)AGENT.get();
			
			result = new Game(ACTION_TIME_LIMIT).playMultiple(AGENT, REPEATS, random);

			System.out.println(result.toCvsRow());
			for(Tuple t:g.tuples) {
				t.addScore(result.getScore().getMean());
				t.refreshLookupTable();
				allTuples.add(t);
			}
	 		id+=1;
 		}
 		for(int i=id-1;i>=0;i--) {
 			ArrayList<Tuple> tuples= new ArrayList<>();
 			for (int j =0;j<4;j++) {
 				int tupleIndex=0;
 				if (allTuples.size()>1) {
 					tupleIndex = random.nextInt(0, allTuples.size()-1);
 				}
 				
 	 			tuples.add(allTuples.get(tupleIndex));
 	 			allTuples.remove(tupleIndex);
 			}
 			GeneticAgent g = new GeneticAgent(tuples,i);

 			g.storeTuples();
 		}
 		
	}

	private static Supplier<Agent> createAgentFactoryByReflection() {
		GeneticAgent agent = new GeneticAgent(id);
		return () -> agent;
	}
}
