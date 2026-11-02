package model;

import form.FormKasir;
import javax.swing.JFrame;

/** Kasir / petugas pembayaran: validasi pembayaran pesanan. */
public class Kasir extends User {

    public Kasir(int id, String nama, String username, String email, String status) {
        super(id, nama, username, email, status);
    }

    @Override
    public String getRole() {
        return "kasir";
    }

    @Override
    public JFrame bukaDashboard() {
        return new FormKasir(this);
    }
}
