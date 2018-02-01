package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;


class STSSAtom extends Mp4AtomBase
{             
    private TRAKAtom m_Track = null;
    STSSAtom(TRAKAtom track )
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

        uiSize += 4; // version
        uiSize += 4; // entrycount
        uiSize += (long)(m_Track.getM_queueRandomAccessSampNos().size() * 4);
        return uiSize;
    }

    public boolean generateAtom()
    {
        try {
            long lStart = m_BinWriter.getFilePointer();//getBaseStream().getPosition();
            
            setAtomID("stss");            
            super.generateAtom();

            long uiEntryCount = (long)m_Track.getM_queueRandomAccessSampNos().size();//Count;
            RandomAccessFile referenceToM_BinWriter = m_BinWriter;
            Utils.writeUint32(referenceToM_BinWriter, 0);
            Utils.writeUint32(referenceToM_BinWriter, uiEntryCount);
//            long[] arrRandomPts = new long[m_Track.getM_queueRandomAccessSampNos().size()];
//            m_Track.getM_queueRandomAccessSampNos().ensureCapacity(arrRandomPts.length);
            for(long uiVal : m_Track.getM_queueRandomAccessSampNos())
                {
                Utils.writeUint32(referenceToM_BinWriter, uiVal);
                }
            
            long lStop = m_BinWriter.getFilePointer();//getBaseStream().getPosition();
            if ((lStop - lStart) != (long)getAtomSize())
                ;//log.info("*****  Generation Error. Atom - {0}  ExpectedSize {1} ActualSize {2}"+getAtomID()+" | " +getAtomSize()+" | "+ (lStop - lStart));
            
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
            byte[] tempBuff = new byte[4];

            RandomAccessFile referenceToM_BinReader =  m_BinReader;
            long uiVersion = Utils.readUInt32(referenceToM_BinReader, uiSeekOffset);
            uiSeekOffset += 4;


            long uiEntryCount = Utils.readUInt32(referenceToM_BinReader, uiSeekOffset);
            uiSeekOffset += 4;

            int iBuffSize = (int)uiEntryCount *4;
            byte[] data = new byte[iBuffSize];
            referenceToM_BinReader.seek(uiSeekOffset);
            referenceToM_BinReader.read(data, 0, iBuffSize);
            
            for (long uiIndex = 0; (uiIndex & 0xFFFFFFFFL) < (uiEntryCount & 0xFFFFFFFFL); uiIndex++)
            {
                int iBuffIndex = (int)uiIndex*4;
                long uiSampleNum = Utils.readUInt32(data,  iBuffIndex);
                
                m_Track.getM_queueRandomAccessSampNos().add(uiSampleNum);
            }
            data=null;
            return false;
        } catch (IOException ex) {
            Logger.getLogger(STSSAtom.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}

