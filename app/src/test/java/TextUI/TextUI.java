/**
 * store Inspections into inspectionManager.
 * store Restaurants into RestaurantsManager.
 */

package TextUI;

import Model.Inspection;
import Model.InspectionsManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TextUI {
    public TextUI() throws FileNotFoundException {
        File file = new File("/Users/rattlecruiser/Desktop/inspectionreports_itr1.csv"); //this is my source root.
        Scanner scan = null;
        scan = new Scanner(file);
        Inspection inspection;
        InspectionsManager manager = new InspectionsManager();
        String line;
        line = scan.nextLine();
        int inspectionDate;
        int numCritical;
        int numNonCritical;
        while (scan.hasNextLine()) {
            line = scan.nextLine();
            String[] lineArray = line.split(",");
            inspectionDate = Integer.parseInt(lineArray[1]);
            numCritical = Integer.parseInt(lineArray[3]);
            numNonCritical = Integer.parseInt(lineArray[4]);

            if (lineArray.length == 6){
                inspection = new Inspection(lineArray[0], inspectionDate, lineArray[2], numCritical, numNonCritical, lineArray[5], "");
            }
            else{
                inspection = new Inspection(lineArray[0], inspectionDate, lineArray[2], numCritical, numNonCritical, lineArray[5], lineArray[6]);
            }
            manager.addInspection(inspection);
        }
        scan.close();


    }
}
