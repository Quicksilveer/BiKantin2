package form;

import dao.PesananDAO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.Timer;
import model.Pesanan;
import model.User;
import util.UI;

/** Form Admin: kelola menu, akun, dan pesanan masuk. */
public class FormAdmin extends FormDashboard {

    private final PanelDaftarPesanan panelPesanan = new PanelDaftarPesanan();
    private final JComboBox<String> cbStatus = new JComboBox<>(Pesanan.STATUS_PESANAN);
    private final PesananDAO pesananDAO = new PesananDAO();
    private final Timer timer;

    public FormAdmin(User user) {
        super("Admin", user);

        JButton btnUbahStatus = new JButton("Ubah Status");
        JButton btnRefresh = new JButton("Refresh");
        btnUbahStatus.addActionListener(e -> ubahStatus());
        btnRefresh.addActionListener(e -> panelPesanan.muat());
        panelPesanan.getPanelAksi().add(new JLabel("Status:"));
        panelPesanan.getPanelAksi().add(cbStatus);
        panelPesanan.getPanelAksi().add(btnUbahStatus);
        panelPesanan.getPanelAksi().add(btnRefresh);
        panelPesanan.setFilter(null, null);
        panelPesanan.muat();

        JTabbedPane tab = new JTabbedPane();
        tab.addTab("Menu", new PanelMenu());
        tab.addTab("Akun", new PanelAkun(user));
        tab.addTab("Pesanan", panelPesanan);
        setKonten(tab);

        // refresh pesanan tiap 5 detik
        timer = new Timer(5000, e -> panelPesanan.muat());
        timer.start();
    }

    private void ubahStatus() {
        Pesanan p = panelPesanan.getTerpilih();
        if (p == null) {
            UI.error(this, "Pilih pesanan dulu.");
            return;
        }
        String statusBaru = (String) cbStatus.getSelectedItem();
        if ("Selesai".equals(statusBaru) && !p.isLunas()) {
            UI.error(this, "Pesanan belum lunas.");
            return;
        }
        try {
            pesananDAO.ubahStatusPesanan(p.getId(), statusBaru);
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
