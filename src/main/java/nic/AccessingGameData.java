package nic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class AccessingGameData {
	
	public static void save_game_data(float[][]data, float[][]actions, String filename) throws FileNotFoundException, UnsupportedEncodingException {
		
		
		PrintWriter writer = new PrintWriter(filename, "UTF-8");
		
		if(data.length != actions.length) {
			throw new RuntimeException("actions and states must be of same length");
		}
		
		for (int i=0;i<data.length;i++) {
			writer.println("STATE");
			for (int j=0;j<data[i].length;j++) {
				writer.println(data[i][j]);
			}
			writer.println("ACTION");
			for (int j=0;j<actions[i].length;j++) {
				writer.println(actions[i][j]);
			}
		}
		writer.close();
		
		
	}
	
	public static Data get_game_data(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		BufferedReader ar = new BufferedReader(new FileReader(filename));
		String st;
		String at;
		boolean game_state = false;
		
		int data_amount = 0;
		
		while ((at = ar.readLine()) != null) {
			if(at.equals("STATE")) {
				data_amount += 1;
			}
			
			
		}
		
		float[][] states = new float[data_amount][16];
		float[][] actions = new float[data_amount][4];
		
		boolean g_state = false;
		
		int state_i = -1;
		int state_j = 0;
		int action_i = -1;
		int action_j = 0;
		
		while ((st = br.readLine()) != null) {
			
			if(st.equals("STATE")) {
				//System.out.println("game state");
				g_state = true;
				state_i +=1;
				continue;
			}
			
			else if(st.equals("ACTION")) {
				//System.out.println("action");
				g_state = false;
				action_i +=1;
				continue;
			}
			
			if(g_state) {
				float f = Float.parseFloat(st);
				
				if(state_j==16) {
					state_j = 0;
				}
				
				states[state_i][state_j] = f;
				state_j +=1;
				
			}
			
			else {
				float f = Float.parseFloat(st);
				
				if(action_j==4) {
					action_j = 0;
				}
				actions[action_i][action_j] = f;
				action_j +=1;
			}
		  
		 }
		br.close();
		
		Data d = new Data(states,actions);

		return d;
		
	}
	
	
	

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		float[][] g_state = {{0,2,2,0,0,0,0,4,8,16,0,2,4,4,0,0},{86,2,2,0,16,0,0,4,8,16,0,2,4,4,0,0}};
		float[][] action = {{0,0,1,0},{0,1,0,0}};
		//create_ttf(g_state, action);
		//get_ttf("src//test.txt");
		save_game_data(g_state,action,"src//main//java//nic//test.txt");
		Data data = get_game_data("src//main//java//nic//test.txt");
		
		float[][] states = Data.states;
		float[][] actions = Data.actions;
		
		for (int k=0;k<actions.length;k++) {
			System.out.println(Arrays.toString(states[k]));
			System.out.println(Arrays.toString(actions[k]));
		}

	}

}
