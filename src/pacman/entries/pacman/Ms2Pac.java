package pacman.entries.pacman;

import java.util.ArrayList;
import java.util.Arrays;
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
public class Ms2Pac extends Controller<MOVE>
{

	private static final int MIN_DISTANCE = 20;
	private int closestGhost = 999;
	
	public MOVE getMove(Game game, long timeDue) 
	{
				
		MOVE move = game.getPacmanLastMoveMade();
		int currentNode=game.getPacmanCurrentNodeIndex();
		/*
		// RUN AWAY!
		for(GHOST ghost : GHOST.values())
			if(game.getGhostEdibleTime(ghost)==0 && game.getGhostLairTime(ghost)==0)
				if(game.getShortestPathDistance(currentNode,game.getGhostCurrentNodeIndex(ghost))<MIN_DISTANCE)
					return runAway(game);
		*/
		// EAT!
		return move(game);
		//return eat(game);
		
	}

	private MOVE move(Game game) {
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
		
		List<Integer> nearby = getNearbyNodes(game, currentNode, 6);
		for(Integer n : nearby){

			int[] path = {};
			try{
				path = game.getShortestPath(currentNode, n);
			} catch (Exception e){
				continue;
			}
			if (path.length < 5){
				continue;
			}
			int value = 0;
			int x = 0;
			for(int i : path){
				x++;
				if (pillList.contains(i)){
					//value += 20;
				}
				
				int ghostDistance = distanceToGhost(game, i);
				if (ghostDistance != -1){
					value -= ghostDistance;
				}
				
			}
			System.out.println(value);
			if (value > bestValue){
				bestValue = value;
				bestPath = path;
			}
			
		}
		try{
			move = game.getNextMoveTowardsTarget(currentNode, bestPath[0], DM.MANHATTAN);
		} catch (Exception e){
			
		}
		System.out.println("BEST: " + bestValue);
		return move;
		
		
	}
	
	private List<Integer> getNearbyNodes(Game game, int node, int distance){
		List<Integer> list = new ArrayList<Integer>();
		if (distance < 1)
			return list;
		
		for(int n : game.getNeighbouringNodes(node)){
			list.add(n);
			List<Integer> newList = getNearbyNodes(game, n, distance -1);
			for(int i : newList){
				if (!list.contains(i)){
					list.add(i);
				}
			}
		}
		
		return list;
	}

	private MOVE eat(Game game) {
		System.out.println("EATING!");
		int[] pills=game.getPillIndices();
		int pill = 0;
		int best = 0;
		
		int currentNode=game.getPacmanCurrentNodeIndex();
		MOVE move = game.getPacmanLastMoveMade();
		
		for(int p : pills){
			if(game.isPillStillAvailable(p)){
				int pillDistance = game.getShortestPathDistance(currentNode, p);
				int ghostDistance = distanceToGhost(game, p);
				int value = ghostDistance - (pillDistance * 2);
				if (value > best){
					best = value;
					pill = p;
				}
			}
		}
		
		return game.getApproximateNextMoveTowardsTarget(currentNode, pill, move, DM.MANHATTAN);
		
	}

	private int distanceToGhost(Game game, int node) {
		int closestGhost = 999;
		int closestNode = 0;
		for(GHOST ghost : GHOST.values()){
			int ghostNode = game.getGhostCurrentNodeIndex(ghost);
			int distance = game.getShortestPathDistance(node,ghostNode);
			if (distance < closestGhost){
				closestGhost = distance;
				closestNode = node;
			}
		}
		return closestNode;
	}

	private MOVE runAway(Game game) {
		System.out.println("RUNNING!");
		MOVE move = game.getPacmanLastMoveMade();
		int currentNode=game.getPacmanCurrentNodeIndex();
		for(GHOST ghost : GHOST.values()){
			if(game.getGhostEdibleTime(ghost)==0 && game.getGhostLairTime(ghost)==0){
				int closestNow = game.getShortestPathDistance(currentNode,game.getGhostCurrentNodeIndex(ghost));
				if (closestNow < closestGhost){
					closestGhost = closestNow;
					move = changedDirection(lastMove);
				}
			}
		}	
		return move;
	}

	private MOVE changedDirection(MOVE lastMove) {
		int r = (int) (Math.random() * 4);
		if (r == 0 && !lastMove.equals(MOVE.UP)){
			return MOVE.UP;
		} else if (r == 1 && !lastMove.equals(MOVE.DOWN)){
			return MOVE.DOWN;
		} else if (r == 2 && !lastMove.equals(MOVE.LEFT)){
			return MOVE.LEFT;
		} else if (r == 3 && !lastMove.equals(MOVE.RIGHT)){
			return MOVE.RIGHT;
		}
		return changedDirection(lastMove);
	}
}