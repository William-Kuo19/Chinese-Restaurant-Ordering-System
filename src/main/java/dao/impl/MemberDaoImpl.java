package dao.impl;

import dao.MemberDao;
import model.Member;
import util.DBConnection;
import util.LogUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemberDaoImpl implements MemberDao {
    @Override
    public List<Member> findAll() {
        List<Member> list = new ArrayList<>();
        String sql = "SELECT member_id, username, password, name, phone, email, role FROM member ORDER BY member_id";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { LogUtil.error("findAll member failed", e); }
        return list;
    }

    @Override
    public List<Member> search(String keyword) {
        List<Member> list = new ArrayList<>();
        String sql = "SELECT member_id, username, password, name, phone, email, role FROM member " +
                     "WHERE username LIKE ? OR password LIKE ? OR name LIKE ? OR phone LIKE ? OR email LIKE ? OR role LIKE ? ORDER BY member_id";
        String like = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= 6; i++) ps.setString(i, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) { LogUtil.error("search member failed", e); }
        return list;
    }

    @Override
    public Optional<Member> findById(int memberId) {
        String sql = "SELECT member_id, username, password, name, phone, email, role FROM member WHERE member_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) { LogUtil.error("findById member failed", e); }
        return Optional.empty();
    }

    @Override
    public Optional<Member> findByUsername(String username) {
        String sql = "SELECT member_id, username, password, name, phone, email, role FROM member WHERE username = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) { LogUtil.error("findByUsername failed", e); }
        return Optional.empty();
    }

    @Override
    public Optional<Member> login(String username, String password) {
        String sql = "SELECT member_id, username, password, name, phone, email, role FROM member WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) { LogUtil.error("login failed", e); }
        return Optional.empty();
    }

    @Override
    public boolean existsByUsername(String username) { return findByUsername(username).isPresent(); }

    @Override
    public boolean insert(Member member) {
        String sql = "INSERT INTO member(username, password, name, phone, email, role) VALUES(?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            fillStatement(ps, member, false);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { LogUtil.error("insert member failed", e); return false; }
    }

    @Override
    public boolean update(Member member) {
        String sql = "UPDATE member SET username=?, password=?, name=?, phone=?, email=?, role=? WHERE member_id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            fillStatement(ps, member, false);
            ps.setInt(7, member.getMemberId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { LogUtil.error("update member failed", e); return false; }
    }

    @Override
    public boolean delete(int memberId) {
        String sql = "DELETE FROM member WHERE member_id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { LogUtil.error("delete member failed", e); return false; }
    }

    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM member";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { LogUtil.error("count member failed", e); }
        return 0;
    }

    @Override
    public int countAdmins() {
        String sql = "SELECT COUNT(*) FROM member WHERE role='ADMIN'";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { LogUtil.error("count admins failed", e); }
        return 0;
    }

    private void fillStatement(PreparedStatement ps, Member member, boolean includeId) throws SQLException {
        ps.setString(1, member.getUsername());
        ps.setString(2, member.getPassword());
        ps.setString(3, member.getName());
        ps.setString(4, member.getPhone());
        ps.setString(5, member.getEmail());
        ps.setString(6, member.getRole() == null ? "USER" : member.getRole());
    }

    private Member map(ResultSet rs) throws SQLException {
        return new Member(rs.getInt("member_id"), rs.getString("username"), rs.getString("password"), rs.getString("name"), rs.getString("phone"), rs.getString("email"), rs.getString("role"));
    }
}
