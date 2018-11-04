package org.fadi.placementalgorithm;
import java.util.LinkedList;
import java.util.Queue;

public class KCenterServerPlacementAlgorithm extends ServerPlacementAlgorithm {
	@Override
	public LinkedList<Node> execute(LinkedList<Node> nodes, LinkedList<Link> links, int k) {
		// TODO Auto-generated method stub
		calcDegrees(nodes, links);
		placeFirstKCenter(nodes);
		k--;
		while(k>0) {
			for(int i=0; i<nodes.size(); i++) {
				if((nodes.get(i).getIsCenter()== false)&&(nodes.get(i).getisSSASelected()))
				{
					getClosestCenter(nodes, links, nodes.get(i));
				}
			}
			nodes = placeCenter(nodes);
			k--;
		}
		for(int i=0; i< nodes.size(); i++) {
			getClosestCenter(nodes, links, nodes.get(i));
		}
		return nodes;
	}
	
	public LinkedList<Node> placeFirstKCenter(LinkedList<Node> nodes)
	{
		//Sort the nodes and put a server on the highest degree node which satisfy:
		//1- is ssa selected &
		//2- not already a center
		nodes = sortAccordingToDegree(nodes);
		for(int i=nodes.size()-1;i>=0;i--)
		{
			if(nodes.get(i).getisSSASelected()&& nodes.get(i).getIsCenter()==false)
			{
				nodes.get(i).setIsCenter(true);
				nodes.get(i).setDistanceToClosestCenter(0);
				return nodes;
			}
		}
		System.out.println("Error placing first center of K Center placement method!");
		return nodes;
	}

	public LinkedList<Node> calcDistanceFromNodeForKCenter(Node source, LinkedList<Node> nodes, LinkedList<Link> links)
	{
		// Set the .visited field to false, to start correctly.
        for (int i = 0; i < nodes.size(); i++)
        {
            nodes.get(i).setVisited(false);
        }
		 // BFS to calculate the distance between the passed node and all other nodes
        Node current = new Node();
        LinkedList<Node> connectedNodes = new LinkedList<Node>();
        Queue<Node> q = new LinkedList<Node>();
        q.add(source);
        while (q.isEmpty() == false)
        {
        	current = q.remove();
        	connectedNodes = getConnectedNodes(current, nodes, links);
            for (int i = 0; i < connectedNodes.size(); i++)
            {
                if ((connectedNodes.get(i).getVisited()== false) && (connectedNodes.get(i).getDistanceToClosestCenter() > current.getDistanceToClosestCenter()+1))
                {
                	connectedNodes.get(i).setDistanceToClosestCenter(current.getDistanceToClosestCenter()+1);
                	connectedNodes.get(i).setVisited(true);
                    q.add(connectedNodes.get(i));
                }
            }
        }
        return nodes;
        
	}
	
	public  LinkedList<Node> getConnectedNodes(Node source, LinkedList<Node> nodes, LinkedList<Link> links)
	{
		LinkedList<Node> connectedNodes = new LinkedList<Node>();
		for(int i=0; i<links.size(); i++)
		{
			if(links.get(i).getNode1() == source) {
				connectedNodes.add(links.get(i).getNode2());
			}
			else if (links.get(i).getNode2() == source) {
				connectedNodes.add(links.get(i).getNode1());
			}
		}
		return connectedNodes;
	}

	public LinkedList<Node> placeCenter(LinkedList<Node> nodes)
	{
		boolean success= false;
		int currentHighest = 0;
		int indexToPlaceCenter =-1;
		for(int i=0; i<nodes.size(); i++)
		{
			if((nodes.get(i).getDistanceToClosestCenter() > currentHighest) &&(nodes.get(i).getisSSASelected())) {
				success=true;
				currentHighest = nodes.get(i).getDistanceToClosestCenter();
				indexToPlaceCenter = i;
			}
		}
		if(success)
		{
			nodes.get(indexToPlaceCenter).setIsCenter(true);
			nodes.get(indexToPlaceCenter).setDistanceToClosestCenter(0);
		}
		else
		{
			System.out.println("Unable to place center");
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

	public static void getClosestCenter(LinkedList<Node> nodes, LinkedList<Link> links ,Node source)
	{
		source.setDistance(0);
		clearVisited(nodes);
		clearDistances(nodes);
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
					return;
				}
				else if(!q.contains(connectedNodes.get(i)))
				{
					q.add(connectedNodes.get(i));
				}
			}
		}
		
	}

}
