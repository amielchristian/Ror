/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modules;

import java.util.HashMap;

/**
 *
 * @author Ivan
 */
public class SymbolTable {
    HashMap<String, HashMap<String, Object>> map;
    // example: "id_x" : {{"name" : "x"}, {"datatype" : "int"}, {"value" : 10}}
    public SymbolTable() {
        map = new HashMap<>();
    }
    
    public void addToken(String token) {
        map.put(token, new HashMap<>());
        map.get(token).put("tokentype", "id");
        //map.get(token).put("datatype", TODO); for next phase hehe
        //map.get(token).put("value", TODO);     "   "     "    "
    }
    
    public void setTokenDatatype(String token, String datatype) {
        map.get(token).put("datatype", datatype);
    }
    
    public void updateTokenValue(String token, Object newValue) {
        map.get(token).put("value", newValue);
    }
    
    public Object getTokenValue(String token, String key) {
        return map.get(token).get(key);
    }
    
    @Override
    public String toString() {
        String str = "";
        for (String name : map.keySet()) {
            str += name + ": " + map.get(name).toString() + "\n";
        }
        
        return str;
    }
    
    public boolean checkInitialization(String identifier) {
        if (map.get(identifier).containsKey("value")) {
            return true;
        }
        return false;
    }
}

class SymbolException extends Throwable {
     public SymbolException() {
        super();
    }

    public SymbolException(String message) {
        super(message);
    }
}