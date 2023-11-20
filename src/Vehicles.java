// Acknowledgments: This example is a modification of code provided
// by Dimitri Rakitine. Further modified by Shrikanth N C for MySql(MariaDB) support
// Relpace all $USER$ with your unity id and $PASSWORD$ with your 9 digit student id or updated password (if changed)

import java.sql.*;
import java.util.Scanner;

public class Vehicles {

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
			System.out.println("\n Vehicle menu:");
		    System.out.println("1. Add vehicle information");
		    System.out.println("2. Update vehicle information");
	        System.out.println("3. Delete vehicle information");
	        System.out.println("4. Exit");
	        System.out.println("5. Main.main(args)");
	        
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
		            	insertVehicle();
		            	break;
		            case 2 :
		            	updateVehicle();
		            	break;
		            case 3 :
		            	deleteVehicleInfo();
		            	break;
		            case 4: 
		            	exit=true;
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
    
    public static void insertVehicle() throws SQLException{
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nExisting Vehicles are: ");
        printTable("Vehicles");

        // Genre details
        String carLicenseNumber;
        String model;
        String color;
        String manufacturer;
        int year;
        while (true){
            System.out.println("Enter a new vehicle license number: ");
            carLicenseNumber = scanner.nextLine();
            System.out.println("Enter the model: ");
            model= scanner.nextLine();
            System.out.println("Enter the manufacturer name: ");
            manufacturer= scanner.nextLine();
            System.out.println("Enter the color: ");
            color= scanner.nextLine();
            System.out.println("Enter the year: ");
            year = scanner.nextInt();
            if (doesRecordExists("Vehicles", carLicenseNumber)) {
                System.out.println("Vehicle already exists with ID: " + carLicenseNumber + ", please enter new.");
            } else {
                insertVehicle(carLicenseNumber,model,color, manufacturer, year);
                break;
            }
        }
    }
    
    public static void insertVehicle(String carLicenseNumber, String model, String color, String manufacturer, int year) throws SQLException{
        System.out.println("\nExisting Vehicles are: ");
        printTable("Vehicles");

        try (Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            ResultSet uprs = stmt.executeQuery("SELECT * FROM Vehicles");
            uprs.moveToInsertRow();
            uprs.updateString("carLicenseNumber", carLicenseNumber);
            uprs.updateString("model", model);
            uprs.updateString("color", color);
            uprs.updateString("manufacturer", manufacturer);
            uprs.updateInt("year", year);

            uprs.insertRow();
            uprs.beforeFirst();

        } catch (SQLException sqlException){
            System.out.println("Error: " + sqlException.getMessage());
            throw new SQLException("Error in Insertion SQL statement.");
        }
        System.out.println("\n****************************************************************");
        System.out.println("Vehicle added successfully with id: " + carLicenseNumber + " in the database.");
        System.out.println("****************************************************************\n");

        System.out.println("\nAfter insertion, Vehicles table: ");
        printTable("Vehicles");
    }
   
    public static void updateVehicle() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("1. carLicenseNumber");
        System.out.println("2. model");
        System.out.println("3. color");
        System.out.println("4. manufacturer");
        System.out.println("5. year");
       
       
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

        String carLicenseNumber;
        while (true){
            System.out.print("Enter the car license  number to be updated");
            carLicenseNumber = scanner.nextLine();

            if (!doesRecordExists("Vehicles", carLicenseNumber)) {
                System.out.println("Vehicle does not exists\nThe available Vehicles are:");

                String sQuery = "SELECT carLicenseNumber FROM Vehicles;";
                ResultSet rs = stmt.executeQuery(sQuery);
                while (rs.next())
                {
                    String sId = rs.getString("carLicenseNumber");
                    // print the results
                    System.out.format("%s\n", sId);
                }
                System.out.println();

            }
            else {
                break;
            }
        }

        if (doesRecordExists("Vehicles", carLicenseNumber)) {

            //update the given actions

            switch (updateChoice) {
                case 1 : {
                    System.out.print("Enter the new car license number: ");
                    String updatedCarLicenseNumber= scanner.nextLine();
                    stmt.executeUpdate("UPDATE Vehicles SET carLicenseNumber = \'" + updatedCarLicenseNumber + "\' WHERE carLicenseNumber = \'" + carLicenseNumber + "\';");
                    System.out.println("Car license Number updated successfully.");
                    showVehicleInfo(updatedCarLicenseNumber);
                }
                break;
                case 2 : {
                    String updatedModel;
                   
                    System.out.print("Enter the new model: ");
                    updatedModel = scanner.nextLine();
            
                    stmt.executeUpdate("UPDATE Vehicles SET model = \'" + updatedModel + "\' WHERE carLicenseNumber = \'" + carLicenseNumber + "\';");
                    System.out.println("Car model updated successfully.");
                    showVehicleInfo(carLicenseNumber);
                    break;
                }
                case 3 : {
                    String updatedColor;
            
                    System.out.print("Enter the new color: ");
                    updatedColor = scanner.nextLine();
                            
                    stmt.executeUpdate("UPDATE Vehicles SET color = \'" + updatedColor + "\' WHERE carLicenseNumber = \'" + carLicenseNumber + "\';");
                    System.out.println("Car color updated successfully.");
                    showVehicleInfo(carLicenseNumber);
                    break;
                }
                case 4 : {
                    String updatedManufacturer;
                    
                    System.out.print("Enter the new manufacturer: ");
                    updatedManufacturer = scanner.nextLine();
                          

                    stmt.executeUpdate("UPDATE Vehicles SET manufacturer = \'" + updatedManufacturer + "\' WHERE carLicenseNumber = \'" + carLicenseNumber + "\';");
                    System.out.println("Car manufacturer updated successfully.");
                    showVehicleInfo(carLicenseNumber);
                    break;
                }
                case 5 : {
                    int updatedYear;
                    
                    System.out.print("Enter the new year: ");
                    updatedYear = scanner.nextInt();
                           
                    stmt.executeUpdate("UPDATE Vehicles SET year = \'" + updatedYear + "\' WHERE carLicenseNumber = \'" + carLicenseNumber + "\';");
                    System.out.println("year updated successfully.");
                    showVehicleInfo(carLicenseNumber);
                    break;
                }
                default :
                	System.out.println("Invalid choice. Please try again.");
            }
            
        }
        else{
            System.out.println("Vehicle does not exists\nThe available Vehicles are:");

            String sQuery = "SELECT carLicenseNumber FROM Vehicles;";
            ResultSet rs = stmt.executeQuery(sQuery);
            while (rs.next())
            {
                String sId = rs.getString("carLicenseNumber");
                // print the results
                System.out.format("%s\n", sId);
            }
            System.out.println();

        }
    }
    
    public static void deleteVehicleInfo() throws SQLException{
    	
        Scanner scanner = new Scanner(System.in);

        System.out.print("Existing Vehicles are: ");
        printTable("Vehicles");

        System.out.print("Enter the car license number to be deleted: ");
        String carLicenseNumber = scanner.nextLine();

        if (doesRecordExists("Vehicles", carLicenseNumber)){
        	//delete the given item*/

            stmt.executeUpdate("DELETE FROM Vehicles WHERE carLicenseNumber = \'" + carLicenseNumber + "\';");
            System.out.println("Vehicle information with ID " + carLicenseNumber + " deleted successfully.");

            System.out.println("After Deletion: ");
            printTable("Vehicles");
        } else {
            System.out.println("Vehicle does not exists\nThe available Vehicles are:");
            printTable("Vehicles");
        } 
    }
    
    public static void showVehicleInfo(String carLicenseNumber) throws SQLException{
        String sQuery = "SELECT * FROM Vehicles WHERE carLicenseNumber = \'" + carLicenseNumber + "\'";
        ResultSet rs = stmt.executeQuery(sQuery);
        DBTablePrinter.printResultSet(rs);
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
    
    public static boolean citationExists(String carLicenseNumber) throws SQLException {
        boolean alExists = false;
        ResultSet rs = stmt.executeQuery("SELECT * FROM Citations WHERE carLicenseNumber = \'" + carLicenseNumber + "\' AND paymentStatus='Due'");
        if (rs.next()) {
            // If the query returned a row, the song exists
            alExists = true;
        }
        return alExists;
    }

    public static boolean doesRecordExists(String tableName, String id) throws SQLException {
        boolean recordExists = false;

        switch (tableName.toLowerCase()) {
            case "vehicles" :
            {
                return vehicleExists(id);
            }
            case "citations":
            {
                return citationExists(id);
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
