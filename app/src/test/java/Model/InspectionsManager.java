/**
 * A manager to store inspections.
 */
package Model;

import java.util.ArrayList;

public class InspectionsManager {
    private ArrayList<Inspection> inspectionList= new ArrayList<>();

    public void addInspection(Inspection inspection){
        inspectionList.add(inspection);
    }

    public Inspection getInspection(int i){
        return inspectionList.get(i);
    }
}
