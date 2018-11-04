package org.fadi.placementalgorithm;

public class Node {
  private String name;
  private int cost;
  private int importance;
  private boolean isCenter;
  private int packetsHandled =0;
  private boolean Visited = false;
  private int distance = 0;
  private int degree=0;
  private boolean isSSASelected;
  private int distanceToClosestCenter = 16000;
  private Node responsibleCenter;
  
  public String getName()
  {
	  return name;
  }
  public void setName(String name) 
  {
	  this.name =name;
  }
  public int getImportance()
  {
	  return importance;
  }
  public void setImportance(int importance)
  {
	  this.importance = importance;
  }
  public boolean getIsCenter()
  {
	  return isCenter;
  }
  public void setIsCenter(boolean isCenter)

  {
	  this.isCenter = isCenter;
  }
  public int getCost()
  {
	  return cost;
  }
  public void setCost(int cost)
  {
	  this.cost = cost;
  }
  public int getPacketsHandled() {
	return packetsHandled;
}
  public void setPacketsHandled(int packetsHandled) {
	this.packetsHandled = packetsHandled;
}
  public boolean getVisited() {
	return Visited;
}
  public void setVisited(boolean visited) {
	Visited = visited;
}
  public int getDistance() {
	return distance;
}
  public void setDistance(int distance) {
	this.distance = distance;
}
public int getDegree() {
	return degree;
}
public void setDegree(int degree) {
	this.degree = degree;
}
public boolean getisSSASelected() {
	return isSSASelected;
}
public void setisSSASelected(boolean isSSASelected) {
	this.isSSASelected = isSSASelected;
}
public int getDistanceToClosestCenter() {
	return distanceToClosestCenter;
}
public void setDistanceToClosestCenter(int distanceToClosestCenter) {
	this.distanceToClosestCenter = distanceToClosestCenter;
}
public Node getResponsibleCenter() {
	return responsibleCenter;
}
public void setResponsibleCenter(Node responsibleCenter) {
	this.responsibleCenter = responsibleCenter;
}
}
