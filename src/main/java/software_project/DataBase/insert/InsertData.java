package software_project.DataBase.insert;

import software_project.DataBase.retrieve.Retrieve;
import software_project.EventManagement.Event;
import software_project.EventManagement.EventService;
import software_project.EventManagement.Places;
import software_project.UserManagement.User;
import software_project.Vendor.VendorReview;
import software_project.Vendor.VendorService;
import software_project.helper.Generator;

import java.sql.*;

public class InsertData {
    private String status;
    private final Connection conn;

    Retrieve retrieve;

    public InsertData(Connection conn) {
        this.conn = conn;
        retrieve = new Retrieve(conn);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void insertUser(User user) {

        try {
            conn.setAutoCommit(false);
            String query = "insert into \"users\" (\"First_Name\", \"Last_Name\", \"User_Name\", \"Email\", \"Password\", \"Phone\", \"User_Type\" , \"image\") values (?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt = Generator.userToPS(preparedStmt, user);
            preparedStmt.execute();
            setStatus("User was inserted successfully");
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            setStatus("Couldn't insert user");

        }

    }

    public boolean insertEventService(EventService es) {

        try {
            conn.setAutoCommit(false);
            String query = "insert into \"Event_Service\" (\"Title\", \"Details\", \"Event_Category\", \"Price\", \"Place\", \"Start_Time\", \"End_Time\" , \"Booking_Time\") values (?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt = Generator.eventStatementToPS(preparedStmt, es);
            preparedStmt.execute();
            setStatus("Event added successfully");
            conn.commit();
            return true;
        } catch (Exception e) {

            return false;
        }

    }

    public boolean insertEventServicePlace(EventService es) {
        try {
            conn.setAutoCommit(false);
            String query = "insert into \"Place_EventServices\" values (?, ?);";
            try (PreparedStatement preparedStmt = conn.prepareStatement(query)) {
                preparedStmt.setInt(1, retrieve.retriveplace(es.getPlace()).getId());
                preparedStmt.setInt(2, retrieve.retriveeventid(es.getTitle()));

                preparedStmt.execute();
            }
            setStatus("Event_Place added successfully");
            conn.commit();
            return true;
        } catch (Exception e) {

            return false;
        }

    }

    public boolean insertVenue(Places p) {

        try {
            conn.setAutoCommit(false);
            String query = "insert into \"Places\" (\"Name\", \"Capacity\", \"Amenities\") values (?, ?, ? );";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt = Generator.venueStatementToPS(preparedStmt, p);
            preparedStmt.execute();
            setStatus("Venue added successfully");
            conn.commit();
            return true;
        } catch (Exception e) {


            return false;
        }

    }



    public void insertNotAvailableVendor(Event ee)
    {
        try {
            conn.setAutoCommit(false);
            String query = "insert into \"Vendor_NotAvailable\" (\"Vendor_UN\",\"Date\" , \"Time\") values (?,?,?);";
            for(String s: ee.getVendors()) {
                try (PreparedStatement preparedStmt2 = conn.prepareStatement(query)) {
                    preparedStmt2.setString(1, s);
                    preparedStmt2.setString(2, ee.getDate());
                    preparedStmt2.setString(3, ee.getTime());
                    preparedStmt2.execute();
                }
                conn.commit();
                conn.setAutoCommit(false);
            }




        }catch (Exception e){

            e.printStackTrace();

        }
    }

    public boolean insertEvent(Event e) {
        try {
            conn.setAutoCommit(false);
            String query = "insert into \"Event\" (\"EventService_id\",\"Date\", \"Time\", \"Description\", \"Attendee_Count\",\"Balance\") values (?,?, ?, ?, ?,?);";//id is serial
            String query2 = "insert into \"Guests\" (\"Event_id\",\"Guest_Name\") values (?,?);";//guest id is serial
            String query3 = "insert into \"images\" (\"Event_id\",\"Image_Path\") values (?,?);";//image id is serial
            String query4 = "insert into \"Vendor_Bookings\"(\"Vendor_UN\",\"Event_id\") values (?,?);";//booking id is serial
            String query5 = "insert into \"Event_User\"(\"UserName\",\"Event Id\") values (?,?);";

            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt = Generator.eventBookingStatementToPS(preparedStmt, e);
            preparedStmt.execute();
            conn.commit();
            conn.setAutoCommit(false);

            int id = software_project.DataBase.retrieve.Retrieve.retriveeventIID(conn);

            for(String s: e.getVendors()) {
                PreparedStatement preparedStmt2 = conn.prepareStatement(query4);
                preparedStmt2 = Generator.vendorsStatementToPS(preparedStmt2, s , id);
                preparedStmt2.execute();
                conn.commit();
                conn.setAutoCommit(false);
            }

            for(String s: e.getGuestList()) {
                PreparedStatement preparedStmt2 = conn.prepareStatement(query2);
                preparedStmt2 = Generator.guestListStatementToPS(preparedStmt2, s , id);
                preparedStmt2.execute();
                conn.commit();
                conn.setAutoCommit(false);
            }

            for(String path: e.getImages()) {
                PreparedStatement preparedStmt2 = conn.prepareStatement(query3);
                preparedStmt2 = Generator.imageStatementToPS(preparedStmt2,  path , id);
                preparedStmt2.execute();
                conn.commit();
                conn.setAutoCommit(false);

            }

            try (PreparedStatement preparedStmt5 = conn.prepareStatement(query5)) {
                preparedStmt5.setString(1, e.getUsername());
                preparedStmt5.setInt(2, id);
                preparedStmt5.execute();
            }
            conn.commit();
            conn.setAutoCommit(false);



            setStatus("Event booked successfully");
            conn.commit();
            return true;
        } catch (Exception exception) {

            return false;
        }
    }



    public boolean insertVendorService(VendorService vs) {
        try {
            conn.setAutoCommit(false);//                                       avgRating
            String query = "insert into \"Vendor_Service\" (\"Vendor_User_Name\",\"Type\",\"Description\",\"Price\",\"Availability\",\"Average_Rating\") values (?, CAST(? AS \"Vendor_Type\"), ?, ?, ?, ?);";//service id is serial
            try (PreparedStatement preparedStmt = conn.prepareStatement(query)) {
                preparedStmt.setString(1, vs.getVendorUserName());
                preparedStmt.setString(2, vs.getServiceType());
                preparedStmt.setString(3, vs.getServiceDescription());
                preparedStmt.setString(4, vs.getServicePrice());
                preparedStmt.setString(5, vs.getServiceAvailability());
                preparedStmt.setInt(6, vs.getAverageRating());

                preparedStmt.execute();
            }
            setStatus("service confirmed to vendor successfully");
            conn.commit();
            return true;
        } catch (Exception e) {

            return false;
        }
    }

    public boolean insertVendorReview(VendorReview vr) throws SQLException {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            conn.setAutoCommit(false);
            String query = "insert into \"Vendor_Review\" (\"Vendor_User_Name\",\"Customer_User_Name\",\"Rating\",\"FeedBack_Text\") values (?, ?, ?, ?);";//review id is serial
            try (PreparedStatement preparedStmt = conn.prepareStatement(query)) {
                preparedStmt.setString(1, vr.getVendorUserName());
                preparedStmt.setString(2, vr.getCustomerUserName());
                preparedStmt.setString(3, vr.getRating());
                preparedStmt.setString(4, vr.getFeedBackText());

                preparedStmt.execute();
            }

            conn.commit();
            conn.setAutoCommit(false);

            String query2 = "select \"Rating\" from \"Vendor_Review\" where \"Vendor_User_Name\" = \'" + vr.getVendorUserName() + "\' ;";

            ResultSet rs = stmt.executeQuery(query2);
            int avgRating = 0;
            int ratingsCount = 0;
            while (rs.next()) {
                avgRating += Generator.StarCounter(rs.getString("Rating"));
                ratingsCount++;
            }

            if(ratingsCount!=0)
            {
                avgRating /= ratingsCount;
            }


            String query3 = "update \"Vendor_Service\" set \"Average_Rating\" = \'" + avgRating + "\' where \"Vendor_User_Name\" = \'" + vr.getVendorUserName() + "\';";
            stmt.executeUpdate(query3);




            setStatus("service confirmed to vendor successfully");
            conn.commit();

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
        finally {
            if(stmt!=null)
            {
                stmt.close();

            }
        }
    }


}

