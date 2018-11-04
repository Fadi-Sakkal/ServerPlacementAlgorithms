import java.io.IOException;
import java.util.*;

import org.fadi.placementalgorithm.KMedianServerPlacementAlgorithm;
import org.fadi.placementalgorithm.Link;
import org.fadi.placementalgorithm.Node;

public class Main {

	// The structure to hold the Nodes & Lines
	static LinkedList<Node> Nodes = new LinkedList<Node>();
	static LinkedList<Link> Links = new LinkedList<Link>();

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// Read the k value from the user
		Scanner in = new Scanner(System.in);
		System.out.printf("Enter k Value >= 1 :  ");
		int k = in.nextInt();
		while(k<1) {
			System.out.printf("Enter k Value >= 1 :  ");
			k = in.nextInt();
		}
		System.out.printf("Choose 1 for K-Median and 2 for K-Center");
		int Algorithm = in.nextInt();

		// Here initialing the topology and get the counters
		createNodes(10);
		createLinks();
		getPacketsCounters();

		if (Algorithm == 1) {
			// K Median
			// here is only for testing
			System.out.println("Here are the importances of the Nodes:");
			for (int i = 0; i < Nodes.size(); i++) {
				System.out.println(Nodes.get(i).getName() + " has importance of: " + Nodes.get(i).getImportance());
			}
			System.out.println("---------------- Now the Costs: ");
			Node newCenter;
			while (k > 0) {
				for (int i = 0; i < Nodes.size(); i++) {
					if (Nodes.get(i).getIsCenter() == false) {
						calculateCost(Nodes.get(i));
						System.out.println(Nodes.get(i).getCost());
					}
				}
				newCenter = findMinCostNode();

				System.out.println(newCenter.getName() + " is now a center.");
				System.out.println("New Iteration Now");
				System.out.println("-------------");
				newCenter.setIsCenter(true);
				modifyImportances(newCenter);
				System.out.println("Importances .......");
				for (int i = 0; i < Nodes.size(); i++) {
					System.out.println(Nodes.get(i).getName() + " has importance of " + Nodes.get(i).getImportance());
				}
				k--;
			}
		} else if (Algorithm == 2) {
			//Finding where servers should be placed
			calcDegrees();
			placeFirstKCenter();
			k--;
			while(k>0)
			{
				for(int i=0;i<Nodes.size(); i++)
				{
					if(Nodes.get(i).getIsCenter())
					{
						calcDistanceFromNodeForKCenter(Nodes.get(i));
						
						//break;
					}
				}
				placeCenter();
				k--;
			}
			// Finding Closest Center For each node and storing it in the field node.responsibleCenter
			// and the distance to that center will be stored in node.distanceToClosestCenter
			for(int i=0; i<Nodes.size();i++) {
				if(Nodes.get(i).getIsCenter() == false)
				getClosestCenter(Nodes.get(i));
			}
			printNodesInfo();
			for(int i=0; i<Nodes.size();i++) {
				if(Nodes.get(i).getIsCenter() == false && Nodes.get(i).getisSSASelected())
				System.out.println(Nodes.get(i).getName() + " its responsible center is: " + Nodes.get(i).getResponsibleCenter().getName() + " which is:" +Nodes.get(i).getDistanceToClosestCenter() +" hops away");
				else if(Nodes.get(i).getIsCenter())
					System.out.println(Nodes.get(i).getName() +" is a center");
			}
		} 
	}

	public static void createNodes(int numOfNodes) {
		// This method will create the passed number of nodes and add them to a list
		// called Nodes
		for (int i = 1; i <= 10; i++) {
			Node newNode = new Node();
			newNode.setName("S" + i);
			newNode.setImportance(i * 100);
			newNode.setisSSASelected(true);
			Nodes.add(newNode);
		}
		Nodes.get(3).setisSSASelected(false);
		Nodes.get(5).setisSSASelected(false);
	}

	public static void printNodesInfo() {
		for (int i = 0; i < Nodes.size(); i++) {
			System.out.println(Nodes.get(i).getName()+" Is ssa selected?"+ Nodes.get(i).getisSSASelected());
		}
	}

	public static void createLinks() {
		Link newLink;

		// S1--S2
		newLink = new Link();
		newLink.setNode1(Nodes.get(0));
		newLink.setNode2(Nodes.get(1));
		Links.add(newLink);

		// S1--S3
		newLink = new Link();
		newLink.setNode1(Nodes.get(0));
		newLink.setNode2(Nodes.get(2));
		Links.add(newLink);

		// S2--S4
		newLink = new Link();
		newLink.setNode1(Nodes.get(1));
		newLink.setNode2(Nodes.get(3));
		Links.add(newLink);

		// S2--S5
		newLink = new Link();
		newLink.setNode1(Nodes.get(1));
		newLink.setNode2(Nodes.get(4));
		Links.add(newLink);

		// S2--S6
		newLink = new Link();
		newLink.setNode1(Nodes.get(1));
		newLink.setNode2(Nodes.get(5));
		Links.add(newLink);
		

		// S3--S7
		newLink = new Link();
		newLink.setNode1(Nodes.get(2));
		newLink.setNode2(Nodes.get(6));
		Links.add(newLink);

		// S3--S8
		newLink = new Link();
		newLink.setNode1(Nodes.get(2));
		newLink.setNode2(Nodes.get(7));
		Links.add(newLink);


		// S3--S9
		newLink = new Link();
		newLink.setNode1(Nodes.get(2));
		newLink.setNode2(Nodes.get(8));
		Links.add(newLink);

		// S9--S10
		newLink = new Link();
		newLink.setNode1(Nodes.get(8));
		newLink.setNode2(Nodes.get(9));
		Links.add(newLink);
		
	}

	public static void printLinksInfo() {
		for (int i = 0; i < Links.size(); i++) {
			System.out.println(Links.get(i).getNode1().getName() + "----" + Links.get(i).getNode2().getName());
		}
	}

	public static void getPacketsCounters() {
		// Here we should query the controller to provide the number of packets handled
		// by each node. For now we will assign these values randomly
		Random random = new Random();
		for (int i = 0; i < Nodes.size(); i++) {
			if(Nodes.get(i).getisSSASelected() == true) {
			int randomNumber = random.nextInt(1000);
			Nodes.get(i).setPacketsHandled(randomNumber);
			Nodes.get(i).setImportance(randomNumber);
			}
			else
			{
				// if the node is not SSA selected then we will set its importance to 0 so that it will not affect the calculation
				Nodes.get(i).setPacketsHandled(0);
				Nodes.get(i).setImportance(0);
			}
		}
	}

	public static void printPacketsHandled() {
		for (int i = 0; i < Nodes.size(); i++) {
			System.out.println(Nodes.get(i).getPacketsHandled());
		}
	}

	public static void calculateCost(Node source) {
		// Here we calculate the cost of placing a server on the source node
		// This method should be called only after filling out the field "distance"
		// correctly using calcDistanceFromNode
		clearDistances();
		clearVisited();
		calcDistanceFromNode(source);
		int cost = 0;
		for (int i = 0; i < Nodes.size(); i++) {
			cost += Nodes.get(i).getDistance() * Nodes.get(i).getImportance();
			source.setCost(cost);
		}

	}

	public static LinkedList<Node> getConnectedNotVisited(Node source) {
		LinkedList<Node> connectedNodes = new LinkedList<Node>();
		for (int i = 0; i < Links.size(); i++) {
			if ((Links.get(i).getNode1() == source) && (Links.get(i).getNode2().getVisited() == false)) {
				connectedNodes.add(Links.get(i).getNode2());
			} else if ((Links.get(i).getNode2() == source) && (Links.get(i).getNode1().getVisited() == false)) {
				connectedNodes.add(Links.get(i).getNode1());
			}
		}

		return connectedNodes;
	}

	public static void calcDistanceFromNode(Node source) {
		// Here we need to do a BFS starting from the source and then assign the field
		// "distance" on all other nodes to
		// represent the distance between that node and the source node which will be
		// passed as a parameter.
		clearVisited();
		Node current = new Node();
		LinkedList<Node> connectedNodes = new LinkedList<Node>();
		Queue<Node> q = new LinkedList<Node>();

		source.setDistance(0);
		source.setVisited(true);

		q.add(source);
		while (q.isEmpty() == false) {
			current = q.remove();
			connectedNodes = getConnectedNotVisited(current);
			for (int i = 0; i < connectedNodes.size(); i++) {
				connectedNodes.get(i).setDistance(current.getDistance() + 1);
				connectedNodes.get(i).setVisited(true);
				q.add(connectedNodes.get(i));
			}
		}

	}

	public static void printDistances() {
		for (int i = 0; i < Nodes.size(); i++) {
			System.out.println(Nodes.get(i).getDistance());
		}
	}

	public static void clearDistances() {
		for (int i = 0; i < Nodes.size(); i++) {
			Nodes.get(i).setDistance(0);
		}
	}

	public static void clearVisited() {
		for (int i = 0; i < Nodes.size(); i++) {
			Nodes.get(i).setVisited(false);
		}

	}

	public static void modifyImportances(Node center) {
		// Here we need to modify the importances after placing a center on the node
		// called "center"
		clearDistances();
		calcDistanceFromNode(center);
		center.setImportance(0);
		for (int i = 0; i < Nodes.size(); i++) {
			if (Nodes.get(i) != center) {
				int newImportance = Nodes.get(i).getImportance()
						- (Nodes.get(i).getImportance() / (Nodes.get(i).getDistance() + 1));
				Nodes.get(i).setImportance(newImportance);
			}
		}
	}

	public static Node findMinCostNode() {
		int currentMin = Integer.MAX_VALUE;
		int indexOfMin = -1;
		for (int i = 0; i < Nodes.size(); i++) {
			if (Nodes.get(i).getCost() < currentMin) {
				currentMin = Nodes.get(i).getCost();
				indexOfMin = i;
			}
		}
		return Nodes.get(indexOfMin);
	}

	public static void sortAccordingToDegree() {
		Collections.sort(Nodes, new Comparator<Node>() {

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
	}

	public static void calcDegrees() {
		// loop the nodes
		for (int i = 0; i < Nodes.size(); i++) {
			// loop the links
			for (int j = 0; j < Links.size(); j++) {
				if ((Links.get(j).getNode1() == Nodes.get(i)) || (Links.get(j).getNode2() == Nodes.get(i))) {
					Nodes.get(i).setDegree(Nodes.get(i).getDegree() + 1);
				}
			}
		}
	}

	public static void calcDistanceFromNodeForKCenter(Node source)
	{
		 // calculate the distance between the passed node and all other nodes
        Node current = new Node();
        LinkedList<Node> ConnectedNodes = new LinkedList<Node>();
        Queue<Node> q = new LinkedList<Node>();
        q.add(source);
        while (q.isEmpty() == false)
        {
        	current = q.remove();
            ConnectedNodes = getConnectedNodes(current);
            for (int i = 0; i < ConnectedNodes.size(); i++)
            {
                if ((ConnectedNodes.get(i).getVisited()== false) && (ConnectedNodes.get(i).getDistanceToClosestCenter() > current.getDistanceToClosestCenter()+1))
                {
                    ConnectedNodes.get(i).setDistanceToClosestCenter(current.getDistanceToClosestCenter()+1);
                    ConnectedNodes.get(i).setVisited(true);
                    q.add(ConnectedNodes.get(i));
                }
            }
        }
        // Set the .visited field to false, for a new iteration (from a different center point of view)
        for (int i = 0; i < Nodes.size(); i++)
        {
            Nodes.get(i).setVisited(false);
        }
	}
	
	public static void placeCenter()
	{
		boolean success= false;
		int currentHighest = 0;
		int indexToPlaceCenter =-1;
		for(int i=0; i<Nodes.size(); i++)
		{
			if((Nodes.get(i).getDistanceToClosestCenter() > currentHighest) &&(Nodes.get(i).getisSSASelected())) {
				success=true;
				currentHighest = Nodes.get(i).getDistanceToClosestCenter();
				indexToPlaceCenter = i;
			}
		}
		if(success)
		{
			Nodes.get(indexToPlaceCenter).setIsCenter(true);
			Nodes.get(indexToPlaceCenter).setDistanceToClosestCenter(0);
		}
	}
	
	public static void placeFirstKCenter()
	{
		
		//Sort the nodes and put a server on the node which is ssa & has highest degree
		sortAccordingToDegree();
		for(int i=Nodes.size()-1;i>=0;i--)
		{
			if(Nodes.get(i).getisSSASelected()&& Nodes.get(i).getIsCenter()==false)
			{
				Nodes.get(i).setIsCenter(true);
				Nodes.get(i).setDistanceToClosestCenter(0);
				return;
			}
		}
		System.out.println("Error placing first center of K Center placement method!");
	}
	
	public static LinkedList<Node> getConnectedNodes(Node source)
	{
		LinkedList<Node> connectedNodes = new LinkedList<Node>();
		for(int i=0; i<Links.size(); i++)
		{
			if(Links.get(i).getNode1() == source) {
				connectedNodes.add(Links.get(i).getNode2());
			}
			else if (Links.get(i).getNode2() == source) {
				connectedNodes.add(Links.get(i).getNode1());
			}
		}
		return connectedNodes;
	}

	public static void getClosestCenter(Node source)
	{
		source.setDistance(0);
		clearVisited();
		clearDistances();
		Node current;
		LinkedList<Node> connectedNodes = new LinkedList<Node>();
		Queue<Node> q = new LinkedList<Node>();
		q.add(source);
		while(q.isEmpty() == false) {
			current =q.remove();
			current.setVisited(true);
			connectedNodes = getConnectedNotVisited(current);
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
