// Acknowledgments: This example is a modification of code provided
// by Dimitri Rakitine. Further modified by Shrikanth N C for MySql(MariaDB) support
// Relpace all $USER$ with your unity id and $PASSWORD$ with your 9 digit student id or updated password (if changed)

import java.sql.*;
import java.util.Scanner;

public class ParkingLot {

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
			System.out.println("\n Parking Lot menu:");
		    System.out.println("1. Add Parking lot");
		    System.out.println("2. Update Parking lot");
	        System.out.println("3. Delete Parking lot");
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
		            	insertParkingLot();
		            	break;
		            case 2 :
		            	updateParkingLot();
		            	break;
		            case 3 :
		            	deleteParkingLot();
		            	break;
		            case 4:
		            	exit = true;
		            	break;
		            case 5:
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
    
    public static void insertParkingLot() throws SQLException{
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nExisting Parking lots are: ");
        printTable("ParkingLot");

        // Genre details
        String parkingLotName;
        String address;
        while (true){
            System.out.println("Enter a new parking lot name: ");
            parkingLotName = scanner.nextLine();
            System.out.println("Enter a new address for parking lot: ");
            address= scanner.nextLine();
            if (doesRecordExists("ParkingLot", parkingLotName )) {
                System.out.println("Parking Lot already exists with name: " + parkingLotName + ", please enter new.");
            } else {
                insertParkingLot(parkingLotName,address);
                break;
            }
        }
    }
    
    public static void insertParkingLot(String parkingLotName,String address) throws SQLException{
        System.out.println("\nExisting Parking Lots are: ");
        printTable("ParkingLot");

        try (Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            ResultSet uprs = stmt.executeQuery("SELECT * FROM ParkingLot");
            uprs.moveToInsertRow();
            uprs.updateString("parkingLotName", parkingLotName);
            uprs.updateString("address", address);
            uprs.insertRow();
            uprs.beforeFirst();

        } catch (SQLException sqlException){
            System.out.println("Error: " + sqlException.getMessage());
            throw new SQLException("Error in Insertion SQL statement.");
        }
        System.out.println("\n****************************************************************");
        System.out.println("Parking Lot added successfully with name: " + parkingLotName + " in the database.");
        System.out.println("****************************************************************\n");

        System.out.println("\nAfter insertion, Parking Lot table: ");
        printTable("ParkingLot");
    }
    
    public static void updateParkingLot() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("1. parkingLotName");
        System.out.println("2. address");
       
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

        String parkingLotName;
        while (true){
            System.out.print("Enter the parking Lot to be updated");
            parkingLotName = scanner.nextLine();

            if (!doesRecordExists("ParkingLot", parkingLotName)) {
                System.out.println("Parking Lot name does not exists\nThe available Parking Lots are:");

                String sQuery = "SELECT parkingLotName FROM ParkingLot;";
                ResultSet rs = stmt.executeQuery(sQuery);
                while (rs.next())
                {
                    String sId = rs.getString("parkingLotName");
                    // print the results
                    System.out.format("%s\n", sId);
                }
                System.out.println();

            }
            else {
                break;
            }
        }

        if (doesRecordExists("ParkingLot", parkingLotName)) {

            //update the given actions

            switch (updateChoice) {
                case 1 : {
                    System.out.print("Enter the new parking lot name: ");
                    String updatedParkingLotName= scanner.nextLine();
                    stmt.executeUpdate("UPDATE ParkingLot SET parkingLotName = \'" + updatedParkingLotName + "\' WHERE parkingLotName = \'" + parkingLotName + "\';");
                    System.out.println("Parking Lot name updated successfully.");
                    showParkingLotInfo(updatedParkingLotName);
                    break;
                }
                case 2 : {
                	
                    String updatedAddress;
                    
                    System.out.print("Enter the new address: ");
                    updatedAddress = scanner.nextLine();
                          
                    stmt.executeUpdate("UPDATE ParkingLot SET address = \'" + updatedAddress + "\' WHERE parkingLotName = \'" + parkingLotName + "\';");
                    System.out.println("Parking lot address updated successfully.");
                    showParkingLotInfo(parkingLotName);
                    break;
                }
               
                default :
                	System.out.println("Invalid choice. Please try again.");
            }
            //showParkingLotInfo(parkingLotName);   
        }
        else{
            System.out.println("Parking lot name does not exists\nThe available Parking lot names are:");

            String sQuery = "SELECT parkingLotName FROM ParkingLot;";
            ResultSet rs = stmt.executeQuery(sQuery);
            while (rs.next())
            {
                String sId = rs.getString("parkingLotName");
                // print the results
                System.out.format("%s\n", sId);
            }
            System.out.println();

        }
    }
    
    public static void deleteParkingLot() throws SQLException{
        Scanner scanner = new Scanner(System.in);

        System.out.print("Existing Parking Lots are: ");
        printTable("ParkingLot");

        System.out.print("Enter the ParkingLot name to be deleted: ");
        String parkingLotName = scanner.nextLine();

        if (doesRecordExists("ParkingLot", parkingLotName)){
    //delete the given item

            stmt.executeUpdate("DELETE FROM ParkingLot WHERE parkingLotName = \'" + parkingLotName + "\';");
            System.out.println("Parking Lot information with parkingLotName " + parkingLotName + " deleted successfully.");

            System.out.println("After Deletion: ");
            printTable("ParkingLot");
        } else {
            System.out.println("Parking Lot does not exists\nThe available Parking Lots are:");
            printTable("ParkingLot");
        }
    }
    
    
    public static void showParkingLotInfo(String parkingLotName) throws SQLException{
        String sQuery = "SELECT * FROM ParkingLot WHERE parkingLotName = \'" + parkingLotName + "\'";
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
    
    public static boolean doesRecordExists(String tableName, String id) throws SQLException {
        boolean recordExists = false;

        switch (tableName.toLowerCase()) {
            case "parkinglot" :
            {
                return parkingLotExists(id);
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
