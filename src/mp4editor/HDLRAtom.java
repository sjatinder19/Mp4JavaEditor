package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;


class HDLRAtom extends Mp4AtomBase
{   
    private char[] m_handlerType = null;
    private String m_sDesc;
    
    public long getAtomSize()
    {
        return 24 + ((long)m_sDesc.length() +1)+ Utils.HEADER_LENGTH;
    }
    public void setAtomSize(long value)
    {
        super.setAtomSize(value);
    }

    private TRAKAtom m_Track = null;
    HDLRAtom(TRAKAtom track)
    {
        m_Track = track;
    }
    
    public boolean endWriting()
    {
        switch (m_Track.getTRACK_TYPE())
        {
            case "TRAK_AUDIO":
                m_handlerType = "soun".toCharArray();
                m_sDesc = "GPAC ISO Audio Handler";

                break;
            case "TRAK_VIDEO":
                m_handlerType = "vide".toCharArray();
                m_sDesc = "GPAC ISO Video Handler";
                break;
            case "TRAK_HINT":
                m_handlerType = "hint".toCharArray();
                m_sDesc = "GPAC ISO Hint Handler";
                break;
            case "TRAK_META":
                m_handlerType = "meta".toCharArray();
                m_sDesc = "GPAC ISO Meta Handler";
                break;
            default:
                break;
        }
        return super.endWriting();
    }
   
    public boolean generateAtom()
    {
        try {
            long lStart = 0;
            try {
                lStart = m_BinWriter.getFilePointer(); //getBaseStream().getPosition();
            } catch (IOException ex) {
                Logger.getLogger(HDLRAtom.class.getName()).log(Level.SEVERE, null, ex);
            }

            setAtomID("hdlr");
            super.generateAtom();

            if ((getVersion() & 0xFFFF) == 1)
            {
                try {
                    RandomAccessFile referenceToM_BinWriter = m_BinWriter;
                    Utils.writeByte(referenceToM_BinWriter, (byte) 1);
                    Utils.writeUint24(referenceToM_BinWriter, 0);
                } catch (IOException ex) {
                    Logger.getLogger(HDLRAtom.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
                {
                try {
                    RandomAccessFile referenceToM_BinWriter = m_BinWriter;
                    Utils.writeUint32(referenceToM_BinWriter, 0);
                } // Version and flags
                catch (IOException ex) {
                    Logger.getLogger(HDLRAtom.class.getName()).log(Level.SEVERE, null, ex);
                }
                } // Version and flags
            RandomAccessFile referenceToM_BinWriter =  m_BinWriter;
            Utils.writeUint32(referenceToM_BinWriter, 0);
            m_BinWriter.write(new String(m_handlerType).getBytes()); // Handler Type

            Utils.writeUint32(referenceToM_BinWriter, 0);
            Utils.writeUint32(referenceToM_BinWriter, 0);
            Utils.writeUint32(referenceToM_BinWriter, 0);
            m_BinWriter.write(m_sDesc.getBytes());
            m_BinWriter.write('\u0000');


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

            RandomAccessFile referenceToM_BinReader = m_BinReader;
            long uiVersion = Utils.readUInt32(referenceToM_BinReader, uiSeekOffset);
            uiSeekOffset += 4;

            long uiPredefined = Utils.readUInt32(referenceToM_BinReader, uiSeekOffset);
            uiSeekOffset += 4;

            byte[] handler = Utils.readChars(referenceToM_BinReader, uiSeekOffset, 4);
            uiSeekOffset += 4;

            switch (new String(handler))
            {
                case "vide":
                    {
                        m_Track.setTRACK_TYPE("TRAK_VIDEO");                        
                        break;                    
                    }

                case "soun":
                    {
                        m_Track.setTRACK_TYPE("TRAK_AUDIO");                        
                        break;
                    }

                case "hint":
                    {
                        m_Track.setTRACK_TYPE("TRAK_HINT");                        
                        break;
                    }

                case "meta":
                    {
                        m_Track.setTRACK_TYPE("TRAK_META");                        
                        break;
                    }

                default :
                    {
                        m_Track.setTRACK_TYPE("TRAK_UNKNOWN");
                        break;
                    }           
            }


            uiSeekOffset += 12;

            m_BinReader.seek((long)uiSeekOffset);
            String sDesc ="";

            // null terminated string contains description
            char ch;
            while ((ch = m_BinReader.readChar()) != '\u0000')
                sDesc = sDesc + ch;

            //log.info("HDLR  {0} Type = {1} , Description = {2} "+ m_Track.getTRACK_TYPE().toString()+" | "+ handler+" | "+ sDesc);
            return false;
        } catch (IOException ex) {
            //log.error(ex);
            return false;
        }
    }

}

