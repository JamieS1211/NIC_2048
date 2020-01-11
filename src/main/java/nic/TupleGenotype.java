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
    
    
      /**
     * Create a new tuple genotype from two parents
     * @param parent1
     * @param parent2
     */
    TupleGenotype(TupleGenotype parent1, TupleGenotype parent2) {
    	
    	this.directions.add(UP);
        this.directions.add(LEFT);
        this.directions.add(DOWN);
        this.directions.add(RIGHT);
    	
    	Random rand = new Random();
    	TupleGenotype[] parents = new TupleGenotype[] {parent1, parent2};
    	int choose = rand.nextInt(1);
    	
  
    	this.startPosition = parents[choose].startPosition; 
    	
    	choose = rand.nextInt(1);
    	
    	this.startDirection = parents[choose].startDirection;
    	
    	//Turns
    	for (int i=0;i<this.turns.length;i++) {
    		choose = rand.nextInt(1);
    		this.turns[i] = parents[choose].turns[i];
    	}
    }
    void mutate() {
    	Random rand = new Random();
    	
    	int mutation = rand.nextInt(this.chrom_amount);
    	
    	
    	switch(mutation) {
    	
    	case 0:
    		//Start Position
    		int xstart = rand.nextInt(this.board_size[0]-1);
    		int ystart = rand.nextInt(this.board_size[1]-1);
    		this.startPosition = new Pair<>(xstart,ystart);
    		
    		return;
    	
    	case 1:
    		//Start Direction
    		int direction = rand.nextInt(this.directions.size());
    		this.startDirection = direction;
    		
    		return;
    	
    	case 2:
    		//turns
    		int changing_turn = rand.nextInt(this.turnDirections.length);
    		this.turns[changing_turn] = rand.nextInt(this.turnDirections.length);
    		
    		return;
    	}
    	
    	
    }
    	
    void print() {
    	System.out.println("Start Position: "+this.startPosition);
    	System.out.println("Start direction: "+this.startDirection);
    	
    	for (int i=0; i<this.turns.length;i++) {
    		System.out.println("Turn"+i+" : "+this.turnDirections[i]);
    	}
    	System.out.println();
    	
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
