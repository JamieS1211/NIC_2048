package nic;

public class CustomThread implements Runnable {
    private final Thread thread;
    private final GenoTypeScore[] genoTypeScores;

    private boolean done = false;
    double score = 0;

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

        this.score = AgentEvaluation.evaluateAgent(agent);
        this.done = true;
    }

    public boolean isDone() {
        return this.done;
    }
}