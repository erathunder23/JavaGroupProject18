package dao;

import model.Timetable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimetableDAO {

    public List<Timetable> getTimetableBySemester(int semester) {
        List<Timetable> list = new ArrayList<>();
        String query = "SELECT t.*, c.course_code FROM timetable t " +
                "JOIN courses c ON t.course_id = c.course_id " +
                "WHERE t.semester = ? ORDER BY FIELD(t.day_of_week, 'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday'), t.start_time";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, semester);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Timetable t = new Timetable();
                t.setTimetableId(rs.getInt("timetable_id"));
                t.setCourseId(rs.getInt("course_id"));
                t.setCourseCode(rs.getString("course_code"));
                t.setDayOfWeek(rs.getString("day_of_week"));
                t.setStartTime(rs.getString("start_time"));
                t.setEndTime(rs.getString("end_time"));
                t.setSessionType(rs.getString("session_type"));
                t.setVenue(rs.getString("venue"));
                t.setSemester(rs.getInt("semester"));
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
