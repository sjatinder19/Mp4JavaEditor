package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import mp4editor.pojo.CodecInfo;
import mp4editor.pojo.CodecType;
import mp4editor.pojo.MP4AInfo;


// Mp4a atom
class MP4AAtom extends Mp4AtomBase
{
    MP4AInfo m_Mp4aInfo = new MP4AInfo();
    private TRAKAtom m_Track = null;
    MP4AAtom(TRAKAtom track)
    {
        m_Track = track;            
    }
    
    public long getAtomSize()
    {
        super.setAtomSize(calcSize() + Utils.HEADER_LENGTH);
        return super.getAtomSize();
    }
    public void setAtomSize(long value)
    {
        super.setAtomSize(value);
    }

    public boolean endWriting()
    {
        Object obj = (MP4AInfo)m_Track.getCODEC_INFO().getM_codecInfo();
        ((MP4AInfo) obj).CloneTo(m_Mp4aInfo);
        return super.endWriting();
    }
    private long calcSize()
    {
        long uiSize = 0;

        uiSize += 16; //
        uiSize += 2; //  channels
        uiSize += 2; //  samplesize
        uiSize += 2; //  predefined
        uiSize += 2; //  reserved
        uiSize += 4; //  sample rate
        uiSize += Utils.HEADER_LENGTH; // esds header length 
        uiSize += 1; // version
        uiSize += 3; // flags
        uiSize += 1; // es tag
        uiSize += 4; // es len
        uiSize += 3; // esid and stream priority
        uiSize += 1; // decoder cfg tag
        uiSize += 4; // decoder cfg len
        uiSize += 1; // object type
        uiSize += 1; // stream type
        uiSize += 3; // buffer length
        uiSize += 4; // Max bitrate
        uiSize += 4; // avg bitrate
        uiSize += 1; // decoder cfg tag
        uiSize += 4; // decoder cfg len
        uiSize += (((/*uint*/long)m_Mp4aInfo.m_arrDecoderCfgData.length) & 0xFFFFFFFFL); // decoder cfg data
        uiSize += 1; // SL Cfg tag
        uiSize += 4; // SL CFG length
        uiSize += 1; // SL cfg data

        return uiSize;
    }

    public boolean generateAtom()
    {
        try {
            long lStart = m_BinWriter.getFilePointer();//getBaseStream().getPosition();

            setAtomID("mp4a");

            super.generateAtom();

            RandomAccessFile referenceToM_BinWriter = m_BinWriter;
            Utils.writeFixed(referenceToM_BinWriter, 6);
            Utils.writeUint16(referenceToM_BinWriter, 1);
            Utils.writeFixed(referenceToM_BinWriter, 8);

            Utils.writeUint16(referenceToM_BinWriter, (int)(m_Mp4aInfo.m_uiChannels & 0xFFFFFFFFL));

            Utils.writeUint16(referenceToM_BinWriter, (int)(m_Mp4aInfo.m_uiSampleSize & 0xFFFFFFFFL));

            Utils.writeFixed(referenceToM_BinWriter, 2);
            Utils.writeFixed(referenceToM_BinWriter, 2);
            Utils.writeUint32(referenceToM_BinWriter, (long)(((m_Mp4aInfo.m_uiSampleRate <<(( 16) & 0x1F)) & 0xFFFFFFFFL)));

            Utils.writeUint32(referenceToM_BinWriter,(long) ( Utils.HEADER_LENGTH 
                                                        + 4   // version and flags
                                                        + 1   // ESDescTag
                                                        + 4   // length
                                                        + 3   // reserved
                                                        + 5   // decoder cfg
                                                        + ( 13 
                                                            + 5 
                                                            + (/*ulong*/long)m_Mp4aInfo.m_arrDecoderCfgData.length) 
                                                         + 6  
                                                         ));
            m_BinWriter.write("esds".getBytes()); // esds atom id
            
            { // esds data

                Utils.writeByte(referenceToM_BinWriter, (byte) 0);
                Utils.writeUint24(referenceToM_BinWriter, 0);
                Utils.writeByte(referenceToM_BinWriter, (byte) 0x03);
                byte[] buffLen = null;
                byte[][] referenceToBuffLen = { buffLen };
                write_mp4_descr_length(/*out*/ referenceToBuffLen, 3 + 5 + (13 + 5 + m_Mp4aInfo.m_arrDecoderCfgData.length) + 6);
                buffLen = referenceToBuffLen[0];
                Utils.writeBytes(referenceToM_BinWriter, buffLen);
                Utils.writeUint16(referenceToM_BinWriter, 0);
                Utils.writeByte(referenceToM_BinWriter, (byte) 0);

                Utils.writeByte(referenceToM_BinWriter, (byte) 0x04);
                referenceToBuffLen[0] = buffLen;
                write_mp4_descr_length(referenceToBuffLen, 13 + 5 + m_Mp4aInfo.getM_arrDecoderCfgData().length);
                buffLen = referenceToBuffLen[0];
                Utils.writeBytes(referenceToM_BinWriter, buffLen);

                Utils.writeByte(referenceToM_BinWriter, (byte) 0x40);
                Utils.writeByte(referenceToM_BinWriter, (byte) 0x15);

                Utils.writeUint24(referenceToM_BinWriter, 0);
                Utils.writeUint32(referenceToM_BinWriter, 0);
                Utils.writeUint32(referenceToM_BinWriter, 0);

                Utils.writeByte(referenceToM_BinWriter, (byte) 0x05);
                referenceToBuffLen[0] = buffLen;
                write_mp4_descr_length(referenceToBuffLen, m_Mp4aInfo.m_arrDecoderCfgData.length);
                buffLen = referenceToBuffLen[0];
                Utils.writeBytes(referenceToM_BinWriter, buffLen);
                Utils.writeBytes(referenceToM_BinWriter, m_Mp4aInfo.m_arrDecoderCfgData);
                /* SLConfigDescriptor */
                Utils.writeByte(referenceToM_BinWriter, (byte) 0x06);
                referenceToBuffLen[0] = buffLen;
                write_mp4_descr_length(referenceToBuffLen, 1);
                buffLen = referenceToBuffLen[0];
                Utils.writeBytes(referenceToM_BinWriter, buffLen);
                Utils.writeByte(referenceToM_BinWriter, (byte) 0x07);
            }

            //m_BinWriter.Write(m_Mp4aInfo.m_esdsData);
           
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
            
            // 78 bytes info
            long uiSeekOffset = uiFileStart + Utils.HEADER_LENGTH;

            uiSeekOffset += 6; // reserved
            RandomAccessFile referenceToM_BinReader = m_BinReader;
            int uiDataRefIndex = Utils.readUInt16(referenceToM_BinReader, uiSeekOffset) & 0xFFFF;
            uiSeekOffset += 2;

            uiSeekOffset += 8; // reserved

            // Channel Count                         
            m_Mp4aInfo.m_uiChannels = (Utils.readUInt16(referenceToM_BinReader, uiSeekOffset) & 0xFFFF);
            uiSeekOffset += 2;
            // Sample Size                           
            m_Mp4aInfo.m_uiSampleSize = (Utils.readUInt16(referenceToM_BinReader, uiSeekOffset) & 0xFFFF);
            uiSeekOffset += 2;

            uiSeekOffset += 2; // predefined

            uiSeekOffset += 2; // reserved

            // Sample Rate                            
            m_Mp4aInfo.setM_uiSampleRate(m_Track.getM_TrackInfo().getM_uiTimeScale());// Utils.ReadUInt32(ref m_BinReader, iSeekOffset) >> 16;
            uiSeekOffset += 4;


            long uiAtomLength = 0;
            char[] Atom_ID = null;
            long uiAtomEnd = getFileEnd();
            while (uiSeekOffset < uiAtomEnd)
            {
                // Mpeg box
                byte[] MpegBoxBuff = Utils.readBytes(referenceToM_BinReader, uiSeekOffset,(int) Utils.HEADER_LENGTH);
                long[] referenceToUiAtomLength = { uiAtomLength };
                char[][] referenceToAtom_ID = { Atom_ID };
                Utils.readAtom(MpegBoxBuff, referenceToUiAtomLength, referenceToAtom_ID);
                uiAtomLength = referenceToUiAtomLength[0];
                Atom_ID = referenceToAtom_ID[0];
                String sAtomID = new String(Atom_ID);
                sAtomID = sAtomID.toLowerCase();
//                logInfo(msString.format("              MP4A-Parser  AtomID = {0} AtomLength = {1}", sAtomID, uiAtomLength), LogType.INFORMATION);

                switch (sAtomID)
                {
                    case "esds":
                        { 
                            long uiESDSLen = (long)(uiAtomLength - Utils.HEADER_LENGTH);
                            long uiSeek = uiSeekOffset + Utils.HEADER_LENGTH;
                            
                            byte btVersion = Utils.readByte(referenceToM_BinReader, uiSeek);
                            uiSeek += 1;

                            long uiFlags = Utils.readUInt24(referenceToM_BinReader, uiSeek);
                            uiSeek += 3;

                            byte btESDescTag = Utils.readByte(referenceToM_BinReader, uiSeek);
                            uiSeek += 1;
                            if (btESDescTag == 0x03)
                            {
                                long iBytes = 0;
                                byte[] buff = Utils.readBytes(referenceToM_BinReader, uiSeek, 4);
                                long[] referenceToIBytes = { iBytes };
                                int iLen = read_mp4_descr_length(buff, referenceToIBytes);
                                iBytes = referenceToIBytes[0];
                                if (iLen < 5 + 15)
                                    return false;
                                uiSeek += (iBytes & 0xFFFFFFFFL);
                                uiSeek += 3; // skip 3 bytes 00 00 00                                 
                            }
                            else 
                            {
                                uiSeek += 2; // skip 2 bytes 00 00
                            }
                                                      

                            /* get and verify DecoderConfigDescrTab */
                            byte btDecoderCfg = Utils.readByte(referenceToM_BinReader, uiSeek);
                            uiSeek += 1;
                            if (btDecoderCfg != 0x04)
                            {
                                return true;
                            }

                            /* read length */
                            long uiBytesNum = 0;
                            long[] referenceToUiBytesNum = { uiBytesNum };
                            int iLength = read_mp4_descr_length(Utils.readBytes(referenceToM_BinReader, uiSeek, 4), /*ref*/ referenceToUiBytesNum);
                            uiBytesNum = referenceToUiBytesNum[0];
                            uiSeek += (uiBytesNum & 0xFFFFFFFFL);

                            byte ObjectType = Utils.readByte(referenceToM_BinReader, uiSeek);
                            uiSeek += 1;

                            byte StreamType = Utils.readByte(referenceToM_BinReader, uiSeek);
                            uiSeek += 1;

                            long uiBuffSize = Utils.readUInt24(referenceToM_BinReader, uiSeek);
                            uiSeek += 3;

                            long uiMaxBitrate = Utils.readUInt32(referenceToM_BinReader, uiSeek);
                            uiSeek += 4;

                            long uiAvgBitrate = Utils.readUInt32(referenceToM_BinReader, uiSeek);
                            uiSeek += 4;
                                                       

                            /* get and verify DecSpecificInfoTag */
                            byte btDecSpecificInfoTag = Utils.readByte(referenceToM_BinReader, uiSeek);
                            uiSeek += 1;
                            if (btDecSpecificInfoTag != 0x05)
                            {
                                return true;
                            }

                            /* read length */
                            referenceToUiBytesNum[0] = uiBytesNum;
                            long uiDecoderCfgLen = (long)read_mp4_descr_length(Utils.readBytes(referenceToM_BinReader, uiSeek, 4), referenceToUiBytesNum);
                            uiBytesNum = referenceToUiBytesNum[0];
                            uiSeek += (uiBytesNum & 0xFFFFFFFFL);

                            m_Mp4aInfo.m_arrDecoderCfgData = Utils.readBytes(referenceToM_BinReader, uiSeek, (int)(uiDecoderCfgLen & 0xFFFFFFFFL));
                            byte[] decoderCfg = m_Mp4aInfo.m_arrDecoderCfgData;

                            int objType = decoderCfg[0] >> 3;
                            int sampleIndex = ((decoderCfg[0] & 0x2) << 1) | (decoderCfg[1] >> 7);
                            //Changes for case 17984 audio recording not working for ZOOM video.
                            byte chnlBt = (byte)(decoderCfg[1]<<1);
                            chnlBt = (byte)(chnlBt>>4);
                            int chanelIndex = chnlBt;
                            m_Mp4aInfo.m_uiChannels = chanelIndex;
                            
                            uiSeek += (uiDecoderCfgLen & 0xFFFFFFFFL);
                        }
                        break;

                    default:
                        break;                
                }
                
                uiSeekOffset += uiAtomLength;
            }

//            logInfo(msString.format("========AudioInfo mp4a Channels={0} , SampleSize={1} SampleRate={2} ", m_Mp4aInfo.m_uiChannels, m_Mp4aInfo.m_uiSampleSize, m_Mp4aInfo.m_uiSampleRate), LogType.INFORMATION);
            CodecInfo info = new CodecInfo();
            info.setM_codecInfo(new MP4AInfo());
            info.setM_codec(CodecType.MP_4_A);
            m_Mp4aInfo.CloneTo((MP4AInfo)info.getM_codecInfo());
            m_Track.setCODEC_INFO(info.Clone());

            return false;
        } catch (IOException ex) {
            Logger.getLogger(MP4AAtom.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private int read_mp4_descr_length(byte[] buff, long[] uiBytes)
    {
        byte b;
        uiBytes[0] = 0;
        int length = 0;
        do
        {
            b = buff[(int)uiBytes[0]];
            uiBytes[0]++;
            length = (length << 7) | ((b & 0xFF) & 0x7F);
        } while ((((b & 0xFF) & 0x80) != 0x00) && ((uiBytes[0] & 0xFFFFFFFFL) < 4));
        
        return length;
    }

    private boolean write_mp4_descr_length(/*out*/ byte[][] buff, int length)
    {            
        buff[0] = new byte[4];
        buff[0][0] = (byte) 0x80;
        buff[0][1] = (byte) 0x80;
        buff[0][2] = (byte) 0x80;
        buff[0][3] = (byte)length;            
        return false;
    }
}

