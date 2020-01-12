package nic;

import java.io.Serializable;
import java.util.ArrayList;
import put.ci.cevo.util.Pair;


public class Tuple implements Serializable {
	private static final long serialVersionUID = -94781376176241568L;
	private double[] tupleLookupTable;
	private ArrayList<Pair<Integer, Integer>> tupleCells;
	private ArrayList<Integer> tupleKeys; 
	private double current_value;
	private ArrayList<Double> scores = new ArrayList<>();
	/**
	 *
	 * @param lookup
	 * @param tuple_cells
	 */
	Tuple(double[] lookup, ArrayList<Pair<Integer, Integer>> tuple_cells) {
		this.tupleLookupTable =lookup;
		this.tupleCells = tuple_cells;

		tupleKeys = new ArrayList<>();
		current_value = 0;
	}

	/**
	 *
	 * @param boardState
	 * @return
	 */
	public void addScore(double score) {
		scores.add(score);
	}
	public double evaluateScores() {
		double sum =0;
		for (Double score :scores) {
			sum+=score;
		}
		return (sum/=scores.size());
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
     * use update=true only if you are going to update the value for that action
	 * @param afterstate -
	 * @param update - whether to keep the value for later update
	 * @return
	 */
	public double evaluateBoard(int[][] afterstate, boolean update) {
		int key = findKey(afterstate);

		if (update) {
			this.tupleKeys.add(key);
			this.current_value = this.tupleLookupTable[this.tupleKeys.get(this.tupleKeys.size()-1)];
			return this.current_value;
		} else {
			return this.tupleLookupTable[key];
		}
	}

	/**
	 *
	 * @param afterstate
	 * @param update
	 * @return
	 */
	public double evaluateBoardReflection(int[][] afterstate, boolean update) {
		int key = afterstate[(-tupleCells.get(0).first() + 3) % 4][tupleCells.get(0).second()];
		int base15 = 15;

		for (int i = 1; i < tupleCells.size(); i++) {
			key += base15 * afterstate[(-tupleCells.get(i).first() + 3) % 4][tupleCells.get(i).second()];
			base15 *= 15;
		}

		if (update) {
			this.tupleKeys.add(key);
			this.current_value = this.tupleLookupTable[this.tupleKeys.get(this.tupleKeys.size() - 1)];
			return this.current_value;
		} else {
			return this.tupleLookupTable[key];
		}
	}

	/**
	 * updates the stored value by incrementing it by delta
	 * @param delta
	 */
	public void update(double delta) {
		double ndelta = delta / tupleKeys.size();
		synchronized (this) {
			for (Integer key : tupleKeys) {
				this.tupleLookupTable[key] += ndelta;
			}

			tupleKeys.clear();
		}
	}

	/**
	 *
	 * @return
	 */
	public double getTupleValue() {
		return current_value;
	}

	/**
	 * this method is not used yet but it was meant for comparing tuple shapes to possibly share the lookup table between tuples. Should be useful in tuple evaluation.
	 * @param another_tuple
	 * @return
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
