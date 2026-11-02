package form;

import dao.UserDAO;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import util.UI;

/** Registrasi akun baru untuk siswa. */
public class FormRegistrasi extends JDialog {

    private final JTextField txtNama = new JTextField(20);
    private final JTextField txtUsername = new JTextField(20);
    private final JTextField txtEmail = new JTextField(20);
    private final JPasswordField txtPassword = new JPasswordField(20);
    private final JPasswordField txtKonfirmasi = new JPasswordField(20);
    private final UserDAO userDAO = new UserDAO();

    public FormRegistrasi(JFrame parent) {
        super(parent, "BI Kantin - Daftar", true);

        JPanel isian = new JPanel(new GridBagLayout());
        UI.baris(isian, 0, "Nama Lengkap", txtNama);
        UI.baris(isian, 1, "Username", txtUsername);
        UI.baris(isian, 2, "Email", txtEmail);
        UI.baris(isian, 3, "Password", txtPassword);
        UI.baris(isian, 4, "Ulangi Password", txtKonfirmasi);

        JButton btnDaftar = new JButton("Daftar");
        JButton btnBatal = new JButton("Batal");
        btnDaftar.addActionListener(e -> daftar());
        btnBatal.addActionListener(e -> dispose());
        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tombol.add(btnDaftar);
        tombol.add(btnBatal);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        root.add(UI.judul("DAFTAR AKUN"), BorderLayout.NORTH);
        root.add(isian, BorderLayout.CENTER);
        root.add(tombol, BorderLayout.SOUTH);
        setContentPane(root);
        getRootPane().setDefaultButton(btnDaftar);

        pack();
        setLocationRelativeTo(parent);
    }

    private void daftar() {
        String nama = txtNama.getText().trim();
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        String konfirmasi = new String(txtKonfirmasi.getPassword());

        if (nama.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            UI.error(this, "Semua kolom wajib diisi.");
            return;
        }
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            UI.error(this, "Format email tidak valid.");
            return;
        }
        if (password.length() < 6) {
            UI.error(this, "Password minimal 6 karakter.");
            return;
        }
        if (!password.equals(konfirmasi)) {
            UI.error(this, "Konfirmasi password tidak sama.");
            return;
        }
        try {
            if (userDAO.sudahDipakai(username, email, 0)) {
                UI.error(this, "Username atau email sudah terdaftar.");
                return;
            }
            userDAO.tambah(nama, username, email, password, "siswa", "Aktif");
            UI.info(this, "Pendaftaran berhasil, silakan login.");
            dispose();
        } catch (Exception e) {
            UI.error(this, "Registrasi gagal: " + e.getMessage());
        }
    }
}
