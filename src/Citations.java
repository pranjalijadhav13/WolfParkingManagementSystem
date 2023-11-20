// Acknowledgments: This example is a modification of code provided
// by Dimitri Rakitine. Further modified by Shrikanth N C for MySql(MariaDB) support
// Relpace all $USER$ with your unity id and $PASSWORD$ with your 9 digit student id or updated password (if changed)

import java.sql.*;
import java.util.Scanner;

public class Citations {

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
		boolean exit=false;
		while(!exit) {
				System.out.println("\n Citation menu:");
			    System.out.println("1. Add citation");
			    System.out.println("2. Update citation");
		        System.out.println("3. Delete citation");
		        System.out.println("4. Update payment status");
		        System.out.println("5. Request Citation appeal");
		        System.out.println("6. Exit");
		        System.out.println("7. Go back to Citation menu");
		        
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
		            	insertCitation();
		            	break;
		            case 2 :
		            	updateCitation();
		            	break;
		            case 3 :
		            	deleteCitation();
		            	break;
		            case 4: 
		            	updatePaymentStatus();
		            	break;
		            case 5: 
		            	requestCitationAppeal1();
		            	break;
		            case 6: 
		            	exit=true;
		            	break;
		            case 7: 
		            	Citations.main(args);
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
    
    //Transaction 2 executed for this function
    public static void insertCitation() throws SQLException{
    	try {
    		conn.setAutoCommit(false);//Auto-commit is disabled
    
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nExisting Citations are: ");
        printTable("Citations");

        String cNumber;
        String date;
        String time;
        String carLicenseNumber;
        String category;
        int fee=0;
        String paymentStatus;
        String lotName;
        while (true){
            System.out.println("Enter a new citation number: ");
            cNumber = scanner.nextLine();
            System.out.println("Enter the citation date: ");
            date= scanner.nextLine();
            System.out.println("Enter citation time: ");
            time = scanner.nextLine();
            System.out.println("Enter the car License number for which this citation has been issued: ");
            carLicenseNumber = scanner.nextLine();
            System.out.println("Enter citation category: ");
            category = scanner.nextLine();
            if(category.equals("Expired Permit")) {
            	fee=30;
            }
            if(category.equals("No Permit")) {
            	fee=40;
            }
            if(category.equals("Invalid Permit")) {
            	fee=25;
            }
            System.out.println("Enter the parking lot name in whivch citation has been generated: ");
            lotName = scanner.nextLine();
            System.out.println("Enter payment status for the citation: ");
            paymentStatus = scanner.nextLine();
            
            if (doesRecordExists("Citation", cNumber)) {
                System.out.println("Citation already exists with ID: " + cNumber + ", please enter new.");
            } else {
                insertCitation(cNumber, date, time, carLicenseNumber, category, fee, paymentStatus, lotName);
                break;
            }
        }
        
        updateFeeForHandicap();
        
        requestCitationAppeal();//requestCitationAppeal function is called to request an appeal for a citation.
        conn.commit();//On successful execution, the transaction is committed.
    	}catch (Exception e) {
            System.err.println("Encountered a problem while executing the transaction. Rolling back.");
            //The catch block handles the exception if a problem is encountered while executing the transaction.
            if (conn != null) {
                try {
                    System.err.print("Transaction is being rolled back");//The transaction is being rolled back on exception
                    System.err.println(e);
                    conn.rollback();
                    // When an invalid citation number is entered, 
                    //i.e. the citation number does not exist in the citation table (while requesting a appeal), then the transaction is rollbacked.
                } catch (Exception excep) {
                    System.err.println("Encountered an error in rolling back.");//The catch block handles the error in rollback
                    System.err.println(excep);
                }
            }
        }
        finally{
            conn.setAutoCommit(true);
        }
        printTable("Citations");
        printTable("Appeal");
    }
    
    public static void insertCitation(String cNumber, String date, String time, String carLicenseNumber, String category, int fee, String
    		paymentStatus,String lotName) throws SQLException{
        System.out.println("\nExisting Citations are: ");
        printTable("Citations");

        try (Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            ResultSet uprs = stmt.executeQuery("SELECT * FROM Citations");
            uprs.moveToInsertRow();
            uprs.updateString("cNumber", cNumber);
            uprs.updateString("date", date);
            uprs.updateString("time", time);
            uprs.updateString("carLicenseNumber", carLicenseNumber);
            uprs.updateString("category", category);
            uprs.updateInt("fee", fee);
            uprs.updateString("paymentStatus", paymentStatus);
            uprs.updateString("lotName", lotName);
            uprs.insertRow();
            uprs.beforeFirst();

        } catch (SQLException sqlException){
            System.out.println("Error: " + sqlException.getMessage());
            throw new SQLException("Error in Insertion SQL statement.");
        }
        System.out.println("\n****************************************************************");
        System.out.println("Citation added successfully with id: " + cNumber + " in the database.");
        System.out.println("****************************************************************\n");

        System.out.println("\nAfter insertion, Citations table: ");
        printTable("Citations");
    }
    
    public static void updateCitation() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("1. carLicenseNumber");
        System.out.println("2. category");
        System.out.println("3. Parking Lot");
        System.out.println("4. Appeal status");
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

        String cNumber;
        while (true){
            System.out.print("Enter the citation number to be updated");
            cNumber = scanner.nextLine();

            if (!doesRecordExists("Citations", cNumber)) {
                System.out.println("Citation does not exists\nThe available citations are:");

                String sQuery = "SELECT cNumber FROM Citations;";
                ResultSet rs = stmt.executeQuery(sQuery);
                while (rs.next())
                {
                    String sId = rs.getString("cNumber");
                    // print the results
                    System.out.format("%d\n", sId);
                }
                System.out.println();

            }
            else {
                break;
            }
        }

        if (doesRecordExists("Citations", cNumber)) {

            //update the given actions

            switch (updateChoice) {
                case 1 : {
                    System.out.print("Enter the new car license number: ");
                    String updatedCarLicenseNumber= scanner.nextLine();
                    if(doesRecordExists("Vehicles", updatedCarLicenseNumber)) {
                    	stmt.executeUpdate("UPDATE Citations SET carLicenseNumber = \'" + updatedCarLicenseNumber + "\' WHERE cNumber = \'" + cNumber + "\';");
                        System.out.println("Car license number charged by the citation has been updated successfully.");
                    }
                    else {
                    	System.out.println("The new car license number doesn't exist in Vehicles table. Can't update the row.");
                    }
                    showCitationInfo(cNumber);
                }
                break;
                case 2 : {
                    String updatedCategory;
                    
                    System.out.print("Enter the new citation category: ");
                    
                    updatedCategory = scanner.nextLine();
                    stmt.executeUpdate("UPDATE Citations SET category = \'" + updatedCategory + "\' WHERE cNumber = \'" + cNumber + "\';");
                    System.out.println("Citation category updated successfully.");
                    if(updatedCategory.equals("Expired Permit")) {
                    	stmt.executeUpdate("UPDATE Citations SET fee = 30 WHERE cNumber = \'" + cNumber + "\';");
                        
                    }
                    if(updatedCategory.equals("No Permit")) {
                    	stmt.executeUpdate("UPDATE Citations SET fee = 40 WHERE cNumber = \'" + cNumber + "\';");
                    }
                    if(updatedCategory.equals("Invalid Permit")) {
                    	stmt.executeUpdate("UPDATE Citations SET fee = 25 WHERE cNumber = \'" + cNumber + "\';");
                    }
                    updateFeeForHandicap();
                    
                    showCitationInfo(cNumber);
                }
                break;
                
                case 3 : {
                    String updatedLotName;
                    
                    System.out.print("Enter the new parking lot name : ");
                    updatedLotName = scanner.nextLine();
                  
                    if(doesRecordExists("ParkingLot", updatedLotName)) {
                    	stmt.executeUpdate("UPDATE Citations SET lotName = \'" + updatedLotName + "\' WHERE cNumber = \'" + cNumber + "\';");
                        System.out.println("Parking lot for which citation is generated updated successfully.");
                    }
                    else {
                    	System.out.println("The new parking lot name doesn't exist in Parking Lot table. Can't update the row.");
                    }
                    
                    showCitationInfo(cNumber);
                }
                break;
                
                case 4 : {
                   
                    if(doesRecordExists("Appeal", cNumber)) {
                    	stmt.executeUpdate("UPDATE Appeal SET appealStatus = 'Resolved' WHERE cNumber = \'" + cNumber + "\';");
                        System.out.println("Appeal status updated successfully.");
                    }
                    else {
                    	System.out.println("The citation number doesn't exist in Appeal table. Can't update the row.");
                    }
                    printTable("Appeal");
                }
                break; 
                default :
                	System.out.println("Invalid choice. Please try again.");
            }
            
        }
        else{
            System.out.println("Citation does not exists\nThe available citations are:");

            String sQuery = "SELECT cNumber FROM Citations;";
            ResultSet rs = stmt.executeQuery(sQuery);
            while (rs.next())
            {
                String sId = rs.getString("cNumber");
                // print the results
                System.out.format("%s\n", sId);
            }
            System.out.println();

        }
    }
    
    public static void updateFeeForHandicap() throws SQLException {
    	System.out.println("Before updating fees for handicap");
    	printTable("Citations");
    	stmt.executeUpdate("UPDATE Citations\r\n"
    			+ "SET fee = fee * 0.5\r\n"
    			+ "WHERE EXISTS (\r\n"
    			+ "    SELECT 1\r\n"
    			+ "    FROM Permits a\r\n"
    			+ "    JOIN Contains b ON a.permitID = b.permitID\r\n"
    			+ "    JOIN AssignedTo c ON c.permitID = b.permitID\r\n"
    			+ "    WHERE c.carLicenseNumber = Citations.carLicenseNumber AND b.spaceType = 'Handicap'\r\n"
    			+ ");");
    	System.out.println("After updating fees for handicap");
    	printTable("Citations");
        
    }
    
    public static void deleteCitation() throws SQLException{
        Scanner scanner = new Scanner(System.in);

        System.out.print("Existing Citations are: ");
        printTable("Citations");

        System.out.print("Enter the citation number to be deleted: ");
        String cNumber= scanner.nextLine();

        if (doesRecordExists("Citations", cNumber)){
    //delete the given item

            stmt.executeUpdate("DELETE FROM Citations WHERE cNumber = \'" + cNumber + "\';");
            System.out.println("Citation information with ID " + cNumber + " deleted successfully.");

            System.out.println("After Deletion: ");
            printTable("Citations");
        } else {
            System.out.println("Citation does not exist\nThe available Citations are:");
            printTable("Citations");
        }
    }
    
    public static void requestCitationAppeal() throws SQLException{
    	Scanner scanner = new Scanner(System.in);
    	
        System.out.print("Existing Citations are: ");
        printTable("Citations");

        System.out.println("Enter the citation number for which you want to request an appeal: ");
        String cNumber = scanner.nextLine();
    
       
		System.out.println("Enter the description: ");
        String description = scanner.nextLine();
		
		System.out.println("Enter the driver ID: ");
        long driverID = scanner.nextLong();
                
               
    	 try (Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
             ResultSet uprs = stmt.executeQuery("SELECT * FROM Appeal");
             uprs.moveToInsertRow();
             uprs.updateString("cNumber", cNumber);
             uprs.updateLong("driverID", driverID);
             uprs.updateString("description", description);
             uprs.updateString("appealStatus", "In process");
             
             uprs.insertRow();
             uprs.beforeFirst();

         } catch (SQLException sqlException){
             System.out.println("Error: " + sqlException.getMessage());
             throw new SQLException("Error in Insertion SQL statement.");
         }
         System.out.println("\n****************************************************************");
         System.out.println("Appeal requested successfully in the database.");
         System.out.println("****************************************************************\n");
    }
    
    public static void updatePaymentStatus() throws SQLException{//input a citation number not present in the table
    		
	    	Scanner scanner = new Scanner(System.in);
	    	
	        System.out.print("Existing Citations are: ");
	        printTable("Citations");
	
	    
	        String cNumber;
	        while (true){
	        	System.out.print("Enter the citation number for which fee has been paid by the driver: ");
	            cNumber = scanner.nextLine();
	
	            if (!doesRecordExists("Citations", cNumber)) {
	                System.out.println("Citation does not exists\nThe available citations are:");
	
	                String sQuery = "SELECT cNumber FROM Citations;";
	                ResultSet rs = stmt.executeQuery(sQuery);
	                while (rs.next())
	                {
	                    String sId = rs.getString("cNumber");
	                    // print the results
	                    System.out.printf("%s", sId);
	                }
	                System.out.println();
	
	            }
	            else {
	                break;
	            }
	        }
	
	        if (doesRecordExists("Citations", cNumber)) {
	
		        stmt.executeUpdate("UPDATE Citations SET paymentStatus = 'Paid ' WHERE cNumber = \'" + cNumber + "\'");
		        System.out.println("Car license number charged by the citation has been updated successfully.");
		        showCitationInfo(cNumber);
	        }
	        else{
	            System.out.println("Citation does not exists\nThe available citations are:");
	
	            String sQuery = "SELECT cNumber FROM Citations;";
	            ResultSet rs = stmt.executeQuery(sQuery);
	            while (rs.next())
	            {
	                String sId = rs.getString("cNumber");
	                // print the results
	                System.out.format("%s\n", sId);
	            }
	            System.out.println();
	
	        }
        
    	
    }
    
    public static void requestCitationAppeal1() throws SQLException{
    	Scanner scanner = new Scanner(System.in);
    	
        System.out.print("Existing Citations are: ");
        printTable("Citations");

        System.out.println("Enter the citation number for which you want to request an appeal: ");
        String cNumber = scanner.nextLine();
    
        if (doesRecordExists("Citations", cNumber)){
    
        	if(!doesRecordExists("Appeal", cNumber)){
        		
        		System.out.println("Enter the description: ");
                String description = scanner.nextLine();
        		
        		System.out.println("Enter the driver ID: ");
                long driverID = scanner.nextLong();
                
               
	        	 try (Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
	                 ResultSet uprs = stmt.executeQuery("SELECT * FROM Appeal");
	                 uprs.moveToInsertRow();
	                 uprs.updateString("cNumber", cNumber);
	                 uprs.updateLong("driverID", driverID);
	                 uprs.updateString("description", description);
	                 uprs.updateString("appealStatus", "In process");
	                 
	                 uprs.insertRow();
	                 uprs.beforeFirst();
	
	             } catch (SQLException sqlException){
	                 System.out.println("Error: " + sqlException.getMessage());
	                 throw new SQLException("Error in Insertion SQL statement.");
	             }
	             System.out.println("\n****************************************************************");
	             System.out.println("Appeal requested successfully in the database.");
	             System.out.println("****************************************************************\n");
	
	             System.out.println("\nAfter insertion, Appeal table: ");
	             printTable("Appeal");
        	}
        	else {
        		System.out.println(" An appeal has already been requested for citation : " + cNumber + ", please enter new.");
            	
        	}
        	
        } 
    	else{ 
    		System.out.println("Citation: "+ cNumber + " doesn't exist in parent(Citations) table ,please enter other input.");
        }
    }
     
    public static void showCitationInfo(String cNumber) throws SQLException{
        String sQuery = "SELECT * FROM Citations WHERE cNumber = \'" + cNumber + "\'";
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
    
    public static boolean citationExists(String cNumber) throws SQLException {
        boolean alExists = false;
        ResultSet rs = stmt.executeQuery("SELECT * FROM Citations WHERE cNumber = \'" + cNumber + "\'");
        if (rs.next()) {
            // If the query returned a row, the song exists
            alExists = true;
        }
        return alExists;
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
   
   public static boolean appealExists(String cNumber) throws SQLException {
       boolean alExists = false;
       ResultSet rs = stmt.executeQuery("SELECT * FROM Appeal WHERE cNumber = \'" + cNumber + "\'");
       if (rs.next()) {
           // If the query returned a row, the song exists
           alExists = true;
       }
       return alExists;
   }
    public static boolean doesRecordExists(String tableName, String id) throws SQLException {
        boolean recordExists = false;

        switch (tableName.toLowerCase()) {
            	
            case "citations" :
            {
                return citationExists(id);
            }
            case "vehicles" :
            {
                return vehicleExists(id);
            }
            case "parkinglot" :
            {
                return parkingLotExists(id);
            }
            case "appeal" :
            {
                return appealExists(id);
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
