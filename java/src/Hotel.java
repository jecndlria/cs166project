/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import java.util.Scanner;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Hotel {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Hotel 
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Hotel(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Hotel

   // Method to calculate euclidean distance between two latitude, longitude pairs. 
   public double calculateDistance (double lat1, double long1, double lat2, double long2){
      double t1 = (lat1 - lat2) * (lat1 - lat2);
      double t2 = (long1 - long2) * (long1 - long2);
      return Math.sqrt(t1 + t2); 
   }
   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
      Statement stmt = this._connection.createStatement ();

      ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
      if (rs.next())
         return rs.getInt(1);
      return -1;
   }

   public int getNewUserID(String sql) throws SQLException {
      Statement stmt = this._connection.createStatement ();
      ResultSet rs = stmt.executeQuery (sql);
      if (rs.next())
         return rs.getInt(1);
      return -1;
   }
   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Hotel.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      Hotel esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Hotel object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Hotel (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Hotels within 30 units");
                System.out.println("2. View Rooms");
                System.out.println("3. Book a Room");
                System.out.println("4. View recent booking history");

                //the following functionalities basically used by managers
                System.out.println("5. Update Room Information");
                System.out.println("6. View 5 recent Room Updates Info");
                System.out.println("7. View booking history of the hotel");
                System.out.println("8. View 5 regular Customers");
                System.out.println("9. Place room repair Request to a company");
                System.out.println("10. View room repair Requests history");

                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewHotels(esql); break;
                   case 2: viewRooms(esql); break;
                   case 3: bookRooms(esql, authorisedUser); break;
                   case 4: viewRecentBookingsfromCustomer(esql, authorisedUser); break;
                   case 5: updateRoomInfo(esql, authorisedUser); break;
                   case 6: viewRecentUpdates(esql, authorisedUser); break;
                   case 7: viewBookingHistoryofHotel(esql, authorisedUser); break;
                   case 8: viewRegularCustomers(esql, authorisedUser); break;
                   case 9: placeRoomRepairRequests(esql, authorisedUser); break;
                   case 10: viewRoomRepairHistory(esql, authorisedUser); break;
                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(Hotel esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine(); 
         String type="Customer";
			String query = String.format("INSERT INTO USERS (name, password, userType) VALUES ('%s','%s', '%s')", name, password, type);
         esql.executeUpdate(query);
         System.out.println ("User successfully created with userID = " + esql.getNewUserID("SELECT last_value FROM users_userID_seq"));
         
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Hotel esql){
      try{
         System.out.print("\tEnter userID: ");
         String userID = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USERS WHERE userID = '%s' AND password = '%s'", userID, password);
         int userNum = esql.executeQuery(query);
         if (userNum > 0)
            return userID;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here

   public static void viewRooms(Hotel esql) {
      try{
         System.out.print("\tEnter hotelID: ");
         String hotelID = in.readLine();
         String date = "";
         String dateRegex = "^(0?[1-9]|1[0-2])\\/(0?[1-9]|[12][0-9]|3[01])\\/\\d{4}$";
         Pattern pattern = Pattern.compile(dateRegex);
         System.out.print("\tEnter date in the format MM/DD/YYYY: ");
         Matcher matcher = pattern.matcher(date);

         while (!matcher.find())
         {
            date = in.readLine();
            matcher = pattern.matcher(date);
            if (!matcher.find())
            System.out.print("\nInvalid date. Please enter another date: ");
            else break;
         }

         String query = String.format(
            "SELECT r.roomNumber as room, r.price " +
            "FROM Rooms r " +
            "WHERE r.hotelID = %s AND NOT EXISTS (SELECT b.roomNumber " +
            "FROM RoomBookings b WHERE r.roomNumber = b.roomNumber AND b.bookingDate = '%s');", hotelID, date);
         int available_rooms = esql.executeQueryAndPrintResult(query);
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }
   public static void viewHotels(Hotel esql) 
   {
      try{
         double latitude = 100;
         double longitude = 1000;
         Scanner scanner = new Scanner(System.in);
         System.out.print("\nEnter Latitude: ");
         while(latitude > 90 || latitude < -90)
         {
            latitude = Math.round(scanner.nextDouble() * 1e6) / 1e6;
            if (latitude > 90 || latitude < -90)
            System.out.print("\nInvalid latitude. Please enter another value: ");

         }
         System.out.println("\nLatitude: " + latitude);
         System.out.print("\nEnter Longitude: ");
         while(longitude > 180 || longitude < -180)
         {
            longitude = Math.round(scanner.nextDouble() * 1e6) / 1e6;
            if (longitude > 180 || longitude < -180)
            System.out.print("\nInvalid longitude. Please enter another value: ");
         }
         System.out.println("\nLongitude: " + longitude);

         String query = String.format("SELECT hotelID, hotelName, dateEstablished FROM Hotel WHERE calculate_distance(%f, %f, latitude, longitude) <= 30;", latitude, longitude);
         int rows = esql.executeQueryAndPrintResult(query);
         System.out.println("\nTotal number of hotels within 30 units of your location: " + rows);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void bookRooms(Hotel esql, String userID) 
   {
      try{
         Scanner scanner = new Scanner(System.in);
         int hotelID;
         int roomNumber;
         String date = "";
         String dateRegex = "^(0?[1-9]|1[0-2])\\/(0?[1-9]|[12][0-9]|3[01])\\/\\d{4}$";
         Pattern pattern = Pattern.compile(dateRegex);
         System.out.println("\nEnter a Hotel ID: ");
         hotelID = scanner.nextInt();
         System.out.println("\nEnter a room number: ");
         roomNumber = scanner.nextInt();
         System.out.println("Enter a date in the format of MM/DD/YYYY: ");
         Matcher matcher = pattern.matcher(date);

         while (!matcher.find())
         {
            date = in.readLine();
            matcher = pattern.matcher(date);
            if (!matcher.find())
            System.out.print("\nInvalid date. Please enter another date: ");
            else break;
         }

         String query = String.format(
            "SELECT H.hotelID, R.price, R.roomNumber " +
            "FROM Rooms R, Hotel H " +
            "WHERE H.hotelID = %d AND R.hotelID = %d AND R.roomNumber = %d AND NOT EXISTS(" +
            "SELECT B.roomNumber " +
            "FROM RoomBookings B " +
            "WHERE H.hotelID = %d AND B.roomNumber = %d AND bookingDate = '%s');", hotelID, hotelID, roomNumber, hotelID, roomNumber, date);
         
         int rows = esql.executeQueryAndPrintResult(query);
         

         if (rows == 0)
         {
            System.out.println("\nSorry, this room is not available at this date.");
            return;
         }

         String query2 = String.format(
            "INSERT INTO RoomBookings (customerID, hotelID, roomNumber, bookingDate) VALUES ('%s', %d, %d, '%s');",
            userID, hotelID, roomNumber, date
         );

         esql.executeUpdate(query2);
         System.out.println("\nBooking made for " + date + " in Hotel " + hotelID + ", Room " + roomNumber);
         
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void viewRecentBookingsfromCustomer(Hotel esql, String userID) {
      try{
         System.out.print("\tDisplaying your last 5 recent bookings... \n");

         String query = String.format("SELECT * FROM (SELECT b.hotelID as hotel, b.roomNumber as room, b.bookingDate, r.price as billingInfo "+
         "FROM RoomBookings b, Rooms r WHERE b.customerID = %s " +
         "AND b.hotelID = r.hotelID AND b.roomNumber = r.roomNumber " +
         "ORDER BY b.bookingDate LIMIT 5) as Top5 " +
         "ORDER BY bookingDate ASC;", userID);

         int top5bookings = esql.executeQueryAndPrintResult(query);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void updateRoomInfo(Hotel esql, String userID) {
      try{
         String user_query = String.format("SELECT u.userType FROM Users u WHERE u.userID = %s AND (u.userType = 'manager' OR u.userType = 'admin');", userID);
         int user_type = esql.executeQuery(user_query);

         if(user_type == 0){
            System.out.print("\tYou must be a manager to update room info.\n");
            return;
         }

         int hotels_managed = 0;
         String hotelID = "";

         while(hotels_managed == 0){
            System.out.print("\tEnter hotelID: ");
            hotelID = in.readLine();
            String hotelstring = String.format("SELECT * FROM Hotel h WHERE h.managerUserID = %s " +
            "AND h.hotelID = %s;", userID, hotelID);
            hotels_managed = esql.executeQuery(hotelstring);
            if(hotels_managed == 0){
               System.out.print("\tPlease pick a hotel you manage.\n");
            }
         }

         int room_exists = 0;
         String roomNumber = "";
         while(room_exists ==0){
            System.out.print("\tEnter room number to update: ");
            roomNumber = in.readLine();
            String roomstring = String.format("SELECT * FROM Rooms WHERE " +
            "hotelID = %s AND roomNumber = %s;", hotelID, roomNumber);
            room_exists = esql.executeQuery(roomstring);
            if(room_exists == 0){
               String printthis = String.format("\tThere is no room number %s in hotel %s.\n", roomNumber, hotelID);
               System.out.print(printthis);
            }
         }

         System.out.print("\tUpdate price: ");
         String price = in.readLine();
         System.out.print("\tUpdate image url: ");
         String image_url = in.readLine();

         String query = String.format("UPDATE Rooms " +
         "SET price = %s, imageURL = '%s' " +
         "WHERE roomNumber = %s;", price, image_url, roomNumber);
         esql.executeUpdate(query);

         Timestamp temp = new Timestamp(System.currentTimeMillis());
         String timestamp = temp.toString(); // just to be safe... i think it turns into a timestamp in postgre
         String query2 = String.format("INSERT INTO RoomUpdatesLog (managerID, hotelID, roomNumber, updatedOn) " +
         "VALUES (%s, %s, %s, '%s');", userID, hotelID, roomNumber, timestamp);
         esql.executeUpdate(query2);

         System.out.print("\tRoom info has been successfully updated!\n");

      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }
   public static void viewRecentUpdates(Hotel esql, String userID) {
      try{
         String user_query = String.format("SELECT u.userType FROM Users u WHERE u.userID = %s AND " +
         "(u.userType = 'manager' OR u.userType = 'admin');", userID);
         int user_type = esql.executeQuery(user_query);

         if(user_type == 0){
            System.out.print("\tYou must be a manager to view update info.\n");
            return;
         }

         System.out.print("\tViewing the last 5 recent updates...\n");
         String query3 = String.format("SELECT updateNumber as update, hotelID as hotel, " +
         "roomNumber as room, updatedOn FROM (SELECT * FROM roomUpdatesLog WHERE managerID = %s " +
         "ORDER BY updatedOn DESC LIMIT 5) AS last5 ORDER BY updatedOn ASC;", userID);
         int last_updated = esql.executeQueryAndPrintResult(query3);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void viewBookingHistoryofHotel(Hotel esql, String userID) 
   {
      try{
         String checkManager = String.format(
            "SELECT userType " +
            "FROM Users " +
            "WHERE (userType = 'manager' OR userType = 'admin') AND userID = %s;", userID
         );
         int isManager = esql.executeQuery(checkManager);
         if (isManager == 0)
         {
            System.out.println("\nYou do not have permission for this option!");
            return;
         }

         String lowerBoundDate = "";
         String upperBoundDate = "";
         String dateRegex = "^(0?[1-9]|1[0-2])\\/(0?[1-9]|[12][0-9]|3[01])\\/\\d{4}$";
         Pattern pattern = Pattern.compile(dateRegex);
         Matcher matcher = pattern.matcher(lowerBoundDate);

         System.out.println("\nEnter a lower bound for date range (MM/DD/YYYY) (inclusive): ");

         while (!matcher.find())
         {
            lowerBoundDate = in.readLine();
            matcher = pattern.matcher(lowerBoundDate);
            if (!matcher.find())
            System.out.print("\nInvalid date. Please enter another date: ");
            else break;
         }

         System.out.println("\nEnter an upper bound for the date range (MM/DD/YYYY) (inclusive): ");
         matcher = pattern.matcher(upperBoundDate);

         while (!matcher.find())
         {
            upperBoundDate = in.readLine();
            matcher = pattern.matcher(upperBoundDate);
            if (!matcher.find())
            System.out.print("\nInvalid date. Please enter another date: ");
            else break;
         }

         String query = String.format(
            "SELECT B.bookingID, U.name, B.hotelID, B.roomNumber, B.bookingDate " +
            "FROM RoomBookings B, Users U, Hotel H " +
            "WHERE H.hotelID = B.hotelID AND B.customerID = U.userID " +
            "AND B.bookingDate >= '%s' AND B.bookingDate <= '%s' " +
            "ORDER BY B.bookingDate ASC;" , lowerBoundDate, upperBoundDate
         );

         int rows = esql.executeQueryAndPrintResult(query);
         System.out.println("\nTotal number of bookings for your hotels within the range of " + lowerBoundDate + " and " + upperBoundDate + ": " + rows);

         }catch(Exception e){
         System.err.println (e.getMessage());
         }
   }
   public static void viewRegularCustomers(Hotel esql, String userID) 
   {
      try{
         String checkManager = String.format(
            "SELECT userType " +
            "FROM Users " +
            "WHERE (userType = 'manager' OR userType = 'admin') AND userID = %s;", userID
         );
         int isManager = esql.executeQuery(checkManager);
         if (isManager == 0)
         {
            System.out.println("\nYou do not have permission for this option!");
            return;
         }
         Scanner scanner = new Scanner(System.in);
         int hotelID;
         System.out.println("\nEnter a hotel ID: ");
         hotelID = scanner.nextInt();

         String checkIfManager = String.format(
            "SELECT managerUserID " +
            "FROM Hotel " +
            "WHERE managerUserID = %s AND hotelID = %d;",
            userID, hotelID
         );

         int managesHotel = esql.executeQuery(checkIfManager);

         if (managesHotel == 0)
         {
            System.out.println("\nYou do not manage this hotel!");
            return;
         }

         String query = String.format(
            "SELECT U.name, COUNT(*) as NumBookings " +
            "FROM Users U, RoomBookings B " +
            "WHERE U.userID = B.customerID AND B.hotelID = %d" +
            "GROUP BY U.name " +
            "ORDER BY NumBookings DESC " +
            "LIMIT 5", hotelID
         );

         int rows = esql.executeQueryAndPrintResult(query);

         }catch(Exception e){
         System.err.println (e.getMessage());
         }
   }
   public static void placeRoomRepairRequests(Hotel esql, String userID) {
      try{
         String user_query = String.format("SELECT u.userType FROM Users u WHERE u.userID = %s AND " +
         "(u.userType = 'manager' OR u.userType = 'admin');", userID);
         int user_type = esql.executeQuery(user_query);

         if(user_type == 0){
            System.out.print("\tYou must be a manager to view update info.\n");
            return;
         }

         System.out.print("\tFill in the following information to submit a room repair request.\n");

         int hotels_managed = 0;
         String hotelID = "";

         while(hotels_managed == 0){
            System.out.print("\tEnter hotelID: ");
            hotelID = in.readLine();
            String hotelstring = String.format("SELECT * FROM Hotel h WHERE h.managerUserID = %s " +
            "AND h.hotelID = %s;", userID, hotelID);
            hotels_managed = esql.executeQuery(hotelstring);
            if(hotels_managed == 0){
               System.out.print("\tPlease pick a hotel you manage.\n");
            }
         }
         //System.out.print("\tEnter hotelID: ");
         //String hotelID = in.readLine();

         //System.out.print("\tEnter roomNumber: ");
         //String roomNumber = in.readLine();
         int room_exists = 0;
         String roomNumber = "";
         while(room_exists ==0){
            System.out.print("\tEnter room number: ");
            roomNumber = in.readLine();
            String roomstring = String.format("SELECT * FROM Rooms WHERE " +
            "hotelID = %s AND roomNumber = %s;", hotelID, roomNumber);
            room_exists = esql.executeQuery(roomstring);
            if(room_exists == 0){
               String printthis = String.format("\tThere is no room number %s in hotel %s.\n", roomNumber, hotelID);
               System.out.print(printthis);
            }
         }

         System.out.print("\tEnter companyID: ");
         String companyID = in.readLine();

         //update RoomRepairs
         String repair = String.format("INSERT INTO roomRepairs (companyID, hotelID, roomNumber, repairDate) " +
         "VALUES (%s, %s, %s, (SELECT CURRENT_DATE));", companyID, hotelID, roomNumber);
         esql.executeUpdate(repair);

         //update RoomRepairRequests
         String repair_id = "SELECT count(*) FROM roomRepairs;";
         List<List<String>> repairID = esql.executeQueryAndReturnResult(repair_id);
         String query2 = String.format("INSERT INTO roomRepairRequests (managerID, repairID) " +
         "VALUES (%s, %s);", userID, repairID.get(0).get(0));
         esql.executeUpdate(query2);

         System.out.print("\tRequest has been submitted!\n");
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void viewRoomRepairHistory(Hotel esql, String userID) {
      try{
         String user_query = String.format("SELECT u.userType FROM Users u WHERE u.userID = %s AND " +
         "(u.userType = 'manager' OR u.userType = 'admin');", userID);
         int user_type = esql.executeQuery(user_query);

         if(user_type == 0){
            System.out.print("\tYou must be a manager to view update info.\n");
            return;
         }

         System.out.print("\tViewing room request history...\n");
         String query = String.format("SELECT a.companyID as company, " +
         "a.hotelID as hotel, a.roomNumber as room, a.repairDate " +
         "FROM roomRepairs a, roomRepairRequests b " +
         "WHERE b.managerID = %s " +
         "AND  a.repairID = b.repairID;", userID);
         int last_updated = esql.executeQueryAndPrintResult(query);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }

}//end Hotel

