package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import koneksi.Koneksi;
import model.Menu;

/** Akses data tabel menus (CRUD menu makanan/minuman). */
public class MenuDAO {

    /** Daftar menu yang belum dihapus. hanyaTersedia = true untuk katalog siswa. */
    public List<Menu> cari(String keyword, boolean hanyaTersedia) throws SQLException {
        String sql = "SELECT * FROM menus WHERE deleted_at IS NULL AND (nama LIKE ? OR kategori LIKE ?)"
                + (hanyaTersedia ? " AND tersedia = 1" : "")
                + " ORDER BY kategori, nama";
        List<Menu> list = new ArrayList<>();
        try (PreparedStatement ps = Koneksi.configDB().prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            try (ResultSet r = ps.executeQuery()) {
                while (r.next()) {
                    list.add(new Menu(r.getInt("id"), r.getString("nama"), r.getString("kategori"),
                            r.getInt("harga"), r.getString("deskripsi"), r.getString("foto"),
                            r.getBoolean("tersedia")));
                }
            }
        }
        return list;
    }

    public void tambah(String nama, String kategori, int harga, String deskripsi, String foto, boolean tersedia)
            throws SQLException {
        String sql = "INSERT INTO menus (nama, kategori, harga, deskripsi, foto, tersedia) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = Koneksi.configDB().prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, kategori);
            ps.setInt(3, harga);
            ps.setString(4, deskripsi);
            ps.setString(5, foto);
            ps.setBoolean(6, tersedia);
            ps.executeUpdate();
        }
    }

    public void ubah(int id, String nama, String kategori, int harga, String deskripsi, String foto,
                     boolean tersedia) throws SQLException {
        String sql = "UPDATE menus SET nama = ?, kategori = ?, harga = ?, deskripsi = ?, foto = ?, tersedia = ? "
                + "WHERE id = ?";
        try (PreparedStatement ps = Koneksi.configDB().prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, kategori);
            ps.setInt(3, harga);
            ps.setString(4, deskripsi);
            ps.setString(5, foto);
            ps.setBoolean(6, tersedia);
            ps.setInt(7, id);
            ps.executeUpdate();
        }
    }

    /** Soft delete supaya riwayat pesanan yang memakai menu ini tetap utuh. */
    public void hapus(int id) throws SQLException {
        try (PreparedStatement ps = Koneksi.configDB().prepareStatement(
                "UPDATE menus SET deleted_at = NOW() WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
