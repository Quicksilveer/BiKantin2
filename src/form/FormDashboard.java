package form;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import model.User;
import util.UI;

/** Kelas induk untuk form Admin, Siswa, dan Kasir (berisi nama user + tombol Logout). */
public abstract class FormDashboard extends JFrame {

    protected final User user;

    protected FormDashboard(String judul, User user) {
        this.user = user;
        setTitle("BI Kantin - " + judul);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(950, 600);

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> logout());

        JPanel atas = new JPanel(new BorderLayout());
        atas.setBorder(BorderFactory.createEmptyBorder(8, 10, 0, 10));
        atas.add(new JLabel("Selamat datang, " + user.getNama()), BorderLayout.WEST);
        atas.add(btnLogout, BorderLayout.EAST);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(atas, BorderLayout.NORTH);
        setLocationRelativeTo(null);
    }

    protected void setKonten(JComponent konten) {
        getContentPane().add(konten, BorderLayout.CENTER);
    }

    /** Dipanggil sebelum logout, misalnya untuk menghentikan timer. */
    protected void sebelumTutup() {
    }

    protected void logout() {
        if (UI.konfirmasi(this, "Yakin ingin logout?")) {
            sebelumTutup();
            dispose();
            new FormLogin().setVisible(true);
        }
    }
}
