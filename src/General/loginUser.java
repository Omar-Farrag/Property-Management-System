/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package General;

import DatabaseManagement.Attribute;
import DatabaseManagement.Attribute.Name;
import DatabaseManagement.Filters;
import DatabaseManagement.QueryResult;
import DatabaseManagement.Table;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class LoginUser {

    private String userID;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String emailAddress;
    private String roleID;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getRoleID() {
        return roleID;
    }

    private static Controller controller = new Controller();

    private LoginUser() {
    }

    public static LoginUser retrieve(String userID) throws SQLException {
        if (userID == null) {
            return null;
        }
        LoginUser user = new LoginUser();

        user.userID = userID;

        Filters filters = new Filters();
        filters.addEqual(new Attribute(Name.USER_ID, user.userID, Table.USERS));

        QueryResult userInfo = controller.retrieve(Table.USERS, filters);
        if (!userInfo.noErrors() || userInfo.getRowsAffected() == 0) {
            return null;
        }

        ResultSet result = userInfo.getResult();
        result.next();

        user.firstName = result.getString(Name.FNAME.getName());
        user.lastName = result.getString(Name.LNAME.getName());
        user.phoneNumber = result.getString(Name.PHONE_NUMBER.getName());
        user.emailAddress = result.getString(Name.EMAIL_ADDRESS.getName());
        user.roleID = result.getString(Name.ROLE_ID.getName());

        return user;

    }

    public String getUserID() {
        return userID;
    }

}
