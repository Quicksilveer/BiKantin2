package model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Keranjang belanja siswa sebelum checkout (disimpan di memori). */
public class Keranjang {

    private final Map<Integer, DetailPesanan> items = new LinkedHashMap<>();

    /** Menambah menu; kalau menu sudah ada, jumlahnya ditambahkan. */
    public void tambah(Menu menu, int jumlah) {
        DetailPesanan item = items.get(menu.getId());
        if (item == null) {
            items.put(menu.getId(), new DetailPesanan(menu.getId(), menu.getNama(), menu.getHarga(), jumlah));
        } else {
            item.setJumlah(item.getJumlah() + jumlah);
        }
    }

    public void ubahJumlah(int menuId, int jumlah) {
        DetailPesanan item = items.get(menuId);
        if (item != null) {
            item.setJumlah(jumlah);
        }
    }

    public void hapus(int menuId) {
        items.remove(menuId);
    }

    public void kosongkan() {
        items.clear();
    }

    public boolean isKosong() {
        return items.isEmpty();
    }

    public List<DetailPesanan> getItems() {
        return new ArrayList<>(items.values());
    }

    public int getTotal() {
        int total = 0;
        for (DetailPesanan item : items.values()) {
            total += item.getSubtotal();
        }
        return total;
    }
}
