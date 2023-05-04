/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DatabaseManagement;

/**
 *
 * @author Dell
 */
public class DBParameters {

    private AttributeCollection collection;
    private Filters filters;

    public DBParameters(AttributeCollection collection, Filters filters) {
        this.collection = collection;
        this.filters = filters;
    }

    public AttributeCollection getCollection() {
        return collection;
    }

    public Filters getFilters() {
        return filters;
    }

}
