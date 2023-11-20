import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

	public class Reports {
		static final String USERNAME = "psjadhav";
	    static final String PW = "200508780";
	    static final String jdbcURL = "jdbc:mariadb://classdb2.csc.ncsu.edu:3306/psjadhav";

	    public static Connection conn = null;
	    public static Statement stmt = null;
	    public static final ResultSet rs = null;
	   
	    private static void executeQuery(String sqlQuery) {
	        try (Statement statement = conn.createStatement()) {
	            statement.executeUpdate(sqlQuery);
	            System.out.println("Query executed successfully.");
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	    
	    public static void connectToDatabase() throws ClassNotFoundException, SQLException {
	        try {
	            Class.forName("org.mariadb.jdbc.Driver");
	            conn = DriverManager.getConnection(jdbcURL, USERNAME, PW);
	            stmt = conn.createStatement();
	        } catch (SQLException e) {
	            throw new SQLException("Could not connect to database.");
	        }
	        catch (Exception e) {
	            throw new ClassNotFoundException("Class for driver not found.");
	        }
	    }
	    
	    public static void main(String args[]) {
	    	try {
	    		connectToDatabase();
	    		try {
    			boolean exit = false;
    	    	while (!exit) {
		        System.out.println("Reports - Choose operation:");
		        System.out.println("1. Generate Report for Citations");
		        System.out.println("2. Generate Report for Total Citations in a Lot");
		        System.out.println("3. Return List of Zones for Each Lot");
		        System.out.println("4. Return Number of Cars Currently in Violation");
		        System.out.println("5. Return Number of Employees with Permits for a Zone");
		        System.out.println("6. Return Permit Information by ID or Phone Number");
		        System.out.println("7. Return Available Space Number by Space Type in a Lot");
		        System.out.println("8. Return to Main Menu");		        
		        
		        Scanner scanner = new Scanner(System.in);
		        int updateChoice;
		        while(true) {
		            try {
		                System.out.print("Enter a choice: ");
		                updateChoice = scanner.nextInt();
		                break;
		            }
		            catch(Exception e) {
		                System.out.println("Please enter a valid choice (numerical)");
		            }
		        }


	        switch (updateChoice) {
	            case 1:
	                // Implement operation to generate report for citations
	                ResultSet rs = stmt.executeQuery("SELECT Citations.cNumber AS CitationNumber, Citations.date AS CitationDate, Citations.time AS CitationTime, Citations.category AS CitationCategory, Citations.fee AS CitationFee, Citations.paymentStatus AS PaymentStatus, Citations.carLicenseNumber AS CarLicenseNumber, Appeal.driverID AS DriverID, Appeal.appealStatus AS AppealStatus FROM Citations LEFT OUTER JOIN Appeal ON Citations.cNumber = Appeal.cNumber"
	                		);
	                System.out.println("Generating Report for Citations...");
	                DBTablePrinter.printResultSet(rs);
	                break;
	            case 2:
	            	System.out.println("Enter how you want to report generate");
	            	System.out.println("1. For a given time range");
	            	System.out.println("2. Monthly");
	            	System.out.println("3. Annually");
	            	
	            	System.out.println("Enter your choice");
	            	int userChoice = scanner.nextInt();
	            	switch(userChoice) {
	            		case 1: 
	            			System.out.println("Enter START TIME (YYYY-MM-DD): ");
	    	                String startTimePH = scanner.nextLine();
	    	                scanner.nextLine();
	    	                System.out.println("Enter END TIME (YYYY-MM-DD): ");
	    	                String endTimePH = scanner.nextLine();
	    	                ResultSet rs2 = stmt.executeQuery("SELECT lotName, COUNT(DISTINCT(cNumber)) AS TotalCitations FROM Citations WHERE date>= '" + startTimePH + "' AND date <= '" + endTimePH +  "' GROUP BY lotName");
	    	                System.out.println("Generating Report for Total Citations in a Lot...");
	    	                DBTablePrinter.printResultSet(rs2);
	    	                break;
	            	
	            		case 2:
	            			 scanner.nextLine();
	            			System.out.println("Enter the month for which you want to generate citation(MM): ");
	            			String month = scanner.nextLine();
	    	                ResultSet rs1= stmt.executeQuery("SELECT  lotName, COUNT(DISTINCT(cNumber)) AS TotalCitations FROM Citations WHERE MONTH(date) = '" + month + "'");
	    	                DBTablePrinter.printResultSet(rs1);
	    	                break;
	            		case 3:
	            			 scanner.nextLine();
	            			System.out.println("Enter the year for which you want to generate citation(MM): ");
	            			String year = scanner.nextLine();
	    	                rs= stmt.executeQuery("SELECT  lotName, COUNT(DISTINCT(cNumber)) AS TotalCitations FROM Citations WHERE YEAR(date) = '" + year + "'");
	    	                DBTablePrinter.printResultSet(rs);
	    	                break;
	            	
	            	}
	            	break;
	             
	            case 3:
	                // Implement operation to return list of zones for each lot
	                rs =stmt.executeQuery("SELECT P.parkingLotName AS Lot, Z.zoneID AS Zone FROM ParkingLot P JOIN Zones Z ON P.parkingLotName = Z.parkingLotName ORDER BY P.parkingLotName, Z.zoneID");
	                System.out.println("Returning List of Zones for Each Lot...");
	                DBTablePrinter.printResultSet(rs);
	                break;
	            case 4:
	                // Implement operation to return number of cars currently in violation
	                rs = stmt.executeQuery("SELECT COUNT(DISTINCT carLicenseNumber) FROM Citations WHERE paymentStatus = 'Due'");
	                System.out.println("Returning Number of Cars Currently in Violation...");
	                DBTablePrinter.printResultSet(rs);
	                break;
	            case 5:
	                // Implement operation to return number of employees with permits for a zone
	            	System.out.println("Enter ZoneID: ");
	                String imputZone = scanner.next();
	                System.out.println("Enter ParkingLot: ");
	                scanner.nextLine();
	                String inputParkingLot = scanner.nextLine();
	                rs = stmt.executeQuery("SELECT COUNT(DISTINCT driverID) AS NumberOfEmployeesWithPermits FROM Drivers JOIN Permits ON Drivers.ID = Permits.driverID JOIN Contains ON Permits.permitID = Contains.permitID WHERE Drivers.status = 'E'  AND Contains.zoneID = \'"+imputZone+"\' AND Contains.parkingLotName= \'"+inputParkingLot+"\'");
	                System.out.println("Returning Number of Employees with Permits for a Zone...");
	                DBTablePrinter.printResultSet(rs);
	                break;
	            case 6:
	                // Implement operation to return permit information by ID or Phone Number
	            	System.out.println("Enter ID or PhoneNumber: ");
	                long inputId = scanner.nextLong();
	                System.out.println("Returning Permit Information by ID or Phone Number...");
	                rs = stmt.executeQuery("SELECT * FROM Permits WHERE driverID = \'"+inputId+"\'");
	                DBTablePrinter.printResultSet(rs);
	                break;
	            case 7:
	                // Implement operation to return available space number by space type in a lot
	            	System.out.println("Enter ZoneID: ");
	                String inputZone = scanner.next();
	                scanner.nextLine();
	                System.out.println("Enter ParkingLot: ");
	                String inputParkingLot2 = scanner.nextLine();
	                scanner.nextLine();
	                System.out.println("Enter inputSpaceType: ");
	                String inputSpaceType = scanner.nextLine();
	            	System.out.println("Returning Available Space Number by Space Type in a Lot...");
	                rs = stmt.executeQuery("SELECT DISTINCT(a.spaceNumber) as AvailableSpaces FROM Spaces a, Contains b WHERE a.spaceNumber=b.spaceNumber AND a.zoneID = b.zoneID AND a.parkingLotName=  b.parkingLotName AND b.parkingLotName = \'"+inputParkingLot2+"\' AND b.zoneID = \'"+inputZone+"\' AND b.spaceType = \'"+inputSpaceType+ "\' AND a.availability = 'Yes'");
	                DBTablePrinter.printResultSet(rs);
	                break;
	            case 8:
	            	//Go back to Main menu
	            	Main.main(args);
                	break;
	            case 9:
	            	exit=true;
	            	break;
	            default:
	                System.out.println("Invalid operation");
	        }

    	    	}
	    	} finally {
                close(rs);
                close(stmt);
                close(conn);
	    	}
	    }catch(Throwable oops) {
            oops.printStackTrace();
        }	
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
	    
}