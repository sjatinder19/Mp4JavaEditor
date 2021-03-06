package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.logging.Level;




class CO64Atom extends Mp4AtomBase
{
    private TRAKAtom m_Track = null;
    CO64Atom(TRAKAtom track)
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
        uiSize += (long)(m_Track.getM_dictChunkOffset().size() * 8);
        return uiSize;
    }

    public boolean generateAtom()
    {
        try {
            long lStart = m_BinWriter.getFilePointer();//getBaseStream().getPosition();
                        
            setAtomID("co64");            
            super.generateAtom();

            long uiEntryCount = (long)m_Track.getM_dictChunkOffset().size();
  
            ByteArrayOutputStream stream = new  ByteArrayOutputStream();
            //stream.
            //BufferedWriter writer  = new BufferedWriter();
            
            RandomAccessFile referenceToM_BinWriter = m_BinWriter;
            Utils.writeByte(referenceToM_BinWriter, (byte) 0);
            Utils.writeUint24(referenceToM_BinWriter, 0);
            Utils.writeUint32(referenceToM_BinWriter, uiEntryCount);
 
            byte[] data = new byte[(int)uiEntryCount * 8];
            long uiIndex = 0;
            for (Map.Entry<Long, Long> entry : m_Track.getM_dictChunkOffset().entrySet()) 
            {
                int iDataIndex = (int)uiIndex * 8;
                long offset = entry.getValue();
                Utils.writeUint64(data,iDataIndex,offset);                
                uiIndex++;
            }         
           
            referenceToM_BinWriter.write(data);

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

            RandomAccessFile referenceToM_BinReader = m_BinReader ;
            //long uiVersion = Utils.readUInt32(referenceToM_BinReader, uiSeekOffset);
            uiSeekOffset += 4;

            long uiEntryCount = Utils.readUInt32(referenceToM_BinReader, uiSeekOffset);
            uiSeekOffset += 4;

            int iBuffSize = (int)(uiEntryCount * 8);
            m_Track.m_ChunkOffsetInfo.Init(1, (int)(uiEntryCount));
            referenceToM_BinReader.seek(uiSeekOffset);
            referenceToM_BinReader.read(m_Track.m_ChunkOffsetInfo.m_aarChunkOffset, 0, iBuffSize);
            
            /*for (long uiIndex = 1; (uiIndex & 0xFFFFFFFFL) <= (uiEntryCount & 0xFFFFFFFFL); uiIndex++)
            {

                long uiChunkOffset = Utils.readUInt64(referenceToM_BinReader, uiSeekOffset);
                uiSeekOffset += 8;

                m_Track.getM_dictChunkOffset().put(uiIndex, uiChunkOffset);
            }*/
//            log.info("STCO Info Index {0} ChunkOffset = {1} "+uiIndex+" | "+ uiChunkOffset);
            return true;
        } catch (IOException ex) {
            ;//log.error(ex);
            return false;
        }
    }
}

