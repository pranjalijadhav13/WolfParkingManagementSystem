# WolfParkingManagementSystem
This is our project submission for CSC-540 : Database Management System

The Wolf Parking Management System is designed to efficiently handle the complexities of managing parking on a university campus. The system maintains comprehensive information about drivers, parking lots, permits, and citation records. Driver information includes name, status (student, employee, or visitor), and a unique identifier (UnivID for students and employees, phone number for visitors). Parking lots are described by name, address, and details about zones and spaces. Zones are categorized by ID, with specific designations for students, employees, and visitors. Spaces have unique numbers, types (e.g., electric, handicap, compact), and availability status. Permits, a crucial aspect of the system, include various details such as permit ID, lot, zone ID, space type, vehicle information, start and expiration dates, and associated UnivID or phone number. The system accommodates different permit types like residential, commuter, peak hours, special events, and Park & Ride. Each driver, depending on their status, is allowed a specific number of permits, with students and visitors limited to one vehicle per permit and employees allowed up to two. 

The administrators have the authority to manage parking lots, zones, spaces, permits, and vehicle lists. To park in a lot, a user's permit must match the designated zone and space type. Security personnel can create, update, or delete citations for parking violations, with detailed information such as citation number, car license number, category, and fee. The payment status of a citation can be changed from unpaid to paid through a payment procedure. This system ensures efficient management of parking resources while maintaining accurate records and enforcing parking regulations.

To get the code running, you'll need the MariaDB client jar file and Eclipse IDE to execute the application programs
