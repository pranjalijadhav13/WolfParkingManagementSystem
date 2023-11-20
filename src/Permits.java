// Acknowledgments: This example is a modification of code provided
// by Dimitri Rakitine. Further modified by Shrikanth N C for MySql(MariaDB) support
// Relpace all $USER$ with your unity id and $PASSWORD$ with your 9 digit student id or updated password (if changed)

import java.sql.*;
import java.util.Scanner;

public class Permits {

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
				System.out.println("\n Permit menu:");
			    System.out.println("1. Assign permit to driver");
			    System.out.println("2. Update permit information");
		        System.out.println("3. Delete permit information");
		        System.out.println("4. Adding vehicle to permit");
		        System.out.println("5. Delete vehicle from permit");
		        System.out.println("6. Exit");
		        System.out.println("7. Go back to main menu");
        
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
			            	assignPermitToDriver();
			            	break;
			            case 2 :
			            	updatePermit();
			            	break;
			            case 3 :
			            	deletePermit();
			            	break;
			            case 4:
			            	addVehicleToPermit1();
			            	break;
			            case 5 :
			            	deleteVehicleFromPermit();
			            	break;
			            case 6 :
			            	exit = true;
			            case 7 :
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
    
    //Transaction 1 executed for this function
    public static void assignPermitToDriver() throws SQLException{
    	try {
    	conn.setAutoCommit(false); //Auto-commit is disabled 
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nExisting Permits are: ");
        printTable("Permits");
        
        System.out.println("\nExisting Drivers are: ");
        printTable("Drivers");
        // Genre details
        String permitID;
        String permitType;
        String startDate;
        String expirationDate;
        String expirationTime;
        long driverID;
        while (true){
            System.out.println("Enter a new permit ID: ");
            permitID = scanner.nextLine();
            
            if (doesRecordExists("Permits", permitID)) {
                System.out.println("Permit already exists with ID: " + permitID + ", please enter new.");
            } else {
            	System.out.println("Enter the permit type for new permit: ");
                permitType= scanner.nextLine();
                System.out.println("Enter the start date: ");
                startDate = scanner.nextLine();
                System.out.println("Enter the expiration date: ");
                expirationDate = scanner.nextLine();
                System.out.println("Enter the expiration time: ");
                expirationTime = scanner.nextLine();
                System.out.println("Enter the driver ID: ");
                driverID = scanner.nextLong();
            	
                assignPermitToDriver(permitID,permitType, startDate, expirationDate, expirationTime, driverID);
                break;
            }
        }
        
        
        addVehicleToPermit();//addVehicleToPermit function is called and vehicle gets added to permit on successful completion of the transaction.
        conn.commit();//On successful execution, the transaction is committed.
        }catch (Exception e) {
            System.err.println("Encountered a problem while executing the transaction. Rolling back.");
            //The catch block handles the exception if a problem is encountered while executing the transaction.
            if (conn != null) {
                try {
                    System.err.print("Transaction is being rolled back");//The transaction is being rolled back on exception.
                    System.err.println(e);
                    conn.rollback();
                 // If for a permit, a valid vehicle is not assigned, 
                    //i.e. if the vehicle being added to the permit doesn’t exist in parent(Vehicles table), then the transaction is rolled back.
                } catch (Exception excep) {
                    System.err.println("Encountered an error in rolling back.");//The catch block handles the error in rollback
                    System.err.println(excep);
                }
            }
        }
        finally{
            conn.setAutoCommit(true);//Auto-commit is enabled 
        }
        printTable("AssignedTo");
        printTable("Permits");
    }
    
    public static void assignPermitToDriver(String permitID,String permitType, String startDate, String expirationDate, String expirationTime, long driverID) throws SQLException{
       

        try (Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            ResultSet uprs = stmt.executeQuery("SELECT * FROM Permits");
            uprs.moveToInsertRow();
            uprs.updateString("permitID", permitID);
            uprs.updateString("permitType", permitType);
            uprs.updateString("startDate", startDate);
            uprs.updateString("expirationDate", expirationDate);
            uprs.updateString("expirationTime", expirationTime);
            uprs.updateLong("driverID", driverID);
            
            uprs.insertRow();
            uprs.beforeFirst();

        } catch (SQLException sqlException){
            System.out.println("Error: " + sqlException.getMessage());
            throw new SQLException("Error in Insertion SQL statement.");
        }
        System.out.println("\n****************************************************************");
        System.out.println("Permit added successfully with id: " + permitID + " in the database.");
        System.out.println("****************************************************************\n");

        System.out.println("\nAfter insertion, Permits table: ");
        printTable("Permits");
    }
    
    public static void addVehicleToPermit() throws SQLException{
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nExisting Vehicle and permit combinations are: ");
        printTable("AssignedTo");

        String permitID;
        String carLicenseNumber;
        //while (true){
            System.out.println("Enter a permit ID: ");
            permitID = scanner.nextLine();
           
            System.out.println("Enter the car license number which is to added to the permit ");
            carLicenseNumber = scanner.nextLine();
            
            addVehicleToPermit(permitID,carLicenseNumber);
            
            /*if (doesRecordExists("Permits", permitID) & doesRecordExists("Vehicles", carLicenseNumber)) {
            	
            	if(doesRecordExists ("AssignedTo", permitID, carLicenseNumber)) {
            		System.out.println("Vehicle: " + carLicenseNumber + "  already assigned to permit : " + permitID + ", please enter new.");
            	}
            	else {
            		addVehicleToPermit(permitID,carLicenseNumber);
                break;
            	}
            	  }
            else if (doesRecordExists("Permits", permitID) & !doesRecordExists("Vehicles", carLicenseNumber)) {
            	System.out.println("Vehicle: " + carLicenseNumber+ " doesn't exist in parent(Vehicles) table ,please enter another car license number.");
                
            }
        	else if (!doesRecordExists("Permits", permitID) & doesRecordExists("Vehicles", carLicenseNumber)){
        		System.out.println("Permit: " + permitID + " doesn't exist in parent(Permits) table ,please enter another permit ID.");
            }
            
        	else{ 
        		System.out.println("Permit: " + permitID + " doesn't exist in parent(Permits) table and Vehicle: "+ carLicenseNumber + " doesn't exist in parent(Vehicles) table ,please enter other input.");
            }*/
       // }
    }
    
    public static void addVehicleToPermit(String permitID,String carLicenseNumber) throws SQLException{
        System.out.println("\nExisting Vehicle and permit combinations are: ");
        printTable("AssignedTo");

        try (Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            ResultSet uprs = stmt.executeQuery("SELECT * FROM AssignedTo");
            uprs.moveToInsertRow();
            uprs.updateString("permitID", permitID);
            uprs.updateString("carLicenseNumber", carLicenseNumber);
            uprs.insertRow();
            uprs.beforeFirst();

        } catch (SQLException sqlException){
            System.out.println("Error: " + sqlException.getMessage());
            throw new SQLException("Error in Insertion SQL statement.");
        }
        System.out.println("\n****************************************************************");
        System.out.println("Vehicle: " + carLicenseNumber + " added successfully to permit "  + permitID + " in the database.");
        System.out.println("****************************************************************\n");

        System.out.println("\nAfter insertion, AssignedTo table: ");
        printTable("AssignedTo");
    }
    
    public static void addVehicleToPermit1() throws SQLException{
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nExisting Vehicle and permit combinations are: ");
        printTable("AssignedTo");

        String permitID;
        String carLicenseNumber;
        while (true){
            System.out.println("Enter a permit ID: ");
            permitID = scanner.nextLine();
           
            System.out.println("Enter the car license number which is to added to the permit ");
            carLicenseNumber = scanner.nextLine();
            
            
            
            if (doesRecordExists("Permits", permitID) & doesRecordExists("Vehicles", carLicenseNumber)) {
            	
            	if(doesRecordExists ("AssignedTo", permitID, carLicenseNumber)) {
            		System.out.println("Vehicle: " + carLicenseNumber + "  already assigned to permit : " + permitID + ", please enter new.");
            	}
            	else {
            		addVehicleToPermit1(permitID,carLicenseNumber);
                break;
            	}
            	  }
            else if (doesRecordExists("Permits", permitID) & !doesRecordExists("Vehicles", carLicenseNumber)) {
            	System.out.println("Vehicle: " + carLicenseNumber+ " doesn't exist in parent(Vehicles) table ,please enter another car license number.");
                
            }
        	else if (!doesRecordExists("Permits", permitID) & doesRecordExists("Vehicles", carLicenseNumber)){
        		System.out.println("Permit: " + permitID + " doesn't exist in parent(Permits) table ,please enter another permit ID.");
            }
            
        	else{ 
        		System.out.println("Permit: " + permitID + " doesn't exist in parent(Permits) table and Vehicle: "+ carLicenseNumber + " doesn't exist in parent(Vehicles) table ,please enter other input.");
            }
       }
    }
    
    public static void addVehicleToPermit1(String permitID,String carLicenseNumber) throws SQLException{
        System.out.println("\nExisting Vehicle and permit combinations are: ");
        printTable("AssignedTo");

        try (Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            ResultSet uprs = stmt.executeQuery("SELECT * FROM AssignedTo");
            uprs.moveToInsertRow();
            uprs.updateString("permitID", permitID);
            uprs.updateString("carLicenseNumber", carLicenseNumber);
            uprs.insertRow();
            uprs.beforeFirst();

        } catch (SQLException sqlException){
            System.out.println("Error: " + sqlException.getMessage());
            throw new SQLException("Error in Insertion SQL statement.");
        }
        System.out.println("\n****************************************************************");
        System.out.println("Vehicle: " + carLicenseNumber + " added successfully to permit "  + permitID + " in the database.");
        System.out.println("****************************************************************\n");

        System.out.println("\nAfter insertion, AssignedTo table: ");
        printTable("AssignedTo");
    }
    
    public static void updatePermit() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("1. permitID");
        System.out.println("2. permitType");
        System.out.println("3. startDate");
        System.out.println("4. expirationDate");
        System.out.println("5. expirationTime");
        System.out.println("6. driverID");
       
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

        String permitID;
        while (true){
            System.out.print("Enter the permit ID to be updated");
            permitID = scanner.nextLine();

            if (!doesRecordExists("Permits", permitID)) {
                System.out.println("Permit ID does not exists\nThe available Permits are:");

                String sQuery = "SELECT permitID FROM Permits;";
                ResultSet rs = stmt.executeQuery(sQuery);
                while (rs.next())
                {
                    String sId = rs.getString("permitID");
                    // print the results
                    System.out.format("%s\n", sId);
                }
                System.out.println();

            }
            else {
                break;
            }
        }

        if (doesRecordExists("Permits", permitID)) {

            //update the given actions

            switch (updateChoice) {
                case 1 : {
                    System.out.print("Enter the new permit ID: ");
                    String updatedPermitID= scanner.nextLine();
                    stmt.executeUpdate("UPDATE Permits SET permitID = \'" + updatedPermitID + "\' WHERE permitID = \'" + permitID + "\';");
                    System.out.println("Permit ID updated successfully.");
                    showPermitInfo(updatedPermitID);
                    break;
                }
                case 2 : {
                    String updatedType;
                    
                            System.out.print("Enter the new permit type: ");
                            updatedType = scanner.nextLine();
                           
                    stmt.executeUpdate("UPDATE Permits SET permitType = \'" + updatedType + "\' WHERE permitID = \'" + permitID + "\';");
                    System.out.println("Permit type updated successfully.");
                    break;
                }
                case 3 : {
                    String updatedStartDate;
                    
                            System.out.print("Enter the new start date: ");
                            updatedStartDate = scanner.nextLine();
                           

                    stmt.executeUpdate("UPDATE Permits SET startDate = \'" + updatedStartDate + "\' WHERE permitID = \'" + permitID + "\';");
                    System.out.println("Permit start date updated successfully.");
                    break;
                }
                
                case 4 : {
                    String updatedExpDate;
                    
                            System.out.print("Enter the new expiration date: ");
                            updatedExpDate = scanner.nextLine();
                           
                    stmt.executeUpdate("UPDATE Permits SET expirationDate = \'" + updatedExpDate + "\' WHERE permitID = \'" + permitID + "\';");
                    System.out.println("Permit expiration date updated successfully.");
                    break;
                }
                
                case 5 : {
                    String updatedExpTime;
                    
                            System.out.print("Enter the new expiration time: ");
                            updatedExpTime = scanner.nextLine();
                           
                    stmt.executeUpdate("UPDATE Permits SET expirationTime = \'" + updatedExpTime + "\' WHERE permitID = \'" + permitID + "\';");
                    System.out.println("Permit expiration time updated successfully.");
                    break;
                }
                
                case 6 : {
                    long updatedDriverID;
                   
                            System.out.print("Enter the new driver ID: ");
                            updatedDriverID = scanner.nextLong();
                            
                    if(doesRecordExists("Drivers", updatedDriverID)) {
                    	stmt.executeUpdate("UPDATE Permits SET driverID = \'" + updatedDriverID + "\' WHERE permitID = \'" + permitID + "\';");
                        System.out.println("Driver ID associated with the Permit has been updated successfully.");
                    }
                    else {
                    	System.out.println("The new driver ID doesn't exist in Drivers table. Can't update the row.");
                    }
                    break;
                }
                
                default :
                	System.out.println("Invalid choice. Please try again.");
            }
            
        }
        else{
            System.out.println("Driver ID does not exists\nThe available driver IDs are:");

            String sQuery = "SELECT ID FROM Drivers;";
            ResultSet rs = stmt.executeQuery(sQuery);
            while (rs.next())
            {
                int sId = rs.getInt("ID");
                // print the results
                System.out.format("%s\n", sId);
            }
            System.out.println();

        }
    }
    
    public static void deletePermit() throws SQLException{
        Scanner scanner = new Scanner(System.in);

        System.out.print("Existing Permits are: ");
        printTable("Permits");

        System.out.print("Enter the permit ID to be deleted: ");
        String permitID = scanner.nextLine();

        if (doesRecordExists("Permits", permitID)){
    //delete the given item

            stmt.executeUpdate("DELETE FROM Permits WHERE permitID = \'" + permitID + "\';");
            System.out.println("Permit information with ID " + permitID + " deleted successfully.");

            System.out.println("After Deletion: ");
            printTable("Permits");
        } else {
            System.out.println("Permits does not exists\nThe available Permits are:");
            printTable("Permits");
        }
    }
    
    public static void deleteVehicleFromPermit() throws SQLException{
        Scanner scanner = new Scanner(System.in);

        System.out.print("Existing Vehicle and Permit combinations are: ");
        printTable("AssignedTo");
        
        System.out.print("Enter the permitID to be deleted: ");
        String permitID = scanner.next();
        
        System.out.print("Enter the car license number to be deleted from the permit: ");
        String carLicenseNumber = scanner.next();

        if (doesRecordExists("AssignedTo", permitID, carLicenseNumber)){
    //delete the given item

            stmt.executeUpdate("DELETE FROM AssignedTo WHERE permitID = \'" + permitID + "\' AND carLicenseNumber = \'" + carLicenseNumber + "\'");
            System.out.println("Vehicle " + carLicenseNumber + " deleted from permit " + permitID +  " successfully.");

            System.out.println("After Deletion: ");
            printTable("AssignedTo");
        } else {
            System.out.println("Vehicle Permit combination does not exists\nThe available combinations are:");
            printTable("AssignedTo");
        }
    }
     
    public static void showPermitInfo(String permitID) throws SQLException{
        String sQuery = "SELECT * FROM Permits WHERE permitID = \'" + permitID + "\'";
        ResultSet rs = stmt.executeQuery(sQuery);
        DBTablePrinter.printResultSet(rs);
    }
    
    public static boolean permitExists(String permitID) throws SQLException {
        boolean alExists = false;
        ResultSet rs = stmt.executeQuery("SELECT * FROM Permits WHERE permitID = \'" + permitID + "\'");
        if (rs.next()) {
            // If the query returned a row, the song exists
            alExists = true;
        }
        return alExists;
    }
    
    public static boolean assignedToExists(String permitID, String carLicenseNumber) throws SQLException {
        boolean alExists = false;
        ResultSet rs = stmt.executeQuery("SELECT * FROM AssignedTo WHERE permitID = \'" + permitID + "\' AND carLicenseNumber = \'" + carLicenseNumber + "\' ");
        if (rs.next()) {
            // If the query returned a row, the song exists
            alExists = true;
        }
        return alExists;
    }
     
    public static boolean driverExists(long ID) throws SQLException {
        boolean alExists = false;
        ResultSet rs = stmt.executeQuery("SELECT * FROM Drivers WHERE ID = \'" + ID + "\'");
        if (rs.next()) {
            // If the query returned a row, the song exists
            alExists = true;
        }
        return alExists;
    }
    
    public static boolean vehicleExists(String carLicenseNumber) throws SQLException {
        boolean alExists = false;
        ResultSet rs = stmt.executeQuery("SELECT * FROM Vehicles WHERE carLicenseNumber = \'" + carLicenseNumber + "\'");
        if (rs.next()) {
            // If the query returned a row, the song exists
            alExists = true;
        }
        return alExists;
    }
    
    public static boolean doesRecordExists(String tableName, String id) throws SQLException {
        boolean recordExists = false;

        switch (tableName.toLowerCase()) {
            	
            case "permits" :
            {
                return permitExists(id);
            }
            case "vehicles" :
            {
                return vehicleExists(id);
            } 
            default :
            	System.out.println("No such table exists");
        }
        return recordExists;
    }
    
    public static boolean doesRecordExists(String tableName, long id) throws SQLException {
        boolean recordExists = false;

        switch (tableName.toLowerCase()) {
            	
            case "drivers" :
            {
                return driverExists(id);
            }
            default :
            	System.out.println("No such table exists");
        }
        return recordExists;
    }
    
    public static boolean doesRecordExists(String tableName, String id, String id1) throws SQLException {
        boolean recordExists = false;

        switch (tableName.toLowerCase()) {
            	
            case "assignedto" :
            {
                return assignedToExists(id, id1);
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
