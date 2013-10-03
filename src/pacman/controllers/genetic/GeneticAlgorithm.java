package pacman.controllers.genetic;

import java.util.List;

public abstract class GeneticAlgorithm {
	
	protected int size;
	protected int generations;
	protected int mutationChange;
	protected int trials;
	
	public GeneticAlgorithm(int size, int generations, int mutationChange, int trials){
		this.size = size;
		this.generations = generations;
		this.mutationChange = mutationChange;
		this.trials = trials;
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

	public int getMutationChange() {
		return mutationChange;
	}

	public void setMutationChange(int mutationChange) {
		this.mutationChange = mutationChange;
	}

	public int getTrials() {
		return trials;
	}

	public void setTrials(int trials) {
		this.trials = trials;
	}
	
}
