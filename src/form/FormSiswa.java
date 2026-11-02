package form;

import dao.MenuDAO;
import dao.PesananDAO;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import model.DetailPesanan;
import model.Keranjang;
import model.Menu;
import model.User;
import util.UI;

/** Form Siswa: lihat menu, keranjang, checkout, dan status pesanan. */
public class FormSiswa extends FormDashboard {

    private final MenuDAO menuDAO = new MenuDAO();
    private final PesananDAO pesananDAO = new PesananDAO();
    private final Keranjang keranjang = new Keranjang();

    private final DefaultTableModel modelMenu = UI.modelTabel("Nama Menu", "Kategori", "Harga", "Deskripsi");
    private final JTable tblMenu = UI.tabel(modelMenu);
    private final JTextField txtCari = new JTextField(14);
    private final JSpinner spJumlah = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));

    private final DefaultTableModel modelKeranjang = UI.modelTabel("Menu", "Harga", "Jumlah", "Subtotal");
    private final JTable tblKeranjang = UI.tabel(modelKeranjang);
    private final JLabel lblTotal = new JLabel("Total: Rp 0");
    private final JTextField txtCatatan = new JTextField(20);

    private final PanelDaftarPesanan panelStatus = new PanelDaftarPesanan();
    private final JTabbedPane tab = new JTabbedPane();
    private final Timer timer;

    private List<Menu> dataMenu = new ArrayList<>();

    public FormSiswa(User user) {
        super("Siswa", user);

        tab.addTab("Pesan Menu", buatPanelPesan());
        tab.addTab("Pesanan Saya", panelStatus);
        setKonten(tab);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> panelStatus.muat());
        panelStatus.getPanelAksi().add(btnRefresh);
        panelStatus.setFilter(user.getId(), null);
        panelStatus.muat();

        muatMenu();
        // refresh status pesanan tiap 5 detik
        timer = new Timer(5000, e -> panelStatus.muat());
        timer.start();
    }

    private JPanel buatPanelPesan() {
        // daftar menu
        JButton btnCari = new JButton("Cari");
        btnCari.addActionListener(e -> muatMenu());
        txtCari.addActionListener(e -> muatMenu());
        JPanel cari = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cari.add(new JLabel("Cari:"));
        cari.add(txtCari);
        cari.add(btnCari);

        JButton btnTambah = new JButton("Tambah");
        btnTambah.addActionListener(e -> tambahKeKeranjang());
        JPanel aksiMenu = new JPanel(new FlowLayout(FlowLayout.LEFT));
        aksiMenu.add(new JLabel("Jumlah:"));
        aksiMenu.add(spJumlah);
        aksiMenu.add(btnTambah);

        JPanel panelMenu = new JPanel(new BorderLayout());
        panelMenu.setBorder(BorderFactory.createTitledBorder("Menu"));
        panelMenu.add(cari, BorderLayout.NORTH);
        panelMenu.add(new JScrollPane(tblMenu), BorderLayout.CENTER);
        panelMenu.add(aksiMenu, BorderLayout.SOUTH);

        // keranjang
        JButton btnUbah = new JButton("Ubah Jumlah");
        JButton btnHapus = new JButton("Hapus");
        JButton btnCheckout = new JButton("Checkout");
        btnUbah.addActionListener(e -> ubahJumlah());
        btnHapus.addActionListener(e -> hapusItem());
        btnCheckout.addActionListener(e -> checkout());
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD));

        JPanel aksiKeranjang = new JPanel(new FlowLayout(FlowLayout.LEFT));
        aksiKeranjang.add(btnUbah);
        aksiKeranjang.add(btnHapus);
        aksiKeranjang.add(new JLabel("Catatan:"));
        aksiKeranjang.add(txtCatatan);
        JPanel checkout = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        checkout.add(lblTotal);
        checkout.add(btnCheckout);
        JPanel bawah = new JPanel(new GridLayout(2, 1));
        bawah.add(aksiKeranjang);
        bawah.add(checkout);

        JPanel panelKeranjang = new JPanel(new BorderLayout());
        panelKeranjang.setBorder(BorderFactory.createTitledBorder("Keranjang"));
        panelKeranjang.add(new JScrollPane(tblKeranjang), BorderLayout.CENTER);
        panelKeranjang.add(bawah, BorderLayout.SOUTH);

        JPanel p = new JPanel(new GridLayout(2, 1, 5, 5));
        p.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        p.add(panelMenu);
        p.add(panelKeranjang);
        return p;
    }

    private void muatMenu() {
        try {
            dataMenu = menuDAO.cari(txtCari.getText().trim(), true);
            modelMenu.setRowCount(0);
            for (Menu m : dataMenu) {
                modelMenu.addRow(new Object[]{m.getNama(), m.getKategori(), UI.rupiah(m.getHarga()), m.getDeskripsi()});
            }
        } catch (Exception e) {
            UI.error(this, "Gagal memuat menu: " + e.getMessage());
        }
    }

    private void tambahKeKeranjang() {
        int row = tblMenu.getSelectedRow();
        if (row < 0) {
            UI.error(this, "Pilih menu dulu.");
            return;
        }
        keranjang.tambah(dataMenu.get(row), (Integer) spJumlah.getValue());
        muatKeranjang();
        spJumlah.setValue(1);
    }

    private DetailPesanan itemTerpilih() {
        int row = tblKeranjang.getSelectedRow();
        return row < 0 ? null : keranjang.getItems().get(row);
    }

    private void ubahJumlah() {
        DetailPesanan item = itemTerpilih();
        if (item == null) {
            UI.error(this, "Pilih item di keranjang dulu.");
            return;
        }
        keranjang.ubahJumlah(item.getMenuId(), (Integer) spJumlah.getValue());
        muatKeranjang();
    }

    private void hapusItem() {
        DetailPesanan item = itemTerpilih();
        if (item == null) {
            UI.error(this, "Pilih item di keranjang dulu.");
            return;
        }
        keranjang.hapus(item.getMenuId());
        muatKeranjang();
    }

    private void muatKeranjang() {
        modelKeranjang.setRowCount(0);
        for (DetailPesanan d : keranjang.getItems()) {
            modelKeranjang.addRow(new Object[]{
                d.getNamaMenu(), UI.rupiah(d.getHarga()), d.getJumlah(), UI.rupiah(d.getSubtotal())
            });
        }
        lblTotal.setText("Total: " + UI.rupiah(keranjang.getTotal()));
    }

    private void checkout() {
        if (keranjang.isKosong()) {
            UI.error(this, "Keranjang masih kosong.");
            return;
        }
        if (!UI.konfirmasi(this, "Pesan sekarang? Total " + UI.rupiah(keranjang.getTotal()))) {
            return;
        }
        try {
            pesananDAO.buatPesanan(user.getId(), keranjang, txtCatatan.getText().trim());
            keranjang.kosongkan();
            txtCatatan.setText("");
            muatKeranjang();
            UI.info(this, "Pesanan berhasil dibuat. Silakan bayar ke kasir.");
            panelStatus.muat();
            tab.setSelectedComponent(panelStatus);
        } catch (Exception e) {
            UI.error(this, "Checkout gagal: " + e.getMessage());
        }
    }

    @Override
    protected void sebelumTutup() {
        timer.stop();
    }
}
