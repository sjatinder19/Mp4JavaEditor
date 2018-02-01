package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;


//aligned(8) class SoundMediaHeaderBox
//extends FullBox(�smhd�, version = 0, 0) {
//template int(16) balance = 0;
//const unsigned int(16) reserved = 0;
//}


class SMHDAtom extends Mp4AtomBase
{
    private TRAKAtom m_Track = null;
    SMHDAtom(TRAKAtom track)
    {
        m_Track = track;   
    }
   
    public long getAtomSize()
    {
        super.setAtomSize(8 + Utils.HEADER_LENGTH);
        return super.getAtomSize();
    }
    public void setAtomSize(long value)
    {
        super.setAtomSize(value);
    }

    public boolean generateAtom()
    {
        try {
            long lStart = m_BinWriter.getFilePointer();//getBaseStream().getPosition();

            setAtomID("smhd");
            super.generateAtom();

            RandomAccessFile referenceToM_BinWriter = m_BinWriter;
            Utils.writeUint32(referenceToM_BinWriter,0);
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
        super.parseAtoms(uiFileStart, uiAtomSize);
        return false;
    }
}

