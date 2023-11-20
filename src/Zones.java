// Acknowledgments: This example is a modification of code provided
// by Dimitri Rakitine. Further modified by Shrikanth N C for MySql(MariaDB) support
// Relpace all $USER$ with your unity id and $PASSWORD$ with your 9 digit student id or updated password (if changed)

import java.sql.*;
import java.util.Scanner;

public class Zones {

    static final String jdbcURL = "jdbc:mariadb://classdb2.csc.ncsu.edu:3306/psjadhav";

	static final String user = "psjadhav";
	static final String passwd = "200508780";

    public static Connection conn = null;
    public static Statement stmt = null;
    public static final ResultSet rs = null;

    public static void main(String[] args) {
     try {
    	 connectToDatabase();
	    try {
	    	boolean exit = false;
	    	while (!exit) {
					System.out.println("\n Zones menu:");
				    System.out.println("1. Assign Zone to Parking lot");
				    System.out.println("2. Update Zone information");
			        System.out.println("3. Delete Zone information");
			        System.out.println("4. Exit");
			        System.out.println("5. Go back to main menu");
			        
			        Scanner scanner = new Scanner(System.in);
			        int updateChoice;
			        while(true) {
			            try {
			                System.out.print("Enter a choice: ");
			                updateChoice = Integer.parseInt(scanner.nextLine());
			                break;
			            }
			            catch(Exception e) {
			                System.out.println("Please enter a valid choice (numerical)");
			            }
			        }
			
			
			        // Perform the selected action
			        switch (updateChoice) {
			            case 1 :
			            	assignZoneToParkingLot();
			            	break;
			            case 2 :
			            	updateZoneInfo();
			            	break;
			            case 3 :
			            	deleteZoneInfo();
			            	break;
			            case 4 :
			            	exit = true;
			            	break;
			            case 5 :
			            	Main.main(args);;
			            	break;
			            default :
			            	System.out.println("Invalid choice. Please try again.");
			        }
	    		}
            } finally {
                close(rs);
                close(stmt);
                close(conn);
            }
        } catch(Throwable oops) {
            oops.printStackTrace();
        }
    }
    
    public static void printTable(String tableName) throws SQLException {
        String query = "Select * from " + tableName;
        PreparedStatement myStmt = conn.prepareStatement(query);
        ResultSet rs = myStmt.executeQuery();
        DBTablePrinter.printResultSet(rs);
    }
    
    public static void assignZoneToParkingLot() throws SQLException{
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nExisting Zones are: ");
        printTable("Zones");

        String parkingLotName;
        String zoneID;
        while (true){
            System.out.println("Enter a new zone ID: ");
            zoneID = scanner.nextLine();
           
            System.out.println("Enter the parking lot to which this zone belongs: ");
            parkingLotName = scanner.nextLine();
            
            if (doesRecordExists("ParkingLot", parkingLotName)) {
            	
            	if(doesRecordExists1("Zones", zoneID, parkingLotName)) {
            		System.out.println("Zone with name: " + zoneID + "  already exists in Parking lot: " + parkingLotName + ", please enter new.");
            	}
            	else {
            		assignZoneToParkingLot(zoneID,parkingLotName);
                break;
            	}
            	  }
            else {
            	System.out.println("Parking lot: " + parkingLotName + " doesn't exist in parent table, please enter another parking Lot name.");
                
            }
            
        }
    }
    
    public static void assignZoneToParkingLot(String zoneID,String parkingLotName) throws SQLException{
        System.out.println("\nExisting Zones are: ");
        printTable("Zones");

        try (Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            ResultSet uprs = stmt.executeQuery("SELECT * FROM Zones");
            uprs.moveToInsertRow();
            uprs.updateString("zoneID", zoneID);
            uprs.updateString("parkingLotName", parkingLotName);
            uprs.insertRow();
            uprs.beforeFirst();

        } catch (SQLException sqlException){
            System.out.println("Error: " + sqlException.getMessage());
            throw new SQLException("Error in Insertion SQL statement.");
        }
        System.out.println("\n****************************************************************");
        System.out.println("Zone added successfully with ID: " + zoneID + " in Parking Lot "  + parkingLotName + " in the database.");
        System.out.println("****************************************************************\n");

        System.out.println("\nAfter insertion, Zones table: ");
        printTable("Zones");
    }
    
    public static void updateZoneInfo() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("1. zoneID");
        System.out.println("2. parkingLotName");
       
        int updateChoice;
        while(true) {
            try {
                System.out.print("Enter a choice: ");
                updateChoice = Integer.parseInt(scanner.nextLine());
                break;
            }
            catch(Exception e) {
                System.out.println("Please enter a valid choice (numerical)");
            }
        }

        String zoneID;
        String parkingLotName;
        while (true){
            System.out.println("Enter the zoneID to be updated");
            zoneID = scanner.nextLine();
            System.out.println("Enter the parking lot name to be updated");
            parkingLotName = scanner.nextLine();

            if (!doesRecordExists1("Zones", zoneID,parkingLotName)) {
                System.out.println("Zone does not exist\nThe available Zones are:");

                String sQuery = "SELECT zoneID, parkingLotName FROM Zones;";
                ResultSet rs = stmt.executeQuery(sQuery);
                while (rs.next())
                {
                    String sId = rs.getString("zoneID");
                    String sId1 = rs.getString("parkingLotName");
                    // print the results
                    System.out.format("%s  %s\n", sId , sId1);
                }
                System.out.println();

            }
            else {
                break;
            }
        }
        if (doesRecordExists("ParkingLot", parkingLotName)) {
	        if (doesRecordExists1("Zones", zoneID,parkingLotName)) {
	
	            //update the given actions
	
	            switch (updateChoice) {
	                case 1 : {
	                	System.out.println("Enter the new zoneID: ");
	                    String updatedZoneID= scanner.nextLine();
	                    stmt.executeUpdate("UPDATE Zones SET zoneID = \'" + updatedZoneID + "\' WHERE zoneID = \'" + zoneID + "\' AND parkingLotName = \'" + parkingLotName + "\';");
	                    System.out.println("Zone ID updated successfully.");
	                    showZonesInfo(updatedZoneID, parkingLotName);
	                    break;
	                    
	                }
	                case 2 : {
	                	
	                	System.out.println("Enter the new parking Lot Name: ");
	                    String updatedParkingLotName= scanner.nextLine();
	                    if(doesRecordExists("ParkingLot", updatedParkingLotName)) {
	                		stmt.executeUpdate("UPDATE Zones SET parkingLotName = \'" + updatedParkingLotName + "\' WHERE zoneID = \'" + zoneID + "\' AND parkingLotName = \'" + parkingLotName + "\';");
		                    System.out.println("Parking lot name ID updated successfully.");
		                    showZonesInfo(zoneID, updatedParkingLotName);
	                	}
	                	else {
	                		System.out.println("Parking lot: " + updatedParkingLotName + " doesn't exist in parent(Parking Lot) table ,please enter another Parking lot.");
	                    }
	                    break;
	                }
	               
	                default :
	                	System.out.println("Invalid choice. Please try again.");
	            }
	            //showZonesInfo(zoneID, parkingLotName);
	        }
	        else{
	            System.out.println("Zone does not exists\nThe available Zones are:");
	
	            String sQuery = "SELECT zoneID, parkingLotName FROM Zones;";
	            ResultSet rs = stmt.executeQuery(sQuery);
	            while (rs.next())
	            {
	            	String sId = rs.getString("zoneID");
	                String sId1 = rs.getString("parkingLotName");
	                // print the results
	                System.out.format("%s\n", sId + " " + "%s\n" , sId1);
	            }
	            System.out.println();
	        } 
        	 
        }
        else {
        	System.out.println("Child row cannot be updated if the value in column referencing parent table doesn't exist in parent table(foreign key constraint)");
            
        }
    }
    
    public static void deleteZoneInfo() throws SQLException{
        Scanner scanner = new Scanner(System.in);

        System.out.print("Existing Zones are: ");
        printTable("Zones");

        System.out.print("Enter the zone ID to be deleted: ");
        String zoneID = scanner.nextLine();
        System.out.print("Enter the ParkingLot name to which this zone belongs: ");
        String parkingLotName = scanner.nextLine();

       
        if (doesRecordExists1("Zones", zoneID, parkingLotName)){

            stmt.executeUpdate("DELETE FROM Zones WHERE zoneID = \'" + zoneID + "\' AND parkingLotName = \'" + parkingLotName + "\'");
            System.out.println("Zone information with ID: " + zoneID + " & parkingLotName " + parkingLotName + " deleted successfully.");

            System.out.println("After Deletion: ");
            printTable("Zones");
        } else {
            System.out.println("Zone does not exists\nThe available Zones are:");
            printTable("Zones");
        } 
             
    }
    
    
    public static void showZonesInfo(String zoneID, String parkingLotName) throws SQLException{
        String sQuery = "SELECT * FROM Zones WHERE zoneID = \'" + zoneID + "\' AND parkinglotName = \'" + parkingLotName + "\'";
        ResultSet rs = stmt.executeQuery(sQuery);
        DBTablePrinter.printResultSet(rs);
    }
   
    
    public static boolean parkingLotExists(String parkingLotName) throws SQLException {
        boolean alExists = false;
        ResultSet rs = stmt.executeQuery("SELECT * FROM ParkingLot WHERE parkingLotName = \'" + parkingLotName + "\'");
        if (rs.next()) {
            // If the query returned a row, the song exists
            alExists = true;
        }
        return alExists;
    }
    
    public static boolean zoneExists(String zoneID, String parkingLotName) throws SQLException {
        boolean alExists = false;
        ResultSet rs = stmt.executeQuery("SELECT * FROM Zones WHERE zoneID = \'" + zoneID + "\' AND parkingLotName = \'" + parkingLotName + "\'");
        if (rs.next()) {
            // If the query returned a row, the song exists
            alExists = true;
        }
        return alExists;
    }
    
    public static boolean doesRecordExists(String tableName, String id1) throws SQLException {
        boolean recordExists = false;

        switch (tableName.toLowerCase()) {
            case "parkinglot" :
            {
                return parkingLotExists(id1);
            }
            default :
            	System.out.println("No such table exists");
        }
        return recordExists;
    }
    
    public static boolean doesRecordExists1(String tableName, String id1, String id2) throws SQLException {
        boolean recordExists = false;

        switch (tableName.toLowerCase()) {
            case "zones" :
            {
                return zoneExists(id1, id2);
            }
            default :
            	System.out.println("No such table exists");
        }
        return recordExists;
    }

    static void close(Connection conn) {
        if(conn != null) {
            try { conn.close(); } catch(Throwable whatever) {}
        }
    }

    static void close(Statement st) {
        if(st != null) {
            try { st.close(); } catch(Throwable whatever) {}
        }
    }

    static void close(ResultSet rs) {
        if(rs != null) {
            try { rs.close(); } catch(Throwable whatever) {}
        }
    }
    
    public static void connectToDatabase() throws ClassNotFoundException, SQLException {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            conn = DriverManager.getConnection(jdbcURL, user, passwd);
            stmt = conn.createStatement();
        } catch (SQLException e) {
            throw new SQLException("Could not connect to database.");
        }
        catch (Exception e) {
            throw new ClassNotFoundException("Class for driver not found.");
        }
    }
}
