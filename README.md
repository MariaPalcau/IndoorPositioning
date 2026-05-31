# Indoor Positioning

This Android application was designed to help students keep track of their classes and improve attendance. 
The application allows users to manage their timetable and receive automatic notifications when a class is about to begin.

To use the application, the user must keep both Wi-Fi and location services enabled. 

For each classroom they regularly attend, the user performs an initial scan of the room. 
Rather than relying on GPS, which is often inaccurate indoors, this app uses Wi-Fi fingerprinting to determine the user's location inside the university.

Wi-Fi fingerprinting works by measuring the RSSI (Received Signal Strength Indicator) values of nearby Wi-Fi Access Points. Since each room has a unique combination of access points and signal strengths, it is possible to distinguish between different locations within the building. The application allows users to create their own fingerprints for the rooms they visit, providing a personalized and adaptable indoor positioning system.

Although Wi-Fi fingerprinting generally provides better indoor positioning than GPS, it is not perfectly accurate. Signal strengths can fluctuate due to obstacles such as walls, furniture, people moving through the area, and temporary interference from electronic devices. As a result, the detected location may occasionally differ from the user's actual position, particularly in neighboring rooms with similar Wi-Fi environments. To reduce these errors, the application compares multiple access points simultaneously rather than relying on a single signal measurement.

All application data, including timetables and room fingerprints, is stored locally on the device in JSON files, ensuring that the system can operate without an internet connection and preserving user privacy.

Before each scheduled class, the application checks whether the user is located in one of the previously saved rooms. If the detected room matches the classroom associated with the upcoming class, no notification is displayed. Otherwise, the user receives a reminder notification at the number of minutes configured in the application settings, helping them arrive at the correct location on time.
