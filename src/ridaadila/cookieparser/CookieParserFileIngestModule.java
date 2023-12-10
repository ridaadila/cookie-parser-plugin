/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ridaadila.cookieparser;

/**
 *
 * @author ridaa
 */
import java.util.HashMap;
import java.util.logging.Level;
import org.sleuthkit.autopsy.coreutils.Logger;
import org.sleuthkit.autopsy.ingest.FileIngestModule;
import org.sleuthkit.autopsy.ingest.IngestJobContext;
import org.sleuthkit.autopsy.ingest.IngestMessage;
import org.sleuthkit.autopsy.ingest.IngestModule;
import org.sleuthkit.autopsy.ingest.IngestModuleReferenceCounter;
import org.sleuthkit.autopsy.ingest.IngestServices;
import org.sleuthkit.datamodel.AbstractFile;
import org.sleuthkit.datamodel.Blackboard;
import org.sleuthkit.datamodel.BlackboardArtifact;
import org.sleuthkit.datamodel.BlackboardAttribute;
import org.sleuthkit.datamodel.TskCoreException;
import org.sleuthkit.datamodel.TskData;
    
class CookieParserFileIngestModule implements FileIngestModule {
    
        private static final HashMap<Long, Long> artifactCountsForIngestJobs = new HashMap<>();
        private static final BlackboardAttribute.ATTRIBUTE_TYPE ATTR_TYPE = BlackboardAttribute.ATTRIBUTE_TYPE.TSK_COUNT;
        private final boolean skipKnownFiles;
        private IngestJobContext context = null;
        private static final IngestModuleReferenceCounter refCounter = new IngestModuleReferenceCounter();
    
        CookieParserFileIngestModule(CookieParserModuleIngestJobSettings settings) {
            this.skipKnownFiles = settings.skipKnownFiles();
        }
    
        @Override
        public void startUp(IngestJobContext context) throws IngestModule.IngestModuleException {
            this.context = context;
            refCounter.incrementAndGet(context.getJobId());
        }
    
        @Override
        public IngestModule.ProcessResult process(AbstractFile file) {
   
            // Skip anything other than actual file system files.
            if ((file.getType() == TskData.TSK_DB_FILES_TYPE_ENUM.UNALLOC_BLOCKS)
                    || (file.getType() == TskData.TSK_DB_FILES_TYPE_ENUM.UNUSED_BLOCKS)
                    || (file.isFile() == false)) {
                return IngestModule.ProcessResult.OK;
            }
    
            // Skip NSRL / known files.
            if (skipKnownFiles && file.getKnown() == TskData.FileKnown.KNOWN) {
                return IngestModule.ProcessResult.OK;
            }
    
           // Do a nonsensical calculation of the number of 0x00 bytes
            // in the first 1024-bytes of the file.  This is for demo
            // purposes only.
            try {
                byte buffer[] = new byte[1024];
                int len = file.read(buffer, 0, 1024);
                int count = 0;
                for (int i = 0; i < len; i++) {
                    if (buffer[i] == 0x00) {
                        count++;
                    }
                }
    
                // Make an attribute using the ID for the attribute attrType that 
               // was previously created.
               BlackboardAttribute attr = new BlackboardAttribute(ATTR_TYPE, CookieParserIngestModuleFactory.getModuleName(), count);
   
              // Add the to the general info artifact for the file. In a
               // real module, you would likely have more complex data types 
               // and be making more specific artifacts.
               BlackboardArtifact art = file.getGenInfoArtifact();
               art.addAttribute(attr);
  
               // This method is thread-safe with per ingest job reference counted
               // management of shared data.
               addToBlackboardPostCount(context.getJobId(), 1L);
   
               /*
                * Post the artifact to the blackboard. Doing so will cause events
                * to be published that will trigger additional analysis, if
                * applicable. For example, the creation of timeline events,
                * indexing of the artifact for keyword search, and analysis by the
                * data artifact ingest modules if the artifact is a data artifact.
                */
               file.getSleuthkitCase().getBlackboard().postArtifact(art, CookieParserIngestModuleFactory.getModuleName(), context.getJobId());
   
               return IngestModule.ProcessResult.OK;
   
           } catch (TskCoreException | Blackboard.BlackboardException ex) {
               IngestServices ingestServices = IngestServices.getInstance();
               Logger logger = ingestServices.getLogger(CookieParserIngestModuleFactory.getModuleName());
               logger.log(Level.SEVERE, "Error processing file (id = " + file.getId() + ")", ex);
               return IngestModule.ProcessResult.ERROR;
           }
       }
   
       @Override
       public void shutDown() {
           // This method is thread-safe with per ingest job reference counted
           // management of shared data.
           reportBlackboardPostCount(context.getJobId());
       }
   
       synchronized static void addToBlackboardPostCount(long ingestJobId, long countToAdd) {
           Long fileCount = artifactCountsForIngestJobs.get(ingestJobId);
   
           // Ensures that this job has an entry
           if (fileCount == null) {
               fileCount = 0L;
               artifactCountsForIngestJobs.put(ingestJobId, fileCount);
           }
   
           fileCount += countToAdd;
           artifactCountsForIngestJobs.put(ingestJobId, fileCount);
       }
   
       synchronized static void reportBlackboardPostCount(long ingestJobId) {
           Long refCount = refCounter.decrementAndGet(ingestJobId);
           if (refCount == 0) {
               Long filesCount = artifactCountsForIngestJobs.remove(ingestJobId);
               String msgText = String.format("Posted %d times to the blackboard", filesCount);
               IngestMessage message = IngestMessage.createMessage(
                      IngestMessage.MessageType.INFO,
                       CookieParserIngestModuleFactory.getModuleName(),
                       msgText);
               IngestServices.getInstance().postMessage(message);
           }
       }
   }

