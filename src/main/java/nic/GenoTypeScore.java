package nic;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

public class GenoTypeScore {

    public int id;
    public TupleGenotype genotype;
    public double score;
    public int roundsPlayed;

    /**
     *
     * @param id
     * @param minLen
     * @param maxLen
     */
    GenoTypeScore(int id, int minLen, int maxLen) {
        RandomDataGenerator rand = new RandomDataGenerator(new MersenneTwister());
        int size = rand.nextInt(minLen, maxLen);
        this.genotype = new TupleGenotype(size);

        this.score = 0;
        this.roundsPlayed = 0;
    }

    /**
     *
     */
    GenoTypeScore(GenoTypeScore geno){
        this.genotype = geno.genotype;
        this.score = 0;
        this.roundsPlayed = 0;
    }

    /**
     * Increment the score of this genotype
     * @param score - Amount to increment the score
     */
    void incrementScore(double score){
        this.score += score;
    }

    /**
     * Increment the round count of this genotype
     * @param rounds - Amount of rounds to increment the game count by
     */
    void incrementRounds(int rounds){
        this.roundsPlayed += rounds;
    }


}
