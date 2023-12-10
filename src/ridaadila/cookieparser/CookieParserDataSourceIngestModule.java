/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ridaadila.cookieparser;

/**
 *
 * @author ridaa
 */
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.sleuthkit.autopsy.casemodule.Case;
import org.sleuthkit.autopsy.casemodule.services.FileManager;
import org.sleuthkit.autopsy.ingest.DataSourceIngestModuleProgress;
import org.sleuthkit.autopsy.ingest.IngestModule;
import org.sleuthkit.datamodel.AbstractFile;
import org.sleuthkit.datamodel.Content;
import org.sleuthkit.datamodel.TskCoreException;
import org.sleuthkit.autopsy.coreutils.Logger;
import org.sleuthkit.autopsy.ingest.DataSourceIngestModule;
import org.sleuthkit.autopsy.ingest.IngestJobContext;
import org.sleuthkit.autopsy.ingest.IngestMessage;
import org.sleuthkit.autopsy.ingest.IngestServices;
import org.sleuthkit.datamodel.BlackboardArtifact;
import org.sleuthkit.datamodel.BlackboardAttribute;
import org.sleuthkit.datamodel.SleuthkitCase;
import org.sleuthkit.datamodel.TskData;
import org.sleuthkit.datamodel.TskDataException;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Map;
import org.openide.util.Exceptions;
import org.sleuthkit.autopsy.casemodule.NoCurrentCaseException;

 class CookieParserDataSourceIngestModule implements DataSourceIngestModule {
 
   private final boolean skipKnownFiles;
   private IngestJobContext context = null;
   private BufferedImage image;
   
   CookieParserDataSourceIngestModule(CookieParserModuleIngestJobSettings settings) {
        this.skipKnownFiles = settings.skipKnownFiles();
   }
   
   @Override
   public void startUp(IngestJobContext context) throws IngestModuleException {
        this.context = context;
        
   }
   
   @Override
   public ProcessResult process(Content dataSource, DataSourceIngestModuleProgress progressBar) {
    
            progressBar.switchToDeterminate(2);
            
            try {
                
                FileManager fileManager = Case.getCurrentCase().getServices().getFileManager();
                
                List<AbstractFile> allFiles = fileManager.findFiles(dataSource, "%" + "");
               
                long fileCount = 0;
                for (AbstractFile eachFile : allFiles) {
                    
                    String extension = eachFile.getNameExtension();
                    
                    if(eachFile.isFile() && extension.isEmpty())
                    {
                        byte buffer[] = new byte[(int) eachFile.getSize()];
                        int len = eachFile.read(buffer, 0, 4);
                        
                        if(len >= 4)
                        {
                            String bufferString = new String(buffer, 0, len);
                            if (bufferString.contentEquals("cook")) {
                                fileCount++;

                                CookieParser parser = new CookieParser(eachFile.getLocalPath());

                                parser.open_file();
                                List<Map<String, String>> cookies = parser.read_cookie_file();
                                String json_prettifier_cookies = parser.getStringInJsonFormatFromCookies(cookies);
                                parser.close_file();
                                
                                SleuthkitCase skCase = Case.getCurrentCaseThrows().getSleuthkitCase();
                                BlackboardArtifact.Type cookieFilesArtifact = skCase.addBlackboardArtifactType("COOKIE_FILES", "Cookie Files");
                                BlackboardAttribute.Type cookieDataAttribute = skCase.addArtifactAttributeType("COOKIE_DATA", BlackboardAttribute.TSK_BLACKBOARD_ATTRIBUTE_VALUE_TYPE.STRING, "Cookie Data");
                            
                                
                                List<BlackboardAttribute> attributes = new ArrayList<>();
                                attributes.add(new BlackboardAttribute(BlackboardAttribute.ATTRIBUTE_TYPE.TSK_PROG_NAME, "", "Cookie Parser"));
                                attributes.add(new BlackboardAttribute(cookieDataAttribute, eachFile.getLocalPath(), json_prettifier_cookies));
                                BlackboardArtifact art = eachFile.newDataArtifact(cookieFilesArtifact, attributes);
                            }   
                        }
                        
                    }
                    
                    
                }
                progressBar.progress(1);
    
                if (context.dataSourceIngestIsCancelled()) {
                    return IngestModule.ProcessResult.OK;
                }
                
              progressBar.progress(1);
   
              if (context.dataSourceIngestIsCancelled()) {
                   return IngestModule.ProcessResult.OK;
               }
   
               String msgText = String.format("%d cookie files have been found", fileCount);
               IngestMessage message = IngestMessage.createMessage(
                      IngestMessage.MessageType.DATA,
                       CookieParserIngestModuleFactory.getModuleName(),
                       msgText);
              IngestServices.getInstance().postMessage(message);
   
               return IngestModule.ProcessResult.OK;
   
           } catch (TskCoreException ex) {
               IngestServices ingestServices = IngestServices.getInstance();
               Logger logger = ingestServices.getLogger(CookieParserIngestModuleFactory.getModuleName());
               logger.log(Level.SEVERE, "File query failed", ex);
               return IngestModule.ProcessResult.ERROR;
           } catch (NoCurrentCaseException ex) {
           Exceptions.printStackTrace(ex);
       } catch (TskDataException ex) {
           Exceptions.printStackTrace(ex);
       }
       return null;
            
       }
  
   }

