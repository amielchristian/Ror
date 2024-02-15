/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modules;

import com.google.gson.stream.JsonReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Ivan
 */
public class DFALex {
    State startState;     
    HashMap<String, State> map = new HashMap<>();
    
    public DFALex(String filename) {
        getStateFromJSON(filename);
        this.startState = map.get("q0");
    }
    
    public boolean run(String input) {
        boolean match = false;
        
        State curr = this.startState;
        for (int i = 0; i < input.length(); i++) {
           
            String c = String.valueOf(input.charAt(i));
            
            curr = map.get(curr.goToNext(c));
            
	    if (curr == null) {
                System.out.println("Did not arrive at accepting state");
		break;
	    }
            
            if (curr.isNullState) {
                System.out.println("Arrived at Null state");
                break;
            }
            
	}
        
	if (curr != null && curr.isAcceptState) {
            System.out.println("Input Token: " + input + ", Token: " + curr.token);
	    match = true;
	} else {
            System.out.println("Did not arrive at accepting state");
        }
        

	return match;
    }
    
    void getStateFromJSON(String filename) {
        try {
            JsonReader reader = new JsonReader(new FileReader("dfa.json"));
            reader.beginArray();
                while (reader.hasNext()) {
                    String name = "";
                    String stateName = "";
                    ArrayList<Edge> edges = new ArrayList<>();
                    boolean isAcceptState = false;
                    boolean isNullState = false;
                    String token = "";
                    reader.beginObject();
                    while (reader.hasNext()) {
                        name = reader.nextName();
                        if (name.equals("name")) {
                            stateName = reader.nextString();
//                            System.out.println("name: " + stateName);
             
                        } else if (name.equals("type")) {
                            String type = reader.nextString();
//                            System.out.println("type: " + type);
                            isAcceptState = type.equals("final");
                            isNullState = type.equals("null");
                            
                        } else if (name.equals("transitions")) {
                            edges = readTransitions(reader);
//                            System.out.println("transitions: " + edges);
                            
                        } else if (name.equals("token")) {
                            token = reader.nextString();
//                            System.out.println("token: " + token);
                        }
                    }
                    
                    State s = new State(edges, isAcceptState, isNullState, token);
                    this.map.put(stateName, s);
                    reader.endObject();
                }
            reader.endArray();
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
   
    }
    
    ArrayList<Edge> readTransitions(JsonReader reader) {
        ArrayList<Edge> transitions = new ArrayList();
        try {
            
            reader.beginArray();
            String pointsTo, val;
            Edge e;
            while(reader.hasNext()) {
                reader.beginArray();
                
                pointsTo = reader.nextString();
                val = reader.nextString();
                e = new Edge(val, pointsTo);
                transitions.add(e);
                
                reader.endArray();
                
            }
            reader.endArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return transitions;
    }
}


class State {
    ArrayList<Edge> edges;
    boolean isAcceptState;
    boolean isNullState;
    String token;

    // a node can go to different edges
    State(ArrayList<Edge> edges, boolean isAcceptState, boolean isNullState, String token) {
        this.edges = edges;
        this.isAcceptState = isAcceptState;
        this.isNullState = isNullState;
        this.token = token;
    }

    String goToNext(String c) {
        String edgeVal;
//        System.out.println("Character: " + c    );
        for (int i = 0; i < this.edges.size(); i++) {
            edgeVal = this.edges.get(i).val;
            
//            System.out.println("Edge Value: " + edgeVal);
            
            if (edgeVal.equals(c)) {
//                System.out.println("Next State:" + this.edges.get(i).pointsTo + "\n");
                return this.edges.get(i).pointsTo;
            } else if (edgeVal.charAt(0) == '[') {
                if(c.matches(edgeVal)) {
//                    System.out.println("Next State:" + this.edges.get(i).pointsTo + "\n");
                    return this.edges.get(i).pointsTo; 
                }
            } else if (edgeVal.equals("Others")) {
//                System.out.println("Next State:" + this.edges.get(i).pointsTo + "\n");
                return this.edges.get(i).pointsTo;
            }
        }
        
        return null;
        }
    }


class Edge {
    String val;
    String pointsTo;

    // an edge has a value and points to another state
    Edge(String v, String to) {
        this.val = v;
        this.pointsTo = to;
    }
    
    @Override
    public String toString() {
        return ("val: " + val + "\npointsTo: " + pointsTo);
    }
}