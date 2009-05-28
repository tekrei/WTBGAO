package ga.fitness;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import ga.data.Chromosome;

public class TSPFitnessFunction implements FitnessFunction {
	private Vector<Point2D> sehirler;

	public TSPFitnessFunction(Vector<Point2D> _sehirler) {
		sehirler = _sehirler;
	}

	public float getFitness(Chromosome chr) {
		float fitness = 0;

		int[] genes = chr.getGenes();

		for (int i = 0; i < chr.getGeneCount(); i++) {
			fitness += getDistance(genes[i], genes[i + 1]);
		}

		return fitness;
	}

	private float getDistance() {

		return sehirler.;

	}

	private void parseFile(String fileName) throws NumberFormatException,
			IOException {

		int EUC_2D = 1;

		int fileType = 0;

		File dosya = new File("/home/yzgrafik/workspace/WTBGAO" + fileName);
		BufferedReader in = new BufferedReader(new FileReader(dosya));
		String line;
		boolean nodeCoordSection = false;
		sehirler = new Vector<Point2D>();
		while ((line = in.readLine()) != null) {
			if (!line.equalsIgnoreCase("EOF") && !line.equalsIgnoreCase(" EOF")
					&& !line.equals("")) {
				if (!line.equalsIgnoreCase("NODE_COORD_SECTION")
						&& !nodeCoordSection) {
					if (line.equalsIgnoreCase("EDGE_WEIGHT_TYPE: EUC_2D"))
						fileType = EUC_2D;
				} else if (line.equalsIgnoreCase("NODE_COORD_SECTION")) {
					nodeCoordSection = true;
				} else { // All the numbers are in this part

					StringTokenizer strTok = new StringTokenizer(line, " \t");

					strTok.nextToken(); // Discard the city number
					if (fileType == EUC_2D) {
						double x = Double.valueOf(strTok.nextToken())
								.doubleValue();
						double y = Double.valueOf(strTok.nextToken())
								.doubleValue();
						sehirler.addElement(new Point2D.Double(x, y));

					}

				}
			}
		}
	}
}
/*
 * private double getDistance(Gene gi, Gene gj) { return sehirler.get((Integer)
 * gi.getAllele()).distance( sehirler.get((Integer) gj.getAllele()));
 */