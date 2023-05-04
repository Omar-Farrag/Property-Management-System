package DatabaseManagement.QueryGeneration;

import DatabaseManagement.ConstraintsHandling.ReferentialResolver;
import DatabaseManagement.Attribute;
import DatabaseManagement.AttributeCollection;
import DatabaseManagement.Filters;
import DatabaseManagement.Table;

import java.util.*;

public class Graph {

    private HashMap<Node, Set<Link>> graph;
    private ReferentialResolver resolver;

    public Graph() {
        graph = new HashMap<>();
        resolver = ReferentialResolver.getInstance();
        initGraph();
    }

    public Set<Node> getNeighbors(Node current) {
        Set<Node> nodes = new HashSet<>();
        for (Link link : graph.get(current)) {
            nodes.add(link.tail);
        }
        return nodes;
    }

    private void initGraph() {

        for (Table t : Table.values()) {
            graph.put(new Node(t), new HashSet<>());
        }

        for (Table t : Table.values()) {

            AttributeCollection referencedAttributes = resolver.getReferencedAttributes(t);

            for (Attribute referenced : referencedAttributes.attributes()) {
                HashMap<Table, Filters> referencingAttributes = resolver.getReferencingAttributes(t, referenced);
                Node head = new Node(referenced.getT());

                for (var referencing : referencingAttributes.entrySet()) {
                    Node tail = new Node(referencing.getKey());
                    Attribute referencingAtt
                            = referencing.getValue().getAttributes().iterator().next();
                    graph.get(head).add(new Link(head, tail, referenced, referencingAtt));
                    graph.get(tail).add(new Link(tail, head, referencingAtt, referenced));
                }
            }
        }
    }

    public Link getLinkTo(Node current, Node tail) {
        for (Link link : graph.get(current)) {
            if (link.tail.equals(tail)) {
                return link;
            }
        }
        return null;
    }

    public void unVisitNodes() {
        for (Node node : graph.keySet()) {
            node.unVisit();
        }
    }

    public static class Node {

        private Table table;
        private Node parent;
        private boolean visited;

        public Node(Table table) {
            this.table = table;
            visited = false;

        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Node node = (Node) o;
            return table == node.table;
        }

        @Override
        public int hashCode() {
            return Objects.hash(table.getTableName());
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public String getAliasedName() {
            return table.getAliasedName();
        }

        public Node getParent() {
            return parent;
        }

        public void visit() {
            visited = true;
        }

        public void unVisit() {
            visited = false;
        }

        public boolean isVisited() {
            return visited;
        }

    }

    public static class Link {

        private Node head;
        private Node tail;
        private Attribute headAttribute;
        private Attribute tailAttribute;

        public Link(Node head, Node tail, Attribute headAttribute, Attribute tailAttribute) {
            this.head = head;
            this.tail = tail;
            this.headAttribute = headAttribute;
            this.tailAttribute = tailAttribute;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Link link = (Link) o;
            return (head.equals(link.head) && tail.equals(link.tail)) || (head.equals(link.tail) && tail.equals(link.head));
        }

        @Override
        public int hashCode() {
            return Objects.hash(tail);
        }

        public String getAliasedCondition() {
            return headAttribute.getAliasedStringName() + " = " + tailAttribute.getAliasedStringName();
        }

        public String getAliasedHead() {
            return head.getAliasedName();
        }

        public String getAliasedTail() {
            return tail.getAliasedName();
        }
    }

}
