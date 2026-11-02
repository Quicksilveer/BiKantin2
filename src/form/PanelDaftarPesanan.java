package form;

import dao.PesananDAO;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.DetailPesanan;
import model.Pesanan;
import util.UI;

/**
 * Komponen daftar pesanan + detail item yang dipakai bersama oleh Admin, Kasir, dan Siswa.
 * Tombol aksi ditambahkan oleh pemakai lewat getPanelAksi().
 */
public class PanelDaftarPesanan extends JPanel {

    private final PesananDAO pesananDAO = new PesananDAO();
    private final DefaultTableModel modelPesanan = UI.modelTabel(
            "No. Pesanan", "Waktu", "Siswa", "Total", "Status Pesanan", "Pembayaran", "Kasir", "Catatan");
    private final DefaultTableModel modelDetail = UI.modelTabel("Menu", "Harga", "Jumlah", "Subtotal");
    private final JTable tblPesanan = UI.tabel(modelPesanan);
    private final JLabel lblInfo = new JLabel(" ");
    private final JPanel panelAksi = new JPanel(new FlowLayout(FlowLayout.LEFT));

    private List<Pesanan> data = new ArrayList<>();
    private Integer filterUserId;
    private String filterPembayaran;

    public PanelDaftarPesanan() {
        super(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        tblPesanan.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                muatDetail();
            }
        });

        JPanel bawah = new JPanel(new BorderLayout());
        bawah.add(new JLabel("Detail Pesanan"), BorderLayout.NORTH);
        bawah.add(new JScrollPane(UI.tabel(modelDetail)), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(tblPesanan), bawah);
        split.setResizeWeight(0.6);

        JPanel selatan = new JPanel(new BorderLayout());
        selatan.add(panelAksi, BorderLayout.CENTER);
        selatan.add(lblInfo, BorderLayout.SOUTH);

        add(split, BorderLayout.CENTER);
        add(selatan, BorderLayout.SOUTH);
    }

    /** userId = null -> semua siswa; statusPembayaran = null -> semua status. */
    public void setFilter(Integer userId, String statusPembayaran) {
        this.filterUserId = userId;
        this.filterPembayaran = statusPembayaran;
    }

    /** Memuat ulang data dari database, pilihan baris tetap dipertahankan. */
    public void muat() {
        Pesanan terpilih = getTerpilih();
        int idTerpilih = terpilih == null ? -1 : terpilih.getId();
        try {
            data = pesananDAO.cari(filterUserId, filterPembayaran);
            modelPesanan.setRowCount(0);
            int barisPilih = -1;
            for (int i = 0; i < data.size(); i++) {
                Pesanan p = data.get(i);
                modelPesanan.addRow(new Object[]{
                    "#" + p.getId(), UI.waktu(p.getCreatedAt()), p.getNamaSiswa(), UI.rupiah(p.getTotal()),
                    p.getStatusPesanan(), p.getStatusPembayaran(),
                    p.getNamaKasir() == null ? "-" : p.getNamaKasir(),
                    p.getCatatan() == null ? "" : p.getCatatan()
                });
                if (p.getId() == idTerpilih) {
                    barisPilih = i;
                }
            }
            if (barisPilih >= 0) {
                tblPesanan.setRowSelectionInterval(barisPilih, barisPilih);
            } else {
                modelDetail.setRowCount(0);
            }
            lblInfo.setText("Jumlah pesanan: " + data.size());
        } catch (Exception e) {
            lblInfo.setText("Gagal memuat pesanan: " + e.getMessage());
        }
    }

    private void muatDetail() {
        modelDetail.setRowCount(0);
        Pesanan p = getTerpilih();
        if (p == null) {
            return;
        }
        try {
            for (DetailPesanan d : pesananDAO.detail(p.getId())) {
                modelDetail.addRow(new Object[]{
                    d.getNamaMenu(), UI.rupiah(d.getHarga()), d.getJumlah(), UI.rupiah(d.getSubtotal())
                });
            }
        } catch (Exception e) {
            lblInfo.setText("Gagal memuat detail: " + e.getMessage());
        }
    }

    public Pesanan getTerpilih() {
        int row = tblPesanan.getSelectedRow();
        return row < 0 || row >= data.size() ? null : data.get(row);
    }

    public JPanel getPanelAksi() {
        return panelAksi;
    }
}
