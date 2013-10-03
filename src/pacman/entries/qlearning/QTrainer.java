package pacman.entries.qlearning;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.EnumMap;
import java.util.HashMap;

import pacman.controllers.Controller;
import pacman.controllers.examples.Legacy;
import pacman.game.Game;
import pacman.game.GameView;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class QTrainer {
	
	public HashMap<QState, Integer> states;
	public float learningRate = 0.2f;
	
	public static void main(String[] args)
	{
		//while(true)
			new QTrainer().run(2500, 1.0f, true);
		//new QTrainer().run(10, 0.9f, true);
	}
	
	public QTrainer(){
		super();
		loadStates("states.dat");
	}
	
	public void run(int runs, float exploit, boolean visual){
		
		QLearner learner = new QLearner(states);
		
		for(int i = 0; i < runs; i++)  {
			
			int score = runExperiment(new Legacy(), learner, exploit, visual);
			
			System.out.println("Run: " + i + "\tscore: " + score + "\texploit: " + exploit + "\tstates: " + states.size());
			
		}
		
		persist("states.dat");
		
	}
	
	private void persist(String filename) {
		
		// Output
		String out = "";
		
		for(QState state : states.keySet()){
			
			out += state.getLevel() + ";";
			out += state.getJunction() + ";";
			
			out += state.getDistanceUp() + ";";
			out += state.isEdibleUp() + ";";
			
			out += state.getDistanceRight() + ";";
			out += state.isEdibleRight() + ";";
			
			out += state.getDistanceDown() + ";";
			out += state.isEdibleDown() + ";";
			
			out += state.getDistanceLeft() + ";";
			out += state.isEdibleLeft() + ";";
			
			out += state.isFirstPP() + ";";
			out += state.isSecondPP() + ";";
			out += state.isThirdPP() + ";";
			out += state.isFourthPP() + ";";
			
			out += states.get(state) + "\n";
			
		}	
		
		// Write to file
        FileWriter fw = null;
		try {
			File old = new File(filename);
			if (old.exists()){
				old.delete();
				System.out.println(filename + " deleted");
			}
			File file = new File(filename);
			fw = new FileWriter(file);
			fw.write(out);
			fw.close();
			System.out.println(filename + " saved");
		} catch (FileNotFoundException e1) {
			System.out.println("Error saving " + filename + ". " + e1);
		} catch (IOException e2) {
			System.out.println("Error saving " + filename + ". " + e2);
		}
		
	}
	
	private void loadStates(String filename){
		
		states = new HashMap<QState, Integer>();
		
		try {

            FileInputStream fstream = new FileInputStream(filename);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;

            while ((strLine = br.readLine()) != null) {
                
            	String[] chunks = strLine.split(";");
            	
            	int level = Integer.parseInt(chunks[0]);
            	int junction = Integer.parseInt(chunks[1]);
            	int distanceUp = Integer.parseInt(chunks[2]);
            	boolean edibleUp = Boolean.parseBoolean(chunks[3]);
            	int distanceDown = Integer.parseInt(chunks[4]);
            	boolean edibleDown = Boolean.parseBoolean(chunks[5]);
            	int distanceRight = Integer.parseInt(chunks[6]);
            	boolean edibleRight = Boolean.parseBoolean(chunks[7]);
            	int distanceLeft = Integer.parseInt(chunks[8]);
            	boolean edibleLeft = Boolean.parseBoolean(chunks[9]);
            	int value = Integer.parseInt(chunks[14]);
            	
            	boolean firstPP = Boolean.parseBoolean(chunks[10]);
            	boolean secondPP = Boolean.parseBoolean(chunks[11]);
            	boolean thirdPP = Boolean.parseBoolean(chunks[12]);
            	boolean fourthPP = Boolean.parseBoolean(chunks[13]);
            	
            	QState state = new QState(	level, 
            								junction, 
            								distanceUp, 
            								edibleUp, 
            								distanceDown, 
            								edibleDown, 
            								distanceRight, 
            								edibleRight, 
            								distanceLeft, 
            								edibleLeft, 
            								firstPP,
            								secondPP,
            								thirdPP,
            								fourthPP);
            	
            	states.put(state, value);
            	
            }

            in.close();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
		
	}

	public int runExperiment(Controller<EnumMap<GHOST,MOVE>> ghostController, QLearner learner, float exploit, boolean visual) {

		Game game=new Game(0);

		GameView gv=null;
		
		int delay = 0;
		if(visual){
			gv=new GameView(game).showGame();
			delay = 8;
		}
		
		while(!game.gameOver())
		{
	        game.advanceGame(learner.getMove(game.copy(),exploit),ghostController.getMove(game.copy(),-1));
	        
	        if(visual){
	        	gv.repaint();
	        
		        try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	        }
	        
		}
		
		for(QState state : learner.visited){
			if(states.containsKey(state))
				updateStateValue(state, game.getScore());
			else
				states.put(state, game.getScore());
		}
		
		return game.getScore();
	}

	private void updateStateValue(QState state, int score) {
		
		int prevValue = states.get(state);
		int newValue = (int)(prevValue + learningRate * (score - prevValue));
		states.put(state, newValue);
		
	}
	
	
	
}
