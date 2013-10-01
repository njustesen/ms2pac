package pacman.entries.qlearning;

import java.util.EnumMap;
import java.util.HashMap;

import pacman.Executor;
import pacman.controllers.Controller;
import pacman.controllers.examples.Legacy;
import pacman.entries.mcts.MCTS;
import pacman.entries.mcts.MctsNode;
import pacman.entries.mcts.RandomJunctionPacman;
import pacman.game.Game;
import pacman.game.GameView;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class QTrainer {
	
	public HashMap<QState, Integer> states;
	public float learningRate;
	
	public static void main(String[] args)
	{
		new QTrainer().run(10000, 0.75f, false);
		new QTrainer().run(10, 0f, true);
	}
	
	public QTrainer(){
		super();
		this.states = new HashMap<QState, Integer>();
	}
	
	public void run(int runs, float exploit, boolean visual){
		
		for(int i = 0; i < runs; i++)  {
			
			int score = runExperiment(new Legacy(), exploit, visual);
			
			System.out.println("Run: " + i + ", score: " + score + ", states: " + states.size());
			
		}
		
		
	}
	
	public int runExperiment(Controller<EnumMap<GHOST,MOVE>> ghostController, float exploit, boolean visual) {

		QLearner learner = new QLearner(states);
    	
		Game game=new Game(0);

		GameView gv=null;
		
		if(visual)
			gv=new GameView(game).showGame();
		
		while(!game.gameOver())
		{
	        game.advanceGame(learner.getMove(game.copy(),exploit),ghostController.getMove(game.copy(),-1));
	        
	        if(visual)
	        	gv.repaint();
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
