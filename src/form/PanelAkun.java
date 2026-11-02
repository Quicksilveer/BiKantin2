package form;

import dao.UserDAO;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.User;
import util.UI;

/** Admin: CRUD akun pengguna dengan pencarian, filter role, dan paginasi. */
public class PanelAkun extends JPanel {

    private static final int PER_HALAMAN = 10;

    private final UserDAO userDAO = new UserDAO();
    private final User adminLogin;

    private final DefaultTableModel model = UI.modelTabel("ID", "Nama", "Username", "Email", "Role", "Status");
    private final JTable tabel = UI.tabel(model);
    private final JTextField txtCari = new JTextField(14);
    private final JComboBox<String> cbFilterRole = new JComboBox<>(new String[]{"Semua", "admin", "siswa", "kasir"});
    private final JLabel lblHalaman = new JLabel();
    private final JButton btnSebelum = new JButton("<");
    private final JButton btnBerikut = new JButton(">");

    private final JTextField txtNama = new JTextField(16);
    private final JTextField txtUsername = new JTextField(16);
    private final JTextField txtEmail = new JTextField(16);
    private final JPasswordField txtPassword = new JPasswordField(16);
    private final JComboBox<String> cbRole = new JComboBox<>(new String[]{"siswa", "kasir", "admin"});
    private final JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Aktif", "Non-aktif"});

    private List<User> data = new ArrayList<>();
    private int halaman = 1;
    private int totalHalaman = 1;

    public PanelAkun(User adminLogin) {
        super(new BorderLayout(8, 8));
        this.adminLogin = adminLogin;
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // --- kiri: pencarian, filter, tabel, paginasi ---
        JButton btnCari = new JButton("Cari");
        btnCari.addActionListener(e -> { halaman = 1; muat(); });
        txtCari.addActionListener(e -> { halaman = 1; muat(); });
        cbFilterRole.addActionListener(e -> { halaman = 1; muat(); });
        JPanel cari = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cari.add(new JLabel("Cari:"));
        cari.add(txtCari);
        cari.add(new JLabel("Role:"));
        cari.add(cbFilterRole);
        cari.add(btnCari);

        btnSebelum.addActionListener(e -> { halaman--; muat(); });
        btnBerikut.addActionListener(e -> { halaman++; muat(); });
        JPanel paginasi = new JPanel(new FlowLayout(FlowLayout.CENTER));
        paginasi.add(btnSebelum);
        paginasi.add(lblHalaman);
        paginasi.add(btnBerikut);

        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                isiForm();
            }
        });

        JPanel kiri = new JPanel(new BorderLayout());
        kiri.add(cari, BorderLayout.NORTH);
        kiri.add(new JScrollPane(tabel), BorderLayout.CENTER);
        kiri.add(paginasi, BorderLayout.SOUTH);

        // --- kanan: form akun ---
        JPanel isian = new JPanel(new GridBagLayout());
        UI.baris(isian, 0, "Nama Lengkap", txtNama);
        UI.baris(isian, 1, "Username", txtUsername);
        UI.baris(isian, 2, "Email", txtEmail);
        UI.baris(isian, 3, "Password", txtPassword);
        UI.baris(isian, 4, "Role", cbRole);
        UI.baris(isian, 5, "Status", cbStatus);
        JButton btnTambah = new JButton("Tambah");
        JButton btnUbah = new JButton("Ubah");
        JButton btnReset = new JButton("Reset Password");
        JButton btnHapus = new JButton("Hapus");
        JButton btnBersih = new JButton("Bersihkan");
        btnTambah.addActionListener(e -> tambah());
        btnUbah.addActionListener(e -> ubah());
        btnReset.addActionListener(e -> resetPassword());
        btnHapus.addActionListener(e -> hapus());
        btnBersih.addActionListener(e -> bersihkan());
        JPanel tombol = new JPanel(new GridBagLayout());
        JPanel t1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        t1.add(btnTambah);
        t1.add(btnUbah);
        t1.add(btnHapus);
        JPanel t2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        t2.add(btnReset);
        t2.add(btnBersih);
        UI.baris(tombol, 0, "", t1);
        UI.baris(tombol, 1, "", t2);

        JPanel kanan = new JPanel(new BorderLayout());
        kanan.setBorder(BorderFactory.createTitledBorder("Data Akun"));
        kanan.add(isian, BorderLayout.NORTH);
        kanan.add(tombol, BorderLayout.SOUTH);
        JPanel kananWrap = new JPanel(new BorderLayout());
        kananWrap.add(kanan, BorderLayout.NORTH);

        add(kiri, BorderLayout.CENTER);
        add(kananWrap, BorderLayout.EAST);
        muat();
    }

    private String filterRole() {
        String r = (String) cbFilterRole.getSelectedItem();
        return "Semua".equals(r) ? null : r;
    }

    private void muat() {
        try {
            String keyword = txtCari.getText().trim();
            int total = userDAO.hitung(keyword, filterRole());
            totalHalaman = Math.max(1, (int) Math.ceil(total / (double) PER_HALAMAN));
            halaman = Math.max(1, Math.min(halaman, totalHalaman));

            data = userDAO.cari(keyword, filterRole(), PER_HALAMAN, (halaman - 1) * PER_HALAMAN);
            model.setRowCount(0);
            for (User u : data) {
                model.addRow(new Object[]{
                    u.getId(), u.getNama(), u.getUsername(), u.getEmail(), u.getRole(), u.getStatus()
                });
            }
            lblHalaman.setText("Halaman " + halaman + " / " + totalHalaman);
            btnSebelum.setEnabled(halaman > 1);
            btnBerikut.setEnabled(halaman < totalHalaman);
        } catch (Exception e) {
            UI.error(this, "Gagal memuat akun: " + e.getMessage());
        }
    }

    private User terpilih() {
        int row = tabel.getSelectedRow();
        return row < 0 ? null : data.get(row);
    }

    private void isiForm() {
        User u = terpilih();
        if (u == null) {
            return;
        }
        txtNama.setText(u.getNama());
        txtUsername.setText(u.getUsername());
        txtEmail.setText(u.getEmail());
        txtPassword.setText("");
        cbRole.setSelectedItem(u.getRole());
        cbStatus.setSelectedItem(u.getStatus());
    }

    /** Validasi input umum. Mengembalikan pesan error, atau null kalau valid. */
    private String validasi(int excludeId) throws Exception {
        if (txtNama.getText().isBlank() || txtUsername.getText().isBlank() || txtEmail.getText().isBlank()) {
            return "Nama, username, dan email wajib diisi.";
        }
        if (!txtEmail.getText().trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return "Format email tidak valid.";
        }
        if (userDAO.sudahDipakai(txtUsername.getText().trim(), txtEmail.getText().trim(), excludeId)) {
            return "Username atau email sudah dipakai akun lain.";
        }
        return null;
    }

    private void tambah() {
        String password = new String(txtPassword.getPassword());
        try {
            String err = validasi(0);
            if (err == null && password.length() < 6) {
                err = "Password minimal 6 karakter.";
            }
            if (err != null) {
                UI.error(this, err);
                return;
            }
            userDAO.tambah(txtNama.getText().trim(), txtUsername.getText().trim(), txtEmail.getText().trim(),
                    password, (String) cbRole.getSelectedItem(), (String) cbStatus.getSelectedItem());
            UI.info(this, "Akun berhasil ditambahkan.");
            bersihkan();
            muat();
        } catch (Exception e) {
            UI.error(this, "Gagal menambah akun: " + e.getMessage());
        }
    }

    private void ubah() {
        User u = terpilih();
        if (u == null) {
            UI.error(this, "Pilih akun di tabel yang ingin diubah.");
            return;
        }
        String role = (String) cbRole.getSelectedItem();
        String status = (String) cbStatus.getSelectedItem();
        if (u.getId() == adminLogin.getId() && (!"admin".equals(role) || !"Aktif".equals(status))) {
            UI.error(this, "Tidak bisa mengubah role atau status akun sendiri.");
            return;
        }
        try {
            String err = validasi(u.getId());
            if (err != null) {
                UI.error(this, err);
                return;
            }
            userDAO.ubah(u.getId(), txtNama.getText().trim(), txtUsername.getText().trim(),
                    txtEmail.getText().trim(), role, status);
            UI.info(this, "Akun berhasil diubah.");
            bersihkan();
            muat();
        } catch (Exception e) {
            UI.error(this, "Gagal mengubah akun: " + e.getMessage());
        }
    }

    private void resetPassword() {
        User u = terpilih();
        if (u == null) {
            UI.error(this, "Pilih akun yang password-nya ingin direset.");
            return;
        }
        JPasswordField pf = new JPasswordField();
        int ok = JOptionPane.showConfirmDialog(this, new Object[]{"Password baru untuk " + u.getUsername() + ":", pf},
                "Reset Password", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) {
            return;
        }
        String baru = new String(pf.getPassword());
        if (baru.length() < 6) {
            UI.error(this, "Password minimal 6 karakter.");
            return;
        }
        try {
            userDAO.resetPassword(u.getId(), baru);
            UI.info(this, "Password " + u.getUsername() + " berhasil direset.");
        } catch (Exception e) {
            UI.error(this, "Gagal reset password: " + e.getMessage());
        }
    }

    private void hapus() {
        User u = terpilih();
        if (u == null) {
            UI.error(this, "Pilih akun yang ingin dihapus.");
            return;
        }
        if (u.getId() == adminLogin.getId()) {
            UI.error(this, "Tidak bisa menghapus akun sendiri.");
            return;
        }
        if (!UI.konfirmasi(this, "Hapus akun " + u.getUsername() + "?")) {
            return;
        }
        try {
            userDAO.hapus(u.getId());
            bersihkan();
            muat();
        } catch (Exception e) {
            UI.error(this, "Gagal menghapus akun: " + e.getMessage());
        }
    }

    private void bersihkan() {
        tabel.clearSelection();
        txtNama.setText("");
        txtUsername.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        cbRole.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);
    }
}
