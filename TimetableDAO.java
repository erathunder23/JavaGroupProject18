package dao;

import model.Timetable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimetableDAO {

    // Create timetable entry
    public boolean createTimetableEntry(Timetable timetable) {
        String query = "INSERT INTO timetable (course_id, day_of_week, start_time, end_time, session_type, venue, semester) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, timetable.getCourseId());
            pstmt.setString(2, timetable.getDayOfWeek());
            pstmt.setTime(3, timetable.getStartTime());
            pstmt.setTime(4, timetable.getEndTime());
            pstmt.setString(5, timetable.getSessionType());
            pstmt.setString(6, timetable.getVenue());
            pstmt.setInt(7, timetable.getSemester());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get timetable by semester
    public List<Timetable> getTimetableBySemester(int semester) {
        List<Timetable> timetables = new ArrayList<>();
        String query = "SELECT t.*, c.course_code, c.course_name FROM timetable t " +
                "INNER JOIN courses c ON t.course_id = c.course_id " +
                "WHERE t.semester = ? " +
                "ORDER BY FIELD(t.day_of_week, 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'), t.start_time";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, semester);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Timetable timetable = new Timetable();
                timetable.setTimetableId(rs.getInt("timetable_id"));
                timetable.setCourseId(rs.getInt("course_id"));
                timetable.setCourseCode(rs.getString("course_code"));
                timetable.setCourseName(rs.getString("course_name"));
                timetable.setDayOfWeek(rs.getString("day_of_week"));
                timetable.setStartTime(rs.getTime("start_time"));
                timetable.setEndTime(rs.getTime("end_time"));
                timetable.setSessionType(rs.getString("session_type"));
                timetable.setVenue(rs.getString("venue"));
                timetable.setSemester(rs.getInt("semester"));
                timetables.add(timetable);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return timetables;
    }

    // Update timetable entry
    public boolean updateTimetableEntry(Timetable timetable) {
        String query = "UPDATE timetable SET day_of_week = ?, start_time = ?, end_time = ?, " +
                "session_type = ?, venue = ? WHERE timetable_id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, timetable.getDayOfWeek());
            pstmt.setTime(2, timetable.getStartTime());
            pstmt.setTime(3, timetable.getEndTime());
            pstmt.setString(4, timetable.getSessionType());
            pstmt.setString(5, timetable.getVenue());
            pstmt.setInt(6, timetable.getTimetableId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete timetable entry
    public boolean deleteTimetableEntry(int timetableId) {
        String query = "DELETE FROM timetable WHERE timetable_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, timetableId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}