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
	private TupleGenotype genotype;

	/**
	 *
	 * @param lookup
	 * @param genotype
	 */
	Tuple(double[] lookup, TupleGenotype genotype) {
		this.tupleLookupTable = lookup;
		this.genotype = genotype;
		this.tupleCells = genotype.buildTupleCells();

		tupleKeys = new ArrayList<>();
		current_value = 0;
	}

	/**
	 *
	 * @param boardState
	 * @return
	 */
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
}
