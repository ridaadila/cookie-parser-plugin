/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ridaadila.cookieparser;

/**
 *
 * @author ridaa
 */
import org.sleuthkit.autopsy.ingest.IngestModuleIngestJobSettings;
import org.sleuthkit.autopsy.ingest.IngestModuleIngestJobSettingsPanel;
    
@SuppressWarnings("PMD.SingularField") // UI widgets cause lots of false positives
public class CookieParserIngestModuleIngestJobSettingsPanel extends IngestModuleIngestJobSettingsPanel {
    
public CookieParserIngestModuleIngestJobSettingsPanel(CookieParserModuleIngestJobSettings settings) {
      initComponents();
      customizeComponents(settings);
 }

//    CookieParserIngestModuleIngestJobSettingsPanel(CookieParserModuleIngestJobSettings CookieParserModuleIngestJobSettings) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
//    }
    
 private void customizeComponents(CookieParserModuleIngestJobSettings settings) {
//         skipKnownFilesCheckBox.setSelected(settings.skipKnownFiles());
 }
    
 @Override
 public IngestModuleIngestJobSettings getSettings() {
   return new CookieParserModuleIngestJobSettings(skipKnownFilesCheckBox.isSelected());
 }
   
 @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {
    
   skipKnownFilesCheckBox = new javax.swing.JCheckBox();
    
//            org.openide.awt.Mnemonics.setLocalizedText(skipKnownFilesCheckBox, org.openide.util.NbBundle.getMessage(CookieParserIngestModuleIngestJobSettingsPanel.class, "CookieParserIngestModuleIngestJobSettingsPanel.skipKnownFilesCheckBox.text")); // NOI18N
    
            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
            this.setLayout(layout);
//            layout.setHorizontalGroup(
//                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                .addGroup(layout.createSequentialGroup()
//                    .addContainerGap()
//                    .addComponent(skipKnownFilesCheckBox)
//                    .addContainerGap(155, Short.MAX_VALUE))
//            );
//            layout.setVerticalGroup(
//                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                .addGroup(layout.createSequentialGroup()
//                    .addContainerGap()
//                    .addComponent(skipKnownFilesCheckBox)
//                    .addContainerGap(270, Short.MAX_VALUE))
//            );
        }// </editor-fold>//GEN-END:initComponents
        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JCheckBox skipKnownFilesCheckBox;
        // End of variables declaration//GEN-END:variables
}
