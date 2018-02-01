package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import mp4editor.pojo.ChunkInfo;


class STSCAtom extends Mp4AtomBase
{
    private TRAKAtom m_Track = null;
    STSCAtom(TRAKAtom track)
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
        uiSize += (long)(m_Track.getM_dictChunkInfo().size() * 12);
        return uiSize;
    }

    public boolean generateAtom()
    {
        try {
            long lStart = m_BinWriter.getFilePointer();//getBaseStream().getPosition();
                        
            setAtomID("stsc");           
            super.generateAtom();

            long uiEntryCount = (long)m_Track.getM_dictChunkInfo().size();
            RandomAccessFile referenceToM_BinWriter = m_BinWriter;
            Utils.writeUint32(referenceToM_BinWriter, 0);
            Utils.writeUint32(referenceToM_BinWriter, uiEntryCount);

            byte[] data = new byte[(int)uiEntryCount * 12];
            long uiIndex = 0;
            for (Map.Entry<Long, ChunkInfo> entry :  m_Track.getM_dictChunkInfo().entrySet()) 
            {
                int iDataIndex = (int)uiIndex * 12;
                int iChunkNo = (int)entry.getValue().getM_uiChunkNo();
                Utils.writeUint32(data,iDataIndex,iChunkNo);
                iDataIndex += 4;
                
                int iSampleCount = (int)entry.getValue().getM_uiSampleCount();
                Utils.writeUint32(data,iDataIndex,iSampleCount);
                iDataIndex += 4;                
                
                Utils.writeUint32(data,iDataIndex,1);
                uiIndex++;
            }    
            referenceToM_BinWriter.write(data);
            
            /*Set keySet = m_Track.getM_dictChunkInfo().keySet();
            Iterator keyItr = keySet.iterator();
            while(keyItr.hasNext()){
                Object keyObj = keyItr.next();
                
                Utils.writeUint32(referenceToM_BinWriter, m_Track.getM_dictChunkInfo().get(keyObj).getM_uiChunkNo());
                Utils.writeUint32(referenceToM_BinWriter, m_Track.getM_dictChunkInfo().get(keyObj).getM_uiSampleCount());
                Utils.writeUint32(referenceToM_BinWriter, 1);
            }*/
            
//            for ( KeyValuePair<Long, ChunkInfo> kp : (Iterable<KeyValuePair<Long, ChunkInfo>>) m_Track.m_dictChunkInfo)
//            {
//                referenceToM_BinWriter[0] = m_BinWriter;
//                Utils.writeUint32(/*ref*/ referenceToM_BinWriter, kp.Value.m_uiChunkNo);
//                m_BinWriter = referenceToM_BinWriter[0];
//                referenceToM_BinWriter[0] = m_BinWriter;
//                Utils.writeUint32(/*ref*/ referenceToM_BinWriter, kp.Value.m_uiSampleCount);
//                m_BinWriter = referenceToM_BinWriter[0];
//                referenceToM_BinWriter[0] = m_BinWriter;
//                Utils.writeUint32(/*ref*/ referenceToM_BinWriter, kp.Value.m_uiDescIndex);
//                m_BinWriter = referenceToM_BinWriter[0];
//            }


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
            long uiEntryCount = Utils.readUInt32(referenceToM_BinReader, uiSeekOffset);
            uiSeekOffset += 4;
                   
            int iBuffSize = (int)(uiEntryCount * 12);
            m_Track.m_ChunkInfo.Init((int)(uiEntryCount));
            referenceToM_BinReader.seek(uiSeekOffset);
            referenceToM_BinReader.read(m_Track.m_ChunkInfo.m_aarChunkInfo, 0, iBuffSize);
            
            
            /*for (int iIndex = 0; iIndex < (uiEntryCount & 0xFFFFFFFFL); iIndex++)
            {
                long firstChunk = Utils.readUInt32(referenceToM_BinReader, uiSeekOffset);
                uiSeekOffset += 4;

                long Sample_per_chunk = Utils.readUInt32( referenceToM_BinReader, uiSeekOffset);
                uiSeekOffset += 4;

                long Sample_desc_index = Utils.readUInt32(referenceToM_BinReader, uiSeekOffset);
                uiSeekOffset += 4; 

                ChunkInfo info = new ChunkInfo();
                info.setM_uiSampleCount(Sample_per_chunk);
                info.setM_uiDescIndex(Sample_desc_index);
                info.setM_uiChunkNo(firstChunk);                
//                m_Track.m_dictCunkNo = firstChunk;                
                m_Track.getM_dictChunkInfo().put(firstChunk, info.Clone());
                               
                //if (iIndex < 10)
                  // LogInfo(String.Format("            STSC Info (Chunk = {0} - Samples Per Chunk = {1} - SampleDescIndex = {2} ", firstChunk, Sample_per_chunk, Sample_desc_index));
                 
                
            }*/
            return false;
        } catch (IOException ex) {
            Logger.getLogger(STSCAtom.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
}

