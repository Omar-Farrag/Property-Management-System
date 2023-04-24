/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package TableViewer;

import DatabaseManagement.AttributeCollection;
import DatabaseManagement.Filters;
import DatabaseManagement.Table;
import FormManipulationStrategies.FormInitializationStrategy;
import javax.swing.JButton;
import javax.swing.JFrame;

/**
 *
 * @author Dell
 */
public interface Form {

    public AttributeCollection getAllAttributes();

    public Filters getBrowsingFilters();

    public Filters getPKFilter();

    public Table[] getTables();

    public JFrame getFrame();

    public void enableFields();

    public void disableUnmodifiableFields();

    public void populateFields(AttributeCollection toPopulateWith);

    public void clearFields();

    public void setLabelType(String labelType);

    public JButton getActionBtn();

    public void setViewer(TableViewer viewer);

    public TableViewer getViewer();

    public void setInitStrategy(FormInitializationStrategy initStrat);

    public void applyInitStrategy();

}
