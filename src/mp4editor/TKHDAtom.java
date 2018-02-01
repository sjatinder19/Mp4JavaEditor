package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import mp4editor.pojo.AVC1Info;
import mp4editor.util.LogType;


class TKHDAtom extends Mp4AtomBase
{
    private long m_uiCreation = ((new Date().getTime()) - (new Date(1904,1,1).getTime()))/1000;//(/*UInt64*/long)((TimeSpan)(DateTime.subtract(.Now, new DateTime(1904, 1, 1)))).getTotalSeconds();
    private long m_uiModification = ((new Date().getTime()) - (new Date(1904,1,1).getTime()))/1000;// = (/*UInt64*/long)((TimeSpan)(DateTime.subtract(.Now, new DateTime(1904, 1, 1)))).getTotalSeconds();                
    private long[] m_qrruiReserved = {0, 0};                
    private long[]m_arruiMatrix = {0x00010000,0,0,0,0x00010000,0,0,0,0x40000000 };
    
    private TRAKAtom m_Track = null;
    TKHDAtom(TRAKAtom track)
    {
        m_Track = track;
    }
    
    public long getAtomSize()
    {
        super.setAtomSize(84 + (/*ulong*/long)(((getVersion() & 0xFFFF) == 1) ? 12 : 0) + Utils.HEADER_LENGTH);
        return super.getAtomSize();
    }
    public void setAtomSize(long value)
    {
        super.setAtomSize(value);
    }
//
    public boolean generateAtom()
    {
        try {
            long lStart = m_BinWriter.getFilePointer();//getBaseStream().getPosition();

            setAtomID("tkhd");            
            super.generateAtom();

            RandomAccessFile referenceToM_BinWriter = m_BinWriter;
            Utils.writeByte(referenceToM_BinWriter, (byte)(getVersion() & 0xFFFF));
            Utils.writeUint24(referenceToM_BinWriter, 1);
            if ((getVersion() & 0xFFFF) == 0)
            {
                Utils.writeUint32(referenceToM_BinWriter, (long)m_uiCreation);
                Utils.writeUint32(referenceToM_BinWriter, (long)m_uiModification);
            }
            else
            {
                Utils.writeUUInt64(referenceToM_BinWriter, m_uiCreation);
                Utils.writeUUInt64(referenceToM_BinWriter, m_uiModification);
            }
            Utils.writeUint32(referenceToM_BinWriter, m_Track.getM_TrackInfo().getM_uiTrack_ID());
            Utils.writeUint32(referenceToM_BinWriter, 0);
            if ((getVersion() & 0xFFFF) == 0)
                {
                Utils.writeUint32(referenceToM_BinWriter, (long)((MOOVAtom)m_Track.m_Parent).m_uiMOOVDuration);
                }  // Duration
            else
                {
                Utils.writeUUInt64(referenceToM_BinWriter, ((MOOVAtom)m_Track.m_Parent).m_uiMOOVDuration);
                }
            
            Utils.writeUint32(referenceToM_BinWriter, m_qrruiReserved[0]);
            Utils.writeUint32(referenceToM_BinWriter, m_qrruiReserved[1]);
            Utils.writeUint16(referenceToM_BinWriter, 0);
            Utils.writeUint16(referenceToM_BinWriter, 0);

            if(m_Track.getTRACK_TYPE().equalsIgnoreCase("TRAK_AUDIO"))
                {
                Utils.writeUint16(referenceToM_BinWriter, 256);
                }
            else
                {
                Utils.writeUint16( referenceToM_BinWriter, 0);
                }
            Utils.writeUint16(referenceToM_BinWriter, 0);

            for(int i= 0;i<9;i++)
                {            
                Utils.writeUint32(referenceToM_BinWriter, m_arruiMatrix[i]);
                } // Fixed Matrix


            if (m_Track.getTRACK_TYPE().equalsIgnoreCase("TRAK_VIDEO"))
            {
                Utils.writeUint32(referenceToM_BinWriter, (((long)(((AVC1Info)m_Track.getCODEC_INFO().getM_codecInfo()).getM_uiWidth() & 0xFFFF) <<(( 16) & 0x1F)) & 0xFFFFFFFFL));
                Utils.writeUint32(referenceToM_BinWriter, (((long)(((AVC1Info)m_Track.getCODEC_INFO().getM_codecInfo()).getM_uiHeight() & 0xFFFF) <<(( 16) & 0x1F)) & 0xFFFFFFFFL));
            }
            else
            {
                Utils.writeUint32(referenceToM_BinWriter, 0);
                Utils.writeUint32(referenceToM_BinWriter, 0);
            }

            long lStop = m_BinWriter.getFilePointer();//getBaseStream().getPosition();
            if ((lStop - lStart) != (long)getAtomSize()){
//            log.info("*****  Generation Error. Atom - {0}  ExpectedSize {1} ActualSize {2}"+getAtomID()+" | " +getAtomSize()+" | "+ (lStop - lStart));
                m_Track.printLog(LogType.information, "*****  Generation Error. Atom - {0}  ExpectedSize {1} ActualSize {2}" + getAtomID() + " | " + getAtomSize() + " | " + (lStop - lStart), null);
            }
            return true;
        } catch (IOException ex) {
            m_Track.printLog(LogType.error, "mp4editor.TKHDAtom | ", ex);
            return false;
        }
    }

    public boolean parseAtoms(long uiFileStart, long uiAtomSize)
    {
        try {
            super.parseAtoms(uiFileStart, uiAtomSize);

            long uiSeekOffset = getFileStart() + Utils.HEADER_LENGTH;
            byte[] tempBuff = new byte[4];
            m_BinReader.seek((long)uiSeekOffset);
            m_BinReader.read(tempBuff, 0, 1);
            long uiVersion = (tempBuff[0] & 0xFF);
            uiSeekOffset += 1;

            RandomAccessFile referenceToM_BinReader = m_BinReader;
            long uiFlags = Utils.readUInt24(referenceToM_BinReader, uiSeekOffset);
            uiSeekOffset += 3;

            if ((uiVersion & 0xFFFFFFFFL) == 0)
            {
                m_uiCreation = (Utils.readUInt32(referenceToM_BinReader, uiSeekOffset) & 0xFFFFFFFFL);
                uiSeekOffset += 4;

                m_uiModification = (Utils.readUInt32(referenceToM_BinReader, uiSeekOffset) & 0xFFFFFFFFL);
                uiSeekOffset += 4;
            }
            else
            {
                m_uiCreation = Utils.readUInt64(referenceToM_BinReader, uiSeekOffset);
                uiSeekOffset += 8;

                m_uiModification = Utils.readUInt64(referenceToM_BinReader, uiSeekOffset);
                uiSeekOffset += 8;
            }
            m_Track.getM_TrackInfo().setM_uiTrack_ID(Utils.readUInt32(referenceToM_BinReader, uiSeekOffset));
            uiSeekOffset += 4;

            uiSeekOffset += 4; // 8 bytes reserved

            long uiDuration = 0;
            if ((uiVersion & 0xFFFFFFFFL) == 0)
            {
                uiDuration = (Utils.readUInt32(referenceToM_BinReader, uiSeekOffset) & 0xFFFFFFFFL);
                uiSeekOffset += 4;
            }
            else 
            {
                uiDuration = Utils.readUInt64(referenceToM_BinReader, uiSeekOffset);
                uiSeekOffset += 8;
            }

            uiSeekOffset += 8; // 8 bytes reserved
            int layer = Utils.readUInt16(referenceToM_BinReader, uiSeekOffset);
            uiSeekOffset += 2;
            int alternateGroup = Utils.readUInt16(referenceToM_BinReader, uiSeekOffset);
            uiSeekOffset += 2;
            int volume = Utils.readUInt16(referenceToM_BinReader, uiSeekOffset);
            uiSeekOffset += 2;

            uiSeekOffset += 2;// reserved

            uiSeekOffset += 36; //Matrix
            long uiWidth = (((long)Utils.readUInt32(referenceToM_BinReader, uiSeekOffset)) & 0xFFFFFFFFL) >> 16;
            uiSeekOffset += 4;

            long uiHeight = (((long)Utils.readUInt32(referenceToM_BinReader, uiSeekOffset)) & 0xFFFFFFFFL) >> 16;
            uiSeekOffset += 4;
//            logInfo(msString.format("      TKHDAtom  Creation = {0} Modify = {1} TrackID = {2} Duration {3} Volume {4} Width {5} Height {6}", m_uiCreation, m_uiModification, m_Track.m_TrackInfo.m_uiTrack_ID, uiDuration, volume, uiWidth, uiHeight), LogType.INFORMATION);
            return false;
        } catch (IOException ex) {
            m_Track.printLog(LogType.error, "mp4editor.TKHDAtom | ", ex);
        }
        return false;
    }
}


