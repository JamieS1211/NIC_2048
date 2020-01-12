package nic;

import put.ci.cevo.util.Pair;

import java.util.ArrayList;
import java.util.Random;

public class TupleGenotype {
    public Pair<Integer, Integer> startPosition;
    public int startDirection;
    public int[] turns;

    private int chrom_amount = 3;
    private int[] board_size = {4, 4};

    // Increasing index by 1 is a turn to left
    public Pair<Integer, Integer> UP = new Pair<>(0, -1);
    public Pair<Integer, Integer> LEFT = new Pair<>(-1, 0);
    public Pair<Integer, Integer> DOWN = new Pair<>(0, 1);
    public Pair<Integer, Integer> RIGHT = new Pair<>(1, 0);
    public ArrayList<Pair<Integer, Integer>> directions = new ArrayList<>();

    public int TURN_RIGHT = -1;
    public int TURN_STRAIGHT = 0;
    public int TURN_LEFT = 1;
    public int[] turnDirections = {TURN_RIGHT, TURN_STRAIGHT, TURN_LEFT};

    /**
     * Private constructor to add directions
     */
    private TupleGenotype() {
        this.directions.add(UP);
        this.directions.add(LEFT);
        this.directions.add(DOWN);
        this.directions.add(RIGHT);
    }

    /**
     * Create a new random tuple genotype of given size
     * @param tupleSize - Size of the tuple
     */
    TupleGenotype(int tupleSize) {
        this();

        Random rand = new Random();

        this.startPosition = new Pair<>(rand.nextInt(3), rand.nextInt(3));
        this.startDirection = rand.nextInt(directions.size());
        this.turns = new int[tupleSize - 1];

        boolean valid = false;

        do {
            for (int i = 0; i < tupleSize - 1; i++) {
                this.turns[i] = rand.nextInt(turnDirections.length) - 1;
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
        this();

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
        this();
 
        Random rand = new Random();
        TupleGenotype[] parents = {parent1, parent2};

        int choose = rand.nextInt(1);
        this.startPosition = parents[choose].startPosition;

        choose = rand.nextInt(1);
        this.startDirection = parents[choose].startDirection;
           
        boolean valid = false;
        int cTurn;
        while (!valid) {
        	this.turns = new int[parents[choose].turns.length];
            for (int i = 0; i < this.turns.length; i++) {
                choose = rand.nextInt(1);
                cTurn = rand.nextInt(parents[choose].turns.length);
                this.turns[i] = parents[choose].turns[cTurn];
            }
            
            valid = this.isTupleValid(this.buildTupleCells());
        }
        
    }

    /**
     * Mutate a tuple
     */
    void mutate() {
        Random rand = new Random();

        int mutation = rand.nextInt(this.chrom_amount);

        switch (mutation) {

            case 0:
                // Start Position
                int xstart = rand.nextInt(this.board_size[0] - 1);
                int ystart = rand.nextInt(this.board_size[1] - 1);
                this.startPosition = new Pair<>(xstart, ystart);

                return;

            case 1:
                // Start Direction
                int direction = rand.nextInt(this.directions.size());
                this.startDirection = direction;

                return;

            case 2:
                // Turns
                int changing_turn = rand.nextInt(this.turnDirections.length);
                this.turns[changing_turn] = rand.nextInt(this.turnDirections.length);

                return;
        }
    }

    /**
     * Print the tuple info
     */
    void print() {
        System.out.println("Start Position: " + this.startPosition);
        System.out.println("Start direction: " + this.startDirection);

        for (int i = 0; i < this.turns.length; i++) {
            System.out.println("Turn" + i + " : " + this.turnDirections[i]);
        }

        System.out.println();
    }

    /**
     * Check if tuple is valid
     * @param tupleCells
     * @return
     */
    public boolean isTupleValid(ArrayList<Pair<Integer, Integer>> tupleCells) {
        // Check it doesn't go back on itself
        for (Pair<Integer, Integer> cell : tupleCells) {
            if (cell.first() < 0 || cell.first() >= 4 || cell.second() < 0 || cell.second() >= 4) {
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
            currentDirection = ((currentDirection + this.turns[turn]) + 4) % 4;
            currentPosition = new Pair<>(currentPosition.first() + directions.get(currentDirection).first(), currentPosition.second() + directions.get(currentDirection).second());
            tupleCells.add(currentPosition);
        }

        return tupleCells;
    }
}