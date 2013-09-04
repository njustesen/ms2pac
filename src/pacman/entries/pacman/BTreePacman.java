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
public class BTreePacman extends Controller<MOVE>
{

	BTtree tree = null;
	int runs = 0;
	public MOVE getMove(Game game, long timeDue) 
	{
				
		if (tree == null)
			setupBTtree();
		
		if (runs == 0){
			int i = 0;
			i++;
		}
		
		
		Action action = null;
		while(action == null)
			action = tree.getAction(new BTstate(game));
		
		runs++;
		return appropiateMove(action, game);
		
	}
	
	private MOVE appropiateMove(Action action, Game game) {
		
		switch (action){
		case FLEE: return fleeMove(game);
		case EAT_PILLS: return eatPillsMove(game);
		default:
			return null;
		}
		
	}

	private MOVE eatPillsMove(Game game) {
		System.out.println("EATING PILLS!");
		int currentNode=game.getPacmanCurrentNodeIndex();
		
		int pillNode = closestPillNode(game, currentNode);
		
		return game.getNextMoveTowardsTarget(currentNode, pillNode, DM.MANHATTAN);
	}

	private int closestPillNode(Game game, int node) {
		int closestPill = 999;
		int closestNode = 0;
		for(int i : game.getActivePillsIndices()){
			int distance = game.getShortestPathDistance(node,i);
			if (distance < closestPill){
				closestPill = distance;
				closestNode = i;
			}
		}
		return closestNode;
	}

	private MOVE fleeMove(Game game) {
		System.out.println("FLEEING!");
		int currentNode=game.getPacmanCurrentNodeIndex();
		
		int ghostNode = closestGhostNode(game, currentNode);
		
		return game.getNextMoveAwayFromTarget(currentNode, ghostNode, DM.MANHATTAN);
		
	}

	private void setupBTtree() {
		
		// Priority selector
		PrioritySelector root = new PrioritySelector(null);
		
		// Flee!
		SequenceSelector fleeSel = new SequenceSelector(root);
		BTcondition fleeCondition = new BTcondition(fleeSel){
			@Override
			public boolean holds(BTstate state) {
				int currentNode = state.getGame().getPacmanCurrentNodeIndex();
				int distanceToGhost = distanceToGhost(state.getGame(), currentNode);
				if (distanceToGhost < 40)
					return true;
				else 
					return false;
			}	
		};
		fleeSel.getChildren().add(fleeCondition);
		BTaction fleeAction = new BTaction(fleeSel){
			@Override
			public Action run(BTstate state) {
				return Action.FLEE;
			}	
		};
		fleeSel.getChildren().add(fleeAction);
		root.getChildren().add(fleeSel);
		
		// Eat pills!
		SequenceSelector eatSel = new SequenceSelector(root);

		BTaction eatAction = new BTaction(eatSel){
			@Override
			public Action run(BTstate state) {
				return Action.EAT_PILLS;
			}	
		};
		eatSel.getChildren().add(eatAction);
		root.getChildren().add(eatSel);
		
		// Eat ghosts!
		// TODO:
		
		tree = new BTtree(root);
		
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

	// ACTIONS
	public enum Action{
		FLEE, EAT_PILLS, EAT_GHOSTS;
	}
	
	// ABSTRACT CLASSES AND INTERFACES
	public class BTtree {
		private BTselector root;
		private BTaction running;
		public BTtree(BTselector root){
			this.root = root;
		}
		public Action getAction(BTstate state){
			return root.run(state).getAction();
		}
		public BTselector getRoot() {
			return root;
		}
		public void setRoot(BTselector root) {
			this.root = root;
		}
		public BTaction getRunning() {
			return running;
		}
		public void setRunning(BTaction running) {
			this.running = running;
		}
	}
	public abstract class BTnode {
		protected List<BTnode> children;
		protected BTnode parent;
		public BTnode(BTnode parent){
			this.parent = parent;
			children = new ArrayList<BTnode>();
		}
		public void resetUp(BTnode except){
			if (parent != null)
				parent.resetUp(this);
			resetDown(except);
		}
		public void resetDown(BTnode except){
			for(BTnode child : children){
				if (child != except)
					child.resetDown(except);
			}
		}
	}
	public class BTstate {
		private Game game;
		public BTstate (Game game){
			this.game = game;
		}
		public Game getGame() {
			return game;
		}
		public void setGame(Game game) {
			this.game = game;
		}
	}
	public class BTresponse {
		private boolean success;
		private Action action;
		public BTresponse(boolean success, Action action){
			this.action = action;
			this.success = success;
		}
		public boolean isSuccess() {
			return success;
		}
		public void setSuccess(boolean success) {
			this.success = success;
		}
		public Action getAction() {
			return action;
		}
		public void setAction(Action action) {
			this.action = action;
		}
	}
	public abstract class BTselector extends BTnode {
		public BTselector(BTnode parent) {
			super(parent);
		}
		public abstract BTresponse run(BTstate state);
		public List<BTnode> getChildren() {
			return children;
		}
		public void setChildren(List<BTnode> children) {
			this.children = children;
		}
	}
	public abstract class BTaction extends BTnode {
		public BTaction(BTnode parent) {
			super(parent);
		}

		public abstract Action run(BTstate state);
	}
	public abstract class BTcondition extends BTnode {
		public BTcondition(BTnode parent) {
			super(parent);
		}

		public abstract boolean holds(BTstate state);
	}
	
	// IMPLEMENTATIONS
	public class PrioritySelector extends BTselector {
		public PrioritySelector(BTnode parent) {
			super(parent);
		}

		@Override
		public BTresponse run(BTstate state) {
			for(BTnode child : children){
				// Condition
				if (child instanceof BTcondition && 
						!((BTcondition) child).holds(state) )
					return new BTresponse(false, null);
				// Action
				if (child instanceof BTaction){
					Action action = ((BTaction)child).run(state);
					return new BTresponse(true, action);
				}
				// Selector
				if (child instanceof BTselector){
					BTresponse response = ((BTselector)child).run(state);
					if (!response.isSuccess())
						continue;
					else
						return response;
				}
			}
			return new BTresponse(true, null);
		}
	}
	
	public class SequenceSelector extends BTselector {
		public SequenceSelector(BTnode parent) {
			super(parent);
		}
		private BTnode running;
		
		@Override
		public void resetDown(BTnode except){
			if (running != except)
				running = null;
			
			for(BTnode child : children){
				if (child != except && child instanceof SequenceSelector)
					((SequenceSelector)child).resetDown(except);
			}
		}
		
		@Override
		public void resetUp(BTnode except){
			if (running != except)
				running = null;
			
			parent.resetUp(this);
			resetDown(except);
		}
		
		@Override
		public BTresponse run(BTstate state) {
			return runOnce(state, true);
		}
		
		public BTresponse runOnce(BTstate state, boolean newRun) {
			if (newRun){
				// Sequence logic
				if (running != null && children.contains(running)){
					// Done?
					if (running == children.get(children.size()-1)){
						running = null;
						return new BTresponse(true, null);
					}
					int runningIdx = children.indexOf(running);
					running = children.get(runningIdx + 1);
				} else if (running != null){
					parent.resetUp(this);
					running = children.get(0);
				} else {
					running = children.get(0);
				}
			}
			
			// Condition
			if (running instanceof BTcondition) {
				if (((BTcondition) running).holds(state))
					return run(state);
				running = null;
			}
			// Action
			if (running instanceof BTaction){
				Action action = ((BTaction)running).run(state);
				return new BTresponse(true, action);
			}
			// Selector
			if (running instanceof BTselector){
				BTresponse response = ((BTselector)running).run(state);
				if (!response.isSuccess())
					return runAgain(state);
				else
					return response;
			}
			return new BTresponse(false, null);
		}
		private BTresponse runAgain(BTstate state) {
			return runOnce(state, false);
		}

		public BTnode getRunning() {
			return running;
		}
		public void setRunning(BTnode running) {
			this.running = running;
		}
	}
	
}


