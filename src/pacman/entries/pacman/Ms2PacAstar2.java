package pacman.entries.pacman;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Node;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class Ms2PacAstar2 extends Controller<MOVE>
{

	public MOVE getMove(Game game, long timeDue) 
	{
				
		int position = game.getPacmanCurrentNodeIndex();
		
		int[] bestPath = {};
		int bestValue = -1;
		int[] junctions = game.getJunctionIndices();
		int[] ppills = game.getPowerPillIndices();
		/*
		int[] pills = game.getPillIndices();
		*/
		List<Integer> points = new ArrayList<Integer>();
		points.addAll(getTurns(game));
/*
		for(int i=0; i<junctions.length;i++)
			points.add(junctions[i]);
	*/	
		for(int i=0; i<ppills.length;i++)
			points.add(ppills[i]);
		
		if (game.isGhostEdible(GHOST.BLINKY))
			points.add(game.getGhostCurrentNodeIndex(GHOST.BLINKY));
		if (game.isGhostEdible(GHOST.PINKY))
			points.add(game.getGhostCurrentNodeIndex(GHOST.PINKY));
		if (game.isGhostEdible(GHOST.INKY))
			points.add(game.getGhostCurrentNodeIndex(GHOST.INKY));
		if (game.isGhostEdible(GHOST.SUE))
			points.add(game.getGhostCurrentNodeIndex(GHOST.SUE));
		
		if (game.isGhostEdible(GHOST.BLINKY) && 
				game.isGhostEdible(GHOST.PINKY) &&
				game.isGhostEdible(GHOST.INKY) && 
				game.isGhostEdible(GHOST.SUE)){
			points = new ArrayList<Integer>();
			points.add(game.getGhostCurrentNodeIndex(GHOST.BLINKY));
			points.add(game.getGhostCurrentNodeIndex(GHOST.PINKY));
			points.add(game.getGhostCurrentNodeIndex(GHOST.INKY));
			points.add(game.getGhostCurrentNodeIndex(GHOST.SUE));
		}
			
		
		for(int j : points){
			
			int[] path = game.getShortestPath(position, j);
			int value = valueOfPath(game, path);
			
			if (value > bestValue || bestPath.length == 0){
				bestPath = path;
				bestValue = value;
			}
			
		}
		
		return game.getNextMoveTowardsTarget(position, bestPath[0], DM.MANHATTAN);
		//return eat(game);
		
	}
	
	private Collection<? extends Integer> getTurns(Game game) {
		
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

	private int valueOfPath(Game game, int[] path) {
		
		int[] pills = game.getActivePillsIndices();
		int[] powers = game.getActivePowerPillsIndices();
		ArrayList<Integer> pillList = new ArrayList<Integer>();
		ArrayList<Integer> powerPillList = new ArrayList<Integer>();
		
		for(int i = 0; i<pills.length; i++){
			pillList.add(pills[i]);
		}
		
		for(int i = 0; i<powers.length; i++){
			powerPillList.add(powers[i]);
		}
		
		int value = 0;
		int step = 0;
		boolean power = false;
		int pillValue = 10;
		if (game.getActivePillsIndices().length < 40)
			pillValue = 20;
		else if (game.getActivePillsIndices().length < 20)
			pillValue = 30;
		else if (game.getActivePillsIndices().length < 10)
			pillValue = 40;
		else if (game.getActivePillsIndices().length < 5)
			pillValue = 50;
		
		int pillsPicked = 0;
		for(int i : path){
			
			if (pillList.contains(i)){
				value+=pillValue;
				pillsPicked++;
			}
			
			if (powerPillList.contains(i)){
				value += 100 - distanceToGhost(game, i);
				power = true;
			}
			
			if (distanceToGhost(game, i) <= step && !power){
				value-=1000;
			} else if (distanceToGhost(game, i) <= 10 && !power){
				value -= Math.max(0, 10-distanceToGhost(game, i));
			}
			
			if (distanceToGhost(game, i) == 0 && power){
				value+=1000;
			}
			
			step++;
		}
		if (pillsPicked == game.getActivePillsIndices().length){
			value+=100;
		}
		value+=step*1.5;
		return value;
	}

	private int distanceToGhost(Game game, int node) {
		int closestGhost = 999;
		for(GHOST ghost : GHOST.values()){
			int ghostNode = game.getGhostCurrentNodeIndex(ghost);
			int distance = game.getShortestPathDistance(node,ghostNode);
			if (distance < closestGhost && distance != -1){
				closestGhost = distance;
			}
		}
		return closestGhost;
	}

}


