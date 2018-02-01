package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********



// Media data container
class MDATAtom extends Mp4AtomBase
{
    public boolean generateAtom()
    {         
        setAtomID("mdat");            
        super.generateAtom();
       // Utils.WriteFixed(ref m_BinWriter,4);
        return false;
    }
}

