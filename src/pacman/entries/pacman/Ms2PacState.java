package pacman.entries.pacman;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

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
public class Ms2PacState extends Controller<MOVE>
{
	
	private enum PacState {
		EAT, FLEE
	}

	private static final int FLEE_DISTANCE = 30;
	private static final int FREE_DISTANCE = 50;
	private static PacState state = PacState.EAT;
	
	public MOVE getMove(Game game, long timeDue) 
	{
				
		MOVE move = game.getPacmanLastMoveMade();
		int currentNode=game.getPacmanCurrentNodeIndex();
		
		// RUN AWAY!
		int distanceToGhost = distanceToGhost(game, currentNode);
		System.out.println("Distance: " + distanceToGhost);
		if (distanceToGhost <= FLEE_DISTANCE && distanceToGhost != -1)
			state = PacState.FLEE;
		else if (state == PacState.FLEE && distanceToGhost >= FREE_DISTANCE)
			state = PacState.EAT;
		
		if (state == PacState.FLEE)
			return flee(game);
			
		return eat(game);
		
	}
	
	private MOVE flee(Game game) {
		System.out.println("FLEEING!");
		int currentNode=game.getPacmanCurrentNodeIndex();
		
		int ghostNode = closestGhostNode(game, currentNode);
		
		return game.getNextMoveAwayFromTarget(currentNode, ghostNode, DM.MANHATTAN);
		
	}

	private MOVE eat(Game game) {
		System.out.println("EATING!");
		int depth = 15;
		int currentNode=game.getPacmanCurrentNodeIndex();
		MOVE move = game.getPacmanLastMoveMade();
		int[] pills=game.getPillIndices();
		List<Integer> pillList = new ArrayList<Integer>();
		for(int p = 0; p < pills.length; p++){
			if (game.isPillStillAvailable(game.getPillIndex(pills[p]))){
				pillList.add(pills[p]);
			}
		}
		
		int[] bestPath = {0};
		int bestValue = -1;
		
		nodes = new HashSet<Integer>();
		List<Integer> nearby = getNearbyNodes(game, currentNode, depth);
		for(Integer n : nearby){

			int[] path = {};
			try{
				path = game.getShortestPath(currentNode, n);
			} catch (Exception e){
				continue;
			}
			int value = 0;
			for(int i : path){
				if (pillList.contains(i)){
					value += 1;
				}
			}
			if (value > bestValue){
				bestValue = value;
				bestPath = path;
			}
		}
		try{
			move = game.getNextMoveTowardsTarget(currentNode, bestPath[0], DM.MANHATTAN);
		} catch (Exception e){
			
		}
		System.out.println("BEST: " + bestValue + ", l: " + bestPath.length);
		return move;
		
		
	}
	
	private HashSet<Integer> nodes = new HashSet<Integer>();
	private List<Integer> getNearbyNodes(Game game, int node, int distance){
		List<Integer> list = new ArrayList<Integer>();
		if (distance < 1)
			return list;
		
		for(int n : game.getNeighbouringNodes(node)){
			if (!nodes.contains(n)){
				list.add(n);
				nodes.add(n);
				list.addAll(getNearbyNodes(game, n, distance -1));
			}
		}
		
		return list;
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
	
	private int closestGhostNode(Game game, int node) {
		int closestGhost = 999;
		int closestNode = 0;
		for(GHOST ghost : GHOST.values()){
			int ghostNode = game.getGhostCurrentNodeIndex(ghost);
			int distance = game.getShortestPathDistance(node,ghostNode);
			if (distance < closestGhost){
				closestGhost = distance;
				closestNode = ghostNode;
			}
		}
		return closestNode;
	}

}