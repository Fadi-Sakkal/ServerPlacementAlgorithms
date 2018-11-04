package org.fadi.placementalgorithm;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

public class KMedianServerPlacementAlgorithm extends ServerPlacementAlgorithm {
	
	int distribution;
	@Override
	public List<Node> execute(LinkedList<Node> nodes, LinkedList<Link> links, int k) {
		
		Scanner in = new Scanner(System.in);
		System.out.printf("Choose 1 for normal distribution, 2 for uniform distribution : ");
		distribution = in.nextInt();
		if (nodes.size() == 15) {
			// initialize small topology
			nodes = initializePacketsCounters(nodes, distribution);
			nodes = initializeImportances(nodes);
		}
		else if(nodes.size() ==25) {
			//initialize medium topology
			nodes=initializePacketsCountersMedium(nodes,distribution);
			nodes=initializeImportances(nodes);
		}
		System.out.println("Here are the importances and the packets handled of the Nodes:");
		for (int i = 0; i < nodes.size(); i++) {
			System.out.println(nodes.get(i).getName() + " handled packets : " + nodes.get(i).getPacketsHandled() +" has importance of : " + nodes.get(i).getImportance());
		}
		System.out.println("---------------- Now the Costs: ");
		Node newCenter;
		while (k > 0) {
			for (int i = 0; i < nodes.size(); i++) {
				if (nodes.get(i).getIsCenter() == false) {
					nodes = calculateCost(nodes.get(i), nodes, links);
					System.out.println(nodes.get(i).getName() + " has cost of : " + nodes.get(i).getCost());
				}
			}
			newCenter = findMinCostNode(nodes);

			System.out.println(newCenter.getName() + " is now a center.");
			System.out.println("New Iteration Now");
			System.out.println("-------------");
			newCenter.setIsCenter(true);
			modifyImportances(newCenter, nodes, links);
			System.out.println("Importances .......");
			for (int i = 0; i < nodes.size(); i++) {
				System.out.println(nodes.get(i).getName() + " has importance of " + nodes.get(i).getImportance());
			}
			k--;
		}
		for(int i =0; i<nodes.size(); i++) {
			nodes = getClosestCenter(nodes.get(i), nodes, links);
		}
		
		return nodes;
	}
	
	public LinkedList<Node> simulate(LinkedList<Node> nodes){
		nodes = clearPacketCounters(nodes);
		if (nodes.size() == 15) { // small topology

			nodes = generatePackets(nodes, distribution);
		} else {// medium topology
			
			nodes = generatePacketsMedium(nodes, distribution);
		}
		return nodes;
	}


	public LinkedList<Node> calculateCost(Node source, LinkedList<Node> nodes, LinkedList<Link> links) {
		// Here we calculate the cost of placing a server on the source node
		// This method should be called only after filling out the field "distance"
		// correctly using calcDistanceFromNode
		nodes = clearDistances(nodes);
		clearVisited(nodes);
		calcDistanceFromNode(source, nodes, links);
		int cost = 0;
		for (int i = 0; i < nodes.size(); i++) {
			if((nodes.get(i)!= source)&&(nodes.get(i).getisSSASelected())) {
			cost += nodes.get(i).getDistance() * nodes.get(i).getImportance();
			source.setCost(cost);
			}
		}
		return nodes;
	}

	public LinkedList<Node> calcDistanceFromNode(Node source, LinkedList<Node> nodes, LinkedList<Link> links) {
		// Here we need to do a BFS starting from the source and then assign the field
		// "distance" on all other nodes to
		// represent the distance between that node and the source node which will be
		// passed as a parameter.
		clearVisited(nodes);
		Node current = new Node();
		LinkedList<Node> connectedNodes = new LinkedList<Node>();
		Queue<Node> q = new LinkedList<Node>();

		source.setDistance(0);
		source.setVisited(true);

		q.add(source);
		while (q.isEmpty() == false) {
			current = q.remove();
			connectedNodes = getConnectedNotVisited(current, links);
			for (int i = 0; i < connectedNodes.size(); i++) {
				connectedNodes.get(i).setDistance(current.getDistance() + 1);
				connectedNodes.get(i).setVisited(true);
				q.add(connectedNodes.get(i));
			}
		}
		return nodes;

	}

	public LinkedList<Node> modifyImportances(Node center, LinkedList<Node> nodes, LinkedList<Link> links) {
		// Here we need to modify the importance after placing a center on the node
		// called "center"
		clearDistances(nodes);
		calcDistanceFromNode(center, nodes, links);
		center.setImportance(0);
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i) != center) {
				int newImportance = nodes.get(i).getImportance()
						- (nodes.get(i).getImportance() / (nodes.get(i).getDistance() + 1));
				nodes.get(i).setImportance(newImportance);
			}
		}
		return nodes;
	}

	public static Node findMinCostNode(LinkedList<Node> nodes) {
		int currentMin = Integer.MAX_VALUE;
		int indexOfMin = -1;
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).getCost() < currentMin) {
				currentMin = nodes.get(i).getCost();
				indexOfMin = i;
			}
		}
		return nodes.get(indexOfMin);
	}

	public LinkedList<Node> getClosestCenter(Node source, LinkedList<Node> nodes, LinkedList<Link> links)
	{
		if(source.getIsCenter())
		{
			source.setResponsibleCenter(source);
			source.setDistanceToClosestCenter(0);
			return nodes;
		}
		source.setDistance(0);
		nodes = clearVisited(nodes);
		nodes = clearDistances(nodes);
		Node current;
		LinkedList<Node> connectedNodes = new LinkedList<Node>();
		Queue<Node> q = new LinkedList<Node>();
		q.add(source);
		while(q.isEmpty() == false) {
			current =q.remove();
			current.setVisited(true);
			connectedNodes = getConnectedNotVisited(current,links);
			for(int i=0; i<connectedNodes.size();i++) {
				connectedNodes.get(i).setDistance(current.getDistance()+1);
				if(connectedNodes.get(i).getIsCenter()==true) {
					source.setDistanceToClosestCenter(connectedNodes.get(i).getDistance());
					source.setResponsibleCenter(connectedNodes.get(i));
					return nodes;
				}
				else if(!q.contains(connectedNodes.get(i)))
				{
					q.add(connectedNodes.get(i));
				}
			}
		}
		return nodes;
	}
	
	public int generateRandom(int mean, int deviation) {
		int number;
		Random gen = new Random();
		number = (int) gen.nextGaussian();
		number *= deviation;
		number += mean;
		return number;
	}

	public LinkedList<Node> initializePacketsCounters(LinkedList<Node>nodes, int distribution) {
		//Generate 1000 packets at p1, 1000 packets at p2 and simulate their flow through the network
		//according to the subscribers' interests.
		int number =0;
		int powerMean = 50;
		int powerDeviation = 20;
		int tempMean = 15;
		int tempDeviation = 10;
		int packetsNumber = 1000;
		int uniformP1Range = 120;
		int uniformP2Range = 50;
		Random uniform = new Random();
		//Generate 1000 packets at p1 "power value" and forward them to the interested subscribers
		for(int i=0; i<packetsNumber; i++)
		{
			if(distribution == 1) {
			 number = generateRandom(powerMean, powerDeviation);}
			else if (distribution ==2) {
				number = uniform.nextInt(uniformP1Range);
			}
			 nodes.get(7).setPacketsHandled(nodes.get(7).getPacketsHandled()+1);
			 if((number >25)&&(number<100)) {
				 nodes.get(3).setPacketsHandled(nodes.get(3).getPacketsHandled()+1);
				 if(number<50) {
					 nodes.get(8).setPacketsHandled(nodes.get(8).getPacketsHandled()+1);
				 }
				 if(number >50) {
					 nodes.get(1).setPacketsHandled(nodes.get(1).getPacketsHandled()+1);
					 nodes.get(4).setPacketsHandled(nodes.get(4).getPacketsHandled()+1);
					 nodes.get(9).setPacketsHandled(nodes.get(9).getPacketsHandled()+1);
				 }
			 }
			 if(number >100) {
				 nodes.get(3).setPacketsHandled(nodes.get(3).getPacketsHandled()+1);
				 nodes.get(1).setPacketsHandled(nodes.get(1).getPacketsHandled()+1);
				 nodes.get(0).setPacketsHandled(nodes.get(0).getPacketsHandled()+1);
				 nodes.get(2).setPacketsHandled(nodes.get(2).getPacketsHandled()+1);
				 nodes.get(5).setPacketsHandled(nodes.get(5).getPacketsHandled()+1);
				 nodes.get(11).setPacketsHandled(nodes.get(11).getPacketsHandled()+1);
			 }
		}
		//Generate 1000 packets at p1 "temperature value" and forward them to the interested subscribers
		for(int i=0;i<packetsNumber;i++) {
			if(distribution ==1) {
			number = generateRandom(tempMean, tempDeviation);}
			else if(distribution ==2) {
				number = uniform.nextInt(uniformP2Range);
			}
			nodes.get(14).setPacketsHandled(nodes.get(14).getPacketsHandled()+1);
			nodes.get(6).setPacketsHandled(nodes.get(6).getPacketsHandled()+1);
			if((number>10)&&(number<30)) {
				nodes.get(13).setPacketsHandled(nodes.get(13).getPacketsHandled()+1);
			}
			if((number <10)||(number>40)){
				nodes.get(2).setPacketsHandled(nodes.get(2).getPacketsHandled()+1);
				if(number <10) {
					nodes.get(5).setPacketsHandled(nodes.get(5).getPacketsHandled()+1);
					nodes.get(12).setPacketsHandled(nodes.get(12).getPacketsHandled()+1);
				}
				if(number>40) {
					nodes.get(0).setPacketsHandled(nodes.get(0).getPacketsHandled()+1);
					nodes.get(1).setPacketsHandled(nodes.get(1).getPacketsHandled()+1);
					nodes.get(4).setPacketsHandled(nodes.get(4).getPacketsHandled()+1);
					nodes.get(10).setPacketsHandled(nodes.get(10).getPacketsHandled()+1);
				}
			}
		}
		return nodes;
	}

	public LinkedList<Node> initializeImportances(LinkedList<Node> nodes){
		// At the beginning the importance of the node is equal to the number of packets it handles
		for(int i=0;i<nodes.size();i++) {
			nodes.get(i).setImportance(nodes.get(i).getPacketsHandled());
		}
		return nodes;
	}

	public LinkedList<Double> generateResultReport(LinkedList<Node> nodes) {
		// revisit here
		Double totalHops = 0.0;
		Double totalPackets = 0.0;
		Double averageHopesRequiredPerPackets = 0.0;
		Double maxDistance =0.0;
		Double totalDistances = 0.0;
		Double averageDistance = 0.0;
		int numOfSSA=0;
		LinkedList<Double> results = new LinkedList<>();
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).getisSSASelected()) {
				numOfSSA++;
				totalPackets += nodes.get(i).getPacketsHandled();
				totalDistances += nodes.get(i).getDistanceToClosestCenter();
				if (nodes.get(i).getIsCenter() == false) {
					totalHops += nodes.get(i).getPacketsHandled() * nodes.get(i).getDistanceToClosestCenter();
				}
				if(nodes.get(i).getDistanceToClosestCenter()>maxDistance) {
					maxDistance = (double) nodes.get(i).getDistanceToClosestCenter();
				}
			}
		}
		averageHopesRequiredPerPackets = totalHops / totalPackets;
		averageDistance = totalDistances / numOfSSA;
		
		results.add(totalPackets);
		results.add(totalHops);
		results.add(averageHopesRequiredPerPackets);
		results.add(maxDistance);
		results.add(averageDistance);
		return results;
	}
	
	
	
	public LinkedList<Node> clearPacketCounters(LinkedList<Node> nodes){
		for(int i=0;i<nodes.size(); i++) {
			nodes.get(i).setPacketsHandled(0);
		}
		return nodes;
	}
	
	public LinkedList<Node> generatePackets(LinkedList<Node> nodes, int distribution){
		// Generate 1000 packets at p1, 1000 packets at p2 and simulate their flow
		// through the network
		// according to the subscribers' interests.
		int number =0;
		int powerMean = 50;
		int powerDeviation = 20;
		int tempMean = 15;
		int tempDeviation = 10;
		int packetsNumber = 1000;
		int uniformP1Range = 120;
		int uniformP2Range = 50;
		Random uniform = new Random();
		// Generate 1000 packets at p1 "power value" and forward them to the interested
		// subscribers
		for (int i = 0; i < packetsNumber; i++) {
			if(distribution ==1) {
			number = generateRandom(powerMean, powerDeviation);}
			else if(distribution ==2) {
				number = uniform.nextInt(uniformP1Range);
			}
			nodes.get(7).setPacketsHandled(nodes.get(7).getPacketsHandled() + 1);
			if ((nodes.get(7).getisSSASelected() == false)) {
				if ((number > 25) && (number < 100)) {
					nodes.get(3).setPacketsHandled(nodes.get(3).getPacketsHandled() + 1);
					if ((nodes.get(3).getisSSASelected() == false)) {
						if (number < 50) {
							nodes.get(8).setPacketsHandled(nodes.get(8).getPacketsHandled() + 1);
						}
						if (number > 50) {
							nodes.get(1).setPacketsHandled(nodes.get(1).getPacketsHandled() + 1);
							if ((nodes.get(1).getisSSASelected() == false))
								nodes.get(4).setPacketsHandled(nodes.get(4).getPacketsHandled() + 1);
							if ((nodes.get(4).getisSSASelected() == false))
								nodes.get(9).setPacketsHandled(nodes.get(9).getPacketsHandled() + 1);
						}
					}
				}
				if (number > 100) {
					nodes.get(3).setPacketsHandled(nodes.get(3).getPacketsHandled() + 1);
					if ((nodes.get(3).getisSSASelected() == false))
						nodes.get(1).setPacketsHandled(nodes.get(1).getPacketsHandled() + 1);
					if ((nodes.get(1).getisSSASelected() == false))
						nodes.get(0).setPacketsHandled(nodes.get(0).getPacketsHandled() + 1);
					if ((nodes.get(0).getisSSASelected() == false))
						nodes.get(2).setPacketsHandled(nodes.get(2).getPacketsHandled() + 1);
					if ((nodes.get(2).getisSSASelected() == false))
						nodes.get(5).setPacketsHandled(nodes.get(5).getPacketsHandled() + 1);
					if ((nodes.get(5).getisSSASelected() == false))
						nodes.get(11).setPacketsHandled(nodes.get(11).getPacketsHandled() + 1);
				}
			}
		}
		// Generate 1000 packets at p2 "temperature value" and forward them to the
		// interested subscribers
		for (int i = 0; i < packetsNumber; i++) {
			if(distribution ==1) {
			number = generateRandom(tempMean, tempDeviation);}
			else if(distribution ==2) {
				number = uniform.nextInt(uniformP2Range);
			}
			nodes.get(14).setPacketsHandled(nodes.get(14).getPacketsHandled() + 1);
			if ((nodes.get(14).getisSSASelected() == false)) {
				nodes.get(6).setPacketsHandled(nodes.get(6).getPacketsHandled() + 1);
				if ((nodes.get(6).getisSSASelected() == false)) {
					if ((number > 10) && (number < 30)) {
						nodes.get(13).setPacketsHandled(nodes.get(13).getPacketsHandled() + 1);
					}
					if ((number < 10) || (number > 40)) {
						nodes.get(2).setPacketsHandled(nodes.get(2).getPacketsHandled() + 1);
						if ((nodes.get(2).getisSSASelected() == false)) {
							if (number < 10) {
								nodes.get(5).setPacketsHandled(nodes.get(5).getPacketsHandled() + 1);
								if ((nodes.get(5).getisSSASelected() == false))
									nodes.get(12).setPacketsHandled(nodes.get(12).getPacketsHandled() + 1);
							}
							if (number > 40) {
								nodes.get(0).setPacketsHandled(nodes.get(0).getPacketsHandled() + 1);
								if ((nodes.get(0).getisSSASelected() == false)) {
									nodes.get(1).setPacketsHandled(nodes.get(1).getPacketsHandled() + 1);
									if ((nodes.get(1).getisSSASelected() == false))
										nodes.get(4).setPacketsHandled(nodes.get(4).getPacketsHandled() + 1);
									if ((nodes.get(4).getisSSASelected() == false))
										nodes.get(10).setPacketsHandled(nodes.get(10).getPacketsHandled() + 1);
								}
							}
						}
					}
				}
			}
		}
		return nodes;
	}

	public LinkedList<Node> initializePacketsCountersMedium(LinkedList<Node> nodes, int distribution){
		//Simulate the generation of 1000 packets at p1, 1000 packets at p2, and 1000 packets at p3
		//and simulate their flow through the network
		//according to the subscribers' interests.
		// First ask the user which distribution they want to have for the packets
		System.out.println("Choose 1 for normal distribution, 2 for uniform distribution of events: ");
		int number = 0;
		int powerMean = 25;
		int powerDeviation = 15;
		int T1Mean = 15;
		int T1Deviation = 10;
		int T2Mean = 25;
		int T2Deviation = 10;
		int uniformP1Range = 50;
		int uniformP2Range = 50;
		int uniformP3Range = 50;
		Random uniform = new Random();
		// at P1, generate 1000 P values and flow them through the network
		for(int i=0;i<1000; i++) {
			if(distribution == 1) {
			number = generateRandom(powerMean, powerDeviation);
			}
			else if (distribution ==2 ) {
				number = uniform.nextInt(uniformP1Range);
			}
			nodes.get(8).setPacketsHandled(nodes.get(8).getPacketsHandled()+1);
			nodes.get(6).setPacketsHandled(nodes.get(6).getPacketsHandled()+1);
			nodes.get(1).setPacketsHandled(nodes.get(1).getPacketsHandled()+1);
			nodes.get(5).setPacketsHandled(nodes.get(5).getPacketsHandled()+1);
			if((number >=20)&&(number<=30)) {
				nodes.get(9).setPacketsHandled(nodes.get(9).getPacketsHandled()+1);
			}
			if((number>=30)&&(number<=40)) {
				nodes.get(7).setPacketsHandled(nodes.get(7).getPacketsHandled()+1);
			}
		}
		// end of P1 section
		
		//at P2, generate 1000 T values and flow them through the network
		for(int i=0;i<1000;i++) {
			if(distribution ==1) {
			number = generateRandom(T1Mean, T1Deviation);
			}
			else if (distribution == 2) {
				number = uniform.nextInt(uniformP2Range);
			}
			
			nodes.get(13).setPacketsHandled(nodes.get(13).getPacketsHandled()+1);
			nodes.get(11).setPacketsHandled(nodes.get(11).getPacketsHandled()+1);
			nodes.get(3).setPacketsHandled(nodes.get(3).getPacketsHandled()+1);
			nodes.get(0).setPacketsHandled(nodes.get(0).getPacketsHandled()+1);
			nodes.get(2).setPacketsHandled(nodes.get(2).getPacketsHandled()+1);
			nodes.get(20).setPacketsHandled(nodes.get(20).getPacketsHandled()+1);
			nodes.get(4).setPacketsHandled(nodes.get(4).getPacketsHandled()+1);
			nodes.get(17).setPacketsHandled(nodes.get(17).getPacketsHandled()+1);
			nodes.get(16).setPacketsHandled(nodes.get(16).getPacketsHandled()+1);
			nodes.get(18).setPacketsHandled(nodes.get(18).getPacketsHandled()+1);
			if(number<0) {
				nodes.get(14).setPacketsHandled(nodes.get(14).getPacketsHandled()+1);
				nodes.get(24).setPacketsHandled(nodes.get(24).getPacketsHandled()+1);
			}
			if(number>40) {
				nodes.get(10).setPacketsHandled(nodes.get(10).getPacketsHandled()+1);
				nodes.get(22).setPacketsHandled(nodes.get(22).getPacketsHandled()+1);
			}
			if((number>=10)&&(number<=15)) {
				nodes.get(12).setPacketsHandled(nodes.get(12).getPacketsHandled()+1);
			}
			if(number<15) {
				nodes.get(15).setPacketsHandled(nodes.get(15).getPacketsHandled()+1);
			}
			if(number>15) {
				nodes.get(19).setPacketsHandled(nodes.get(19).getPacketsHandled()+1);
			}
			if((number>=10)&&(number<=20)) {
				nodes.get(23).setPacketsHandled(nodes.get(23).getPacketsHandled()+1);
			}
		}
		// end of P2 section
		
		//at P3, generate 1000 T values and flow them through the network
		for (int i=0; i<1000; i++) {
			if(distribution ==1) {
			number = generateRandom(T2Mean, T2Deviation);
			}
			else if(distribution==2) {
				number = uniform.nextInt(uniformP3Range);
			}
			nodes.get(18).setPacketsHandled(nodes.get(18).getPacketsHandled()+1);
			nodes.get(16).setPacketsHandled(nodes.get(16).getPacketsHandled()+1);
			nodes.get(4).setPacketsHandled(nodes.get(4).getPacketsHandled()+1);
			nodes.get(17).setPacketsHandled(nodes.get(17).getPacketsHandled()+1);
			nodes.get(0).setPacketsHandled(nodes.get(0).getPacketsHandled()+1);
			nodes.get(2).setPacketsHandled(nodes.get(2).getPacketsHandled()+1);
			nodes.get(20).setPacketsHandled(nodes.get(20).getPacketsHandled()+1);
			if(number >15) {
				nodes.get(19).setPacketsHandled(nodes.get(19).getPacketsHandled()+1);
			}
			if(number <15) {
				nodes.get(15).setPacketsHandled(nodes.get(15).getPacketsHandled()+1);
			}
			if( (number <0)||(number>40)|| ((number>=10)&&(number<=15)) ) {
				nodes.get(3).setPacketsHandled(nodes.get(3).getPacketsHandled()+1);
				if(number >40) {
					nodes.get(10).setPacketsHandled(nodes.get(10).getPacketsHandled()+1);
					nodes.get(22).setPacketsHandled(nodes.get(22).getPacketsHandled()+1);
				}
				if(number <0) {
					nodes.get(14).setPacketsHandled(nodes.get(14).getPacketsHandled()+1);
				}
				if((number>=10)&&(number<=15)) {
					nodes.get(12).setPacketsHandled(nodes.get(12).getPacketsHandled()+1);
				}
			}
			if( ((number>=10)&&(number<=20))||(number<0)){
				nodes.get(21).setPacketsHandled(nodes.get(21).getPacketsHandled()+1);
				if ((number>=10)&&(number<=20)) {
					nodes.get(23).setPacketsHandled(nodes.get(23).getPacketsHandled()+1);
				}
				if(number<0) {
					nodes.get(24).setPacketsHandled(nodes.get(24).getPacketsHandled()+1);
				}
			}
		}
		// end of P3 section
		
		return nodes;
	}
	
	public LinkedList<Node> generatePacketsMedium(LinkedList<Node> nodes, int distribution){
		// Generate 1000 packets at p1, 1000 packets at p2 and simulate their flow
		// through the network
		// according to the subscribers' interests and centers placement
		int number=0;
		int powerMean = 25;
		int powerDeviation = 15;
		int T1Mean = 15;
		int T1Deviation = 10;
		int T2Mean = 25;
		int T2Deviation = 10;
		int uniformP1Range = 50;
		int uniformP2Range = 50;
		int uniformP3Range = 50;
		Random uniform = new Random();
		int packetsNumber =2000;
		
		// generate 1000 packets at P1, forward them to the center or the interested subscribers
		for(int i=0; i<packetsNumber; i++) {
			if(distribution ==1)
			{
			number = generateRandom(powerMean, powerDeviation);
			}
			else if (distribution ==2)
			{
				number = uniform.nextInt(uniformP1Range);
			}
			nodes.get(8).setPacketsHandled(nodes.get(8).getPacketsHandled() + 1);
			if(nodes.get(8).getisSSASelected()== false) {
				nodes.get(6).setPacketsHandled(nodes.get(6).getPacketsHandled() + 1);
				if(nodes.get(6).getisSSASelected()== false) {
					nodes.get(1).setPacketsHandled(nodes.get(1).getPacketsHandled() + 1);
					if((number>=20)&&(number<=30)) {
						nodes.get(9).setPacketsHandled(nodes.get(9).getPacketsHandled() + 1);
					}
					if(nodes.get(1).getisSSASelected()== false) {
						nodes.get(5).setPacketsHandled(nodes.get(5).getPacketsHandled() + 1);
						if((number>=30)&&(number<=40)) {
							nodes.get(7).setPacketsHandled(nodes.get(7).getPacketsHandled() + 1);
						}
					}
				}
			}
		}
		// end of P1 section
		
		// generate 1000 packets at P2, forward them to the center or the interested subscribers
		for(int i=0;i<packetsNumber; i++) {
			if(distribution ==1) {
			number = generateRandom(T1Mean, T1Deviation);
			}
			else if (distribution ==2)
			{
				number = uniform.nextInt(uniformP2Range);
			}
			nodes.get(13).setPacketsHandled(nodes.get(13).getPacketsHandled() + 1);
			if(nodes.get(13).getisSSASelected()== false) {
				nodes.get(11).setPacketsHandled(nodes.get(11).getPacketsHandled() + 1);
				if(nodes.get(11).getisSSASelected()== false) {
					if(number<0) {
						nodes.get(14).setPacketsHandled(nodes.get(14).getPacketsHandled() + 1);
					}
					nodes.get(3).setPacketsHandled(nodes.get(3).getPacketsHandled() + 1);
					if(nodes.get(3).getisSSASelected()== false) {
						if(number>40) {
							nodes.get(10).setPacketsHandled(nodes.get(10).getPacketsHandled() + 1);
						}
						if((number>=10)&&(number<=15)) {
							nodes.get(12).setPacketsHandled(nodes.get(12).getPacketsHandled() + 1);
						}
						nodes.get(0).setPacketsHandled(nodes.get(0).getPacketsHandled() + 1);
						if(nodes.get(0).getisSSASelected()==false) {
							nodes.get(2).setPacketsHandled(nodes.get(2).getPacketsHandled() + 1);
							nodes.get(4).setPacketsHandled(nodes.get(4).getPacketsHandled() + 1);
							if(nodes.get(4).getisSSASelected()==false) {
								nodes.get(17).setPacketsHandled(nodes.get(17).getPacketsHandled() + 1);
								if(number<15) {
									nodes.get(15).setPacketsHandled(nodes.get(15).getPacketsHandled() + 1);
								}
								if(number>15) {
									nodes.get(19).setPacketsHandled(nodes.get(19).getPacketsHandled() + 1);
								}
							}
							if(nodes.get(2).getisSSASelected()==false) {
								nodes.get(20).setPacketsHandled(nodes.get(20).getPacketsHandled() + 1);
								if((number<0)||((number>=10)&&(number<=20))) {
									nodes.get(21).setPacketsHandled(nodes.get(21).getPacketsHandled() + 1);
									if(nodes.get(21).getisSSASelected()== false) {
										if(number<0) {
											nodes.get(24).setPacketsHandled(nodes.get(24).getPacketsHandled() + 1);
										}
										if((number>=10)&&(number<=20)) {
											nodes.get(23).setPacketsHandled(nodes.get(23).getPacketsHandled() + 1);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		// end of P2 section
		
		// generate 1000 packets at P2, forward them to the center or the interested subscribers
		for(int i=0; i<packetsNumber;i++) {
			if(distribution ==1)
			{
			number = generateRandom(T2Mean, T2Deviation);
			}
			else if (distribution ==2) {
				number = uniform.nextInt(uniformP3Range);
			}
			nodes.get(18).setPacketsHandled(nodes.get(18).getPacketsHandled() + 1);
			if (nodes.get(18).getisSSASelected() == false) {
				nodes.get(16).setPacketsHandled(nodes.get(16).getPacketsHandled() + 1);
				if (nodes.get(16).getisSSASelected() == false) {
					if (number > 15) {
						nodes.get(19).setPacketsHandled(nodes.get(19).getPacketsHandled() + 1);
					}
					nodes.get(4).setPacketsHandled(nodes.get(4).getPacketsHandled() + 1);
					if (nodes.get(4).getisSSASelected() == false) {
						if (number < 15) {
							nodes.get(15).setPacketsHandled(nodes.get(15).getPacketsHandled() + 1);
						}
						nodes.get(17).setPacketsHandled(nodes.get(17).getPacketsHandled() + 1);
						nodes.get(0).setPacketsHandled(nodes.get(0).getPacketsHandled() + 1);
						if (nodes.get(0).getisSSASelected() == false) {
							nodes.get(2).setPacketsHandled(nodes.get(2).getPacketsHandled() + 1);
							if (nodes.get(2).getisSSASelected() == false) {
								nodes.get(20).setPacketsHandled(nodes.get(20).getPacketsHandled() + 1);
								if (((number >= 10) && (number <= 20)) || (number < 0)) {
									nodes.get(21).setPacketsHandled(nodes.get(21).getPacketsHandled() + 1);
									if (nodes.get(21).getisSSASelected() == false) {
										if (number < 0) 
											nodes.get(24).setPacketsHandled(nodes.get(24).getPacketsHandled() + 1);
										if((number >= 10) && (number <= 20)) 
											nodes.get(23).setPacketsHandled(nodes.get(23).getPacketsHandled() + 1);
									}
								}
								if(number>40) 
									nodes.get(22).setPacketsHandled(nodes.get(22).getPacketsHandled() + 1);
							}
							if ((number>40)|| (number<0)|| ((number>=10)&&(number<=15))) {
								nodes.get(3).setPacketsHandled(nodes.get(3).getPacketsHandled() + 1);
								if(nodes.get(3).getisSSASelected()==false) {
									if(number>40)
										nodes.get(10).setPacketsHandled(nodes.get(10).getPacketsHandled() + 1);
									if((number>=10)&&(number<=15))
										nodes.get(12).setPacketsHandled(nodes.get(12).getPacketsHandled() + 1);
									if(number<0) {
										nodes.get(11).setPacketsHandled(nodes.get(11).getPacketsHandled() + 1);
										if(nodes.get(11).getisSSASelected()==false) {
											nodes.get(14).setPacketsHandled(nodes.get(14).getPacketsHandled() + 1);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return nodes;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
