package model;

import form.FormSiswa;
import javax.swing.JFrame;

/** Siswa / pembeli: katalog, keranjang, checkout, pantau status pesanan. */
public class Siswa extends User {

    public Siswa(int id, String nama, String username, String email, String status) {
        super(id, nama, username, email, status);
    }

    @Override
    public String getRole() {
        return "siswa";
    }

    @Override
    public JFrame bukaDashboard() {
        return new FormSiswa(this);
    }
}
