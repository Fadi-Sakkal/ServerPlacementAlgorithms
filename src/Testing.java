import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

import org.fadi.placementalgorithm.KCenterServerPlacementAlgorithm;
import org.fadi.placementalgorithm.KMedianServerPlacementAlgorithm;
import org.fadi.placementalgorithm.Link;
import org.fadi.placementalgorithm.Node;
import org.fadi.placementalgorithm.RandomPlacementAlgorithm;
import org.fadi.placementalgorithm.UtilitarianPlacementAlgorithm;

public class Testing {
	
	static LinkedList<Node> Nodes = new LinkedList<Node>();
	static LinkedList<Link> Links = new LinkedList<Link>();
	
	public static void main(String[] args) throws IOException {

		// Read the k value from the user
		Scanner in = new Scanner(System.in);
		System.out.printf("Enter k Value >= 1 :  ");
		int k = in.nextInt();
		while(k<1) {
			System.out.printf("Enter k Value >= 1 :  ");
			k = in.nextInt();
		}
		System.out.printf("Choose 1 for K-Median, 2 for K-Center, 3 for random placement , 4 for utilitarian placement : ");
		int Algorithm = in.nextInt();
		int topology =0;
		while((topology != 1)&&(topology !=2)) {
		System.out.printf("Choose 1 for small topology (15 nodes), 2 for medium topology (25 nodes): ");
		topology = in.nextInt();
		}
		
		if(topology ==1) {
		// Here initialing the small topology 
		Nodes = createNodes(15);
		Links = createLinks(Nodes);
		}
		else if (topology ==2) {
			//Here initializing the medium topology
			Nodes= creatNodesMedium();
			Links = createLinksMedium(Nodes);
		}
		
		
		if(Algorithm ==2) {
		// K center execution
		KCenterServerPlacementAlgorithm algo2 = new KCenterServerPlacementAlgorithm();
		Nodes = algo2.execute(Nodes, Links, k);
		// Print Centers info
		printCentersInfo(Nodes);
		
		// Print non-centers info
		printNonCentersInfo(Nodes);
		
		LinkedList<Double> simulationResult = new LinkedList<>();
		simulationResult = algo2.generateResultReport(Nodes);
		System.out.println("Result Overview:");
		System.out.println("----");
		System.out.println("Max Distance Between an SSA and a Center " +  simulationResult.get(0));
		System.out.println("Average Distance Between an SSA node and its closest Center " + simulationResult.get(1)) ;
		
		}
		else if(Algorithm ==1)
		{
			//K Median
			KMedianServerPlacementAlgorithm algo1 = new KMedianServerPlacementAlgorithm();
			Nodes = (LinkedList<Node>) algo1.execute(Nodes, Links, k);
			for(int i =0; i<Nodes.size(); i++) {
				if(Nodes.get(i).getIsCenter())
				{
					System.out.println(Nodes.get(i).getName() +" is a center");
				}
				else {
				System.out.println(Nodes.get(i).getName() + " is center? " + Nodes.get(i).getIsCenter() +" its responsible center is " + Nodes.get(i).getResponsibleCenter().getName()+ " is " + Nodes.get(i).getDistanceToClosestCenter()+" hops away");
			}
			}
			
			Nodes = algo1.simulate(Nodes);
			LinkedList<Double> simulationResult = new LinkedList<>();
			simulationResult = algo1.generateResultReport(Nodes);
			System.out.println("results:");
			System.out.println("----");
			System.out.println("Total Packets Sent To Centers: " +  simulationResult.get(0).intValue());
			System.out.println("Total Hops Executed By These Packets: " + simulationResult.get(1).intValue()) ;
			System.out.println("In average every packet sent to a center needs : "+ simulationResult.get(2) + " Hops to Reach its Responsible Center");
			System.out.println("Max distance between a SSA and its center is : " + simulationResult.get(3));
			System.out.println("Average distance between a SSA and its Center is :"+ simulationResult.get(4));
		}
		else if (Algorithm ==3) {
			// Random placement
			RandomPlacementAlgorithm algo3 = new RandomPlacementAlgorithm();
			Nodes = (LinkedList<Node>) algo3.execute(Nodes,Links, k);
			// Centers Info
			printCentersInfo(Nodes);
			// Nodes Info:
			printNonCentersInfo(Nodes);
			
			LinkedList<Double> simulationResult = new LinkedList<>();
			simulationResult = algo3.generateResultReport(Nodes);
			System.out.println("results:");
			System.out.println("----");
			System.out.println("Max Distance Between an SSA and a Center " +  simulationResult.get(0));
			System.out.println("Average Distance Between an SSA and a Center " + simulationResult.get(1)) ;
		}
		else if (Algorithm == 4) {
			// Utilitarian placement
			UtilitarianPlacementAlgorithm algo4 = new UtilitarianPlacementAlgorithm();
			Nodes = (LinkedList<Node>) algo4.execute(Nodes, Links, k);
			for(int i =0; i<Nodes.size(); i++) {
				if(Nodes.get(i).getIsCenter())
				{
					System.out.println(Nodes.get(i).getName() +" is a center");
				}
				else {
				System.out.println(Nodes.get(i).getName() + " is center? " + Nodes.get(i).getIsCenter() +" its responsible center is " + Nodes.get(i).getResponsibleCenter().getName()+ " is " + Nodes.get(i).getDistanceToClosestCenter()+" hops away");
			}
			}
			
			Nodes = algo4.simulate(Nodes);
			LinkedList<Double> simulationResult = new LinkedList<>();
			simulationResult = algo4.generateResultReport(Nodes);
			System.out.println("results:");
			System.out.println("----");
			System.out.println("Total Packets Sent To Centers: " +  simulationResult.get(0).intValue());
			System.out.println("Total Hops Executed By These Packets: " + simulationResult.get(1).intValue()) ;
			System.out.println("In average every packet sent to a center needs : "+ simulationResult.get(2) + " Hops to Reach its Responsible Center");
			System.out.println("Max distance between a SSA and its center is : " + simulationResult.get(3));
			System.out.println("Average distance between a SSA and its Center is :"+ simulationResult.get(4));
		}
	}
	
	public static LinkedList<Node> createNodes(int numOfNodes) {
		// This method will create the passed number of nodes and add them to a list
		// called Nodes
		for (int i = 1; i <= numOfNodes; i++) {
			Node newNode = new Node();
			newNode.setName("S" + i);
			newNode.setisSSASelected(true);
			Nodes.add(newNode);
		}
		Nodes.get(14).setisSSASelected(false);
		Nodes.get(7).setisSSASelected(false);
		Nodes.get(6).setisSSASelected(false);
		Nodes.get(3).setisSSASelected(false);
		return Nodes;
	}

	public static void printNodesInfo() {
		for (int i = 0; i < Nodes.size(); i++) {
			System.out.println(Nodes.get(i).getName());
		}
	}

	public static LinkedList<Link> createLinks(LinkedList<Node> nodes) {
		Link newLink;
		LinkedList<Link> links = new LinkedList<Link>();

		// S1--S2
		newLink = new Link();
		newLink.setNode1(nodes.get(0));
		newLink.setNode2(nodes.get(1));
		links.add(newLink);

		// S1--S3
		newLink = new Link();
		newLink.setNode1(nodes.get(0));
		newLink.setNode2(nodes.get(2));
		links.add(newLink);

		// S2--S4
		newLink = new Link();
		newLink.setNode1(nodes.get(1));
		newLink.setNode2(nodes.get(3));
		links.add(newLink);

		// S2--S5
		newLink = new Link();
		newLink.setNode1(nodes.get(1));
		newLink.setNode2(nodes.get(4));
		links.add(newLink);
		
		// S3--S6
		newLink = new Link();
		newLink.setNode1(nodes.get(2));
		newLink.setNode2(nodes.get(5));
		links.add(newLink);
		
		// S3--S7
		newLink = new Link();
		newLink.setNode1(nodes.get(2));
		newLink.setNode2(nodes.get(6));
		links.add(newLink);

		// S4--S8
		newLink = new Link();
		newLink.setNode1(nodes.get(3));
		newLink.setNode2(nodes.get(7));
		links.add(newLink);

		// S4--S9
		newLink = new Link();
		newLink.setNode1(nodes.get(3));
		newLink.setNode2(nodes.get(8));
		links.add(newLink);
		
		
		// S5--S10
		newLink = new Link();
		newLink.setNode1(nodes.get(4));
		newLink.setNode2(nodes.get(9));
		links.add(newLink);
		
		// S5--S11
		newLink = new Link();
		newLink.setNode1(nodes.get(4));
		newLink.setNode2(nodes.get(10));
		links.add(newLink);
		
		// S6--S12
		newLink = new Link();
		newLink.setNode1(nodes.get(5));
		newLink.setNode2(nodes.get(11));
		links.add(newLink);
		
		// S6--S13
		newLink = new Link();
		newLink.setNode1(nodes.get(5));
		newLink.setNode2(nodes.get(12));
		links.add(newLink);
		
		// S7--S14
		newLink = new Link();
		newLink.setNode1(nodes.get(6));
		newLink.setNode2(nodes.get(13));
		links.add(newLink);
		
		// S7--S15
		newLink = new Link();
		newLink.setNode1(nodes.get(6));
		newLink.setNode2(nodes.get(14));
		links.add(newLink);		
				
		return links;
	}
	
	public static LinkedList<Node> creatNodesMedium(){
		// This will create a list of 25 nodes, then choose some of them to be non SSA
		// selected
		for (int i = 1; i <= 25; i++) {
			Node newNode = new Node();
			newNode.setName("S" + i);
			newNode.setisSSASelected(true);
			Nodes.add(newNode);
		}
		// set some node to be non SSA
		Nodes.get(8).setisSSASelected(false);
		Nodes.get(6).setisSSASelected(false);
		Nodes.get(1).setisSSASelected(false);
		Nodes.get(13).setisSSASelected(false);
		Nodes.get(18).setisSSASelected(false);
		return Nodes;
	}

	public static LinkedList<Link> createLinksMedium(LinkedList<Node> nodes){
		Link newLink;
		LinkedList<Link> links = new LinkedList<Link>();
		
		// S1--S2
		newLink = new Link();
		newLink.setNode1(nodes.get(0));
		newLink.setNode2(nodes.get(1));
		links.add(newLink);
		
		// S1--S3
		newLink = new Link();
		newLink.setNode1(nodes.get(0));
		newLink.setNode2(nodes.get(2));
		links.add(newLink);
		
		// S1--S4
		newLink = new Link();
		newLink.setNode1(nodes.get(0));
		newLink.setNode2(nodes.get(3));
		links.add(newLink);
		
		// S1--S5
		newLink = new Link();
		newLink.setNode1(nodes.get(0));
		newLink.setNode2(nodes.get(4));
		links.add(newLink);
		
		// S2--S6
		newLink = new Link();
		newLink.setNode1(nodes.get(1));
		newLink.setNode2(nodes.get(5));
		links.add(newLink);
		
		// S2--S7
		newLink = new Link();
		newLink.setNode1(nodes.get(1));
		newLink.setNode2(nodes.get(6));
		links.add(newLink);
		
		// S2--S8
		newLink = new Link();
		newLink.setNode1(nodes.get(1));
		newLink.setNode2(nodes.get(7));
		links.add(newLink);
				
		// S7--S9
		newLink = new Link();
		newLink.setNode1(nodes.get(6));
		newLink.setNode2(nodes.get(8));
		links.add(newLink);		
		
		// S7--S10
		newLink = new Link();
		newLink.setNode1(nodes.get(6));
		newLink.setNode2(nodes.get(9));
		links.add(newLink);
		
		// S4--S11
		newLink = new Link();
		newLink.setNode1(nodes.get(3));
		newLink.setNode2(nodes.get(10));
		links.add(newLink);
		
		// S4--S12
		newLink = new Link();
		newLink.setNode1(nodes.get(3));
		newLink.setNode2(nodes.get(11));
		links.add(newLink);
		
		// S4--S13
		newLink = new Link();
		newLink.setNode1(nodes.get(3));
		newLink.setNode2(nodes.get(12));
		links.add(newLink);
		
		// S12--S14
		newLink = new Link();
		newLink.setNode1(nodes.get(11));
		newLink.setNode2(nodes.get(13));
		links.add(newLink);
		
		// S12--S15
		newLink = new Link();
		newLink.setNode1(nodes.get(11));
		newLink.setNode2(nodes.get(14));
		links.add(newLink);
		
		// S5--S16
		newLink = new Link();
		newLink.setNode1(nodes.get(4));
		newLink.setNode2(nodes.get(15));
		links.add(newLink);
		
		
		// S5--S17
		newLink = new Link();
		newLink.setNode1(nodes.get(4));
		newLink.setNode2(nodes.get(16));
		links.add(newLink);
		
		// S5--S18
		newLink = new Link();
		newLink.setNode1(nodes.get(4));
		newLink.setNode2(nodes.get(17));
		links.add(newLink);
		
		// S17--S19
		newLink = new Link();
		newLink.setNode1(nodes.get(16));
		newLink.setNode2(nodes.get(18));
		links.add(newLink);
		
		// S17--S20
		newLink = new Link();
		newLink.setNode1(nodes.get(16));
		newLink.setNode2(nodes.get(19));
		links.add(newLink);
		
		// S3--S21
		newLink = new Link();
		newLink.setNode1(nodes.get(2));
		newLink.setNode2(nodes.get(20));
		links.add(newLink);
		
		// S3--S22
		newLink = new Link();
		newLink.setNode1(nodes.get(2));
		newLink.setNode2(nodes.get(21));
		links.add(newLink);
		
		// S3--S23
		newLink = new Link();
		newLink.setNode1(nodes.get(2));
		newLink.setNode2(nodes.get(22));
		links.add(newLink);
		
		// S22--S24
		newLink = new Link();
		newLink.setNode1(nodes.get(21));
		newLink.setNode2(nodes.get(23));
		links.add(newLink);
				
		// S22--S25
		newLink = new Link();
		newLink.setNode1(nodes.get(21));
		newLink.setNode2(nodes.get(24));
		links.add(newLink);
		
		return links;
	}

	public static void printCentersInfo(LinkedList<Node> nodes) {
		System.out.println("Centers :");
		for(int i=0;i<Nodes.size();i++) {
			if(Nodes.get(i).getIsCenter()) {
				System.out.println(Nodes.get(i).getName());
			}
		}
		System.out.println("---------");
	}
	
	public static void printNonCentersInfo(LinkedList<Node> nodes) {
		for (int i = 0; i < Nodes.size(); i++) {
			if (Nodes.get(i).getIsCenter() == false) {
				if (Nodes.get(i).getisSSASelected()) {
					System.out.println(Nodes.get(i).getName() + " is SSA selected, its responsible center is: "
							+ Nodes.get(i).getResponsibleCenter().getName() + " is: "
							+ Nodes.get(i).getDistanceToClosestCenter() + " hops away");
				} else {
					System.out.println(Nodes.get(i).getName() + " is not SSA selected");
				}
			}
		}
	}
}
