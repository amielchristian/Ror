/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modules;

import java.util.ArrayList;

/**
 *
 * @author Ivan
 */
public class ParseTreeNode {

    private ArrayList<ParseTreeNode> children;
    public String name;
    private int level;

    public ParseTreeNode(String name) {
        this.name = name;
        this.children = new ArrayList<ParseTreeNode>();
        this.level = 0;
    }

    public ParseTreeNode(String name, ArrayList<ParseTreeNode> children) {
        this.name = name;
        this.children = children;
    }

    public ParseTreeNode addChild(ParseTreeNode node) {
        node.setLevel(this.level + 1);
        this.children.add(node);
        return node;
    }
    
    public ParseTreeNode addChild(String name) {
        ParseTreeNode node = new ParseTreeNode(name);
        node.setLevel(this.level + 1);
        this.children.add(node);
        return node;
    }
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        String str = this.name;
        if (!this.children.isEmpty()) {
            for (ParseTreeNode child : children) {
                str += "\n" + levelBuilder(child.level) + child.toString();
            }
        }
        return str;
    }
    
    public String levelBuilder(int level) {
        String str = "";
        for (int i = 0; i < level; i++) {
            str += "\t";
        }
        return str;
    }
}
