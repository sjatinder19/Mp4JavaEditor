package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


// Hint Media Handler
class HMHDAtom extends Mp4AtomBase
{
    private TRAKAtom m_Track = null;
    HMHDAtom(TRAKAtom track)
    {
        m_Track = track;
    }
    
    public boolean generateAtom()
    {
        try {
            long lStart = m_BinWriter.getFilePointer();//getBaseStream().getPosition();

            setAtomID("hmhd");            
            super.generateAtom();
            
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
        return false;
    }
}

