import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Main {
    static final String USERNAME = "psjadhav";
    static final String PW = "200508780";
    static final String jdbcURL = "jdbc:mariadb://classdb2.csc.ncsu.edu:3306/psjadhav";
    // Put your oracle ID and password here

    public static Connection connection = null;
    public static Statement statement = null;
    public static final ResultSet result = null;

    /**
     * This function prints a menu to the system console.
     * Through this menu, one can interact with this program.
     * The first step must always be to load the database ("Initialize/Reload Database"),
     * if it is not already loaded. Then one can either look at reports,
     * handle payments, or otherwise interact with the database.
     * @param args
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            connectToDatabase();

            // Display the main menu
            boolean exit = false;
            while (!exit) {
                System.out.println("Menu:");
                System.out.println("1. Drivers");
                System.out.println("2. ParkingLots");
                System.out.println("3. Zones");
                System.out.println("4. Spaces");
                System.out.println("5. Permits");
                System.out.println("6. Vehicles");
                System.out.println("7. Citations");
                System.out.println("8. Reports");
                System.out.println("9. Initialize/Reload database");
                System.out.println("10. Exit()");

                int choice;
                while(true) {
                    try {
                        System.out.print("Enter your choice: ");
                        choice = Integer.parseInt(scanner.nextLine());
                        break;
                    }
                    catch(Exception e) {
                        System.out.println("Please enter a valid choice (numerical)");
                    }
                }

                // Perform the selected action
                switch (choice) {
                    case 1 :
                    	Drivers.main(args);
                    	break;
                    case 2 :
                    	ParkingLot.main(args);
                    	break;
                    case 3 :
                    	Zones.main(args);
                    	break;
                    case 4 :
                    	Spaces.main(args);
                    	break;
                    case 5 :
                    	Permits.main(args);
                    	break;
                    case 6 :
                    	Vehicles.main(args);
                    	break;
                    case 7 :
                    	Citations.main(args);
                    	break;
                    case 8 :
                    	Reports.main(args);
                    	break;
                    case 9 :
                    	initialize();
                    	break;
                    case 10 : exit = true;
                    	break;
                    default : System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        finally {
            System.out.println("\nCLOSING the Database connection finally!");
            close();
        }
    }

   
    public static void connectToDatabase() throws ClassNotFoundException, SQLException {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcURL, USERNAME, PW);
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new SQLException("Could not connect to database.");
        }
        catch (Exception e) {
            throw new ClassNotFoundException("Class for driver not found.");
        }
    }
    
    /**
     * Initializes the database, but connection handling is done outside of this function.
     */
    public static void initialize() {
        try {
            dropAllTables();
            dropAllTables1();
            dropAllTables2();
            createTables();
            insertRows();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void createTables() {
        try {
            statement.executeUpdate("CREATE TABLE Drivers\n" +
                    "(\n" +
                    "    ID                   BIGINT  PRIMARY KEY,\n" +
                    "    name              VARCHAR(45)  NOT NULL,\n" +
                    "    status               VARCHAR(2)  NOT NULL\n" +
                    ");");
            statement.executeUpdate("CREATE TABLE ParkingLot(\n" +
                    "    parkingLotName     VARCHAR(45) PRIMARY KEY,\n" +
                    "    address            VARCHAR(255) NOT NULL\n" +
                    ");");
            statement.executeUpdate("CREATE  TABLE Zones(\n" +
                    "    zoneID 			VARCHAR(4), \n" +
                    "    parkingLotName     VARCHAR(45), \n" +
                    "    PRIMARY KEY(zoneID, parkingLotName),\n" +
                    "	FOREIGN KEY (parkingLotName) REFERENCES ParkingLot (parkingLotName)\n" +
                    "        ON UPDATE CASCADE\n" +
                    "        ON DELETE CASCADE\n" +
                    ");");
            statement.executeUpdate("CREATE TABLE Spaces(\n" +
            		"    spaceNumber 		INT, \n" +
            		"    zoneID 			VARCHAR(4) ,\n" +
                    "    parkingLotName     VARCHAR(45) ,\n" +
                    "	availability     VARCHAR(5) NOT NULL, \n" +
                    "   PRIMARY KEY(spaceNumber, zoneID, parkingLotName),\n" +
                    "	FOREIGN KEY (zoneID, parkingLotName) REFERENCES Zones (zoneID, parkingLotName)\n" +
                    "        ON UPDATE CASCADE\n" +
                    "        ON DELETE CASCADE\n" +
                    ");");
            statement.executeUpdate("CREATE TABLE Vehicles(\n" +
                    "    carLicenseNumber  VARCHAR(15) PRIMARY KEY,\n" +
                    "    model VARCHAR(30),\n" +
                    "    color VARCHAR(30),\n" +
                    "    manufacturer  VARCHAR(30),\n" +
                    "    year	INT\n" +
                    ");");
            statement.executeUpdate("CREATE  TABLE Permits(\n" +
                    "     permitID        VARCHAR(10) PRIMARY KEY,\n" +
                    "    permitType       VARCHAR(20) NOT NULL,\n" +
                    "    startDate       DATE NOT NULL,\n" +
                    "    expirationDate  DATE NOT NULL,\n" +
                    "     expirationTime  TIME NOT NULL,\n" +
                    "    driverID        BIGINT NOT NULL,\n" +
                    "	FOREIGN KEY (driverID) REFERENCES Drivers (ID)\n" +
                    "        ON UPDATE CASCADE\n" +
                    "        ON DELETE CASCADE\n" +
                    ");");
            statement.executeUpdate("CREATE TABLE AssignedTo(\n" +
                    "    permitID        VARCHAR(10),\n" +
                    "    carLicenseNumber  VARCHAR(15),\n" +
                    "   PRIMARY KEY(permitID, carLicenseNumber),\n" +
                    "	FOREIGN KEY (permitID) REFERENCES Permits (permitID)\n" +
                    "        ON UPDATE CASCADE\n" +
                    "        ON DELETE CASCADE,\n" +
                    "	FOREIGN KEY (carLicenseNumber) REFERENCES Vehicles (carLicenseNumber)\n" +
                    "        ON UPDATE CASCADE\n" +
                    "        ON DELETE CASCADE\n" +
                    ");");
            statement.executeUpdate("CREATE TABLE Citations(\n" +
                    "    cNumber VARCHAR(5) PRIMARY KEY,\n" +
                    "    date DATE NOT NULL,\n" +
                    "    time TIME NOT NULL,\n" +
                    "    carLicenseNumber VARCHAR(15) NOT NULL,\n" +
                    "    category VARCHAR(15) NOT NULL,\n" +
                    "    fee DECIMAL(10, 2) NOT NULL,\n" +
                    "    paymentStatus VARCHAR(5) NOT NULL,\n" +
                    "    lotName VARCHAR(45) NOT NULL,\n" +
                    "	FOREIGN KEY (lotName) REFERENCES ParkingLot (parkingLotName)\n" +
                    "        ON UPDATE CASCADE\n" +
                    "        ON DELETE CASCADE,\n" +
                    "	FOREIGN KEY (carLicenseNumber) REFERENCES Vehicles (carLicenseNumber)\n" +
                    "        ON UPDATE CASCADE\n" +
                    "        ON DELETE CASCADE\n" +
                    ");");
            statement.executeUpdate("CREATE TABLE Appeal(\n" +
                    "    cNumber VARCHAR(5),\n" +
                    "    driverID  BIGINT,\n" +
                    "    description VARCHAR(128) NOT NULL,\n" +
                    "    appealStatus VARCHAR(15) NOT NULL ,\n" +
                    "    PRIMARY KEY (cNumber),\n" +
                    "	FOREIGN KEY (cNumber) REFERENCES Citations (cNumber)\n" +
                    "        ON UPDATE CASCADE\n" +
                    "        ON DELETE CASCADE,\n" +
                    "	FOREIGN KEY (driverID) REFERENCES Drivers (ID)\n" +
                    "        ON UPDATE CASCADE\n" +
                    "        ON DELETE CASCADE\n" +
                    ");");
            statement.executeUpdate("CREATE TABLE Contains(\n" +
                    "    permitID VARCHAR(10),\n" +
                    "    spaceNumber  INT ,\n" +
                    "    zoneID VARCHAR(128) ,\n" +
                    "    parkingLotName VARCHAR(45),\n" +
                    "    spaceType VARCHAR(15) NOT NULL ,\n" +
                    "    PRIMARY KEY (permitID, spaceNumber, zoneID, parkingLotName),\n" +
                    "	FOREIGN KEY (permitID) REFERENCES Permits (permitID)\n" +
                    "        ON UPDATE CASCADE\n" +
                    "        ON DELETE CASCADE,\n" +
                    "	FOREIGN KEY (spaceNumber, zoneID, parkingLotName) REFERENCES Spaces (spaceNumber, zoneID, parkingLotName)\n" +
                    "        ON UPDATE CASCADE\n" +
                    "        ON DELETE CASCADE\n" +
                    ");");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void insertRows() {
        try {
            statement.executeUpdate("INSERT INTO Drivers (ID, name, status) VALUES "
            		+ "                        (7729119111, 'Sam BankmanFried', 'V'),"
            		+ "                        (266399121, 'John Clay', 'E'),"
            		+ "                        (366399121, 'Julia Hicks', 'E'),"
            		+ "                        (122765234, 'Sachin Tendulkar','S'),"
            		+ "                        (466399121, 'Ivan Garcia', 'E'),"
            		+ "                        (9194789124, 'Charles Xavier', 'V')");
            statement.executeUpdate("INSERT INTO ParkingLot (parkingLotName, address) VALUES " +
                    "('Poulton Deck', '1021 Main Campus Dr Raleigh, NC, 27606'), " +
                    "('Partners Way Deck', '851 Partners Way Raleigh, NC, 27606'), " +
                    "('Dan Allen Parking Deck', '110 Dan Allen Dr Raleigh, NC, 27607'), " +
                    "('Varsity Drive Parking Deck', '101 Pine Street, Hamletville, NC 27606') ");
            statement.executeUpdate("INSERT INTO Zones (zoneID, parkingLotName) VALUES " + 
        			"('A',  'Poulton Deck'), " +
        			"('BV', 'Poulton Deck'), " +
        			"('CS', 'Poulton Deck'), " +
        			"('D',  'Poulton Deck'), " +
        			"('EV', 'Poulton Deck'), " +
        			"('FS', 'Poulton Deck'), " +
        			"('AV',  'Partners Way Deck'), " +
        			"('BV', 'Partners Way Deck'), " +
        			"('CS', 'Partners Way Deck'), " +
        			"('DS', 'Partners Way Deck'), " +
        			"('E',  'Partners Way Deck'), " +
        			"('F',  'Partners Way Deck'), " +
        			"('A',  'Dan Allen Parking Deck'), " +
        			"('BS', 'Dan Allen Parking Deck'), " +
        			"('CV', 'Dan Allen Parking Deck'), " +
        			"('DS', 'Dan Allen Parking Deck'), " +
        			"('E',  'Dan Allen Parking Deck'), " +
        			"('FV', 'Dan Allen Parking Deck'), " +
        			"('GV', 'Dan Allen Parking Deck')");
            statement.executeUpdate("INSERT INTO Spaces (spaceNumber, zoneID, parkingLotName, availability) VALUES " +
                    "(1, 'A', 'Poulton Deck', 'Yes'), " +
                    "(2, 'A', 'Poulton Deck', 'No'), " +
                    "(1, 'BV', 'Poulton Deck', 'Yes'), " +
                    "(2, 'BV', 'Poulton Deck', 'Yes'), " +
                    "(1, 'CS', 'Poulton Deck', 'Yes'), " +
                    "(2, 'CS', 'Poulton Deck', 'No'), " +
                    "(1, 'D', 'Poulton Deck', 'Yes'), " +
                    "(2, 'D', 'Poulton Deck', 'No'), " +
                    "(1, 'EV', 'Poulton Deck', 'No'), " +
                    "(2, 'EV', 'Poulton Deck', 'Yes'), " +
                    "(1, 'FS', 'Poulton Deck', 'Yes'), " +
                    "(2, 'FS', 'Poulton Deck', 'Yes'), " +
                    "(1, 'AV', 'Partners Way Deck', 'No'), " +
                    "(2, 'AV', 'Partners Way Deck', 'Yes'), " +
                    "(1, 'BV', 'Partners Way Deck', 'No'), " +
                    "(2, 'BV', 'Partners Way Deck', 'Yes'), " +
                    "(1, 'CS', 'Partners Way Deck', 'Yes'), " +
                    "(2, 'CS', 'Partners Way Deck', 'No'), " +
                    "(1, 'DS', 'Partners Way Deck', 'Yes'), " +
                    "(2, 'DS', 'Partners Way Deck', 'No'), " +
                    "(1, 'E', 'Partners Way Deck', 'No'), " +
                    "(2, 'E', 'Partners Way Deck', 'Yes'), " +
                    "(1, 'F', 'Partners Way Deck', 'No'), " +
                    "(2, 'F', 'Partners Way Deck', 'Yes'), " +
                    "(1, 'A', 'Dan Allen Parking Deck', 'No'), " +
                    "(2, 'A', 'Dan Allen Parking Deck', 'Yes'), " +
                    "(1, 'BS', 'Dan Allen Parking Deck', 'No'), " +
                    "(2, 'BS', 'Dan Allen Parking Deck', 'NO'), " +
                    "(1, 'CV', 'Dan Allen Parking Deck', 'Yes'), " +
                    "(2, 'CV', 'Dan Allen Parking Deck', 'No'), " +
                    "(1, 'DS', 'Dan Allen Parking Deck', 'Yes'), " +
                    "(2, 'DS', 'Dan Allen Parking Deck', 'No'), " +
                    "(1, 'E', 'Dan Allen Parking Deck', 'No'), " +
                    "(2, 'FV', 'Dan Allen Parking Deck', 'Yes'), " +
                    "(1, 'GV', 'Dan Allen Parking Deck', 'No'), " +
                    "(2, 'GV', 'Dan Allen Parking Deck', 'Yes'), " +
                    "(4, 'GV', 'Dan Allen Parking Deck', 'Yes') " );
            statement.executeUpdate("INSERT INTO Permits (permitID, permitType, startDate, expirationDate, expirationTime, driverID) VALUES " +
                    "('VSBF1C', 'Commuter', '2023-01-01', '2024-01-01', '06:00:00', 7729119111), " +
                    "('EJC1R', 'Residential', '2010-01-01', '2030-01-01', '07:15:00', 266399121), " +
                    "('EJC2R', 'Special event', '2010-01-01', '2030-01-01', '06:00:00', 266399121)," +
                    "('EJH2C', 'Commuter', '2023-01-01', '2024-01-01', '12:30:00', 366399121), " +
                    "('EIG3C', 'Commuter', '2023-01-01', '2024-01-01', '13:25:00', 466399121), " +
                    "('SST1R', 'Residential', '2022-01-01', '2023-09-30', '10:35:00', 122765234), " +
                    "('VCX1SE', 'Special Event', '2023-01-01', '2023-11-15', '17:55:00', 9194789124), " +
                    "('EJH3C', 'Residential', '2014-01-03', '2024-02-04','23:45:00', 366399121)," +
            		"('EJH4C', 'Special event', '2014-01-03', '2024-02-04','06:00:00', 366399121)," +
            		"('EIG4C', 'Residential', '2014-01-03', '2024-02-04','06:00:00', 466399121)," +
            		"('EIG5C', 'Park and ride', '2014-01-03', '2024-02-04','06:00:00', 466399121), " +
            		"('SST2R', 'Special Event', '2014-01-03', '2024-02-04','06:00:00', 122765234)");
            statement.executeUpdate("INSERT INTO Vehicles (carLicenseNumber, model, color, manufacturer, year) VALUES " +
                    "('SBF', 'GT-R-Nismo', 'Pearl White TriCoat', 'Nissan', 2024), " +
                    "('VAN-9910', 'Chevrolet Malibu', 'White', 'Chevrolet', 2017), " +
                    "('Mark1', 'Chevrolet Malibu', 'White', 'Chevrolet', 2017), " +
                    "('Julia1', 'Chevrolet Malibu', 'White', 'Chevrolet', 2017)," +
                    "('Clark1', 'Chevrolet Malibu', 'White', 'Chevrolet', 2017), " +
                    "('Sophie1', 'Chevrolet Malibu', 'White', 'Chevrolet', 2017), " +
                    "('Clay1', 'Model S', 'Ultra Red', 'Tesla', 2023), " +
                    "('Hicks1', 'M2 Coupe', 'Zandvoort Blue', 'BMW', 2024), " +
                    "('Garcia1', 'Continental GT Speed', 'Blue Fusion', 'Bentley', 2024), " +
                    "('CRICKET', 'Civic SI', 'Sonic Gray Pearl', 'Honda', 2024), " +
                    "('PROFX', 'Taycan Sport Turismo', 'Frozenblue Metallic', 'Porsche', 2024), " +
                    "('Kris1', 'Audi Q5', 'Black', 'Audi', 2006) ");
            
            statement.executeUpdate("INSERT INTO Contains(permitID, spaceNumber, zoneID, parkingLotName, spaceType) VALUES" +
            		"('VSBF1C', 1, 'BV', 'Poulton Deck', 'Regular'), " +
                    "('VCX1SE', 1, 'BV', 'Poulton Deck', 'Handicap'), " +
                    "('EJC1R', 2, 'A', 'Dan Allen Parking Deck', 'Electric'), " +
                    "('EIG3C', 1, 'D', 'Poulton Deck', 'Regular'), " +
                    "('SST1R', 2, 'DS', 'Partners Way Deck', 'Compact car'), " +
                    "('EJH3C', 1, 'F', 'Partners Way Deck', 'Compact car'), " + 
                    "('EJH2C', 1, 'E', 'Dan Allen Parking Deck', 'Regular') " ) ;
            statement.executeUpdate("INSERT INTO Citations (cNumber, date, time,  carLicenseNumber, category, fee, paymentStatus, lotName) VALUES " +
                    "('NP1', '2021-10-11', '08:00:00', 'VAN-9910', 'No Permit', 40.00, 'PAID', 'Dan Allen Parking Deck'), " +
                    "('EP1', '2023-10-01', '08:00:00', 'CRICKET', 'Expired Permit', 30.00, 'DUE', 'Poulton Deck')");
            statement.executeUpdate("INSERT INTO Appeal(cNumber, driverID, description, appealStatus) VALUES " +
                    "('EP1', 122765234, 'The out time is not correct', 'Resolved')");
            
            statement.executeUpdate("INSERT INTO AssignedTo (permitID, carLicenseNumber) VALUES " +
                    "('VSBF1C', 'SBF'), " +
                    "('EJC1R', 'Clay1'), " +
                    "('EJC1R', 'Hicks1'), " +
                    "('EIG3C', 'Garcia1'), " +
                    "('SST1R', 'CRICKET'), " +
                    "('VCX1SE', 'PROFX'), " +
                    "('VCX1SE', 'Kris1')" );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    private static void dropAllTables() {
        try {
            statement.executeUpdate("DROP TABLE AssignedTo");
            statement.executeUpdate("DROP TABLE Appeal");
            statement.executeUpdate("DROP TABLE Contains");
            statement.executeUpdate("DROP TABLE Spaces");
            statement.executeUpdate("DROP TABLE Zones");
            statement.executeUpdate("DROP TABLE Citations");
            statement.executeUpdate("DROP TABLE ParkingLot");
            
            } catch (SQLException e) {
        } catch (Exception ignored) {
        }
    }
    
    private static void dropAllTables1() {
        try {
        	statement.executeUpdate("DROP TABLE Vehicles");
            statement.executeUpdate("DROP TABLE Permits");
            
            
            } catch (SQLException e) {
        } catch (Exception ignored) {
        }
    }
    
    private static void dropAllTables2() {
        try {
        
            statement.executeUpdate("DROP TABLE Drivers");
            
            } catch (SQLException e) {
        } catch (Exception ignored) {
        }
    }
    
    
    private static void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (result != null) {
            try {
                result.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
