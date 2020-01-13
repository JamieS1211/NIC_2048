package nic;

import java.io.IOException;

public class CustomThread implements Runnable {
    Thread thread;
    GenoTypeScore[] genoTypeScores;

    boolean done = false;

    CustomThread(GenoTypeScore[] genoTypeScores) {
        this.thread = new Thread(this);
        this.genoTypeScores = genoTypeScores;

        this.thread.start();
    }

    public void run() {
        TupleGenotype[] genotypes = new TupleGenotype[this.genoTypeScores.length];

        for (int i = 0; i < genotypes.length; i++) {
            genotypes[i] = this.genoTypeScores[i].genotype;
        }

        GeneticAgent agent = new GeneticAgent(genotypes);

        agent.learnAgent(500, 0.0025);

        // TODO - Test this agent and find its score

        this.done = true;
    }

    public boolean isDone() {
        return this.done;
    }
}