package form;

import dao.PesananDAO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.Timer;
import model.Pesanan;
import model.User;
import util.UI;

/** Form Kasir: validasi pembayaran pesanan (Lunas / Siap Diambil). */
public class FormKasir extends FormDashboard {

    private final PanelDaftarPesanan panelPesanan = new PanelDaftarPesanan();
    private final JComboBox<String> cbFilter = new JComboBox<>(new String[]{"Belum Bayar", "Lunas", "Semua"});
    private final PesananDAO pesananDAO = new PesananDAO();
    private final Timer timer;

    public FormKasir(User user) {
        super("Kasir", user);

        JButton btnLunas = new JButton("Lunas");
        JButton btnSiap = new JButton("Siap Diambil");
        JButton btnRefresh = new JButton("Refresh");
        btnLunas.addActionListener(e -> validasiPembayaran());
        btnSiap.addActionListener(e -> tandaiSiapDiambil());
        btnRefresh.addActionListener(e -> panelPesanan.muat());
        cbFilter.addActionListener(e -> terapkanFilter());

        panelPesanan.getPanelAksi().add(new JLabel("Filter:"));
        panelPesanan.getPanelAksi().add(cbFilter);
        panelPesanan.getPanelAksi().add(btnLunas);
        panelPesanan.getPanelAksi().add(btnSiap);
        panelPesanan.getPanelAksi().add(btnRefresh);
        terapkanFilter();
        setKonten(panelPesanan);

        timer = new Timer(5000, e -> panelPesanan.muat());
        timer.start();
    }

    private void terapkanFilter() {
        String f = (String) cbFilter.getSelectedItem();
        panelPesanan.setFilter(null, "Semua".equals(f) ? null : f);
        panelPesanan.muat();
    }

    private void validasiPembayaran() {
        Pesanan p = panelPesanan.getTerpilih();
        if (p == null) {
            UI.error(this, "Pilih pesanan dulu.");
            return;
        }
        if (p.isLunas()) {
            UI.info(this, "Pesanan sudah lunas.");
            return;
        }
        if (!UI.konfirmasi(this, "Pesanan #" + p.getId() + " (" + p.getNamaSiswa() + ") "
                + UI.rupiah(p.getTotal()) + " sudah dibayar?")) {
            return;
        }
        try {
            if (pesananDAO.validasiPembayaran(p.getId(), user.getId())) {
                UI.info(this, "Pembayaran berhasil.");
            } else {
                UI.info(this, "Pesanan sudah lunas.");
            }
            panelPesanan.muat();
        } catch (Exception e) {
            UI.error(this, "Gagal validasi pembayaran: " + e.getMessage());
        }
    }

    private void tandaiSiapDiambil() {
        Pesanan p = panelPesanan.getTerpilih();
        if (p == null) {
            UI.error(this, "Pilih pesanan dulu.");
            return;
        }
        if (!p.isLunas()) {
            UI.error(this, "Pesanan belum lunas.");
            return;
        }
        if ("Selesai".equals(p.getStatusPesanan())) {
            UI.info(this, "Pesanan sudah selesai.");
            return;
        }
        try {
            pesananDAO.ubahStatusPesanan(p.getId(), "Siap Diambil");
            panelPesanan.muat();
        } catch (Exception e) {
            UI.error(this, "Gagal mengubah status: " + e.getMessage());
        }
    }

    @Override
    protected void sebelumTutup() {
        timer.stop();
    }
}
