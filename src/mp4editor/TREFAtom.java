package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import java.io.IOException;
import java.io.RandomAccessFile;
import mp4editor.util.LogType;


class TREFAtom extends Mp4AtomBase
{
    private long m_uiTrackId = 0;
    private TRAKAtom m_track = null;
    TREFAtom(TRAKAtom track )
    {
        m_track = track;
    }
    
    public long getAtomSize()
    {
        super.setAtomSize((long)(16 + ((m_uiTrackId & 0xFFFFFFFFL) > 0 ? 4 : 0)));
        return super.getAtomSize();
    }
    public void setAtomSize(long value)
    {
        super.setAtomSize(value);
    }

    public boolean generateAtom()
    {
        try {
            setAtomID("tref");
            super.generateAtom();

            long iHintLen = getAtomSize() - Utils.HEADER_LENGTH;
            RandomAccessFile referenceToM_BinWriter = m_BinWriter;
            Utils.writeUint32(referenceToM_BinWriter,(long) iHintLen);
            Utils.writeChars(referenceToM_BinWriter,"hint".getBytes());
            if((m_uiTrackId & 0xFFFFFFFFL) > 0)
                {
                Utils.writeUint32( referenceToM_BinWriter, m_uiTrackId);
                }
            return true;
        } catch (IOException ex) {
            m_track.printLog(LogType.error, "mp4editor.TREFAtom | ", ex);
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

                long uiEntryCount = uiAtomLength - Utils.HEADER_LENGTH;
                long uiTrackRefIndex = uiFileOffset + Utils.HEADER_LENGTH;

                if (uiEntryCount > 0)
                    {                
                    m_uiTrackId = Utils.readUInt32(referenceToM_BinReader, uiTrackRefIndex);
                    }

                uiFileOffset += java.lang.Math.max(uiAtomLength, Utils.HEADER_LENGTH);// update file offset
            } catch (IOException ex) {
                m_track.printLog(LogType.error, "mp4editor.TREFAtom | ", ex);
                return false;
            }
        }

        //log.info("TREF parser   TrackRefIndex = {0}"+ m_uiTrackId);
        m_track.printLog(LogType.information, "mp4editor.UDTAAtom || TREF parser   TrackRefIndex = {0}"+ m_uiTrackId, null);
        return true;
    }
}

