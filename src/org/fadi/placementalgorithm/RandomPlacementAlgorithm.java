package org.fadi.placementalgorithm;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


public class RandomPlacementAlgorithm extends ServerPlacementAlgorithm {

	@Override
	public List<Node> execute(LinkedList<Node> nodes, LinkedList<Link> links, int k) {
		
		// Here we will simply will choose randomly on which SSA node will put a server.
		// Then we will get results like max distance and average distance
		int max = nodes.size();
		int min = 0;
		Random random = new Random();
		int index;
		while (k > 0) {
			index = random.nextInt(max);
			if (nodes.get(index).getIsCenter() == false && (nodes.get(index).getisSSASelected())) {
				nodes.get(index).setIsCenter(true);
				k--;
			}
		}
		// Find closest center to each non-center SSA node
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).getIsCenter() == true) {
				// the center is on the same node
				nodes.get(i).setResponsibleCenter(nodes.get(i));
				nodes.get(i).setDistanceToClosestCenter(0);
			} else if (nodes.get(i).getisSSASelected()) {
				// SSA nodes have to be connected with their closest center
				nodes = getClosestCenter(nodes.get(i), nodes, links);
			}
		}
		return nodes;
	}
	
	public LinkedList<Double> generateResultReport(LinkedList<Node> nodes) {
		// revisit here
		Double totalDistances = 0.0;
		Double averageDistance = 0.0;
		Double maxDistance = 0.0;
		int numberOfSSA = 0;
		LinkedList<Double> results = new LinkedList<>();
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).getisSSASelected() &&(nodes.get(i).getIsCenter() == false)) {
				numberOfSSA++;
				
					totalDistances += nodes.get(i).getDistanceToClosestCenter();
					if ((maxDistance < nodes.get(i).getDistanceToClosestCenter())
							&& (nodes.get(i).getIsCenter() == false)) {
						maxDistance = (double) nodes.get(i).getDistanceToClosestCenter();

					}
				}
			
		}
		averageDistance = totalDistances / numberOfSSA;
		results.add(maxDistance);
		results.add(averageDistance);
		return results;
	}
	
	
}
