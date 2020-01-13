package nic;
import java.util.ArrayList;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

public class MultipleAgentLearnEvaluation {
	private static final ArrayList<Integer> done = new ArrayList<>();
	private static final ArrayList<GeneticAgent> agents = new ArrayList<>();
	private static int evaluations = 0 ;
	private static int overallEvaluations = 6;
	public static final int NUMBER_OF_AGENTS = 10; // Due to memory limitations

	/**
	 *
	 */
	private static void createAgents() {
		agents.clear();
 		//RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(random_seed));

 		for (int i = 0; i < NUMBER_OF_AGENTS; i++) {
 			agents.add(new GeneticAgent(i));
 		}
	}

	/**
	 *
	 */
	private static void createNewAgents() {
		agents.clear();
 		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister());
 		//RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(random_seed));
 		Tuple temp_tuple;
 		TupleGenotype temp_geno;

 		for (int i = 0; i < NUMBER_OF_AGENTS; i++) {
 			ArrayList<Tuple> tuples = new ArrayList<>();

 			// Each agent has 4 tuples (length 2-6)
 			for (int j = 0; j < 4; j++) {
 	 			int randomLength = random.nextInt(2, 6);
 	 			double [] lut = new double[(int) Math.pow(15, randomLength)];

 	 			temp_geno = new TupleGenotype(randomLength);
 	 			temp_tuple = new Tuple(lut, temp_geno.buildTupleCells());
 	 			temp_tuple.setGenoType(temp_geno);
 	 			tuples.add(temp_tuple);
 			}
 			
 			agents.add(new GeneticAgent(tuples));
 		}
	}

	/**
	 *
	 * @param id
	 */
	public static void report(int id) {
		done.add(id);

		if (done.size() == NUMBER_OF_AGENTS) {
			done.clear();

			Thread temp = new Thread(() -> {
				evaluations += 1;

				System.out.println();
				System.out.println("Running evaluation: " + evaluations);
				System.out.println();

				AgentEvaluation.id = 0;
				AgentEvaluation.main(new String[0]);

				if (evaluations == overallEvaluations) {
					System.out.println("End");

				} else if (evaluations % 2 == 0) {
					System.out.println("Breeding process");

					// Collect all tuples into one list
					ArrayList<Tuple> tuples = new ArrayList<>();
					for (GeneticAgent g : agents) {
						for (Tuple t : g.tuples) {
							tuples.add(t);
						}
					}
					agents.clear();

					ArrayList<Tuple> newgen = TupleGenotype.new_generation(tuples); // Removes tuple from tuples array list
					TupleGenotype.shuffle(newgen, new RandomDataGenerator(new MersenneTwister()), agents);
					startAgents();
				} else {
					createAgents();
					startAgents();
				}
			});
		
			temp.start();
		}
	}

	/**
	 *
	 */
	private static void startAgents() {
 		for (GeneticAgent g : agents) {
 			new Thread(g).start();
 		}
	}

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		//Initial Population
		createNewAgents();
		startAgents();
		
		/*
		createAgents();
		ArrayList<Tuple> tuples = new ArrayList<Tuple>();
		
		for(GeneticAgent g :agents) {
			for (Tuple t :g.tuples) {
				tuples.add(t);
			}
		}
		//System.out.println(tuples.size());
		
		ArrayList<Tuple> newgen = TupleGenotype.new_generation(tuples);
		//newgen.get(newgen.size()-2).genotype.print();
		TupleGenotype.shuffle(newgen, new RandomDataGenerator(new MersenneTwister()));
		*/
 	}
}
