package form;

import dao.UserDAO;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import model.User;
import util.UI;

/** Halaman login untuk semua role (Admin, Siswa, Kasir). */
public class FormLogin extends JFrame {

    private final JTextField txtUsername = new JTextField(18);
    private final JPasswordField txtPassword = new JPasswordField(18);
    private final UserDAO userDAO = new UserDAO();

    public FormLogin() {
        setTitle("BI Kantin - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel judul = UI.judul("LOGIN BI KANTIN");
        judul.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel isian = new JPanel(new GridBagLayout());
        UI.baris(isian, 0, "Username", txtUsername);
        UI.baris(isian, 1, "Password", txtPassword);

        JButton btnLogin = new JButton("Login");
        JButton btnDaftar = new JButton("Daftar");
        btnLogin.addActionListener(e -> login());
        btnDaftar.addActionListener(e -> new FormRegistrasi(this).setVisible(true));
        txtPassword.addActionListener(e -> login());
        getRootPane().setDefaultButton(btnLogin);

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.CENTER));
        tombol.add(btnLogin);
        tombol.add(btnDaftar);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        root.add(judul, BorderLayout.NORTH);
        root.add(isian, BorderLayout.CENTER);
        root.add(tombol, BorderLayout.SOUTH);
        setContentPane(root);

        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    private void login() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            UI.error(this, "Username dan password wajib diisi.");
            return;
        }
        try {
            User user = userDAO.login(username, password);
            if (user == null) {
                UI.error(this, "Username atau password salah.");
                txtPassword.setText("");
                return;
            }
            if (!user.isAktif()) {
                UI.error(this, "Akun tidak aktif.");
                return;
            }
            // Polimorfisme: tiap role membuka form-nya sendiri
            user.bukaDashboard().setVisible(true);
            dispose();
        } catch (Exception e) {
            UI.error(this, "Koneksi database gagal: " + e.getMessage());
        }
    }
}
