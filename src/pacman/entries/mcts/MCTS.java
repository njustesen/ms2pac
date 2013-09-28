package pacman.entries.mcts;

import static pacman.game.Constants.DELAY;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

import pacman.Executor;
import pacman.controllers.Controller;
import pacman.controllers.examples.AggressiveGhosts;
import pacman.controllers.examples.Legacy;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.internal.Node;
import pacman.game.Game;

public class MCTS extends Controller<MOVE>{

	public static final int NEW_LIFE_VALUE = 1000;
	public static final int LOST_LIFE_VALUE = -2000;
	private static final int SIM_STEPS = 100;
	private static final int TREE_TIME_LIMIT = 30;
	// Hoeffding ineqality
	float C = (float) (1f / Math.sqrt(2));
	Controller<EnumMap<GHOST,MOVE>> ghosts = new Legacy();
	
	@Override
	public MOVE getMove(Game game, long timeDue) {
		
		return MctsSearch(game, 38);
		
	}
	
	private MOVE MctsSearch(Game game, long ms) {
		
		long start = new Date().getTime();
		MctsNode v0 = new MctsNode(new MctsState(true, game), null, game.getPacmanLastMoveMade(), 0);
		
		while(new Date().getTime() < start + ms){
			
			MctsNode v1 = treePolicy(v0);
			if (v1 == null)
				return MOVE.DOWN;
			
			int score = defaultPolicy(v1, v0);
			
			backup(v1, score);
			
		}
		
		System.out.println(v0.print(0));
		
		MOVE move = bestChild(v0, 0).getMove();
		System.out.println(move);
		
		return move;
		
	}


	private MctsNode treePolicy(MctsNode node) {
		
		if (node.isExpandable()){
			if (node.getTime() <= TREE_TIME_LIMIT)
				return expandedNode(node);
			else
				return node;
		}
		
		if (node.getState().isAlive())
			return treePolicy(bestChild(node, C));
		else
			return node;
			
	}

	private MctsNode expandedNode(MctsNode node) {
		return node.expand();
	}
	
	private MctsNode bestChild(MctsNode v, float c) {
		
		float bestValue = -99999999;
		MctsNode urgent = null;
		
		for(MctsNode node : v.children){
			float value = UCT(node, c);
			
			if (c == 0)
				System.out.println(node.move + "(c=" + c + " : " + value);
			
			if (value > bestValue){
				urgent = node;
				bestValue = value;
			}
		}
		
		return urgent;
	}

	private float UCT(MctsNode node, float c) {
		
		//float reward = getReward(node);
		float reward = node.getValue() / node.getVisited();
		// normalize()
		reward = normalize(reward);
		
		float n = 0;
		if (node.getParent() != null)
			n = node.getParent().getVisited();
		
		float nj = node.getVisited();
		
		float uct = (float) (reward + 2 * c * Math.sqrt((2 * Math.log(n)) / nj));
		
		if (reward < 0){
			reward++;
		}
		
		return uct;
		
		//return (float) (reward + 2 * c * Math.sqrt((2 * Math.log(n)) / nj));
		
	}

	private float getReward(MctsNode node) {
		
		float nodeAvgScore = node.getValue() / node.getVisited();
		float parentAvgScore = node.getParent().getValue() / node.getParent().getVisited();
		
		return nodeAvgScore - parentAvgScore;
		
	}

	private float normalize(float x) {	
		
		float min = -30000;
		float max = 2000;
		float range = max - min;
		float inZeroRange = (x - min);
		float norm = inZeroRange / range;
		
		return norm;
	}

	private int defaultPolicy(MctsNode node, MctsNode root) {
		
		// Terminal
		if (!node.getState().isAlive() || 
				node.getState().getGame().getPacmanNumberOfLivesRemaining() < root.getState().getGame().getPacmanNumberOfLivesRemaining())
			return LOST_LIFE_VALUE;
		
		int result = runExperimentWithAvgScoreLimit(SIM_STEPS);
		
		return result -= root.getState().getGame().getScore();

	}

	private void backup(MctsNode v, int score) {
		
		v.setVisited(v.getVisited() + 1);
		v.setValue(v.getValue() + score);
		if (v.getParent() != null)
			backup(v.getParent(), (int)(score));
		
	}
	

	public static List<Integer> getJunctions(Game game){
		List<Integer> junctions = new ArrayList<Integer>();
		
		int[] juncArr = game.getJunctionIndices();
		for(Integer i : juncArr)
			junctions.add(i);
		/*
		if (game.isGhostEdible(GHOST.BLINKY))
			junctions.add(game.getGhostCurrentNodeIndex(GHOST.BLINKY));
		if (game.isGhostEdible(GHOST.PINKY))
			junctions.add(game.getGhostCurrentNodeIndex(GHOST.PINKY));
		if (game.isGhostEdible(GHOST.INKY))
			junctions.add(game.getGhostCurrentNodeIndex(GHOST.INKY));
		if (game.isGhostEdible(GHOST.SUE))
			junctions.add(game.getGhostCurrentNodeIndex(GHOST.SUE));
		*/
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
	
	public int runExperimentWithAvgScoreLimit(int steps) {
		
		Controller<MOVE> pacManController = new RandomJunctionPacman();
		Controller<EnumMap<GHOST,MOVE>> ghostController = ghosts;
    	
    	Random rnd=new Random(0);
		Game game;
		
		game=new Game(rnd.nextLong());
			
		int livesBefore = game.getPacmanNumberOfLivesRemaining();
		int s = 0;
		while(!game.gameOver() && s < steps)
		{
	        game.advanceGame(pacManController.getMove(game.copy(),System.currentTimeMillis()+DELAY),
	        		ghostController.getMove(game.copy(),System.currentTimeMillis()+DELAY));
	        s++;
	        int livesAfter = game.getPacmanNumberOfLivesRemaining();
			if (livesAfter < livesBefore){
				break;
			}
		}
		
		int score = game.getScore();
		int livesAfter = game.getPacmanNumberOfLivesRemaining();
		if (livesAfter > livesBefore){
			score += MCTS.NEW_LIFE_VALUE;
		} else if (livesAfter < livesBefore){
			score += MCTS.LOST_LIFE_VALUE;
		}
		
		return score;
	}
	
	private void saveToFile(String str, String filename) {
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
			fw.write(str);
			fw.close();
			System.out.println(filename + " saved");
		} catch (FileNotFoundException e1) {
			System.out.println("Error saving " + filename + ". " + e1);
		} catch (IOException e2) {
			System.out.println("Error saving " + filename + ". " + e2);
		}
	}
	
}
