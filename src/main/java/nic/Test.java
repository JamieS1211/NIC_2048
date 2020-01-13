package nic;

import java.util.ArrayList;
import java.util.Collections;

public class Test {

    // Sort population
    public static ArrayList<GenoTypeScore> sortGenoTypeScoreList (ArrayList<GenoTypeScore> list) {

        ArrayList<Integer>  scores = new ArrayList<>();
        ArrayList<GenoTypeScore> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++){
            GenoTypeScore item = list.get(i);
            if (item.roundsPlayed > 0) {
                scores.add(item.score / item.roundsPlayed);
            } else {
                scores.add(item.score);
            }
        }
        ArrayList<Integer> sorted_scores = new ArrayList<>(scores);

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

        int totalRounds = 50;
        int roundsBetweenBreeding = 5;
        int discardedIndividualsDuringBreeding = 10;

        int tupleID;


        // Generate initial population
        ArrayList<GenoTypeScore> population = new ArrayList<>();
        for (tupleID = 0; tupleID < agentCount * tuplesPerAgent; tupleID++) {
            population.add(new GenoTypeScore(tupleID, tupleMinLength, tupleMaxLength));
        }

        for (int round = 0; round < totalRounds; round++) {
            if (round % roundsBetweenBreeding == 0 && roundsBetweenBreeding > 0) {
                System.out.println("");
                System.out.println("Breeding");
                System.out.println("");

                // Sort by score / games plaid
                population = sortGenoTypeScoreList(population);


                // Discard of worst few tuples
                int limit = population.size() - 1 - discardedIndividualsDuringBreeding;
                for (int i = population.size() - 1; i > limit; i--) {
                    population.remove(i);
                }

                // Generate X new from Y remaining
                for (int i = 0; i < discardedIndividualsDuringBreeding; i++) {
                    population.add(new GenoTypeScore(tupleID, 2, 6));
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

                System.out.println("Start waiting");

                while (!threads[thread].isDone()) {
                    Thread.sleep(1000);
                    //TODO - Thread can't yet run the agent
                    //Waiting for this thread to finish
                }

                // Get scores and update our genotype scores with thease values
                for (int tuple = 0; tuple < tuplesPerAgent; tuple++) {
                    population.get((thread * 4) + tuple).incrementRounds(1);
                    population.get((thread * 4) + tuple).incrementScore(1); // TODO - Real value here
                }

                System.out.println("End waiting");

            }
        }

        population = sortGenoTypeScoreList(population);

        // Take top 4 values and build final agent
        TupleGenotype[] bestTupleGenotypes = {population.get(0).genotype, population.get(1).genotype, population.get(2).genotype, population.get(3).genotype};
        GeneticAgent bestTupleAgent = new GeneticAgent(bestTupleGenotypes);

        // Train final agent for X
        // TODO - Train best tuple agent and save results
    }
}
