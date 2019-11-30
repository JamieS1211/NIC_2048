package nic;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

import com.google.common.base.Preconditions;

import put.game2048.Action;
import put.game2048.Agent;
import put.game2048.Board;

public class GeneticAgent implements Agent {
	public RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
	public static Action[] ACTIONS = { Action.UP, Action.RIGHT, Action.DOWN, Action.LEFT };
	
	public List<Rule> rules;
	
	public GeneticAgent() {
		this.rules = new ArrayList<Rule>();
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream("ruleset.bin");
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			this.rules = (ArrayList<Rule>) objectInputStream.readObject();
			objectInputStream.close(); 
		} catch (IOException | ClassNotFoundException e) {
			this.rules = new ArrayList<Rule>();
			e.printStackTrace();
		}
	}

	public GeneticAgent(ArrayList<Rule> rules) {
		this.rules = rules;
	}

	public Action chooseAction(Board board, List<Action> possibleActions, Duration maxTime) {
		Preconditions.checkArgument(0 < possibleActions.size());

		double[] scores = new double[4];
		
		for(Rule rule : rules) {
			rule.evaluate(board, scores);
		}
		
		boolean nothingyet = true;
		double maxScore = 0;
		int argmax = 0;
		for(int i = 1; i < scores.length; i++) {
			if(possibleActions.contains(ACTIONS[i])) {
				if(nothingyet || scores[i] > maxScore) {
					maxScore = scores[i];
					argmax = i;
					nothingyet = false;
				}
			}
		}
		
		return ACTIONS[argmax];
	}
}
