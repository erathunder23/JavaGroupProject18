package dao;

import model.Notice;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoticeDAO {

    // Create notice with attachment support
    public boolean createNotice(Notice notice) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        String query = "INSERT INTO notices (title, content, target_role, created_by, attachment_path, attachment_name) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, notice.getTitle());
            pstmt.setString(2, notice.getContent());
            pstmt.setString(3, notice.getTargetRole());
            pstmt.setInt(4, notice.getCreatedBy());
            pstmt.setString(5, notice.getAttachmentPath());
            pstmt.setString(6, notice.getAttachmentName());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get notices for a specific role
    public List<Notice> getNoticesForRole(String role) {
        List<Notice> notices = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return notices;

        String query = "SELECT n.*, u.full_name as created_by_name FROM notices n " +
                "LEFT JOIN users u ON n.created_by = u.user_id " +
                "WHERE n.target_role = 'all' OR n.target_role = ? " +
                "ORDER BY n.created_date DESC";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, role);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Notice notice = new Notice();
                notice.setNoticeId(rs.getInt("notice_id"));
                notice.setTitle(rs.getString("title"));
                notice.setContent(rs.getString("content"));
                notice.setTargetRole(rs.getString("target_role"));
                notice.setCreatedBy(rs.getInt("created_by"));
                notice.setCreatedByName(rs.getString("created_by_name"));
                notice.setCreatedDate(rs.getTimestamp("created_date"));
                notice.setExpiryDate(rs.getDate("expiry_date"));
                notice.setAttachmentPath(rs.getString("attachment_path"));
                notice.setAttachmentName(rs.getString("attachment_name"));
                notices.add(notice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notices;
    }

    // Get all notices (for admin and lecturer)
    public List<Notice> getAllNotices() {
        List<Notice> notices = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return notices;

        String query = "SELECT n.*, u.full_name as created_by_name FROM notices n " +
                "LEFT JOIN users u ON n.created_by = u.user_id " +
                "ORDER BY n.created_date DESC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Notice notice = new Notice();
                notice.setNoticeId(rs.getInt("notice_id"));
                notice.setTitle(rs.getString("title"));
                notice.setContent(rs.getString("content"));
                notice.setTargetRole(rs.getString("target_role"));
                notice.setCreatedBy(rs.getInt("created_by"));
                notice.setCreatedByName(rs.getString("created_by_name"));
                notice.setCreatedDate(rs.getTimestamp("created_date"));
                notice.setExpiryDate(rs.getDate("expiry_date"));
                notice.setAttachmentPath(rs.getString("attachment_path"));
                notice.setAttachmentName(rs.getString("attachment_name"));
                notices.add(notice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notices;
    }

    // Delete notice
    public boolean deleteNotice(int noticeId) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        // First get the attachment path to delete the actual file
        String selectQuery = "SELECT attachment_path FROM notices WHERE notice_id = ?";
        String deleteQuery = "DELETE FROM notices WHERE notice_id = ?";

        try {
            // Get attachment path
            String attachmentPath = null;
            try (PreparedStatement pstmt = conn.prepareStatement(selectQuery)) {
                pstmt.setInt(1, noticeId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    attachmentPath = rs.getString("attachment_path");
                }
            }

            // Delete from database
            try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
                pstmt.setInt(1, noticeId);
                int result = pstmt.executeUpdate();

                // Delete physical file if exists
                if (result > 0 && attachmentPath != null && !attachmentPath.isEmpty()) {
                    java.io.File file = new java.io.File(attachmentPath);
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

    // Update notice
    public boolean updateNotice(Notice notice) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        String query = "UPDATE notices SET title = ?, content = ?, target_role = ? WHERE notice_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, notice.getTitle());
            pstmt.setString(2, notice.getContent());
            pstmt.setString(3, notice.getTargetRole());
            pstmt.setInt(4, notice.getNoticeId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}