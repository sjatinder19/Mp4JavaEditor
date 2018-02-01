package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;


//aligned(8) class VideoMediaHeaderBox
//extends FullBox(�vmhd�, version = 0, 1) {
//template unsigned int(16) graphicsmode = 0; // copy, see below
//template unsigned int(16)[3] opcolor = {0, 0, 0};
//}


class VMHDAtom extends Mp4AtomBase
{        
    private TRAKAtom m_Track = null;
    VMHDAtom(TRAKAtom track)
    {
        m_Track = track;
    }
    
    public long getAtomSize()
    {
        super.setAtomSize(12 + Utils.HEADER_LENGTH);
        return super.getAtomSize();
    }
    public void setAtomSize(/*UInt64*/long value)
    {
        super.setAtomSize(value);
    }

    public boolean generateAtom()
    {
        try {
            long lStart = m_BinWriter.getFilePointer();//getBaseStream().getPosition();

            setAtomID("vmhd");            
            super.generateAtom();

            // Write 
            RandomAccessFile referenceToM_BinWriter = m_BinWriter;
            Utils.writeUint32(referenceToM_BinWriter, 0);
            Utils.writeUint16(referenceToM_BinWriter, 0);
            Utils.writeUint16(referenceToM_BinWriter, 0);
            Utils.writeUint16(referenceToM_BinWriter, 0);
            Utils.writeUint16(referenceToM_BinWriter, 0);

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

            long uiGraphicsMode = (Utils.readUInt16(referenceToM_BinReader, uiSeekOffset) & 0xFFFF);
            uiSeekOffset += 4;

//            logInfo(msString.format("          VMHD  Graphics Mode = {0}", uiGraphicsMode), LogType.INFORMATION);             
            return false;
        } catch (IOException ex) {
            Logger.getLogger(VMHDAtom.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}

