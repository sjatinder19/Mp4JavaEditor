package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;



public class Utils
{
    public static final long OFFSET_LENGTH = 4;
    public static final long IDENTIFIER_LENGTH = 4;
    public static final long HEADER_LENGTH = OFFSET_LENGTH + IDENTIFIER_LENGTH;
    
     public static boolean stringNullCheck(String obj){
        if (obj != null && !obj.equals("") && !obj.equals("null")) {
            return true;
        }
        return false;
    }
    
//    static void processRTPSample(byte[] arrData)
//    {
//        int uiPacketcount = (Utils.readUInt16(arrData, 0) & 0xFFFF);
//        printLog(msString.format("*****  RTP SAMPLE Packet count = {0}",uiPacketcount),LogType.INFORMATION);
//    }
//
//    static boolean printLog(String sMessage, /*LogType*/int type)
//    {
//        Trace.Write(sMessage);
//        return false;
//    }
//
//    static boolean printLog(RuntimeException ex, /*LogType*/int type)
//    {
//        Trace.Write(ex.getMessage());
//        Trace.Write(ex.StackTrace);
//        return false;
//    }
          

    static byte[] readBits(byte b)
    {
        byte[] arr = new byte[8];
        for (int i = 0; i < 8; i++)
        {
            byte temp = (byte) 0x01;
            temp <<= (7-i);

            int iVal = ((temp & 0xFF) & (b & 0xFF));

            if (iVal == 0)
                arr[i] = (byte) 0;
            else
                arr[i] = (byte) 1;
        }

        return arr;
    }

    public static byte[] readBytes(RandomAccessFile _br, long iIndex, int iCount) throws IOException
    {
        // Seek and read from file
        byte[] buff = new byte[iCount];
        _br.seek((long)iIndex);
        _br.read(buff, 0, iCount);

        return buff;
    }
    static byte readByte(RandomAccessFile _br, long iIndex) throws IOException
    {
        // Seek and read from file
        byte bt;
        _br.seek((long)iIndex);
        bt = _br.readByte();

        return bt;
    }
    static byte[] readChars(RandomAccessFile _br, long iIndex, int iCount) throws IOException
    {
        // Seek and read from file
        byte[] buff = new byte[iCount];
        _br.seek((long)iIndex);
        _br.read(buff, 0, iCount);

        return buff;
    }
//
    public static long readUInt32(byte[] buff, int iIndex)throws IOException
    {
        long uiValue = 0;
        uiValue = (buff[iIndex+0] & 0xFF);
        uiValue <<= 8;
        uiValue |= ((buff[iIndex+1]) & 0xFF);
        uiValue <<= 8;
        uiValue |= ((buff[iIndex+2]) & 0xFF);
        uiValue <<= 8;
        uiValue |= ((buff[iIndex+3]) & 0xFF);
        return uiValue;
    }
    
    public static long readUInt64(byte[] buff, int iIndex) throws IOException
    {
        long uiValue = 0;
        uiValue = (buff[iIndex+0] & 0xFF);
        uiValue <<= 8;
        uiValue |= ((buff[iIndex+1]) & 0xFF);
        uiValue <<= 8;
        uiValue |= ((buff[iIndex+2]) & 0xFF);
        uiValue <<= 8;
        uiValue |= ((buff[iIndex+3]) & 0xFF);
        uiValue <<= 8;
        uiValue |= ((buff[iIndex+4]) & 0xFF);
        uiValue <<= 8;
        uiValue |= ((buff[iIndex+5]) & 0xFF);
        uiValue <<= 8;
        uiValue |= ((buff[iIndex+6]) & 0xFF);
        uiValue <<= 8;
        uiValue |= ((buff[iIndex+7]) & 0xFF);        
        return uiValue;
    }
     
    public static long readUInt32(RandomAccessFile _br, long iIndex)throws IOException
    {
        // Seek and read from file
        byte[] buff = new byte[4];
        _br.seek((long)iIndex);
        _br.read(buff, 0, 4);

        long uiValue = 0;
        uiValue = (buff[0] & 0xFF);
        uiValue <<= 8;
        uiValue |= ((buff[1]) & 0xFF);
        uiValue <<= 8;
        uiValue |= ((buff[2]) & 0xFF);
        uiValue <<= 8;
        uiValue |= ((buff[3]) & 0xFF);

        return uiValue;
    }
//
//    static int readInt32(/*ref*/ BinaryReader[] _br, /*UInt64*/long iIndex)
//    {
//        // Seek and read from file
//        byte[] buff = new byte[4];
//        _br[0].getBaseStream().seek((long)iIndex, SeekOrigin.BEGIN);
//        _br[0].read(buff, 0, 4);
//
//        int uiValue = 0;
//        uiValue = (buff[0] & 0xFF);
//        uiValue <<= 8;
//        uiValue |= ((buff[1]) & 0xFF);
//        uiValue <<= 8;
//        uiValue |= ((buff[2]) & 0xFF);
//        uiValue <<= 8;
//        uiValue |= ((buff[3]) & 0xFF);
//
//        return uiValue;
//    }
//
//    static /*uint*/long readUInt32(byte[] buff, /*UInt64*/long iIndex)
//    {
//        /*uint*/long uiValue = 0;
//        uiValue = (buff[iIndex !Autoporter error! Please, do not use uint, long and ulong index in array - it can cause runtime overflow errors! ] & 0xFF);
//        uiValue <<= 8;
//        uiValue |= ((buff[iIndex + 1 !Autoporter error! Please, do not use uint, long and ulong index in array - it can cause runtime overflow errors! ]) & 0xFF);
//        uiValue <<= 8;
//        uiValue |= ((buff[iIndex + 2 !Autoporter error! Please, do not use uint, long and ulong index in array - it can cause runtime overflow errors! ]) & 0xFF);
//        uiValue <<= 8;
//        uiValue |= ((buff[iIndex + 3 !Autoporter error! Please, do not use uint, long and ulong index in array - it can cause runtime overflow errors! ]) & 0xFF);
//
//        return uiValue;
//    }
//
    static int readUInt16(RandomAccessFile _br, long iIndex) throws IOException
    {
        // Seek and read from file
        byte[] buff = new byte[2];
        _br.seek((long)iIndex);
        _br.read(buff, 0, 2);

        int uiValue = 0;
        uiValue = (buff[0] & 0xFF);
        uiValue <<= 8;
        uiValue |= ((buff[1]) & 0xFF);
        return uiValue;
    }
//    
//    static /*ushort*/int readUInt16(byte[] buff, /*UInt64*/long iIndex)
//    {
//        /*ushort*/int uiValue = 0;
//        uiValue = (buff[iIndex !Autoporter error! Please, do not use uint, long and ulong index in array - it can cause runtime overflow errors! ] & 0xFF);
//        uiValue <<= 8;
//        uiValue |= ((buff[iIndex+1 !Autoporter error! Please, do not use uint, long and ulong index in array - it can cause runtime overflow errors! ]) & 0xFF);
//
//        return uiValue;
//    }
//
    public static long readUInt64(RandomAccessFile _br, long iIndex) throws IOException
    {
        // Seek and read from file
        byte[] buff = new byte[8];
        _br.seek((long)iIndex);
        _br.read(buff, 0, 8);

        /*UInt64*/long uiValue = 0;
        uiValue = (buff[0] & 0xFF);
        uiValue <<= 8;
        uiValue |= ((buff[1]) & 0xFF);
        uiValue <<= 8;
        uiValue |= ((buff[2]) & 0xFF);
        uiValue <<= 8;
        uiValue |= ((buff[3]) & 0xFF);
        uiValue <<= 8;
        uiValue |= ((buff[4]) & 0xFF);
        uiValue <<= 8;
        uiValue |= ((buff[5]) & 0xFF);
        uiValue <<= 8;
        uiValue |= ((buff[6]) & 0xFF);
        uiValue <<= 8;
        uiValue |= ((buff[7]) & 0xFF);
        
        return uiValue;
    }
//
//    static long readInt64(/*ref*/ BinaryReader[] _br, /*UInt64*/long iIndex)
//    {
//        // Seek and read from file
//        byte[] buff = new byte[8];
//        _br[0].getBaseStream().seek((long)iIndex, SeekOrigin.BEGIN);
//        _br[0].read(buff, 0, 8);
//
//        long uiValue = 0;
//        uiValue = (buff[0] & 0xFF);
//        uiValue <<= 8;
//        uiValue |= ((buff[1]) & 0xFF);
//        uiValue <<= 8;
//        uiValue |= ((buff[2]) & 0xFF);
//        uiValue <<= 8;
//        uiValue |= ((buff[3]) & 0xFF);
//        uiValue <<= 8;
//        uiValue |= ((buff[4]) & 0xFF);
//        uiValue <<= 8;
//        uiValue |= ((buff[5]) & 0xFF);
//        uiValue <<= 8;
//        uiValue |= ((buff[6]) & 0xFF);
//        uiValue <<= 8;
//        uiValue |= ((buff[7]) & 0xFF);
//
//        return uiValue;
//    }
//
    static long readUInt24(RandomAccessFile _br, long iIndex) throws IOException
    {
        // Seek and read from file
        byte[] buff = new byte[3];
        _br.seek((long)iIndex);
        _br.read(buff, 0, 3);

        /*uint*/long uiValue = 0;
        uiValue = (buff[0] & 0xFF);
        uiValue <<= 8;
        uiValue |= ((buff[1]) & 0xFF);
        uiValue <<= 8;
        uiValue |= ((buff[2]) & 0xFF);
       
        return uiValue;
    }

    static boolean readAtomV1(byte[] buff, /*ref*/ /*UInt64*/long[] uiLength, /*out*/ char[][] sID)
    {
        if (buff.length < 12)
        {
            sID[0] = null;
            return true;
        }

        /*UInt64*/long uiBuffLen = 0;
        uiBuffLen = (buff[0] & 0xFF);
        uiBuffLen <<= 8;
        uiBuffLen |= ((buff[1]) & 0xFF);
        uiBuffLen <<= 8;
        uiBuffLen |= ((buff[2]) & 0xFF);
        uiBuffLen <<= 8;
        uiBuffLen |= ((buff[3]) & 0xFF);
        uiBuffLen <<= 8;
        uiBuffLen |= ((buff[4]) & 0xFF);
        uiBuffLen <<= 8;
        uiBuffLen |= ((buff[5]) & 0xFF);
        uiBuffLen <<= 8;
        uiBuffLen |= ((buff[6]) & 0xFF);
        uiBuffLen <<= 8;
        uiBuffLen |= ((buff[7]) & 0xFF);

        uiLength[0] = uiBuffLen;

        sID[0] = new char[4];
        sID[0][0] = (char)(buff[8] & 0xFF);
        sID[0][1] = (char)(buff[9] & 0xFF);
        sID[0][2] = (char)(buff[10] & 0xFF);
        sID[0][3] = (char)(buff[11] & 0xFF);

        return false;
    }

    public static boolean readAtom(byte[] buff, long[] iLength, char[][] sID)
    {
        if (buff.length < 8)
        {
            sID[0] = null;
            return true;
        }

        long iBuffLen = 0;
        iBuffLen = (buff[0] & 0xFF);
        iBuffLen <<= 8;
        iBuffLen |= ((buff[1]) & 0xFF);
        iBuffLen <<= 8;
        iBuffLen |= ((buff[2]) & 0xFF);
        iBuffLen <<= 8;
        iBuffLen |= ((buff[3]) & 0xFF);

        iLength[0] = iBuffLen;

        sID[0] = new char[4];
        sID[0][0] = (char)(buff[4] & 0xFF);
        sID[0][1] = (char)(buff[5] & 0xFF);
        sID[0][2] = (char)(buff[6] & 0xFF);
        sID[0][3] = (char)(buff[7] & 0xFF);

        return false;
    }
  
    static boolean writeUUInt64(RandomAccessFile _bw, long uiVal) throws IOException
    {
//        byte[] buff = new byte[8];
        
//        for (int iIndex = buff.length - 1; iIndex >= 0; iIndex--)
            _bw.writeLong(uiVal);//Byte(buff[iIndex]);
        return false;
    }
//
    static boolean writeUint32(RandomAccessFile _bw, long uiVal) throws IOException
    {

        ByteOrder b = ByteOrder.nativeOrder();
//        if (b.equals(ByteOrder.BIG_ENDIAN)) {
////            System.out.println("Big-endian");
//        } else {
////            System.out.println("Little-endian");
////            uiVal = ((uiVal & 0x000000FF) << 24) | ((uiVal & 0x0000FF00) << 8) | ((uiVal & 0x00FF0000) >> 8) | ((uiVal & 0xFF000000) >> 24);
//        }
        _bw.writeInt((int) uiVal);
        return false;
    }
//
    static boolean writeUint64(RandomAccessFile _bw, long uiVal) {
        try {
//            byte[] buff = long2bytearray(uiVal);
//            for (int iIndex = buff.length - 1; iIndex >= 0; iIndex--) {
//                _bw.writeByte(buff[iIndex]);
//            }
            _bw.writeLong(uiVal);
            return true;
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
     public static byte[] long2bytearray(long l) {
        byte b[] = new byte[8];

        ByteBuffer buf = ByteBuffer.wrap(b);
        buf.putLong(l);
        return b;
    }
//
    static boolean writeUint16(RandomAccessFile _bw, int uiVal) throws IOException
    {
//        byte[] buff = new byte[uiVal];
//        for (int iIndex = buff.length - 1; iIndex >= 0; iIndex--)
            _bw.writeShort(uiVal);
        return false;
    }
//
    static boolean writeBytes(RandomAccessFile _bw, byte[] arrData) throws IOException
    {
        _bw.write(arrData);
        return false;
    }
    static boolean writeChars(RandomAccessFile _bw, byte[] arrData)throws IOException
    {
        _bw.write(arrData);
        return false;
    }
    
//
//
    static boolean writeUint64(byte[] buff ,int iBaseIndex  , long iVal) throws IOException
    {
        buff[iBaseIndex++] = (byte) ((iVal >> 56) & 0xFF);
        buff[iBaseIndex++] = (byte) ((iVal >> 48) & 0xFF);
        buff[iBaseIndex++] = (byte) ((iVal >> 40) & 0xFF);
        buff[iBaseIndex++] = (byte) ((iVal >> 32) & 0xFF);      
        buff[iBaseIndex++] = (byte) ((iVal >> 24) & 0xFF);
        buff[iBaseIndex++] = (byte) ((iVal >> 16) & 0xFF);
        buff[iBaseIndex++] = (byte) ((iVal >> 8) & 0xFF);
        buff[iBaseIndex++] = (byte) (iVal & 0xFF);
        return false;
    }
    
    static boolean writeUint32(byte[] buff ,int iBaseIndex  , int iVal) throws IOException
    {
        buff[iBaseIndex++] = (byte) ((iVal >> 24) & 0xFF);
        buff[iBaseIndex++] = (byte) ((iVal >> 16) & 0xFF);
        buff[iBaseIndex++] = (byte) ((iVal >> 8) & 0xFF);
        buff[iBaseIndex++] = (byte) (iVal & 0xFF);
        return false;
    }
     
    static boolean writeUint24(byte[] buff ,int iBaseIndex  , int iVal) throws IOException
    {
        buff[iBaseIndex++] = (byte) ((iVal >> 16) & 0xFF);
        buff[iBaseIndex++] = (byte) ((iVal >> 8) & 0xFF);
        buff[iBaseIndex++] = (byte) (iVal & 0xFF);
        return false;
    }
    
    static boolean writeUint24(RandomAccessFile _bw, int iVal) throws IOException
    {
        byte[] buff = new byte[3];

        buff[0] = (byte) ((iVal >> 16) & 0xFF);
        buff[1] = (byte) ((iVal >> 8) & 0xFF);
        buff[2] = (byte) (iVal & 0xFF);
//        buff[3] = (byte) (iVal /*>> 0*/);
//        for (int iIndex = buff.length - 2; iIndex >= 0; iIndex--)
            _bw.write(buff);
        return false;
    }
//
    static boolean writeByte(RandomAccessFile _bw, byte bVal) throws IOException
    {
        _bw.writeByte(bVal);
        return false;
    }
//
    static boolean writeFixed(RandomAccessFile _bw, long ulLength) throws IOException
    {
        byte[] data = new byte[(int)ulLength];
        _bw.write(data);
        return false;
    }
    
    static boolean parseForFrameType(byte[] avcSample ,int iDataLen,int iHdrLen , FrameTypeEnm[] frameType) throws Exception{
        int iSampleSize = iDataLen;
        frameType[0] = FrameTypeEnm.None;
        //int iIndexTONalUnit = 0;

        // Since all slices of an frame has same specs , so no need to iterate each slice , just check the very first slice
        for (int iIndexTONalUnit = 0; iIndexTONalUnit < iSampleSize; ) // to end of the picture
        {
            long uiNalUnitLen = getbits(avcSample, iIndexTONalUnit*8, iHdrLen*8);
            int iForbidden = (readBits(avcSample[iIndexTONalUnit + iHdrLen], 0, 1) & 0xFF);
            int iNal_ref_idc = (readBits(avcSample[iIndexTONalUnit + iHdrLen], 1, 2) & 0xFF);
            int iNal_unit_type = (readBits(avcSample[iIndexTONalUnit + iHdrLen], 3, 5) & 0xFF);
            if ((uiNalUnitLen & 0xFFFFFFFFL) == 0 || uiNalUnitLen > iSampleSize)
                return true; // Invalid NAL unit

            if (iNal_unit_type == 6 || iNal_unit_type == 7 || iNal_unit_type == 8)
            {
                iIndexTONalUnit += (int)(uiNalUnitLen & 0xFFFFFFFFL) + iHdrLen;
                if(iNal_unit_type == 6)
                        frameType[0] = FrameTypeEnm.SEIFrame;
                    if (iNal_unit_type == 7)
                        frameType[0] = FrameTypeEnm.SPSFrame;
                    if (iNal_unit_type == 8)
                        frameType[0] = FrameTypeEnm.PPSFrame;
                continue;
            }

            // IDR Frame
            if (iNal_unit_type == 5)
            {
                frameType[0] = FrameTypeEnm.IDRFrame;
                break;
            }

            int iMaxBitPos = iDataLen * 8;
            int iBitPosition = (iIndexTONalUnit + iHdrLen + 1) * 8; //avcSample[5]
            int[] referenceToIBitPosition = { iBitPosition };
            int first_mb_in_slice = (((int)(getUE(avcSample, referenceToIBitPosition, iMaxBitPos) & 0xFFFFFFFFL)) & 0xFFFF);
            iBitPosition = referenceToIBitPosition[0];  // UE variable length
//            referenceToIBitPosition[0] = iBitPosition;
            int slice_type = (((int)(getUE(avcSample, referenceToIBitPosition, iMaxBitPos) & 0xFFFFFFFFL)) & 0xFFFF);
            iBitPosition = referenceToIBitPosition[0];         // UE variable length
            
            // I Frame slice types
            if ((slice_type & 0xFFFF) == 2 || (slice_type & 0xFFFF) == 4 || (slice_type & 0xFFFF) == 7 || (slice_type & 0xFFFF) == 9)
            {
                frameType[0] = FrameTypeEnm.IFrame;
                break;
            }

            if (slice_type == 0 || slice_type == 3 || slice_type == 5 || slice_type == 8)
                {
                    frameType[0] = FrameTypeEnm.PFrame;
                }

                if (slice_type == 1 || slice_type == 6)
                {
                    frameType[0] = FrameTypeEnm.BFrame;
                }
            iIndexTONalUnit += (int)(uiNalUnitLen & 0xFFFFFFFFL) + iHdrLen;
        }            
        return false;
    }
    
     static boolean parseForNalUnits(byte[] avcSample ,int iDataLen,int iHdrLen , boolean[] IsIFrame) throws Exception
    {            
        IsIFrame[0] = false;
        int iSampleSize = iDataLen;
        //int iIndexTONalUnit = 0;

        // Since all slices of an frame has same specs , so no need to iterate each slice , just check the very first slice
        for (int iIndexTONalUnit = 0; iIndexTONalUnit < iSampleSize; ) // to end of the picture
        {
            long uiNalUnitLen = getbits(avcSample, iIndexTONalUnit*8, iHdrLen*8);
            int iForbidden = (readBits(avcSample[iIndexTONalUnit + iHdrLen], 0, 1) & 0xFF);
            int iNal_ref_idc = (readBits(avcSample[iIndexTONalUnit + iHdrLen], 1, 2) & 0xFF);
            int iNal_unit_type = (readBits(avcSample[iIndexTONalUnit + iHdrLen], 3, 5) & 0xFF);
            if ((uiNalUnitLen & 0xFFFFFFFFL) == 0)
                return true;

            if (iNal_unit_type == 6 || iNal_unit_type == 7 || iNal_unit_type == 8)
            {
                iIndexTONalUnit += (int)(uiNalUnitLen & 0xFFFFFFFFL) + iHdrLen;
                continue;
            }

            // IDR Frame
            if (iNal_unit_type == 5)
            {
                IsIFrame[0] = true;
                break;
            }

            int iMaxBitPos = iDataLen * 8;
            int iBitPosition = (iIndexTONalUnit + iHdrLen + 1) * 8; //avcSample[5]
            int[] referenceToIBitPosition = { iBitPosition };
            int first_mb_in_slice = (((int)(getUE(avcSample, referenceToIBitPosition, iMaxBitPos) & 0xFFFFFFFFL)) & 0xFFFF);
            iBitPosition = referenceToIBitPosition[0];  // UE variable length
//            referenceToIBitPosition[0] = iBitPosition;
            int slice_type = (((int)(getUE(avcSample, referenceToIBitPosition, iMaxBitPos) & 0xFFFFFFFFL)) & 0xFFFF);
            iBitPosition = referenceToIBitPosition[0];         // UE variable length
            
            // I Frame slice types
            if ((slice_type & 0xFFFF) == 2 || (slice_type & 0xFFFF) == 4 || (slice_type & 0xFFFF) == 7 || (slice_type & 0xFFFF) == 9)
            {
                IsIFrame[0] = true;
                break;
            }

            iIndexTONalUnit += (int)(uiNalUnitLen & 0xFFFFFFFFL) + iHdrLen;
        }            
        return false;
    }
    

    static byte readBits(byte btVal, int iIndex, int iCount)
    {
        byte btValue = (byte) 0;
        while (iCount > 0)
        {
            btValue <<= 1;
            byte btTemp = (byte)(1 << (7 - iIndex));
            btTemp &= btVal;
            if (btTemp != 0)
                btValue |= 0x01;
            iCount--;
            iIndex++;
        }
        return btValue;
    }

    public static long getUE(byte[] buf, int[] iBitPosition, int iMaxBitPos) throws Exception
    {
        if (iBitPosition[0] >= iMaxBitPos)
            return 0;
        long iRetVal = 0;
        int iLeadingZeros = 0;
        long iTrailingValue = 0;
        while ((getbits(buf, iBitPosition[0] + iLeadingZeros, 1) & 0xFFFFFFFFL) == 0)
        {
            iLeadingZeros++;
            if (iBitPosition[0] + iLeadingZeros >= iMaxBitPos)
                return 0;
        }

        if (iBitPosition[0] + (iLeadingZeros * 2) - 1 > iMaxBitPos)
            return 0;

        if (iLeadingZeros > 0)
        {
            iTrailingValue = getbits(buf, iBitPosition[0] + iLeadingZeros + 1, iLeadingZeros);
            iRetVal = (((long)((1 << iLeadingZeros) - 1)) & 0xFFFFFFFFL) + (iTrailingValue & 0xFFFFFFFFL);
        }

        // update bit position
        iBitPosition[0] += (2 * iLeadingZeros) + 1;
        return iRetVal;
    }

    public static int getSE(byte[] buf, int[] iBitPosition, int iMaxBitPos) throws Exception
    {

        int iRetVal = 0;
        long code = (getUE(buf, iBitPosition, iMaxBitPos) & 0xFFFFFFFFL);

        /* Decode magnitude */
        iRetVal = (int)(((code & 0xFFFFFFFFL) >> 1) + ((code & 0xFFFFFFFFL) & 1));

        /* Decode sign */
        if (((code & 0xFFFFFFFFL) & 1) == 0)
            iRetVal = -iRetVal;

        return iRetVal;
    }

    public static long getU(byte[] buf, int[] iBitPosition, int iCount, int iMaxBitPos) throws Exception
    {
        if (iBitPosition[0] + iCount > iMaxBitPos)
            return 0;

        long iRetVal = (getbits(buf, iBitPosition[0], iCount) & 0xFFFFFFFFL);
        iBitPosition[0] += iCount;
        return iRetVal;
    }

    public static long getbits(byte[] buf, int start, int count) throws Exception
    {
        long result = 0;
        char bit;
        try{
        while (count > 0)
        {
            if (start < buf.length * 8) {
                result <<= 1;
                bit = (char) (1 << (7 - (start % 8)));
                result |= (long) ((((buf[start / 8] & 0xFF) & bit) == 0) ? 0 : 1);
                start++;
                count--;
            }else{
                return 0;
            }
        }
        }
        catch(Exception ex){
            throw ex;
//            log.error(ex);
        }
        return result;
    }

    public static long getbits(byte buf, int start, int count)
    {
        long result = 0;
        char bit;

        while (count > 0)
        {
            result <<= 1;
            bit = (char)(1 << (7 - (start % 8)));
            result |= (/*uint*/long)((((buf & 0xFF) & bit) == 0) ? 0 : 1);
            start++;
            count--;
        }
        return result;
    }
}

