package nic;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

public class GenoTypeScore {
    public TupleGenotype genotype;
    public double score;
    public int roundsPlayed;

    /**
     * Create new random instance between the min and max size
     * @param minLen - Minimum tuple size
     * @param maxLen - Maximum tuple size
     */
    GenoTypeScore(int minLen, int maxLen) {
        RandomDataGenerator rand = new RandomDataGenerator(new MersenneTwister());
        int size = rand.nextInt(minLen, maxLen);
        this.genotype = new TupleGenotype(size);

        this.score = 0;
        this.roundsPlayed = 0;
    }

    /**
     * Create new instance
     * @param genoTypeScore - Duplicate a GenoTypeScore
     */
    GenoTypeScore(GenoTypeScore genoTypeScore){
        this.genotype = genoTypeScore.genotype;
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
