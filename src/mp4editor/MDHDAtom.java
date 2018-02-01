package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


class MDHDAtom extends Mp4AtomBase
{
    private long m_uiCreation = ((new Date().getTime()) - (new Date(1904,1,1).getTime()))/1000;//= (/*UInt64*/long)((TimeSpan)(DateTime.subtract(.Now, new DateTime(1904, 1, 1)))).getTotalSeconds();
    private long m_uiModification = ((new Date().getTime()) - (new Date(1904,1,1).getTime()))/1000;// = (/*UInt64*/long)((TimeSpan)(DateTime.subtract(.Now, new DateTime(1904, 1, 1)))).getTotalSeconds();
    
    private TRAKAtom m_Track = null;
    MDHDAtom(TRAKAtom track)
    {
        m_Track = track;
    }
   
    public long getAtomSize()
    {
        super.setAtomSize(24 + (long)(((getVersion() & 0xFFFF) == 1) ? 12 : 0) + Utils.HEADER_LENGTH);
        return super.getAtomSize();
    }
    public void setAtomSize(long value)
    {
        super.setAtomSize(value);
    }
   
    public boolean generateAtom()
    {
        try {
            long lStart = m_BinWriter.getFilePointer();//getBaseStream().getPosition();

            setAtomID("mdhd");            
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
            Utils.writeUint32(referenceToM_BinWriter, m_Track.getM_TrackInfo().getM_uiTimeScale());

            if ((getVersion() & 0xFFFF) == 0)
                {
                Utils.writeUint32(referenceToM_BinWriter, (long)m_Track.getM_TrackInfo().getM_uiDuration());
                } // duration
            else
                {
                Utils.writeUUInt64(referenceToM_BinWriter, m_Track.getM_TrackInfo().getM_uiDuration());
                } // duration

            // 55 c4 00 00 
            Utils.writeByte(referenceToM_BinWriter, (byte) 0x00);
            Utils.writeByte(referenceToM_BinWriter, (byte) 0x00);
            Utils.writeByte(referenceToM_BinWriter, (byte) 0x00);
            Utils.writeByte(referenceToM_BinWriter, (byte) 0x00);


            long lStop = m_BinWriter.getFilePointer();//getBaseStream().getPosition();
            if ((lStop - lStart) != (long)getAtomSize())
                ;//log.info("*****  Generation Error. Atom - {0}  ExpectedSize {1} ActualSize {2}"+getAtomID()+" | "+ getAtomSize()+" | "+(lStop - lStart));
            
            return true;
        } catch (IOException ex) {
            //log.error(ex);
            return false;
        }
    }

    public boolean parseAtoms(long uiFileStart, long uiAtomSize)
    {
        try {
            super.parseAtoms(uiFileStart, uiAtomSize);

            long uiSeekOffset = getFileStart() + Utils.HEADER_LENGTH;
            RandomAccessFile referenceToM_BinReader = m_BinReader;
            byte[] tempBuff = Utils.readBytes(referenceToM_BinReader, uiSeekOffset, 4);
            
            long uiVersion = (tempBuff[0] & 0xFF);
            uiSeekOffset += 4;

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

            m_Track.getM_TrackInfo().setM_uiTimeScale(Utils.readUInt32(referenceToM_BinReader, uiSeekOffset));
            uiSeekOffset += 4;

            if ((uiVersion & 0xFFFFFFFFL) == 0)
            {
                m_Track.getM_TrackInfo().setM_uiDuration(Utils.readUInt32(referenceToM_BinReader, uiSeekOffset) & 0xFFFFFFFFL);
                uiSeekOffset += 4;
            }
            else 
            {
                m_Track.getM_TrackInfo().setM_uiDuration(Utils.readUInt64(referenceToM_BinReader, uiSeekOffset));
                uiSeekOffset += 8;
            }
//            logInfo(msString.format("        MDHD  creation {0} , Modification {1} , TimeScale  {2} , Duration  {3} ({4} seconds)", m_uiCreation, m_uiModification, m_Track.m_TrackInfo.m_uiTimeScale, m_Track.m_TrackInfo.m_uiDuration, (float)m_Track.m_TrackInfo.m_uiDuration / (m_Track.m_TrackInfo.m_uiTimeScale & 0xFFFFFFFFL)), LogType.INFORMATION);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(MDHDAtom.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}

