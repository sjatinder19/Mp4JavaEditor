package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.logging.Level;


class STSZAtom extends Mp4AtomBase
{
    private long m_uiSampleSize = 0;
    private long m_uiSampleCount = 0;
    private TRAKAtom m_Track = null;
    
    STSZAtom(TRAKAtom track)
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
        uiSize += 4; // SampleSize
        uiSize += 4; // SampleCount

        if ((m_uiSampleSize & 0xFFFFFFFFL) == 0)
        {
            uiSize += (long)(m_Track.getM_dictSampleSize().size() * 4);
        }
        return uiSize;
    }

    public boolean generateAtom()
    {
        try {
            long lStart = (long)m_BinWriter.getFilePointer();//getBaseStream().getPosition();

            m_uiSampleCount = (long)m_Track.getM_dictSampleSize().size();
            setAtomID("stsz");            
            
            super.generateAtom();

            
            RandomAccessFile referenceToM_BinWriter = m_BinWriter; 
            Utils.writeUint32(referenceToM_BinWriter, 0);
            Utils.writeUint32(referenceToM_BinWriter, m_uiSampleSize);
            Utils.writeUint32(referenceToM_BinWriter, m_uiSampleCount);

            if ((m_uiSampleSize & 0xFFFFFFFFL) == 0)
            {                
                byte[] data = new byte[(int)m_uiSampleCount * 4];
                long uiIndex = 0;
                for (Map.Entry<Long, Integer> entry : m_Track.getM_dictSampleSize().entrySet()) 
                {
                    int iDataIndex = (int)uiIndex * 4;
                    int iSize = entry.getValue();
                    Utils.writeUint32(data,iDataIndex,iSize);               
                    uiIndex++;
                }           
                referenceToM_BinWriter.write(data);
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
            //long uiVersion = Utils.readUInt32(referenceToM_BinReader, uiSeekOffset);
            uiSeekOffset += 4;


            m_uiSampleSize = Utils.readUInt32(referenceToM_BinReader, uiSeekOffset);
            uiSeekOffset += 4;


            m_uiSampleCount = Utils.readUInt32(referenceToM_BinReader, uiSeekOffset);
            uiSeekOffset += 4;

            m_Track.m_SampleInfo.m_iSampleCount = m_uiSampleCount;
            
            m_Track.getM_TrackInfo().setM_uiSampleCount(m_uiSampleCount);
            if (m_Track.isPartialParsing) {
                return false;
            }
            if ((m_uiSampleSize & 0xFFFFFFFFL) == 0)
            {
                int iBuffSize = (int)(m_uiSampleCount * 4);
                m_Track.m_SampleSizeInfo.Init((int)(m_uiSampleCount));
                
                referenceToM_BinReader.seek(uiSeekOffset);
                referenceToM_BinReader.read(m_Track.m_SampleSizeInfo.m_aarSampleSize, 0, iBuffSize);
                
                /*for (long uiIndex = 1; (uiIndex & 0xFFFFFFFFL) <= (m_uiSampleCount & 0xFFFFFFFFL); uiIndex++)
                {
                    long uiSampleSize = Utils.readUInt32(referenceToM_BinReader, uiSeekOffset);
                    uiSeekOffset += 4;

                    // Add info to dictionary
                    m_Track.getM_dictSampleSize().put(uiIndex, uiSampleSize);

                    //if (uiIndex < 10)
                     //   LogInfo(String.Format("            STSZ Info TotalSamples = {0} (Sample = {1} - Size = {2})", m_uiSampleCount, uiIndex, uiSampleSize));
                       
                }*/
            }
            else
               ;//log.info("STSZ Info  SampleSize - {0} , SampleCount - {1}"+m_uiSampleSize+m_uiSampleCount);
               
            return false;
        } catch (IOException ex) {
            //log.error(ex);
        }
        return true;
    }
}

