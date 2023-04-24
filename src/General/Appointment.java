/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package General;

import DatabaseManagement.Attribute;
import DatabaseManagement.Attribute.Name;
import DatabaseManagement.AttributeCollection;
import DatabaseManagement.Exceptions.DBManagementException;
import DatabaseManagement.Filters;
import DatabaseManagement.QueryResult;
import DatabaseManagement.Table;
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

    public static Appointment create(LoginUser tenant, AttributeCollection storeInformation, AttributeCollection appointmentInfo) throws SQLException, DBManagementException {

        Appointment appointment = new Appointment();
        appointment.slotNumber = appointmentInfo.getValue(new Attribute(Name.SLOT_NUM, Table.APPOINTMENT_SLOTS));
        appointment.tenant = tenant;

        String agentID = appointmentInfo.getValue(new Attribute(Name.AGENT_ID, Table.APPOINTMENT_SLOTS));
        appointment.agent = LoginUser.retrieve(agentID);

        String locationNum = controller.findLocationNum(storeInformation);
        appointment.store = Store.retrieve(locationNum);

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

}
