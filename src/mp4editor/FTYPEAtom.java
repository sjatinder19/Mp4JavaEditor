package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;


class FTYPEAtom extends Mp4AtomBase
{

    private byte[] m_arrMajorBrand = "isom".getBytes();
    private long m_uiMinorBrand = 1;
    private String[] m_arruiCompatibleBrands = { "isom", "avc1", "ISM "};

    public long getAtomSize()
    {
        super.setAtomSize(calcSize() + Utils.HEADER_LENGTH);
        return super.getAtomSize();
    }
    public void setAtomSize(long value)
    {
        super.setAtomSize(value);
    }

    private long calcSize()
    {
        long uiSize = 0;
        uiSize = 4 + 4 + 4 * (((long)m_arruiCompatibleBrands.length) & 0xFFFFFFFFL);
        return uiSize;
    }

    public boolean generateAtom()
    {
        try {
            long lStart = m_BinWriter.getFilePointer();//getBaseStream().getPosition();

            setAtomID("ftyp");            
            super.generateAtom();

            m_BinWriter.write(m_arrMajorBrand);
            RandomAccessFile referenceToM_BinWriter = m_BinWriter;
            Utils.writeUint32(referenceToM_BinWriter, m_uiMinorBrand);
            for (int i = 0; i < m_arruiCompatibleBrands.length; i++) // Compatible Brands
                m_BinWriter.write(m_arruiCompatibleBrands[i].getBytes());


            long lStop = m_BinWriter.getFilePointer();//getBaseStream().getPosition();
            if ((lStop - lStart) != (long)getAtomSize())
                ;//log.info("*****  Generation Error. Atom - {0}  ExpectedSize {1} ActualSize {2}"+getAtomID()+" | "+ getAtomSize()+" | "+(lStop - lStart));
            
            return true;
        } catch (IOException ex) {
            //log.error(ex);
            return false;
        }
    }

    public boolean parseAtoms(long uiFileStart,long uiAtomSize)
    {
        try {
            super.parseAtoms(uiFileStart, uiAtomSize);

            long uiSeekOffset = getFileStart() + Utils.HEADER_LENGTH;
            RandomAccessFile referenceToM_BinReader = m_BinReader;
            m_arrMajorBrand = Utils.readChars(referenceToM_BinReader, uiSeekOffset, 4);
            uiSeekOffset += 4;
            byte[] minor = Utils.readChars(referenceToM_BinReader, uiSeekOffset, 4);
            m_uiMinorBrand = Utils.readUInt32(referenceToM_BinReader, uiSeekOffset);

            uiSeekOffset += 4;

            for (; uiSeekOffset < getFileEnd(); uiSeekOffset += 4)
            {
                byte[] brand = Utils.readChars(referenceToM_BinReader, uiSeekOffset, 4);
            }

//            logInfo(msString.format("  FTYP Detail  - Major Version {0} - Minor {1}", msString.ctor(m_arrMajorBrand), m_uiMinorBrand), LogType.INFORMATION);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(FTYPEAtom.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}

