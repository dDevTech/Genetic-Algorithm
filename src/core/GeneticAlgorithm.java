package core;

import java.util.ArrayList;
import java.util.Comparator;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Stack;
import java.util.function.Function;
import java.util.function.Supplier;

public class GeneticAlgorithm<T extends DNA> {
	private Function<T, Double> costFunction;
	private PriorityQueue<T> elements;
	private final Supplier<T> instanceCreator;
	private int generation = 1;
	private Random random;

	public GeneticAlgorithm(Supplier<T> instanceCreator, Function<T, Double> costFunction) {
		this.instanceCreator = instanceCreator;
		this.costFunction = costFunction;
		random = new Random();
		elements = new PriorityQueue<>(new Comparator<T>() {
			@Override
			public int compare(T entity1, T entity2) {
				double cost1 = costFunction.apply(entity1);
				double cost2 = costFunction.apply(entity2);
				return cost1 > cost2 ? -1 : 1;

			}
		});
	}

	public void initialize(int sizeDna, double leftInterval, double rightInterval) {
		if (leftInterval > rightInterval)
			throw new IllegalArgumentException("Left interval must be less or equal than right interval");
		if (sizeDna <= 0)
			throw new IllegalArgumentException("Size dna must be positive");

		for (T element : elements) {
			Double[] dna = new Double[sizeDna];
			for (int k = 0; k < dna.length; k++) {
				dna[k] = random.nextDouble() * (Math.abs(leftInterval) + Math.abs(rightInterval)) + leftInterval;
			}
			element.setDna(dna);
			
		}
	}

	public void initialize(int sizeDna) {
		initialize(sizeDna, -1, 1);
	}

	private T newInstance() {
		return instanceCreator.get();
	}

	public void addNewGeneticEntity(T element) {
		elements.add(element);
	}

	private List<T> select(int maxEntitiesToSelect) {
		if (maxEntitiesToSelect <= 0)
			throw new IllegalArgumentException("maxEntitiesToSelect must be positive");
		List<T> selected = new ArrayList<>();
		Stack<T> removed = new Stack<>();
		while (maxEntitiesToSelect > 0) {
			T element = elements.poll();
			selected.add(element);
			removed.add(element);
			maxEntitiesToSelect--;
		}
		elements.addAll(removed);
		return selected;
	}

	private List<T> selectAndRemove(int maxEntitiesToSelect) {
		if (maxEntitiesToSelect <= 0)
			throw new IllegalArgumentException("maxEntitiesToSelect must be positive");
		List<T> selected = new ArrayList<>();
		while (maxEntitiesToSelect > 0) {
			selected.add(elements.poll());
			maxEntitiesToSelect--;
		}
		return selected;
	}

	private List<T> crossover(CROSSOVER_METHOD method, int children, List<T> parents) {
		if (children <= 0)
			throw new IllegalArgumentException("children must be positive");
		List<T> childrens = new ArrayList<>();

		double[] probs = new double[parents.size()];

		if (method == CROSSOVER_METHOD.UNIFORM || method == CROSSOVER_METHOD.MEAN) {
			for (int k = 0; k < probs.length; k++) {
				probs[k] = 1d / parents.size();
			}
		}
		if (method == CROSSOVER_METHOD.PROPORTIONAL || method == CROSSOVER_METHOD.MEAN_PROPORTIONAL) {
			double sumCost = 0;
			for (int k = 0; k < parents.size(); k++) {
				probs[k] = costFunction.apply(parents.get(k));
				sumCost += probs[k];
			}
			for (int k = 0; k < probs.length; k++) {
				probs[k] = probs[k] / sumCost;
			}
		}

		int sizeDNA = parents.get(0).getDna().length;
		for (int c = 0; c < children; c++) {

			Double[] dna = new Double[sizeDNA];

			for (int i = 0; i < sizeDNA; i++) {
				if (method == CROSSOVER_METHOD.MEAN || method == CROSSOVER_METHOD.MEAN_PROPORTIONAL) {
					double sum = 0;
					for (int k = 0; k < parents.size(); k++) {
						sum += parents.get(k).getDna()[i] * probs[k];
					}
					dna[i] = sum;

				} else {
					double prob = random.nextDouble();

					boolean found = false;
					double acc = 0;
					int k = 0;
					for (k = 0; k < probs.length && !found; k++) {
						acc += probs[k];

						if (prob < acc)
							found = true;
					}

					int pos = --k;
					double value = parents.get(pos).getDna()[i];
					dna[i] = value;
				}

			}

			T child = newInstance();
			child.setDna(dna);
			childrens.add(child);
		}

		return childrens;

	}

	/**
	 * 
	 * @param method
	 * @param randomizer
	 * @param shrink     when increased generation the interval of random numbers
	 *                   start to shrink
	 * @param mutate
	 * @param args       OPTIONAL: first arg : probability of mutation second arg:
	 *                   left interval third arg: right interval
	 * @return
	 */
	private void mutate(MUTATION_METHOD method, RANDOMIZER randomizer, boolean shrink, T mutate, double... args) {
		double prob = 1d / mutate.getDna().length;

		if (args.length >= 1) {
			prob = args[0];
		}

		for (int i = 0; i < mutate.getDna().length; i++) {
			if (random.nextDouble() < prob) {
				if (method == MUTATION_METHOD.NEW_VALUE) {
					if (randomizer == RANDOMIZER.UNIFORM) {
						if (args.length >= 3) {
							mutate.getDna()[i] = random.nextDouble() * (Math.abs(args[1]) + Math.abs(args[2]))
									+ args[1];
						} else {
							
							mutate.getDna()[i] = random.nextDouble() * 2 - 1;
						}

					}
					if (randomizer == RANDOMIZER.NORMAL) {
						if (args.length >= 3) {
							mutate.getDna()[i] = random.nextGaussian() * (Math.abs(args[1]) + Math.abs(args[2]))
									+ args[1];
						} else {
							mutate.getDna()[i] = random.nextGaussian() * 2 - 1;
						}

					}
				}
				if (method == MUTATION_METHOD.BOUNDARY) {
					if (randomizer == RANDOMIZER.UNIFORM) {
						if (args.length >= 3) {
							mutate.getDna()[i] += random.nextDouble() * (Math.abs(args[1]) + Math.abs(args[2]))
									+ args[1];
						} else {
							mutate.getDna()[i] += random.nextDouble() * 2 - 1;
						}

					}
					if (randomizer == RANDOMIZER.NORMAL) {
						if (args.length >= 3) {
							mutate.getDna()[i] += random.nextGaussian() * (Math.abs(args[1]) + Math.abs(args[2]))
									+ args[1];
						} else {
							mutate.getDna()[i] += random.nextGaussian() * 2 - 1;
						}

					}
				}
				if (shrink)
					mutate.getDna()[i] = mutate.getDna()[i] * (1d / generation);

			}

		}

	}

	public List<T> executeAlgorithmDefault(int numChildrens, CROSSOVER_METHOD crossoverMeth,
			MUTATION_METHOD mutationMeth, double mutationProb) {
		List<T> parents = select(2);

		List<T> childs = crossover(crossoverMeth, numChildrens, parents);
	
		for (T child : childs) {
			mutate(mutationMeth, RANDOMIZER.NORMAL, false, child, mutationProb);
		}
		return childs;

	}

	public int getGeneration() {
		return generation;
	}
}
