package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import mp4editor.pojo.AVC1Info;
import mp4editor.pojo.CodecInfo;
import mp4editor.pojo.CodecType;



class AVC1Atom extends Mp4AtomBase
{
    private AVC1Info m_AVC1Info = new AVC1Info();
    private TRAKAtom m_Track = null;
    AVC1Atom(TRAKAtom track)
    {
        m_Track = track;                       
    }
    
    public boolean endWriting()
    {
       m_AVC1Info = (AVC1Info)m_Track.getCODEC_INFO().getM_codecInfo();
        // Add expected child atoms
        addAtom("avcc"); // Expected Atom
       
        return super.endWriting();
    }
//
    public boolean generateAtom()
    {           
        try {
            long lStart = m_BinWriter.getFilePointer();//getBaseStream().getPosition();

            setAtomID("avc1");            
            super.generateAtom();
                        
            RandomAccessFile referenceToM_BinWriter = m_BinWriter ;
            Utils.writeFixed(referenceToM_BinWriter, 6);
            Utils.writeUint16(referenceToM_BinWriter, 1);
            Utils.writeUint16(referenceToM_BinWriter, 0);
            Utils.writeUint16(referenceToM_BinWriter, 0);
            Utils.writeUint32(referenceToM_BinWriter, 0);
            Utils.writeUint32(referenceToM_BinWriter, 0);
            Utils.writeUint32(referenceToM_BinWriter, 0);
            Utils.writeUint16(referenceToM_BinWriter, m_AVC1Info.getM_uiWidth());
            Utils.writeUint16(referenceToM_BinWriter, m_AVC1Info.getM_uiHeight());
            Utils.writeUint32(referenceToM_BinWriter, 0x00480000);
            Utils.writeUint32(referenceToM_BinWriter, 0x00480000);
            Utils.writeUint32(referenceToM_BinWriter, 0);
            Utils.writeUint16(referenceToM_BinWriter, 1);
            Utils.writeFixed(referenceToM_BinWriter, 32);
            Utils.writeUint16(referenceToM_BinWriter, 0x0018);
            Utils.writeUint16(referenceToM_BinWriter, 65535);
            
            Set keySet = m_dictChildAtoms.keySet();
            Iterator keyItr = keySet.iterator();
            while(keyItr.hasNext()){
                Object keyObj = keyItr.next();
                
                m_dictChildAtoms.get(keyObj).setWriter(referenceToM_BinWriter);
                m_dictChildAtoms.get(keyObj).generateAtom();
            }

//            for (KeyValuePair<String, Mp4AtomBase> kp : (Iterable<KeyValuePair<String,Mp4AtomBase>>) m_dictChildAtoms)
//            {
//                referenceToM_BinWriter[0] = m_BinWriter;
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
        long uiSize = 78; // header
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
            case "avcc":
                {
                    atom = new AVCCAtom(m_Track);                        
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

    public boolean parseAtoms(long uiFileStart, long uiAtomSize)
    {
        try {
            super.parseAtoms(uiFileStart, uiAtomSize);

            // 78 bytes info
            /*UInt64*/long uiFileOffset = uiFileStart + Utils.HEADER_LENGTH;
            byte[] tempBuff = new byte[4];

            uiFileOffset += 6; // reserved

            RandomAccessFile referenceToM_BinReader =  m_BinReader;
            int uiDataRefIndex = (Utils.readUInt16(referenceToM_BinReader, uiFileOffset) & 0xFFFF);
            uiFileOffset += 2;

            uiFileOffset += 2; // predefined

            uiFileOffset += 2; // reserved

            uiFileOffset += 12; // predefined

            // Width                           
            m_AVC1Info.setM_uiWidth(Utils.readUInt16(referenceToM_BinReader, uiFileOffset));
            uiFileOffset += 2;

            // Height                           
            m_AVC1Info.setM_uiHeight(Utils.readUInt16(referenceToM_BinReader, uiFileOffset));
            uiFileOffset += 2;

            // Horizental Resolution                            
            long uiHResolution = Utils.readUInt32(referenceToM_BinReader, uiFileOffset);
            uiFileOffset += 4;
            // Vertical Resolution                           
            /*uint*/long uiVResolution = Utils.readUInt32(referenceToM_BinReader, uiFileOffset);
            uiFileOffset += 4;

            uiFileOffset += 4; // reserved
            long uiFrameCount = (Utils.readUInt16(referenceToM_BinReader, uiFileOffset) & 0xFFFF);
            uiFileOffset += 2;

            byte[] chName = Utils.readChars(referenceToM_BinReader, uiFileOffset, 4);
            String sCompressorName = new String(chName);
            uiFileOffset += 32;

            long uiDepth = (Utils.readUInt16(referenceToM_BinReader, uiFileOffset) & 0xFFFF);
            uiFileOffset += 2;

            short iPredef = (short)(Utils.readUInt16(referenceToM_BinReader, uiFileOffset) & 0xFFFF);
            uiFileOffset += 2;
//            logInfo(msString.format("              AVC1-Parser  Width = {0} Height = {1} Depth = {2}", m_AVC1Info.m_uiWidth, m_AVC1Info.m_uiHeight, uiDepth), LogType.INFORMATION);

            long uiAtomEnd = getFileEnd();
            long uiAtomLength = 0;
            char[] Atom_ID = null;
            while (uiFileOffset < (uiAtomEnd - Utils.HEADER_LENGTH))
            {
                // Mpeg box
                byte[] MpegBoxBuff = Utils.readBytes(referenceToM_BinReader, uiFileOffset,(int) Utils.HEADER_LENGTH);
               
                long[] referenceToUiAtomLength = { uiAtomLength };
                char[][] referenceToAtom_ID = { Atom_ID };
                Utils.readAtom(MpegBoxBuff, referenceToUiAtomLength, referenceToAtom_ID);
                uiAtomLength = referenceToUiAtomLength[0];
                Atom_ID = referenceToAtom_ID[0];
                String sAtomID = new String(Atom_ID);
                sAtomID = sAtomID.toLowerCase();
                Mp4AtomBase atom = null;
//                logInfo(msString.format("              AVC1-Parser  AtomID = {0} AtomLength = {1}", sAtomID, uiAtomLength), LogType.INFORMATION);
                
                switch (sAtomID)
                {
                    case "avcc":
                        {
                            atom = new AVCCAtom(m_Track);                           
                            break;
                        }


                    case "uuid":
                        {                            
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
            }

            CodecInfo info = new CodecInfo();
            info.setM_codec(CodecType.AVC_1);
            AVC1Info mAVC1Info = new AVC1Info();
            info.setM_codecInfo(mAVC1Info);
            m_AVC1Info.CloneTo((AVC1Info)info.getM_codecInfo());
            m_Track.setCODEC_INFO(info.Clone());
            return false;
        } catch (IOException ex) {
            Logger.getLogger(AVC1Atom.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public AVC1Info getM_AVC1Info() {
        return m_AVC1Info;
    }

    public void setM_AVC1Info(AVC1Info m_AVC1Info) {
        this.m_AVC1Info = m_AVC1Info;
    }
    
    
}

