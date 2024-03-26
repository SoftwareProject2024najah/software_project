package software_project.DataBase.retrieve;

import software_project.UserManagement.User;
import software_project.Vendor.AVendorBooking;
import software_project.helper.Generator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class retrieveuser {
    private Connection con;
    private String status;

    public retrieveuser(Connection con) {
        this.con = con;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<User> selectUsersWithCondition(String condition) {
        List<User> users = new ArrayList<>();
        Statement st = null;
        try {

            String query = "SELECT * FROM users " + condition;
            st = con.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs != null && rs.next())
                users.add(Generator.rsToUser(rs));
            setStatus("Retrieving users successfully");
            return users;
        } catch (Exception e) {
            setStatus("Error while retrieving users from database");
            return new ArrayList<>();
        } finally {
            try {
                if (st != null) st.close();
            } catch (Exception e) {
            }
        }
    }

    public List<User> selectFromusersTable(String field, String input) {
        return selectUsersWithCondition("where " + "\"" + field + "\"" + " = \'" + input + "\';");
    }


    public List<User> findUserByUsername(String username) {
        return selectFromusersTable("User_Name", username);
    }





}