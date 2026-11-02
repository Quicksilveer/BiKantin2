package model;

import javax.swing.JFrame;

/**
 * Kelas induk (abstract) untuk semua pengguna BI-KANTIN.
 * Subclass: Admin, Siswa, Kasir. Setiap subclass membuka dashboard-nya sendiri (polimorfisme).
 */
public abstract class User {

    protected int id;
    protected String nama;
    protected String username;
    protected String email;
    protected String status;

    public User(int id, String nama, String username, String email, String status) {
        this.id = id;
        this.nama = nama;
        this.username = username;
        this.email = email;
        this.status = status;
    }

    /** Nilai kolom role di database: admin / siswa / kasir. */
    public abstract String getRole();

    /** Membuat jendela dashboard sesuai role. */
    public abstract JFrame bukaDashboard();

    public boolean isAktif() {
        return "Aktif".equals(status);
    }

    /** Factory: membuat objek subclass yang sesuai dengan role. */
    public static User buat(int id, String nama, String username, String email, String role, String status) {
        return switch (role) {
            case "admin" -> new Admin(id, nama, username, email, status);
            case "kasir" -> new Kasir(id, nama, username, email, status);
            default -> new Siswa(id, nama, username, email, status);
        };
    }

    public int getId() { return id; }
    public String getNama() { return nama; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }

    public void setNama(String nama) { this.nama = nama; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setStatus(String status) { this.status = status; }
}
