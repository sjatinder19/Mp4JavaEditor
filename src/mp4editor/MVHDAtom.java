package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import mp4editor.util.LogType;
import mp4editor.util.LoggingEvents;


public class MVHDAtom extends Mp4AtomBase
{        
    private long m_uiCreationTime = ((new Date().getTime()) - (new Date(1904,1,1).getTime()))/1000;  //DateTime.subtract(.Now, new DateTime(1904,1,1)))).getTotalSeconds();
//    private Calendar calander;
    private long m_uiModificationTime = ((new Date().getTime()) - (new Date(1904,1,1).getTime()))/1000;//= (long)((TimeSpan)(DateTime.subtract(.Now, new DateTime(1904, 1, 1)))).getTotalSeconds();        
    private long m_uiRate = 0x00010000; // typically 1.0
    private int m_uiVolume = 0x0100; // typically, full volume
    private int[] m_arruiMatrix = { 0x00010000, 0, 0, 0, 0x00010000, 0, 0, 0, 0x40000000 };
    private long m_uiNextTrackId = 1;       
    LoggingEvents logging = null;
    
    public void addLoggingListener(LoggingEvents logging){
        this.logging = logging;
    }
    
    public void printLog(LogType logType, String message, Throwable th){
        if(logging!=null){
            logging.printLog(logType, th, message);
        }
    }
    
    public long getAtomSize()
    {
        super.setAtomSize(100 + (long)(((getVersion() & 0xFFFF) == 1) ? 12 : 0) + Utils.HEADER_LENGTH);
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

            setAtomID("mvhd");
            super.generateAtom();

            RandomAccessFile referenceToM_BinWriter = m_BinWriter;
            Utils.writeByte(referenceToM_BinWriter, (byte)(getVersion() & 0xFFFF));
            Utils.writeUint24(referenceToM_BinWriter, 1);
            if ((getVersion() & 0xFFFF) == 0)
            {
                Utils.writeUint32(referenceToM_BinWriter,(long) m_uiCreationTime);
                Utils.writeUint32(referenceToM_BinWriter, (long)m_uiModificationTime);
            }
            else
            {
                Utils.writeUUInt64(referenceToM_BinWriter, m_uiCreationTime);
                Utils.writeUUInt64(referenceToM_BinWriter, m_uiModificationTime);
            }
            Utils.writeUint32(referenceToM_BinWriter, ((MOOVAtom)m_Parent).m_uiMOOVTimeScale);

            if ((getVersion() & 0xFFFF) == 0)
                {
                Utils.writeUint32(referenceToM_BinWriter,(long) ((MOOVAtom)m_Parent).m_uiMOOVDuration);
                }
            else
                {
                Utils.writeUUInt64(referenceToM_BinWriter, ((MOOVAtom)m_Parent).m_uiMOOVDuration);
                }

            Utils.writeUint32(referenceToM_BinWriter, m_uiRate);
            Utils.writeUint16(referenceToM_BinWriter, m_uiVolume);
            Utils.writeUint16(referenceToM_BinWriter, 0);
            Utils.writeUint32(referenceToM_BinWriter, 0);
            Utils.writeUint32(referenceToM_BinWriter, 0);
            for (int i = 0; i < m_arruiMatrix.length; i++ )
                {
                Utils.writeUint32(referenceToM_BinWriter, m_arruiMatrix[i]);
                } // reserved

            Utils.writeUint32(referenceToM_BinWriter, 0);
            Utils.writeUint32(referenceToM_BinWriter, 0);
            Utils.writeUint32(referenceToM_BinWriter, 0);
            Utils.writeUint32(referenceToM_BinWriter, 0);
            Utils.writeUint32(referenceToM_BinWriter, 0);
            Utils.writeUint32(referenceToM_BinWriter, 0);
            Utils.writeUint32(referenceToM_BinWriter, m_uiNextTrackId);

            long lStop = m_BinWriter.getFilePointer();//getBaseStream().getPosition();
            if ((lStop - lStart) != (long)getAtomSize()){
                //log.info("*****  Generation Error. Atom - {0}  ExpectedSize {1} ActualSize {2}"+getAtomID()+" | " +getAtomSize()+" | "+ (lStop - lStart));
                printLog(LogType.information, "mp4editor.MVHDAtom | "+"*****  Generation Error. Atom - {0}  ExpectedSize {1} ActualSize {2}"+getAtomID()+" | " +getAtomSize()+" | "+(lStop - lStart), null);
            }
            
            return true;
        } catch (IOException ex) {
            printLog(LogType.error, "mp4editor.MVHDAtom | ", ex);
            return false;
        }
    }

    public boolean parseAtoms(long uiFileStart, long uiAtomSize) 
    {
        try {
            super.parseAtoms(uiFileStart, uiAtomSize);

            long uiSeekOffset = getFileStart() + Utils.HEADER_LENGTH;
            long uilen = getAtomSize();

            RandomAccessFile referenceToM_BinReader = m_BinReader;
            byte[] tempBuff = Utils.readBytes(referenceToM_BinReader, uiSeekOffset, 4);
            long uiVersion = (tempBuff[0] & 0xFF);
            uiSeekOffset += 4;

            if ((uiVersion & 0xFFFFFFFFL) == 0)
            {
                m_uiCreationTime = (Utils.readUInt32(referenceToM_BinReader, uiSeekOffset) & 0xFFFFFFFFL);
                uiSeekOffset += 4;

                m_uiModificationTime = (Utils.readUInt32(referenceToM_BinReader, uiSeekOffset) & 0xFFFFFFFFL);
                uiSeekOffset += 4;
            }
            else 
            {
                m_uiCreationTime = Utils.readUInt64(referenceToM_BinReader, uiSeekOffset);
                uiSeekOffset += 8;

                m_uiModificationTime = Utils.readUInt64(referenceToM_BinReader, uiSeekOffset);
                uiSeekOffset += 8;
            }

            ((MOOVAtom)m_Parent).m_Mp4FileInfo.setM_uiTimeScale(Utils.readUInt32(referenceToM_BinReader, uiSeekOffset));
            uiSeekOffset += 4;
            
            if ((uiVersion & 0xFFFFFFFFL) == 0)
            {
                ((MOOVAtom)m_Parent).m_Mp4FileInfo.setM_uiDuration(Utils.readUInt32(referenceToM_BinReader, uiSeekOffset) & 0xFFFFFFFFL);

                uiSeekOffset += 4;
            }
            else
            {
                ((MOOVAtom)m_Parent).m_Mp4FileInfo.setM_uiDuration(Utils.readUInt64(referenceToM_BinReader, uiSeekOffset));
                uiSeekOffset += 8;
            }

            long uiRate = Utils.readUInt32(referenceToM_BinReader, uiSeekOffset);
            uiSeekOffset += 4;

            long uiVolume = (Utils.readUInt16(referenceToM_BinReader, uiSeekOffset) & 0xFFFF);
            uiSeekOffset += 2;

            uiSeekOffset += 2; // reserved =0

            uiSeekOffset += 8; // reserved =0

            uiSeekOffset += 36; // reserved =0

            uiSeekOffset += 24; // reserved =0

            long uiNextTrackID = Utils.readUInt32(referenceToM_BinReader, uiSeekOffset);
            //((MOOVAtom)m_Parent).m_Mp4FileInfo.m_uiNextTrackID = uiNextTrackID;
            uiSeekOffset += 4;

//            log.info("    MVHD  - CreationTime {0} , ModifyTime {1} , TimeScale {2} , Duration {3} ({4} seconds) , Rate {5} , Volume {6} , NextTrackID {7}", m_uiCreationTime+ m_uiModificationTime+ ((MOOVAtom)m_Parent).m_Mp4FileInfo.getM_uiTimeScale()+ ((MOOVAtom)m_Parent).m_Mp4FileInfo.getM_uiDuration()+ (float)((MOOVAtom)m_Parent).m_Mp4FileInfo.getM_uiDuration() / (((MOOVAtom)m_Parent).m_Mp4FileInfo.m_uiTimeScale & 0xFFFFFFFFL), uiRate, uiVolume, uiNextTrackID), LogType.INFORMATION);
            return false;
        } catch (IOException ex) {
            printLog(LogType.error, "mp4editor.MVHDAtom | ", ex);
        }
        return false;
    }

    public long getM_uiNextTrackId() {
        return m_uiNextTrackId;
    }

    public void setM_uiNextTrackId(long m_uiNextTrackId) {
        this.m_uiNextTrackId = m_uiNextTrackId;
    }
    
    
}

