package org.fadi.placementalgorithm;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class ServerPlacementAlgorithm {
	public abstract List<Node> execute(LinkedList<Node> nodes, LinkedList<Link> links, int k);

	
	
protected static LinkedList<Node> clearDistances(LinkedList<Node> nodes) {
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).setDistance(0);
		}
		return nodes;
	}

	protected static LinkedList<Node> clearVisited(LinkedList<Node> nodes) {
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).setVisited(false);
		}
		return nodes;
	}

	protected static LinkedList<Node> getConnectedNotVisited(Node source, LinkedList<Link> links) {
		LinkedList<Node> connectedNodes = new LinkedList<Node>();
		for (int i = 0; i < links.size(); i++) {
			if ((links.get(i).getNode1() == source) && (links.get(i).getNode2().getVisited() == false)) {
				connectedNodes.add(links.get(i).getNode2());
			} else if ((links.get(i).getNode2() == source) && (links.get(i).getNode1().getVisited() == false)) {
				connectedNodes.add(links.get(i).getNode1());
			}
		}

		return connectedNodes;
	}

	protected static LinkedList<Node> sortAccordingToDegree(LinkedList<Node> nodes) {
		Collections.sort(nodes, new Comparator<Node>() {

			@Override
			public int compare(Node o1, Node o2) {
				// TODO Auto-generated method stub
				if (o1.getDegree() > o2.getDegree()) {
					return 1;
				} else if (o1.getDegree() < o2.getDegree()) {
					return -1;
				}
				return 0;
			}
		});
		return nodes;
	}

	protected static void calcDegrees(LinkedList<Node> nodes, LinkedList<Link> links) {
		// loop the nodes
		for (int i = 0; i < nodes.size(); i++) {
			// loop the links
			for (int j = 0; j < links.size(); j++) {
				if ((links.get(j).getNode1() == nodes.get(i)) || (links.get(j).getNode2() == nodes.get(i))) {
					nodes.get(i).setDegree(nodes.get(i).getDegree() + 1);
				}
			}
		}
	}

	protected static void printLinksInfo(LinkedList<Link> links) {
		for (int i = 0; i < links.size(); i++) {
			System.out.println(links.get(i).getNode1().getName() + "----" + links.get(i).getNode2().getName());
		}
	}

	protected LinkedList<Node> getClosestCenter(Node source, LinkedList<Node> nodes, LinkedList<Link> links)
	{
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
}
