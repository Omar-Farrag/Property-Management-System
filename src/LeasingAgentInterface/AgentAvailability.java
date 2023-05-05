/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LeasingAgentInterface;

import DatabaseManagement.Attribute;
import DatabaseManagement.Attribute.Name;
import DatabaseManagement.AttributeCollection;
import DatabaseManagement.DBParameters;
import DatabaseManagement.Exceptions.DBManagementException;
import DatabaseManagement.Filters;
import DatabaseManagement.Table;
import LeasingAgentInterface.AppointmentSlotForm;
import GUI.TableViewer;
import java.sql.SQLException;

/**
 *
 * @author Dell
 */
public class AgentAvailability {

    public DBParameters getAgentAvailability() throws SQLException, DBManagementException {
        AttributeCollection toShow = new AttributeCollection();

        toShow.add(new Attribute(Name.SLOT_NUM, Table.APPOINTMENT_SLOTS));
        toShow.add(new Attribute(Name.DAY, Table.APPOINTMENT_SLOTS));
        toShow.add(new Attribute(Name.START_DATE, Table.APPOINTMENT_SLOTS));
        toShow.add(new Attribute(Name.END_DATE, Table.APPOINTMENT_SLOTS));
        toShow.add(new Attribute(Name.AGENT_ID, Table.APPOINTMENT_SLOTS));
        toShow.add(new Attribute(Name.FNAME, Table.USERS));
        toShow.add(new Attribute(Name.PHONE_NUMBER, Table.USERS));
        toShow.add(new Attribute(Name.EMAIL_ADDRESS, Table.USERS));

        Filters filters = new Filters();

        filters.addEqual(new Attribute(Name.BOOKED, "0", Table.APPOINTMENT_SLOTS));

        return new DBParameters(toShow, filters);
    }
}
