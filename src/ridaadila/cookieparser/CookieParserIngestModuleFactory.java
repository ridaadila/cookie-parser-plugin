/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ridaadila.cookieparser;

/**
 *
 * @author ridaa
 */
import org.openide.util.lookup.ServiceProvider;
import org.sleuthkit.autopsy.ingest.IngestModuleFactory;
import org.sleuthkit.autopsy.ingest.DataSourceIngestModule;
import org.sleuthkit.autopsy.ingest.FileIngestModule;
import org.sleuthkit.autopsy.ingest.IngestModuleGlobalSettingsPanel;
import org.sleuthkit.autopsy.ingest.IngestModuleIngestJobSettings;
import org.sleuthkit.autopsy.ingest.IngestModuleIngestJobSettingsPanel;
 
@ServiceProvider(service = IngestModuleFactory.class) // Cookie Parser is discarded at runtime 
public class CookieParserIngestModuleFactory implements IngestModuleFactory {
   
   private static final String VERSION_NUMBER = "1.0.0";
    
        // This class method allows the ingest module instances created by this 
        // factory to use the same display name that is provided to the Autopsy
        // ingest framework by the factory.
        static String getModuleName() {
            return "Cookie Parser";
//            return NbBundle.getMessage(CookieParserIngestModuleFactory.class, "CookieParserIngestModuleFactory.moduleName");
        }
  
  @Override
  public String getModuleDisplayName() {
           return getModuleName();
  }
   
  @Override
  public String getModuleDescription() {
      return "Modules that can be parsing all of Cookie Files";
//           return NbBundle.getMessage(CookieParserIngestModuleFactory.class, "CookieParserIngestModuleFactory.moduleDescription");
  }
   
    @Override
    public String getModuleVersionNumber() {
         return VERSION_NUMBER;
    }
  
       @Override
       public boolean hasGlobalSettingsPanel() {
           return false;
       }
   
       @Override
       public IngestModuleGlobalSettingsPanel getGlobalSettingsPanel() {
           throw new UnsupportedOperationException();
       }
   
       @Override
       public IngestModuleIngestJobSettings getDefaultIngestJobSettings() {
           return new CookieParserModuleIngestJobSettings();
       }
   
       @Override
       public boolean hasIngestJobSettingsPanel() {
           return true;
       }
   
       @Override
       public IngestModuleIngestJobSettingsPanel getIngestJobSettingsPanel(IngestModuleIngestJobSettings settings) {
           if (!(settings instanceof CookieParserModuleIngestJobSettings)) {
               throw new IllegalArgumentException("Expected settings argument to be instanceof CookieParserModuleIngestJobSettings");
           }
          return new CookieParserIngestModuleIngestJobSettingsPanel((CookieParserModuleIngestJobSettings) settings);
       }
   
       @Override
       public boolean isDataSourceIngestModuleFactory() {
           return true;
       }
   
       @Override
       public DataSourceIngestModule createDataSourceIngestModule(IngestModuleIngestJobSettings settings) {
           if (!(settings instanceof CookieParserModuleIngestJobSettings)) {
               throw new IllegalArgumentException("Expected settings argument to be instanceof CookieParserModuleIngestJobSettings");
           }
           return new CookieParserDataSourceIngestModule((CookieParserModuleIngestJobSettings) settings);
       }
   
       @Override
       public boolean isFileIngestModuleFactory() {
           return true;
       }
   
       @Override
       public FileIngestModule createFileIngestModule(IngestModuleIngestJobSettings settings) {
           if (!(settings instanceof CookieParserModuleIngestJobSettings)) {
               throw new IllegalArgumentException("Expected settings argument to be instanceof CookieParserModuleIngestJobSettings");
           }
           return new CookieParserFileIngestModule((CookieParserModuleIngestJobSettings) settings);
       }
   }

