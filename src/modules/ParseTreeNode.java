/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modules;

import java.util.ArrayList;
import java.util.Iterator;

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
        ParseTreeNode node = new ParseTreeNode("<"+name+">");
        node.setLevel(this.level + 1);
        this.children.add(node);
        return node;
    }
    
    public ParseTreeNode addNonTermChild(String name) {
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
        StringBuilder buffer = new StringBuilder(50);
        print(buffer, "", "");
        return buffer.toString();
    }

    private void print(StringBuilder buffer, String prefix, String childrenPrefix) {
        buffer.append(prefix);
        buffer.append(name);
        buffer.append('\n');
        for (Iterator<ParseTreeNode> it = children.iterator(); it.hasNext();) {
            ParseTreeNode next = it.next();
            if (it.hasNext()) {
                next.print(buffer, childrenPrefix + " |--- ", childrenPrefix + " |    ");
            } else {
                next.print(buffer, childrenPrefix + " |--- ", childrenPrefix + "      ");
            }
        }
    }
}
