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
public class Ms2PacAstar extends Controller<MOVE>
{

	private static final int MIN_DISTANCE = 20;
	
	public MOVE getMove(Game game, long timeDue) 
	{
				
		MOVE move = game.getPacmanLastMoveMade();
		int position = game.getPacmanCurrentNodeIndex();
		ArrayList<Integer> path = new Astar().search(game, position);
		return game.getNextMoveTowardsTarget(position, path.get(0), DM.MANHATTAN);
		//return eat(game);
		
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
	

	public class SearchNode {
		private int index;
		private int score;
		private int time;
		public SearchNode(int index){
			this.index = index;
			this.score = 0;
			this.time = 0;
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public int getScore() {
			return score;
		}
		public void setScore(int score) {
			this.score = score;
		}
		public int getTime() {
			return time;
		}
		public void setTime(int time) {
			this.time = time;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + index;
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
			SearchNode other = (SearchNode) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (index != other.index)
				return false;
			return true;
		}
		private Ms2PacAstar getOuterType() {
			return Ms2PacAstar.this;
		}
		
	}


	public class Astar {
	
	
		public ArrayList<Integer> search(Game game, int from) {
			
	        HashTreeSet<SearchNode> openSet = new HashTreeSet<SearchNode>();
	        
	        HashSet<SearchNode> closedSet = new HashSet<SearchNode>();
	        Map<SearchNode,SearchNode> cameFrom = new HashMap<SearchNode, SearchNode>();
	        HashMap<SearchNode, Integer> times = new HashMap<SearchNode, Integer>();
	        HashMap<SearchNode, Integer> scores = new HashMap<SearchNode, Integer>();
	        
	        SearchNode start = new SearchNode(from);
	        openSet.add(start);
	        
	        SearchNode bestNode = start;
	        while (openSet.size()!= 0){
	        
	        	SearchNode current = openSet.pollFirst();
	        	if (current == null)
	        		return createPath(start, bestNode, scores, cameFrom);
	        
	        	if (current.getScore() > bestNode.getScore()){
	        		//bestScore = current.getScore();
	        		bestNode = current;
	        	}
	        	
	        	times.put(current, 0);
	        	scores.put(current, 0);
	            
	        	closedSet.add(current);
	        	openSet.remove(current);

	        	for (Integer i : game.getNeighbouringNodes(current.getIndex())){
	        		SearchNode neighbor = new SearchNode(i);
	        		if (scores.get(neighbor) == null)
	        			scores.put(neighbor, 0);
        			if (times.get(neighbor) == null)
        				times.put(neighbor, 0);
	            	
	            	int time = times.get(current) + 1;
	            	int score = scores.get(current) + valueOf(neighbor, game, time);
	            	
	            	/*
	            	if (hitGhost(neighbor, game, time))
	            		continue;
	            	*/
	            	if (closedSet.contains(neighbor) && score < scores.get(neighbor))
	            		continue;
	            	
	            	if (!closedSet.contains(neighbor) && score >= scores.get(neighbor)){
	            		scores.put(neighbor, score);
	            		times.put(neighbor, time);
	            		neighbor.setScore(score);
	            		neighbor.setTime(time);
	            		cameFrom.put(neighbor, current);
	            		if (!openSet.contains(neighbor))
	            			openSet.add(neighbor);
	                }
	            	
	            }
	        }
	        ArrayList<Integer> path = new ArrayList<Integer>();
	        path.add(start.getIndex());
	        return path;
	        
	    }

		private boolean hitGhost(SearchNode node, Game game, int time) {
			
			if(distanceToGhost(game, node.getIndex()) <= time){
				return true;
			}
			return false;
			
		}

		private int valueOf(SearchNode node, Game game, int time) {
			
			int value = 1;
			
			// Any pill?
			for(int p : game.getActivePillsIndices()){
				if (p == node.getIndex()){
					value += 10;
				}
			}
			
			if (hitGhost(node, game, time)){
				value -= 1000;
			}
			
			return value;
		}

		private ArrayList<Integer> createPath(SearchNode start, SearchNode end, Map<SearchNode, Integer> scores, Map<SearchNode, SearchNode> cameFrom) {
	        /*
			SearchNode goal = null;
			int bestScore = -1000;
			for(SearchNode n : scores.keySet()){
				int score = scores.get(n);
				if (score > bestScore){
					bestScore = score;
					goal = n;
				}
			}
			*/
			// Collect path
			ArrayList<SearchNode> path = new ArrayList<SearchNode>();
			SearchNode from = end;
	        
	        while (!(from.equals(start))){
	        	//System.out.println(i);
	            path.add(from);
	            from = cameFrom.get(from);
	        }
	        
	        System.out.println("path length: " + path.size());
	        // Reverse list
	        Collections.reverse(path);
	        
	        ArrayList<Integer> indexPath = new ArrayList<Integer>();
	        for(SearchNode n : path){
				indexPath.add(n.getIndex());
			}
	        
	        return indexPath;
	    }
	}

	public class HashTreeSet<T> implements Set<SearchNode> {
		
		TreeSet<SearchNode> treeSet;
		HashSet<SearchNode> hashSet;
		
		public HashTreeSet(){
			
			treeSet = new TreeSet<SearchNode>(new Comparator<SearchNode>() {

				@Override
				public int compare(SearchNode arg0, SearchNode arg1) {
					if (arg0.getScore() > arg1.getScore())
						return 1;
					if (arg0.getScore() < arg1.getScore())
						return -1;
					return 0;
				}
				
				
			});
	        
	        hashSet = new HashSet<SearchNode>();
	        
		}
		
		public SearchNode pollFirst(){
			SearchNode n = treeSet.pollFirst();
			hashSet.remove(n);
			return n;
		}

		@Override
		public boolean add(SearchNode n) {
			treeSet.add(n);
			hashSet.add(n);
			return false;
		}

		@Override
		public boolean addAll(Collection<? extends SearchNode> c) {
			treeSet.addAll(c);
			hashSet.addAll(c);
			return false;
		}

		@Override
		public void clear() {
			treeSet.clear();
			hashSet.clear();
		}

		@Override
		public boolean contains(Object o) {
			return hashSet.contains(o);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return hashSet.containsAll(c);
		}

		@Override
		public boolean isEmpty() {
			return hashSet.isEmpty();
		}

		@Override
		public Iterator<SearchNode> iterator() {
			return hashSet.iterator();
		}

		@Override
		public boolean remove(Object o) {
			if (hashSet.remove(o) && treeSet.remove(o)){
				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			if (hashSet.removeAll(c) && treeSet.removeAll(c)){
				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			if (hashSet.retainAll(c) && treeSet.retainAll(c)){
				return true;
			} else {
				return false;
			}
		}

		@Override
		public int size() {
			return hashSet.size();
		}

		@Override
		public Object[] toArray() {
			return hashSet.toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			// TODO Auto-generated method stub
			return null;
		}
	}

}


