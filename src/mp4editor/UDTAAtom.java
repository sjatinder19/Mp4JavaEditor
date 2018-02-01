package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import java.io.IOException;
import java.io.RandomAccessFile;
import mp4editor.util.LogType;


class UDTAAtom extends Mp4AtomBase
{
    private TRAKAtom m_Track = null;
    private HNTIAtom m_hntiAtom = null;
    UDTAAtom(TRAKAtom track)
    {
        m_Track = track;
    }

    public boolean generateAtom()
    {
        try {
            long lStart = m_BinWriter.getFilePointer();//getBaseStream().getPosition();

            setAtomID("udta");
            setAtomSize(8);
            super.generateAtom();


            long lStop = m_BinWriter.getFilePointer();//getBaseStream().getPosition();
            if ((lStop - lStart) != (long)getAtomSize()){
                //log.info("*****  Generation Error. Atom - {0}  ExpectedSize {1} ActualSize {2}"+getAtomID()+" | " +getAtomSize()+" | "+ (lStop - lStart));
                m_Track.printLog(LogType.information, "mp4editor.UDTAAtom || *****  Generation Error. Atom - {0}  ExpectedSize {1} ActualSize {2}" + getAtomID() + " | " + getAtomSize() + " | " + (lStop - lStart), null);
            }
            
            return true;
        } catch (IOException ex) {
            m_Track.printLog(LogType.error, "mp4editor.UDTAAtom | ", ex);
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
                byte[] MpegBoxBuff = Utils.readBytes(referenceToM_BinReader, uiFileOffset,(int) Utils.HEADER_LENGTH);
                long[] referenceToUiAtomLength = { uiAtomLength };
                char[][] referenceToAtom_ID = { Atom_ID };
                Utils.readAtom(MpegBoxBuff, referenceToUiAtomLength, referenceToAtom_ID);
                uiAtomLength = referenceToUiAtomLength[0];
                Atom_ID = referenceToAtom_ID[0];
                String sAtomID = new String(Atom_ID);
                sAtomID = sAtomID.toLowerCase();

                //log.info("MOOV-Parser  AtomID = {0} AtomLength = {1}"+sAtomID+" | "+uiAtomLength);
                m_Track.printLog(LogType.information, "mp4editor.UDTAAtom || MOOV-Parser  AtomID = {0} AtomLength = {1}"+sAtomID+" | "+uiAtomLength, null);
                switch (sAtomID)
                {
                    case "hnti":
                        {
                            m_hntiAtom = new HNTIAtom();
                            m_hntiAtom.setAtomID(sAtomID);
                            m_hntiAtom.setReader(referenceToM_BinReader);
                            m_hntiAtom.m_Parent = this;
                            m_hntiAtom.parseAtoms(uiFileOffset, uiAtomLength);
                            break;
                        }
                }

                uiFileOffset += java.lang.Math.max(uiAtomLength, Utils.HEADER_LENGTH);
            } catch (IOException ex) {
                m_Track.printLog(LogType.error, "mp4editor.UDTAAtom | ", ex);
                return false;
            }
        }
        return true;
    }
}

