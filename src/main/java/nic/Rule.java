package nic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.util.RandomUtils;
import put.game2048.Board;

public class Rule {
	public int[][] pattern;
	public double[] scoreChange;
	
	private int width, height;
	
	public Rule(int[][] pattern, double[] scoreChange) {
		this.pattern = pattern;
		this.scoreChange = scoreChange;
		height = pattern.length;
		width  = pattern[0].length;
	}
	
	public static Rule randomRule(int size, int numScores, RandomDataGenerator random) {
		int[][] pattern = new int[size][size];
		double[] scoreChange = new double[numScores]; 
		
		for(int y = 0; y < size; y++) {
			for(int x = 0; x < size; x++) {
				pattern[x][y] = random.nextInt(-1, Board.TILE_VALUES.length);
			}
		}
		
		for(int i = 0; i < numScores; i++) {
			scoreChange[i] = random.nextGaussian(0, 1);
		}
		
		return new Rule(pattern, scoreChange);
	}
	
	public void evaluate(Board board, double[] scores) {
		int[][] boardState = board.get();
		for(int row = 0; row < Board.SIZE - height; row++) {
			for(int col = 0; col < Board.SIZE - width; col++) {
				// Inner Loop
				boolean allmatch = true;
				for(int drow = 0; drow < height; drow++) {
					for(int dcol = 0; dcol < width; dcol++) {
						if(pattern[drow][dcol] == -1) {
							// Pattern doesn't care about here
							continue;
						} else if (boardState[row+drow][col+dcol] == pattern[drow][dcol]) {
							// Pattern matches here
							continue;
						} else {
							// Pattern violated
							allmatch = false;
							break;
						}
					}
					if(!allmatch)
						break;
				}
				if(allmatch) {
					for(int i = 0; i < scores.length; i++) {
						scores[i] += scoreChange[i];
					}
				}
			}
		}
	}
}
