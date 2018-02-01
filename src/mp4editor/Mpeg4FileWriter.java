package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import java.io.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import mp4editor.pojo.CodecInfo;
import mp4editor.pojo.MP4Buffer;
import mp4editor.pojo.TrackInfo;
import mp4editor.pojo.enm.BuffTypeEnm;
import mp4editor.pojo.enm.WriterStateEnm;
import mp4editor.util.LogType;
import mp4editor.util.LoggingEvents;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;



public class Mpeg4FileWriter 
{
    
//    private FileInputStream m_FileWriteStream = null;
    private RandomAccessFile m_BinaryWriter = null;
    private FileWriter m_InfoWriter = null;
    private LinkedHashMap<String, Mp4AtomBase> m_dictChildAtoms = null;
    private MpegFileInfo m_MpegInfo = new MpegFileInfo();
    private WriterStateEnm m_State;
    private long m_uiMdataStart = 0;
    private long m_uiMdataEnd = 0;
//    private long m_uiMdataAccumulated = 0;
    private int m_uiVersion = 0;
    private static final long M_UI_MDATA_THRESH_HOLD = (Long.MAX_VALUE & 0xFFFFFFFFL) - (1920 * 1080 * 3);
    private String m_sTrackInfoXML = "";
    private String m_sOutputFile = "";
    private String m_sMediaInfoFile = "";
    private String m_sTrackInfo = "";
    LoggingEvents logging = null;
//    private LogInfoPlus m_evtLogInfoPlusDelegate;

    public Mpeg4FileWriter()
    {
        m_dictChildAtoms = new LinkedHashMap<String, Mp4AtomBase>();
        m_State = WriterStateEnm.IDLE;            
    }

    public void addLoggingListener(LoggingEvents logging){
        this.logging = logging;
    }
    
    public void printLog(LogType logType, String message, Throwable ex){
        if(logging!=null){
            logging.printLog(logType, ex, message);
        }
    }
    
    public void unInit()
    {
        m_dictChildAtoms.clear();
        m_dictChildAtoms = null;
    }
    
    public int getVersion()
    { 
        return m_uiVersion; 
    }
    public void setVersion(int value)
    { 
        m_uiVersion = value; 
    }

    private void cleanUp(String[] sError) {
        sError[0] = "";
        try {
            if (m_BinaryWriter != null) {
                m_BinaryWriter.close();
            }
            m_BinaryWriter = null;
//            if (m_FileWriteStream != null)
//                m_FileWriteStream.close();
//            m_FileWriteStream = null;

            // Closes info writer objects
            if (m_InfoWriter != null) {
                m_InfoWriter.close();
            }
            m_InfoWriter = null;

            m_dictChildAtoms.clear();

        } catch (IOException ex) {
            printLog(LogType.error, "mp4editor.Mpeg4FileWriter | ", ex);
        } catch (RuntimeException ex) {
            printLog(LogType.error, "mp4editor.Mpeg4FileWriter | ", ex);
        }
    }

    public WriterStateEnm getWRITER_STATUS() { 
        return m_State; 
    }

    public double getAccumulatedDurationSecs()
    {
        double dDuration = 0.0;
        try
        {
            double[] referenceToDDuration = { dDuration };
            ((MOOVAtom)(m_dictChildAtoms.get("moov"))).getMovieDuration(referenceToDDuration);
            dDuration = referenceToDDuration[0];
        }
        catch (RuntimeException ex)
        {
            printLog(LogType.error, "mp4editor.Mpeg4FileWriter | ", ex);
        }
        return dDuration;
    }
            
    public boolean getVideoAccumulatedDurationSecs(double[] dDurInSeconds) {
        return ((MOOVAtom)(m_dictChildAtoms.get("moov"))).getVideoTrackDuration(dDurInSeconds);
    }
    
    public boolean getAudioAccumulatedDurationSecs(double[] dDurInSeconds) {
        return ((MOOVAtom)(m_dictChildAtoms.get("moov"))).getAudioTrackDuration(dDurInSeconds);
    }
    
    public boolean startRecording(String sFileName, String[] sError)
    {
        sError[0] = "";
        try
        {
            // Already recording or paused
            if (m_State == WriterStateEnm.RECORDING || m_State == WriterStateEnm.PAUSED)
            {
                m_State = WriterStateEnm.RECORDING;
                return false;
            }

            cleanUp(sError);                  // clean up                
//            m_FileWriteStream = new FileStream(sFileName, FileMode.CREATE, FileAccess.READ_WRITE, FileShare.READ);
            File writableFile = new File(sFileName);
            if(writableFile.exists())
                writableFile.delete();
            m_BinaryWriter = new RandomAccessFile(writableFile,"rw");
                           

//            FileInfo info = new FileInfo(sFileName);
              m_sOutputFile = writableFile.getName();
//            String sMediaInfo = info.Name.Remove(info.Name.indexOf(".mp4"));
            
            String fileInfo = sFileName.substring(0, sFileName.indexOf(".mp4"));
            
//            m_sMediaInfoFile = msString.format("{0}\\{1}.info", info.getDirectoryName(), sMediaInfo);
             m_sMediaInfoFile = fileInfo+".info";
             
            if (Utils.stringNullCheck(m_sTrackInfo))
                m_sTrackInfoXML = "<trackinfo>"+m_sTrackInfo+"</trackinfo>";

            // Create info writer file stream                
            m_InfoWriter = new FileWriter(m_sMediaInfoFile);
//            m_InfoWriter.write("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
//            m_InfoWriter.close();
            // clear the string
            m_sTrackInfo = "";

            // Begin Writing
            beginWriting();             // begin writing
           
            // generate ftyp and mdat atoms at start
            m_dictChildAtoms.get("ftyp").generateAtom();
            m_uiMdataStart = (long)m_BinaryWriter.length();//getBaseStream().getLength();
            m_dictChildAtoms.get("mdat").generateAtom();
            RandomAccessFile referenceToM_BinaryWriter = m_BinaryWriter;
            Utils.writeUint64(referenceToM_BinaryWriter, 0);

            // Begin Accumulating media info
            beginMediaInfo();

            m_State = WriterStateEnm.RECORDING;
        }catch (FileNotFoundException ex) {
            printLog(LogType.error, "mp4editor.Mpeg4FileWriter | ", ex);
        } 
        catch (IOException ex) {
            printLog(LogType.error, "mp4editor.Mpeg4FileWriter | ", ex);
        } 
        catch (RuntimeException ex)
        {
            printLog(LogType.error, "mp4editor.Mpeg4FileWriter | ", ex);
            return true;
        } 
        return false;
    }

    public boolean resumeRecording(String[] sError)
    {
        sError[0] = "";
        try
        {
            java.util.Set keySet = m_dictChildAtoms.keySet();
            Iterator keyItr = keySet.iterator();
            while(keyItr.hasNext()){
                m_dictChildAtoms.get(keyItr.next()).resumeWriting();
            }
//            for (KeyValuePair<String, Mp4AtomBase> kp : (Iterable<KeyValuePair<String,Mp4AtomBase>>) m_dictChildAtoms)
//                kp.Value.resumeWriting();                
            m_State = WriterStateEnm.RECORDING;
        }
        catch (RuntimeException ex)
        {
            printLog(LogType.error, "mp4editor.Mpeg4FileWriter | ", ex);
            sError[0] = ex.getMessage();
            return true;
        }
        return false;
    }

    public boolean pauseRecording(String[] sError)
    { 
        sError[0] = "";
        try
        {
            if (m_State != WriterStateEnm.PAUSED)
            {
                java.util.Set keySet = m_dictChildAtoms.keySet();
                Iterator keyItr = keySet.iterator();
                while(keyItr.hasNext()){
                    m_dictChildAtoms.get(keyItr.next()).pauseWriting();
                }
//                for (KeyValuePair<String, Mp4AtomBase> kp : (Iterable<KeyValuePair<String,Mp4AtomBase>>) m_dictChildAtoms)
//                    kp.Value.pauseWriting();
            }
            m_State = WriterStateEnm.PAUSED;
        }
        catch (RuntimeException ex)
        {
            printLog(LogType.error, "mp4editor.Mpeg4FileWriter | ", ex);
            sError[0] = ex.getMessage();
            return false;
        }
        return true; 
    }

    public boolean stopRecording(String[] sError) throws IOException,ParserConfigurationException,SAXException
    {
        sError[0] = "";
        String xmlString = null;
        try
        {
            if (m_State != WriterStateEnm.RECORDING && m_State != WriterStateEnm.PAUSED)
            {
                //log.info("StopRecording skipping because recording state is " + m_State.toString());
                printLog(LogType.information, "mp4editor.Mpeg4FileWriter | StopRecording skipping because recording state is "+ m_State.toString(), null);
                return false;
            }

            m_State = WriterStateEnm.STOPPING;

            // Locking is required to cleanly close the file
            // During stop , it may take some time for large files to insert info in files
            // and during that period no more data should be inserted into the file
            synchronized (m_InfoWriter)
            {
            }
            m_State = WriterStateEnm.STOPPING;

            // Jatinder Always write version 1 files
            //if (m_BinaryWriter.length() > (Long.MAX_VALUE & 0xFFFFFFFFL))
                setVersion(1);

            // End accumulating media info
            endMediaInfo();

            // Close the Info File if already Opened
            if (m_InfoWriter != null)
                m_InfoWriter.close();
            m_InfoWriter = null;
            
            File infoFile = new File(m_sMediaInfoFile);
            if (infoFile.exists())            
                infoFile.delete();
                       
            
//            XmlDocument doc = new XmlDocument();
//            FileWriter info = new FileWriter(m_sMediaInfoFile);
            /*File infoFile = new File(m_sMediaInfoFile);
            if (!infoFile.exists())
            {
                // Gop Info header file not found
                //log.error("Gop Info header file not found.");

                // cleanup 
                cleanUp(sError);

                m_State = WriterStateEnm.IDLE;
                return false;
            }
            
            //CC: Changes for case no 11486:
            //We are getting Out-Of-Memory error when processing large Media Files.
            StringBuffer fileData = new StringBuffer();
            BufferedReader reader = new BufferedReader(
                    new FileReader(infoFile));
            char[] buf = new char[1024*1024];
            int numRead = 0;
            boolean bIgnoreFirst = true;
            String previousData = null;
            while ((numRead = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                if(previousData!=null){
                    readData = previousData.concat(readData);
                }
                String[] gopInfoArr = readData.split("<gopinfo>");
                for (int i = 0; i < gopInfoArr.length; i++) {
                    if(bIgnoreFirst)
                    {
                        bIgnoreFirst = false;
                        continue;
                    }
                    String gopInfoString = gopInfoArr[i];
                    if (i == gopInfoArr.length - 1 && !gopInfoString.contains("</root>")) {
                        previousData = gopInfoString;
                        continue;
//                        gopInfoString = gopInfoString.replaceAll("</root>", "");
                    }
                    if (i == gopInfoArr.length - 1 && gopInfoString.contains("</root>")) {
                        gopInfoString = gopInfoString.replaceAll("</root>", "");
                    }
                    gopInfoString = "<gopinfo>".concat(gopInfoString);
                    xmlString = gopInfoString;
                    InputStream is = new ByteArrayInputStream(gopInfoString.getBytes());
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document doc = db.parse(is);
                    ((MOOVAtom) m_dictChildAtoms.get("moov")).setGopInfo(doc.getElementsByTagName(XMLDefines.ELEMENT_GOPINFO));
                    System.gc();
                }
//                fileData.append(readData);
            }
            reader.close();
            
//            String fileDataString = fileData.toString();
            
            
            
            // Loading the Info XML  
//            doc.load(m_sMediaInfoFile);
            
            // recover from file from disk
            

*/
            
            // End Writing
            endWriting();

            // Generate Atoms
            generate();

            // delete the info ile
            //infoFile.delete();
        }
        catch (RuntimeException ex)
        {
            printLog(LogType.information, "XML IS ----  | "+xmlString,null);
            printLog(LogType.error, "mp4editor.Mpeg4FileWriter | ", ex);
            sError[0] = ex.getMessage();
        }catch(Throwable th){
            printLog(LogType.information, "XML IS ----  | "+xmlString,null);
            printLog(LogType.error, "mp4editor.Mpeg4FileWriter | ", th);
            sError[0] = th.getMessage();
        } 
        finally {
            m_State = WriterStateEnm.IDLE;
            cleanUp(sError);
        }
        return false;
    }

    public boolean addBuffer(BuffTypeEnm type, byte[] buf,int iDataLen, double sampleDelta, String[] sError) throws Exception
    {
        sError[0] = "";
        boolean bResult = false;
        if (m_State == WriterStateEnm.STOPPING)
        {
            sError[0] = "Can not add buffer to file , Writter is stopped.";
            return true;
        }
                   

        try
        {
            // Locking is required to cleanly close the file
            // During stop , it may take some time for large files to insert info in files
            // and during that period no more data should be inserted into the file
            synchronized (this)
            {
                if (m_State == WriterStateEnm.STOPPING)
                {
                    sError[0] = "Can not add buffer to file , Writer is stopped.";
                    return true;
                }
                MP4Buffer buffer = new MP4Buffer();
                buffer.setType(type);
                buffer.setData(buf);
                buffer.setiDataLength(iDataLen);
                buffer.setSampleDelta(sampleDelta);
                ((MOOVAtom)m_dictChildAtoms.get("moov")).AddMP4Buffer(buffer);
                bResult = true;

//                m_uiMdataAccumulated += (((long)iDataLen) & 0xFFFFFFFFL);
//                if (m_uiMdataAccumulated >= (M_UI_MDATA_THRESH_HOLD & 0xFFFFFFFFL))
//                {
//                    m_uiMdataEnd = (long)m_BinaryWriter.length();                            
//                    long uiMdatSize = m_uiMdataEnd - m_uiMdataStart;
//                    m_BinaryWriter.seek((long)m_uiMdataStart);
//
//                    RandomAccessFile referenceToM_BinaryWriter = m_BinaryWriter;
//                    Utils.writeUint32(referenceToM_BinaryWriter, (long)uiMdatSize);
//                    m_BinaryWriter.seek(0);
//
//                    // reset values
//                    m_uiMdataStart = (long)m_BinaryWriter.length();
//                    m_uiMdataEnd = 0;
//                    m_uiMdataAccumulated = 0;
//
//                    // generate another mdat element
//                    Utils.writeUint32(referenceToM_BinaryWriter, 0);
//                    Utils.writeChars(referenceToM_BinaryWriter, "mdat".getBytes());
//                    Utils.writeUint64(referenceToM_BinaryWriter, 0);
//                }
            }
        } catch (IOException ex) {
            printLog(LogType.error, "mp4editor.Mpeg4FileWriter | ", ex);
            sError[0] = ex.getMessage();
            bResult = false;
            throw ex;
        }
        catch (RuntimeException ex)
        {
            printLog(LogType.error, "mp4editor.Mpeg4FileWriter | ", ex);
            sError[0] = ex.getMessage();
            bResult = false;
            throw ex;
//            return false;
        }catch(Exception ex){
            printLog(LogType.error, "mp4editor.Mpeg4FileWriter | ", ex);
            sError[0] = ex.getMessage();
            bResult = false;
            throw ex;
//            return false;
        }
        return bResult;
    }
    
    public FrameTypeEnm getFrameType(BuffTypeEnm type, byte[] buf,int iDataLen, double sampleDelta,double samplePresentationDelta , String[] sError) throws Exception{
        sError[0] = "";
        boolean bResult = false;
        FrameTypeEnm frameType = null;
        try
        {
            // Locking is required to cleanly close the file
            // During stop , it may take some time for large files to insert info in files
            // and during that period no more data should be inserted into the file
            synchronized (this)
            {
                MP4Buffer buffer = new MP4Buffer();
                buffer.setType(type);
                buffer.setData(buf);
                buffer.setiDataLength(iDataLen);
                buffer.setSampleDelta(sampleDelta);
                buffer.setbHasCompTime(true);
                buffer.setfCompTimeTimeSecs((float)samplePresentationDelta);
                frameType = ((MOOVAtom)m_dictChildAtoms.get("moov")).getFrameType(buffer);
                return frameType;
            }
        } catch (IOException ex) {
            printLog(LogType.error, "mp4editor.Mpeg4FileWriter | ", ex);
            sError[0] = ex.getMessage();
            frameType = null;
            throw ex;
        }
        catch (RuntimeException ex)
        {
            printLog(LogType.error, "mp4editor.Mpeg4FileWriter | ", ex);
            sError[0] = ex.getMessage();
            frameType = null;
            throw ex;
//            return false;
        }catch(Exception ex){
            printLog(LogType.error, "mp4editor.Mpeg4FileWriter | ", ex);
            sError[0] = ex.getMessage();
            frameType = null;
            throw ex;
//            return false;
        }
    }
            
            
    public boolean addBuffer(BuffTypeEnm type, byte[] buf,int iDataLen, double sampleDelta,double samplePresentationDelta , String[] sError) throws Exception
    {
        sError[0] = "";
        boolean bResult = false;
        if (m_State == WriterStateEnm.STOPPING)
        {
            sError[0] = "Can not add buffer to file , Writter is stopped.";
            return true;
        }
                   

        try
        {
            // Locking is required to cleanly close the file
            // During stop , it may take some time for large files to insert info in files
            // and during that period no more data should be inserted into the file
            synchronized (this)
            {
                if (m_State == WriterStateEnm.STOPPING)
                {
                    sError[0] = "Can not add buffer to file , Writer is stopped.";
                    return true;
                }
                MP4Buffer buffer = new MP4Buffer();
                buffer.setType(type);
                buffer.setData(buf);
                buffer.setiDataLength(iDataLen);
                buffer.setSampleDelta(sampleDelta);
                buffer.setbHasCompTime(true);
                buffer.setfCompTimeTimeSecs((float)samplePresentationDelta);
                ((MOOVAtom)m_dictChildAtoms.get("moov")).AddMP4Buffer(buffer);
                bResult = true;
            }
        } catch (IOException ex) {
            printLog(LogType.error, "mp4editor.Mpeg4FileWriter | ", ex);
            sError[0] = ex.getMessage();
            bResult = false;
            throw ex;
        }
        catch (RuntimeException ex)
        {
            printLog(LogType.error, "mp4editor.Mpeg4FileWriter | ", ex);
            sError[0] = ex.getMessage();
            bResult = false;
            throw ex;
//            return false;
        }catch(Exception ex){
            printLog(LogType.error, "mp4editor.Mpeg4FileWriter | ", ex);
            sError[0] = ex.getMessage();
            bResult = false;
            throw ex;
//            return false;
        }
        return bResult;
    }

    public boolean setTrackInfo(String sTrackInfo, String[] sError)
    {
        sError[0] = "";
        try
        {
            m_sTrackInfo = m_sTrackInfo+sTrackInfo;                
        }
        catch (RuntimeException ex)
        {
            printLog(LogType.error, "mp4editor.Mpeg4FileWriter | ", ex);
            sError[0] = ex.getMessage();
            return true;
        }
        return false;
    }

public boolean setMpegFileInfo(String sXMLInfo, String[] sError)
    {
        sError[0] = "";
        try
        {
            m_sTrackInfoXML = sXMLInfo;               
        }
        catch (RuntimeException ex)
        {
            printLog(LogType.error, "mp4editor.Mpeg4FileWriter | ", ex);
            sError[0] = ex.getMessage();
            return true;
        }             
        return false;
    }

    public boolean setMpegFileInfo(MpegFileInfo info)
    {
       info.CloneTo(m_MpegInfo);
       m_MpegInfo.setM_uiTimeScale((long)(info.getM_fFrameRate() * 1000));
       return false;
    }

    private boolean beginWriting()
    {
        // Add movie headers
        addHeaders();

        if (!Utils.stringNullCheck(m_sTrackInfoXML))
        {
            // Add tracks
            if (m_MpegInfo.isM_bHasVideo())
                addVideoTrack(m_MpegInfo.getM_VideoInfo().getM_trakInfo().Clone(), m_MpegInfo.getM_VideoInfo().getM_codec().Clone());

            if (m_MpegInfo.isM_bHasAudio())
                addAudioTrack(m_MpegInfo.getM_AudioInfo().getM_trakInfo().Clone(), m_MpegInfo.getM_AudioInfo().getM_codec().Clone());
        }
        else
        {
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                //parse using builder to get DOM representation of the XML file
                InputSource is = new InputSource(new StringReader(m_sTrackInfoXML));
                Document dom = db.parse(is);
                ((MOOVAtom)m_dictChildAtoms.get("moov")).setM_Mp4Infodoc(dom);
                NodeList sourceMovieList = dom.getDocumentElement().getElementsByTagName("track");//ChildNodes();//getDocumentElement().getChildNodes();//ElementsByTagName(IsmConstants.SOURCE_MOVIES);
                for(int i=0; i<sourceMovieList.getLength();i++){
                    Node track = sourceMovieList.item(i);//.getChildNodes();
                    addTrack((Element)track);
//                    for(int j=0;j<trackList.getLength();j++){
//                        Node track = trackList.item(i);
//                        if(track!=null && track.getNodeType()== Node.ELEMENT_NODE)
//                            
//                }
                }
//                NodeList listTracks = objMP4Doc.getDocumentElement().ChildNodes;
                
//                for (Element track : (Iterable<Element>) listTracks)
//                    addTrack(track.OuterXml);
            } catch (SAXException ex) {
                printLog(LogType.error, "mp4editor.Mpeg4FileWriter | ", ex);
            } catch (IOException ex) {
                printLog(LogType.error, "mp4editor.Mpeg4FileWriter | ", ex);
            } catch (ParserConfigurationException ex) {
                printLog(LogType.error, "mp4editor.Mpeg4FileWriter | ", ex);
            }
        }
        
        // Set media info file and target file
        ((MOOVAtom)m_dictChildAtoms.get("moov")).setMediaInfoFile(m_sMediaInfoFile);
        ((MOOVAtom)m_dictChildAtoms.get("moov")).setTargetFile(m_sOutputFile);
        ((MOOVAtom)m_dictChildAtoms.get("moov")).setInfoWriter(m_InfoWriter);
        
        java.util.Set m_dictChildAtomsKeySet = m_dictChildAtoms.keySet();
        Iterator keyItr = m_dictChildAtomsKeySet.iterator();
        while(keyItr.hasNext()){
            m_dictChildAtoms.get(keyItr.next()).beginWriting();
        }
//        for (KeyValuePair<String, Mp4AtomBase> kp : (Iterable<KeyValuePair<String,Mp4AtomBase>>) m_dictChildAtoms)
//            kp.Value.beginWriting();

        return false;
    }

    private boolean endWriting()
    {
        java.util.Set keySet = m_dictChildAtoms.keySet();
        Iterator keyItr = keySet.iterator();
        while(keyItr.hasNext()){
            Object ibjKey = keyItr.next();
            m_dictChildAtoms.get(ibjKey).setVersion(getVersion());
            m_dictChildAtoms.get(ibjKey).endWriting();
        }
//        for (KeyValuePair<String, Mp4AtomBase> kp : (Iterable<KeyValuePair<String,Mp4AtomBase>>) m_dictChildAtoms)
//        {
//            kp.Value.setVersion(getVersion());
//            kp.Value.endWriting();
//        }
        return true;
    }

    private boolean beginMediaInfo()
    {
        java.util.Set keySet = m_dictChildAtoms.keySet();
        Iterator keyItr = keySet.iterator();
        while(keyItr.hasNext()){
            m_dictChildAtoms.get(keyItr.next()).beginMediaInfo();
        }
        return false;
    }

    private boolean endMediaInfo()
    {
        java.util.Set keySet = m_dictChildAtoms.keySet();
        Iterator keyItr = keySet.iterator();
        while(keyItr.hasNext()){
            m_dictChildAtoms.get(keyItr.next()).endMediaInfo();
        }
//        for (KeyValuePair<String, Mp4AtomBase> kp : (Iterable<KeyValuePair<String,Mp4AtomBase>>) m_dictChildAtoms)
//        {
//            kp.Value.endMediaInfo();
//        }
        return false;
    }  

    private boolean addHeaders()
    {
        // Required atoms
        FTYPEAtom ftype = new FTYPEAtom();
                  
        RandomAccessFile referenceToM_BinaryWriter = m_BinaryWriter;
        ftype.setWriter(referenceToM_BinaryWriter);
        m_dictChildAtoms.put("ftyp", ftype);
        

        MOOVAtom moov = new MOOVAtom();
        moov.addLoggingListener(logging);
        moov.setWriter(referenceToM_BinaryWriter);
        MpegFileInfo[] referenceToM_MpegInfo = { m_MpegInfo };
        moov.setMpegFileInfo(referenceToM_MpegInfo);
        referenceToM_MpegInfo[0].CloneTo(m_MpegInfo);            
        m_dictChildAtoms.put("moov", moov);
        

        MDATAtom mdat = new MDATAtom();

        mdat.setWriter(referenceToM_BinaryWriter);
        m_dictChildAtoms.put("mdat", mdat);
       
        return false;
    }

//    private void logInfo(String sError, /*LogType*/int type)
//    {
//        if (m_evtLogInfo != null)
//            m_evtLogInfo.invoke(sError, type);           
//    }
//    private void logInfoPlus(String sError, /*LogType*/int type, int nVerbosity)
//    {
//        if (m_evtLogInfoPlus != null)
//            m_evtLogInfoPlus.invoke(sError, type, nVerbosity);
//    }

    private boolean addTrack(Element sTrackInfoXML) throws IOException
    {
        return ((MOOVAtom)m_dictChildAtoms.get("moov")).addTrack(sTrackInfoXML);
    }

    private boolean addVideoTrack(TrackInfo tkInfo,  CodecInfo codecInfo)
    {
        return ((MOOVAtom)m_dictChildAtoms.get("moov")).addTrack("TRAK_VIDEO", tkInfo.Clone(), codecInfo.Clone());  
    }

    private boolean addAudioTrack(TrackInfo tkInfo, CodecInfo codecInfo)
    {
        return ((MOOVAtom)m_dictChildAtoms.get("moov")).addTrack("TRAK_AUDIO", tkInfo.Clone(), codecInfo.Clone());
    }

    private boolean generate() throws IOException
    {
        m_uiMdataEnd = m_BinaryWriter.length();
        long uiMdatSize = m_uiMdataEnd - m_uiMdataStart;

        // Seek to start of MDAT
        m_BinaryWriter.seek((long)m_uiMdataStart);
       
        // update length
        RandomAccessFile referenceToM_BinaryWriter = m_BinaryWriter ;
        
        // Jatinder , Always write version 1 MDAT atom
        // Rahul , verify the changes
        // Version1 Atom , Read length from next 8 bytes
        Utils.writeUint32(referenceToM_BinaryWriter, 1);       
        m_BinaryWriter.seek((long)m_uiMdataStart + 8);//, SeekOrigin.END);
        Utils.writeUint64(referenceToM_BinaryWriter,uiMdatSize);
            
        // Seek end
        m_BinaryWriter.seek(m_BinaryWriter.length());//, SeekOrigin.END);

        // generate moov atom at end
        m_dictChildAtoms.get("moov").generateAtom();

        return false;
    }      
}

