package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import koneksi.Koneksi;
import model.DetailPesanan;
import model.Keranjang;
import model.Pesanan;

/** Akses data tabel orders & order_details. */
public class PesananDAO {

    /**
     * Checkout: simpan isi keranjang sebagai 1 pesanan baru (header + detail) dalam satu transaksi.
     * Mengembalikan id pesanan baru.
     */
    public int buatPesanan(int userId, Keranjang keranjang, String catatan) throws SQLException {
        Connection c = Koneksi.configDB();
        c.setAutoCommit(false);
        try {
            int orderId;
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO orders (user_id, total, catatan) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, userId);
                ps.setInt(2, keranjang.getTotal());
                ps.setString(3, catatan.isBlank() ? null : catatan);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    orderId = keys.getInt(1);
                }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO order_details (order_id, menu_id, jumlah, harga, subtotal) VALUES (?, ?, ?, ?, ?)")) {
                for (DetailPesanan item : keranjang.getItems()) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, item.getMenuId());
                    ps.setInt(3, item.getJumlah());
                    ps.setInt(4, item.getHarga());
                    ps.setInt(5, item.getSubtotal());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            c.commit();
            return orderId;
        } catch (SQLException e) {
            c.rollback();
            throw e;
        } finally {
            c.setAutoCommit(true);
        }
    }

    /**
     * Daftar pesanan terbaru dulu.
     * userId = null -> semua siswa (admin/kasir); statusPembayaran = null -> semua.
     */
    public List<Pesanan> cari(Integer userId, String statusPembayaran) throws SQLException {
        String sql = "SELECT o.*, s.nama AS nama_siswa, k.nama AS nama_kasir FROM orders o "
                + "JOIN users s ON o.user_id = s.id "
                + "LEFT JOIN users k ON o.kasir_id = k.id WHERE 1 = 1"
                + (userId != null ? " AND o.user_id = ?" : "")
                + (statusPembayaran != null ? " AND o.status_pembayaran = ?" : "")
                + " ORDER BY o.created_at DESC, o.id DESC";
        List<Pesanan> list = new ArrayList<>();
        try (PreparedStatement ps = Koneksi.configDB().prepareStatement(sql)) {
            int idx = 1;
            if (userId != null) {
                ps.setInt(idx++, userId);
            }
            if (statusPembayaran != null) {
                ps.setString(idx, statusPembayaran);
            }
            try (ResultSet r = ps.executeQuery()) {
                while (r.next()) {
                    list.add(new Pesanan(r.getInt("id"), r.getInt("user_id"), r.getString("nama_siswa"),
                            r.getInt("total"), r.getString("catatan"), r.getString("status_pesanan"),
                            r.getString("status_pembayaran"), r.getString("nama_kasir"),
                            r.getTimestamp("created_at")));
                }
            }
        }
        return list;
    }

    public List<DetailPesanan> detail(int orderId) throws SQLException {
        String sql = "SELECT d.menu_id, m.nama, d.harga, d.jumlah FROM order_details d "
                + "JOIN menus m ON d.menu_id = m.id WHERE d.order_id = ? ORDER BY d.id";
        List<DetailPesanan> list = new ArrayList<>();
        try (PreparedStatement ps = Koneksi.configDB().prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet r = ps.executeQuery()) {
                while (r.next()) {
                    list.add(new DetailPesanan(r.getInt("menu_id"), r.getString("nama"),
                            r.getInt("harga"), r.getInt("jumlah")));
                }
            }
        }
        return list;
    }

    public void ubahStatusPesanan(int orderId, String status) throws SQLException {
        try (PreparedStatement ps = Koneksi.configDB().prepareStatement(
                "UPDATE orders SET status_pesanan = ? WHERE id = ?")) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }

    /** Kasir memvalidasi pembayaran -> Lunas. Mengembalikan false kalau pesanan sudah lunas sebelumnya. */
    public boolean validasiPembayaran(int orderId, int kasirId) throws SQLException {
        try (PreparedStatement ps = Koneksi.configDB().prepareStatement(
                "UPDATE orders SET status_pembayaran = 'Lunas', kasir_id = ?, dibayar_at = NOW() "
                + "WHERE id = ? AND status_pembayaran = 'Belum Bayar'")) {
            ps.setInt(1, kasirId);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        }
    }
}
