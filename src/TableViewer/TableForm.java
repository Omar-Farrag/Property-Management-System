/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TableViewer;

import DatabaseManagement.Table;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author Dell
 */
public abstract class TableForm extends JFrame implements Form {

    protected Table[] tables;
    private JLabel TopLabel;
    private JButton ActionBtn;
    protected FormInitializationStrategy initStrategy;
    private TableViewer viewer;

    protected void initBaseComponents(Table[] tables, JLabel TopLabel, JButton ActionBtn) {
        this.tables = tables;
        this.TopLabel = TopLabel;
        this.ActionBtn = ActionBtn;
    }

    protected boolean isInserting() {
        return initStrategy instanceof InsertForm;
    }

    @Override
    public Table[] getTables() {
        return tables;
    }

    @Override
    public JFrame getFrame() {
        return this;
    }

    @Override
    public void setLabelType(String labelType) {
        TopLabel.setText(labelType.toUpperCase());
    }

    @Override
    public JButton getActionBtn() {
        return ActionBtn;
    }

    @Override
    public TableViewer getViewer() {
        return viewer;
    }

    @Override
    public void setInitStrategy(FormInitializationStrategy initStrat) {
        this.initStrategy = initStrat;
    }

    @Override
    public void applyInitStrategy() {
        initStrategy.handleFormInitialization(this);
    }

    @Override
    public void setViewer(TableViewer viewer) {
        this.viewer = viewer;
    }

}
