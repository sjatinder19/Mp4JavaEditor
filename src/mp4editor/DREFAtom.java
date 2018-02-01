package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;


// Data ref box
class DREFAtom extends Mp4AtomBase
{
    private TRAKAtom m_Track = null;

    DREFAtom(TRAKAtom track)
    {
        m_Track = track;
    }
    
    public long getAtomSize()
    {
        super.setAtomSize((long)(20 + Utils.HEADER_LENGTH));
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

            setAtomID("dref");            
            super.generateAtom();

            if ((getVersion() & 0xFFFF) == 1)
            {
                RandomAccessFile referenceToM_BinWriter = m_BinWriter;
                Utils.writeByte(referenceToM_BinWriter, (byte) 1);
                Utils.writeUint24(referenceToM_BinWriter, 0);
            }
            else
                {
                RandomAccessFile referenceToM_BinWriter = m_BinWriter;
                Utils.writeUint32(referenceToM_BinWriter, 0);
                } // version
            RandomAccessFile referenceToM_BinWriter = m_BinWriter;
            Utils.writeUint32(referenceToM_BinWriter, 1);
            Utils.writeUint32(referenceToM_BinWriter, 12);
            m_BinWriter.write("url ".getBytes());
            Utils.writeUint32(referenceToM_BinWriter, 1);

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

            RandomAccessFile referenceToM_BinReader =  m_BinReader;
            long uiVersion = Utils.readUInt32(referenceToM_BinReader, uiSeekOffset);
            uiSeekOffset += 4;

            long entryCount = Utils.readUInt32(referenceToM_BinReader, uiSeekOffset);
            uiSeekOffset += 4;

            
            char ch;
            String[] arrBox = new String[Integer.parseInt(String.valueOf(entryCount))];
            for (int iIndex = 0; iIndex < (entryCount & 0xFFFFFFFFL); iIndex++)
            {
                long uiLength = Utils.readUInt32(referenceToM_BinReader, uiSeekOffset);
                uiSeekOffset += 4;
                arrBox[iIndex] = "";
                m_BinReader.seek(uiSeekOffset);
                while ((ch = m_BinReader.readChar()) != '\u0000')
                    arrBox[iIndex].concat(String.valueOf(ch));
            }
            return false;
        } catch (IOException ex) {
            Logger.getLogger(DREFAtom.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}

