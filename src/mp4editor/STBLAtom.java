package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.logging.Level;
import mp4editor.pojo.ChunkInfo;



class STBLAtom extends Mp4AtomBase
{        
    private TRAKAtom m_Track = null;
    
    
    STBLAtom(TRAKAtom track)
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

    public int getVersion()
    {
        return super.getVersion();
    }
    public void setVersion(int value)
    {
        java.util.Set<String> keySet = m_dictChildAtoms.keySet();
         Iterator keyItr = keySet.iterator();
         while(keyItr.hasNext()){
             String key = (String) keyItr.next();
             Mp4AtomBase mp4AtomBase = m_dictChildAtoms.get(key);
             mp4AtomBase.setVersion(value);
         }
         
//        for (KeyValuePair<String, Mp4AtomBase> kv : (Iterable<KeyValuePair<String,Mp4AtomBase>>) m_dictChildAtoms)
//            kv.Value.setVersion(value);
        super.setVersion(value);
    }


    private long calcSize()
    {
        long uiSize = 0;
        java.util.Set<String> keySet = m_dictChildAtoms.keySet();
         Iterator keyItr = keySet.iterator();
         while(keyItr.hasNext()){
             String key = (String) keyItr.next();
             Mp4AtomBase mp4AtomBase = m_dictChildAtoms.get(key);
             uiSize += mp4AtomBase.getAtomSize();
         }
//        for (KeyValuePair<String, Mp4AtomBase> kp : (Iterable<KeyValuePair<String,Mp4AtomBase>>) m_dictChildAtoms)
//            uiSize += kp.Value.getAtomSize();           
        return uiSize;
    }

    public boolean endWriting()
    {
        // Add expected child atoms
        addAtom("stsd"); // Expected Atom
        addAtom("stts"); // Expected Atom
        if(m_Track.getM_dictCttsInfo().size()>1){
            addAtom("ctts");
        }
        if (m_Track.getTRACK_TYPE().equalsIgnoreCase("TRAK_VIDEO"))
            addAtom("stss"); // Expected Atom
        addAtom("stsc"); // Expected Atom
        addAtom("stsz"); // Expected Atom

        if ((m_Track.getVersion() & 0xFFFF) == 0)
            addAtom("stco"); // Expected Atom
        else
            addAtom("co64"); // Expected Atom
        
        return super.endWriting();
    }
    
    public boolean generateAtom()
    {
        try {
            long lStart = m_BinWriter.getFilePointer();//getBaseStream().getPosition();

            setAtomID("stbl");            
            super.generateAtom();

            Set keySet = m_dictChildAtoms.keySet();
            Iterator keyItr = keySet.iterator();
            while(keyItr.hasNext()){
                Object keyObj = keyItr.next();
                RandomAccessFile referenceToM_BinWriter = m_BinWriter;
                m_dictChildAtoms.get(keyObj).setWriter(referenceToM_BinWriter);
                m_dictChildAtoms.get(keyObj).generateAtom();
            }
            
//            for (KeyValuePair<String, Mp4AtomBase> kp : (Iterable<KeyValuePair<String,Mp4AtomBase>>) m_dictChildAtoms)
//            {
//                BinaryWriter[] referenceToM_BinWriter = { m_BinWriter };
//                kp.Value.setWriter(/*ref*/ referenceToM_BinWriter);
//                m_BinWriter = referenceToM_BinWriter[0];
//                kp.Value.generateAtom();
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

    private boolean addAtom(String sAtomID)
    {
        Mp4AtomBase atom = null;
        switch (sAtomID)
        {
            case "stsd":
                {
                    atom = new STSDAtom(m_Track);                       
                    break;
                }

            case "stts":
                {
                    atom = new STTSAtom(m_Track);
                    break;
                }

            case "stss":
                {
                    atom = new STSSAtom(m_Track);
                    break;
                }

            case "stsc":
                {
                    atom = new STSCAtom(m_Track);
                    break;
                }

            case "stsz":
                {
                    atom = new STSZAtom(m_Track);
                    break;
                }
            case "stco":
                {
                    atom = new STCOAtom(m_Track);
                    break;
                }

            case "co64":
                {
                    atom = new CO64Atom(m_Track);
                    break;
                }
            case "ctts":
                {
                    atom = new CTTSAtom(m_Track);
                    break;
                }

             default:
                {
                    break;
                }
        }

        if (atom != null)
        {
            atom.setAtomID(sAtomID);
            atom.m_Parent = this;               
            m_dictChildAtoms.put(sAtomID, atom);
        }
        return false;
    }

    public boolean parseAtoms(long uiFileStart, long uiAtomSize)
    {
        super.parseAtoms(uiFileStart, uiAtomSize);

        long uiFileOffset = getFileStart() + Utils.HEADER_LENGTH;
        long uiAtomEnd = getFileEnd();
        long uiAtomLength = 0;
        char[] Atom_ID = null;
        while (uiFileOffset < uiAtomEnd)
        {
            try {
                RandomAccessFile referenceToM_BinReader = m_BinReader;
                byte[] MpegBoxBuff = Utils.readBytes(referenceToM_BinReader, uiFileOffset, (int)Utils.HEADER_LENGTH);
              
                long[] referenceToUiAtomLength = { uiAtomLength };
                char[][] referenceToAtom_ID = { Atom_ID };
                Utils.readAtom(MpegBoxBuff, referenceToUiAtomLength, referenceToAtom_ID);
                uiAtomLength = referenceToUiAtomLength[0];
                Atom_ID = referenceToAtom_ID[0];
                String sAtomID = new String(Atom_ID);
                sAtomID = sAtomID.toLowerCase();
                Mp4AtomBase atom = null;

//                logInfo(msString.format("          STBL-Parser  AtomID = {0} AtomLength = {1}", sAtomID, uiAtomLength), LogType.INFORMATION);
                
                switch (sAtomID)
                {
                    case "stsd":
                        {
                            atom = new STSDAtom(m_Track);
                            break;
                        }

                    case "stts":
                        {
//                            if (!m_Track.isPartialParsing) 
                                atom = new STTSAtom(m_Track);
                            break;
                        }

                    case "stss":
                        {
//                            if (!m_Track.isPartialParsing) 
                                atom = new STSSAtom(m_Track);
                            break;
                        }

                    case "stsc":
                        {
                            if (!m_Track.isPartialParsing) 
                                atom = new STSCAtom(m_Track);
                            break;
                        }

                    case "stsz":
                        {
                            atom = new STSZAtom(m_Track);
                            break;
                        }
                    case "stco":
                        {
                            if (!m_Track.isPartialParsing) 
                                atom = new STCOAtom(m_Track);
                            break;
                        }

                    case "co64":
                        {
                            if (!m_Track.isPartialParsing) 
                                atom = new CO64Atom(m_Track);
                            break;
                        }
                    case "ctts":
                        {
                            if (!m_Track.isPartialParsing) 
                                atom = new CTTSAtom(m_Track);
                            break;
                        }
                   
                    default:
                        {
                            break;
                        }
                }

                if (atom != null)
                {
                    atom.setAtomID(sAtomID);
                    atom.setReader(referenceToM_BinReader);
                    atom.m_Parent = this;
                    atom.parseAtoms(uiFileOffset , uiAtomLength);
                    m_dictChildAtoms.put(sAtomID, atom);
                }
                Atom_ID = null;

                
                // update file offset
                uiFileOffset += java.lang.Math.max(uiAtomLength, Utils.HEADER_LENGTH);
            } catch (IOException ex) {
                //log.error(ex);
                return false;
            }
        }

        //fillChunkInfo();

        return true;            
    }

//    private void fillChunkInfo() // fill chunk info to easily access samples
//    {
//        try
//        {
//            ChunkInfo infoPrev = new ChunkInfo();
//            long uiSampleNumber = 0;
//            LinkedHashMap<Long,Long> kp = m_Track.getM_dictChunkOffset();
//            Set keySet  = kp.keySet();
//            Iterator keyItr = keySet.iterator();
//            while(keyItr.hasNext()){
//                
////            for (HashMap<Long,Long> kp : (Iterable<HashMap<Long,Long>>) m_Track.getM_dictChunkOffset())
////            {
//                ChunkInfo info = new ChunkInfo();
//                ChunkInfo[] referenceToInfo = { info };
//                boolean outRefCondition0 = false;
//                Object keyChunk = keyItr.next();
//                referenceToInfo[0] = m_Track.getM_dictChunkInfo().get(keyChunk);
//                if(referenceToInfo[0]!=null){
//                    outRefCondition0 = true;
//                    referenceToInfo[0].CloneTo(info);
//                }
////                 = getDictChunkInfo(kp, referenceToInfo);
//                
//                if (outRefCondition0)
//                {
//                    info.CloneTo(infoPrev);                    
////                    info.setM_uiFirstSampleNo((uiSampleNumber & 0xFFFFFFFFL) + 1);
////                    info.setM_uiLastSampleNo((uiSampleNumber & 0xFFFFFFFFL) + (info.getM_uiSampleCount() & 0xFFFFFFFFL));                        
//                }
//                else
//                {
//                    infoPrev.CloneTo(info);
////                    Set kpKey =  kp.keySet();
////                    Iterator keyItr = kpKey.iterator();
////                    while(keyItr.hasNext()){
//                        info.setM_uiChunkNo((long)keyChunk);
////                    }
//                    
//                    info.setM_uiFirstSampleNo((uiSampleNumber & 0xFFFFFFFFL) + 1);
//                    info.setM_uiLastSampleNo((uiSampleNumber & 0xFFFFFFFFL) + (info.getM_uiSampleCount() & 0xFFFFFFFFL));                        
//                }
//
//                for (long uiSampleIndex = 0; (uiSampleIndex & 0xFFFFFFFFL) < (info.getM_uiSampleCount() & 0xFFFFFFFFL); uiSampleIndex++)
//                    m_Track.getM_dictSampleInfo().put(++uiSampleNumber, info.Clone());
//                
//            }
//            System.out.println("jhjhjhjhj");
//        }
//        catch (RuntimeException ex)
//        {
//            //log.error(ex);
//        }
//    }
}
