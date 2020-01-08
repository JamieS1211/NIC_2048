package nic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import put.ci.cevo.games.game2048.Game2048Board;
import put.ci.cevo.util.Pair;



public class Tuple implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -94781376176241568L;
	private double[] tupleLookupTable;
	private ArrayList<Pair<Integer,Integer>> tupleCells; 
	private int tupleKey; 
	private double current_value;
	Tuple(double[] lookup,ArrayList<Pair<Integer,Integer>> tuple_cells){
		this.tupleLookupTable =lookup;
		this.tupleCells = tuple_cells;

		tupleKey = 0;
		current_value=0;
	} 
	/**
	 * Each tuple keeps its current key used for last action evaluation.
     * use update=true only if you are going to update the value for that action
	 * @param afterstate
	 * @param update - whether to keep the value for later update
	 * @return
	 */
	public double evaluateBoard(int[][] afterstate,boolean update) {

		int key = 0;
		int base15 = 1;
		
		for (Pair<Integer,Integer> cellPosition:tupleCells) {
			key += base15*afterstate[cellPosition.first()][cellPosition.second()];
			base15*=15;
		}
		if (update) {
			this.tupleKey=key;
			this.current_value = this.tupleLookupTable[this.tupleKey];
			return this.current_value;
		}
		else {
			return this.tupleLookupTable[key];
		}
	}
	/**
	 * updates the stored value by incrementing it by delta
	 * @param delta
	 */
	public void update(double delta) {
		this.tupleLookupTable[tupleKey]+=delta;
	}
	public double getTupleValue() {

		return current_value;
	}
	/**
	 * this method is not used yet but it was meant for comparing tuple shapes to possibly share the lookup table between tuples. Should be useful in tuple evaluation.
	 * @param another_tuple
	 * @return
	 */
	public boolean compareShapes(ArrayList<Pair<Integer,Integer>> another_tuple) {
		if(another_tuple.size() !=tupleCells.size()) {
			return false;
		}
		Pair <Integer,Integer> difference;
		difference= new Pair<Integer,Integer>(another_tuple.get(0).first()-tupleCells.get(0).first(),another_tuple.get(0).second()-tupleCells.get(0).second());
		for (int i = 1;i<another_tuple.size();i++) {
			if(!(difference.first()==another_tuple.get(i).first()-tupleCells.get(i).first() && difference.second()==another_tuple.get(i).second()-tupleCells.get(i).second())) {
				return false;
			}
		}
		return true;
	}
}
