package dao;

import model.CourseMaterial;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseMaterialDAO {

    public List<CourseMaterial> getMaterialsByCourse(int courseId) {
        List<CourseMaterial> list = new ArrayList<>();
        String query = "SELECT cm.*, u.full_name AS lecturer_name FROM course_materials cm " +
                "JOIN lecturers l ON cm.lecturer_id = l.lecturer_id " +
                "JOIN users u ON l.user_id = u.user_id " +
                "WHERE cm.course_id = ? ORDER BY cm.uploaded_date DESC";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                CourseMaterial m = new CourseMaterial();
                m.setMaterialId(rs.getInt("material_id"));
                m.setCourseId(rs.getInt("course_id"));
                m.setTitle(rs.getString("title"));
                m.setDescription(rs.getString("description"));
                m.setFilePath(rs.getString("file_path"));
                m.setFileName(rs.getString("file_name"));
                m.setFileSize(rs.getLong("file_size"));
                m.setUploadedDate(rs.getTimestamp("uploaded_date"));
                m.setLecturerName(rs.getString("lecturer_name"));
                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
