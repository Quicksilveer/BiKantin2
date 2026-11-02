package form;

import dao.MenuDAO;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import model.Menu;
import util.UI;

/** Admin: CRUD menu makanan/minuman. */
public class PanelMenu extends JPanel {

    private final MenuDAO menuDAO = new MenuDAO();
    private final DefaultTableModel model = UI.modelTabel("ID", "Nama", "Kategori", "Harga", "Tersedia", "Deskripsi");
    private final JTable tabel = UI.tabel(model);
    private final JTextField txtCari = new JTextField(15);

    private final JTextField txtNama = new JTextField(18);
    private final JComboBox<String> cbKategori = new JComboBox<>(new String[]{"Makanan", "Minuman", "Snack"});
    private final JTextField txtHarga = new JTextField(18);
    private final JTextArea txtDeskripsi = new JTextArea(4, 18);
    private final JTextField txtFoto = new JTextField(14);
    private final JCheckBox chkTersedia = new JCheckBox("Tersedia", true);

    private List<Menu> data = new ArrayList<>();

    public PanelMenu() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // --- kiri: pencarian + tabel ---
        JButton btnCari = new JButton("Cari");
        btnCari.addActionListener(e -> muat());
        txtCari.addActionListener(e -> muat());
        JPanel cari = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cari.add(new JLabel("Cari menu:"));
        cari.add(txtCari);
        cari.add(btnCari);

        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                isiForm();
            }
        });

        JPanel kiri = new JPanel(new BorderLayout());
        kiri.add(cari, BorderLayout.NORTH);
        kiri.add(new JScrollPane(tabel), BorderLayout.CENTER);

        // --- kanan: form input ---
        txtDeskripsi.setLineWrap(true);
        txtDeskripsi.setWrapStyleWord(true);
        txtFoto.setEditable(false);
        JButton btnFoto = new JButton("Pilih...");
        btnFoto.addActionListener(e -> pilihFoto());
        JPanel foto = new JPanel(new BorderLayout(4, 0));
        foto.add(txtFoto, BorderLayout.CENTER);
        foto.add(btnFoto, BorderLayout.EAST);

        JPanel isian = new JPanel(new GridBagLayout());
        UI.baris(isian, 0, "Nama Menu", txtNama);
        UI.baris(isian, 1, "Kategori", cbKategori);
        UI.baris(isian, 2, "Harga (Rp)", txtHarga);
        UI.baris(isian, 3, "Deskripsi", new JScrollPane(txtDeskripsi));
        UI.baris(isian, 4, "Foto", foto);
        UI.baris(isian, 5, "", chkTersedia);

        JButton btnTambah = new JButton("Tambah");
        JButton btnUbah = new JButton("Ubah");
        JButton btnHapus = new JButton("Hapus");
        JButton btnBersih = new JButton("Bersihkan");
        btnTambah.addActionListener(e -> simpan(false));
        btnUbah.addActionListener(e -> simpan(true));
        btnHapus.addActionListener(e -> hapus());
        btnBersih.addActionListener(e -> bersihkan());
        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tombol.add(btnTambah);
        tombol.add(btnUbah);
        tombol.add(btnHapus);
        tombol.add(btnBersih);

        JPanel kanan = new JPanel(new BorderLayout());
        kanan.setBorder(BorderFactory.createTitledBorder("Data Menu"));
        kanan.add(isian, BorderLayout.NORTH);
        kanan.add(tombol, BorderLayout.CENTER);

        add(kiri, BorderLayout.CENTER);
        add(kanan, BorderLayout.EAST);
        muat();
    }

    private void muat() {
        try {
            data = menuDAO.cari(txtCari.getText().trim(), false);
            model.setRowCount(0);
            for (Menu m : data) {
                model.addRow(new Object[]{
                    m.getId(), m.getNama(), m.getKategori(), UI.rupiah(m.getHarga()),
                    m.isTersedia() ? "Ya" : "Habis", m.getDeskripsi()
                });
            }
        } catch (Exception e) {
            UI.error(this, "Gagal memuat menu: " + e.getMessage());
        }
    }

    private Menu terpilih() {
        int row = tabel.getSelectedRow();
        return row < 0 ? null : data.get(row);
    }

    private void isiForm() {
        Menu m = terpilih();
        if (m == null) {
            return;
        }
        txtNama.setText(m.getNama());
        cbKategori.setSelectedItem(m.getKategori());
        txtHarga.setText(String.valueOf(m.getHarga()));
        txtDeskripsi.setText(m.getDeskripsi() == null ? "" : m.getDeskripsi());
        txtFoto.setText(m.getFoto() == null ? "" : m.getFoto());
        chkTersedia.setSelected(m.isTersedia());
    }

    private void pilihFoto() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Gambar (jpg, png)", "jpg", "jpeg", "png"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            txtFoto.setText(fc.getSelectedFile().getAbsolutePath());
        }
    }

    private void simpan(boolean ubah) {
        Menu m = terpilih();
        if (ubah && m == null) {
            UI.error(this, "Pilih menu di tabel yang ingin diubah.");
            return;
        }
        String nama = txtNama.getText().trim();
        if (nama.isEmpty()) {
            UI.error(this, "Nama menu wajib diisi.");
            return;
        }
        int harga;
        try {
            harga = Integer.parseInt(txtHarga.getText().trim());
            if (harga <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            UI.error(this, "Harga harus berupa angka lebih dari 0.");
            return;
        }
        String kategori = (String) cbKategori.getSelectedItem();
        String deskripsi = txtDeskripsi.getText().trim();
        String foto = txtFoto.getText().isBlank() ? null : txtFoto.getText();
        try {
            if (ubah) {
                menuDAO.ubah(m.getId(), nama, kategori, harga, deskripsi, foto, chkTersedia.isSelected());
                UI.info(this, "Menu berhasil diubah.");
            } else {
                menuDAO.tambah(nama, kategori, harga, deskripsi, foto, chkTersedia.isSelected());
                UI.info(this, "Menu berhasil ditambahkan.");
            }
            bersihkan();
            muat();
        } catch (Exception e) {
            UI.error(this, "Gagal menyimpan menu: " + e.getMessage());
        }
    }

    private void hapus() {
        Menu m = terpilih();
        if (m == null) {
            UI.error(this, "Pilih menu di tabel yang ingin dihapus.");
            return;
        }
        if (!UI.konfirmasi(this, "Hapus menu " + m.getNama() + "?")) {
            return;
        }
        try {
            menuDAO.hapus(m.getId());
            bersihkan();
            muat();
        } catch (Exception e) {
            UI.error(this, "Gagal menghapus menu: " + e.getMessage());
        }
    }

    private void bersihkan() {
        tabel.clearSelection();
        txtNama.setText("");
        cbKategori.setSelectedIndex(0);
        txtHarga.setText("");
        txtDeskripsi.setText("");
        txtFoto.setText("");
        chkTersedia.setSelected(true);
    }
}
