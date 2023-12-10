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
   
public class CookieParserModuleIngestJobSettings implements IngestModuleIngestJobSettings {
    
   private static final long serialVersionUID = 1L;
   private boolean skipKnownFiles = true;
    
   CookieParserModuleIngestJobSettings() {
   }
   
   CookieParserModuleIngestJobSettings(boolean skipKnownFiles) {
            this.skipKnownFiles = skipKnownFiles;
   }
   
   @Override
   public long getVersionNumber() {
            return serialVersionUID;
   }
    
   void setSkipKnownFiles(boolean enabled) {
            skipKnownFiles = enabled;
   }
    
   boolean skipKnownFiles() {
           return skipKnownFiles;
   }
}
