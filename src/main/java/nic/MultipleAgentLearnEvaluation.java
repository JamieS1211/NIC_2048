package nic;
import java.util.ArrayList;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

public class MultipleAgentLearnEvaluation {
	
	public static ArrayList<Integer> done= new ArrayList<>();
	public static ArrayList<GeneticAgent> agents = new ArrayList<>();
	public static int id = 0 ;
	public static int evaluations = 0 ;
	public static final int NUMBER_OF_AGENTS =11; //Due to memory limitations
	public static void createAgents() {
		id=0;
		agents.clear();
 		//RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(random_seed));
 		for (int i =0 ; i<NUMBER_OF_AGENTS;i++) {
 			agents.add(new GeneticAgent(i));
 		}
	}
	public static void createNewAgents() {
		agents.clear();
 		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister());
 		//RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(random_seed));
 		for (int i =0 ; i<NUMBER_OF_AGENTS;i++) {
 			ArrayList<Tuple> tuples = new ArrayList<>();
 			
 			for (int j =0;j<4;j++) {
 	 			int randomLength = random.nextInt(2, 6);
 	 			double [] lut = new double[(int)Math.pow(15, randomLength)];
 	 			tuples.add(new Tuple(lut,new TupleGenotype(randomLength).buildTupleCells()));
 			}
 			
 			agents.add(new GeneticAgent(tuples));
 		}

	}
	public static void report(int id) {
		done.add(id);
		if (done.size()==NUMBER_OF_AGENTS) {
			Thread temp = new Thread () {
				public void run() {
					System.out.println("I am alive");
					agents.clear();
					AgentEvaluation.id=0;
					AgentEvaluation.main(new String[0]);
					if (evaluations<5) {
						done.clear();

						createAgents();
						startAgents();
					}

					evaluations+=1;
				}
			};
		
			temp.start();
		}
	}
	public static void startAgents() {
 		for (GeneticAgent g :agents) {
 			new Thread(g).start();
 		}
	}
	public static void main(String[] args) {
		
		createNewAgents();
		startAgents();
		
		/*
		createAgents();
		for(GeneticAgent g :agents) {
			for (Tuple t :g.tuples) {
				System.out.println(t.scores);
			}
		}
		*/
 	}

	
}
