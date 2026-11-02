package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import koneksi.Koneksi;
import model.User;
import util.PasswordUtil;

/** Akses data tabel users (login, registrasi, CRUD akun). */
public class UserDAO {

    private static final String KOLOM = "id, nama, username, email, role, status";

    private User dariResultSet(ResultSet r) throws SQLException {
        return User.buat(r.getInt("id"), r.getString("nama"), r.getString("username"),
                r.getString("email"), r.getString("role"), r.getString("status"));
    }

    /** Login pakai username atau email. Mengembalikan null kalau salah. Akun terhapus tidak bisa login. */
    public User login(String usernameAtauEmail, String password) throws SQLException {
        String sql = "SELECT " + KOLOM + " FROM users "
                + "WHERE (username = ? OR email = ?) AND password = ? AND deleted_at IS NULL";
        try (PreparedStatement ps = Koneksi.configDB().prepareStatement(sql)) {
            ps.setString(1, usernameAtauEmail);
            ps.setString(2, usernameAtauEmail);
            ps.setString(3, PasswordUtil.hash(password));
            try (ResultSet r = ps.executeQuery()) {
                return r.next() ? dariResultSet(r) : null;
            }
        }
    }

    /** Cek apakah username/email sudah dipakai akun lain (excludeId = id akun yang sedang diedit, 0 kalau baru). */
    public boolean sudahDipakai(String username, String email, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE (username = ? OR email = ?) AND id <> ?";
        try (PreparedStatement ps = Koneksi.configDB().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setInt(3, excludeId);
            try (ResultSet r = ps.executeQuery()) {
                r.next();
                return r.getInt(1) > 0;
            }
        }
    }

    public void tambah(String nama, String username, String email, String password,
                       String role, String status) throws SQLException {
        String sql = "INSERT INTO users (nama, username, email, password, role, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = Koneksi.configDB().prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, username);
            ps.setString(3, email);
            ps.setString(4, PasswordUtil.hash(password));
            ps.setString(5, role);
            ps.setString(6, status);
            ps.executeUpdate();
        }
    }

    public void ubah(int id, String nama, String username, String email, String role, String status)
            throws SQLException {
        String sql = "UPDATE users SET nama = ?, username = ?, email = ?, role = ?, status = ? "
                + "WHERE id = ? AND deleted_at IS NULL";
        try (PreparedStatement ps = Koneksi.configDB().prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, username);
            ps.setString(3, email);
            ps.setString(4, role);
            ps.setString(5, status);
            ps.setInt(6, id);
            ps.executeUpdate();
        }
    }

    public void resetPassword(int id, String passwordBaru) throws SQLException {
        try (PreparedStatement ps = Koneksi.configDB().prepareStatement(
                "UPDATE users SET password = ? WHERE id = ?")) {
            ps.setString(1, PasswordUtil.hash(passwordBaru));
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    /** Soft delete: akun ditandai terhapus & non-aktif, datanya tetap ada untuk riwayat transaksi. */
    public void hapus(int id) throws SQLException {
        try (PreparedStatement ps = Koneksi.configDB().prepareStatement(
                "UPDATE users SET deleted_at = NOW(), status = 'Non-aktif' WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /** Bagian WHERE untuk pencarian + filter role (dipakai cari() dan hitung()). */
    private String where(String keyword, String role) {
        String w = " WHERE deleted_at IS NULL AND (nama LIKE ? OR username LIKE ? OR email LIKE ?)";
        if (role != null) {
            w += " AND role = ?";
        }
        return w;
    }

    private int isiParameter(PreparedStatement ps, String keyword, String role) throws SQLException {
        String k = "%" + keyword + "%";
        ps.setString(1, k);
        ps.setString(2, k);
        ps.setString(3, k);
        int idx = 4;
        if (role != null) {
            ps.setString(idx++, role);
        }
        return idx;
    }

    /** Daftar akun dengan pencarian, filter role (null = semua), dan paginasi. */
    public List<User> cari(String keyword, String role, int limit, int offset) throws SQLException {
        String sql = "SELECT " + KOLOM + " FROM users" + where(keyword, role) + " ORDER BY id LIMIT ? OFFSET ?";
        List<User> list = new ArrayList<>();
        Connection c = Koneksi.configDB();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            int idx = isiParameter(ps, keyword, role);
            ps.setInt(idx++, limit);
            ps.setInt(idx, offset);
            try (ResultSet r = ps.executeQuery()) {
                while (r.next()) {
                    list.add(dariResultSet(r));
                }
            }
        }
        return list;
    }

    public int hitung(String keyword, String role) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users" + where(keyword, role);
        try (PreparedStatement ps = Koneksi.configDB().prepareStatement(sql)) {
            isiParameter(ps, keyword, role);
            try (ResultSet r = ps.executeQuery()) {
                r.next();
                return r.getInt(1);
            }
        }
    }
}
