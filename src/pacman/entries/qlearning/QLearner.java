package pacman.entries.qlearning;

import static pacman.game.Constants.DELAY;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import pacman.Executor;
import pacman.controllers.Controller;
import pacman.controllers.examples.AggressiveGhosts;
import pacman.controllers.examples.Legacy;
import pacman.controllers.examples.StarterGhosts;
import pacman.entries.mcts.MCTS;
import pacman.entries.mcts.MctsNode;
import pacman.entries.mcts.MctsState;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.internal.Node;
import pacman.game.Game;

public class QLearner extends Controller<MOVE>{

	private final float NEW_VALUE = 500000f;
	
	public HashMap<QState, Integer> states = new HashMap<QState, Integer>();
	public HashSet<QState> visited = new HashSet<QState>(); 
	public List<Integer> junctions;
	public int lastLevel = -1;
	
	public QLearner(HashMap<QState, Integer> states){
		super();
		this.states = states;
	}
	
	public MOVE getMove(Game game, float exploit) {	
		
		int level = game.getCurrentLevel();
		
		if (junctions == null || lastLevel != level)
			junctions = getJunctions(game);
		
		lastLevel = level;
		
		// Save state if in junction
		int pacman = game.getPacmanCurrentNodeIndex();
		if (junctions.contains(pacman)){
			QState state = new QState(game);
			if (!visited.contains(state))
				visited.add(state);
		}
		
		// Choose best action
		return bestAction(game);
		
	}
	
	private MOVE bestAction(Game game) {
		
		QMove bestMove = null;
		float bestValue = -9999f;
		for(QMove move : getPossblesMoves(game)){
			
			float value = 0;
			if (states.containsKey(move.getState())){
				value = states.get(move.getState());
			} else {
				value = NEW_VALUE;
			}
			
			if (value > bestValue){
				bestMove = move;
				bestValue = value;
			}
			
		}
		
		if (bestMove == null){
			return MOVE.DOWN;
		}
		
		return bestMove.getMove();
		
	}


	private List<QMove> getPossblesMoves(Game game) {
		
		List<QMove> moves = new ArrayList<QMove>();
		
		for(MOVE move : MOVE.values()){
			
			Game result = simulateUntilJunction(game, move);
			if (result == null)
				continue;
			
			QState state = new QState(result);
			moves.add(new QMove(move, state));
			
		}
		
		return moves;
		
	}

	private Game simulateUntilJunction(Game game, MOVE move) {
		
		int pacman = game.getPacmanCurrentNodeIndex();
		int junction = -1;
		
		// Closest junctions
		if (game.getNeighbour(pacman, move) != -1){
			junction = closestJunction(game, move);
		} else {
			return null;
		}
		
		if (junction == -1)
			return null;
			
		Game result = runExperimentUntilJunction(new AggressiveGhosts(), game, junction, move);
		
		return result;
		
	}

	private Game runExperimentUntilJunction(Controller<EnumMap<GHOST,MOVE>> ghostController, Game game, int junction, MOVE move) {
		
		Game clone = game.copy();

		int now = clone.getPacmanCurrentNodeIndex();
		
		while(now != junction){

			int last = now;
			
			clone.advanceGame(move,
		    		ghostController.getMove(clone.copy(),
		    		System.currentTimeMillis()));
		    
			now = clone.getPacmanCurrentNodeIndex();

			if (now == last){
				//System.out.println("ERROR: Junction not found");
				break;
			}
			
		}
		
		return clone;
		
	}

	private int closestJunction(Game game, MOVE move) {
		
		int from = game.getPacmanCurrentNodeIndex();
		int current = from;
		if (current == -1)
			return -1;
		
		while(!junctions.contains(current) || current == from){
			
			int next = game.getNeighbour(current, move);
			
			if (next == from)
				return -1;
			
			current = next;
			if (current == -1)
				return -1;
			
		}
		
		return current;
		
	}

	private boolean dieTest(Game game, MOVE move) {
		
		Controller<EnumMap<GHOST,MOVE>> ghostController = new AggressiveGhosts();
    	
		Game clone = game.copy();
			
		int livesBefore = clone.getPacmanNumberOfLivesRemaining();
		
		clone.advanceGame(move,
	        	ghostController.getMove(clone.copy(),100000));
	        
	    int livesAfter = clone.getPacmanNumberOfLivesRemaining();
		if (livesAfter < livesBefore)
			return false;
		
		return true;
		
	}

	public static List<Integer> getJunctions(Game game){
		List<Integer> junctions = new ArrayList<Integer>();
		
		int[] juncArr = game.getJunctionIndices();
		for(Integer i : juncArr)
			junctions.add(i);
		
		junctions.addAll(getTurns(game));
		
		return junctions;
		
	}
	
	private static Collection<? extends Integer> getTurns(Game game) {
		
		List<Integer> turns = new ArrayList<Integer>();
		
		for(Node n : game.getCurrentMaze().graph){
			
			int down = game.getNeighbour(n.nodeIndex, MOVE.DOWN);
			int up = game.getNeighbour(n.nodeIndex, MOVE.UP);
			int left = game.getNeighbour(n.nodeIndex, MOVE.LEFT);
			int right = game.getNeighbour(n.nodeIndex, MOVE.RIGHT);
			
			if (((down != -1) != (up != -1)) || ((left != -1) != (right != -1))){
				turns.add(n.nodeIndex);
			} else if (down != -1 && up != -1 && left != -1 && right != -1){
				turns.add(n.nodeIndex);
			}
			
		}
		
		return turns;
	}

	@Override
	public MOVE getMove(Game game, long timeDue) {
		return getMove(game, 1.0f);
	}
	
	
}
