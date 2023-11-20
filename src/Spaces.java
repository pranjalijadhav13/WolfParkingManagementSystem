// Acknowledgments: This example is a modification of code provided
// by Dimitri Rakitine. Further modified by Shrikanth N C for MySql(MariaDB) support
// Relpace all $USER$ with your unity id and $PASSWORD$ with your 9 digit student id or updated password (if changed)

import java.sql.*;
import java.util.Scanner;

public class Spaces {

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
					System.out.println("\n Spaces menu:");
				    System.out.println("1. Add Space information");
				    System.out.println("2. Update Space information");
			        System.out.println("3. Delete Space information");
			        System.out.println("4. Assign type to Space");
			        System.out.println("5. Exit");
			        System.out.println("6. Go back to main menu");
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
			            	insertSpace();
			            	break;
			            case 2 :
			            	updateSpace();
			            	break;
			            case 3 :
			            	deleteSpace();
			            	break;
			            case 4 :
			            	assignTypeToSpace();
			            	break;
			            case 5 :
			            	exit = true;
			            	break;
			            case 6 :
			            	Main.main(args);
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
    
    public static void insertSpace() throws SQLException{
        Scanner scanner = new Scanner(System.in);
        

        System.out.println("\nExisting Spaces are: ");
        printTable("Spaces");

        int spaceNumber;
        String zoneID;
        String parkingLotName;
        String availability;
        
        while (true){
        	System.out.println();

            System.out.println("Enter a new Space Number: ");
            spaceNumber = scanner.nextInt();
            
            System.out.println("Enter a Zone Id: ");
            zoneID= scanner.next();
            scanner.nextLine();
            System.out.println("Enter a ParkingLot Name: ");
            parkingLotName = scanner.nextLine();
            
            scanner.nextLine();
            System.out.println("Enter availability: ");
            availability = scanner.nextLine();
           
           
            if (doesRecordExists("Zones", zoneID, parkingLotName)) {
	            if (doesRecordExists("Spaces", spaceNumber, zoneID, parkingLotName)) {
	                System.out.println("Space already exists with space Number: " + spaceNumber + " ,zone ID " + zoneID + ", parkingLotName " + parkingLotName + " , please enter new.");
	            } else {
	            	insertSpace(spaceNumber,zoneID,parkingLotName, availability);
	                break;
	            }
            } 
            else {
            	System.out.println("Zone " + zoneID + " " + parkingLotName + " doesn't exist in parent table, please enter another input.");
                
            }
        }
    }
    
    public static void insertSpace(int spaceNumber,String zoneID, String parkingLotName, String availability) throws SQLException{
      
        try (Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            ResultSet uprs = stmt.executeQuery("SELECT * FROM Spaces");
            uprs.moveToInsertRow();
            uprs.updateInt("spaceNumber", spaceNumber);
            uprs.updateString("zoneID", zoneID);
            uprs.updateString("parkingLotName", parkingLotName);
            uprs.updateString("availability", availability);

            uprs.insertRow();
            uprs.beforeFirst();

        } catch (SQLException sqlException){
            System.out.println("Error: " + sqlException.getMessage());
            throw new SQLException("Error in Insertion SQL statement.");
        }
        System.out.println("\n****************************************************************");
        System.out.println("Spaces added successfully with spaceNumber: " + spaceNumber + ", zone ID: " + zoneID + ", parking lot " +  parkingLotName + " in the database.");
        System.out.println("****************************************************************\n");

        System.out.println("\nAfter insertion, Spaces table: ");
        printTable("Spaces");
    }
    
    public static void updateSpace() throws SQLException {
    	Scanner scanner = new Scanner(System.in);
    	
    	System.out.println("\nExisting Spaces are: ");
        printTable("Spaces");

        System.out.println("1. spaceNumber");
        System.out.println("2. zoneID");
        System.out.println("3. parkingLotName");
        System.out.println("4. availability");
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

        int spaceNumber;
        String zoneID;
        String parkingLotName;
        while (true){
        	System.out.println("Enter the space number to be updated");
            spaceNumber = scanner.nextInt();
            System.out.println("Enter the zoneID to be updated");
            zoneID = scanner.next();
            scanner.nextLine();
            System.out.println("Enter the parking lot name to be updated");
            parkingLotName = scanner.nextLine();

            if (!doesRecordExists("Spaces", spaceNumber, zoneID,parkingLotName)) 
            {
                System.out.println("Space does not exist\nThe available Spaces are:");

                /*String sQuery = "SELECT spaceNumber, zoneID, parkingLotName FROM Spaces;";
                ResultSet rs = stmt.executeQuery(sQuery);
                while (rs.next())
                {
                	int sId2 = rs.getInt(spaceNumber);
                    String sId = rs.getString("zoneID");
                    String sId1 = rs.getString("parkingLotName");
                    // print the results
                    System.out.printf("%d %s %s\n", sId2 , sId, sId1);
                }
                System.out.println();
                */
                printTable("Spaces");

            }
            else {
                break;
            }
        }
        
	        if (doesRecordExists("Spaces", spaceNumber, zoneID,parkingLotName)) {
	
	            //update the given actions
	
	            switch (updateChoice) {
	                case 1 : {
	                	System.out.print("Enter the new spaceNumber: ");
	                    int updatedSpaceNumber=scanner.nextInt();
	                    stmt.executeUpdate("UPDATE Spaces SET spaceNumber = \'" + updatedSpaceNumber + "\' WHERE spaceNumber = \'" + spaceNumber + "\' AND zoneID = \'" + zoneID + "\' AND parkingLotName = \'" + parkingLotName + "\';");
	                    System.out.println("Space Number updated successfully.");
	                    showSpaceInfo(updatedSpaceNumber,zoneID, parkingLotName);
	                    break;
	                    
	                }
	                case 2 : {
	                	System.out.print("Enter the new zone ID: ");
	                    String updatedZoneID = scanner.nextLine();
	                	if(doesRecordExists("Zones", updatedZoneID, parkingLotName)) {
	                		stmt.executeUpdate("UPDATE Spaces SET zoneID = \'" + updatedZoneID + "\' WHERE spaceNumber = \'" + spaceNumber + "\' AND zoneID = \'" + zoneID + "\' AND parkingLotName = \'" + parkingLotName + "\';");
		                    System.out.println("Zone ID updated successfully.");
		                    showSpaceInfo(spaceNumber,updatedZoneID, parkingLotName);
	                	}
	                	else {
	                		System.out.println("Zone ID: " + updatedZoneID + " doesn't exist in parent(Zones) table ,please enter another zone ID.");
	                    }
	                    break;
	                }
	                case 3 : {
	                	System.out.print("Enter the new parking lot name: ");
	                    String updatedParkingLotName= scanner.nextLine();
	                	if(doesRecordExists("ParkingLot",updatedParkingLotName)) {
	                		if(doesRecordExists("Zones", zoneID, updatedParkingLotName)) {
	                		stmt.executeUpdate("UPDATE Spaces SET parkingLotName = \'" + updatedParkingLotName	  + "\' WHERE spaceNumber = \'" + spaceNumber + "\' AND zoneID = \'" + zoneID + "\' AND parkingLotName = \'" + parkingLotName + "\';");
		                    System.out.println("Parking Lot Name updated successfully.");
		                    showSpaceInfo(spaceNumber,zoneID, updatedParkingLotName);
	                		}
	                		else {
	                			System.out.println("Zone ID: " + zoneID + " " + updatedParkingLotName + " doesn't exist in parent(Zones) table ,please enter another parking lot name.");
	      	                  
	                		}
	                	}
	                	else {
	                		System.out.println("Parking lot name: " + updatedParkingLotName + " doesn't exist in parent(ParkingLot) table ,please enter another lot name.");
	                    }
	                    break;
	                }
	                case 4 : {
	                	System.out.print("Enter the new availability: ");
	                    String updatedAvail= scanner.nextLine();
	                	
                		stmt.executeUpdate("UPDATE Spaces SET availability = \'" + updatedAvail + "\' WHERE spaceNumber = \'" + spaceNumber + "\' AND zoneID = \'" + zoneID + "\' AND parkingLotName = \'" + parkingLotName + "\';");
	                    System.out.println("Availability updated successfully.");
	                    showSpaceInfo(spaceNumber,zoneID, parkingLotName);
	                	
	                    break;
	                }
	               
	                default :
	                	System.out.println("Invalid choice. Please try again.");
	            }
	           
	        }
	        else{
	            System.out.println("Space does not exists\nThe available Spaces are:");
	           
	            String sQuery = "SELECT spaceNumber, zoneID, parkingLotName FROM Spaces;";
	            ResultSet rs = stmt.executeQuery(sQuery);
	            while (rs.next())
	            {
	            	int sId2 = rs.getInt("spaceNumber");
	            	String sId = rs.getString("zoneID");
	                String sId1 = rs.getString("parkingLotName");
	                // print the results
	                System.out.printf("%d %s %s\n", sId2, sId, sId1);
	            }
	            System.out.println();
	        } 
      
    }
    
    public static void deleteSpace() throws SQLException{
        Scanner scanner = new Scanner(System.in);

        System.out.print("Existing Spaces are: ");
        printTable("Spaces");

        System.out.print("Enter the spaceNumber to be deleted: ");
        int spaceNumber = scanner.nextInt();
        
        System.out.println("Enter the zoneID to be deleted: ");
        String zoneID = scanner.next();
        
        scanner.nextLine();
        System.out.println("Enter the parkingLotName to be deleted: ");
        String parkingLotName = scanner.nextLine();

        if (doesRecordExists("Spaces", spaceNumber, zoneID, parkingLotName)){
    //delete the given item

            stmt.executeUpdate("DELETE FROM Spaces WHERE spaceNumber = \'" + spaceNumber + "\' AND zoneID = \'" + zoneID + "\' AND parkingLotName = \'" + parkingLotName + "\';");
            System.out.println("Space number:  " + spaceNumber + ",zone ID" + zoneID + " ,parkingLotName" +  parkingLotName + " deleted successfully.");

            System.out.println("After Deletion: ");
            printTable("Spaces");
        } else {
            System.out.println("Spaces does not exists\nThe available Spaces are:");
            printTable("Spaces");
        }
    }
    
    
    public static void assignTypeToSpace() throws SQLException{
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nExisting Permit and Space combinations are: ");
        printTable("Contains");
 
        while (true){
        	System.out.println("Enter the permit ID to which space is to be assigned");
            String permitID = scanner.nextLine();
            
            System.out.print("Enter the spaceNumber : ");
            int spaceNumber = scanner.nextInt();
            
            System.out.println("Enter the zoneID : ");
            String zoneID = scanner.next();
            scanner.nextLine();
            System.out.print("Enter the parkingLotName : ");
            String parkingLotName = scanner.nextLine();
            
            System.out.print("Enter the space type to be assigned: ");
            String spaceType = scanner.nextLine();
            
            if (doesRecordExists("Permits", permitID) & doesRecordExists("Spaces", spaceNumber, zoneID, parkingLotName)) {
            	
            	if(doesRecordExists ("Contains", permitID, spaceNumber, zoneID, parkingLotName)) {
            		System.out.println("Space type: " + spaceType + "  already assigned to Space : " + spaceNumber + " " + zoneID + " " + parkingLotName + " , please enter new.");
            	}
            	else {
            		assignTypeToSpace(permitID,spaceNumber,zoneID, parkingLotName, spaceType);
                break;
            	}
            	  }
            else if (doesRecordExists("Permits", permitID) & !doesRecordExists("Spaces", spaceNumber, zoneID, parkingLotName)) {
            	System.out.println("Spaces: " + spaceNumber + " " + zoneID + " " + parkingLotName + " doesn't exist in parent(Spaces) table ,please enter another car license number.");
                
            }
        	else if (!doesRecordExists("Permits", permitID) & doesRecordExists("Spaces", spaceNumber, zoneID, parkingLotName)){
        		System.out.println("Permit: " + permitID + " doesn't exist in parent(Permits) table ,please enter another permit ID.");
            }
            
        	else{ 
        		System.out.println("Permit: " + permitID + " doesn't exist in parent(Permits) table and Space: "+ spaceNumber + " " + zoneID + " " + parkingLotName + " doesn't exist in parent(Spaces) table ,please enter other input.");
            }
        }
    }
    
    public static void assignTypeToSpace(String permitID,int spaceNumber, String zoneID, String parkingLotName, String spaceType) throws SQLException{
        System.out.println("\nExisting Permit and Space combinations are: ");
        printTable("Contains");

        try (Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            ResultSet uprs = stmt.executeQuery("SELECT * FROM Contains");
            uprs.moveToInsertRow();
            uprs.updateString("permitID", permitID);
            uprs.updateInt("spaceNumber", spaceNumber);
            uprs.updateString("zoneID", zoneID);
            uprs.updateString("parkingLotName", parkingLotName);
            uprs.updateString("spaceType", spaceType);
            uprs.insertRow();
            uprs.beforeFirst();

        } catch (SQLException sqlException){
            System.out.println("Error: " + sqlException.getMessage());
            throw new SQLException("Error in Insertion SQL statement.");
        }
        System.out.println("\n****************************************************************");
        System.out.println("Space Type: " + spaceType + " assigned to Space: " + spaceNumber + " " + zoneID + " " + parkingLotName + "successfully in the database.");
        System.out.println("****************************************************************\n");

        System.out.println("\nAfter insertion, Contains table: ");
        printTable("Contains");
    }
      
    public static void showSpaceInfo(int spaceNumber, String zoneID, String parkingLotName) throws SQLException{
        String sQuery = "SELECT * FROM Spaces WHERE spaceNumber = \'" + spaceNumber + "\' AND zoneID = \'" + zoneID + "\' AND parkingLotName = \'" + parkingLotName + "\'";
        ResultSet rs = stmt.executeQuery(sQuery);
        DBTablePrinter.printResultSet(rs);
    }
    public static boolean permitExists (String permitID) throws SQLException {
        boolean alExists = false;
        ResultSet rs = stmt.executeQuery("SELECT * FROM Permits WHERE permitID = \'" + permitID + "\'");
        if (rs.next()) {
            // If the query returned a row, the song exists
            alExists = true;
        }
        return alExists;
    }
    
    public static boolean parkingLotExists (String parkingLotName) throws SQLException {
        boolean alExists = false;
        ResultSet rs = stmt.executeQuery("SELECT * FROM ParkingLot WHERE parkingLotName = \'" + parkingLotName + "\'");
        if (rs.next()) {
            // If the query returned a row, the song exists
            alExists = true;
        }
        return alExists;
    }
     
    public static boolean zoneExists(String zoneID, String parkingLotname) throws SQLException {
        boolean alExists = false;
        ResultSet rs = stmt.executeQuery("SELECT * FROM Zones WHERE zoneID = \'" + zoneID + "\' AND parkingLotName = \'" + parkingLotname + "\'");
        if (rs.next()) {
            // If the query returned a row, the song exists
            alExists = true;
        }
        return alExists;
    }
    
    public static boolean spaceExists(int spaceNumber, String zoneID, String parkingLotname) throws SQLException {
        boolean alExists = false;
        ResultSet rs = stmt.executeQuery("SELECT * FROM Spaces WHERE spaceNumber = \'" + spaceNumber + "\' AND zoneID = \'" + zoneID + "\' AND parkingLotName = \'" + parkingLotname + "\'");
        if (rs.next()) {
            // If the query returned a row, the song exists
            alExists = true;
        }
        return alExists;
    }
    
    public static boolean containsExists(String permitID, int spaceNumber, String zoneID, String parkingLotname) throws SQLException {
        boolean alExists = false;
        ResultSet rs = stmt.executeQuery("SELECT * FROM Contains WHERE permitID = \'" + permitID + "\' AND spaceNumber = \'" + spaceNumber + "\' AND zoneID = \'" + zoneID + "\' AND parkingLotName = \'" + parkingLotname + "\'");
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
            case "permits":{
            	return permitExists(id1);
            }
            default :
            	System.out.println("No such table exists");
        }
        return recordExists;
    }
    
    public static boolean doesRecordExists(String tableName, String id1, String id2) throws SQLException {
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
    
    public static boolean doesRecordExists(String tableName, int id1, String id2, String id3) throws SQLException {
        boolean recordExists = false;

        switch (tableName.toLowerCase()) {
            	
            case "spaces" :
            {
                return spaceExists(id1, id2, id3);
            }
            default :
            	System.out.println("No such table exists");
        }
        return recordExists;
    }
    
    public static boolean doesRecordExists(String tableName, String id4, int id1, String id2, String id3) throws SQLException {
        boolean recordExists = false;

        switch (tableName.toLowerCase()) {
            	
            case "contains" :
            {
                return containsExists(id4, id1, id2, id3);
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

