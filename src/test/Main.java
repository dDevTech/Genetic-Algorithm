package test;

import java.util.List;

import core.CROSSOVER_METHOD;
import core.GeneticAlgorithm;
import core.MUTATION_METHOD;
import core.RANDOMIZER;

public class Main {
	private static double distanceTravel = 100;
	public static void main(String[] args) {
		GeneticAlgorithm<Animal>genetic = new GeneticAlgorithm<Animal>(Animal::new,Main::cost);
		genetic.addNewGeneticEntity(new Animal(100));
		genetic.addNewGeneticEntity(new Animal(3));
		genetic.addNewGeneticEntity(new Animal(2));
		genetic.addNewGeneticEntity(new Animal(5));
		
	
		genetic.initialize(20);
	
		List<Animal>childs=genetic.executeAlgorithmDefault(10, CROSSOVER_METHOD.UNIFORM, MUTATION_METHOD.NEW_VALUE, 0.99d);
		for(Animal a:childs) {
			System.out.println(a);
		}
		
	}
	public static Double cost(Animal i) {
		return i.getValue()* distanceTravel;
	}
}
