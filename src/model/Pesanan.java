package model;

import java.sql.Timestamp;

/** Pesanan (tabel orders). */
public class Pesanan {

    public static final String[] STATUS_PESANAN = {
        "Menunggu Konfirmasi", "Sedang Dimasak", "Siap Diambil", "Selesai"
    };
    public static final String BELUM_BAYAR = "Belum Bayar";
    public static final String LUNAS = "Lunas";

    private final int id;
    private final int userId;
    private final String namaSiswa;
    private final int total;
    private final String catatan;
    private final String statusPesanan;
    private final String statusPembayaran;
    private final String namaKasir;
    private final Timestamp createdAt;

    public Pesanan(int id, int userId, String namaSiswa, int total, String catatan, String statusPesanan,
                   String statusPembayaran, String namaKasir, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.namaSiswa = namaSiswa;
        this.total = total;
        this.catatan = catatan;
        this.statusPesanan = statusPesanan;
        this.statusPembayaran = statusPembayaran;
        this.namaKasir = namaKasir;
        this.createdAt = createdAt;
    }

    public boolean isLunas() {
        return LUNAS.equals(statusPembayaran);
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getNamaSiswa() { return namaSiswa; }
    public int getTotal() { return total; }
    public String getCatatan() { return catatan; }
    public String getStatusPesanan() { return statusPesanan; }
    public String getStatusPembayaran() { return statusPembayaran; }
    public String getNamaKasir() { return namaKasir; }
    public Timestamp getCreatedAt() { return createdAt; }
}
