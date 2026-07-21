5: Smart Parking Management System
Overall Project Description
The Smart Parking Management System is a desktop-based Java Swing application used to manage parking slots, registered vehicles, vehicle entry and exit, parking reservations, and parking staff/security monitoring.
Implementation note: The original project requires the `.txt` filenames. According to the additional Java Serialization requirement, this implementation preserves those names but stores binary Java serialized `ArrayList` objects inside them.
The project is divided into independent modules so that each student works on one specific part of the system.
Each module should have its own GUI, data file, model class, manager class, and file handling class. Students do not need to create the complete parking system alone. They only need to complete their assigned module properly.
The system should use file handling for data storage. For example, modules may read data from other module files if required. For example, the Vehicle Entry and Exit Management Module may read parking slot details from parking_slots.txt to assign an available slot to a vehicle.
________________________________________
Modules in Project 5
This project can be divided into 4 modules, where each module is assigned to one student.
•	Parking Slot and Vehicle Registration Management
•	Parking Entry, Exit, and Fee Management
•	Parking Reservation Management
•	Parking Staff and Security Monitoring Management
________________________________________
Module 1: Parking Slot and Vehicle Registration Management
Student Task
The student working on this module should manage parking slots and registered vehicle records.
This module combines two basic operations: managing parking spaces and storing details of vehicles that may use the parking facility.
The student should allow the user to add, view, search, edit, and delete parking slots. The student should also allow the user to add, view, search, edit, and delete registered vehicle records.
This module should not manage vehicle entry and exit. It should only maintain the master records of parking slots and vehicles.
________________________________________
Expected Features
The Parking Slot and Vehicle Registration Management module should provide:
•	Add new parking slot
•	View all parking slots in a JTable
•	Search parking slot by slot ID, floor number, slot type, or slot status
•	Edit parking slot details
•	Delete parking slot record
•	Mark slot as available, occupied, reserved, or under maintenance
•	Store slot type, such as small, medium, large, or electric
•	Store hourly parking rate for each slot
•	Add registered vehicle
•	View all registered vehicles in a JTable
•	Search vehicle by vehicle number, owner name, phone number, or vehicle type
•	Edit vehicle details
•	Delete vehicle record
•	Store owner details
•	Store vehicle type, such as car, bike, van, or truck
•	Generate simple parking slot and vehicle registration report
________________________________________
Data Provided to Other Modules
This module should provide parking slot data to the Parking Entry, Exit, and Fee Management Module and the Parking Reservation Management Module.
Other modules may need:
slotId
floorNumber
slotType
slotStatus
hourlyRate
For example, the Entry and Exit Management student should only assign slots whose status is:
AVAILABLE
This module should also provide registered vehicle data to the Entry and Exit module.
The Entry and Exit module may need:
vehicleNumber
ownerName
phone
vehicleType
registrationStatus
Suggested data files:
parking_slots.txt
registered_vehicles.txt
________________________________________
Expected GUI Screens
The student may create one main Swing window with tabs or panels.
Suggested screens:
•	Parking Slot Form Panel
o	Add/edit parking slot details
•	Parking Slot Table Panel
o	Display all parking slots in JTable
•	Vehicle Registration Form Panel
o	Add/edit registered vehicle details
•	Vehicle Table Panel
o	Display all registered vehicles in JTable
•	Search Panel
o	Search slots by slot ID, floor, type, or status
o	Search vehicles by vehicle number, owner name, or type
•	Status Update Panel
o	Mark slot as available
o	Mark slot as occupied
o	Mark slot as reserved
o	Mark slot as maintenance
•	Report Panel
o	Show total parking slots
o	Available slot count
o	Occupied slot count
o	Reserved slot count
o	Maintenance slot count
o	Total registered vehicles
________________________________________
The student should understand the task like this:
My module is responsible for managing parking slots and registered vehicle records. I will create a Java Swing GUI where the user can add parking slots, update slot details, delete slots, search slots, and view all slots in a table. I will also allow users to register vehicles and maintain vehicle owner details. My module will save slot data in parking_slots.txt and vehicle data in registered_vehicles.txt. Other modules will use these files to assign slots and identify vehicles.
________________________________________
Module 2: Parking Entry, Exit, and Fee Management
Student Task
The student working on this module should manage the entry and exit of vehicles in the parking area.
This module should allow the user to record when a vehicle enters the parking area, assign an available parking slot, record exit time, calculate parking duration, and calculate parking fee.
There is no separate billing module in this project. The parking fee calculation should be handled inside this Entry and Exit module.
This module should not create parking slots and should not fully manage vehicle registration. It should read slot and vehicle data from files created by the Parking Slot and Vehicle Registration Management module.
________________________________________
Expected Features
The Parking Entry, Exit, and Fee Management module should provide:
•	Add new vehicle entry record
•	Select vehicle from registered vehicle data, if available
•	Allow manual entry for unregistered vehicle, if required
•	Select available parking slot
•	Record entry date and entry time
•	View all parking records in a JTable
•	Search parking record by entry ID, vehicle number, slot ID, or status
•	Edit parking record details
•	Mark vehicle as exited
•	Record exit date and exit time
•	Calculate total parking hours
•	Calculate parking fee using hourly rate
•	Apply discount if required
•	Store final payable amount
•	Mark payment as paid or unpaid
•	Update slot status when vehicle enters
•	Update slot status when vehicle exits
•	Generate simple parking entry, exit, and fee report
________________________________________
Data Needed from Other Modules
This module needs data from the Parking Slot and Vehicle Registration Management Module.
It should read:
parking_slots.txt
registered_vehicles.txt
`
This is needed because:
•	A vehicle should be assigned to an existing parking slot
•	Only available slots should be assigned
•	The hourly rate should come from the parking slot record
•	Registered vehicle details can be loaded automatically
•	Slot status should be updated when a vehicle enters or exits
For example:
•	When a vehicle enters, selected slot status may become:
OCCUPIED
•	When a vehicle exits, selected slot status may become:
AVAILABLE
Suggested data file:
parking_entries.txt
________________________________________
Data Provided to Other Modules
This module may provide parking usage data to the Parking Staff and Security Monitoring Management Module.
The security module may need:
entryId
vehicleNumber
ownerName
slotId
entryDate
entryTime
exitDate
exitTime
parkingStatus
paymentStatus
For example, security staff may want to see which vehicles are currently parked inside the parking area.
________________________________________
Expected GUI Screens
Suggested Swing GUI screens:
•	Vehicle Entry Panel
o	Select or enter vehicle number
o	Select available slot
o	Record entry date
o	Record entry time
•	Parking Record Table Panel
o	Display all entry and exit records in JTable
•	Vehicle Exit Panel
o	Search vehicle by vehicle number or entry ID
o	Enter exit date and exit time
o	Calculate total parking hours
o	Calculate parking fee
o	Mark payment as paid or unpaid
•	Search Panel
o	Search by entry ID
o	Search by vehicle number
o	Search by slot ID
o	Search by parking status
•	Report Panel
o	Total vehicle entries
o	Currently parked vehicles
o	Exited vehicles
o	Paid records
o	Unpaid records
o	Total collected amount
________________________________________
The student should understand the task like this:
My module is responsible for managing vehicle entry, vehicle exit, and parking fee calculation. I will not create parking slots myself. I will read available slots from parking_slots.txt and assign one available slot when a vehicle enters. When the vehicle exits, I will record exit time, calculate total hours, calculate the parking fee using the slot hourly rate, and mark payment status. My module will also update the slot status when the vehicle enters or exits.
________________________________________
Module 3: Parking Reservation Management
Student Task
The student working on this module should manage advance parking reservations.
This module should allow the user to reserve a parking slot for a vehicle for a specific date and time. The module should read parking slot data and allow reservation only for valid available slots.
This module should not manage vehicle entry and exit. A reservation only means that a slot has been reserved for future use. Actual entry and exit should be handled by the Parking Entry, Exit, and Fee Management module.
________________________________________
Expected Features
The Parking Reservation Management module should provide:
•	Add new parking reservation
•	Select vehicle or enter vehicle details
•	Select available parking slot
•	Enter reservation date
•	Enter start time and end time
•	View all reservations in a JTable
•	Search reservation by reservation ID, vehicle number, slot ID, or date
•	Edit reservation details
•	Cancel reservation
•	Mark reservation as completed
•	Prevent reservation of unavailable or maintenance slots
•	Prevent duplicate reservation for the same slot at the same date and time
•	Update slot status as reserved when reservation is active
•	Generate simple parking reservation report
________________________________________
Data Needed from Other Modules
This module needs data from the Parking Slot and Vehicle Registration Management Module.
It should read:
parking_slots.txt
registered_vehicles.txt
This is needed because:
•	A reservation should only be created for an existing parking slot
•	The selected slot should not be under maintenance
•	Vehicle details can be loaded automatically if the vehicle is registered
•	The GUI may show available slots in a combo box
For example, the reservation should normally be allowed only for slots whose status is:
AVAILABLE
Suggested data file:
parking_reservations.txt
________________________________________
Data Provided to Other Modules
This module may provide reservation data to the Parking Entry, Exit, and Fee Management Module.
The Entry and Exit module may need:
reservationId
vehicleNumber
slotId
reservationDate
startTime
endTime
reservationStatus
For example, if a vehicle arrives with a reservation, the Entry and Exit module may search the reservation and assign the reserved slot.
________________________________________
Expected GUI Screens
Suggested Swing GUI screens:
•	Reservation Form Panel
o	Select or enter vehicle number
o	Select parking slot
o	Enter reservation date
o	Enter start time
o	Enter end time
•	Reservation Table Panel
o	Display all reservations in JTable
•	Search Panel
o	Search by reservation ID
o	Search by vehicle number
o	Search by slot ID
o	Search by date
•	Status Update Panel
o	Mark reservation as reserved
o	Mark reservation as cancelled
o	Mark reservation as completed
•	Report Panel
o	Total reservations
o	Active reservations
o	Cancelled reservations
o	Completed reservations
o	Reservations by date
________________________________________
The student should understand the task like this:
My module is responsible for managing parking reservations. I will not manage actual vehicle entry and exit. I will read parking slot data from parking_slots.txt and allow the user to reserve an available slot for a selected vehicle, date, and time. I should make sure the same slot is not reserved twice for the same time period. I will save all reservation records in parking_reservations.txt.
________________________________________
Module 4: Parking Staff and Security Monitoring Management
Student Task
The student working on this module should manage parking staff and provide basic security monitoring information.
This module should allow the user to add parking staff, assign staff to parking floors or gates, view staff duty details, and monitor currently parked vehicles.
This module should not create parking entries and should not create parking slots. It may read data from the Entry and Exit module to display which vehicles are currently inside the parking area.
________________________________________
Expected Features
The Parking Staff and Security Monitoring Management module should provide:
•	Add parking staff record
•	View all staff records in a JTable
•	Search staff by staff ID, staff name, role, duty gate, or status
•	Edit staff details
•	Delete staff record
•	Assign staff to floor, gate, or shift
•	Mark staff as on duty, off duty, or on leave
•	View currently parked vehicles
•	Search currently parked vehicle by vehicle number or slot ID
•	View vehicles with unpaid parking fee
•	Generate simple staff and security monitoring report
________________________________________
Data Needed from Other Modules
This module may need data from the Parking Entry, Exit, and Fee Management Module.
It may read:
parking_entries.txt
This is needed because:
•	Security staff may need to see currently parked vehicles
•	Security staff may need to search a vehicle by vehicle number
•	Security staff may need to check which slot a vehicle is using
•	Security staff may need to see vehicles with unpaid fee
This module may also read data from the Parking Slot and Vehicle Registration Management Module.
It may read:
parking_slots.txt
registered_vehicles.txt
This is useful because:
•	Security staff can verify registered vehicle details
•	Security staff can see slot statuses
•	The system can display floor-wise parking information
Suggested data file:
parking_staff.txt
________________________________________
Data Provided to Other Modules
This module mainly provides staff duty data.
Other modules may read:
parking_staff.txt
Useful fields:
staffId
staffName
role
assignedGate
assignedFloor
shiftTime
staffStatus
For example, the Entry and Exit module may optionally show which staff member is assigned to the entry gate.
________________________________________
Expected GUI Screens
Suggested Swing GUI screens:
•	Staff Form Panel
o	Add/edit staff details
•	Staff Table Panel
o	Display all staff records in JTable
•	Search Panel
o	Search by staff ID
o	Search by staff name
o	Search by role
o	Search by assigned gate or floor
•	Duty Assignment Panel
o	Assign staff to gate
o	Assign staff to floor
o	Assign staff to shift
o	Mark staff as on duty, off duty, or on leave
•	Security Monitoring Panel
o	View currently parked vehicles
o	Search vehicle by vehicle number
o	Search vehicle by slot ID
o	View unpaid parking records
•	Report Panel
o	Total staff
o	Staff on duty
o	Staff off duty
o	Staff on leave
o	Currently parked vehicles
o	Vehicles with unpaid parking fee
________________________________________
The student should understand the task like this:
My module is responsible for managing parking staff and basic security monitoring. I will create a Java Swing GUI where the user can add staff, update staff details, assign staff to gates or floors, and update staff duty status. I may also read parking_entries.txt to show currently parked vehicles and unpaid records. I will not create parking slots or parking entry records myself.

