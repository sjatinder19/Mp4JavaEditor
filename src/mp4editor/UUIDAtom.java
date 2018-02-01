package mp4editor;
import java.io.IOException;
import mp4editor.Mp4AtomBase;

class UUIDAtom extends Mp4AtomBase
{
    public boolean generateAtom()
    {
        try
        {
        long lStart = m_BinWriter.getFilePointer();//getBaseStream().getPosition();

        setAtomID("uuid");
        setAtomSize(8);
        super.generateAtom();


        long lStop = m_BinWriter.getFilePointer();//getBaseStream().getPosition();
            if ((lStop - lStart) != (long)getAtomSize())
               ;// log.info("*****  Generation Error. Atom - {0}  ExpectedSize {1} ActualSize {2}"+getAtomID()+" | " +getAtomSize()+" | "+ (lStop - lStart));
            
            return true;
        } catch (IOException ex) {
            //log.error(ex);
            return false;
        }
    }
}
//
