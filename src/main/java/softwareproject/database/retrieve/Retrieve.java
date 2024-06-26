package softwareproject.database.retrieve;

import softwareproject.eventmanagement.Event;
import softwareproject.eventmanagement.EventService;
import softwareproject.eventmanagement.Places;
import softwareproject.usermanagement.User;
import softwareproject.vendor.AVendorBooking;
import softwareproject.vendor.VendorService;
import softwareproject.helper.Generator;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.abs;

public class Retrieve {

    public static final String PRICE = "Price";
    public static final String TITLE = "Title";
    public static final String EVENT_ID = "Event_id";
    public static final String EVENT_SERVICE_ID = "EventService_id";
    public static final String DESCRIPTION = "Description";
    public static final String ERROR_WHILE_RETRIEVING_EVENT_FROM_DATABASE = "Error while retrieving event from database";
    private final Connection con;
    private String status;

    public Retrieve(Connection con) {
        this.con = con;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<EventService> selectEventServicesOfParticularPlace(String placename) {
        List<EventService> eventServices = new ArrayList<>();
        try (PreparedStatement selectPlaceIdStatement = con.prepareStatement("SELECT \"iD\" FROM \"Places\" WHERE \"Name\" = ?")) {
            selectPlaceIdStatement.setString(1, placename);
            try (ResultSet rs = selectPlaceIdStatement.executeQuery()) {
                int placeid = 0;
                while (rs.next())
                    placeid = rs.getInt("iD");

                try (PreparedStatement selectEventServiceIdsStatement = con.prepareStatement("SELECT \"ID_EventService\" FROM \"Place_EventServices\" WHERE \"ID_Place\" = ?")) {
                    selectEventServiceIdsStatement.setInt(1, placeid);
                    try (ResultSet rs2 = selectEventServiceIdsStatement.executeQuery()) {
                        List<Integer> ids = new ArrayList<>();
                        while (rs2.next()) {
                            ids.add(rs2.getInt("ID_EventService"));
                        }
                        for (int id : ids) {
                            try (PreparedStatement selectEventServiceStatement = con.prepareStatement("SELECT * FROM \"Event_Service\" WHERE \"Id\" = ?")) {
                                selectEventServiceStatement.setInt(1, id);
                                try (ResultSet rs3 = selectEventServiceStatement.executeQuery()) {
                                    while (rs3 != null && rs3.next()) {
                                        EventService es = new EventService();
                                        es.setId(rs3.getInt("Id"));
                                        es.setTitle(rs3.getString(TITLE));
                                        es.setDetails(rs3.getString("Details"));
                                        es.setEventCategory(rs3.getString("Event_Category"));
                                        es.setPrice(rs3.getString(PRICE));
                                        es.setPlace(rs3.getString("Place"));
                                        es.setStartTime(rs3.getString("Start_Time"));
                                        es.setEndTime(rs3.getString("End_Time"));
                                        es.setBookingTime(rs3.getString("Booking_Time"));
                                        eventServices.add(es);
                                    }
                                }
                            }
                        }
                        setStatus("Retrieving event services for the place successfully");
                        return eventServices;
                    }
                }
            }
        } catch (SQLException e) {
            setStatus("Error while retrieving event services for the place from database");
            return new ArrayList<>();
        }
    }



    public Places retriveplace(String placename) throws SQLException {
        try (PreparedStatement selectPlaceStatement = con.prepareStatement("SELECT * FROM \"Places\" WHERE \"Name\" = ?")) {
            selectPlaceStatement.setString(1, placename);
            try (ResultSet rs = selectPlaceStatement.executeQuery()) {
                Places place = new Places();
                while (rs.next()) {
                    place.setId(rs.getInt("iD"));
                    place.setName(rs.getString("Name"));
                    place.setCapacity(rs.getString("Capacity"));
                    place.setAmenities(rs.getString("Amenities"));
                }
                return place;
            }
        } catch (SQLException e) {
            setStatus("Error while retrieving placeID for the place from database");
            return null;
        }
    }


    public static int retriveeventIID(Connection con2) throws SQLException {
        Statement stmt = null;
        int eventid = 0;


        try {
            stmt = con2.createStatement();
            stmt.execute("SELECT nextval('public.\"Event_Event_id_seq\"');");

            String query = "SELECT currval('public.\"Event_Event_id_seq\"') as \"ii\";";

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next())
                eventid = rs.getInt("ii");
            return eventid-1;



        } catch (SQLException e) {

            con2.rollback();

        }
       finally {
            if(stmt!=null)
            {
                stmt.close();
            }
        }
        return eventid-1;
    }



    public int retriveeventid(String title) throws SQLException {
        PreparedStatement pstmt = null;
        int eventid = 0;

        try {
            String query = "SELECT \"Id\" FROM \"Event_Service\" WHERE \"Title\" = ?";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, title);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
                eventid = rs.getInt("Id");
            return eventid;
        } catch (SQLException e) {
            setStatus("Error while retrieving event_id for the EventServices from database");
        } finally {
            if (pstmt != null) {
                pstmt.close();
            }
        }
        return eventid;
    }



    public List<EventService> retrieveAllEventServices() throws SQLException {
        Statement stmt = null;

        List<EventService> eventServices = new ArrayList<>();
        try {
            stmt = con.createStatement();
            String query = "SELECT * FROM \"Event_Service\";";

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next())
            {
                EventService eventService = new EventService(rs.getString(2) , rs.getString(3) ,rs.getString(4) , rs.getString(5),rs.getString(6) , rs.getString(7) ,rs.getString(8),rs.getString(9));
                eventService.setId(rs.getInt(1));
                eventServices.add(eventService);

            }

        } catch (SQLException e) {
            setStatus("Error while retrieving EventServices from database");

        }
        finally {
            if(stmt!=null)
            {
                stmt.close();
            }
        }

        return  eventServices;

    }

    public List<User> retrieveAllUsers() throws SQLException {
        Statement stmt = null;

        List<User> users = new ArrayList<>();
        try {
            stmt = con.createStatement();
            String query = "SELECT * FROM \"users\";";

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next())
            {

                User user = new User();
                user.setFirstName(rs.getString("First_Name"));
                user.setLastName(rs.getString("Last_Name"));
                user.setUsername(rs.getString("User_Name"));
                user.setUserType(rs.getString("User_Type"));
                users.add(user);

            }

        } catch (SQLException e) {
            setStatus("Error while retrieving users from database");

        }
        finally {
            if(stmt!=null)
            {
                stmt.close();
            }
        }

        return  users;

    }

    public EventService selectEventServicesOfParticularName(String serviceTitle) throws SQLException {
        PreparedStatement pstmt = null;
        EventService es = new EventService();
        try {
            String query = "SELECT * FROM \"Event_Service\" WHERE \"Title\" = ?";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, serviceTitle);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                es.setId(rs.getInt("Id"));
                es.setTitle(rs.getString(TITLE));
                es.setDetails(rs.getString("Details"));
                es.setEventCategory(rs.getString("Event_Category"));
                es.setPrice(rs.getString(PRICE));
                es.setPlace(rs.getString("Place"));
                es.setStartTime(rs.getString("Start_Time"));
                es.setEndTime(rs.getString("End_Time"));
                es.setBookingTime(rs.getString("Booking_Time"));
            }

            setStatus("Retrieving event successfully");
            return es;

        } catch (Exception e) {
            setStatus(ERROR_WHILE_RETRIEVING_EVENT_FROM_DATABASE);
            return es;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (Exception e) {
                setStatus(ERROR_WHILE_RETRIEVING_EVENT_FROM_DATABASE);
            }
        }
    }



    public String selectEventServicesOfParticularid(int id) throws SQLException {
        PreparedStatement pstmt = null;
        String title = "";
        try {
            String query = "SELECT \"Title\" FROM \"Event_Service\" WHERE \"Id\" = ?";
            pstmt = con.prepareStatement(query);
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                title = rs.getString(TITLE);
            }

            setStatus("Retrieving event successfully");
            return title;

        } catch (Exception e) {
            setStatus(ERROR_WHILE_RETRIEVING_EVENT_FROM_DATABASE);
            return title;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (Exception e) {
                // Handle or log the exception
            }
        }
    }


    public List<Event> selectEventOfParticularDateAndServiceId(String date, int serviceid) {
        List<Event> events = new ArrayList<>();
        try (PreparedStatement selectEventServiceStatement = con.prepareStatement("SELECT * FROM \"Event\" WHERE \"Date\" = ? AND \"EventService_id\" = ?")) {
            selectEventServiceStatement.setString(1, date);
            selectEventServiceStatement.setInt(2, serviceid);
            try (ResultSet rs = selectEventServiceStatement.executeQuery()) {
                String query1 = "SELECT \"Title\" FROM \"Event_Service\" WHERE \"Id\" = ?";
                try (PreparedStatement selectServiceTitleStatement = con.prepareStatement(query1)) {
                    selectServiceTitleStatement.setInt(1, serviceid);
                    try (ResultSet rs1 = selectServiceTitleStatement.executeQuery()) {
                        String servicetitle = "";
                        while (rs1.next()) {
                            servicetitle = rs1.getString(TITLE);
                        }

                        while (rs.next()) {
                            Event e = new Event(con);
                            e.setId(rs.getInt(EVENT_ID));
                            e.setServiceId(rs.getInt(EVENT_SERVICE_ID));
                            e.setServiceTitle(servicetitle);
                            e.setDate(rs.getString("Date"));
                            e.setTime(rs.getString("Time"));
                            e.setDescription(rs.getString(DESCRIPTION));
                            e.setAttendeeCount(String.valueOf(rs.getInt("Attendee_Count")));
                            events.add(e);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            setStatus("Error while retrieving events for the date from database");
        }
        return events;
    }


    public VendorService selectVendorServiceOfParticularName(String vendorName) throws SQLException {
        VendorService vs = new VendorService();
        try (PreparedStatement selectVendorServiceStatement = con.prepareStatement("SELECT * FROM \"Vendor_Service\" WHERE \"Vendor_User_Name\" = ?")) {
            selectVendorServiceStatement.setString(1, vendorName);
            try (ResultSet rs = selectVendorServiceStatement.executeQuery()) {
                while (rs.next()) {
                    vs.setVendorUserName(rs.getString("Vendor_User_Name"));
                    vs.setServiceType(rs.getString("Type"));
                    vs.setServiceDescription(rs.getString(DESCRIPTION));
                    vs.setServicePrice(rs.getString(PRICE));
                    vs.setServiceAvailability(rs.getString("Availability"));
                    vs.setAverageRating(rs.getInt("Average_Rating"));
                }
                setStatus("Retrieving vendor service successfully");
            }
        } catch (SQLException e) {
            setStatus("Error while retrieving vendor service from database");
        }
        return vs;
    }


    public List<AVendorBooking> selectVendorBookingOfParticularName(String s) {
        List<AVendorBooking> vbs = new ArrayList<>();
        try (PreparedStatement selectVendorBookingStatement = con.prepareStatement("SELECT * FROM \"Vendor_Bookings\" WHERE \"Vendor_UN\" = ?")) {
            selectVendorBookingStatement.setString(1, s);
            try (ResultSet rs = selectVendorBookingStatement.executeQuery()) {
                List<Integer> eventIDs = new ArrayList<>();
                while (rs.next()) {
                    eventIDs.add(rs.getInt(EVENT_ID));
                }
                for (int eventId : eventIDs) {
                    try (PreparedStatement selectEventStatement = con.prepareStatement("SELECT * FROM \"Event\" WHERE \"Event_id\" = ?")) {
                        selectEventStatement.setInt(1, eventId);
                        try (ResultSet rs1 = selectEventStatement.executeQuery()) {
                            AVendorBooking vb = new AVendorBooking();
                            String serviceTitle = "";
                            int serviceId = 0;
                            while (rs1.next()) {
                                serviceId = rs1.getInt(EVENT_SERVICE_ID);
                                vb.setBookingdate(rs1.getString("Date"));
                                vb.setStarttime(rs1.getString("Time"));
                            }
                            serviceTitle = this.selectEventServicesOfParticularid(serviceId);
                            vb.setBookingtime(this.selectEventServicesOfParticularName(serviceTitle).getBookingTime());
                            vb.setVendorusername(s);
                            vbs.add(vb);
                        }
                    }
                }
                setStatus("Retrieving vendor booking successfully");
            }
        } catch (SQLException e) {
            setStatus("Error while retrieving vendor booking from database");
        }
        return vbs;
    }






    private static void incrementCount(Map<String, Integer> map, String date) {
        map.put(date, map.getOrDefault(date, 0) + 1);
    }






    public Map<Integer,Boolean> checkDays(int year , int month ,EventService eventService) {

        Map<String, Integer> dateCountMap = new HashMap<>();

        Map<Integer, Boolean> dateAvalability = new HashMap<>();


        List<Event> events = this.selectEventOfParticularServiceId(eventService);

        int timeDiff = abs(Generator.getTimeDifference(eventService.getStartTime(), eventService.getEndTime()));
        int bookingTime = Integer.parseInt(eventService.getBookingTime()) * 60;

        int numberOfEvents = timeDiff / bookingTime;


        for (Event event : events) {
            incrementCount(dateCountMap, event.getDate());
        }

        for (Map.Entry<String, Integer> entry : dateCountMap.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            LocalDate date = LocalDate.parse(key, formatter);


            int day1 = date.getDayOfMonth();
            int month1 = date.getMonthValue();
            int year1 = date.getYear();


            if (year == year1 && month == month1 && (value == numberOfEvents)) {
                    dateAvalability.put(day1, false);



            }



        }
        return dateAvalability;


    }








    public List<Event> selectEventOfParticularServiceId(EventService eventService) {
        List<Event> events = new ArrayList<>();
        PreparedStatement pstmt = null;
        try {
            String query ="SELECT * FROM \"Event\" where \"EventService_id\" = ?";
            pstmt = con.prepareStatement(query);
            pstmt.setInt(1, eventService.getId());

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Event e = new Event(con);
                e.setId(rs.getInt(EVENT_ID));
                e.setServiceId(rs.getInt(EVENT_SERVICE_ID));
                e.setServiceTitle(eventService.getTitle());
                e.setDate(rs.getString("Date"));
                e.setTime(rs.getString("Time"));
                e.setDescription(rs.getString(DESCRIPTION));
                e.setAttendeeCount(String.valueOf(rs.getInt("Attendee_Count")));
                events.add(e);
            }

            return events;
        } catch (Exception e) {
            setStatus("Exception while retrieving data");

            return events;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (Exception e) {
                setStatus("Exception while retrieving data");
            }
        }
    }





}



