// Acknowledgments: This example is a modification of code provided
// by Dimitri Rakitine. Further modified by Shrikanth N C for MySql(MariaDB) support
// Relpace all $USER$ with your unity id and $PASSWORD$ with your 9 digit student id or updated password (if changed)

import java.sql.*;
import java.util.Scanner;

public class Drivers {

    static final String jdbcURL = "jdbc:mariadb://classdb2.csc.ncsu.edu:3306/psjadhav";

	static final String user = "psjadhav";
	static final String passwd = "200508780";

    public static Connection conn = null;
    public static Statement stmt = null;
    public static final ResultSet rs = null;

    public static void main(String[] args) {
     try {
    	 connectToDatabase();
    	 boolean exit = false;
	    try {
	    	while (!exit) {
			System.out.println("\n Driver menu:");
		    System.out.println("1. Add driver information");
		    System.out.println("2. Update driver information");
	        System.out.println("3. Delete driver information");
	        System.out.println("4. Exit");
	        System.out.println("5. Go back to Main menu");
	        
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
			            	insertDriver();
			            	break;
			            case 2 :
			            	updateDriver();
			            	break;
			            case 3 :
			            	deleteDriver();
			            	break;
			            case 4:
			            	exit=true;
			            	break;
			            case 5:
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
    
    public static void insertDriver() throws SQLException{
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nExisting Drivers are: ");
        printTable("Drivers");

        // Genre details
        long ID;
        String name;
        String status;
        while (true){
            System.out.println("Enter a new driver ID: ");
            ID = scanner.nextLong();
            System.out.println("Enter a new status: ");
            status = scanner.next();
            scanner.nextLine();
            System.out.println("Enter a new name: ");
            name= scanner.nextLine();
            
            if (doesRecordExists("Drivers", ID)) {
                System.out.println("Driver already exists with ID: " + ID + ", please enter new.");
            } else {
                insertDriver(ID,name, status);
                break;
            }
        }
    }
    
    public static void insertDriver(long ID,String name, String status) throws SQLException{
        System.out.println("\nExisting Drivers are: ");
        printTable("Drivers");

        try (Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            ResultSet uprs = stmt.executeQuery("SELECT * FROM Drivers");
            uprs.moveToInsertRow();
            uprs.updateLong("ID", ID);
            uprs.updateString("name", name);
            uprs.updateString("status", status);

            uprs.insertRow();
            uprs.beforeFirst();

        } catch (SQLException sqlException){
            System.out.println("Error: " + sqlException.getMessage());
            throw new SQLException("Error in Insertion SQL statement.");
        }
        System.out.println("\n****************************************************************");
        System.out.println("Driver added successfully with id: " + ID + " in the database.");
        System.out.println("****************************************************************\n");

        System.out.println("\nAfter insertion, Drivers table: ");
        printTable("Drivers");
    }
    
    public static void updateDriver() throws SQLException {
    	System.out.println("\nExisting Drivers are: ");
        printTable("Drivers");
        Scanner scanner = new Scanner(System.in);

        System.out.println("1. ID");
        System.out.println("2. name");
        System.out.println("3. status");
       
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

        long ID;
        while (true){
            System.out.print("Enter the driver ID to be updated");
            ID = scanner.nextLong();

            if (!doesRecordExists("Drivers", ID)) {
                System.out.println("Driver ID does not exists\nThe available driver IDs are:");

                String sQuery = "SELECT ID FROM Drivers;";
                ResultSet rs = stmt.executeQuery(sQuery);
                while (rs.next())
                {
                    long sId = rs.getLong("ID");
                    // print the results
                    System.out.format("%d\n", sId);
                }
                System.out.println();

            }
            else {
                break;
            }
        }

        if (doesRecordExists("Drivers", ID)) {

            //update the given actions

            switch (updateChoice) {
                case 1 : 
                {
                    System.out.print("Enter the new Driver ID: ");
                    long updatedDriverID= scanner.nextLong();
                    stmt.executeUpdate("UPDATE Drivers SET ID = \'" + updatedDriverID + "\' WHERE ID = \'" + ID + "\'");
                    System.out.println("Driver ID updated successfully.");
                    showDriverInfo(updatedDriverID);
                }
                break;
                case 2 : 
                {
                    String updatedName;
                    scanner.nextLine();
                    System.out.print("Enter the new name: ");
                    updatedName = scanner.nextLine();
                   
                    stmt.executeUpdate("UPDATE Drivers SET name = \'" + updatedName + "\' WHERE ID = \'" + ID + "\'");
                    System.out.println("Driver name updated successfully.");
                    showDriverInfo(ID);
                }
                break;
                case 3 : {
                    String updatedStatus;
                    
                    System.out.print("Enter the new status: ");
                    updatedStatus = scanner.next();
                 
                    stmt.executeUpdate("UPDATE Drivers SET status = \'" + updatedStatus + "\' WHERE ID = \'" + ID + "\'");
                    System.out.println("Driver status updated successfully.");
                    showDriverInfo(ID);
                }
                break;
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
                long sId = rs.getLong("ID");
                // print the results
                System.out.format("%d\n", sId);
            }
            System.out.println();

        }
    }
    
    public static void deleteDriver() throws SQLException{
        Scanner scanner = new Scanner(System.in);

        System.out.print("Existing Drivers are: ");
        printTable("Drivers");

        System.out.print("Enter the driver ID to be deleted: ");
        long ID = scanner.nextLong();

        if (doesRecordExists("Drivers", ID)){
    //delete the given item

            stmt.executeUpdate("DELETE FROM Drivers WHERE ID = \'" + ID + "\';");
            System.out.println("Driver information with ID " + ID + " deleted successfully.");

            System.out.println("After Deletion: ");
            printTable("Drivers");
        } else {
            System.out.println("Drivers does not exists\nThe available Drivers are:");
            printTable("Drivers");
        }
        
    }
    
    
    
    public static void showDriverInfo(long ID) throws SQLException{
        String sQuery = "SELECT * FROM Drivers WHERE ID = \'" + ID + "\'";
        ResultSet rs = stmt.executeQuery(sQuery);
        DBTablePrinter.printResultSet(rs);
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
