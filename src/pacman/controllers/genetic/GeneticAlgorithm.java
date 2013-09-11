package pacman.controllers.genetic;

import java.util.List;

public abstract class GeneticAlgorithm {
	
	protected int size;
	protected int generations;
	
	public GeneticAlgorithm(int size, int generations){
		this.size = size;
		this.generations = generations;
	}
	
	public abstract Genome getBest();
	public abstract double fitness(Genome genome);

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getGenerations() {
		return generations;
	}

	public void setGenerations(int generations) {
		this.generations = generations;
	}
	
}
