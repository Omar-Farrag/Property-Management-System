package DatabaseManagement.Exceptions;

import java.util.Set;
import java.util.stream.Collectors;

import DatabaseManagement.QueryGeneration.Graph.Node;

public class InvalidJoinException extends DBManagementException {
    private Set<Node> tables;

    public InvalidJoinException(Set<Node> tables) {
        this.tables = tables;
    }


    @Override
    public String getMessage() {
        return "The attributes you're retrieving, in tables " +
                tables.stream().map(Node::getAliasedName).collect(Collectors.joining(",")) +
                ", that cannot be joined";
    }
}