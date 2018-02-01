/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mp4editor.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import mp4editor.MVHDAtom;
import mp4editor.Mp4AtomBase;
import mp4editor.Utils;
import mp4editor.pojo.AttributeInfo;

/**
 *
 * @author RAHUL KUMAR SHARMA
 */
public class Mp4EditorUtil {
    LoggingEvents log = null;
    long offSet = 0;
    long length = 0;
    
    AttributeInfo m_attrInfo = new AttributeInfo(); 
    
    public void addLoggingListener(LoggingEvents loggingListener){
        log = loggingListener;
    }
    
    /**
     * 
     * @param fileNameWithPath
     * @return true if file is valid.
     */
    
    public boolean isMediaFileValid(String fileNameWithPath) {
        boolean fileLoaded = false;
        //KBL: case no 9045: V6.0.53.0 Closing file handle.
        try {
            log.printLog(LogType.information, null, "mp4editor.Mp4EditorUtil | Inside isMediaFileValid() method - Validating media file. "+fileNameWithPath);
            fileLoaded = validateFile(fileNameWithPath);
            log.printLog(LogType.information, null, "mp4editor.Mp4EditorUtil | Inside isMediaFileValid() method - Validating media file. Media File valid if true non-valid if false "+fileLoaded);
        } catch (IOException ex) {
            log.printLog(LogType.error, ex, "mp4editor.util.Mp4EditorUtil | Exception while validating the file : ");
        }
        return fileLoaded;
    }
    
    
    public AttributeInfo getMediaFileInfo(String fileNameWithPath) throws IOException {
        RandomAccessFile accessFile = null;
        //KBL: case no 9045: V6.0.53.0 Closing file handle.
        try {
            if (stringNullCheck(fileNameWithPath)) {
                File file = new File(fileNameWithPath);
                if (file.isFile() && file.exists()) {
                    accessFile = new RandomAccessFile(file, "r");
                    length = file.length();
//                    m_dictChildAtoms.clear();
                    return getTotalFileDurationFrmAtomsParsing(accessFile);
                }
            }
        } catch (FileNotFoundException ex) {
            log.printLog(LogType.error, ex, "mp4editor.util.Mp4EditorUtil | ");
            return m_attrInfo;
        } finally {
            if (accessFile != null) {
                accessFile.close();
            }
        }
        return m_attrInfo;
    }
    
    /**
     * will check null condition of the String.
     * @param obj
     * @return boolean
     */
    public boolean stringNullCheck(String obj){
        if (obj != null && !obj.equals("") && !obj.equals("null")) {
            return true;
        }
        return false;
    }
    
    private boolean validateFile(String mediaFilePath) throws IOException {
        if (stringNullCheck(mediaFilePath)) {
            RandomAccessFile accessFile =  null;
            try {
                File file = new File(mediaFilePath);
                if (file.isFile() && file.exists()) {
                    accessFile = new RandomAccessFile(file,"r");
                    length = file.length();
//                    m_dictChildAtoms.clear();
                    return validateAtoms(accessFile);
                }
            } catch (FileNotFoundException ex) {
                log.printLog(LogType.error, ex, "mp4editor.util.Mp4EditorUtil | ");
                return false;
            }finally{
                if(accessFile!=null)
                    accessFile.close();
            }
            return true;
        }
        return false;
    }
    
    private boolean validateAtoms(RandomAccessFile accessFile) {
        byte[] fileContent ;
        boolean bIsMp4File = false;
        try {
            while (offSet < length) {
                fileContent = new byte[8];
                accessFile.seek(offSet);
                accessFile.read(fileContent, (int) 0, 8);
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
                    accessFile.seek(offSet + 8);
                    accessFile.read(fileContent, 0, 8);
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
                    case "ftyp": {
                        bIsMp4File = true;
                        break;
                    }
                    case "mdat": {
                        if (value <= 16) {
                            return false;
                        }
                        break;
                    }
                    case "moov": {
                        double duration = validateMoovAtoms(offSet, value, accessFile);
                        if (duration == 0) {
                            return false;
                        }
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
                    log.printLog(LogType.information, null, "mp4editor.Mp4EditorUtil | Given file is not mp4 format file.");
                    return false;
                }
                
                offSet += value;
            }
        } catch (IOException ex) {
            log.printLog(LogType.error, ex, "mp4editor.util.Mp4EditorUtil | ");
            return false;
        }
        return true;
    }
    
    
    private AttributeInfo getTotalFileDurationFrmAtomsParsing(RandomAccessFile accessFile) {
        byte[] fileContent ;
        boolean bIsMp4File = false;
        //CC: duration is sec.
        double duration = 0.0;
        try {
            while (offSet < length) {
                fileContent = new byte[8];
                accessFile.seek(offSet);
                accessFile.read(fileContent, (int) 0, 8);
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
                    accessFile.seek(offSet + 8);
                    accessFile.read(fileContent, 0, 8);
                    value = 0;
                    for (int i = 0; i < 8; i++) {
                        value = (value << 8) + (fileContent[i] & 0xff);
                    }
                }
                else if(value == 0) // Atom length is zero , atom is to end of file
                {
                    return null;
                }
                
                System.out.println("Atom Name : " + atomName);
                System.out.println("Off Set : " + offSet);
                Mp4AtomBase atom = null;
                
                switch (atomName)
                {
                    case "ftyp": {
                        bIsMp4File = true;
                        break;
                    }
                    case "mdat": {
                        if (value <= 16) {
                            return null;
                        }
                        break;
                    }
                    case "moov": {
                        duration = validateMoovAtoms(offSet, value, accessFile);
                        if(duration!=0)
                            return m_attrInfo;
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
                    log.printLog(LogType.information, null, "mp4editor.Mpeg4FileReader | Given file is not mp4 format file.");
                    return null;
                }
                
                offSet += value;
            }
        } catch (IOException ex) {
            log.printLog(LogType.error, ex, "mp4editor.util.Mp4EditorUtil | ");
            return null;
        }
        return null;
    }
    
    private double validateMoovAtoms(long uiFileStart, long uiAtomSize, RandomAccessFile accessFile)
    {

        long uiFileOffset = uiFileStart + Utils.HEADER_LENGTH;
        long uiAtomEnd = uiFileStart + uiAtomSize;
        long uiAtomLength = 0;
        char[] Atom_ID = null;
        double duration = 0 ;
        while (uiFileOffset < uiAtomEnd)
        {
            try {
//                RandomAccessFile referenceToM_BinReader = randomAccessFile ;
                byte[] MpegBoxBuff = Utils.readBytes(accessFile, uiFileOffset, (int)Utils.HEADER_LENGTH);
//                m_BinReader = referenceToM_BinReader;                
                long[] referenceToUiAtomLength = { uiAtomLength };
                char[][] referenceToAtom_ID = { Atom_ID };
                Utils.readAtom(MpegBoxBuff,referenceToUiAtomLength,referenceToAtom_ID);
                uiAtomLength = referenceToUiAtomLength[0];
                Atom_ID = referenceToAtom_ID[0];
                String sAtomID = new String(Atom_ID);
                sAtomID = sAtomID.toLowerCase();
//                log.info("  MOOV-Parser  AtomID = {"+sAtomID+"} AtomLength = {"+uiAtomLength+"}");
//                log.printLog(LogType.information, null, "mp4editor.Mpeg4FileReader | Inside validateMoovAtoms() method - ");
                
                switch (sAtomID)
                {
                    case "mvhd":
                        {
                            MVHDAtom mMVHDAtom = new MVHDAtom();
                            mMVHDAtom.setAtomID(sAtomID);
                            mMVHDAtom.setReader(accessFile);
//                            mMVHDAtom.m_Parent = this;
//                            mMVHDAtom.parseAtoms(uiFileOffset, uiFileOffset);
                            duration = validateMVHDAtom(uiFileOffset, uiAtomLength, accessFile);
                            
                            m_attrInfo.setDuration(duration);
                            //file duration will never be ZERO if it is ZERO it will be a incompatible Media-File.
                            //return duration;
//                            if (duration == 0) {
//                                return false;
//                            }else{
//                                return true;
//                            }
                            break;
                        }
                        
                    case "udta" :
                    {
                        ParseUdatAtom(uiFileOffset, uiAtomLength, accessFile);
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
                log.printLog(LogType.error, ex, "mp4editor.util.Mp4EditorUtil | ");
            }
        }
        return duration;
    }
    
    private void ParseUdatAtom(long uiFileStart, long uiAtomSize, RandomAccessFile accessFile) 
    {
        try {

            byte[] buff = new byte[(int)uiAtomSize];
            int iIndex = 0;
            accessFile.seek(uiFileStart);
            accessFile.read(buff, 0, (int)uiAtomSize);
            
            iIndex = (int)Utils.HEADER_LENGTH;
                  
            if (buff.length < iIndex + 8)            
                return;
            

            long iAtomLen = 0;
            iAtomLen = (buff[iIndex + 0] & 0xFF);
            iAtomLen <<= 8;
            iAtomLen |= ((buff[iIndex + 1]) & 0xFF);
            iAtomLen <<= 8;
            iAtomLen |= ((buff[iIndex + 2]) & 0xFF);
            iAtomLen <<= 8;
            iAtomLen |= ((buff[iIndex + 3]) & 0xFF);

            
            char[] sID = new char[4];
            sID[0] = (char)(buff[iIndex + 4] & 0xFF);
            sID[1] = (char)(buff[iIndex + 5] & 0xFF);
            sID[2] = (char)(buff[iIndex + 6] & 0xFF);
            sID[3] = (char)(buff[iIndex + 7] & 0xFF);
            
            String sAtomID = new String(sID);
            sAtomID = sAtomID.toLowerCase();

            // Ism private data storing 
            switch (sAtomID)
            {
                case "crtm":                   
                    
                    long lCreationTimeMilliSecs = (Utils.readUInt64(buff, iIndex + 8));
                    m_attrInfo.setCreationTimeMillisecs(lCreationTimeMilliSecs);
                    break;
            }
            
        } catch (IOException ex) {
            log.printLog(LogType.error, ex, "mp4editor.util.Mp4EditorUtil | Exception while validating MP4 file | MOOV-ATOM Validation | ");
        }
        return ;
    }
    
    private double validateMVHDAtom(long uiFileStart, long uiAtomSize, RandomAccessFile accessFile) 
    {
        try {

            byte[] buff = new byte[(int)uiAtomSize];
            int iIndex = 0;
            accessFile.seek(uiFileStart);
            accessFile.read(buff, 0, (int)uiAtomSize);
            
            iIndex = (int)Utils.HEADER_LENGTH;
            //byte[] tempBuff = Utils.readBytes(accessFile, uiSeekOffset, 4);
            long uiVersion = (buff[iIndex] & 0xFF);
            iIndex += 4;

            if ((uiVersion & 0xFFFFFFFFL) == 0)
            {
                long m_uiCreationTime = (Utils.readUInt32(buff, iIndex) & 0xFFFFFFFFL);
                iIndex += 4;
                
                long m_uiModificationTime = (Utils.readUInt32(buff, iIndex) & 0xFFFFFFFFL);
                iIndex += 4;
                
                m_attrInfo.setCreationTimeMillisecs(m_uiCreationTime * 1000);
                m_attrInfo.setModificationTimeMillisecs(m_uiModificationTime * 1000);
            }
            else 
            {
                long m_uiCreationTime = Utils.readUInt64(buff, iIndex);
                iIndex += 8;

                long m_uiModificationTime = Utils.readUInt64(buff, iIndex);
                iIndex += 8;
                
                m_attrInfo.setCreationTimeMillisecs(m_uiCreationTime * 1000);
                m_attrInfo.setModificationTimeMillisecs(m_uiModificationTime * 1000);
            }
            //CC: timeScale is Ticks/Sec
            long timeScale = Utils.readUInt32(buff, iIndex);
            iIndex += 4;
            
            if ((uiVersion & 0xFFFFFFFFL) == 0)
            {
                //CC: duration is total nouber of ticks in file.
                long duration = Utils.readUInt32(buff, iIndex);
                double durationInSec = (double)duration/timeScale;
                iIndex += 4;
                return durationInSec;
            }
            else
            {
                long duration = Utils.readUInt64(buff, iIndex);
                double durationInSec = (double)duration/timeScale;
                iIndex += 8;
                return durationInSec;
            }
        } catch (IOException ex) {
            log.printLog(LogType.error, ex, "mp4editor.util.Mp4EditorUtil | Exception while validating MP4 file | MOOV-ATOM Validation | ");
        }
        return 0;
    }
    
    
}
