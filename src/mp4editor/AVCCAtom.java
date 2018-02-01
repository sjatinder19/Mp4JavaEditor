package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import mp4editor.pojo.NALInfo;




// avc configuration box atom
class AVCCAtom extends Mp4AtomBase
{        
    private TRAKAtom m_Track = null;
    AVCCAtom(TRAKAtom track)
    {
        m_Track = track;
    }
   
    public long getAtomSize()
    {
        super.setAtomSize(calcSize() + Utils.HEADER_LENGTH); 
        return super.getAtomSize();
    }
    public void setAtomSize(long value)
    {
        super.setAtomSize(value);
    }

    private long calcSize()
    {
        long uiSize = 0;

        uiSize += 1;
        uiSize += 1;
        uiSize += 1;
        uiSize += 1;
        uiSize += 1;
        uiSize += 1;
        for (NALInfo info : ((AVC1Atom)m_Parent).getM_AVC1Info().getM_objAVCCInfo().getM_sequenceParameterSetNALUnit())
        {
            uiSize += 2;
            uiSize += (info.m_uiLength & 0xFFFFFFFFL);
        }

        uiSize += 1;
        for (NALInfo info : ((AVC1Atom)m_Parent).getM_AVC1Info().getM_objAVCCInfo().getM_pictureParameterSetNALUnit())
        {
            uiSize += 2;
            uiSize += (info.m_uiLength & 0xFFFFFFFFL);
        }

        return uiSize;
    }

    public boolean generateAtom()
    {
        try {
            long lStart = m_BinWriter.getFilePointer();//getBaseStream().getPosition();

            setAtomID("avcC");            
            super.generateAtom();

            byte[] buf = new byte[5];
            buf[0] = (byte) 1;
            buf[1] = (byte)((AVC1Atom)m_Parent).getM_AVC1Info().getM_objAVCCInfo().getM_AVCProfileIndication();
            buf[2] = (byte)((AVC1Atom)m_Parent).getM_AVC1Info().getM_objAVCCInfo().getM_Profile_compatibility();
            buf[3] = (byte)((AVC1Atom)m_Parent).getM_AVC1Info().getM_objAVCCInfo().getM_AVCLevelIndication();
            buf[4] = (byte)(((byte)((AVC1Atom)m_Parent).getM_AVC1Info().getM_objAVCCInfo().getM_LenMinusOne()) | 0xfc);

            m_BinWriter.write(buf);
            byte bTemp = (byte)((AVC1Atom)m_Parent).getM_AVC1Info().getM_objAVCCInfo().getM_sequenceParameterSetNALUnit().length;
            bTemp |= 0xe0;
            m_BinWriter.writeByte(bTemp);
            for (NALInfo info : ((AVC1Atom)m_Parent).getM_AVC1Info().getM_objAVCCInfo().getM_sequenceParameterSetNALUnit())
            {
                RandomAccessFile referenceToM_BinWriter = m_BinWriter;
                Utils.writeUint16(referenceToM_BinWriter, (int)(info.m_uiLength & 0xFFFFFFFFL));
                m_BinWriter.write(info.getM_data());
            }

            bTemp = (byte)((AVC1Atom)m_Parent).getM_AVC1Info().getM_objAVCCInfo().getM_pictureParameterSetNALUnit().length;
            m_BinWriter.writeByte(bTemp);

            for (NALInfo info : ((AVC1Atom)m_Parent).getM_AVC1Info().getM_objAVCCInfo().getM_pictureParameterSetNALUnit())
            {
                RandomAccessFile referenceToM_BinWriter = m_BinWriter ;
                Utils.writeUint16(referenceToM_BinWriter, (int)(info.m_uiLength & 0xFFFFFFFFL));
                m_BinWriter.write(info.getM_data());
            }


            long lStop = m_BinWriter.getFilePointer();//getBaseStream().getPosition();
            if ((lStop - lStart) != (long)getAtomSize())
                ;//log.info("*****  Generation Error. Atom - {0}  ExpectedSize {1} ActualSize {2}"+ getAtomID()+" | "+getAtomSize()+" | "+ (lStop - lStart));
            
            return true;
        } catch (IOException ex) {
           // log.error(ex);
            return false;
        }
    }

    public boolean parseAtoms(long uiFileStart,long uiAtomSize)
    {
        try {
            super.parseAtoms(uiFileStart, uiAtomSize);

            long uiSeekOffset = uiFileStart + Utils.HEADER_LENGTH;
            RandomAccessFile referenceToM_BinReader =  m_BinReader;
            byte[] buff = Utils.readBytes(referenceToM_BinReader, uiSeekOffset, 4);
            uiSeekOffset += 4;
            byte ConfigurationVersion = buff[0];
            byte AVCProfileIndication = buff[1];
            byte Profile_compatibility = buff[2];
            byte AVCLevelIndication = buff[3];

            buff = Utils.readBytes(referenceToM_BinReader, uiSeekOffset, 1);
            uiSeekOffset += 1;
            byte LenMinusOne = (byte)((buff[0] & 0xFF) & 0x03);

            buff = Utils.readBytes(referenceToM_BinReader, uiSeekOffset, 1);
            uiSeekOffset += 1;

            ((AVC1Atom)m_Parent).getM_AVC1Info().getM_objAVCCInfo().setM_AVCLevelIndication(AVCLevelIndication);
            ((AVC1Atom)m_Parent).getM_AVC1Info().getM_objAVCCInfo().setM_AVCProfileIndication(AVCProfileIndication);
            ((AVC1Atom)m_Parent).getM_AVC1Info().getM_objAVCCInfo().setM_Profile_compatibility(Profile_compatibility);
            ((AVC1Atom)m_Parent).getM_AVC1Info().getM_objAVCCInfo().setM_LenMinusOne(LenMinusOne);
          
            int uiNumOfSequenceParameterSets = (((int)((buff[0] & 0xFF) & 0x1F)) & 0xFFFF);

            int uiSequenceParameterSetLength = 0;
            NALInfo[] nALInfoArr = new NALInfo[uiNumOfSequenceParameterSets];
            ((AVC1Atom)m_Parent).getM_AVC1Info().getM_objAVCCInfo().setM_sequenceParameterSetNALUnit(nALInfoArr);
            for (int i = 0; i < (uiNumOfSequenceParameterSets & 0xFFFF); i++)
            {
                uiSequenceParameterSetLength = Utils.readUInt16(referenceToM_BinReader, uiSeekOffset);
                uiSeekOffset += 2;
                ((AVC1Atom)m_Parent).getM_AVC1Info().getM_objAVCCInfo().getM_sequenceParameterSetNALUnit()[i] = new NALInfo();
                ((AVC1Atom)m_Parent).getM_AVC1Info().getM_objAVCCInfo().getM_sequenceParameterSetNALUnit()[i].setM_uiLength(uiSequenceParameterSetLength & 0xFFFF);
                byte[] uiSequenceParameter = new byte[uiSequenceParameterSetLength];
                m_BinReader.read(uiSequenceParameter,(int)0,(uiSequenceParameterSetLength & 0xFFFF));
                ((AVC1Atom)m_Parent).getM_AVC1Info().getM_objAVCCInfo().getM_sequenceParameterSetNALUnit()[i].setM_data(uiSequenceParameter);

                uiSeekOffset += (uiSequenceParameterSetLength & 0xFFFF); // Skip NAL Sequence unit                                
            }
            buff = Utils.readBytes(referenceToM_BinReader, uiSeekOffset, 1);
            uiSeekOffset += 1;

            int uiPictureParameterSetLength = 0;
            int uiNumOfPictureParameterSets = (buff[0] & 0xFF);
            NALInfo[] uiNumOfPictureParameterArr = new NALInfo[uiNumOfPictureParameterSets];
            
            ((AVC1Atom)m_Parent).getM_AVC1Info().getM_objAVCCInfo().setM_pictureParameterSetNALUnit(uiNumOfPictureParameterArr);
            for (int i = 0; i < (uiNumOfPictureParameterSets & 0xFFFF); i++)
            {
                uiPictureParameterSetLength = Utils.readUInt16(referenceToM_BinReader, uiSeekOffset);
                uiSeekOffset += 2;
                ((AVC1Atom)m_Parent).getM_AVC1Info().getM_objAVCCInfo().getM_pictureParameterSetNALUnit()[i] = new NALInfo();
                ((AVC1Atom)m_Parent).getM_AVC1Info().getM_objAVCCInfo().getM_pictureParameterSetNALUnit()[i].setM_uiLength(uiPictureParameterSetLength & 0xFFFF);
                byte[] uiPictureParameter = new byte[uiPictureParameterSetLength];
                m_BinReader.read(uiPictureParameter,0,(uiPictureParameterSetLength & 0xFFFF));        
                ((AVC1Atom)m_Parent).getM_AVC1Info().getM_objAVCCInfo().getM_pictureParameterSetNALUnit()[i].setM_data(uiPictureParameter);

                uiSeekOffset += (uiPictureParameterSetLength & 0xFFFF); // Skip NAL Parameter unit                               
            }

//            String sMsg = msString.format(" ************ AVC1-Parser NAL unit info : ConfigVersion({0}) , ProfileIndication({1}) , ProfileCompatibility({2}) , LevelIndication ({3}) , LenMinus1 ({4}) SPS count ({5}) SPS Length ({6}) PPS count({7}) PPS Length ({8})",
//                            ConfigurationVersion, AVCProfileIndication, Profile_compatibility, AVCLevelIndication, LenMinusOne, uiNumOfSequenceParameterSets, uiSequenceParameterSetLength, uiNumOfPictureParameterSets, uiPictureParameterSetLength);
//            logInfo(sMsg, LogType.INFORMATION);

            
            return false;
        } catch (IOException ex) {
            Logger.getLogger(AVCCAtom.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}

