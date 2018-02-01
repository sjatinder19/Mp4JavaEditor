package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import java.io.IOException;
import java.io.RandomAccessFile;


class HNTIAtom extends Mp4AtomBase
{
    byte[] m_sSDPText = null;
     
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
                byte[] MpegBoxBuff = Utils.readBytes(referenceToM_BinReader, uiFileOffset, (int)Utils.HEADER_LENGTH);
                long[] referenceToUiAtomLength = { uiAtomLength };
                char[][] referenceToAtom_ID = { Atom_ID };
                Utils.readAtom(MpegBoxBuff, referenceToUiAtomLength, referenceToAtom_ID);
                uiAtomLength = referenceToUiAtomLength[0];
                Atom_ID = referenceToAtom_ID[0];
                String sAtomID = new String(Atom_ID);
                sAtomID = sAtomID.toLowerCase();

                //log.info("MOOV-Parser  AtomID = {0} AtomLength = {1}"+sAtomID+" | "+uiAtomLength);
                switch (sAtomID)
                {
                    case "rtp":
                        {
                            long uiIndex = uiFileOffset + Utils.HEADER_LENGTH;
                            byte[] sdp = Utils.readChars(referenceToM_BinReader, uiIndex,4);
                            uiIndex += 4;

                            long uiLength = uiAtomEnd - uiIndex;
                            m_sSDPText = Utils.readBytes(referenceToM_BinReader, uiIndex,(int) uiLength);
                            break;
                        }

                    case "sdp":
                        {
                            long uiIndex = uiFileOffset + Utils.HEADER_LENGTH;

                            long uiLength = uiAtomEnd - uiIndex;
                            m_sSDPText = Utils.readBytes(referenceToM_BinReader, uiIndex, (int)uiLength);

                            break;
                        }
                }

                uiFileOffset += java.lang.Math.max(uiAtomLength, Utils.HEADER_LENGTH);
            } catch (IOException ex) {
                //log.error(ex);
                return false;
            }
        }
        return true;
    }
}

