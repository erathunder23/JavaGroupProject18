package dao;

import model.CourseMaterial;
import model.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseMaterialDAO {

    // Upload course material
    public boolean uploadMaterial(CourseMaterial material) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        String query = "INSERT INTO course_materials (course_id, lecturer_id, title, description, file_path, file_name, file_size) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, material.getCourseId());
            pstmt.setInt(2, material.getLecturerId());
            pstmt.setString(3, material.getTitle());
            pstmt.setString(4, material.getDescription());
            pstmt.setString(5, material.getFilePath());
            pstmt.setString(6, material.getFileName());
            pstmt.setLong(7, material.getFileSize());

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    material.setMaterialId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get materials for a course
    public List<CourseMaterial> getMaterialsByCourse(int courseId) {
        List<CourseMaterial> materials = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return materials;

        String query = "SELECT cm.*, c.course_code, c.course_name, u.full_name as lecturer_name " +
                "FROM course_materials cm " +
                "INNER JOIN courses c ON cm.course_id = c.course_id " +
                "INNER JOIN lecturers l ON cm.lecturer_id = l.lecturer_id " +
                "INNER JOIN users u ON l.user_id = u.user_id " +
                "WHERE cm.course_id = ? " +
                "ORDER BY cm.uploaded_date DESC";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                CourseMaterial material = new CourseMaterial();
                material.setMaterialId(rs.getInt("material_id"));
                material.setCourseId(rs.getInt("course_id"));
                material.setCourseCode(rs.getString("course_code"));
                material.setCourseName(rs.getString("course_name"));
                material.setLecturerId(rs.getInt("lecturer_id"));
                material.setLecturerName(rs.getString("lecturer_name"));
                material.setTitle(rs.getString("title"));
                material.setDescription(rs.getString("description"));
                material.setFilePath(rs.getString("file_path"));
                material.setFileName(rs.getString("file_name"));
                material.setFileSize(rs.getLong("file_size"));
                material.setUploadedDate(rs.getTimestamp("uploaded_date"));
                materials.add(material);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materials;
    }

    // Get all materials for a lecturer
    public List<CourseMaterial> getMaterialsByLecturer(int lecturerId) {
        List<CourseMaterial> materials = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return materials;

        String query = "SELECT cm.*, c.course_code, c.course_name, u.full_name as lecturer_name " +
                "FROM course_materials cm " +
                "INNER JOIN courses c ON cm.course_id = c.course_id " +
                "INNER JOIN lecturers l ON cm.lecturer_id = l.lecturer_id " +
                "INNER JOIN users u ON l.user_id = u.user_id " +
                "WHERE cm.lecturer_id = ? " +
                "ORDER BY cm.uploaded_date DESC";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, lecturerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                CourseMaterial material = new CourseMaterial();
                material.setMaterialId(rs.getInt("material_id"));
                material.setCourseId(rs.getInt("course_id"));
                material.setCourseCode(rs.getString("course_code"));
                material.setCourseName(rs.getString("course_name"));
                material.setLecturerId(rs.getInt("lecturer_id"));
                material.setLecturerName(rs.getString("lecturer_name"));
                material.setTitle(rs.getString("title"));
                material.setDescription(rs.getString("description"));
                material.setFilePath(rs.getString("file_path"));
                material.setFileName(rs.getString("file_name"));
                material.setFileSize(rs.getLong("file_size"));
                material.setUploadedDate(rs.getTimestamp("uploaded_date"));
                materials.add(material);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materials;
    }

    // Delete material
    public boolean deleteMaterial(int materialId) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        // First get the file path to delete the actual file
        String selectQuery = "SELECT file_path FROM course_materials WHERE material_id = ?";
        String deleteQuery = "DELETE FROM course_materials WHERE material_id = ?";

        try {
            // Get file path
            String filePath = null;
            try (PreparedStatement pstmt = conn.prepareStatement(selectQuery)) {
                pstmt.setInt(1, materialId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    filePath = rs.getString("file_path");
                }
            }

            // Delete from database
            try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
                pstmt.setInt(1, materialId);
                int result = pstmt.executeUpdate();

                // Delete physical file if exists
                if (result > 0 && filePath != null) {
                    java.io.File file = new java.io.File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
                return result > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update material
    public boolean updateMaterial(CourseMaterial material) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        String query = "UPDATE course_materials SET title = ?, description = ? WHERE material_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, material.getTitle());
            pstmt.setString(2, material.getDescription());
            pstmt.setInt(3, material.getMaterialId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}