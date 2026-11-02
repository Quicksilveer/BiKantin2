package model;

/** Menu makanan / minuman di kantin. */
public class Menu {

    private int id;
    private String nama;
    private String kategori;
    private int harga;
    private String deskripsi;
    private String foto;
    private boolean tersedia;

    public Menu(int id, String nama, String kategori, int harga, String deskripsi, String foto, boolean tersedia) {
        this.id = id;
        this.nama = nama;
        this.kategori = kategori;
        this.harga = harga;
        this.deskripsi = deskripsi;
        this.foto = foto;
        this.tersedia = tersedia;
    }

    public int getId() { return id; }
    public String getNama() { return nama; }
    public String getKategori() { return kategori; }
    public int getHarga() { return harga; }
    public String getDeskripsi() { return deskripsi; }
    public String getFoto() { return foto; }
    public boolean isTersedia() { return tersedia; }
}
