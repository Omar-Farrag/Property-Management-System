/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package LeasingAgentInterface;

import DatabaseManagement.Attribute;
import DatabaseManagement.Attribute.Name;
import DatabaseManagement.AttributeCollection;
import DatabaseManagement.DatabaseManager;
import DatabaseManagement.Filters;
import DatabaseManagement.QueryResult;
import DatabaseManagement.Table;
import TableViewer.FilterForm;
import TableViewer.FormInitializationStrategy;
import General.Controller;
import TableViewer.TableForm;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dell
 */
public class AppointmentSlotForm extends TableForm {

    private Controller controller;

    private String currentWeekDay;

    private String currentDay;
    private String currentMonth;
    private String currentYear;

    private String currentStartHour;
    private String currentStartMinute;
    private String currentStartPeriod;

    private String currentEndHour;
    private String currentEndMinute;
    private String currentEndPeriod;

    private String slotNumber;

    private HashMap<String, String> mm_to_MMM = new HashMap<>();

    /**
     * Creates new form PropertyBrowsing
     */
    public AppointmentSlotForm() {
        initComponents();
        Table[] tables = new Table[]{Table.APPOINTMENT_SLOTS};
        initBaseComponents(tables, TopLabel, ActionBtn);
        controller = new Controller();
        mm_to_MMM.put("01", "Jan");
        mm_to_MMM.put("02", "Feb");
        mm_to_MMM.put("03", "Mar");
        mm_to_MMM.put("04", "Apr");
        mm_to_MMM.put("05", "May");
        mm_to_MMM.put("06", "Jun");
        mm_to_MMM.put("07", "Jul");
        mm_to_MMM.put("08", "Aug");
        mm_to_MMM.put("09", "Sep");
        mm_to_MMM.put("10", "Oct");
        mm_to_MMM.put("11", "Nov");
        mm_to_MMM.put("12", "Dec");
        populateWeekDayCMB();

        populateDayCMB();
        populateMonthCMB();
        populateYearCMB();

        populateHourCMB();
        populateMinuteCMB();

        populateHourCMB();
        populateMinuteCMB();

        populatePeriodCMB();
    }

    @Override
    public AttributeCollection getAllAttributes() {
        AttributeCollection collection = new AttributeCollection();

        if (isInserting()) {
            slotNumber = generateNewSlot();
        }

        currentWeekDay = weekDayCMB.getSelectedItem().toString().trim();
        currentDay = dayCMB.getSelectedItem().toString().trim();
        currentMonth = monthCMB.getSelectedItem().toString().trim();
        currentYear = yearCMB.getSelectedItem().toString().trim();
        currentStartHour = startHourCMB.getSelectedItem().toString().trim();
        currentStartMinute = startMinuteCMB.getSelectedItem().toString().trim();
        currentStartPeriod = startPeriodCMB.getSelectedItem().toString().trim();
        currentEndHour = endHourCMB.getSelectedItem().toString().trim();
        currentEndMinute = endMinuteCMB.getSelectedItem().toString().trim();
        currentEndPeriod = endPeriodCMB.getSelectedItem().toString().trim();

        String startDate = currentDay + "-" + mm_to_MMM.get(currentMonth) + "-" + currentYear + " " + currentStartHour + ":" + currentStartMinute + ":00 " + currentStartPeriod;
        String endDate = currentDay + "-" + mm_to_MMM.get(currentMonth) + "-" + currentYear + " " + currentEndHour + ":" + currentEndMinute + ":00 " + currentEndPeriod;

        startDate = controller.getTimestamp(startDate);
        endDate = controller.getTimestamp(endDate);

        collection.add(new Attribute(Name.BOOKED, "0", Table.APPOINTMENT_SLOTS));
        collection.add(new Attribute(Name.AGENT_ID, controller.getUserID(), Table.APPOINTMENT_SLOTS));
        collection.add(new Attribute(Name.DAY, currentWeekDay, Table.APPOINTMENT_SLOTS));
        collection.add(new Attribute(Name.START_DATE, startDate, Table.APPOINTMENT_SLOTS));
        collection.add(new Attribute(Name.END_DATE, endDate, Table.APPOINTMENT_SLOTS));
        collection.add(new Attribute(Name.SLOT_NUM, slotNumber, Table.APPOINTMENT_SLOTS));

        return collection;

    }

    @Override
    public Filters getBrowsingFilters() {
        Filters filters = new Filters();

        currentWeekDay = weekDayCMB.getSelectedItem().toString().trim();
        if (!currentWeekDay.isBlank()) {
            filters.addEqual(new Attribute(Name.DAY, currentWeekDay, Table.APPOINTMENT_SLOTS));
        }

        return filters;
    }

    @Override
    public void applyInitStrategy() {
        initStrategy.handleFormInitialization(this);
        if (initStrategy instanceof FilterForm) {
            dayCMB.setEnabled(false);
            monthCMB.setEnabled(false);
            yearCMB.setEnabled(false);
            startHourCMB.setEnabled(false);
            startMinuteCMB.setEnabled(false);
            startPeriodCMB.setEnabled(false);
            endHourCMB.setEnabled(false);
            endMinuteCMB.setEnabled(false);
            endPeriodCMB.setEnabled(false);
        }
    }

    @Override
    public Filters getPKFilter() {
        Filters filters = new Filters();
        filters.addEqual(new Attribute(Name.SLOT_NUM, slotNumber, Table.APPOINTMENT_SLOTS));
        return filters;
    }

    @Override
    public void enableFields() {
        weekDayCMB.setEnabled(true);
        dayCMB.setEnabled(true);
        monthCMB.setEnabled(true);
        yearCMB.setEnabled(true);
        startHourCMB.setEnabled(true);
        startMinuteCMB.setEnabled(true);
        startPeriodCMB.setEnabled(true);
        endHourCMB.setEnabled(true);
        endMinuteCMB.setEnabled(true);
        endPeriodCMB.setEnabled(true);
    }

    @Override
    public void disableUnmodifiableFields() {
        return;
    }

    @Override
    public void populateFields(AttributeCollection toPopulateWith) {
        slotNumber = toPopulateWith.getValue(new Attribute(Name.SLOT_NUM, Table.APPOINTMENT_SLOTS));
        currentWeekDay = toPopulateWith.getValue(new Attribute(Name.DAY, Table.APPOINTMENT_SLOTS));
        weekDayCMB.setSelectedItem(currentWeekDay);

        String start = toPopulateWith.getValue(new Attribute(Name.START_DATE, Table.APPOINTMENT_SLOTS));
        String end = toPopulateWith.getValue(new Attribute(Name.END_DATE, Table.APPOINTMENT_SLOTS));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yy hh:mm:ss.SSSSSSSSS a");
        LocalDateTime startDate = LocalDateTime.parse(start, formatter);
        LocalDateTime endDate = LocalDateTime.parse(end, formatter);

        currentDay = handlePadding(startDate.getDayOfMonth());
        currentMonth = handlePadding(startDate.getMonthValue());
        currentYear = handlePadding(startDate.getYear());

        dayCMB.setSelectedItem(currentDay);
        monthCMB.setSelectedItem(currentMonth);
        yearCMB.setSelectedItem(currentYear);

        currentStartHour = handlePadding(handleHour(startDate.getHour()));
        currentStartMinute = handlePadding(startDate.getMinute());
        currentStartPeriod = startDate.getHour() >= 12 ? "PM" : "AM";
        currentEndHour = handlePadding(handleHour(endDate.getHour()));
        currentEndMinute = handlePadding(endDate.getMinute());
        currentEndPeriod = endDate.getHour() >= 12 ? "PM" : "AM";

        startHourCMB.setSelectedItem(currentStartHour);
        startMinuteCMB.setSelectedItem(currentStartMinute);
        startPeriodCMB.setSelectedItem(currentStartPeriod);
        endHourCMB.setSelectedItem(currentEndHour);
        endMinuteCMB.setSelectedItem(currentEndMinute);
        endPeriodCMB.setSelectedItem(currentEndPeriod);

    }

    @Override
    public void clearFields() {
        weekDayCMB.setSelectedItem("");
        dayCMB.setSelectedItem("");
        monthCMB.setSelectedItem("");
        yearCMB.setSelectedItem("");
        startHourCMB.setSelectedItem("");
        startMinuteCMB.setSelectedItem("");
        startPeriodCMB.setSelectedItem("");
        endHourCMB.setSelectedItem("");
        endMinuteCMB.setSelectedItem("");
        endPeriodCMB.setSelectedItem("");
    }

    public void populateWeekDayCMB() {
        weekDayCMB.removeAllItems();
        weekDayCMB.addItem("");
        String[] weekDays = new String[]{
            "MONDAY",
            "TUESDAY",
            "WEDNESDAY",
            "THURSDAY",
            "FRIDAY",
            "SATURDAY",
            "SUNDAY"
        };
        for (String weekDay : weekDays) {
            weekDayCMB.addItem(weekDay);
        }
    }

    public void populateDayCMB() {
        dayCMB.removeAllItems();
        dayCMB.addItem("");

        for (int i = 1; i <= 31; i++) {

            dayCMB.addItem(handlePadding(i));

        }

    }

    public void populateMonthCMB() {
        monthCMB.removeAllItems();
        monthCMB.addItem("");

        for (int i = 1; i <= 12; i++) {
            monthCMB.addItem(handlePadding(i));
        }

    }

    public void populateYearCMB() {
        yearCMB.removeAllItems();
        yearCMB.addItem("");
        for (int i = 2030; i >= 2023; i--) {
            yearCMB.addItem(handlePadding(i));
        }

    }

    public void populateHourCMB() {
        startHourCMB.removeAllItems();
        startHourCMB.addItem("");

        endHourCMB.removeAllItems();
        endHourCMB.addItem("");

        for (int i = 1; i <= 12; i++) {
            startHourCMB.addItem(handlePadding(i));
            endHourCMB.addItem(handlePadding(i));
        }

    }

    public void populateMinuteCMB() {
        startMinuteCMB.removeAllItems();
        startMinuteCMB.addItem("");

        endMinuteCMB.removeAllItems();
        endMinuteCMB.addItem("");

        for (int i = 0; i <= 59; i++) {

            startMinuteCMB.addItem(handlePadding(i));
            endMinuteCMB.addItem(handlePadding(i));

        }
    }

    public void populatePeriodCMB() {
        startPeriodCMB.removeAllItems();
        startPeriodCMB.addItem("");
        startPeriodCMB.addItem("AM");
        startPeriodCMB.addItem("PM");

        endPeriodCMB.removeAllItems();
        endPeriodCMB.addItem("");
        endPeriodCMB.addItem("AM");
        endPeriodCMB.addItem("PM");
    }

    private String handlePadding(int x) {
        String value = String.valueOf(x);

        if (x < 10) {
            value = "0" + value;
        }

        return value;
    }

    private int handleHour(int x) {
        if (x > 12) {
            return x % 12;
        }
        return x;
    }

    private String generateNewSlot() {
        try {
            QueryResult maxQueryResult = controller.retrieveMax(new Attribute(Name.SLOT_NUM, Table.APPOINTMENT_SLOTS));
            ResultSet result = maxQueryResult.getResult();
            result.next();
            return String.valueOf(result.getInt(1) + 1);
        } catch (SQLException ex) {
            controller.displaySQLError(ex);
        }
        return "";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox6 = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        ActionBtn = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        weekDayCMB = new javax.swing.JComboBox<>();
        dayCMB = new javax.swing.JComboBox<>();
        monthCMB = new javax.swing.JComboBox<>();
        yearCMB = new javax.swing.JComboBox<>();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        startHourCMB = new javax.swing.JComboBox<>();
        startMinuteCMB = new javax.swing.JComboBox<>();
        endHourCMB = new javax.swing.JComboBox<>();
        endMinuteCMB = new javax.swing.JComboBox<>();
        jLabel24 = new javax.swing.JLabel();
        startPeriodCMB = new javax.swing.JComboBox<>();
        endPeriodCMB = new javax.swing.JComboBox<>();
        clearBtn = new javax.swing.JButton();
        TopLabel = new javax.swing.JLabel();

        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel5.setBackground(new java.awt.Color(204, 204, 204));
        jLabel5.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Week Day");
        jLabel5.setOpaque(true);

        ActionBtn.setBackground(new java.awt.Color(0, 204, 0));
        ActionBtn.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        ActionBtn.setForeground(new java.awt.Color(255, 255, 255));
        ActionBtn.setText("Submit");

        jLabel17.setBackground(new java.awt.Color(204, 204, 204));
        jLabel17.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("Day of Month");
        jLabel17.setOpaque(true);

        jLabel18.setBackground(new java.awt.Color(204, 204, 204));
        jLabel18.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Month");
        jLabel18.setOpaque(true);

        jLabel19.setBackground(new java.awt.Color(204, 204, 204));
        jLabel19.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Year");
        jLabel19.setOpaque(true);

        weekDayCMB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        dayCMB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        monthCMB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        yearCMB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel20.setBackground(new java.awt.Color(204, 204, 204));
        jLabel20.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("Start Time");
        jLabel20.setOpaque(true);

        jLabel21.setBackground(new java.awt.Color(204, 204, 204));
        jLabel21.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("Hour:");
        jLabel21.setOpaque(true);

        jLabel22.setBackground(new java.awt.Color(204, 204, 204));
        jLabel22.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("End Time");
        jLabel22.setOpaque(true);

        jLabel23.setBackground(new java.awt.Color(204, 204, 204));
        jLabel23.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("Minute:");
        jLabel23.setOpaque(true);

        startHourCMB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        startMinuteCMB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        endHourCMB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        endMinuteCMB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel24.setBackground(new java.awt.Color(204, 204, 204));
        jLabel24.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("AM/PM");
        jLabel24.setOpaque(true);

        startPeriodCMB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        endPeriodCMB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        clearBtn.setBackground(new java.awt.Color(255, 51, 51));
        clearBtn.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        clearBtn.setForeground(new java.awt.Color(255, 255, 255));
        clearBtn.setText("Clear");
        clearBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(weekDayCMB, javax.swing.GroupLayout.Alignment.LEADING, 0, 177, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(dayCMB, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(monthCMB, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(yearCMB, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 13, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(startMinuteCMB, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(startPeriodCMB, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                                    .addComponent(startHourCMB, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(clearBtn, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                                        .addComponent(endHourCMB, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(endMinuteCMB, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(endPeriodCMB, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ActionBtn))))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(weekDayCMB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dayCMB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(monthCMB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(yearCMB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(startHourCMB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(endHourCMB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(startMinuteCMB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(endMinuteCMB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(startPeriodCMB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(endPeriodCMB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ActionBtn)
                    .addComponent(clearBtn))
                .addGap(25, 25, 25))
        );

        jScrollPane1.setViewportView(jPanel1);

        TopLabel.setBackground(new java.awt.Color(0, 0, 0));
        TopLabel.setFont(new java.awt.Font("Verdana", 1, 24)); // NOI18N
        TopLabel.setForeground(new java.awt.Color(255, 255, 255));
        TopLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        TopLabel.setText("APPOINTMENT SLOT");
        TopLabel.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(TopLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TopLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void clearBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearBtnActionPerformed
        clearFields();
    }//GEN-LAST:event_clearBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ActionBtn;
    private javax.swing.JLabel TopLabel;
    private javax.swing.JButton clearBtn;
    private javax.swing.JComboBox<String> dayCMB;
    private javax.swing.JComboBox<String> endHourCMB;
    private javax.swing.JComboBox<String> endMinuteCMB;
    private javax.swing.JComboBox<String> endPeriodCMB;
    private javax.swing.JComboBox<String> jComboBox6;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> monthCMB;
    private javax.swing.JComboBox<String> startHourCMB;
    private javax.swing.JComboBox<String> startMinuteCMB;
    private javax.swing.JComboBox<String> startPeriodCMB;
    private javax.swing.JComboBox<String> weekDayCMB;
    private javax.swing.JComboBox<String> yearCMB;
    // End of variables declaration//GEN-END:variables

}
