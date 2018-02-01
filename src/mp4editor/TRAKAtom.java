package mp4editor;

import mp4editor.pojo.DeltaInfo;
import mp4editor.pojo.CompTimeInfo;
import mp4editor.pojo.TrackInfo;
import mp4editor.pojo.CodecInfo;
import mp4editor.pojo.ChunkInfo;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import mp4editor.pojo.enm.IFrameSearchEnm;
import mp4editor.util.LogType;
import mp4editor.util.LoggingEvents;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class TRAKAtom extends Mp4AtomBase {

    private LinkedHashMap<Long, ChunkInfo> m_dictChunkInfo;            // 
    private LinkedHashMap<Long, ChunkInfo> m_dictSampleInfo;           //
    private LinkedHashMap<Long, Long> m_dictChunkOffset;          // Chunk offset dictionary
    private ArrayList<Long> m_queueRandomAccessSampNos;  // 
    private LinkedHashMap<Long, Integer> m_dictSampleSize;           //
    private LinkedHashMap<Long, DeltaInfo> m_dictDeltaInfo;            // 
    private LinkedHashMap<Long, CompTimeInfo> m_dictCttsInfo;            // 
//    private LinkedHashMap<Long, CompTimeInfo> m_dictCompTimeInfo;
    private CodecInfo m_CodecInfo = new CodecInfo();
    private TrackInfo m_TrackInfo = new TrackInfo();
    private String m_enmTrkType;
//   
    private long m_uiSampleCount = 0;
    private long m_uiSamplesPerChunk = 0;
    private long m_uiChunkNo = 0;
    private long m_uiPrevSamplesPerChunk = 0;
    private long m_uiLastGopSamplesPerChunk = 0;
    private long m_uiPreviousCompTime = 0;
    private double m_uiPreviousSampleDelta = 0;
    private long m_uiAccumulatedDuration = 0;
//    private String m_sTrackInfoXML = "";               
    private Element m_sTrackInfoXML;
    boolean isPartialParsing = false;
    LoggingEvents logging = null;
    
    // 
    CompTimeInfo m_presentationInfo = new CompTimeInfo();
    DeltaInfo m_DeltaInfo = new DeltaInfo();
    
    public class SampleSizeInfo {

        public int m_iBuffSize = 0;
        public int m_iEntryCount = 0;
        public byte[] m_aarSampleSize = null;
        
        public void Init(int iCount)
        {
            m_iEntryCount = iCount;
            m_iBuffSize = m_iEntryCount * 4;
            m_aarSampleSize = new byte[m_iBuffSize];        
        }
        
        public void DeInit()
        {
            m_aarSampleSize = null;
            m_iBuffSize = 0;
            m_iEntryCount = 0;
        }
    }
    public SampleSizeInfo m_SampleSizeInfo = new SampleSizeInfo();

    public class ChunkOffsetInfo {

        public int m_iBuffSize = 0;
        public int m_iVersion = 0;
        public int m_iEntryCount = 0;
        public byte[] m_aarChunkOffset = null;
        
        public void Init(int iVersion , int iCount)
        {
            m_iEntryCount = iCount;
            m_iVersion = iVersion;
            m_iBuffSize = m_iEntryCount * ((iVersion == 0) ? 4 : 8);
            m_aarChunkOffset = new byte[m_iBuffSize];        
        }
        
        public void DeInit()
        {
            m_aarChunkOffset = null;
            m_iBuffSize = 0;
            m_iEntryCount = 0;
            m_iVersion = 0;
        }
    }
    public ChunkOffsetInfo m_ChunkOffsetInfo = new ChunkOffsetInfo();

    public class ChnkInfo {

        public int m_iBuffSize = 0;
        public int m_iEntryCount = 0;
        byte[] m_aarChunkInfo = null;
        
        public void Init(int iCount)
        {
            m_iEntryCount = iCount;
            m_iBuffSize = m_iEntryCount * 12;
            m_aarChunkInfo = new byte[m_iBuffSize];        
        }
        
        public void DeInit()
        {
            m_aarChunkInfo = null;
            m_iBuffSize = 0;
            m_iEntryCount = 0;
        }
    }
    public ChnkInfo m_ChunkInfo = new ChnkInfo();

    public class MSampleInfo {

        public int m_iSize;
        public long m_lOffset;
        public int m_DecodeDelta;
        public int m_PresentationDelta;
    }
    
    
    public class TSampleInfo{
        public long m_iSampleCount = 0;
        public LinkedHashMap<Integer, MSampleInfo> m_dictSampleInfo = new LinkedHashMap<Integer, MSampleInfo>();
    }
    public TSampleInfo m_SampleInfo = new TSampleInfo();
    
    public void addLoggingListener(LoggingEvents logging){
        this.logging = logging;
    }
    
    public void printLog(LogType logType, String message, Throwable th){
        if(logging!=null){
            logging.printLog(logType, th, message);
        }
    }
    
    public TRAKAtom() {
        setAtomID("trak");
        m_dictChunkInfo = new LinkedHashMap<Long, ChunkInfo>();
        m_dictSampleInfo = new LinkedHashMap<Long, ChunkInfo>();
        m_dictChunkOffset = new LinkedHashMap<Long, Long>();
        m_queueRandomAccessSampNos = new ArrayList<Long>();
        m_dictSampleSize = new LinkedHashMap<Long, Integer>();
        m_dictDeltaInfo = new LinkedHashMap<Long, DeltaInfo>();
        m_dictCttsInfo = new LinkedHashMap<Long, CompTimeInfo>();        
    }

    public void unInit() {
//        if(m_dictChunkInfo != null){
//            Set<Long> keySet = m_dictChunkInfo.keySet();
//            Iterator<Long> keyItr = keySet.iterator();        
//            while(keyItr.hasNext()){
//                long key = keyItr.next();
//                ChunkInfo atomObj = m_dictChunkInfo.get(key);
//                atomObj.unInit();
//            }
//        }
//        if(m_dictSampleInfo != null){
//            Set<Long> keySet = m_dictSampleInfo.keySet();
//            Iterator<Long> keyItr = keySet.iterator();        
//            while(keyItr.hasNext()){
//                long key = keyItr.next();
//                ChunkInfo atomObj = m_dictSampleInfo.get(key);
//                atomObj.unInit();
//            }
//        }
//        
//        if(m_dictDeltaInfo != null){
//            Set<Long> keySet = m_dictDeltaInfo.keySet();
//            Iterator<Long> keyItr = keySet.iterator();        
//            while(keyItr.hasNext()){
//                long key = keyItr.next();
//                DeltaInfo atomObj = m_dictDeltaInfo.get(key);
//                atomObj.unInit();
//            }
//        }
//        
//        if(m_dictCttsInfo != null){
//            Set<Long> keySet = m_dictCttsInfo.keySet();
//            Iterator<Long> keyItr = keySet.iterator();        
//            while(keyItr.hasNext()){
//                long key = keyItr.next();
//                CompTimeInfo atomObj = m_dictCttsInfo.get(key);
//                atomObj.unInit();
//            }
//        }
        m_dictChunkInfo.clear();
        m_dictSampleInfo.clear();
        m_dictChunkOffset.clear();
        m_queueRandomAccessSampNos.clear();
        m_dictSampleSize.clear();
        m_dictDeltaInfo.clear();
        m_dictCttsInfo.clear();

        m_dictChunkInfo = null;
        m_dictSampleInfo = null;
        m_dictChunkOffset = null;
        m_queueRandomAccessSampNos = null;
        m_dictSampleSize = null;
        m_dictDeltaInfo = null;
        m_dictCttsInfo = null;
    }

//
    CodecInfo getCODEC_INFO() {
        return m_CodecInfo;
    }

    void setCODEC_INFO(CodecInfo value) {
        value.CloneTo(m_CodecInfo);
    }

    public LinkedHashMap<Long, DeltaInfo> getM_dictDeltaInfo() {
        return m_dictDeltaInfo;
    }

    public ArrayList<Long> getM_queueRandomAccessSampNos() {
        return m_queueRandomAccessSampNos;
    }

    public void setM_dictDeltaInfo(LinkedHashMap<Long, DeltaInfo> m_dictDeltaInfo) {
        this.m_dictDeltaInfo = m_dictDeltaInfo;
    }

    public void setM_queueRandomAccessSampNos(ArrayList<Long> m_queueRandomAccessSampNos) {
        this.m_queueRandomAccessSampNos = m_queueRandomAccessSampNos;
    }

    public LinkedHashMap<Long, Long> getM_dictChunkOffset() {
        return m_dictChunkOffset;
    }

    public void setM_dictChunkOffset(LinkedHashMap<Long, Long> m_dictChunkOffset) {
        this.m_dictChunkOffset = m_dictChunkOffset;
    }

    public LinkedHashMap<Long, ChunkInfo> getM_dictChunkInfo() {
        return m_dictChunkInfo;
    }

    public void setM_dictChunkInfo(LinkedHashMap<Long, ChunkInfo> m_dictChunkInfo) {
        this.m_dictChunkInfo = m_dictChunkInfo;
    }

    public LinkedHashMap<Long, Integer> getM_dictSampleSize() {
        return m_dictSampleSize;
    }

    public void setM_dictSampleSize(LinkedHashMap<Long, Integer> m_dictSampleSize) {
        this.m_dictSampleSize = m_dictSampleSize;
    }

    public LinkedHashMap<Long, ChunkInfo> getM_dictSampleInfo() {
        return m_dictSampleInfo;
    }

    public void setM_dictSampleInfo(LinkedHashMap<Long, ChunkInfo> m_dictSampleInfo) {
        this.m_dictSampleInfo = m_dictSampleInfo;
    }

    public LinkedHashMap<Long, CompTimeInfo> getM_dictCttsInfo() {
        return m_dictCttsInfo;
    }

    public void setM_dictCttsInfo(LinkedHashMap<Long, CompTimeInfo> m_dictCttsInfo) {
        this.m_dictCttsInfo = m_dictCttsInfo;
    }

//
    TrackInfo getTRACK_INFO() {
        return m_TrackInfo;
    }

    void setTRACK_INFO(TrackInfo value) {
        value.CloneTo(m_TrackInfo);
    }
//

    Element getTRACK_INFO_XML() {
        return m_sTrackInfoXML;
    }

    void setTRACK_INFO_XML(Element value) {
        this.m_sTrackInfoXML = value;
    }
//  
//    /*uint*/long getSampleCount() {return m_uiSampleCount ;}
//    void setSampleCount(/*uint*/long value) { m_uiSampleCount = value; }
//

    String getTRACK_TYPE() {
        return m_enmTrkType;
    }

    void setTRACK_TYPE(String value) {
        m_enmTrkType = value;
    }

    public long getAtomSize() {
        super.setAtomSize(calcChildsAtomSize() + Utils.HEADER_LENGTH);
        return super.getAtomSize();
    }

    public void setAtomSize(long value) {
        super.setAtomSize(value);
    }

    public int getVersion() {
        return super.getVersion();
    }

    public void setVersion(int value) {
        Set keySet = m_dictChildAtoms.keySet();
        Iterator keyItr = keySet.iterator();
        while (keyItr.hasNext()) {
            Object keyObj = keyItr.next();
            m_dictChildAtoms.get(keyObj).setVersion(value);
        }
//        for (KeyValuePair<String, Mp4AtomBase> kv : (Iterable<KeyValuePair<String,Mp4AtomBase>>) m_dictChildAtoms)
//            kv.Value.setVersion(value);
        super.setVersion(value);
    }

    TRAKAtom getTrack(/*uint*/long uiTrackID) {
        TRAKAtom track = null;
        track = ((MOOVAtom) m_Parent).m_dictTracks.get(uiTrackID);
        return track;
    }

    float getSampleDurationInSecs() {
        float fSampleDuration = 0.0f;
        if ((m_TrackInfo.getM_uiTimeScale() & 0xFFFFFFFFL) > 0) {
            fSampleDuration = (float) (m_TrackInfo.getM_uiSampleDuration() & 0xFFFFFFFFL) / (m_TrackInfo.getM_uiTimeScale() & 0xFFFFFFFFL);
        }

        return fSampleDuration;
    }

    boolean readDataFromFile(long uiFileOffset, long uiSampleSize, byte[] arrData) throws IOException {
        synchronized (m_BinReader) {
            arrData = new byte[(int) uiSampleSize];
            m_BinReader.seek((long) uiFileOffset);
            m_BinReader.read(arrData, 0, (int) (uiSampleSize & 0xFFFFFFFFL));
        }
        return false;
    }

    // delta info contains time offset for decoding
    private boolean setDeltaInfo(String strSampleCount, String strDelta) {
        if (!Utils.stringNullCheck(strSampleCount) || !Utils.stringNullCheck(strDelta)) {
            strSampleCount = Long.toString(m_uiSampleCount);
            strDelta = Long.toString(m_TrackInfo.getM_uiSampleDuration());
        }

        String[] arrSCounts = strSampleCount.split(";");
        String[] arrSDeltas = strDelta.split(";");

        if (arrSCounts.length != arrSDeltas.length) {
            return true;
        }

        for (int uiIndex = 0; (uiIndex & 0xFFFFFFFFL) < arrSCounts.length; uiIndex++) {
            long uiSCount = 0;
            int uiSDelta = 0;
            if (Utils.stringNullCheck(arrSCounts[uiIndex])) {
                uiSCount = Integer.parseInt(arrSCounts[uiIndex]);
            }
            if (Utils.stringNullCheck(arrSDeltas[uiIndex])) {
                uiSDelta = Integer.parseInt(arrSDeltas[uiIndex]);
            }

            DeltaInfo info = new DeltaInfo();
            info.setM_uiSampleCount(uiSCount);
            info.setM_uiSampleDelta(uiSDelta);
            m_dictDeltaInfo.put((((long) m_dictDeltaInfo.size()) & 0xFFFFFFFFL) + 1, info.Clone());

        }
        return false;
    }

    public long getSampleTimeSecToSampleNumber(double cutInTime) {
        long cutInTimeScale = (long) (cutInTime * (m_TrackInfo.getM_uiTimeScale() & 0xFFFFFFFFL));
        long sampleCount = 0;
        long sampleDuration = 0;
        if (m_dictDeltaInfo != null) {
            Set<Long> keySet = m_dictDeltaInfo.keySet();
            Iterator itr = keySet.iterator();
            while (itr.hasNext()) {
                long key = (Long) itr.next();
                DeltaInfo info = m_dictDeltaInfo.get(key);
                long sampleNumberPerDelta = info.getM_uiSampleCount();
                for (int i = 0; i < sampleNumberPerDelta; i++) {
                    ++sampleCount;
                    sampleDuration += info.getM_uiSampleDelta();
                    if (sampleDuration >= cutInTimeScale) {
                        return sampleCount;
                    }
                }
            }
        }
        return sampleCount;
    }
    
    public double getSamoleNuberToSampleTimeSec(long sampleNumber){
//        long cutInTimeScale = (long) (cutInTime * (m_TrackInfo.getM_uiTimeScale() & 0xFFFFFFFFL));
        long sampleCount = 0;
        double sampleDuration = 0;
        if (m_dictDeltaInfo != null) {
            Set<Long> keySet = m_dictDeltaInfo.keySet();
            Iterator itr = keySet.iterator();
            while (itr.hasNext()) {
                long key = (Long) itr.next();
                DeltaInfo info = m_dictDeltaInfo.get(key);
                long sampleNumberPerDelta = info.getM_uiSampleCount();
                for (int i = 0; i < sampleNumberPerDelta; i++) {
                    ++sampleCount;
                    sampleDuration += info.getM_uiSampleDelta();
                    if (sampleCount >= sampleNumber) {
                       sampleDuration = (double) sampleDuration/(m_TrackInfo.getM_uiTimeScale() & 0xFFFFFFFFL);
                       return sampleDuration;
                    }
                }
            }
        }
        return sampleDuration;
    }

//           
//
    public boolean endMediaInfo() {
        if ((m_uiSamplesPerChunk & 0xFFFFFFFFL) > 0) {
            ChunkInfo info = new ChunkInfo();
            info.setM_uiChunkNo(m_uiChunkNo);
//            info.setM_uiDescIndex(1);
            info.setM_uiSampleCount(m_uiSamplesPerChunk);
            m_dictChunkInfo.put(m_uiChunkNo, info.Clone());
            m_uiSamplesPerChunk = 0;
        }
        //m_uiSampleCount = 0;
        m_uiChunkNo = 0;

        long iSampleCount = m_presentationInfo.getUiSampleCount();
        if(iSampleCount > 0)
        { 
            CompTimeInfo presentation = new CompTimeInfo(); 
            m_presentationInfo.CloneTo(presentation);
            m_dictCttsInfo.put((long) m_dictCttsInfo.size() + 1, presentation);
                
            // reset info with current time
            m_presentationInfo.setUiSampleCount(0);
            m_presentationInfo.setUiOffset(0); 
        }
        
        iSampleCount = m_DeltaInfo.getM_uiSampleCount();
        if(iSampleCount > 0)
        { 
            DeltaInfo comp = new DeltaInfo(); 
            m_DeltaInfo.CloneTo(comp);
            m_dictDeltaInfo.put((long) m_dictDeltaInfo.size() + 1, comp);
                
            // reset info with current time
            m_DeltaInfo.setM_uiSampleCount(0);
            m_DeltaInfo.setM_uiSampleDelta(0); 
        }
        
        
        return false;
    }
//
//    /*override*/ boolean pauseWriting()
//    {
//        // Pauses writing and clears the partial gop info if present
//        // and restores values from the last valid GOP.
//        super.pauseWriting();
//
//        // These are samples from partial gop
//        m_uiChunkNo = (m_uiChunkNo & 0xFFFFFFFFL) - ((((/*uint*/long)m_dictChunkOffset.size()) & 0xFFFFFFFFL));                    
//        m_uiSampleCount = (m_uiSampleCount & 0xFFFFFFFFL) - ((((/*uint*/long)m_dictSampleSize.size()) & 0xFFFFFFFFL));
//        m_uiSamplesPerChunk = 0;
//        m_uiPrevSamplesPerChunk = m_uiLastGopSamplesPerChunk;
//        clearInfo();
//
//        return false;
//    }
//

    public boolean endWriting() {
//        setDeltaInfo("", "");     
               
        
        m_TrackInfo.setM_uiSampleCount(m_uiSampleCount);
        m_TrackInfo.setM_uiDuration((m_uiSampleCount & 0xFFFFFFFFL) * (m_TrackInfo.getM_uiSampleDuration()) & 0xFFFFFFFFL);
        m_TrackInfo.setM_fDurationSecs((float) m_TrackInfo.getM_uiDuration() / (m_TrackInfo.getM_uiTimeScale() & 0xFFFFFFFFL));

        // Add expected child atoms
        addAtom("tkhd"); // Expected Atom
        addAtom("mdia"); // Expected Atom


        return super.endWriting();
    }
//

    public boolean generateAtom() {
        try {
            long lStart = (long) m_BinWriter.getFilePointer();//getBaseStream().getPosition();                                  
            super.generateAtom();
            Set keySet = m_dictChildAtoms.keySet();
            Iterator keyItr = keySet.iterator();
            while (keyItr.hasNext()) {
                Object keyObj = keyItr.next();
                RandomAccessFile referenceToM_BinWriter = m_BinWriter;
                m_dictChildAtoms.get(keyObj).setWriter(referenceToM_BinWriter);
                m_dictChildAtoms.get(keyObj).generateAtom();
            }
//            for (KeyValuePair<String, Mp4AtomBase> kp : (Iterable<KeyValuePair<String,Mp4AtomBase>>) m_dictChildAtoms)
//            {
//                BinaryWriter[] referenceToM_BinWriter = { m_BinWriter };
//                kp.Value.setWriter(/*ref*/ referenceToM_BinWriter);
//                m_BinWriter = referenceToM_BinWriter[0];
//                kp.Value.generateAtom();
//            }

            long lStop = (long) m_BinWriter.getFilePointer();//getBaseStream().getPosition();
            if ((lStop - lStart) != getAtomSize()) {
                //log.info("*****  Generation Error. Atom - {0}  ExpectedSize {1} ActualSize {2}" + getAtomID() + " | " + getAtomSize() + " | " + (lStop - lStart));
                printLog(LogType.information, "mp4editor.TRAKAtom | "+"*****  Generation Error. Atom - {0}  ExpectedSize {1} ActualSize {2}"+getAtomID()+" | " +getAtomSize()+" | "+(lStop - lStart), null);
            }

            return true;
        } catch (IOException ex) {
            printLog(LogType.error, "mp4editor.TRAKAtom | ", ex);            
            return false;
        }
    }
//

    public long calcChildsAtomSize() {
        long iSize = 0;
        for (Map.Entry<String, Mp4AtomBase> entry : m_dictChildAtoms.entrySet())         
            iSize += entry.getValue().getAtomSize();       
        return iSize;
    }
//

    private void clearInfo() {
        m_queueRandomAccessSampNos.clear();//Clear();
        m_dictChunkInfo.clear();
        m_dictChunkOffset.clear();
        m_dictSampleSize.clear();
        m_dictCttsInfo.clear();
        m_dictDeltaInfo.clear();
    }
//
    
    long m_lDuration = 0;
    double getTrackDurationSecs()
    {
        double dbDurationSecs = 0.0;
        dbDurationSecs = (double)m_lDuration / m_TrackInfo.getM_uiTimeScale();
        return dbDurationSecs;
    }
    /*
    boolean setTrackInfo(Element track , double dbAdjustDuration) {
        if (m_enmTrkType.equalsIgnoreCase("TRAK_VIDEO")) {
            m_queueRandomAccessSampNos.add((((long) m_dictSampleSize.size()) & 0xFFFFFFFFL) + 1);
        }
        try {
            NodeList chunkInfoNode = track.getElementsByTagName(XMLDefines.ELEMENT_CHUNK_INFO);
            for (int i = 0; i < chunkInfoNode.getLength(); i++) {
                Element chunkInfo = (Element) chunkInfoNode.item(i);
                if (chunkInfo != null) {
                    NodeList listChunks = chunkInfo.getChildNodes();
                    for (int j = 0; j < listChunks.getLength(); j++) {
                        Node chunkNode = listChunks.item(j);
                        if (chunkNode != null && chunkNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element chunk = (Element) chunkNode;
                            String[] arrChunkInfo = chunk.getTextContent().split(";");
                            if (arrChunkInfo != null && arrChunkInfo.length != 0) {
                                ChunkInfo info = new ChunkInfo();
                                info.setM_uiChunkNo(Long.parseLong(arrChunkInfo[0]));
//                                info.setM_uiDescIndex(Long.parseLong(arrChunkInfo[1]));
                                info.setM_uiSampleCount(Long.parseLong(arrChunkInfo[2]));
//                                m_dictChunkInfo.put(info.getM_uiChunkNo(), info.Clone());
                                m_dictChunkInfo.put(info.getM_uiChunkNo(), info);
                            }
                        }
                    }
                }
            }
//            Element chunkInfo = (Element)track.SelectSingleNode(XMLDefines.ELEMENT_CHUNK_INFO);
        } catch (RuntimeException ex) {
            printLog(LogType.error, "mp4editor.TRAKAtom | ", ex);            
        }

        try {
//            XmlElement chunkOffset = (XmlElement)track.SelectSingleNode(XMLDefines.ELEMENT_CHUNKOFFSET_INFO);
            NodeList chunkOffSetNodes = track.getElementsByTagName(XMLDefines.ELEMENT_CHUNKOFFSET_INFO);
            for (int i = 0; i < chunkOffSetNodes.getLength(); i++) {
                Node chunkOffsetNode = chunkOffSetNodes.item(i);
                if (chunkOffsetNode != null && chunkOffsetNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element chunkOffset = (Element) chunkOffsetNode;
                    if (chunkOffset != null) {
                        String[] arrChunkOffSets = chunkOffset.getTextContent().split(";");//, ";".toCharArray());
                        for (String sChunkOffset : arrChunkOffSets) {
                            if (Utils.stringNullCheck(sChunkOffset)) {
                                m_dictChunkOffset.put((((long) m_dictChunkOffset.size()) & 0xFFFFFFFFL) + 1, Long.parseLong(sChunkOffset));
                            }
                        }
                    }
                }
            }
        } catch (RuntimeException ex) {
            printLog(LogType.error, "mp4editor.TRAKAtom | ", ex);            
        }

        try {
            NodeList sampleSizeNodes = track.getElementsByTagName(XMLDefines.ELEMENT_SAMPLESIZE_INFO);
            for (int i = 0; i < sampleSizeNodes.getLength(); i++) {
                Node sampleNode = sampleSizeNodes.item(i);
                if (sampleNode != null && sampleNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element sampleSize = (Element) sampleNode;
                    if (sampleSize != null) {
                        String[] arrSampleSizes = sampleSize.getTextContent().split(";");
                        for (String sSampleSize : arrSampleSizes) {
                            if (Utils.stringNullCheck(sSampleSize)) {
                                m_dictSampleSize.put((( m_dictSampleSize.size()) & 0xFFFFFFFFL) + 1, Long.parseLong(sSampleSize));
                                m_uiSampleCount++;
                            }
                        }
                    }
                }
            }
        } catch (RuntimeException ex) {
            printLog(LogType.error, "mp4editor.TRAKAtom | ", ex);            
        }
        try {
            NodeList compTimeNode = track.getElementsByTagName(XMLDefines.ELEMENT_COMPTIME_INFO);
            if (compTimeNode != null) {
                for (int i = 0; i < compTimeNode.getLength(); i++) {
                    Node sampleNode = compTimeNode.item(i);
                    if (sampleNode != null && sampleNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element sampleSize = (Element) sampleNode;
                        if (sampleSize != null) {
                            String[] arrSampleSizes = sampleSize.getTextContent().split(";");
                            for (String sSampleSize : arrSampleSizes) {
                                if (Utils.stringNullCheck(sSampleSize)) {
                                    int uiTime = Integer.parseInt(sSampleSize);
                                    CompTimeInfo compTimeInfo = new CompTimeInfo();
                                    compTimeInfo.setUiOffset(uiTime);
                                    compTimeInfo.setUiSampleCount(1);
                                    if (m_dictCttsInfo.size() > 0) {
                                        if (m_uiPreviousCompTime == uiTime) {
                                            CompTimeInfo lastTime = m_dictCttsInfo.get((long) m_dictCttsInfo.size() - 1);
                                            lastTime.setUiSampleCount(lastTime.getUiSampleCount() + 1);
                                            m_dictCttsInfo.put((long) m_dictCttsInfo.size() - 1, lastTime);
                                        } else {
                                            m_dictCttsInfo.put((long) m_dictCttsInfo.size(), compTimeInfo);
                                        }
                                    } else {
                                        m_dictCttsInfo.put((long) m_dictCttsInfo.size(), compTimeInfo);
                                    }
                                    m_uiPreviousCompTime = uiTime;
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception ex) {
            printLog(LogType.error, "mp4editor.TRAKAtom | ", ex);            
        }
        try {
            NodeList deltaInfoNode = track.getElementsByTagName(XMLDefines.ELEMENT_SAMPLE_DELTA_INFO);
            if (deltaInfoNode != null) {
                for (int i = 0; i < deltaInfoNode.getLength(); i++) {
                    Node sampleDeltaNode = deltaInfoNode.item(i);
                    if (sampleDeltaNode != null && sampleDeltaNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element sampleValue = (Element) sampleDeltaNode;
                        if (sampleValue != null) {
                            String[] arrSampleValue = sampleValue.getTextContent().split(";");
                            
                            int iAdjust = 0;  
                            if (m_enmTrkType.equalsIgnoreCase("TRAK_VIDEO"))
                                iAdjust = (int)((dbAdjustDuration * m_TrackInfo.getM_uiTimeScale()) / arrSampleValue.length);
                                
                            for (String sSampleValue : arrSampleValue) {
                                if (Utils.stringNullCheck(sSampleValue)) {
                                    int uiTime = Integer.parseInt(sSampleValue);
                                    uiTime += iAdjust;
                                    if(uiTime < 0)
                                        uiTime = 0;
                                        
                                    DeltaInfo deltaInfo = new DeltaInfo();
                                    deltaInfo.setM_uiSampleDelta(uiTime);
                                    deltaInfo.setM_uiSampleCount(1);
                                    
                                    // Update duration of track
                                    m_lDuration += uiTime;
                                    
                                    if (m_dictDeltaInfo.size() > 0) {
                                        if (m_uiPreviousSampleDelta == uiTime) {
                                            DeltaInfo lastDeltaInfo = m_dictDeltaInfo.get((long) m_dictDeltaInfo.size());
                                            lastDeltaInfo.setM_uiSampleCount(lastDeltaInfo.getM_uiSampleCount()+ 1);
                                            m_dictDeltaInfo.put((long) m_dictDeltaInfo.size(), lastDeltaInfo);
                                        } else {
                                            m_dictDeltaInfo.put((long) m_dictDeltaInfo.size()+1, deltaInfo);
                                        }
                                    } else {
                                        m_dictDeltaInfo.put((long) m_dictDeltaInfo.size()+1, deltaInfo);
                                    }
                                    m_uiPreviousSampleDelta = uiTime;
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception ex) {
            printLog(LogType.error, "mp4editor.TRAKAtom | ", ex);            
        }
        
        return true;
    }   

    Element getTrackInfo(Document doc) {
        // Updates info headers 
        updateInfoHeaders();

        Element trkInfo = null;
        if (m_dictChunkInfo.size() > 0 || m_dictChunkOffset.size() > 0 || m_dictSampleSize.size() > 0) {

            trkInfo = doc.createElement(XMLDefines.ELEMENT_TRACK);
            trkInfo.setAttribute(XMLDefines.ATTR_TRACK_ID, Long.toString(m_TrackInfo.getM_uiTrack_ID()));

            if (m_dictChunkInfo.size() > 0) {
                Element chunkInfo = doc.createElement(XMLDefines.ELEMENT_CHUNK_INFO);
//                for (KeyValuePair<Long, ChunkInfo> kp : (Iterable<KeyValuePair<Long,ChunkInfo>>) m_dictChunkInfo)
                java.util.Set keySet = m_dictChunkInfo.keySet();
                Iterator keyItr = keySet.iterator();
                while (keyItr.hasNext()) {
                    Object keyObj = keyItr.next();
//                for(int i=0; i<m_dictChunkInfo.size();i++)
//                {
                    Element chunk = doc.createElement(XMLDefines.ELEMENT_CHUNK);
                    StringBuilder sBuilder = new StringBuilder();//msStringBuilder.ctor();
                    sBuilder.append(m_dictChunkInfo.get(keyObj).getM_uiChunkNo());
                    sBuilder.append(";");
//                    sBuilder.append(m_dictChunkInfo.get(keyObj).getM_uiDescIndex());
                    sBuilder.append(";");
                    sBuilder.append(m_dictChunkInfo.get(keyObj).getM_uiSampleCount());
                    chunk.setTextContent(sBuilder.toString());
                    chunkInfo.appendChild(chunk);
                }
                trkInfo.appendChild(chunkInfo);
            }

            if (m_dictChunkOffset.size() > 0) {

                Element chunkOffset = doc.createElement(XMLDefines.ELEMENT_CHUNKOFFSET_INFO);
                StringBuilder sBuilder = new StringBuilder();
                java.util.Set keySet = m_dictChunkOffset.keySet();
                Iterator keyItr = keySet.iterator();
                while (keyItr.hasNext()) {
//                for (KeyValuePair<Long,Long> kp : (Iterable<KeyValuePair<Long,Long>>) m_dictChunkOffset)
                    Object keyObj = keyItr.next();
                    sBuilder.append(m_dictChunkOffset.get(keyObj));
                    sBuilder.append(";");
                }
                chunkOffset.setTextContent(sBuilder.toString());
                trkInfo.appendChild(chunkOffset);
            }

            if (m_dictSampleSize.size() > 0) {
                Element SampleSize = doc.createElement(XMLDefines.ELEMENT_SAMPLESIZE_INFO);
                StringBuilder sBuilder = new StringBuilder();//.ctor();
                java.util.Set keySet = m_dictSampleSize.keySet();
                Iterator keyItr = keySet.iterator();
                while (keyItr.hasNext()) {
                    Object keyObj = keyItr.next();
//                for (KeyValuePair<Long,Long> kp : (Iterable<KeyValuePair<Long,Long>>) m_dictSampleSize)
//                {
                    sBuilder.append(m_dictSampleSize.get(keyObj));
                    sBuilder.append(";");
                }
                SampleSize.setTextContent(sBuilder.toString());
                trkInfo.appendChild(SampleSize);
            }

            if (m_dictCttsInfo.size() > 0) {
                Element comptime = doc.createElement(XMLDefines.ELEMENT_COMPTIME_INFO);
//                    XmlElement comptime = doc.CreateElement(XMLDefines.ELEMENT_COMPTIME_INFO);
                StringBuilder sBuilder = new StringBuilder();
                java.util.Set keySet = m_dictCttsInfo.keySet();
                Iterator keyItr = keySet.iterator();

                while (keyItr.hasNext()) {
                    Object keyObj = keyItr.next();
                    sBuilder.append(((CompTimeInfo) m_dictCttsInfo.get(keyObj)).getUiOffset());
                    sBuilder.append(";");
                }

                comptime.setTextContent(sBuilder.toString());
                trkInfo.appendChild(comptime);
            }
            
            if(m_dictDeltaInfo.size()>0){
                Element deltaElement = doc.createElement(XMLDefines.ELEMENT_SAMPLE_DELTA_INFO);
                StringBuilder sBuilder = new StringBuilder();
                
                for(Long key: m_dictDeltaInfo.keySet()){
                    DeltaInfo deltaInfo = m_dictDeltaInfo.get(key);
                    sBuilder.append(deltaInfo.getM_uiSampleDelta());
                    sBuilder.append(";");
                }
                deltaElement.setTextContent(sBuilder.toString());
                trkInfo.appendChild(deltaElement);
            }
        }
        m_uiLastGopSamplesPerChunk = m_uiPrevSamplesPerChunk;
        clearInfo();
        return trkInfo;
    }
    */
//

    boolean updateInfoHeaders() {
        if ((m_uiSamplesPerChunk & 0xFFFFFFFFL) > 0) {
            if (m_uiPrevSamplesPerChunk != m_uiSamplesPerChunk) {
                ChunkInfo info = new ChunkInfo();
                info.setM_uiChunkNo(m_uiChunkNo);
//                info.setM_uiDescIndex(1);
                info.setM_uiSampleCount(m_uiSamplesPerChunk);
                m_dictChunkInfo.put(m_uiChunkNo, info.Clone());
                m_uiPrevSamplesPerChunk = m_uiSamplesPerChunk;
            }

            m_uiSamplesPerChunk = 0;
        }
        return false;
    }
//
    long previousAudioSampleDelta = -1;
    boolean addSample(boolean bIsNewChunk, long uiChunkOffset, long uiSampleSize, double sampleDelta, boolean bIFrame) {
        if (bIsNewChunk) {
            updateInfoHeaders();
            m_uiChunkNo++;
            m_dictChunkOffset.put(m_uiChunkNo, uiChunkOffset);
        }

        m_uiSamplesPerChunk++;
        m_uiSampleCount++;

        if (bIFrame) // This is I Frame and hence random access point
        {
            m_queueRandomAccessSampNos.add(m_uiSampleCount);
        }
         
        int delta = (int)(sampleDelta*m_TrackInfo.getM_uiTimeScale());         
       
        // Optimized
        UpdateNewCompTime(delta);
        
        m_dictSampleSize.put(m_uiSampleCount, (int)uiSampleSize);
        return false;
    }
    
    void UpdateNewPresentationTime(int presTime)
    {
        long iSampleCount = m_presentationInfo.getUiSampleCount();
        if(iSampleCount > 0)
        {            
            if(m_presentationInfo.getUiOffset() == presTime)
            {
                 // Increment sample count as time is same for both samples
                 m_presentationInfo.setUiSampleCount(iSampleCount + 1);
            }
            else
            {
                CompTimeInfo presentation = new CompTimeInfo(); 
                m_presentationInfo.CloneTo(presentation);
                m_dictCttsInfo.put((long) m_dictCttsInfo.size() + 1, presentation);
                
                // reset info with current time
                m_presentationInfo.setUiSampleCount(1);
                m_presentationInfo.setUiOffset(presTime); 
            }
        }
        else
        {            
            m_presentationInfo.setUiSampleCount(1);
            m_presentationInfo.setUiOffset(presTime);            
        }
    }
    
    void UpdateNewPresentationTime(double presTimeOffset)
    {
        int presTime = (int)(presTimeOffset * m_TrackInfo.getM_uiTimeScale());
        long iSampleCount = m_presentationInfo.getUiSampleCount();
        if(iSampleCount > 0)
        {            
            if(m_presentationInfo.getUiOffset() == presTime)
            {
                 // Increment sample count as time is same for both samples
                 m_presentationInfo.setUiSampleCount(iSampleCount + 1);
            }
            else
            {
                CompTimeInfo presentation = new CompTimeInfo(); 
                m_presentationInfo.CloneTo(presentation);
                m_dictCttsInfo.put((long) m_dictCttsInfo.size() + 1, presentation);
                
                // reset info with current time
                m_presentationInfo.setUiSampleCount(1);
                m_presentationInfo.setUiOffset(presTime); 
            }
        }
        else
        {            
            m_presentationInfo.setUiSampleCount(1);
            m_presentationInfo.setUiOffset(presTime);            
        }
    }
    
    
    void UpdateNewCompTime(int compTime)
    {
        m_uiAccumulatedDuration += compTime;
       
        long iSampleCount = m_DeltaInfo.getM_uiSampleCount();
        if(iSampleCount > 0)
        {            
            if(m_DeltaInfo.getM_uiSampleDelta() == compTime)
            {
                 // Increment sample count as time is same for both samples
                 m_DeltaInfo.setM_uiSampleCount(iSampleCount + 1);
            }
            else
            {
                DeltaInfo comp = new DeltaInfo(); 
                m_DeltaInfo.CloneTo(comp);
                m_dictDeltaInfo.put((long) m_dictDeltaInfo.size() + 1, comp);
                
                // reset info with current time
                m_DeltaInfo.setM_uiSampleCount(1);
                m_DeltaInfo.setM_uiSampleDelta(compTime); 
            }
        }
        else
        {            
            m_DeltaInfo.setM_uiSampleCount(1);
            m_DeltaInfo.setM_uiSampleDelta(compTime);            
        }
    }
    
    long previousVideoSampleDelta = -1;
    boolean addSample(boolean bIsNewChunk, long uiChunkOffset, long uiSampleSize, boolean bIFrame, boolean bReOrder,double sampleDelta, int iFrames) {
        
        int presentation = (int) (iFrames * m_TrackInfo.getM_uiSampleDuration());
        
        // Optimized Update presentation time
        UpdateNewPresentationTime(presentation);
                        
        if (bIsNewChunk) {
            updateInfoHeaders();
            m_uiChunkNo++;
            m_dictChunkOffset.put(m_uiChunkNo, uiChunkOffset);
        }

        m_uiSamplesPerChunk++;
        m_uiSampleCount++;

        if (bIFrame) // This is I Frame and hence random access point
        {
            m_queueRandomAccessSampNos.add(m_uiSampleCount);
        }
        
        int delta = (int)(sampleDelta*m_TrackInfo.getM_uiTimeScale());
        
        // Optimized
        UpdateNewCompTime(delta);
        
        m_dictSampleSize.put(m_uiSampleCount, (int)uiSampleSize);
        return false;
    }
    
    
     boolean addSample(boolean bIsNewChunk, long uiChunkOffset, long uiSampleSize, boolean bIFrame, double sampleDelta, double samplePresentationDelta) {
        
        // Optimized Update presentation time
        UpdateNewPresentationTime(samplePresentationDelta);
                        
        if (bIsNewChunk) {
            updateInfoHeaders();
            m_uiChunkNo++;
            m_dictChunkOffset.put(m_uiChunkNo, uiChunkOffset);
        }

        m_uiSamplesPerChunk++;
        m_uiSampleCount++;

        if (bIFrame) // This is I Frame and hence random access point
        {
            m_queueRandomAccessSampNos.add(m_uiSampleCount);
        }
        
        int delta = (int)(sampleDelta*m_TrackInfo.getM_uiTimeScale());
        
        // Optimized
        UpdateNewCompTime(delta);
        
        m_dictSampleSize.put(m_uiSampleCount, (int)uiSampleSize);
        return false;
    }

    private boolean addAtom(String sAtomID) {
        Mp4AtomBase atom = null;
        switch (sAtomID) {
            case "tkhd": {
                atom = new TKHDAtom(this);
                break;
            }

            case "mdia": {
                atom = new MDIAAtom(this);
                break;
            }
        }

        if (atom != null) {
            atom.m_Parent = this;
            m_dictChildAtoms.put(sAtomID, atom);
        }

        return false;
    }
//               
//
//    boolean getSampleCount(/*ref*/ /*uint*/long[] uiSampleCount)
//    {
//        uiSampleCount[0] = (/*uint*/long)m_dictSampleSize.size();
//        return false;
//    }
//

    boolean getTrackDuration(long[] uiTimeScale, long[] uiDuration) {
        uiTimeScale[0] = m_TrackInfo.getM_uiTimeScale();
        uiDuration[0] = m_uiAccumulatedDuration;
        return false;
    }

    boolean getTrackDuration(double[] dDurInSeconds) {
        dDurInSeconds[0] = 0.0;
        if ((m_TrackInfo.getM_uiTimeScale() & 0xFFFFFFFFL) > 0) {
            dDurInSeconds[0] = (double) m_uiAccumulatedDuration / (m_TrackInfo.getM_uiTimeScale());
        }
        return false;
    }

    boolean getIFrame(long uiSampleNo, IFrameSearchEnm search, long[] uiISample) {
        uiISample[0] = 1;
        if (m_queueRandomAccessSampNos.size() == 0 || m_queueRandomAccessSampNos.contains(uiSampleNo)) {
            uiISample[0] = uiSampleNo;
            return false;
        }

        if (uiSampleNo < m_queueRandomAccessSampNos.get(0)) {
            uiISample[0] = m_queueRandomAccessSampNos.get(0);
            return false;
        }

        if (uiSampleNo > m_queueRandomAccessSampNos.get(m_queueRandomAccessSampNos.size() - 1)) {
            if (uiSampleNo > m_SampleInfo.m_iSampleCount) {
                uiISample[0] = m_SampleInfo.m_iSampleCount;
            } else {
                uiISample[0] = uiSampleNo;
            }
            return false;
//            uiISample[0] = m_queueRandomAccessSampNos.get(m_queueRandomAccessSampNos.size()-1);
//            return false;
        }

//        long[] uiRandomSamples = new long[m_queueRandomAccessSampNos.size()];

//        m_queueRandomAccessSampNos.CopyTo(uiRandomSamples, 0);
        int iCount = m_queueRandomAccessSampNos.size();
        for (int iIndex = 0; iIndex < iCount; iIndex++) {
            if ((uiSampleNo & 0xFFFFFFFFL) <= (m_queueRandomAccessSampNos.get(iIndex) & 0xFFFFFFFFL)) {
                // Exact sample found
                if (uiSampleNo == m_queueRandomAccessSampNos.get(iIndex)) {
                    uiISample[0] = m_queueRandomAccessSampNos.get(iIndex);
                    return false;
                }

                switch (search) {
                    case LOWER: {
                        if (iIndex > 0) {
                            uiISample[0] = m_queueRandomAccessSampNos.get(iIndex - 1);
                        } else {
                            uiISample[0] = m_queueRandomAccessSampNos.get(iIndex);
                        }
                        break;
                    }
                    case HEIGHER: {
                        uiISample[0] = m_queueRandomAccessSampNos.get(iIndex);
                        break;
                    }
                    case NEAREST: {
                        if (iIndex > 0) {
                            long uiMid = ((m_queueRandomAccessSampNos.get(iIndex) & 0xFFFFFFFFL) + (m_queueRandomAccessSampNos.get(iIndex - 1) & 0xFFFFFFFFL)) / 2;
                            if ((uiSampleNo & 0xFFFFFFFFL) < (uiMid & 0xFFFFFFFFL)) {
                                uiISample[0] = m_queueRandomAccessSampNos.get(iIndex - 1);
                            } else {
                                uiISample[0] = m_queueRandomAccessSampNos.get(iIndex);
                            }
                        } else {
                            uiISample[0] = m_queueRandomAccessSampNos.get(iIndex);
                        }
                        break;
                    }
                }

                return false;
            }
        }
        return true;
    }
//

    boolean getTrackSampleInfo(long uiSampleNumber, long[] uiFileOffset, long[] uiSampleSize,double[] sampleDuration, String[] sError) {
        sError[0] = "";
        uiFileOffset[0] = 0;
        uiSampleSize[0] = 0;
        try {
            int iSampleNo = (int)uiSampleNumber;
            uiFileOffset[0] = m_SampleInfo.m_dictSampleInfo.get(iSampleNo).m_lOffset;
            uiSampleSize[0] = m_SampleInfo.m_dictSampleInfo.get(iSampleNo).m_iSize;
            sampleDuration[0] = (double)m_SampleInfo.m_dictSampleInfo.get(iSampleNo).m_DecodeDelta/m_TrackInfo.getM_uiTimeScale();
            return false;
        } catch (RuntimeException error) {
            printLog(LogType.error, "mp4editor.TRAKAtom | ", error);            
        }
        return true;
    }
    
    long getTotalTrackSamples() {
        
        try {
            return m_SampleInfo.m_dictSampleInfo.size();
        } catch (RuntimeException error) {
            printLog(LogType.error, "mp4editor.TRAKAtom | ", error);            
        }
        return 0;
    }
    
     boolean getTrackSampleInfo(long uiSampleNumber, long[] uiFileOffset, long[] uiSampleSize,double[] sampleDuration,double[] samplePresentationOffset, String[] sError) {
        sError[0] = "";
        uiFileOffset[0] = 0;
        uiSampleSize[0] = 0;
        try {
            int iSampleNo = (int)uiSampleNumber;
            uiFileOffset[0] = m_SampleInfo.m_dictSampleInfo.get(iSampleNo).m_lOffset;
            uiSampleSize[0] = m_SampleInfo.m_dictSampleInfo.get(iSampleNo).m_iSize;
            sampleDuration[0] = (double)m_SampleInfo.m_dictSampleInfo.get(iSampleNo).m_DecodeDelta/m_TrackInfo.getM_uiTimeScale();
            samplePresentationOffset[0] = (double)m_SampleInfo.m_dictSampleInfo.get(iSampleNo).m_PresentationDelta/m_TrackInfo.getM_uiTimeScale();
            return false;
        } catch (RuntimeException error) {
            printLog(LogType.error, "mp4editor.TRAKAtom | ", error);            
        }
        return true;
    }
//
//    byte[] getSDPPayload()
//    {
//        return ((UDTAAtom)getChild("udta")).m_hntiAtom.m_sSDPText;
//    }
//

    public boolean parseAtoms(long uiFileStart, long uiAtomSize) {
        super.parseAtoms(uiFileStart, uiAtomSize);

        long uiFileOffset = getFileStart() + Utils.HEADER_LENGTH;
        long uiAtomEnd = getFileEnd();
        long uiAtomLength = 0;
        char[] Atom_ID = null;

        while (uiFileOffset < uiAtomEnd) {
            try {
                RandomAccessFile referenceToM_BinReader = m_BinReader;
                byte[] MpegBoxBuff = Utils.readBytes(referenceToM_BinReader, uiFileOffset, (int) Utils.HEADER_LENGTH);

                long[] referenceToUiAtomLength = {uiAtomLength};
                char[][] referenceToAtom_ID = {Atom_ID};
                Utils.readAtom(MpegBoxBuff, referenceToUiAtomLength, referenceToAtom_ID);
                uiAtomLength = referenceToUiAtomLength[0];
                Atom_ID = referenceToAtom_ID[0];
                String sAtomID = new String(Atom_ID);
                sAtomID = sAtomID.toLowerCase();
                Mp4AtomBase atom = null;

//                logInfo(msString.format("    TRAK-Parser  AtomID = {0} AtomLength = {1}", sAtomID, uiAtomLength), LogType.INFORMATION);

                switch (sAtomID) {
                    case "tkhd": {
                        atom = new TKHDAtom(this);
                        break;
                    }

                    case "mdia": {
                        atom = new MDIAAtom(this);
                        break;
                    }

                    case "udta": {
                        atom = new UDTAAtom(this);
                        break;
                    }
                    case "tref": {
                        atom = new TREFAtom(this);
                        break;
                    }
                    default: {
                        break;
                    }
                }

                if (atom != null) {
                    atom.setAtomID(sAtomID);
                    atom.setReader(referenceToM_BinReader);
                    atom.m_Parent = this;
                    atom.parseAtoms(uiFileOffset, uiAtomLength);
                    m_dictChildAtoms.put(sAtomID, atom);
                }
                Atom_ID = null;
                // update file offset
                uiFileOffset += java.lang.Math.max(uiAtomLength, Utils.HEADER_LENGTH);
            } catch (IOException ex) {
                printLog(LogType.error, "mp4editor.TRAKAtom | ", ex);            
                return true;
            }
        }

        fillChunkInfo();
        return false;
    }
    
    long GetChunkInfo(int iIndex ,long[] iChunkNo)
    {
        long iRetVal = -1;
        try 
        {
            int iOffset = iIndex *12;
            if(iOffset >= m_ChunkInfo.m_iBuffSize)
                return -1;
            iChunkNo[0] = Utils.readUInt32(m_ChunkInfo.m_aarChunkInfo, iOffset);
            iRetVal = Utils.readUInt32(m_ChunkInfo.m_aarChunkInfo, iOffset+4);
        } 
        catch (Exception ex)
        {
            printLog(LogType.error, "mp4editor.TRAKAtom | ", ex);            
        }
        return iRetVal;
    }
    
    long GetSampleSize(int iIndex)
    {
        long iRetVal = -1;
        try 
        {
            int iOffset = iIndex * 4;
            iRetVal = Utils.readUInt32(m_SampleSizeInfo.m_aarSampleSize, iOffset);            
        } 
        catch (Exception ex)
        {
            printLog(LogType.error, "mp4editor.TRAKAtom | ", ex);            
        }
        return iRetVal;
    }
    
    long GetChunkOffset(int iIndex)
    {
        long iRetVal = -1;
        try 
        {
            int iOffset = iIndex * (m_ChunkOffsetInfo.m_iVersion == 0 ? 4 : 8);
            if(m_ChunkOffsetInfo.m_iVersion == 0)
                iRetVal = Utils.readUInt32(m_ChunkOffsetInfo.m_aarChunkOffset, iOffset);
            else
                iRetVal = Utils.readUInt64(m_ChunkOffsetInfo.m_aarChunkOffset, iOffset);
        } 
        catch (Exception ex)
        {
            printLog(LogType.error, "mp4editor.TRAKAtom | ", ex);            
        }
        return iRetVal;
    }

    private void fillChunkInfo() // fill chunk info to easily access samples
    {
        try {
            
            long iSampleCount = 0;
            int iChunkInfoIndex = 0;
            int iSampleIndex = 0;
            
            // Decoding time stamps
            int[] sampleDuration = null;
            if (m_SampleSizeInfo.m_iEntryCount != 0) {
                 sampleDuration = new int[m_SampleSizeInfo.m_iEntryCount];
                int index = 0;
                if (m_dictDeltaInfo != null && m_dictDeltaInfo.size() > 0) {
                    for (Long key : m_dictDeltaInfo.keySet()) {
                        DeltaInfo deltaInfo = m_dictDeltaInfo.get(key);
                        for (int i = 0; i < deltaInfo.getM_uiSampleCount(); i++) {
                            sampleDuration[index++] = (int) deltaInfo.getM_uiSampleDelta();
                        }
                    }
                }
            }
            
            // Composition time stamps
            int[] samplePresentationDuration = null;
            if (m_SampleSizeInfo.m_iEntryCount != 0) {                
                int index = 0;
                if (m_dictCttsInfo != null && m_dictCttsInfo.size() > 0) 
                {
                    samplePresentationDuration = new int[m_SampleSizeInfo.m_iEntryCount];
                    
                    for (Long key : m_dictCttsInfo.keySet()) {
                        CompTimeInfo compInfo = m_dictCttsInfo.get(key);
                        for (int i = 0; i < compInfo.getUiSampleCount(); i++) {
                            samplePresentationDuration[index++] = compInfo.getUiOffset();
                        }
                    }
                }
            }
            for(int iChunkIndex = 0; iChunkIndex < m_ChunkOffsetInfo.m_iEntryCount ;iChunkIndex++)
            {
                boolean bChunkFound = false;
                long[] arrChunkNo = new long[1];
                long iCount = GetChunkInfo(iChunkInfoIndex,arrChunkNo);
                if(arrChunkNo[0] == (iChunkIndex + 1))
                {
                    bChunkFound = true;
                    iSampleCount = iCount;                   
                }
                
                long lChunkOffset = GetChunkOffset(iChunkIndex);
                long lSampleOffset = lChunkOffset;
                for(int iIndex = 0; iIndex < iSampleCount ; iIndex ++)
                {                    
                    long iSampleSize = GetSampleSize(iSampleIndex);
                    
                    MSampleInfo info = new MSampleInfo();
                    info.m_iSize = (int)iSampleSize;
                    info.m_lOffset = lSampleOffset;
                    if(sampleDuration!=null && sampleDuration.length>0)
                        info.m_DecodeDelta = sampleDuration[iSampleIndex];
                    
                    if(samplePresentationDuration != null && samplePresentationDuration.length >0)
                        info.m_PresentationDelta = samplePresentationDuration[iSampleIndex];
                    
                    m_SampleInfo.m_dictSampleInfo.put(iSampleIndex+1, info);
                    // Next sample offset
                    lSampleOffset += iSampleSize;
                    iSampleIndex++;
                }
                
                if(bChunkFound) // We have found chunk info move to next chunk
                {
                    iChunkInfoIndex++;// Next chunk index
                }
            }
            
            // Only m_SampleInfo hash map is required , release memory for other data structures
            m_ChunkOffsetInfo.DeInit();
            m_SampleSizeInfo.DeInit();
            m_ChunkInfo.DeInit();
            
        } catch (RuntimeException ex) {
            printLog(LogType.error, "mp4editor.TRAKAtom | ", ex);            
        }
    }

    public TrackInfo getM_TrackInfo() {
        return m_TrackInfo;
    }

    public void setM_TrackInfo(TrackInfo m_TrackInfo) {
        this.m_TrackInfo = m_TrackInfo;
    }

    public boolean isIsPartialParsing() {
        return isPartialParsing;
    }

    public void setIsPartialParsing(boolean isPartialParsing) {
        this.isPartialParsing = isPartialParsing;
    }
}
