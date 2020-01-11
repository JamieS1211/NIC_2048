package nic;

import java.io.Serializable;
import java.util.ArrayList;

import put.ci.cevo.util.Pair;

public class Tuple implements Serializable {

	private static final long serialVersionUID = -94781376176241568L;
	private final double[] tupleLookupTable;
	private final ArrayList<Pair<Integer, Integer>> tupleCells;
	private int tupleKey; 
	private double currentValue;

	/**
	 * Constructor
	 * @param tupleLookupTable - Lookup table
	 * @param tupleCells - Cells of tuple
	 */
	Tuple(double[] tupleLookupTable, ArrayList<Pair<Integer, Integer>> tupleCells){
		this.tupleLookupTable = tupleLookupTable;
		this.tupleCells = tupleCells;

		tupleKey = 0;
		currentValue = 0;
	}

	public int findKey(int[][] boardState) {
		int key = boardState[tupleCells.get(0).first()][tupleCells.get(0).second()];
		int base15 = 15;

		for (int i = 1; i < tupleCells.size(); i++) {
			key += base15 * boardState[tupleCells.get(i).first()][tupleCells.get(i).second()];
			base15 *= 15;
		}

		return key;
	}

	/**
	 * Each tuple keeps its current key used for last action evaluation.
     * use update = true only if you are going to update the value for that action
	 * @param boardState - Board state after change
	 * @param update - Keep the value for later update?
	 * @return Value of tuple for given board
	 */
	public double evaluateBoard(int[][] boardState, boolean update) {
		int key = findKey(boardState);

		if (update) {
			this.tupleKey = key;
			this.currentValue = this.tupleLookupTable[this.tupleKey];
			return this.currentValue;
		} else {
			return this.tupleLookupTable[key];
		}
	}

	/**
	 * Updates the most recent value by incrementing it by delta
	 * @param delta - Value to increment tuple by
	 */
	public void update(double delta) {
		this.tupleLookupTable[tupleKey] += delta;
	}

	/**
	 * Return value of tuple
	 * @return - Current value of this tuple
	 */
	public double getTupleValue() {
		return currentValue;
	}

	/**
	 * Currently unused. Should be useful in tuple evaluation
	 * Compares tuple shapes (regardless of position) to possibly share the lookup table between tuples
	 * @param another_tuple - Tuple to compare to
	 * @return - Are tuples same shape
	 */
	public boolean compareShapes(ArrayList<Pair<Integer, Integer>> another_tuple) {
		if (another_tuple.size() != tupleCells.size()) {
			return false;
		}

		Pair <Integer, Integer> difference;
		difference = new Pair<>(another_tuple.get(0).first() - tupleCells.get(0).first(), another_tuple.get(0).second() - tupleCells.get(0).second());

		for (int i = 1; i < another_tuple.size(); i++) {
			if (difference.first() != another_tuple.get(i).first() - tupleCells.get(i).first() || difference.second() != another_tuple.get(i).second() - tupleCells.get(i).second()) {
				return false;
			}
		}

		return true;
	}
}
