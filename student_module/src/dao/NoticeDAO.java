package dao;

import model.Notice;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoticeDAO {

    public List<Notice> getNoticesForRole(String role) {
        List<Notice> list = new ArrayList<>();
        String query = "SELECT * FROM notices WHERE target_role IN ('all', ?) " +
                "AND (expiry_date IS NULL OR expiry_date >= CURDATE()) ORDER BY created_date DESC";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, role);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapNotice(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Notice> getAllNotices() {
        List<Notice> list = new ArrayList<>();
        String query = "SELECT * FROM notices ORDER BY created_date DESC";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(mapNotice(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Notice mapNotice(ResultSet rs) throws SQLException {
        Notice n = new Notice();
        n.setNoticeId(rs.getInt("notice_id"));
        n.setTitle(rs.getString("title"));
        n.setContent(rs.getString("content"));
        n.setTargetRole(rs.getString("target_role"));
        n.setCreatedDate(rs.getTimestamp("created_date"));
        n.setAttachmentPath(rs.getString("attachment_path"));
        n.setAttachmentName(rs.getString("attachment_name"));
        return n;
    }
}
