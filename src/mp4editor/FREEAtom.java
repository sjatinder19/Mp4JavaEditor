package mp4editor;

//package org.ism.mvp.model.editing;
//
//// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********
//
//// Free space atom
//class FREEAtom extends Mp4AtomBase
//{
//    /*override*/ boolean generateAtom()
//    {
//        long lStart = m_BinWriter.getBaseStream().getPosition();
//
//        setAtomID("free".toCharArray());
//        setAtomSize(8);
//        super.generateAtom();
//
//
//        long lStop = m_BinWriter.getBaseStream().getPosition();
//        if ((lStop - lStart) != (long)getAtomSize())
//            logInfo(msString.format("*****  Generation Error. Atom - {0}  ExpectedSize {1} ActualSize {2}", msString.ctor(getAtomID()), getAtomSize(), lStop - lStart), LogType.INFORMATION);
//        
//        return false;
//    }
//}
//
