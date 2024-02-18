/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modules;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 *
 * @author Ivan
 */
public class SymbolTable {
    HashMap<String, HashSet<String>> map;
    
    public SymbolTable() {
        map = new HashMap<>();
        map.put("and", new HashSet<String>());
        map.put("not", new HashSet<String>());
        map.put("or", new HashSet<String>());
        map.put("repeat", new HashSet<String>());
        map.put("nom", new HashSet<String>());
        map.put("else", new HashSet<String>());
        map.put("if", new HashSet<String>());
        map.put("roar", new HashSet<String>());
        map.put("gte", new HashSet<String>());
        map.put("lte", new HashSet<String>());
        map.put("assign_op", new HashSet<String>());
        map.put("single_comment", new HashSet<String>());
        map.put("group_comment_start", new HashSet<String>());
        map.put("group_comment_end", new HashSet<String>());
        map.put("parenthesis_start", new HashSet<String>());
        map.put("parenthesis_end", new HashSet<String>());
        map.put("modulo_op", new HashSet<String>());
        map.put("div_op", new HashSet<String>());
        map.put("mult_op", new HashSet<String>());
        map.put("add_op", new HashSet<String>());
        map.put("minus_op", new HashSet<String>());
        map.put("increment_op", new HashSet<String>());
        map.put("decrement_op", new HashSet<String>());
    }
    
    public void add(String token, String word) {
        map.get(token).add(word);
    }
    
    @Override
    public String toString() {
        String str = "";
        int i = 0;
        for (Map.Entry<String, HashSet<String>> entry : map.entrySet()) {
            
            str += "\n" + i + " - " +  entry.getKey() + ": " + entry.getValue().toString();
            i++;
        }
        
        return str;
    }
}
