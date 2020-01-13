package nic;

import java.util.ArrayList;
import java.util.Collections;

public class RunMultithreadedFullTraining {

    // Sort population
    public static ArrayList<GenoTypeScore> sortGenoTypeScoreList (ArrayList<GenoTypeScore> list) {

        ArrayList<Double>  scores = new ArrayList<>();
        ArrayList<GenoTypeScore> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++){
            GenoTypeScore item = list.get(i);
            if (item.roundsPlayed > 0) {
                scores.add(item.score / item.roundsPlayed);
            } else {
                scores.add(item.score);
            }
        }
        ArrayList<Double> sorted_scores = new ArrayList<>(scores);

        for (int j = 0; j < sorted_scores.size(); j++){
            newList.add(list.get(scores.indexOf(sorted_scores.get(j))));
        }

        return new ArrayList<>(newList);
    }


    public static void main(String[] args) throws InterruptedException {
        // Variable setup
        int agentCount = 10;
        int tuplesPerAgent = 4;
        int tupleMinLength = 2;
        int tupleMaxLength = 6;

        int totalRounds = 40;
        int roundsBetweenBreeding = 4;
        int discardedIndividualsDuringBreeding = 20;

        int tupleID;
        double mutationProbability = 0.3;


        // Generate initial population
        ArrayList<GenoTypeScore> population = new ArrayList<>();
        for (tupleID = 0; tupleID < agentCount * tuplesPerAgent; tupleID++) {
            population.add(new GenoTypeScore(tupleMinLength, tupleMaxLength));
        }

        // Each round tuples will be distributed between agents
        for (int round = 0; round < totalRounds; round++) {
            // Breed new tuples based on best of population
            if (round % roundsBetweenBreeding == 0 && round > 0) {
                System.out.println("");
                System.out.println("Breeding");
                System.out.println("");

                // Sort by (score / games plaid)
                population = sortGenoTypeScoreList(population);


                // Discard of worst few tuples
                int limit = population.size() - 1 - discardedIndividualsDuringBreeding;
                for (int i = population.size() - 1; i > limit; i--) {
                    population.remove(i);
                }

                // Breeding via tournament approach
                int top_cat = 5;
                GenoTypeScore parent1;
                GenoTypeScore parent2;

                for (int p = 0; p < discardedIndividualsDuringBreeding; p++){
                    ArrayList<GenoTypeScore> tournamentIndividuals = new ArrayList<>();
                    Collections.shuffle(population);

                    for (int t = 0; t < top_cat; t++) {
                        tournamentIndividuals.add(population.get(t));
                    }

                    parent1 = sortGenoTypeScoreList(tournamentIndividuals).get(0);
                    parent2 = sortGenoTypeScoreList(tournamentIndividuals).get(1);

                    if (Math.random() < mutationProbability){//mutation
                        GenoTypeScore child = new GenoTypeScore(parent1);
                        population.add(child);
                    } else {
                        GenoTypeScore child = new GenoTypeScore(parent2);
                        child.genotype = new TupleGenotype(parent1.genotype, parent2.genotype);
                        population.add(child);
                    }
                }
            }

            System.out.println("Evaluating");

            // Shuffle and sort population into groups of 4
            Collections.shuffle(population);


            // Create a thread from each group
            CustomThread[] threads = new CustomThread[agentCount];
            for (int thread = 0; thread < threads.length; thread++) {
                GenoTypeScore[] genotypes = {population.get((thread * 4) + 0), population.get((thread * 4) + 1), population.get((thread * 4) + 2), population.get((thread * 4) + 3)};
                threads[thread] = new CustomThread(genotypes);
            }


            // For each thread await done and update scores
            for (int thread = 0; thread < threads.length; thread++) {

                System.out.println("Start waiting: " + round);

                while (!threads[thread].isDone()) {
                    Thread.sleep(1000);
                    //Waiting for this thread to finish
                }

                // Get scores and update our genotype scores with thease values
                for (int tuple = 0; tuple < tuplesPerAgent; tuple++) {
                    population.get((thread * 4) + tuple).incrementRounds(1);
                    population.get((thread * 4) + tuple).incrementScore(threads[thread].score);
                }

                System.out.println("End waiting: " + round);
            }
        }

        System.out.println("Building agent with best tuples");

        population = sortGenoTypeScoreList(population);

        // Take top 4 values and build final agent (this is agent from the tuples we believe are best)
        TupleGenotype[] bestTupleGenotypes = {population.get(0).genotype, population.get(1).genotype, population.get(2).genotype, population.get(3).genotype};
        GeneticAgent bestTupleAgent = new GeneticAgent(bestTupleGenotypes);

        // Train final agent for a long period of time
        for (int i = 0; i < 100; i++) {
            bestTupleAgent.learnAgent(2000, 0.0025);
            System.out.println("Saving");
            bestTupleAgent.storeTuples();
        }
    }
}
