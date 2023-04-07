package Properties;

import DatabaseManagement.QueryResult;

public interface PropertyManagementInterface {
    public QueryResult insert();

    public QueryResult modify();

    public QueryResult delete();
}
