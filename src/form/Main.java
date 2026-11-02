package form;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Titik awal aplikasi BI-KANTIN. */
public class Main {

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // tetap pakai tampilan bawaan
        }
        SwingUtilities.invokeLater(() -> new FormLogin().setVisible(true));
    }
}
