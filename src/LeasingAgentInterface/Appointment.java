/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LeasingAgentInterface;

import DatabaseManagement.Attribute;
import DatabaseManagement.Attribute.Name;
import DatabaseManagement.AttributeCollection;
import DatabaseManagement.Exceptions.DBManagementException;
import DatabaseManagement.Filters;
import DatabaseManagement.QueryResult;
import DatabaseManagement.Table;
import General.Controller;
import General.LoginUser;
import General.Store;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Dell
 */
public class Appointment {

    private String slotNumber;

    private LoginUser tenant;

    private LoginUser agent;

    private Store store;

    private String startDate;
    private String endDate;

    private static Controller controller = new Controller();

    private Appointment() {
    }

    public static Appointment create(LoginUser tenant, Store store, LoginUser agent, AttributeCollection appointmentInfo) throws SQLException, DBManagementException {

        Appointment appointment = new Appointment();

        appointment.tenant = tenant;
        appointment.agent = agent;
        appointment.store = store;

        appointment.slotNumber = appointmentInfo.getValue(new Attribute(Name.SLOT_NUM, Table.APPOINTMENT_SLOTS));

        appointment.startDate = appointmentInfo.getValue(new Attribute(Name.START_DATE, Table.APPOINTMENT_SLOTS));
        appointment.endDate = appointmentInfo.getValue(new Attribute(Name.END_DATE, Table.APPOINTMENT_SLOTS));

        AttributeCollection attsToModify = new AttributeCollection();
        attsToModify.add(new Attribute(Name.BOOKED, "1", Table.APPOINTMENT_SLOTS));

        Filters slotsToBook = new Filters();
        slotsToBook.addEqual(new Attribute(Name.SLOT_NUM, appointment.slotNumber, Table.APPOINTMENT_SLOTS));

        QueryResult slotBooking = controller.modify(Table.APPOINTMENT_SLOTS, attsToModify, slotsToBook, true);

        AttributeCollection toInsert = new AttributeCollection();
        toInsert.add(new Attribute(Name.POTENTIAL_TENANT_ID, appointment.tenant.getUserID(), Table.APPOINTMENTS));
        toInsert.add(new Attribute(Name.LOCATION_NUM, String.valueOf(appointment.store.getLocationNum()), Table.APPOINTMENTS));
        toInsert.add(new Attribute(Name.APPOINTMENT_SLOT, appointment.slotNumber, Table.APPOINTMENTS));

        QueryResult appointmentInsertion = controller.insert(Table.APPOINTMENTS, toInsert, true);

        if (slotBooking.noErrors() && appointmentInsertion.noErrors()) {
            return appointment;
        } else {
            return null;
        }
    }

    public static AttributeCollection getVisibleAttributes() {
        AttributeCollection toShow = new AttributeCollection();

        toShow.add(new Attribute(Name.SLOT_NUM, Table.APPOINTMENT_SLOTS));
        toShow.add(new Attribute(Name.DAY, Table.APPOINTMENT_SLOTS));
        toShow.add(new Attribute(Name.START_DATE, Table.APPOINTMENT_SLOTS));
        toShow.add(new Attribute(Name.END_DATE, Table.APPOINTMENT_SLOTS));
        toShow.add(new Attribute(Name.POTENTIAL_TENANT_ID, Table.APPOINTMENTS));
        toShow.add(new Attribute(Name.FNAME, Table.USERS));
        toShow.add(new Attribute(Name.PHONE_NUMBER, Table.USERS));
        toShow.add(new Attribute(Name.EMAIL_ADDRESS, Table.USERS));
        toShow.add(new Attribute(Name.NAME, Table.PROPERTIES));
        toShow.add(new Attribute(Name.NAME, Table.MALLS));

        return toShow;

    }

}
