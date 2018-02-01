package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


class MINFAtom extends Mp4AtomBase
{
    private TRAKAtom m_Track = null;
    MINFAtom(TRAKAtom track)
    {
        m_Track = track;
        m_dictChildAtoms = new LinkedHashMap<String, Mp4AtomBase>();
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
         super.setVersion(value);
//        for (KeyValuePair<String, Mp4AtomBase> kv : (Iterable<KeyValuePair<String,Mp4AtomBase>>) m_dictChildAtoms)
//            kv.Value.setVersion(value);
        
    }

    long calcSize()
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
    
    private boolean addAtom(String sAtomID)
    {
        Mp4AtomBase atom = null;
        switch (sAtomID)
        {
            case "stbl":
                {
                    atom = new STBLAtom(m_Track);
                    break;
                }

            case "dinf":
                {
                    atom = new DINFAtom(m_Track);
                    break;
                }

            case "hdlr":
                {
                    switch (m_Track.getTRACK_TYPE())
                    {
                        case "TRAK_AUDIO":
                            atom = new SMHDAtom(m_Track);
                            break;
                        case "TRAK_VIDEO":
                            atom = new VMHDAtom(m_Track);
                            break;                         
                    }                       
                    break;
                }
        }

        if (atom != null)
        {
            atom.m_Parent = this;
            m_dictChildAtoms.put(sAtomID, atom);
        }

        return false;
    }

    public boolean endWriting()
    {
        // Add expected child atoms
        addAtom("hdlr");    // media handler
        addAtom("dinf");    // data info box
        addAtom("stbl");    // sample tablebpx

        return super.endWriting();
    }
           
    public boolean generateAtom()
    {
        try {
            long lStart = m_BinWriter.getFilePointer();//getBaseStream().getPosition();

            setAtomID("minf");
            super.generateAtom();

            Set keySet = m_dictChildAtoms.keySet();
            Iterator keyItr = keySet.iterator();
            while(keyItr.hasNext()){
                Object keyObj = keyItr.next();
                RandomAccessFile referenceToM_BinWriter =  m_BinWriter;
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
                ;//log.info("*****  Generation Error. Atom - {0}  ExpectedSize {1} ActualSize {2}"+getAtomID()+" | "+ getAtomSize()+" | "+(lStop - lStart));
            
            return true;
        } catch (IOException ex) {
            //log.error(ex);
            return false;
        }
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
                switch (sAtomID)
                {
                    case "vmhd":
                        {
                            atom = new VMHDAtom(m_Track);
                            break;
                        }

                    case "smhd":
                        {
                            atom = new SMHDAtom(m_Track);
                            break;
                        }

                    case "hmhd":
                        {
                            atom = new HMHDAtom(m_Track);
                            break;
                        }

                    case "dinf":
                        {
                            atom = new DINFAtom(m_Track);
                            break;
                        }

                    case "stbl":
                        {
                            atom = new STBLAtom(m_Track);
                            break;
                        }

                    default:
                        {
                            break;
                        }
                }
//                logInfo(msString.format("        MINF-Parser  AtomID = {0} AtomLength = {1}", sAtomID, uiAtomLength), LogType.INFORMATION);
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
                Logger.getLogger(MINFAtom.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return false;
    }
}

