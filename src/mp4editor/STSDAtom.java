package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import mp4editor.pojo.CodecInfo;
import mp4editor.pojo.CodecType;



class STSDAtom extends Mp4AtomBase
{
    private TRAKAtom m_Track = null;
    
    STSDAtom(TRAKAtom track )
    {
        m_Track = track;
    }
    
    public long getAtomSize()
    {
        super.setAtomSize(calcZize() + Utils.HEADER_LENGTH);
        return super.getAtomSize();
    }
    public void setAtomSize(long value)
    {
        super.setAtomSize(value);
    }

    private long calcZize()
    {
        long uiSize = 8; // version + entry count
        java.util.Set<String> keySet = m_dictChildAtoms.keySet();
         Iterator keyItr = keySet.iterator();
         while(keyItr.hasNext()){
             String key = (String) keyItr.next();
             Mp4AtomBase mp4AtomBase = m_dictChildAtoms.get(key);
             uiSize += mp4AtomBase.getAtomSize();
         }
//        for(KeyValuePair<String, Mp4AtomBase> kv : (Iterable<KeyValuePair<String, Mp4AtomBase>>) m_dictChildAtoms)
//            uiSize += kv.Value.getAtomSize();

        return uiSize;
    }

    public boolean  endWriting()
    {
        addEncoder();

        return super.endWriting();
    }

    public boolean generateAtom()
    {
        try {
            long lStart = m_BinWriter.getFilePointer();//getBaseStream().getPosition();
            
            setAtomID("stsd");            
            super.generateAtom();

            RandomAccessFile referenceToM_BinWriter = m_BinWriter ;
            Utils.writeUint32(referenceToM_BinWriter, 0);
            
            Set keySet = m_dictChildAtoms.keySet();
            Iterator keyItr = keySet.iterator();
            while(keyItr.hasNext()){
                Object keyObj = keyItr.next();
                Utils.writeUint32(referenceToM_BinWriter, 1);
                m_dictChildAtoms.get(keyObj).setWriter(referenceToM_BinWriter);
                m_dictChildAtoms.get(keyObj).generateAtom();
            }
            
//            for (KeyValuePair<String, Mp4AtomBase> kv : (Iterable<KeyValuePair<String,Mp4AtomBase>>) m_dictChildAtoms)
//            {
//                referenceToM_BinWriter[0] = m_BinWriter;
//                Utils.writeUint32(/*ref*/ referenceToM_BinWriter, 1);
//                m_BinWriter = referenceToM_BinWriter[0]; // Entry Count 1
//
//                referenceToM_BinWriter[0] = m_BinWriter;
//                kv.Value.setWriter(/*ref*/ referenceToM_BinWriter);
//                m_BinWriter = referenceToM_BinWriter[0];
//                kv.Value.generateAtom();
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

    boolean addEncoder()
    {
        Mp4AtomBase atom = null;
        String sAtomId = "";
        switch (m_Track.getTRACK_TYPE())
        {
            case "TRAK_VIDEO":
                {
                    CodecInfo codec = m_Track.getCODEC_INFO().Clone();
                    switch (codec.getM_codec())
                    {
                        case CodecType.AVC_1:
                            {
                                sAtomId = "avc1";
                                atom = new AVC1Atom(m_Track);                                                                      
                                break;
                            }
                    }
                    break;                        
                }

            case "TRAK_AUDIO":
                {
                    CodecInfo codec = m_Track.getCODEC_INFO().Clone();
                    switch (codec.getM_codec())
                    {
                        case CodecType.MP_4_A:
                            {
                                sAtomId = "mp4a";
                                atom = new MP4AAtom(m_Track);                                                                      
                                break;
                            }
                    }
                    break;
                }

//            case TRAK_HINT:
//                {
//                    sAtomId = "rtp ";
//                    atom = new RTPAtom(m_Track);                        
//                    break;
//                }
            default :
                return true;
        }

        if (atom != null)
        {
            atom.m_Parent = this;
            m_dictChildAtoms.put(sAtomId, atom);
        }
        return false;
    }

    public boolean parseAtoms(long uiFileStart,long uiAtomSize)
    {
        try {
            super.parseAtoms(uiFileStart, uiAtomSize);

            long uiFileOffset = getFileStart() + Utils.HEADER_LENGTH;
            long uiAtomEnd = getFileEnd();
            long uiAtomLength = 0;
            char[] Atom_ID = null;

            RandomAccessFile referenceToM_BinReader = m_BinReader;
            byte[] Buff = Utils.readChars(referenceToM_BinReader, uiFileOffset, 4);
            int iVersion = Buff[0];

            uiFileOffset += 4; // Skip version and flags

            // entry count
            long uiEntryCount = Utils.readUInt32(referenceToM_BinReader, uiFileOffset);
            uiFileOffset += 4;

            
            while (uiFileOffset < uiAtomEnd)
            {
                byte[] MpegBoxBuff = Utils.readBytes(referenceToM_BinReader, uiFileOffset, (int)Utils.HEADER_LENGTH);

                long[] referenceToUiAtomLength = { uiAtomLength };
                char[][] referenceToAtom_ID = { Atom_ID };
                Utils.readAtom(MpegBoxBuff, /*ref*/ referenceToUiAtomLength, /*out*/ referenceToAtom_ID);
                uiAtomLength = referenceToUiAtomLength[0];
                Atom_ID = referenceToAtom_ID[0];
                String sAtomID = new String(Atom_ID);
                sAtomID = sAtomID.toLowerCase();


//                logInfo(msString.format("            STSD-Parser  AtomID = {0} AtomLength = {1}", sAtomID, uiAtomLength), LogType.INFORMATION);

                Mp4AtomBase atom = null;
                // Expected childs
                switch (sAtomID)
                {
                    case "avc1":
                        {
                            atom = new AVC1Atom(m_Track);                            
                            break;
                        }

                    case "mp4a":
                        {
                            atom = new MP4AAtom(m_Track);
                            break;
                        }

//                    case rtp:
//                        {
////                            atom = new RTPAtom(m_Track);
//                            break;
//                        }

                    case "enca":
                    case "mp4s":
                    case "mp4v":
                    case "encv":                    
                    case "samr":
                    case "sawb":
                    case "s263":
                    case "alac":
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
            }

            return true;
        } catch (IOException ex) {
            //log.error(ex);
            return false;
        }
    }
}

