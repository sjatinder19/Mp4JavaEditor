package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import mp4editor.pojo.CodecType;
import mp4editor.pojo.MP4Buffer;
import mp4editor.pojo.AVC1Info;
import mp4editor.pojo.TrackInfo;
import mp4editor.pojo.NALInfo;
import mp4editor.pojo.MP4AInfo;
import java.io.*;
import java.lang.reflect.Array;
import javax.xml.transform.Transformer;
import mp4editor.pojo.CodecInfo;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.transform.*;
import mp4editor.pojo.enm.BuffTypeEnm;
import mp4editor.pojo.enm.CodecTypeEnm;
import mp4editor.pojo.enm.IFrameSearchEnm;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import mp4editor.util.LogType;
import mp4editor.util.LoggingEvents;
import org.apache.commons.codec.binary.Base64;


public class MOOVAtom extends Mp4AtomBase
{
    MVHDAtom m_MVHDAtom = null;        
    HashMap<Long, TRAKAtom> m_dictTracks;       
    MpegFileInfo m_Mp4FileInfo = new MpegFileInfo();
    private long m_uiVideoID = 0;
    private long m_uiAudioID = 0;
    private long m_uiSampleCount = 0;        
    private BuffTypeEnm m_PrevType = BuffTypeEnm.INVALID;
    long m_uiMOOVTimeScale = 600;
    long m_uiMOOVDuration = 0;
    private Document m_Mp4Infodoc = null;
    private String m_sTargetFile = "";
    private String m_sMediaInfoFile = "";
    private FileWriter m_InfoWriter = null; // Mp4 Info writer object
    private int m_uiBFrameCount = 0;
    private boolean m_bFirstFrame = true;
    List<MP4Buffer> m_queueBFrames;
    String m_sTrackInfoXML;
    private boolean partialParsing = false;
    LoggingEvents logging = null;
    private int m_iNalHdrLen = -1;
    
    private boolean m_bRemoveOrphanedFrames = true;
    private int m_iNonBFrameCount = 0;
    private double m_dbDroppedFramesDurationSecs = 0;
    private int bFrameDropedCnt = 0;
    public MOOVAtom()
    {
        m_dictTracks = new HashMap<Long, TRAKAtom>();            
    }
    
    public void addLoggingListener(LoggingEvents logging){
        this.logging = logging;
    }
    
    public void printLog(LogType logType, String message, Throwable th){
        if(logging!=null){
            logging.printLog(logType, th, message);
        }
    }
    
    public void unInit(){
        if(m_dictTracks != null){
            Set<Long> keySet = m_dictTracks.keySet();
            Iterator<Long> keyItr = keySet.iterator();        
            while(keyItr.hasNext()){
                long key = keyItr.next();
                Mp4AtomBase atomObj = m_dictTracks.get(key);
                atomObj.unInit();
            }
        }
        m_dictTracks.clear();
        m_dictTracks = null;
    }

    public void setM_Mp4Infodoc(Document m_Mp4Infodoc) {
        this.m_Mp4Infodoc = m_Mp4Infodoc;
    }

    public Document getM_Mp4Infodoc() {
        return m_Mp4Infodoc;
    }

    void setInfoWriter(FileWriter value)
    {
        this.m_InfoWriter = value;
    }

    String getTargetFile()
    {
        return m_sTargetFile;
    }
    void setTargetFile(String value)
    {
        m_sTargetFile = value;
    }

    String getMediaInfoFile()
    {
        return m_sMediaInfoFile;
    }
    void setMediaInfoFile(String value)
    {
        m_sMediaInfoFile = value;
    }

    public int getVersion()
    {
        return super.getVersion();
    }
    
    public void setPartialParsing(boolean partialParsing){
        this.partialParsing = partialParsing;
    }
    
    public void setVersion(int value)
    {
        if (m_MVHDAtom != null)
            m_MVHDAtom.setVersion(value);
        java.util.Set<Long> keySet = m_dictTracks.keySet();
        Iterator keyItr = keySet.iterator();
        while (keyItr.hasNext()) {
            long key = (long) keyItr.next();
            Mp4AtomBase mp4AtomBase = m_dictTracks.get(key);
            mp4AtomBase.setVersion(value);
        }
        
//        for (KeyValuePair<Long, TRAKAtom> kv : (Iterable<KeyValuePair<Long,TRAKAtom>>) m_dictTracks)
//            kv.Value.setVersion(value);
        super.setVersion(value);
    }

    public boolean setMpegFileInfo(MpegFileInfo[] fileinfo)
    {
        fileinfo[0].CloneTo(m_Mp4FileInfo);            
        return false;
    }

    public MpegFileInfo getMpegFileInfo()
    {
        m_Mp4FileInfo.setM_fDurationSecs((float)m_Mp4FileInfo.getM_uiDuration() / (m_Mp4FileInfo.getM_uiTimeScale() & 0xFFFFFFFFL));
        return m_Mp4FileInfo;
    }

    public long getAtomSize()
    {
        super.setAtomSize(calcChildAtomsSize() + Utils.HEADER_LENGTH);
        return super.getAtomSize();
    }
    public void setAtomSize(long value)
    {
        super.setAtomSize(value);
    }

    public boolean beginWriting()
    {
//        DocumentBuilder documentBuilder = null;
        try {
//            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//            documentBuilder = documentBuilderFactory.newDocumentBuilder();
//            m_Mp4Infodoc = documentBuilder.newDocument();
//            m_Mp4Infodoc.loadXml(XMLDefines.MP_4_INFO_XML);
            Element rootElement = m_Mp4Infodoc.createElement(XMLDefines.MP_4_INFO_XML);
            rootElement.setAttribute(XMLDefines.ATTR_TARGET_FILE, m_sTargetFile);
            Element trackinfo = m_Mp4Infodoc.createElement("trackinfo"); // temporary only
            // Tracks
            java.util.Set keySet = m_dictTracks.keySet();
            Iterator keyItr = keySet.iterator();
            while(keyItr.hasNext()){
                Element trackElement = m_dictTracks.get(keyItr.next()).getTRACK_INFO_XML();
                trackinfo.appendChild(trackElement);
            }
//            for (KeyValuePair<Long, TRAKAtom> kv : (Iterable<KeyValuePair<Long,TRAKAtom>>) m_dictTracks)
//            {
//                trackinfo.InnerXml = kv.Value.getTRACK_INFO_XML();
//                m_Mp4Infodoc.getDocumentElement().appendChild(trackinfo.getFirstChild());                
//            }
            // Save information in .info file
            // It will write the info about tracks
            m_InfoWriter.write("<root>");
//            m_InfoWriter.writeLine();
            String xmlString = getStringFrXML(trackinfo,false);
            m_InfoWriter.write(xmlString);
//            m_InfoWriter.writeLine();
            return false;
        } catch (IOException ex) {
            printLog(LogType.error, "mp4editor.MOOVAtom | ", ex);
        } catch (Exception ex) {
            printLog(LogType.error, "mp4editor.MOOVAtom | ", ex);
        } 
        finally {
//            documentBuilder.close();
        }
        
        return false;
    }

    boolean getVideoTrackDuration(double[] dDurInSeconds) {
        dDurInSeconds[0] = 0.0;         
        m_dictTracks.get(m_uiVideoID).getTrackDuration(dDurInSeconds);        
        return false;
    }
    
    boolean getAudioTrackDuration(double[] dDurInSeconds) {
        dDurInSeconds[0] = 0.0;
        m_dictTracks.get(m_uiAudioID).getTrackDuration(dDurInSeconds);
        return false;
    }
    
    public boolean getMovieDuration(double[] dDurInSeconds)
    {
        dDurInSeconds[0] = 0.0;
        java.util.Set<Long> keySet = m_dictTracks.keySet();
        Iterator keyItr = keySet.iterator();
        while (keyItr.hasNext()) {
            String key = (String) keyItr.next();
            TRAKAtom mp4AtomBase = m_dictTracks.get(key);
            double dTrackDuration = 0.0;
            double[] referenceToDTrackDuration = { dTrackDuration };
            mp4AtomBase.getTrackDuration(referenceToDTrackDuration);
            dTrackDuration = referenceToDTrackDuration[0];
            dDurInSeconds[0] = Math.max(dDurInSeconds[0], dTrackDuration);
        }
//        for (KeyValuePair<Long, TRAKAtom> kv : (Iterable<KeyValuePair<Long,TRAKAtom>>) m_dictTracks)
//        {                
//            double dTrackDuration = 0.0;
//            double[] referenceToDTrackDuration = { dTrackDuration };
//            kv.Value.getTrackDuration(/*ref*/ referenceToDTrackDuration);
//            dTrackDuration = referenceToDTrackDuration[0];
//            dDurInSeconds[0] = Math.max(dDurInSeconds[0], dTrackDuration);
//        }
        return true;
    }

    public boolean endWriting()
    { 
        m_MVHDAtom = new MVHDAtom();
        RandomAccessFile referenceToM_BinWriter =  m_BinWriter;
        m_MVHDAtom.setWriter(referenceToM_BinWriter);
        m_MVHDAtom.m_Parent = this;
        m_MVHDAtom.setM_uiNextTrackId((long)(m_dictTracks.size() + 1));

        float fDuration = 0.0f;

        // Tracks
        super.endWriting();
        
        Set keySet = m_dictTracks.keySet();
            Iterator keyItr = keySet.iterator();
            while(keyItr.hasNext()){
                Object keyObj = keyItr.next();
                m_dictTracks.get(keyObj).endWriting();
                double[] fTrackDuration = new double[1];
                m_dictTracks.get(keyObj).getTrackDuration(fTrackDuration);
                fDuration = Math.max(fDuration, (float)fTrackDuration[0]);
            }
        
//        for (KeyValuePair<Long, TRAKAtom> kv : (Iterable<KeyValuePair<Long,TRAKAtom>>) m_dictTracks)
//        {                
//            kv.Value.endWriting();
//            fDuration = Math.max(fDuration, kv.Value.getTRACK_INFO().m_fDurationSecs);
//        }

        // MVHD Atom
        if (m_MVHDAtom != null)
        {                
            m_uiMOOVDuration = (((long)(fDuration * (m_uiMOOVTimeScale & 0xFFFFFFFFL))) & 0xFFFFFFFFL);
        }            
        return false;
    }

    public boolean pauseWriting()
    {
        // Reset these flags to remove orphaned frames
        m_bRemoveOrphanedFrames = true;
        m_iNonBFrameCount = 0;
        m_dbDroppedFramesDurationSecs = 0;
        java.util.Set keySet = m_dictTracks.keySet();
            Iterator keyItr = keySet.iterator();
            while(keyItr.hasNext()){
                Object keyObj = keyItr.next();
                m_dictTracks.get(keyObj).pauseWriting();
            }
//        for (KeyValuePair<Long, TRAKAtom> kv : (Iterable<KeyValuePair<Long,TRAKAtom>>) m_dictTracks)
//            kv.Value.pauseWriting();
        m_uiSampleCount = 0;
        return false;
    }

    public boolean endMediaInfo()
    {
        try {
            // Dump file info (Last gop info)
//            Element gopElement = m_Mp4Infodoc.createElement(XMLDefines.ELEMENT_GOPINFO);
            
            java.util.Set keySet = m_dictTracks.keySet();
            Iterator keyItr = keySet.iterator();
            while(keyItr.hasNext()){
                Object keyObj = keyItr.next();
                m_dictTracks.get(keyObj).endMediaInfo();
//                Element trackElement = m_dictTracks.get(keyObj).getTrackInfo(m_Mp4Infodoc);
//                if (trackElement != null)
//                    gopElement.appendChild(trackElement);               
            }
                //UpdateInfoFile();
            
    //        for (KeyValuePair<Long, TRAKAtom> kv : (Iterable<KeyValuePair<Long,TRAKAtom>>) m_dictTracks)
    //        {
    //            // End Media Info
    //            kv.Value.endMediaInfo();
    //
    //            XmlDocument[] referenceToM_Mp4Infodoc = { m_Mp4Infodoc };
    //            XmlElement trackElement = kv.Value.getTrackInfo(/*ref*/ referenceToM_Mp4Infodoc);
    //            m_Mp4Infodoc = referenceToM_Mp4Infodoc[0];
    //            if (trackElement != null)
    //        }
    //        }

            // Ending writing Info file
            m_InfoWriter.write("</root>");

            // Partial Gop is not inserted into disk
            //if (false)
            //{
            //    if (gopElement.FirstChild != null)
            //        m_Mp4Infodoc.DocumentElement.AppendChild(gopElement);
            //    m_Mp4Infodoc.Save(m_sMediaInfoFile);
            //}

            m_Mp4Infodoc = null;            
        } catch (IOException ex) {
            printLog(LogType.error, "mp4editor.MOOVAtom | ", ex);
            return false;
        } catch (Exception ex) {
            printLog(LogType.error, "mp4editor.MOOVAtom | ", ex);
            return false;
        }
        return true;
    }

    public long getSampleTimeSecToSampleNumber(double timeInSec, long trackID) {
        return m_dictTracks.get(trackID).getSampleTimeSecToSampleNumber(timeInSec);
    }
    
    public double getSampleNumberToSampleTimeSec(long sampleNumber, long trackID){
        return m_dictTracks.get(trackID).getSamoleNuberToSampleTimeSec(sampleNumber);
    }
    /*
    public boolean setGopInfo(NodeList listGops)
    {           
//        for (XmlNode gop : (Iterable<XmlNode>) listGops)
        
        for(int i=0; i<listGops.getLength();i++)
        {
            NodeList listTracks = listGops.item(i).getChildNodes();
            
            double dbAdjustment = 0.0;
            if(listTracks.getLength() > 1)
            {
                double dbAudioDur = m_dictTracks.get(m_uiAudioID).getTrackDurationSecs();
                double dbVideoDur = m_dictTracks.get(m_uiVideoID).getTrackDurationSecs();
                if((Math.abs(dbAudioDur - dbVideoDur) * 1000.0) > 250)
                    dbAdjustment = dbAudioDur - dbVideoDur;
            }
            
            //for (XmlElement track : (Iterable<XmlElement>) listTracks)
            for(int j=0;j<listTracks.getLength();j++)
            {
                Node trackNode = listTracks.item(j);
                        
                if (trackNode != null && trackNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element track = (Element) trackNode;
                        long uiTrackID = Long.parseLong(track.getAttribute(XMLDefines.ATTR_TRACK_ID));
                        m_dictTracks.get(uiTrackID).setTrackInfo(track,dbAdjustment);
                    }
                    }
                }
        return false;
    }
    * */
//
    public boolean generateAtom()
    {
        try {
            long lStart = m_BinWriter.getFilePointer();//getBaseStream().getPosition();           

            setAtomID("moov");
            super.generateAtom();
                                       
            RandomAccessFile referenceToM_BinWriter = m_BinWriter;
            m_MVHDAtom.setWriter(referenceToM_BinWriter);
            m_MVHDAtom.generateAtom();
            
            Set keySet = m_dictTracks.keySet();
            Iterator keyItr = keySet.iterator();
            while(keyItr.hasNext()){
                Object keyObj = keyItr.next();
                m_dictTracks.get(keyObj).setWriter(referenceToM_BinWriter);
                m_dictTracks.get(keyObj).generateAtom();
            }
//            for (KeyValuePair<Long, TRAKAtom> kv : (Iterable<KeyValuePair<Long,TRAKAtom>>) m_dictTracks)
//            {                
//                referenceToM_BinWriter[0] = m_BinWriter;
//                kv.Value.setWriter(/*ref*/ referenceToM_BinWriter);
//                m_BinWriter = referenceToM_BinWriter[0];
//                kv.Value.generateAtom();
//            }
                       
            
            long lStop = m_BinWriter.getFilePointer();//getBaseStream().getPosition();
            if ((lStop - lStart) != (long)getAtomSize()){
                //log.info("*****  Generation Error. Atom - {0}  ExpectedSize {1} ActualSize {2}"+getAtomID()+" | " +getAtomSize()+" | "+(lStop - lStart));
                printLog(LogType.information, "mp4editor.MOOVAtom | "+"*****  Generation Error. Atom - {0}  ExpectedSize {1} ActualSize {2}"+getAtomID()+" | " +getAtomSize()+" | "+(lStop - lStart), null);
            }

            m_MVHDAtom = null;
            return true;
        } catch (IOException ex) {
            printLog(LogType.error, "mp4editor.MOOVAtom | ", ex);
            return false;
        }
    }
//
    long calcChildAtomsSize()
    {
        long uiSize = 0;
        if (m_MVHDAtom != null)
            uiSize += m_MVHDAtom.getAtomSize();
        
        Set keySet = m_dictTracks.keySet();
            Iterator keyItr = keySet.iterator();
            while(keyItr.hasNext()){
                Object keyObj = keyItr.next();
                uiSize += m_dictTracks.get(keyObj).getAtomSize();
            }
//        for(KeyValuePair<Long, TRAKAtom> kv : (Iterable<KeyValuePair<Long, TRAKAtom>>) m_dictTracks)
//            uiSize += kv.Value.getAtomSize();

        return uiSize;
    }
//
//    byte[] getSDPPayload(/*uint*/long uiTrackID)
//    {
//        return m_dictTracks.get(uiTrackID).getSDPPayload();
//    }
//
    boolean addTrack(Element sTrackInfoXML) throws IOException
    {            
        TrackInfo tkInfo = new TrackInfo();
        CodecInfo codecInfo = new CodecInfo();
        TRAKAtom atomTrack = new TRAKAtom();
        atomTrack.addLoggingListener(logging);
        atomTrack.m_Parent = this;
        
        String sTrack = sTrackInfoXML.getAttribute(XMLDefines.ATTR_TRACK_TYPE);
        String sTrackID = sTrackInfoXML.getAttribute(XMLDefines.ATTR_TRACK_ID);
        long uiTrackID = 0;
        if (!Utils.stringNullCheck(sTrackID))
            uiTrackID = (((long)m_dictTracks.size()) & 0xFFFFFFFFL) + 1;
        else
            uiTrackID = Integer.parseInt(sTrackID);

        switch (sTrack)
        {
            case XMLDefines.VALUE_AUDIO_TYPE:
                {
                    atomTrack.setTRACK_TYPE("TRAK_AUDIO");
                    m_uiAudioID = uiTrackID;
                    TrackInfo[] referenceToTkInfo = { tkInfo };
                    CodecInfo[] referenceToCodecInfo = { codecInfo };
                    NodeList avc1cfgList = sTrackInfoXML.getChildNodes();
//                    System.out.println("Node Name :::: "+avc1cfgList.getLength());
//                    System.out.println("Node Name "+avc1cfgList.getNodeValue());
                    for(int j=0;j<avc1cfgList.getLength();j++){
                        Node avc1cfg = avc1cfgList.item(j);//.getChildNodes().item(0);
//                        Element firstChd = (Element)avc1cfg.getFirstChild();
//                        System.out.println("Type ::: "+avc1cfg.getNodeType());
                        if(avc1cfg !=null && avc1cfg.getNodeType() == Node.ELEMENT_NODE){
//                        System.out.println("FDFDFDFDFDFDDF ::::: KKKK "+avc1cfg.getNodeName());
                    loadTrackInfo(CodecTypeEnm.MP_4_A,
                                    (Element)avc1cfg,
                                     referenceToTkInfo,
                                    referenceToCodecInfo);
                    referenceToTkInfo[0].CloneTo(tkInfo);
                    referenceToCodecInfo[0].CloneTo(codecInfo);
                        }
                    }
                    
                    break;
                }
            case XMLDefines.VALUE_VIDEO_TYPE:
                {
                    atomTrack.setTRACK_TYPE("TRAK_VIDEO");
                    m_uiVideoID = uiTrackID;                        
                    TrackInfo[] referenceToTkInfo = { tkInfo };
                    CodecInfo[] referenceToCodecInfo = { codecInfo };
                    NodeList avc1cfgList = sTrackInfoXML.getChildNodes();
//                    System.out.println("Node Name :::: "+avc1cfgList.getLength());
//                    System.out.println("Node Name "+avc1cfgList.getNodeValue());
                    for(int j=0;j<avc1cfgList.getLength();j++){
                        Node avc1cfg = avc1cfgList.item(j);//.getChildNodes().item(0);
//                        Element firstChd = (Element)avc1cfg.getFirstChild();
//                        System.out.println("Type ::: "+avc1cfg.getNodeType());
                        if(avc1cfg !=null && avc1cfg.getNodeType() == Node.ELEMENT_NODE){
//                        System.out.println("FDFDFDFDFDFDDF ::::: KKKK "+avc1cfg.getNodeName());
                        loadTrackInfo(CodecTypeEnm.AVC_1,
                                        (Element)avc1cfg,
                                            referenceToTkInfo, 
                                            referenceToCodecInfo);
                        referenceToTkInfo[0].CloneTo(tkInfo);
                        referenceToCodecInfo[0].CloneTo(codecInfo);                        
                    }
                    }
                    break;
                }
        }

        tkInfo.setM_uiTrack_ID(uiTrackID);// = uiTrackID;
        sTrackInfoXML.setAttribute(XMLDefines.ATTR_TRACK_ID, Long.toString(uiTrackID));
        atomTrack.setTRACK_INFO_XML(sTrackInfoXML);
        atomTrack.setTRACK_INFO(tkInfo.Clone());
        atomTrack.setCODEC_INFO(codecInfo.Clone());
        m_dictTracks.put(tkInfo.getM_uiTrack_ID(), atomTrack);
        return false;
    }

    private AVC1Info getAVC1Info(Element avc1Element) throws IOException
    {
        AVC1Info infoAVC = new AVC1Info();

        infoAVC.setM_uiWidth(Integer.parseInt(avc1Element.getAttribute(XMLDefines.ATTR_VIDEO_WIDTH)));
        infoAVC.setM_uiHeight(Integer.parseInt(avc1Element.getAttribute(XMLDefines.ATTR_VIDEO_HEIGHT)));   
     
        //infoAVC.m_objAVCCInfo.m_AVCProfileIndication = System.Convert.ToByte(avc1Element.GetAttribute(XMLDefines.ATTR_H264_ProfileIndication));
        //infoAVC.m_objAVCCInfo.m_Profile_compatibility = System.Convert.ToByte(avc1Element.GetAttribute(XMLDefines.ATTR_H264_ProfileCompatibility));
        //infoAVC.m_objAVCCInfo.m_AVCLevelIndication = System.Convert.ToByte(avc1Element.GetAttribute(XMLDefines.ATTR_H264_LevelIndication));
        infoAVC.getM_objAVCCInfo().setM_AVCProfileIndication(Byte.parseByte(avc1Element.getAttribute(XMLDefines.ATTR_H_264_PROFILE_INDICATION)));//, NumberStyles.HEX_NUMBER);
        infoAVC.getM_objAVCCInfo().setM_Profile_compatibility(Byte.parseByte(avc1Element.getAttribute(XMLDefines.ATTR_H_264_PROFILE_COMPATIBILITY)));//, NumberStyles.HEX_NUMBER);
        infoAVC.getM_objAVCCInfo().setM_AVCLevelIndication(Byte.parseByte(avc1Element.getAttribute(XMLDefines.ATTR_H_264_LEVEL_INDICATION)));//, NumberStyles.HEX_NUMBER);
       
        infoAVC.getM_objAVCCInfo().setM_LenMinusOne(Byte.parseByte(avc1Element.getAttribute(XMLDefines.ATTR_H_264_LENGTH_MINUS_ONE)));

        // SPS nal unit
        NodeList nextNode = avc1Element.getElementsByTagName(XMLDefines.ELEMENT_SPS_NAL_UNIT);
        int arrayLength = 0;
        for(int i=0;i<nextNode.getLength(); i++){
            NodeList listSPSNodes = nextNode.item(i).getChildNodes();
            
            for(int j=0;j<listSPSNodes.getLength();j++){
                Node childNode = listSPSNodes.item(j);
                if(childNode != null && childNode.getNodeType() == Node.ELEMENT_NODE){
                    arrayLength++;
                }
            }
        }
            NALInfo[] nalInfoArr = new NALInfo[arrayLength];
            infoAVC.getM_objAVCCInfo().setM_sequenceParameterSetNALUnit(nalInfoArr);
        
//        Element spsElement = (Element) avc1Element.getElementsByTagName(XMLDefines.ELEMENT_SPS_NAL_UNIT);
//        Element spsElement = (Element)avc1Element.SelectSingleNode(XMLDefines.ELEMENT_SPS_NAL_UNIT);
        
        for(int j=0;j<nextNode.getLength(); j++){
        NodeList listSPSNodes = nextNode.item(j).getChildNodes();
        int iIndex = 0;
//        for (XmlElement element : (Iterable<XmlElement>) listSPSNodes)
        for(int i=0;i<listSPSNodes.getLength();i++)
        {
            Node listNodes = listSPSNodes.item(i);
            if (listNodes != null && listNodes.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) listNodes;
                NALInfo nalInfo = new NALInfo();
                nalInfo.setM_data(Base64.decodeBase64(element.getTextContent().getBytes()));
                infoAVC.getM_objAVCCInfo().getM_sequenceParameterSetNALUnit()[iIndex] = nalInfo;
                infoAVC.getM_objAVCCInfo().getM_sequenceParameterSetNALUnit()[iIndex].setM_uiLength((long) infoAVC.getM_objAVCCInfo().getM_sequenceParameterSetNALUnit()[0].getM_data().length);
                iIndex++;
            }
        }
    }

        // PPS nal unit
        NodeList ppsElement = avc1Element.getElementsByTagName(XMLDefines.ELEMENT_PPS_NAL_UNIT);
        int arrlgth = 0;
        for (int i = 0; i < ppsElement.getLength(); i++) {
            NodeList childNodes = ppsElement.item(i).getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node childNode = childNodes.item(j);
                if (childNode != null && childNode.getNodeType() == Node.ELEMENT_NODE) {
                    arrlgth++;
                }
            }
        }
        
        NALInfo[] ppsnalunitNLInfo = new NALInfo[arrlgth];
        
        for (int i = 0; i < ppsElement.getLength(); i++) {
            int iIndex = 0;
            NodeList childNodes = ppsElement.item(i).getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node childNode = childNodes.item(j);
                if (childNode != null && childNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element)childNode;
                    infoAVC.getM_objAVCCInfo().setM_pictureParameterSetNALUnit(ppsnalunitNLInfo);// = msArray.createArrayWithInitialization( NALInfo.class, ppsElement.ChildNodes.Count);
//                    NodeList listPPSNodes = ppsElement.getChildNodes();
//                    iIndex = 0;
//                    for (Element element : (Iterable<Element>) listPPSNodes) {
                        NALInfo nalInfo = new NALInfo();
                        nalInfo.setM_data(Base64.decodeBase64(element.getTextContent().getBytes()));
                        infoAVC.getM_objAVCCInfo().getM_pictureParameterSetNALUnit()[iIndex] = nalInfo;
                        infoAVC.getM_objAVCCInfo().getM_pictureParameterSetNALUnit()[iIndex].setM_uiLength((long) infoAVC.getM_objAVCCInfo().getM_pictureParameterSetNALUnit()[0].getM_data().length);
                        iIndex++;
//                    }
                }
            }
       
        }
        return infoAVC;
    }

    private MP4AInfo getMP4AInfo(Element mp4aElement) throws IOException
    {
        MP4AInfo infoMP4A = new MP4AInfo();
        infoMP4A.setM_uiChannels(Long.parseLong(mp4aElement.getAttribute(XMLDefines.ATTR_AUDIO_CHANNELS)));
        infoMP4A.setM_uiSampleSize(Long.parseLong(mp4aElement.getAttribute(XMLDefines.ATTR_AUDIO_SAMPLE_SIZE)));
        infoMP4A.setM_uiSampleRate(Long.parseLong(mp4aElement.getAttribute(XMLDefines.ATTR_AUDIO_SAMPLE_RATE)));
        infoMP4A.setM_arrDecoderCfgData(Base64.decodeBase64(mp4aElement.getAttribute(XMLDefines.ATTR_AUDIO_DECODER_CFG_DATA).getBytes()));
        return infoMP4A;
    }

    private String getStringFrXML(Element gopElement, boolean fromChild) throws TransformerException, TransformerFactoryConfigurationError, TransformerConfigurationException, IllegalArgumentException {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();
        transformer.setOutputProperty("indent", "yes");
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source = null;
        if(fromChild){
        NodeList nl = gopElement.getChildNodes();
        for (int x = 0; x < nl.getLength(); x++) {
            Node e = nl.item(x);
            if (e instanceof Element) {
                source = new DOMSource(e);
                break;
            }
        }
        }else{
            source = new DOMSource(gopElement);
        }
        //Do the transformation and output
        transformer.transform(source, result);
        String xmlString = sw.toString();
        xmlString = xmlString.substring(xmlString.indexOf("?>")+2);
//        System.out.println("XML File "+xmlString);
        return xmlString;
    }

    private boolean loadTrackInfo(CodecTypeEnm type, Element codecElement, TrackInfo[] info, CodecInfo[] codec) throws IOException
    {
        if (type == CodecTypeEnm.AVC_1)
        {
            codec[0].setM_codec(CodecType.AVC_1);
            codec[0].setM_codecInfo(new AVC1Info());
            getAVC1Info(codecElement).CloneTo((AVC1Info)codec[0].getM_codecInfo());
            float fFrameRate = Float.parseFloat(codecElement.getAttribute(XMLDefines.ATTR_VIDEO_FRAME_RATE));
            info[0].setM_uiTimeScale((long)(fFrameRate * 1000.0));
            info[0].setM_uiSampleDuration((long)(1.0 / fFrameRate * (info[0].getM_uiTimeScale() & 0xFFFFFFFFL)));
        }
        else if (type == CodecTypeEnm.MP_4_A)
        {
            codec[0].setM_codec(CodecType.MP_4_A);
            codec[0].setM_codecInfo(new MP4AInfo());
            getMP4AInfo(codecElement).CloneTo((MP4AInfo)codec[0].getM_codecInfo());
            long uiSampleDuration = Long.parseLong(codecElement.getAttribute(XMLDefines.ATTR_AUDIO_BLOCK_SIZE));
            info[0].setM_uiTimeScale(((MP4AInfo)codec[0].getM_codecInfo()).getM_uiSampleRate());
            info[0].setM_uiSampleDuration(uiSampleDuration);
        }
        else
            return true;

        return false;
    }
//
    boolean addTrack(String type, TrackInfo tkInfo, CodecInfo codecInfo)
    {
        TRAKAtom atomTrack = new TRAKAtom();
        atomTrack.m_Parent = this;
        atomTrack.setTRACK_TYPE(type);
        switch (type)
        {
            case "TRAK_VIDEO":
                {
                    m_uiVideoID = (((/*uint*/long)m_dictTracks.size()) & 0xFFFFFFFFL) + 1;
                    tkInfo.setM_uiTrack_ID(m_uiVideoID);
                    break;
                }

            case "TRAK_AUDIO":
                {
                    m_uiAudioID = (((/*uint*/long)m_dictTracks.size()) & 0xFFFFFFFFL) + 1;
                    tkInfo.setM_uiTrack_ID(m_uiAudioID);
                    break;
                }
        }

        atomTrack.setTRACK_INFO(tkInfo.Clone());
        atomTrack.setCODEC_INFO(codecInfo.Clone());
        m_dictTracks.put(tkInfo.getM_uiTrack_ID(), atomTrack);            
        return false;
    }
//
    public FrameTypeEnm getFrameType(MP4Buffer buffer) throws Exception{
        FrameTypeEnm[] type = null;
        if (buffer.getType() == BuffTypeEnm.VIDEO)
            {
                type = new FrameTypeEnm[1];
                type[0] = FrameTypeEnm.None;
                if(m_iNalHdrLen == -1)
                {
                   int iLenMinus1 = ((AVC1Info)( m_dictTracks.get(m_uiVideoID).getCODEC_INFO().getM_codecInfo())).m_objAVCCInfo.m_LenMinusOne;
                   m_iNalHdrLen = iLenMinus1 + 1;
                }
                Utils.parseForFrameType(buffer.getData(),buffer.getiDataLength(),m_iNalHdrLen,type);
            }
        return type[0];
    }
    
    long AddMP4Buffer(MP4Buffer buffer) throws Exception{
        
            long uiDataWritten = 0;
            if (buffer.getType() == BuffTypeEnm.VIDEO)
            {                
                FrameTypeEnm[] type = new FrameTypeEnm[1];
                type[0] = FrameTypeEnm.None;
                if(m_iNalHdrLen == -1)
                {
                   int iLenMinus1 = ((AVC1Info)( m_dictTracks.get(m_uiVideoID).getCODEC_INFO().getM_codecInfo())).m_objAVCCInfo.m_LenMinusOne;
                   m_iNalHdrLen = iLenMinus1 + 1;
                }
                Utils.parseForFrameType(buffer.getData(),buffer.getiDataLength(),m_iNalHdrLen,type);
                if (type[0] == FrameTypeEnm.None || type[0] == FrameTypeEnm.PPSFrame || type[0] == FrameTypeEnm.SEIFrame || type[0] == FrameTypeEnm.SPSFrame)
                    return 0;

                int iFrames = 0;
                // Store B frames
                if (type[0] == FrameTypeEnm.BFrame)
                {             
                    // Remove orphaned frames
                    if(m_bRemoveOrphanedFrames)
                    {
                        if(m_iNonBFrameCount > 1)
                        {
                            // Now all orphaned frames are removed , safe to write b frames
                            m_bRemoveOrphanedFrames = false;
                            m_iNonBFrameCount = 0;
                        }
                        else
                        {
                            bFrameDropedCnt++;
                            m_dbDroppedFramesDurationSecs += buffer.getSampleDelta();
                            return 0;
                        }
                    }
                    
                    m_uiBFrameCount++;                    
                }
                else if (type[0] == FrameTypeEnm.IDRFrame || type[0] == FrameTypeEnm.IFrame || type[0] == FrameTypeEnm.PFrame)
                {
                    iFrames = m_uiBFrameCount + 1;
                    m_uiBFrameCount = 0;
                }
            
                if(m_bRemoveOrphanedFrames)
                    m_iNonBFrameCount++;
                
                if(m_dbDroppedFramesDurationSecs > 0)
                {
                    // Adjust the dropped frames duration
                    double dbSampleDuration = buffer.getSampleDelta();
                    double dbAdjustedDuration = dbSampleDuration / 2.0;
                    buffer.setSampleDelta(dbSampleDuration + dbAdjustedDuration);
                    m_dbDroppedFramesDurationSecs -= dbAdjustedDuration;
                }
                
                // Add current buffer
                long[] uiChunkOffset = new long[1];
                boolean[] bNewChunk = new boolean[1];
                uiChunkOffset[0] = 0;
                bNewChunk[0] = false;
                boolean bIFrame = false;

                // Add current buffer
                AddDatatoFile(buffer.getType(), buffer.getData(), buffer.getiDataLength(), bNewChunk, uiChunkOffset);

                // Check for iFrame
                if (type[0] == FrameTypeEnm.IDRFrame || type[0] == FrameTypeEnm.IFrame)
                {                      
                    // Force new chunk for new gop
                    bIFrame = true;
                    bNewChunk[0] = true;
                    m_uiSampleCount = 0;
                }

                if(buffer.isbHasCompTime() == true)
                {
                    UpdateMetaInfo(buffer.getType(), buffer.getiDataLength(), bNewChunk[0], uiChunkOffset[0], bIFrame,buffer.getSampleDelta(), buffer.getfCompTimeTimeSecs());
                }
                else
                {
                    UpdateMetaInfo(buffer.getType(), buffer.getiDataLength(), bNewChunk[0], uiChunkOffset[0], bIFrame, buffer.isbReorder(),buffer.getSampleDelta(), iFrames);
                }
                
                uiDataWritten += (long)buffer.getiDataLength();
            }
            else 
            {
                if(bFrameDropedCnt>0){
                  bFrameDropedCnt--;   
                }else{
                    addBuffer(buffer.getType(), buffer.getData(), buffer.getiDataLength(), buffer.getSampleDelta());
                }
                uiDataWritten = (long)buffer.getiDataLength();
            }           
            return uiDataWritten;
    }
    
     private boolean UpdateMetaInfo(BuffTypeEnm type, int iDataLen, boolean bNewChunk, long uiChunkOffset, boolean bIFrame,boolean bReorder,double sampleDelta,int iFrames) throws Exception
        {
            switch (type)
            {
                case VIDEO:
                    {
                        if (m_dictTracks.containsKey(m_uiVideoID))
                            m_dictTracks.get(m_uiVideoID).addSample(bNewChunk, uiChunkOffset, (long)iDataLen, bIFrame, bReorder,sampleDelta, iFrames);
                        else
                        {
                            String sMsg = String.format("MOOVAtom::UpdateMetaInfo , No video track is created. TrackInfoXML - {0}", m_sTrackInfoXML);
                            //log.error(sMsg);
                            throw new Exception(String.format("No video track is created. TrackInfoXML - {0}", m_sTrackInfoXML));
                        }
                        break;
                    }

                case AUDIO:
                    {
                        if (m_dictTracks.containsKey(m_uiAudioID))
                            m_dictTracks.get(m_uiAudioID).addSample(bNewChunk, uiChunkOffset, (long)iDataLen, sampleDelta, false);
                        else
                        {
                            String sMsg = String.format("MOOVAtom::UpdateMetaInfo , No Audio track is created. TrackInfoXML - {0}", m_sTrackInfoXML);
                            //log.error(sMsg);
                            throw new Exception(String.format("No Audio track is created. TrackInfoXML - {0}", m_sTrackInfoXML));
                        }
                        break;
                    }
            }
            return false;
        }
    
    private boolean UpdateMetaInfo(BuffTypeEnm type, int iDataLen, boolean bNewChunk, long uiChunkOffset, boolean bIFrame,double sampleDelta,double samplePresentationOffset) throws Exception
        {
            switch (type)
            {
                case VIDEO:
                    {
                        if (m_dictTracks.containsKey(m_uiVideoID))
                            m_dictTracks.get(m_uiVideoID).addSample(bNewChunk, uiChunkOffset, (long)iDataLen, bIFrame, sampleDelta, samplePresentationOffset);
                        else
                        {
                            String sMsg = String.format("MOOVAtom::UpdateMetaInfo , No video track is created. TrackInfoXML - {0}", m_sTrackInfoXML);
                            //log.error(sMsg);
                            throw new Exception(String.format("No video track is created. TrackInfoXML - {0}", m_sTrackInfoXML));
                        }
                        break;
                    }

                case AUDIO:
                    {
                        if (m_dictTracks.containsKey(m_uiAudioID))
                            m_dictTracks.get(m_uiAudioID).addSample(bNewChunk, uiChunkOffset, (long)iDataLen, sampleDelta, false);
                        else
                        {
                            String sMsg = String.format("MOOVAtom::UpdateMetaInfo , No Audio track is created. TrackInfoXML - {0}", m_sTrackInfoXML);
                            //log.error(sMsg);
                            throw new Exception(String.format("No Audio track is created. TrackInfoXML - {0}", m_sTrackInfoXML));
                        }
                        break;
                    }
            }
            return false;
        }
    
    /*private boolean UpdateInfoFile() throws TransformerException, IOException
        {
            Element gopElement = m_Mp4Infodoc.createElement(XMLDefines.ELEMENT_GOPINFO);
            java.util.Set keySet = m_dictTracks.keySet();
                Iterator keyItr = keySet.iterator();
                while(keyItr.hasNext()){
//                    Document[] referenceToM_Mp4Infodoc = { m_Mp4Infodoc };
                    Element trackElement = m_dictTracks.get(keyItr.next()).getTrackInfo(m_Mp4Infodoc);
                    if (trackElement != null)                    
                        gopElement.appendChild(trackElement);
                }
            
////            XmlElement gopElement = m_Mp4Infodoc.CreateElement(XMLDefines.ELEMENT_GOPINFO);
//            foreach (KeyValuePair<uint, TRAKAtom> kp in m_dictTracks)
//            {
//                XmlElement trackElement = kp.Value.GetTrackInfo(ref m_Mp4Infodoc);
//                if (trackElement != null)
//                    gopElement.AppendChild(trackElement);
//            }

            // It will write the gopinfo to file
            if (gopElement.getFirstChild() != null) {
                String xmlString = getStringFrXML(gopElement, false);
                m_InfoWriter.write(xmlString);

//                m_InfoWriter.Write(gopElement.OuterXml);
//                m_InfoWriter.WriteLine();
            }

            //if (gopElement.FirstChild != null)
            //    m_Mp4Infodoc.DocumentElement.AppendChild(gopElement);
            //m_Mp4Infodoc.Save(m_sMediaInfoFile);

            return false;
        }
    */
    
    private boolean AddDatatoFile(BuffTypeEnm type, byte[] buf, int iDataLen, boolean[] bNewChunk, long[] uiChunkOffset) throws IOException
        {            
            bNewChunk[0] = false;
            synchronized (m_BinWriter)
            {
                uiChunkOffset[0] = (long)m_BinWriter.getFilePointer();//BaseStream.Position;
                if (m_PrevType != type)
                {
                    m_PrevType = type;
                    m_uiSampleCount = 0;
                }

                if (m_uiSampleCount % 10 == 0)
                    bNewChunk[0] = true;

                m_uiSampleCount++;

                // Write Buffer
                m_BinWriter.write(buf, 0, iDataLen);
            }
            return false;
        }
    
    boolean addBuffer(BuffTypeEnm type, byte[] buf, int iDataLen, double sampleDelta) throws IOException,Exception
    {
            long[] uiChunkOffset = new long[1];
            boolean[] bNewChunk = new boolean[1];
            uiChunkOffset[0] = 0;
            bNewChunk[0] = false;
            boolean[] bIFrame = new boolean[1];

            // Add data to file
            AddDatatoFile(type, buf, iDataLen, bNewChunk, uiChunkOffset);

            // Update info file on I frames
            if (type == BuffTypeEnm.VIDEO)
            {
                if(m_iNalHdrLen == -1)
                {
                   int iLenMinus1 = ((AVC1Info)( m_dictTracks.get(m_uiVideoID).getCODEC_INFO().getM_codecInfo())).m_objAVCCInfo.m_LenMinusOne;
                   m_iNalHdrLen = iLenMinus1 + 1;
                }
                
                Utils.parseForNalUnits(buf, iDataLen,m_iNalHdrLen, bIFrame);
                if (bIFrame[0]) // dump Gop info to file
                {
                    // Update Info File
                    //UpdateInfoFile();

                    // Force new chunk for new gop
                    bNewChunk[0] = true;
                    m_uiSampleCount = 0;
                }
            }

            // Update Meta info
            updateMetaInfo(type, iDataLen, bNewChunk[0], uiChunkOffset[0],sampleDelta, m_bFirstFrame);
           
            return false;
    }

    private boolean updateMetaInfo(BuffTypeEnm type, int iDataLen, boolean bNewChunk, long uiChunkOffset, double sampleDelta, boolean bIFrame) throws Exception
        {
            switch (type)
            {
                case VIDEO:
                    {
                        if (m_dictTracks.containsKey(m_uiVideoID))
                            m_dictTracks.get(m_uiVideoID).addSample(bNewChunk, uiChunkOffset, (long) iDataLen, sampleDelta, bIFrame);
                        else
                        {
                            String sMsg = String.format("MOOVAtom::UpdateMetaInfo , No video track is created. TrackInfoXML - {0}", m_sTrackInfoXML);
                            //log.error(sMsg);
                            throw new Exception(String.format("No video track is created. TrackInfoXML - {0}", m_sTrackInfoXML));
                        }
                        break;
                    }

                case AUDIO:
                    {
                        if (m_dictTracks.containsKey(m_uiAudioID))
                            m_dictTracks.get(m_uiAudioID).addSample(bNewChunk, uiChunkOffset, (long)iDataLen, sampleDelta, false);
                        else
                        {
                            String sMsg = String.format("MOOVAtom::UpdateMetaInfo , No Audio track is created. TrackInfoXML - {0}", m_sTrackInfoXML);
                            //log.error(sMsg);
                            throw new Exception(String.format("No Audio track is created. TrackInfoXML - {0}", m_sTrackInfoXML));
                        }
                        break;
                    }
            }
            return false;
        }
    
    long getAudioTrackID()
    {
        return m_uiAudioID;
    }

    /*uint*/long getVideoTrackID()
    {
        return m_uiVideoID;
    }

//    float getSampleDurationInSecs(/*uint*/long uiTrackID)
//    {
//        return m_dictTracks.get(uiTrackID).getSampleDurationInSecs();  
//    }
//
//
//    boolean getMovieDuration(/*ref*/ /*uint*/long[] uiTimeScale, /*ref*/ /*UInt64*/long[] uiDuration)
//    {
//        uiTimeScale[0] = m_Mp4FileInfo.m_uiTimeScale;
//        uiDuration[0] = m_Mp4FileInfo.m_uiDuration;
//        return false;
//    }

    boolean getMovieTracksCount(/*ref*/ /*uint*/long[] uiTrackCount)
    {
        uiTrackCount[0] = (/*uint*/long)m_dictTracks.size();
        return false;
    }

//    boolean getTrackType(/*uint*/long uiTrackID, /*ref*/ /*TrackType*/int[] type)
//    {
//        type[0] = m_dictTracks.get(uiTrackID).getTRACK_TYPE();
//        return false;
//    }
//
//    boolean getSampleCount(/*uint*/long uiTrackID, /*ref*/ /*uint*/long[] uiSampleCount)
//    {
//        return m_dictTracks.get(uiTrackID).getSampleCount(/*ref*/ uiSampleCount);            
//    }
//
//    boolean getTrackDuration(/*uint*/long uiTrackID, /*ref*/ /*uint*/long[] uiTimeScale, /*ref*/ /*UInt64*/long[] uiDuration)
//    {
//        return m_dictTracks.get(uiTrackID).getTrackDuration(/*ref*/ uiTimeScale, /*ref*/ uiDuration);  
//    }
//
    boolean getIFrame(long uiTrackID, long uiSampleNo, IFrameSearchEnm search, long[] uiISample)
    {
        return m_dictTracks.get(uiTrackID).getIFrame(uiSampleNo, search, uiISample);  
    }
//
    
    public byte[] GetAACFrameHeader(short nFrameLength)
        {
            byte[] aacFrame = null;
            try
            {
//                MpegFileInfo info = getMpegFileInfo();
                

                aacFrame = new byte[7];

                //A 12 syncword 0xFFF, all bits must be 1
                //B 1 MPEG Version: 0 for MPEG-4, 1 for MPEG-2
                //C 2 Layer: always 0
                //D 1 protection absent, Warning, set to 1 if there is no CRC and 0 if there is CRC
                //E 2 profile, the MPEG-4 Audio Object Type minus 1
                //F 4 MPEG-4 Sampling Frequency Index (15 is forbidden)
                //G 1 private bit, guaranteed never to be used by MPEG, set to 0 when encoding, ignore when decoding
                //H 3 MPEG-4 Channel Configuration (in the case of 0, the channel configuration is sent via an inband PCE)
                //I 1 originality, set to 0 when encoding, ignore when decoding
                //J 1 home, set to 0 when encoding, ignore when decoding
                //K 1 copyrighted id bit, the next bit of a centrally registered copyright identifier, set to 0 when encoding, ignore when decoding
                //L 1 copyright id start, signals that this frame's copyright id bit is the first bit of the copyright id, set to 0 when encoding, ignore when decoding
                //M 13 frame length, this value must include 7 or 9 bytes of header length: FrameLength = (ProtectionAbsent == 1 ? 7 : 9) + size(AACFrame)
                //O 11 Buffer fullness
                //P 2 Number of AAC frames (RDBs) in ADTS frame minus 1, for maximum compatibility always use 1 AAC frame per ADTS frame
                               
                aacFrame[0] = (byte) 0xFF;                
                aacFrame[1] = (byte) 0xF1;
                
                short nProfileId = 01;
                
               int lSampleRate = (int)((MP4AInfo) m_dictTracks.get(m_uiAudioID).getCODEC_INFO().getM_codecInfo()).m_uiSampleRate;
               long lChannels = ((MP4AInfo) m_dictTracks.get(m_uiAudioID).getCODEC_INFO().getM_codecInfo()).m_uiChannels;
               
                aacFrame[2] |= (byte)(nProfileId << 6); // profile value 01
                aacFrame[2] |= (byte)((GetSampleFreqIndex(lSampleRate) & 0x0F) << 2);

                short nChannels = (short)(lChannels);
                //These are the channel configurations:
                //0: Defined in AOT Specifc Configin
                //1: 1 channel: front-center
                //2: 2 channels: front-left, front-right
                //3: 3 channels: front-center, front-left, front-right
                //4: 4 channels: front-center, front-left, front-right, back-center
                //5: 5 channels: front-center, front-left, front-right, back-left, back-right
                //6: 6 channels: front-center, front-left, front-right, back-left, back-right, LFE-channel
                //7: 8 channels: front-center, front-left, front-right, side-left, side-right, back-left, back-right, LFE-channel
                //8-15: Reserved
                aacFrame[2] |= (byte)((nChannels >> 2) & 0x01);
                aacFrame[3] |= (byte)((nChannels & 0x03) << 6);

                short nFrameLen = (short)(nFrameLength + 7);
                aacFrame[3] |= (byte)(nFrameLen >> 11 & 0x02);
                aacFrame[4] |= (byte)((nFrameLen >> 3) & 0xFF);
                aacFrame[5] |= (byte)((nFrameLen & 0x07) << 5);

                aacFrame[5] |= 0x1F;
                aacFrame[6] |= 0xFC;
            } catch(Throwable th){
                th.printStackTrace();
            }
            return aacFrame;
        }


        short GetSampleFreqIndex(int iSampleRate)
        {
            switch (iSampleRate)
            {
                case 96000:
                    return 0;
                case 88200:
                    return 1;
                case 64000:
                    return 2;
                case 48000:
                    return 3;
                case 44100:
                    return 4;
                case 32000:
                    return 5;
                case 24000:
                    return 6;
                case 22050:
                    return 7;
                case 16000:
                    return 8;
                case 12000:
                    return 9;
                case 11025:
                    return 10;
                case 8000:
                    return 11;
                case 7350:
                    return 12;
                default:
                    return 15;
            }
        }
    
    public byte[] GetAudioSampleWthADTS(long uiTrackID, long uiSampleNumber, byte[] arrData,double[] sampleDelta, String[] sError) throws Exception{
       long uiFileOffset = 0;
        long uiSampleSize = 0;
//        arrData[0] = null;            
        
        long[] referenceToUiFileOffset = { uiFileOffset };
        long[] referenceToUiSampleSize = { uiSampleSize };
        boolean outRefCondition0 = m_dictTracks.get(uiTrackID).getTrackSampleInfo(uiSampleNumber, referenceToUiFileOffset, referenceToUiSampleSize, sampleDelta, sError);
        byte[] aacHeader = GetAACFrameHeader((short)referenceToUiSampleSize[0]);
        uiFileOffset = referenceToUiFileOffset[0];
        uiSampleSize = referenceToUiSampleSize[0] + aacHeader.length; // +7 ADTS header
        if (outRefCondition0)
            return arrData;

        synchronized (m_BinReader)
        {            
            arrData = new byte[(int)uiSampleSize];
            for(int iIndex = 0; iIndex < aacHeader.length;iIndex++)
                arrData[iIndex] = aacHeader[iIndex];
            
            m_BinReader.seek((long)uiFileOffset);
            int iRead = m_BinReader.read(arrData, aacHeader.length,(int)(uiSampleSize-aacHeader.length & 0xFFFFFFFFL));
            if (iRead != (uiSampleSize & 0xFFFFFFFFL))
            {
                String errorMsg = "Failed to read required data from file. DataToRead - {0} , DataRead - {1} , CurrentOffset - {2} , FileLength - {3}"+uiSampleSize+" | "+iRead+" | "+ m_BinReader.getFilePointer()+" | "+m_BinReader.length();
                sError[0] = errorMsg;
                //log.error(errorMsg);
                return arrData;
            }
        }
        
        return arrData;
    }    
    
    byte[] getTrackSample(long uiTrackID, long uiSampleNumber, byte[] arrData, double[] sampleDelta, String[] sError) throws Exception
    {
        long uiFileOffset = 0;
        long uiSampleSize = 0;
//        arrData[0] = null;            
        long[] referenceToUiFileOffset = { uiFileOffset };
        long[] referenceToUiSampleSize = { uiSampleSize };
        boolean outRefCondition0 = m_dictTracks.get(uiTrackID).getTrackSampleInfo(uiSampleNumber, referenceToUiFileOffset, referenceToUiSampleSize, sampleDelta, sError);
        uiFileOffset = referenceToUiFileOffset[0];
        uiSampleSize = referenceToUiSampleSize[0];
        if (outRefCondition0)
            return arrData;

        synchronized (m_BinReader)
        {
            arrData = new byte[(int)uiSampleSize];
            m_BinReader.seek((long)uiFileOffset);
            int iRead = m_BinReader.read(arrData, 0,(int)(uiSampleSize & 0xFFFFFFFFL));
            if (iRead != (uiSampleSize & 0xFFFFFFFFL))
            {
                String errorMsg = "Failed to read required data from file. DataToRead - {0} , DataRead - {1} , CurrentOffset - {2} , FileLength - {3}"+uiSampleSize+" | "+iRead+" | "+ m_BinReader.getFilePointer()+" | "+m_BinReader.length();
                sError[0] = errorMsg;
                //log.error(errorMsg);
                return arrData;
            }
        }
        
        return arrData;
    }
    
    long getTotalTrackSamples(long uiTrackID) throws Exception
    {
        return m_dictTracks.get(uiTrackID).getTotalTrackSamples();
    }
    
    
     byte[] getTrackSample(long uiTrackID, long uiSampleNumber, byte[] arrData, double[] sampleDelta,double[] samplePresentationOffset,  String[] sError) throws Exception
    {
        long uiFileOffset = 0;
        long uiSampleSize = 0;
//        arrData[0] = null;            
        long[] referenceToUiFileOffset = { uiFileOffset };
        long[] referenceToUiSampleSize = { uiSampleSize };
        boolean outRefCondition0 = m_dictTracks.get(uiTrackID).getTrackSampleInfo(uiSampleNumber, referenceToUiFileOffset, referenceToUiSampleSize, sampleDelta,samplePresentationOffset, sError);
        uiFileOffset = referenceToUiFileOffset[0];
        uiSampleSize = referenceToUiSampleSize[0];
        if (outRefCondition0)
            return arrData;

        synchronized (m_BinReader)
        {
            arrData = new byte[(int)uiSampleSize];
            m_BinReader.seek((long)uiFileOffset);
            int iRead = m_BinReader.read(arrData, 0,(int)(uiSampleSize & 0xFFFFFFFFL));
            if (iRead != (uiSampleSize & 0xFFFFFFFFL))
            {
                String errorMsg = "Failed to read required data from file. DataToRead - {0} , DataRead - {1} , CurrentOffset - {2} , FileLength - {3}"+uiSampleSize+" | "+iRead+" | "+ m_BinReader.getFilePointer()+" | "+m_BinReader.length();
                sError[0] = errorMsg;
                //log.error(errorMsg);
                return arrData;
            }
        }
        
        return arrData;
    }
//
//    RTPPacket[] getRtpPackets(/*uint*/long uiTrackID, byte[] arrSampleData)
//    {            
//        RTPSample sample = null;
//
//        TRAKAtom trak = m_dictTracks.get(uiTrackID);
//        sample = new RTPSample(trak);
//        sample.process(arrSampleData);
//
//        return sample.getPackets();        
//    }
//
//    RTPSample getRtpSample(/*uint*/long uiTrackID, byte[] arrSampleData)
//    {
//        RTPSample sample = null;
//        
//        TRAKAtom trak = m_dictTracks.get(uiTrackID);
//        sample = new RTPSample(trak);
//        sample.process(arrSampleData);
//
//        return sample;
//    }
//
    public boolean parseAtoms(long uiFileStart, long uiAtomSize)
    {
        super.parseAtoms(uiFileStart, uiAtomSize);

        long uiFileOffset = getFileStart() + Utils.HEADER_LENGTH;
        long uiAtomEnd = getFileEnd();
        long uiAtomLength = 0;
        char[] Atom_ID = null;

        while (uiFileOffset < uiAtomEnd)
        {
            try {
                RandomAccessFile referenceToM_BinReader = m_BinReader ;
                byte[] MpegBoxBuff = Utils.readBytes(referenceToM_BinReader, uiFileOffset, (int)Utils.HEADER_LENGTH);
//                m_BinReader = referenceToM_BinReader;                
                long[] referenceToUiAtomLength = { uiAtomLength };
                char[][] referenceToAtom_ID = { Atom_ID };
                Utils.readAtom(MpegBoxBuff,referenceToUiAtomLength,referenceToAtom_ID);
                uiAtomLength = referenceToUiAtomLength[0];
                Atom_ID = referenceToAtom_ID[0];
                String sAtomID = new String(Atom_ID);
                sAtomID = sAtomID.toLowerCase();
                //log.info("  MOOV-Parser  AtomID = {"+sAtomID+"} AtomLength = {"+uiAtomLength+"}");
                printLog(LogType.information, "mp4editor.MOOVAtom | "+"  MOOV-Parser  AtomID = {"+sAtomID+"} AtomLength = {"+uiAtomLength+"}", null);
                switch (sAtomID)
                {
                    case "mvhd":
                        {
                            m_MVHDAtom = new MVHDAtom();
                            m_MVHDAtom.addLoggingListener(logging);
                            m_MVHDAtom.setAtomID(sAtomID);
                            m_MVHDAtom.setReader(referenceToM_BinReader);
                            m_MVHDAtom.m_Parent = this;
                            m_MVHDAtom.parseAtoms(uiFileOffset, uiAtomLength);
                            break;
                        }

                    case "trak":
                        {                                                     
                            TRAKAtom trkAtom = new TRAKAtom();
                            trkAtom.addLoggingListener(logging);
                            trkAtom.setAtomID(sAtomID);
                            trkAtom.setReader(referenceToM_BinReader);
                            trkAtom.m_Parent = this;
                            trkAtom.setIsPartialParsing(partialParsing);
                            trkAtom.parseAtoms(uiFileOffset, uiAtomLength);
                            m_dictTracks.put(trkAtom.getM_TrackInfo().getM_uiTrack_ID(), trkAtom);
                            if(trkAtom.getM_TrackInfo().getM_uiSampleCount() > 0)
                                trkAtom.getM_TrackInfo().setM_uiSampleDuration((long)(trkAtom.getM_TrackInfo().getM_uiDuration()) / (trkAtom.getM_TrackInfo().getM_uiSampleCount() & 0xFFFFFFFFL));
                                       
                            switch (trkAtom.getTRACK_TYPE())
                            {
                                case "TRAK_AUDIO":
                                    {
                                        trkAtom.getM_TrackInfo().setM_fDurationSecs((float)trkAtom.getM_TrackInfo().getM_uiDuration() / (trkAtom.getM_TrackInfo().getM_uiTimeScale() & 0xFFFFFFFFL));
                                        m_Mp4FileInfo.setM_bHasAudio(true);
                                        trkAtom.getTRACK_INFO().CloneTo(m_Mp4FileInfo.getM_AudioInfo().getM_trakInfo());
                                        trkAtom.getCODEC_INFO().CloneTo(m_Mp4FileInfo.getM_AudioInfo().getM_codec());
                                        m_uiAudioID = m_Mp4FileInfo.getM_AudioInfo().getM_trakInfo().getM_uiTrack_ID();
                                        break;
                                    }

                                case "TRAK_VIDEO":
                                    {
                                        trkAtom.getM_TrackInfo().setM_fDurationSecs((float)trkAtom.getM_TrackInfo().getM_uiDuration() / (trkAtom.getM_TrackInfo().getM_uiTimeScale() & 0xFFFFFFFFL));                                        
                                        m_Mp4FileInfo.setM_bHasVideo(true);
                                        trkAtom.getTRACK_INFO().CloneTo(m_Mp4FileInfo.getM_VideoInfo().getM_trakInfo());
                                         m_Mp4FileInfo.setM_fFrameRate((float)(m_Mp4FileInfo.getM_VideoInfo().getM_trakInfo().getM_uiTimeScale() & 0xFFFFFFFFL) / (float)(m_Mp4FileInfo.getM_VideoInfo().getM_trakInfo().getM_uiSampleDuration() & 0xFFFFFFFFL));
                                        trkAtom.getCODEC_INFO().CloneTo(m_Mp4FileInfo.getM_VideoInfo().getM_codec());
                                        m_uiVideoID = m_Mp4FileInfo.getM_VideoInfo().getM_trakInfo().getM_uiTrack_ID();
                                        break;
                                    }

                                case "TRAK_HINT":
                                    {
                                        m_Mp4FileInfo.setM_bHasHint(true);
                                        trkAtom.getTRACK_INFO().CloneTo(m_Mp4FileInfo.getM_HintInfo().getM_trakInfo());
                                        
                                        break;
                                    }

                                case "TRAK_META":
                                    {
                                        m_Mp4FileInfo.setM_bHasMeta(true);
                                        trkAtom.getTRACK_INFO().CloneTo(m_Mp4FileInfo.getM_MetaInfo().getM_trakInfo());
                                        
                                        break;
                                    }
                            }                    
                            break;
                        }                  

                    case "iods":
                        { 
                            
                            break;
                        }

                    case "udta":
                        {
                            TRAKAtom trkAtom = new TRAKAtom();
                            trkAtom.addLoggingListener(logging);
                            UDTAAtom atom = new UDTAAtom(trkAtom);// check this from jatindar sir
                            atom.setAtomID(sAtomID);
                            atom.setReader(referenceToM_BinReader);
                            atom.m_Parent = this;
                            atom.parseAtoms(uiFileOffset, uiAtomLength);
                            break;
                        }
                    default:
                        {
                            break;
                        }
                }
                              
                Atom_ID = null;

                // update file offset
                uiFileOffset += java.lang.Math.max(uiAtomLength,Utils.HEADER_LENGTH);
            } catch (IOException ex) {
               printLog(LogType.error, "mp4editor.MOOVAtom | ", ex);
            }
        }
        return false;
    }
}

