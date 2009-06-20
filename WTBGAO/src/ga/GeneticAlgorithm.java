package ga;

import ga.data.Chromosome;
import ga.data.GAParametreler;
import ga.fitness.FitnessFunction;
import ga.interfaces.Crossover;
import ga.interfaces.GAListener;
import ga.interfaces.Mutation;
import ga.interfaces.Selection;
import ga.utils.QubbleSortAlgorithm;
import ga.utils.RandomNumberGenerator;

public class GeneticAlgorithm {

	boolean durdur = false;
	boolean duraklat = false;
	Thread th;

	// Elite kromozomlari tutuyoruz
	private Chromosome elite;

	private float oncekiMaliyet;

	private GAParametreler parameters;

	private Chromosome[] population;

	private Crossover crossover;
	private Mutation mutation;
	private Selection selection;
	private FitnessFunction fitness;

	private GAListener gaListener = null;

	public GeneticAlgorithm(GAParametreler p, Crossover c, Mutation m,
			Selection s, FitnessFunction f) {
		this.crossover = c;
		this.mutation = m;
		this.parameters = p;
		this.selection = s;
		this.fitness = f;
	}

	public void setGAListener(GAListener gal) {
		this.gaListener = gal;
	}

	public Chromosome calculate(int beklemeSuresi) {
		
		System.out.println(parameters.getPopulationSize());
		final long baslangic = System.currentTimeMillis();

		generateRandomPopulation(parameters.getPopulationSize());

		int nesil = 0;
		int sayac = 0;

		calculateAndSort();

		elite = population[0];

		while (nesil < parameters.getGenerationCount()) {
			if (durdur)
				break;
			while (duraklat) {
				try {
					wait();
				} catch (Exception e) {
				}
			}
			nesil++;
			final long bas = System.currentTimeMillis();
			if (elite != null)
				oncekiMaliyet = elite.getFitness();
			crossover();
			calculateAndSort();
			mutate();
			calculateAndSort();
			elitism();
			if (elite.getFitness() == oncekiMaliyet) {
				sayac++;
			} else {
				sayac = 0;
			}
			gaListener.generation(nesil, elite);
			gaListener.loadPopulation(population);
			System.out.println("GENERATION" + nesil + "\t T:"
					+ (System.currentTimeMillis() - bas) + "ms \t ELITE:"
					+ elite.toString());
			try {
				Thread.sleep(beklemeSuresi);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("TOPLAM:" + (System.currentTimeMillis() - baslangic)
				+ "ms");
		gaListener.finished(elite);
		return elite;
	}

	public void durdur() {
		// Algoritma durdurulacak
		durdur = true;
	}

	public void duraklat() {
		// Algoritma duraklatilacak
		duraklat = !duraklat;
	}

	private void calculateAndSort() {
		// Calculate cost for every individual of population
		for (final Chromosome chr : population) {
			chr.setFitness(fitness.getFitness(chr));
		}
		// Sort population
		QubbleSortAlgorithm.getInstance().sort(population);
	}

	public void crossover() {
		try {
			final int mateCount = (int) (parameters.getCrossoverProbability()
					* population.length * 0.5);
			Chromosome[] caprazlanacaklar = selection.select(population,
					mateCount);

			for (int i = 0; i < caprazlanacaklar.length - 1; i = i + 2) {
				caprazlanacaklar[i] = crossover.crossover(caprazlanacaklar[i],
						caprazlanacaklar[i + 1]);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private void elitism() {
		if (elite == null) {
			elite = parameters.createChromosome(parameters.getGeneCount());
			elite.setFitness(fitness.getFitness(elite));
		}
		if (elite.getFitness() > population[0].getFitness()) {
			elite.setGenes(population[0].getGenes());
			elite.setFitness(fitness.getFitness(elite));
		} else {
			// Son bireyi degistirelim
			population[population.length - 1].setGenes(elite.getGenes());
			population[population.length - 1].setFitness(elite.getFitness());
		}
	}

	private void generateRandomPopulation(final int populationSize) {
		population = new Chromosome[populationSize];
		final long bas = System.currentTimeMillis();

		final int nodeCount = parameters.getGeneCount();
		// Generate random population
		for (int i = 0; i < populationSize; i++) {
			population[i] = parameters.createChromosome(nodeCount);
		}
		System.out.println("TOPLUM INIT:" + (System.currentTimeMillis() - bas)
				+ "ms");
	}

	private void mutate() {
		int howManyMutation = (int) (population.length * parameters
				.getMutationProbability());
		for (int i = 0; i < howManyMutation; i++) {
			int which = RandomNumberGenerator.getInstance().nextInt(
					population.length);
			mutation.mutate(population[which]);
		}
	}

	public void baslat(final int ms) {
		durdur = false;
		duraklat = false;
		th = new Thread() {
			public void run() {
				calculate(ms);
			};
		};
		th.start();
	}
}