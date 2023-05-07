package DatabaseManagement.QueryGeneration;

import DatabaseManagement.Exceptions.InvalidJoinException;
import DatabaseManagement.Attribute;
import DatabaseManagement.AttributeCollection;
import DatabaseManagement.Filters;
import DatabaseManagement.QueryGeneration.Graph.Link;
import DatabaseManagement.QueryGeneration.Graph.Node;

import java.util.*;

public class QueryGenerator {

    private AttributeCollection toGet;
    private Filters toFilter;
    private Graph graph;
    private Set<Node> tables_to_join;
    private Set<Link> links;

    public QueryGenerator(AttributeCollection toGet, Filters toFilter) {
        this.toGet = toGet;
        this.toFilter = toFilter;
        graph = Graph.getInstance();
        tables_to_join = new HashSet<>();
        links = new HashSet<>();
    }

    public QueryGenerator(Filters toFilter) {
        this.toGet = new AttributeCollection();
        this.toFilter = toFilter;
        graph = Graph.getInstance();
        tables_to_join = new HashSet<>();
        links = new HashSet<>();
    }

    public QueryGenerator(AttributeCollection toGet) {
        this.toGet = toGet;
        this.toFilter = new Filters();
        graph = Graph.getInstance();
        tables_to_join = new HashSet<>();
        links = new HashSet<>();
    }

    public String generateQuery() throws InvalidJoinException {
        //identify which tables to join;
        init_tables_to_join();
        Iterator<Node> nodeIterator = tables_to_join.iterator();
        if (tables_to_join.size() == 1) {
            String query = "SELECT " + toGet.getAliasedFormattedAtt() + " FROM " + nodeIterator.next().getAliasedName();

            if (!toFilter.getFilterClause().isEmpty()) {
                query += " WHERE " + toFilter.getFilterClause();
            }
            return query;
        }

        Set<Node> foundNodes = new HashSet<>();
        Node starting = nodeIterator.next();
        foundNodes.add(starting);
        tables_to_join.remove(starting);
        Iterator<Node> foundNodeIterator = foundNodes.iterator();

        Queue<Node> BFSqueue = new LinkedList<>();

        while (foundNodeIterator.hasNext()) {
            Node start = foundNodeIterator.next();

            graph.unVisitNodes();
            BFSqueue.add(start);
            start.visit();

            while (!BFSqueue.isEmpty()) {
                Node current = BFSqueue.poll();

                if (tables_to_join.contains(current)) {
                    foundNodes.add(current);
                    tables_to_join.remove(current);

                    for (Node node = current; node.getParent() != null; node = node.getParent()) {
                        links.addAll(graph.getLinksTo(node.getParent(), node));
                    }
                    if (current.getParent() == null) {
                        links.addAll(graph.getLinksTo(current, current));
                    }
                    if (tables_to_join.size() == 0) {
                        return generatedQuery();
                    }
                }
                for (Node neighbor : graph.getNeighbors(current)) {
                    if (!neighbor.isVisited()) {
                        neighbor.visit();
                        neighbor.setParent(current);
                        BFSqueue.add(neighbor);
                    }
                }
            }
        }
        throw new InvalidJoinException(tables_to_join);
    }

    public static String getSetClause(AttributeCollection toModify) {
        String clause = "set ";
        ArrayList<String> updates = new ArrayList<>();
        for (Attribute attribute : toModify.attributes()) {
            String update = attribute.getStringName() + " = " + attribute.getStringValue();
            updates.add(update);
        }
        return clause + String.join(",", updates);

    }

    private String generatedQuery() {

        ArrayList<String> conditions = new ArrayList<>();
        Set<String> tableNames = new HashSet<>();

        for (Link link : links) {
            conditions.add(link.getAliasedCondition());
            tableNames.add(link.getAliasedHead());
            tableNames.add(link.getAliasedTail());
        }
        if (!toFilter.getFilterClause().isEmpty()) {
            conditions.add(toFilter.getFilterClause());
        }

        String query = "SELECT " + toGet.getAliasedFormattedAtt();
        query += " FROM " + String.join(", ", tableNames);

        String allConditions = String.join(" AND ", conditions);
        if (!allConditions.isEmpty()) {
            query += " WHERE " + allConditions;
        }

        return query;
    }

    private void init_tables_to_join() {
        for (Attribute attribute : toGet.attributes()) {
            tables_to_join.add(new Node(attribute.getT()));
        }

        for (Attribute attribute : toFilter.getAttributes()) {
            tables_to_join.add(new Node(attribute.getT()));
        }
    }
}
