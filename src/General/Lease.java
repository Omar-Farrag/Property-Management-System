/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package General;

import DatabaseManagement.Attribute;
import DatabaseManagement.Attribute.Name;
import DatabaseManagement.AttributeCollection;
import DatabaseManagement.Table;
import java.time.LocalDateTime;

/**
 *
 * @author Dell
 */
public class Lease {

    private String leaseNum;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String paymentOption;
    private String leaserID;
    private String locationNum;

    private Lease() {
    }

    /**
     *
     * @return List of attributes to be displayed whenever lease information is
     * presented at the UI
     */
    public static AttributeCollection getVisibleAttributes() {
        AttributeCollection toShow = new AttributeCollection();
        toShow.add(new Attribute(Name.LEASE_NUM, Table.LEASES));
        toShow.add(new Attribute(Name.LEASER_ID, Table.LEASES));
        toShow.add(new Attribute(Name.START_DATE, Table.LEASES));
        toShow.add(new Attribute(Name.END_DATE, Table.LEASES));
        toShow.add(new Attribute(Name.PAYMENT_OPTION, Table.LEASES));
        toShow.add(new Attribute(Name.RATE, Table.LEASES));
        toShow.add(new Attribute(Name.NAME, Table.MALLS));
        toShow.add(new Attribute(Name.NAME, Table.PROPERTIES));

        return toShow;
    }

}
