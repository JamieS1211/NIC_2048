package nic;

import put.ci.cevo.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class TupleGenotype {
    public Pair<Integer, Integer> startPosition;
    public int startDirection;
    public int[] turns = new int[3];

    // Increasing index by 1 is a turn to left
    public Pair<Integer, Integer> UP = new Pair<>(0, 1);
    public Pair<Integer, Integer> LEFT = new Pair<>(-1, 0);
    public Pair<Integer, Integer> DOWN = new Pair<>(0, -1);
    public Pair<Integer, Integer> RIGHT = new Pair<>(1, 0);
    public ArrayList<Pair<Integer, Integer>> directions = new ArrayList<>();

    public int TURN_RIGHT = -1;
    public int TURN_STRAIGHT = 0;
    public int TURN_LEFT = 1;
    public int[] turnDirections = {TURN_RIGHT, TURN_STRAIGHT, TURN_LEFT};

    /**
     * Create a new random tuple genotype
     */
    TupleGenotype() {
        Random rand = new Random();

        this.directions.add(UP);
        this.directions.add(LEFT);
        this.directions.add(DOWN);
        this.directions.add(RIGHT);

        this.startPosition = new Pair<>(rand.nextInt(3), rand.nextInt(3));
        this.startDirection = rand.nextInt(directions.size());
        this.turns[0] = rand.nextInt(turnDirections.length);
        this.turns[1] = rand.nextInt(turnDirections.length);
        this.turns[2] = rand.nextInt(turnDirections.length);
    }

    /**
     * Create a new tuple genotype from two parents
     * @param parent1
     * @param parent2
     */
    TupleGenotype(TupleGenotype parent1, TupleGenotype parent2) {

    }

    public boolean isTupleValid(Pair<Integer, Integer> tuple) {
        // Check it doesn't go back on itself

        // Check all are inside board

        return true;
    }

    public ArrayList<Pair<Integer, Integer>> buildTupleCells() {
        ArrayList<Pair<Integer, Integer>> tupleCells = new ArrayList<>();

        Pair<Integer, Integer> currentPosition = new Pair<>(this.startPosition.first(), this.startPosition.second());
        int currentDirection = this.startDirection;

        tupleCells.add(currentPosition);

        for (int turn = 0; turn < this.turns.length; turn++) {
            currentDirection = (currentDirection + this.turns[turn]) % 4;
            currentPosition = new Pair<>(currentPosition.first() + directions.get(currentDirection).first(), currentPosition.second() + directions.get(currentDirection).second());
            tupleCells.add(currentPosition);
        }

        return tupleCells;
    }
}
