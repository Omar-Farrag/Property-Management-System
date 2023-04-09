package General;

import Properties.Mall;
import Properties.Store;
import java.util.ArrayList;

public class Controller {

    public ArrayList<String> getListOfMalls() {
        return Mall.getListOfMalls();
    }

    public int getFloors(String mallName) {
        return Mall.getFloors(mallName);
    }

    public ArrayList<String> getClasses() {
        return Store.getClasses();
    }

    public ArrayList<String> getPurposes() {
        return Store.getPurposes();
    }
}
