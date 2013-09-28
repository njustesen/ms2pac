package pacman.entries.mcts;

import java.util.ArrayList;
import java.util.List;

import pacman.Executor;
import pacman.controllers.examples.AggressiveGhosts;
import pacman.controllers.examples.StarterGhosts;
import pacman.controllers.genetic.GeneticPacman2;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MctsNode {

	MctsState state;
	MctsNode parent;
	MOVE move;
	int visited;
	float value;
	List<MctsNode> children;
	int directions;
	
	private boolean up = false;
	private boolean right = false;
	private boolean down = false;
	private boolean left = false;
	
	public MctsNode(MctsState state, MctsNode parent, MOVE move) {
		super();
		this.state = state;
		this.parent = parent;
		this.move = move;
		this.visited = 0;
		this.directions = getDirections();
		this.value = 0;
		this.children = new ArrayList<MctsNode>();
	}
	

	public MctsNode expand() {
		
		int pacman = state.getGame().getPacmanCurrentNodeIndex();
		int junction = -1;
		MOVE move = null;
		
		if (!state.isAlive()){
			return null;
		}
		
		// Closest junctions
		if (!up && state.getGame().getNeighbour(pacman, MOVE.UP) != -1){
			junction = closestJunction(MOVE.UP);
			move = MOVE.UP;
		} else if (!right && state.getGame().getNeighbour(pacman, MOVE.RIGHT) != -1){
			junction = closestJunction(MOVE.RIGHT);
			move = MOVE.RIGHT;
		} else if (!down && state.getGame().getNeighbour(pacman, MOVE.DOWN) != -1){
			junction = closestJunction(MOVE.DOWN);
			move = MOVE.DOWN;
		} else if (!left && state.getGame().getNeighbour(pacman, MOVE.LEFT) != -1){
			junction = closestJunction(MOVE.LEFT);
			move = MOVE.LEFT;
		}
		
		if (junction == -1){
			return null;
		}
		
		if (junction != -1){
			updateDirection(move);
			Executor exec=new Executor();
			if (junction == 540 && move == MOVE.UP){
				exec=new Executor();
			}
			MctsState childState = exec.runExperimentUntilJunction(new AggressiveGhosts(), state.getGame(), junction, move);
			if (childState == null || childState.getGame() == null){
				return null;
			}
			MctsNode child = new MctsNode(childState, this, move);
			children.add(child);
			return child;
		}

		return null;
		
	}

	private void updateDirection(MOVE move) {
		switch(move) {
		case UP : up = true; break;
		case DOWN : down = true; break;
		case RIGHT : right = true; break;
		case LEFT : left = true; break;
		}
	}


	private int closestJunction(MOVE move) {
		
		int from = state.getGame().getPacmanCurrentNodeIndex();
		int current = from;
		
		List<Integer> junctions = MCTS.getJunctions(state.getGame());
		
		while(!junctions.contains(current) || current == from){
			
			int next = state.getGame().getNeighbour(current, move);
			
			if (next == from)
				return -1;
			
			current = next;
			
		}
		
		return current;
		
	}
	
	private int getDirections() {
		if (!state.isAlive())
			return 0;
		int node = state.getGame().getPacmanCurrentNodeIndex();
		return state.getGame().getNeighbouringNodes(node).length;
		
	}
	
	public MctsNode getParent() {
		return parent;
	}

	public void setParent(MctsNode parent) {
		this.parent = parent;
	}

	public MctsState getState() {
		return state;
	}

	public void setState(MctsState state) {
		this.state = state;
	}

	public MOVE getMove() {
		return move;
	}

	public void setMove(MOVE move) {
		this.move = move;
	}

	public int getVisited() {
		return visited;
	}

	public void setVisited(int visited) {
		this.visited = visited;
	}

	public List<MctsNode> getChildren() {
		return children;
	}

	public void setChildren(List<MctsNode> children) {
		this.children = children;
	}

	public void setDirections(int directions) {
		this.directions = directions;
	}

	public boolean isExpandable() {
		return directions != children.size() && state.isAlive();
	}


	public float getValue() {
		return value;
	}


	public void setValue(float value) {
		this.value = value;
	}


	public String print(int level) {
		
		String out = "";
		for(int n = 0; n < level; n++){
			out += "\t";
		}
		out += "<node move="+move+" avg="+value/visited+" vis="+visited;
		
		if (children.isEmpty()){
			out += "/>\n";
		} else {
			out += ">\n";
		}
		
		int next = level+1;
		for(MctsNode child : children){
			
			out += child.print(next);
			
		}
		
		if (!children.isEmpty()){
			for(int n = 0; n < level; n++){
				out += "\t";
			}
			out += "</node>\n";
		}
		
		return out;
		
	}
	
}
