package model;

import form.FormAdmin;
import javax.swing.JFrame;

/** Admin / pengelola kantin: CRUD menu, CRUD akun, manajemen pesanan. */
public class Admin extends User {

    public Admin(int id, String nama, String username, String email, String status) {
        super(id, nama, username, email, status);
    }

    @Override
    public String getRole() {
        return "admin";
    }

    @Override
    public JFrame bukaDashboard() {
        return new FormAdmin(this);
    }
}
