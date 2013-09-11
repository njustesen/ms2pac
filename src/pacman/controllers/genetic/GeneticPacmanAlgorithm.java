package pacman.controllers.genetic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pacman.Executor;
import pacman.controllers.examples.StarterGhosts;

public class GeneticPacmanAlgorithm extends GeneticAlgorithm {

	public static void main(String[] args)
	{
		
		GeneticPacmanAlgorithm alg = new GeneticPacmanAlgorithm(20, 10);
		alg.getBest();
		
	}
	
	public GeneticPacmanAlgorithm(int size, int generations) {
		super(size, generations);
		
	}

	@Override
	public Genome getBest() {
		
		// Populate
		List<Genome> population = newPopulation();
		
		Genome bestGenome = null;
		for(int g = 0; g < generations; g++){
			// Test
			double[] scores = new double[size];
			int idx = 0;
			for(Genome genome : population){
				
				Executor exec=new Executor();
				double score = exec.runExperimentWithAvgScore(new GeneticPacman2(genome), new StarterGhosts(),1);
				scores[idx] = score;
				idx++;
				
			}
			
			// Kill
			int killings = size / 2;
			double[] clone = new double[scores.length];
			
			double bestScore = -999999;
			for(int i=0; i<scores.length;i++){
				clone[i]=scores[i];
				if (clone[i] > bestScore){
					bestScore = clone[i];
					bestGenome = population.get(i);
				}
			}
			System.out.println("Best: " + bestScore);
			Arrays.sort(clone);
			
			// Stop if last generation
			if (g == generations-1)
				return bestGenome;
			
			double surviveLimit = clone[clone.length-killings];
			
			List<Genome> survivors = new ArrayList<Genome>();
			for(int i=0; i<scores.length;i++){
				if (scores[i] > surviveLimit)
					survivors.add(population.get(i));
			}
			
			// Reproduce
			List<Genome> nextGeneration = new ArrayList<Genome>();
			nextGeneration.addAll(survivors);
			
			for(int i = 0; i<survivors.size();i++){
				
				Genome parentA = survivors.get(i);
				Genome parentB = survivors.get(0);
				if (i+1 < survivors.size())
					parentB = survivors.get(i+1);
				
				Genome child = Genome.breedChild(parentA, parentB);
				nextGeneration.add(child);
				
			}
			
			// Mutate
			for(Genome genome : nextGeneration){
				if (Math.random() * 10 == 1 && genome != bestGenome){
					genome.mutate();
				}
			}
		
			population = nextGeneration;
		}
		
		return null;
	}

	private List<Genome> newPopulation() {
		
		List<Genome> population = new ArrayList<Genome>();
		
		for(int i=0; i<size;i++){
			population.add(Genome.randomGenome());
		}
		
		return population;
		
	}

	@Override
	public double fitness(Genome genome) {
		// TODO Auto-generated method stub
		return 0;
	}

}
