/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mp4editor;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.codec.binary.Base64;
import mp4editor.pojo.AVC1Info;
import mp4editor.pojo.MP4AInfo;
import mp4editor.pojo.enm.IFrameSearchEnm;
import mp4editor.util.LogType;
import mp4editor.util.LoggingEvents;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author RAHUL
 */
public class Mpeg4FileReader {

    RandomAccessFile randomAccessFile = null;
    HashMap<String, Mp4AtomBase> m_dictChildAtoms = null;
    long offSet = 0;
    long length = 0;
    boolean partialParsing = false;
    LoggingEvents logging = null;
    String mediaFileWithPath = null;
    
    public Mpeg4FileReader() {
        m_dictChildAtoms = new HashMap<String, Mp4AtomBase>();
    }
    
    public void addLoggingListener(LoggingEvents logging){
        this.logging = logging;
    }
    
    public void printLog(LogType logType, Exception ex, String message){
        if(logging!=null){
            logging.printLog(logType, ex, message);
        }
    }
    
    public void unInit()
    {
        //KBL: case no 9545 V6.0.52.0 closing the FileStream by calling randomAccessFile.close() method.
        if(randomAccessFile!=null){
            try {
                randomAccessFile.close();
            } catch (IOException ex) {
                printLog(LogType.error, ex, "mp4editor.Mpeg4FileReader | ");
            }
        }
        if(m_dictChildAtoms != null){
            Set<String> keySet = m_dictChildAtoms.keySet();
            Iterator<String> keyItr = keySet.iterator();        
            while(keyItr.hasNext()){
                String key = keyItr.next();
                Mp4AtomBase atomObj = m_dictChildAtoms.get(key);
                atomObj.unInit();
            }
            m_dictChildAtoms.clear();
        }
        
        m_dictChildAtoms = null;
    }

    public boolean loadFile(String mediaFilePath) {
        this.mediaFileWithPath = mediaFilePath;
        if (Utils.stringNullCheck(mediaFilePath)) {
            try {
                File file = new File(mediaFilePath);
                if (file.isFile() && file.exists()) {
                    randomAccessFile = new RandomAccessFile(file,"r");
                    length = file.length();
//                    m_dictChildAtoms.clear();
                    parseAtoms();
                }
            } catch (FileNotFoundException ex) {
                printLog(LogType.error, ex, "mp4editor.Mpeg4FileReader | ");
                return false;
            }
            return true;
        }
        return false;
    }
    
    public boolean parseAtoms() {
        byte[] fileContent ;
        boolean bIsMp4File = false;
        try {
            while (offSet < length) {
                fileContent = new byte[8];
                randomAccessFile.seek(offSet);
                randomAccessFile.read(fileContent, (int) 0, 8);
                long value = 0;
                for (int i = 0; i < 4; i++) {
                    value = (value << 8) + (fileContent[i] & 0xff);
                }
                System.out.println("File content : " + value);
                String atomName = new String(fileContent, 4, 4);
                
                 
                // Jatinder , handling large files
                // Rahul , verify the changes
                // Version1 Atom , Read length from next 8 bytes
                if(value == 1)
                {
                    randomAccessFile.seek(offSet + 8);
                    randomAccessFile.read(fileContent, 0, 8);
                    value = 0;
                    for (int i = 0; i < 8; i++) {
                        value = (value << 8) + (fileContent[i] & 0xff);
                    }
                }
                else if(value == 0) // Atom length is zero , atom is to end of file
                {
                    return false;
                
                }
                
                
                System.out.println("Atom Name : " + atomName);
                System.out.println("Off Set : " + offSet);
                Mp4AtomBase atom = null;
                
                switch (atomName)
                {
                    case  "moov" :
                        {
                            atom = new MOOVAtom();
                            ((MOOVAtom)atom).setPartialParsing(partialParsing);
                            break;
                        }

                    case "ftyp":
                        {
                            bIsMp4File = true;
                            atom = new FTYPEAtom();
                            break;
                        }

                    case "free":
                        {
//                            atom = new FREEAtom();
                            break;
                        }

                    case "mdat":
                        {
                            //atom = new MDATAtom();
                            break;
                        }

                    default:
                        {
                            break;
                        }
                }
                if (!bIsMp4File)
                {
                    //log.info("Given file is not mp4 format file.");            
                    printLog(LogType.error, null, "mp4editor.Mpeg4FileReader | Given file is not mp4 format file. And The File-Path is : "+mediaFileWithPath);
                    return false;
                }
                
                if (atom != null)
                {
//                    atom.m_evtLogInfo += new LogInfo(LogInfo);
                    atom.setAtomID(atomName);
                    atom.setReader(randomAccessFile);
                    atom.parseAtoms(offSet, value);
                    m_dictChildAtoms.put(atomName, atom);
                }
                offSet += value;
            }
        } catch (IOException ex) {
            printLog(LogType.error, ex, "mp4editor.Mpeg4FileReader | ");
            return false;
        }
        return true;
    }
    
    public void setPartialParsing(boolean partialParsing){
        this.partialParsing = partialParsing;
    }
    
    
    public String getMp4FileInfo(String sInfoXML, String[] sError)
        {
            try
            {
                MpegFileInfo info = ((MOOVAtom)m_dictChildAtoms.get("moov")).getMpegFileInfo();
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document objMP4Doc = documentBuilder.newDocument();
                
                String root = XMLDefines.TRACK_XML;
            Element rootElement = objMP4Doc.createElement("trackinfo");
            
//            document.appendChild(rootElement);
//                objMP4Doc.LoadXml();
                if (info.isM_bHasVideo())
                {
                    Element video = objMP4Doc.createElement(XMLDefines.ELEMENT_TRACK);
                    video.setAttribute(XMLDefines.ATTR_TRACK_TYPE, XMLDefines.VALUE_VIDEO_TYPE);
                    video.setAttribute(XMLDefines.ATTR_CODEC_TYPE, XMLDefines.VALUE_H_264_TYPE);

                    Element avcElement = CreateAvc1Element(objMP4Doc, info);
                    video.appendChild(avcElement);
                    rootElement.appendChild(video);
//                    objMP4Doc.appendChild(video);
//                    objMP4Doc.appendChild(rootElement);
                }

                if (info.isM_bHasAudio())
                {
                    Element audio = objMP4Doc.createElement(XMLDefines.ELEMENT_TRACK);
                    audio.setAttribute(XMLDefines.ATTR_TRACK_TYPE, XMLDefines.VALUE_AUDIO_TYPE);
                    audio.setAttribute(XMLDefines.ATTR_CODEC_TYPE, XMLDefines.VALUE_ACC_TYPE);

                    Element mp4aElement = CreateMp4aElement(objMP4Doc,info);
                    audio.appendChild(mp4aElement);

                    rootElement.appendChild(audio);
                    
//                    objMP4Doc.appendChild(audio);
                }
                objMP4Doc.appendChild(rootElement);
//                sInfoXML = objMP4Doc.getTextContent();
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");

                //initialize StreamResult with File object to save to file
                StreamResult result = new StreamResult(new StringWriter());
                DOMSource source = new DOMSource(objMP4Doc);
                transformer.transform(source, result);

                sInfoXML = result.getWriter().toString();
                System.out.println(sInfoXML);
            }
            catch(Exception ex)
            {
                printLog(LogType.error, ex,  "mp4editor.Mpeg4FileReader | ");
            }
            return sInfoXML;
        }
    
    
    public String getVideoFileInfo(String sInfoXML, String[] sError)
        {
            try
            {
                MpegFileInfo info = ((MOOVAtom)m_dictChildAtoms.get("moov")).getMpegFileInfo();
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document objMP4Doc = documentBuilder.newDocument();
                
                String root = XMLDefines.TRACK_XML;
//            Element rootElement = objMP4Doc.createElement("trackinfo");
            
//            document.appendChild(rootElement);
//                objMP4Doc.LoadXml();
                if (info.isM_bHasVideo())
                {
                    Element video = objMP4Doc.createElement(XMLDefines.ELEMENT_TRACK);
                    video.setAttribute(XMLDefines.ATTR_TRACK_TYPE, XMLDefines.VALUE_VIDEO_TYPE);
                    video.setAttribute(XMLDefines.ATTR_CODEC_TYPE, XMLDefines.VALUE_H_264_TYPE);

                    Element avcElement = CreateAvc1Element(objMP4Doc, info);
                    video.appendChild(avcElement);
//                    rootElement.appendChild(video);
//                    objMP4Doc.appendChild(video);
//                    objMP4Doc.appendChild(rootElement);
                    objMP4Doc.appendChild(video);
                }

//                if (info.isM_bHasAudio())
//                {
//                    Element audio = objMP4Doc.createElement(XMLDefines.ELEMENT_TRACK);
//                    audio.setAttribute(XMLDefines.ATTR_TRACK_TYPE, XMLDefines.VALUE_AUDIO_TYPE);
//                    audio.setAttribute(XMLDefines.ATTR_CODEC_TYPE, XMLDefines.VALUE_ACC_TYPE);
//
//                    Element mp4aElement = CreateMp4aElement(objMP4Doc,info);
//                    audio.appendChild(mp4aElement);
//
//                    rootElement.appendChild(audio);
//                    
////                    objMP4Doc.appendChild(audio);
//                }
                
//                sInfoXML = objMP4Doc.getTextContent();
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");

                //initialize StreamResult with File object to save to file
                StreamResult result = new StreamResult(new StringWriter());
                DOMSource source = new DOMSource(objMP4Doc);
                transformer.transform(source, result);
                String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
                sInfoXML = result.getWriter().toString();
                sInfoXML = sInfoXML.replace(xmlStr, "");
                System.out.println(sInfoXML);
            }
            catch(Exception ex)
            {
                printLog(LogType.error, ex,  "mp4editor.Mpeg4FileReader | ");
            }
            return sInfoXML;
        }
    
    public String getAudioNVideo(String audio, String video){
        String trackInfo = "<trackinfo>"+video+audio+"</trackinfo>";
        return trackInfo;
    }
    
    public String getAudioFileInfo(String sInfoXML, String[] sError)
        {
            try
            {
                MpegFileInfo info = ((MOOVAtom)m_dictChildAtoms.get("moov")).getMpegFileInfo();
                
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document objMP4Doc = documentBuilder.newDocument();
                
                String root = XMLDefines.TRACK_XML;
//            Element rootElement = objMP4Doc.createElement("trackinfo");
            
//            document.appendChild(rootElement);
//                objMP4Doc.LoadXml();
//                if (info.isM_bHasVideo())
//                {
//                    Element video = objMP4Doc.createElement(XMLDefines.ELEMENT_TRACK);
//                    video.setAttribute(XMLDefines.ATTR_TRACK_TYPE, XMLDefines.VALUE_VIDEO_TYPE);
//                    video.setAttribute(XMLDefines.ATTR_CODEC_TYPE, XMLDefines.VALUE_H_264_TYPE);
//
//                    Element avcElement = CreateAvc1Element(objMP4Doc, info);
//                    video.appendChild(avcElement);
//                    rootElement.appendChild(video);
////                    objMP4Doc.appendChild(video);
////                    objMP4Doc.appendChild(rootElement);
//                }

                if (info.isM_bHasAudio())
                {
                    Element audio = objMP4Doc.createElement(XMLDefines.ELEMENT_TRACK);
                    audio.setAttribute(XMLDefines.ATTR_TRACK_TYPE, XMLDefines.VALUE_AUDIO_TYPE);
                    audio.setAttribute(XMLDefines.ATTR_CODEC_TYPE, XMLDefines.VALUE_ACC_TYPE);

                    Element mp4aElement = CreateMp4aElement(objMP4Doc,info);
                    audio.appendChild(mp4aElement);
                    objMP4Doc.appendChild(audio);
//                    rootElement.appendChild(audio);
                    
//                    objMP4Doc.appendChild(audio);
                }
                
//                sInfoXML = objMP4Doc.getTextContent();
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");

                //initialize StreamResult with File object to save to file
                StreamResult result = new StreamResult(new StringWriter());
                DOMSource source = new DOMSource(objMP4Doc);
                transformer.transform(source, result);

                String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
                sInfoXML = result.getWriter().toString();
                sInfoXML = sInfoXML.replace(xmlStr, "");
                System.out.println(sInfoXML);
            }
            catch(Exception ex)
            {
                printLog(LogType.error, ex,  "mp4editor.Mpeg4FileReader | ");
            }
            return sInfoXML;
        }
    
//    
    
   public byte[] GetTrackSample(long trackId, long uiSampleNo, byte[] referenceToArrData,double[] sampleDelta, String[] referenceToSError) throws Exception{
       
       if(m_dictChildAtoms.containsKey("moov")){
           referenceToArrData = ((MOOVAtom)m_dictChildAtoms.get("moov")).getTrackSample(trackId, uiSampleNo, referenceToArrData, sampleDelta, referenceToSError);
       }else{
           referenceToSError[0] += "MOOVAtoms object is null";
           //log.error("MOOVAtoms object is null");
//           return false;
       }
       return referenceToArrData;
    }
   
   public long GetTotalTrackSamples(long trackId) throws Exception{
       if(m_dictChildAtoms.containsKey("moov")){
           return ((MOOVAtom)m_dictChildAtoms.get("moov")).getTotalTrackSamples(trackId);
       }else 
           return 0;
    }
    
    public byte[] GetTrackSample(long trackId, long uiSampleNo, byte[] referenceToArrData,double[] sampleDelta,double[] samplePresentationOffset, String[] referenceToSError) throws Exception{
       
       if(m_dictChildAtoms.containsKey("moov")){
           referenceToArrData = ((MOOVAtom)m_dictChildAtoms.get("moov")).getTrackSample(trackId, uiSampleNo, referenceToArrData, sampleDelta,samplePresentationOffset, referenceToSError);
       }else{
           referenceToSError[0] += "MOOVAtoms object is null";
           //log.error("MOOVAtoms object is null");
//           return false;
       }
       return referenceToArrData;
    }
    
    public MpegFileInfo getMp4FileInfo()
        {            
            MpegFileInfo info = new MpegFileInfo();
            try
            {
                info = ((MOOVAtom) m_dictChildAtoms.get("moov")).getMpegFileInfo();
            }
            catch(Exception ex)
            {
                printLog(LogType.error, ex, "mp4editor.Mpeg4FileReader | ");
            }
            return info;
        }
    
    public boolean getIFrame(long uiTrackID,long uiSampleNo,IFrameSearchEnm search, long[] uiISample)
    {
       // log.info("Get IFrame");
        printLog(LogType.information, null, "mp4editor.Mpeg4FileReader | Get IFrame.");
       return ((MOOVAtom)m_dictChildAtoms.get("moov")).getIFrame(uiTrackID, uiSampleNo, search, uiISample);  
    }
    
    public long getSampleTimeSecToSampleNumber(double cutInTimeSec, long trackID){
        return ((MOOVAtom)m_dictChildAtoms.get("moov")).getSampleTimeSecToSampleNumber(cutInTimeSec, trackID);
    }
 
    public double getSampleNumberToSampleTimeSec(long sampleNumber, long trackID){
        return ((MOOVAtom)m_dictChildAtoms.get("moov")).getSampleNumberToSampleTimeSec(sampleNumber, trackID);
    }
    
    private Element CreateAvc1Element(Document doc, MpegFileInfo info)
        {
            AVC1Info infoAVC = (AVC1Info)info.getM_VideoInfo().getM_codec().getM_codecInfo();
            Element avc1Element = doc.createElement(XMLDefines.ELEMENT_AVC_CONFIG);

            avc1Element.setAttribute(XMLDefines.ATTR_VIDEO_WIDTH, String.valueOf(infoAVC.getM_uiWidth()));
            avc1Element.setAttribute(XMLDefines.ATTR_VIDEO_HEIGHT, String.valueOf(infoAVC.getM_uiHeight()));
            avc1Element.setAttribute(XMLDefines.ATTR_VIDEO_FRAME_RATE, String.valueOf(info.getM_fFrameRate()));
            avc1Element.setAttribute(XMLDefines.ATTR_H_264_PROFILE_INDICATION, Byte.toString(infoAVC.getM_objAVCCInfo().getM_AVCProfileIndication()));//.ToString("X")
            avc1Element.setAttribute(XMLDefines.ATTR_H_264_PROFILE_COMPATIBILITY, Byte.toString(infoAVC.getM_objAVCCInfo().getM_Profile_compatibility()));//.ToString("X"));
            avc1Element.setAttribute(XMLDefines.ATTR_H_264_LEVEL_INDICATION, Byte.toString(infoAVC.getM_objAVCCInfo().getM_AVCLevelIndication()));//.ToString("X"));
            avc1Element.setAttribute(XMLDefines.ATTR_H_264_LENGTH_MINUS_ONE, Byte.toString(infoAVC.getM_objAVCCInfo().getM_LenMinusOne()));//.ToString());

            // SPS nal unit
           Element spsElement = doc.createElement(XMLDefines.ELEMENT_SPS_NAL_UNIT);
            for (int iIndex = 0; iIndex < infoAVC.getM_objAVCCInfo().getM_sequenceParameterSetNALUnit().length; iIndex++)
            {
                Element nalElement = doc.createElement(XMLDefines.ELEMENT_INFO);
                nalElement.setTextContent(new String(Base64.encodeBase64(infoAVC.getM_objAVCCInfo().getM_sequenceParameterSetNALUnit()[iIndex].getM_data())));
                spsElement.appendChild(nalElement);
            }
            avc1Element.appendChild(spsElement);

            // PPS nal unit
            Element ppsElement = doc.createElement(XMLDefines.ELEMENT_PPS_NAL_UNIT);
            for (int iIndex = 0; iIndex < infoAVC.getM_objAVCCInfo().getM_pictureParameterSetNALUnit().length; iIndex++)
            {
                Element nalElement = doc.createElement(XMLDefines.ELEMENT_INFO);
                nalElement.setTextContent(new String(Base64.encodeBase64(infoAVC.getM_objAVCCInfo().getM_pictureParameterSetNALUnit()[iIndex].getM_data())));
                ppsElement.appendChild(nalElement);
            }
            avc1Element.appendChild(ppsElement);

            return avc1Element;

        }
    
    
     private Element CreateMp4aElement(Document doc, MpegFileInfo info)
        {
            MP4AInfo infoMP4A = (MP4AInfo)info.getM_AudioInfo().getM_codec().getM_codecInfo();
            Element mp4aElement = doc.createElement(XMLDefines.ELEMENT_AAC_CONFIG);
            mp4aElement.setAttribute(XMLDefines.ATTR_AUDIO_CHANNELS, String.valueOf(infoMP4A.getM_uiChannels()));
            mp4aElement.setAttribute(XMLDefines.ATTR_AUDIO_SAMPLE_SIZE, String.valueOf(infoMP4A.getM_uiSampleSize()));
            mp4aElement.setAttribute(XMLDefines.ATTR_AUDIO_SAMPLE_RATE, String.valueOf(infoMP4A.getM_uiSampleRate()));
            mp4aElement.setAttribute(XMLDefines.ATTR_AUDIO_BLOCK_SIZE, String.valueOf(info.getM_AudioInfo().getM_trakInfo().getM_uiSampleDuration()));
            mp4aElement.setAttribute(XMLDefines.ATTR_AUDIO_DECODER_CFG_DATA, new String(Base64.encodeBase64(infoMP4A.getM_arrDecoderCfgData())));
            return mp4aElement;
        }
     
    public byte[] GetAudioSampleWthADTS(long trackId, long uiSampleNo, byte[] referenceToArrData,double[] sampleDelta, String[] referenceToSError) throws Exception{
       
       if(m_dictChildAtoms.containsKey("moov")){
           referenceToArrData = ((MOOVAtom)m_dictChildAtoms.get("moov")).GetAudioSampleWthADTS(trackId, uiSampleNo, referenceToArrData, sampleDelta, referenceToSError);
       }else{
           referenceToSError[0] += "MOOVAtoms object is null";
           //log.error("MOOVAtoms object is null");
//           return false;
       }
       return referenceToArrData;
    } 
     
}
