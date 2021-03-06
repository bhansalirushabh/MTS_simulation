package edu.gatech;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class BusSystem {
    private HashMap<Integer, BusStop> stops;
    private HashMap<Integer, BusRoute> routes;
    private HashMap<Integer, Bus> buses;

    public BusSystem() {
        stops = new HashMap<Integer, BusStop>();
        routes = new HashMap<Integer, BusRoute>();
        buses = new HashMap<Integer, Bus>();
    }

    public BusStop getStop(int stopID) {
        if (stops.containsKey(stopID)) { return stops.get(stopID); }
        return null;
    }

    public BusRoute getRoute(int routeID) {
        if (routes.containsKey(routeID)) { return routes.get(routeID); }
        return null;
    }

    public Bus getBus(int busID) {
        if (buses.containsKey(busID)) { return buses.get(busID); }
        return null;
    }

    //makeStop without riders
    public int makeStop(int uniqueID, String inputName,  double inputXCoord, double inputYCoord) {
        // int uniqueID = stops.size();
        stops.put(uniqueID, new BusStop(uniqueID, inputName, inputXCoord, inputYCoord));
        return uniqueID;
    }

    //makestop with List of Rider objects
    public int makeStop(int uniqueID, String inputName, double inputXCoord, double inputYCoord, List<Rider> riders) {
        // int uniqueID = stops.size();
        stops.put(uniqueID, new BusStop(uniqueID, inputName, inputXCoord, inputYCoord, riders));
        return uniqueID;
    }

    //do we need a method to create random rider objects at each stop ?


    public int makeRoute(int uniqueID, int inputNumber, String inputName) {
        // int uniqueID = routes.size();
        routes.put(uniqueID, new BusRoute(uniqueID, inputNumber, inputName));
        return uniqueID;
    }

    public int makeBus(int uniqueID, int inputRoute, int inputLocation, int inputCapacity, int inputSpeed) {
        // int uniqueID = buses.size();
        buses.put(uniqueID, new Bus(uniqueID, inputRoute, inputLocation, inputCapacity, inputSpeed));
        return uniqueID;
    }

    public void appendStopToRoute(int routeID, int nextStopID, int avgTravelTime) { routes.get(routeID).addNewStop(nextStopID, avgTravelTime); }

    public HashMap<Integer, BusStop> getStops() { return stops; }

    public HashMap<Integer, BusRoute> getRoutes() { return routes; }

    public HashMap<Integer, Bus> getBuses() { return buses; }
    
    public void displayModel() {
    	ArrayList<MiniPair> busNodes, stopNodes;
    	MiniPairComparator compareEngine = new MiniPairComparator();
    	
    	int[] colorScale = new int[] {9, 29, 69, 89, 101};
    	String[] colorName = new String[] {"#000077", "#0000FF", "#000000", "#770000", "#FF0000"};
    	Integer colorSelector, colorCount, colorTotal;
    	
    	try{
            // create new file access path
            String path="./mts_bus_digraph.dot";
            File file = new File(path);

            // create the file if it doesn't exist
            if (!file.exists()) { file.createNewFile();}

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            
            bw.write("digraph G\n");
            bw.write("{\n");
    	
            busNodes = new ArrayList<MiniPair>();
            /*
            TODO: Discuss with team
            */
            for (Bus b: buses.values()) { busNodes.add(new MiniPair(b.getID(), b.getNumberOfPassengers(), b.getSimulationTime())); }
            Collections.sort(busNodes, compareEngine);


            colorSelector = 0;
            colorCount = 0;
            colorTotal = busNodes.size();
            for (MiniPair c: busNodes) {
            	if (((int) (colorCount++ * 100.0 / colorTotal)) > colorScale[colorSelector]) { colorSelector++; }
               // System.out.println("  bus" + c.getID() + " [ label=\"train#" + c.getID() + " | " + c.getValue() + " riding | time is "+c.getTime()+"\", color=\"#000077 \"];\n");
            	bw.write("  bus" + c.getID() + " [ label=\"bus#" + c.getID() + " | " + c.getValue() + " riding| time is "+c.getTime()+"\", color=\"#0f8417\"];\n");
            }
            bw.newLine();
            
            stopNodes = new ArrayList<MiniPair>();
            /*
            TODO: Check with team
            */
            for (BusStop s: stops.values()) { stopNodes.add(new MiniPair(s.getID(), s.getNumberOfRiders())); }
            Collections.sort(stopNodes, compareEngine);

            colorSelector = 0;
            colorCount = 0;
            colorTotal = stopNodes.size();
            for (MiniPair t: stopNodes) {
            	if (((int) (colorCount++ * 100.0 / colorTotal)) > colorScale[colorSelector]) { colorSelector++; }
            	bw.write("  stop" + t.getID() + " [ label=\"stop#" + t.getID() + " | " + t.getValue() + " waiting\", color=\"#FF0000\"];\n");
            }
            bw.newLine();
            
            for (Bus m: buses.values()) {
            	Integer prevStop = routes.get(m.getRouteID()).getStopID(m.getPastLocation());
            	Integer nextStop = routes.get(m.getRouteID()).getStopID(m.getLocation());
            	bw.write("  stop" + Integer.toString(prevStop) + " -> bus" + Integer.toString(m.getID()) + " [ label=\" dep\" ];\n");
            	bw.write("  bus" + Integer.toString(m.getID()) + " -> stop" + Integer.toString(nextStop) + " [ label=\" arr\" ];\n");
            }
    	
            bw.write("}\n");
            bw.close();
    	} catch (Exception e) {
    		System.out.println(e);
    	}
    }
}
