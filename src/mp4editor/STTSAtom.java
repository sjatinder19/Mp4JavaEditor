package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.logging.Level;
import mp4editor.pojo.ChunkInfo;
import mp4editor.pojo.DeltaInfo;


class STTSAtom extends Mp4AtomBase
{
    private TRAKAtom m_Track = null;
    STTSAtom(TRAKAtom track)
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

    public boolean generateAtom()
    {
        try {
            long lStart = m_BinWriter.getFilePointer();//getBaseStream().getPosition();
                        
            setAtomID("stts");            
            super.generateAtom();

            long uiEntryCount = (long)m_Track.getM_dictDeltaInfo().size();
            RandomAccessFile referenceToM_BinWriter = m_BinWriter;
            Utils.writeUint32(referenceToM_BinWriter, 0);
            Utils.writeUint32(referenceToM_BinWriter, uiEntryCount);

            byte[] data = new byte[(int)uiEntryCount * 8];
            long uiIndex = 0;
            for (Map.Entry<Long, DeltaInfo> entry :  m_Track.getM_dictDeltaInfo().entrySet()) 
            {
                int iDataIndex = (int)uiIndex * 8;
                int iSampleCount = (int)entry.getValue().getM_uiSampleCount();
                Utils.writeUint32(data,iDataIndex,iSampleCount);
                iDataIndex += 4;
                
                int iSampleDelta = (int)entry.getValue().getM_uiSampleDelta();
                Utils.writeUint32(data,iDataIndex,iSampleDelta);
                iDataIndex += 4;             
                uiIndex++;
            }
            referenceToM_BinWriter.write(data);
            //for (long uiIndex = 1; (uiIndex & 0xFFFFFFFFL) <= (uiEntryCount & 0xFFFFFFFFL); uiIndex++)
            //{
            //    Utils.writeUint32(referenceToM_BinWriter, m_Track.getM_dictDeltaInfo().get(uiIndex).getM_uiSampleCount());
            //    Utils.writeUint32(referenceToM_BinWriter, (long)m_Track.getM_dictDeltaInfo().get(uiIndex).getM_uiSampleDelta());
           // }

         long lStop = m_BinWriter.getFilePointer();//getBaseStream().getPosition();
            if ((lStop - lStart) != (long)getAtomSize())
                ;//log.info("*****  Generation Error. Atom - {0}  ExpectedSize {1} ActualSize {2}"+getAtomID()+" | " +getAtomSize()+" | "+ (lStop - lStart));
            
            return true;
        } catch (IOException ex) {
            //log.error(ex);
            return false;
        }
    }

    private long calcSize()
    {
        long uiSize = 0;

        uiSize += 4; // version
        uiSize += 4; // entrycount
        uiSize += (long)(m_Track.getM_dictDeltaInfo().size() * 8);
        return uiSize;        
    }

    public boolean parseAtoms(long uiFileStart, long uiAtomSize)
    {
        try {
            super.parseAtoms(uiFileStart, uiAtomSize);

            long uiSeekOffset = getFileStart() + Utils.HEADER_LENGTH;
            byte[] tempBuff = new byte[4];

            RandomAccessFile referenceToM_BinReader = m_BinReader;
            long uiVersion = Utils.readUInt32(referenceToM_BinReader, uiSeekOffset);
            uiSeekOffset += 4;

            long entryCount = Utils.readUInt32(referenceToM_BinReader, uiSeekOffset);
            uiSeekOffset += 4;

            int iBuffSize = (int)entryCount *8;
            byte[] data = new byte[iBuffSize];
            referenceToM_BinReader.seek(uiSeekOffset);
            referenceToM_BinReader.read(data, 0, iBuffSize);
            
            for (long uiIndex = 0; (uiIndex & 0xFFFFFFFFL) < (entryCount & 0xFFFFFFFFL); uiIndex++)
            {
                int iBuffIndex= (int)uiIndex*8;
                long sampleCount = Utils.readUInt32(data,  iBuffIndex);
                iBuffIndex += 4;

                int SampleDelta = (int) Utils.readUInt32(data,  iBuffIndex);
                iBuffIndex += 4;

                //This information is not required.
                DeltaInfo info = new DeltaInfo();
                info.setM_uiSampleCount(sampleCount);
                info.setM_uiSampleDelta(SampleDelta);
                m_Track.getM_dictDeltaInfo().put(uiIndex, info);
//                log.info("STTS Info (Sample = {0} - Delta = {1})"+sampleCount+" | "+ SampleDelta);                    
            }
            data=null;
            return true;
        } catch (IOException ex) {
            //log.error(ex);
            return false;
        }
    }
}

