package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import mp4editor.pojo.CompTimeInfo;



   
// Composition Time to Sample Box
class CTTSAtom extends Mp4AtomBase
{
    
    private TRAKAtom m_Track = null;
    CTTSAtom(TRAKAtom track)
    {
        this.m_Track = track;
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
        uiSize += (long)(m_Track.getM_dictCttsInfo().size() * 8);
        return uiSize;        
    }
    
    public boolean generateAtom() 
    {
        try {
            long lStart = m_BinWriter.getFilePointer();//getBaseStream().getPosition();

            setAtomID("ctts");            
            super.generateAtom();
            
            long uiEntryCount = (long)m_Track.getM_dictCttsInfo().size();
            RandomAccessFile referenceToM_BinWriter = m_BinWriter;
            Utils.writeUint32(referenceToM_BinWriter, 0);
            Utils.writeUint32(referenceToM_BinWriter, uiEntryCount);

            byte[] data = new byte[(int)uiEntryCount * 8];
            long uiIndex = 0;
           
            for (Map.Entry<Long, CompTimeInfo> entry : m_Track.getM_dictCttsInfo().entrySet())
            {
                int iDataIndex = (int)uiIndex * 8;
                int samplecount = (int)entry.getValue().getUiSampleCount();
                Utils.writeUint32(data,iDataIndex,samplecount);                 
                iDataIndex+=4;
                
                int offset = (int)entry.getValue().getUiOffset();
                Utils.writeUint32(data,iDataIndex,offset);                 
                iDataIndex+=4;               
                
                uiIndex++;
            }
            referenceToM_BinWriter.write(data);
            //for (long uiIndex = 0; (uiIndex & 0xFFFFFFFFL) < (uiEntryCount & 0xFFFFFFFFL); uiIndex++)
            {
                //Utils.writeUint32(referenceToM_BinWriter, m_Track.getM_dictCttsInfo().get(uiIndex).getUiSampleCount());
                //Utils.writeUint32(referenceToM_BinWriter, m_Track.getM_dictCttsInfo().get(uiIndex).getUiOffset());
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

            RandomAccessFile referenceToM_BinReader = m_BinReader;
            long uiVersion = Utils.readUInt32(referenceToM_BinReader, uiSeekOffset);
            uiSeekOffset += 4;

            long entryCount = Utils.readUInt32(referenceToM_BinReader, uiSeekOffset);
            uiSeekOffset += 4;

            long uiTotal = 0;
            for (long uiIndex = 1; (uiIndex & 0xFFFFFFFFL) <= (entryCount & 0xFFFFFFFFL); uiIndex++)
            {
                long uiSampleCount = Utils.readUInt32(referenceToM_BinReader, uiSeekOffset);
                uiSeekOffset += 4;

                uiTotal = (uiTotal & 0xFFFFFFFFL) + ((uiSampleCount & 0xFFFFFFFFL));

                long uiSampleOffSet = Utils.readUInt32(referenceToM_BinReader, uiSeekOffset);
                uiSeekOffset += 4;

                //This information is not used                
                CompTimeInfo info = new CompTimeInfo();
                info.setUiOffset((int)uiSampleOffSet);
                info.setUiSampleCount(uiSampleCount);
                m_Track.getM_dictCttsInfo().put(uiIndex, info);
//                log.info("CTTS Info SampleCount = {0} Sample_Offset = {1}  Total {2}"+uiSampleCount+" | "+uiSampleOffSet+" | "+ uiTotal);
               
            }
            return true;
        } catch (IOException ex) {
            ;//log.error(ex);
            return false;
        }
    }
}

