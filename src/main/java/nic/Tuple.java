package nic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import put.ci.cevo.games.game2048.Game2048Board;
import put.ci.cevo.util.Pair;



public class Tuple implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -94781376176241568L;
	private Map<String, Double> tupleLookupTable;
	private ArrayList<Pair<Integer,Integer>> tupleCells;
	private String tupleKey; 
	private double lastValue;
	private String lastKey;
	private double current_value;
	Tuple(Map<String, Double> lookup,ArrayList<Pair<Integer,Integer>> tuple_cells){
		this.tupleLookupTable = lookup;
		this.tupleCells = tuple_cells;
		lastKey="";
		lastValue=0;
	}
	
	public double evaluateBoard(int[][] boardState,boolean update) {
		//Each tuple keeps its current key-value and previous key-value of the best action.
		//if update is true then this key-value pair is updated. If false then it only evaluates the tuple without storing them.
		if (update) {
			//System.out.println("Last value - " + String.valueOf(current_value));
			//Current key-value pair is referred as 'Correct' and last key-value is the estimate
			this.lastValue = this.current_value;
			this.lastKey = this.tupleKey;
			//System.out.println("Updated last "+lastKey);
			this.tupleKey = "";

			for (Pair<Integer,Integer> cellPosition:tupleCells) {
				this.tupleKey =this.tupleKey.concat(String.valueOf(boardState[cellPosition.first()][cellPosition.second()]));
			}
			//System.out.println("New current "+tupleKey );
			if(this.tupleLookupTable.get(this.tupleKey)==null) {
				//System.out.println("Haven't seen this tuple");
				this.current_value= 0; 
			}
			else {
				this.current_value = this.tupleLookupTable.get(this.tupleKey);
			}
			//System.out.println("Current value - "+String.valueOf(current_value));

			return this.current_value;
		}
		else {
			String temp_key = "";
			
			double temp_value= 0;
			for (Pair<Integer,Integer> cellPosition:tupleCells) {
				temp_key = temp_key.concat(String.valueOf(boardState[cellPosition.first()][cellPosition.second()]));
			}
			if(tupleLookupTable.get(temp_key)==null) {
				temp_value= 0; 
			}
			else {
				temp_value = tupleLookupTable.get(temp_key);
			}
			return temp_value;
		}
	}
	public void update(double reward,double learning_rate) {		
		this.tupleLookupTable.put(this.lastKey,this.lastValue+learning_rate*(this.current_value+reward-this.lastValue));
	}
	public Map<String, Double> getHashTable(){
		return tupleLookupTable;
	}
	public String getTupleKey() {
		return tupleKey;
	}
	public String getLastkey() {
		return lastKey;
	}
	public double getLastValue() {
		return lastValue;
	}
	public double getTupleValue() {

		return current_value;
	}
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
