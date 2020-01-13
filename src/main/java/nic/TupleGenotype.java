package nic;

import put.ci.cevo.util.Pair;

import java.util.ArrayList;
import java.util.Random;

public class TupleGenotype {
    public Pair<Integer, Integer> startPosition;
    public int startDirection;
    public int[] turns;

    /**
     * Create a new random tuple genotype of given size
     * @param tupleSize - Size of the tuple
     */
    TupleGenotype(int tupleSize) {
        Random rand = new Random();

        this.startPosition = new Pair<>(rand.nextInt(3), rand.nextInt(3));
        this.startDirection = rand.nextInt(4);
        this.turns = new int[tupleSize - 1];

        boolean valid;

        do {
            for (int i = 0; i < tupleSize - 1; i++) {
                this.turns[i] = rand.nextInt(3) - 1;
            }

            valid = isTupleValid(this.buildTupleCells());
        } while (!valid);
    }

    /**
     * Create tuple genotype from genetic data
     * @param startPosition - Start cell of tuple
     * @param startDirection - Start direction to draw
     * @param turns - List of turns to make
     */
    TupleGenotype(Pair<Integer, Integer> startPosition, int startDirection, int[] turns) {
        this.startPosition = startPosition;
        this.startDirection = startDirection;
        this.turns = turns;
    }

    /**
     * Create a new tuple genotype from two parents
     * @param parent1 - Parent 1
     * @param parent2 - Parent 2
     */
    TupleGenotype(TupleGenotype parent1, TupleGenotype parent2) throws IllegalArgumentException {
        if (parent1.turns.length != parent2.turns.length) {
            throw new IllegalArgumentException();
        }

        Random rand = new Random();
        TupleGenotype[] parents = {parent1, parent2};

        int choose = rand.nextInt(1);
        this.startPosition = parents[choose].startPosition;

        choose = rand.nextInt(1);
        this.startDirection = parents[choose].startDirection;

        // Turns
        this.turns = new int[parent1.turns.length];
        for (int i = 0; i < this.turns.length; i++) {
            choose = rand.nextInt(1);
            this.turns[i] = parents[choose].turns[i];
        }
    }

    /**
     * Mutate a tuple
     */
    void mutate() {
        Random rand = new Random();

        int mutation = rand.nextInt(3);

        switch (mutation) {

            case 0:
                // Start Position
                this.startPosition = new Pair<>(rand.nextInt(3), rand.nextInt(3));
                return;

            case 1:
                // Start Direction
                this.startDirection = rand.nextInt(4);

                return;

            case 2:
                // Turns
                this.turns[rand.nextInt(3)] = rand.nextInt(3) - 1;
                return;
        }
    }

    /**
     * Check if tuple is valid
     * @param tupleCells
     * @return
     */
    public boolean isTupleValid(ArrayList<Pair<Integer, Integer>> tupleCells) {
        // Check it doesn't go back on itself
        for (Pair<Integer, Integer> cell : tupleCells) {
            if (cell.first() < 0 || cell.first() > 3 || cell.second() < 0 || cell.second() > 3) {
                return false;
            }
        }

        for (int i = 0; i < tupleCells.size(); i++) {
            for (int j = 0; j < tupleCells.size(); j++) {
                if (i == j) {
                    continue;
                }

                Pair<Integer, Integer> t1 = tupleCells.get(i);
                Pair<Integer, Integer> t2 = tupleCells.get(j);

                if (t1.first() == t2.first()) {
                    if (t1.second() == t2.second()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Build the Tuple from its genotype
     * @return - Tuple generated from genotype
     */
    public ArrayList<Pair<Integer, Integer>> buildTupleCells() {
        ArrayList<Pair<Integer, Integer>> tupleCells = new ArrayList<>();

        Pair<Integer, Integer> currentPosition = new Pair<>(this.startPosition.first(), this.startPosition.second());
        int currentDirection = this.startDirection;

        tupleCells.add(currentPosition);

        for (int turn = 0; turn < this.turns.length; turn++) {
            currentDirection = (currentDirection + this.turns[turn] + 4) % 4;

            currentPosition = new Pair<>(currentPosition.first() + EnumAbsoluteDirections.getDirectionData(currentDirection).xDiff, currentPosition.second() + EnumAbsoluteDirections.getDirectionData(currentDirection).yDiff);
            tupleCells.add(currentPosition);
        }

        return tupleCells;
    }
}