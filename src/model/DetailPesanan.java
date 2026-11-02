package model;

/** Satu baris item dalam keranjang / pesanan (tabel order_details). */
public class DetailPesanan {

    private final int menuId;
    private final String namaMenu;
    private final int harga;
    private int jumlah;

    public DetailPesanan(int menuId, String namaMenu, int harga, int jumlah) {
        this.menuId = menuId;
        this.namaMenu = namaMenu;
        this.harga = harga;
        this.jumlah = jumlah;
    }

    public int getSubtotal() {
        return harga * jumlah;
    }

    public int getMenuId() { return menuId; }
    public String getNamaMenu() { return namaMenu; }
    public int getHarga() { return harga; }
    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }
}
