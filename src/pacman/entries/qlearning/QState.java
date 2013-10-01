package pacman.entries.qlearning;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class QState {

	int level;
	int junction;
	int distanceUp;
	boolean edibleUp;
	int distanceDown;
	boolean edibleDown;
	int distanceRight;
	boolean edibleRight;
	int distanceLeft;
	boolean edibleLeft;
	public QState(int level, int junction, int distanceUp, boolean edibleUp,
			int distanceDown, boolean edibleDown, int distanceRight,
			boolean edibleRight, int distanceLeft, boolean edibleLeft) {
		super();
		this.level = level;
		this.junction = junction;
		this.distanceUp = distanceUp;
		this.edibleUp = edibleUp;
		this.distanceDown = distanceDown;
		this.edibleDown = edibleDown;
		this.distanceRight = distanceRight;
		this.edibleRight = edibleRight;
		this.distanceLeft = distanceLeft;
		this.edibleLeft = edibleLeft;
	}
	
	public QState(Game game) {
		
		this.level = game.getCurrentLevel();
		this.junction = game.getPacmanCurrentNodeIndex();
		setDistanceAndEdible(game, MOVE.UP);
			
	}
	
	private void setDistanceAndEdible(Game game, MOVE move) {
		
		int shortest = 10000;
		boolean edible = false;
		for(GHOST ghost : GHOST.values()){
			
			int ghostPos = game.getGhostCurrentNodeIndex(ghost);
			int distance = (int) game.getDistance(game.getPacmanCurrentNodeIndex(), ghostPos, move, DM.MANHATTAN);
			
			if (distance < shortest){
				shortest = distance;
				if (game.isGhostEdible(ghost))
					edible = true;
				else
					edible = false;
			}
			
		}
		
		shortest = digitalize(shortest);
		
		switch(move){
			case UP : this.distanceUp = shortest; this.edibleUp = edible; break;
			case RIGHT : this.distanceRight = shortest; this.edibleRight = edible; break;
			case LEFT : this.distanceLeft = shortest; this.edibleLeft = edible; break;
			case DOWN : this.distanceDown = shortest; this.edibleDown = edible; break;
		}
		
	}
	private int digitalize(int i) {

		int n = 1;
		while(i > n){
			n = n * 2;
		}
		return n;
		
	}

	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getJunction() {
		return junction;
	}
	public void setJunction(int junction) {
		this.junction = junction;
	}
	public int getDistanceUp() {
		return distanceUp;
	}
	public void setDistanceUp(int distanceUp) {
		this.distanceUp = distanceUp;
	}
	public boolean isEdibleUp() {
		return edibleUp;
	}
	public void setEdibleUp(boolean edibleUp) {
		this.edibleUp = edibleUp;
	}
	public int getDistanceDown() {
		return distanceDown;
	}
	public void setDistanceDown(int distanceDown) {
		this.distanceDown = distanceDown;
	}
	public boolean isEdibleDown() {
		return edibleDown;
	}
	public void setEdibleDown(boolean edibleDown) {
		this.edibleDown = edibleDown;
	}
	public int getDistanceRight() {
		return distanceRight;
	}
	public void setDistanceRight(int distanceRight) {
		this.distanceRight = distanceRight;
	}
	public boolean isEdibleRight() {
		return edibleRight;
	}
	public void setEdibleRight(boolean edibleRight) {
		this.edibleRight = edibleRight;
	}
	public int getDistanceLeft() {
		return distanceLeft;
	}
	public void setDistanceLeft(int distanceLeft) {
		this.distanceLeft = distanceLeft;
	}
	public boolean isEdibleLeft() {
		return edibleLeft;
	}
	public void setEdibleLeft(boolean edibleLeft) {
		this.edibleLeft = edibleLeft;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + distanceDown;
		result = prime * result + distanceLeft;
		result = prime * result + distanceRight;
		result = prime * result + distanceUp;
		result = prime * result + (edibleDown ? 1231 : 1237);
		result = prime * result + (edibleLeft ? 1231 : 1237);
		result = prime * result + (edibleRight ? 1231 : 1237);
		result = prime * result + (edibleUp ? 1231 : 1237);
		result = prime * result + junction;
		result = prime * result + level;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QState other = (QState) obj;
		if (distanceDown != other.distanceDown)
			return false;
		if (distanceLeft != other.distanceLeft)
			return false;
		if (distanceRight != other.distanceRight)
			return false;
		if (distanceUp != other.distanceUp)
			return false;
		if (edibleDown != other.edibleDown)
			return false;
		if (edibleLeft != other.edibleLeft)
			return false;
		if (edibleRight != other.edibleRight)
			return false;
		if (edibleUp != other.edibleUp)
			return false;
		if (junction != other.junction)
			return false;
		if (level != other.level)
			return false;
		return true;
	}
	
	
	
}
