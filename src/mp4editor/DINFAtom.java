package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


class DINFAtom extends Mp4AtomBase
{        
    private TRAKAtom m_Track = null;
    DINFAtom(TRAKAtom track)
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
        addDataInfo();
        return super.endWriting();
    }
    public boolean generateAtom()
    {
        try {
            long lStart = m_BinWriter.getFilePointer();//getBaseStream().getPosition();

            setAtomID("dinf");            
            super.generateAtom();

            Set keySet = m_dictChildAtoms.keySet();
                Iterator keyItr = keySet.iterator();
                while(keyItr.hasNext()){
                    Object keyObj = keyItr.next();
                    RandomAccessFile referenceToM_BinWriter =  m_BinWriter;
                m_dictChildAtoms.get(keyObj).setWriter(referenceToM_BinWriter);
                m_dictChildAtoms.get(keyObj).generateAtom();
                }
            
    //        for (KeyValuePair<String, Mp4AtomBase> kp : (Iterable<KeyValuePair<String,Mp4AtomBase>>) m_dictChildAtoms)
    //        {
    //            BinaryWriter[] referenceToM_BinWriter = { m_BinWriter };
    //            kp.Value.setWriter(/*ref*/ referenceToM_BinWriter);
    //            m_BinWriter = referenceToM_BinWriter[0];
    //            kp.Value.generateAtom();
    //        }
            
            long lStop = m_BinWriter.getFilePointer();//getBaseStream().getPosition();
                if ((lStop - lStart) != (long)getAtomSize())
                    ;//log.info("*****  Generation Error. Atom - {0}  ExpectedSize {1} ActualSize {2}"+getAtomID()+" | "+ getAtomSize()+" | "+(lStop - lStart));
                
                return true;
            
        } catch (IOException ex) {
            ;//log.error(ex);
            return false;
        }
    }

    boolean addDataInfo()
    {
        Mp4AtomBase atom = new DREFAtom(m_Track);
        atom.m_Parent = this;
        RandomAccessFile referenceToM_BinWriter =  m_BinWriter;
        atom.setWriter(referenceToM_BinWriter);
        m_dictChildAtoms.put("dref", atom);
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
                // Mpeg box
                RandomAccessFile referenceToM_BinReader = m_BinReader;
                byte[] MpegBoxBuff = Utils.readBytes(referenceToM_BinReader, uiFileOffset, (int)Utils.HEADER_LENGTH);
                long[] referenceToUiAtomLength = { uiAtomLength };
                char[][] referenceToAtom_ID = { Atom_ID };
                Utils.readAtom(MpegBoxBuff,referenceToUiAtomLength,referenceToAtom_ID);
                uiAtomLength = referenceToUiAtomLength[0];
                Atom_ID = referenceToAtom_ID[0];
                String sAtomID = new String(Atom_ID);
                sAtomID = sAtomID.toLowerCase();

//                logInfo(msString.format("          DINFAtom-Parser  AtomID = {0} AtomLength = {1}", sAtomID, uiAtomLength), LogType.INFORMATION);

                Mp4AtomBase atom = null;
                switch (sAtomID)
                {
                    case "dref":
                        {
                            atom = new DREFAtom(m_Track);
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
                    atom.parseAtoms(uiFileOffset, uiAtomLength);
                    m_dictChildAtoms.put(sAtomID, atom);
                }
                Atom_ID = null;

                // update file offset
                uiFileOffset += java.lang.Math.max(uiAtomLength, Utils.HEADER_LENGTH);
            } catch (IOException ex) {
                Logger.getLogger(DINFAtom.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return false;
    }
}

