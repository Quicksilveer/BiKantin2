package util;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/** Fungsi bantu tampilan yang dipakai banyak form. */
public class UI {

    /** Format angka ke Rupiah, contoh: 15000 -> "Rp 15.000". */
    public static String rupiah(int angka) {
        return "Rp " + String.format("%,d", angka).replace(',', '.');
    }

    public static String waktu(Timestamp t) {
        return t == null ? "-" : new SimpleDateFormat("dd-MM-yyyy HH:mm").format(t);
    }

    /** Model tabel yang tidak bisa diedit langsung (edit lewat form/tombol). */
    public static DefaultTableModel modelTabel(String... kolom) {
        return new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public static JTable tabel(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setRowHeight(24);
        t.getTableHeader().setReorderingAllowed(false);
        return t;
    }

    public static JLabel judul(String teks) {
        JLabel l = new JLabel(teks);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 18f));
        return l;
    }

    /** Menambah satu baris "Label : komponen" ke panel ber-GridBagLayout. */
    public static void baris(JPanel panel, int row, String label, JComponent komponen) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.gridy = row;
        g.gridx = 0;
        g.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(label), g);
        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        panel.add(komponen, g);
    }

    public static void info(Component parent, String pesan) {
        JOptionPane.showMessageDialog(parent, pesan, "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(Component parent, String pesan) {
        JOptionPane.showMessageDialog(parent, pesan, "Gagal", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean konfirmasi(Component parent, String pesan) {
        return JOptionPane.showConfirmDialog(parent, pesan, "Konfirmasi",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
