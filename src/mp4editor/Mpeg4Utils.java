package mp4editor;

//package org.ism.mvp.model.editing;
//
//// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********
//
//public class Mpeg4Utils
//{
//    public static boolean getMovieDuration(String sFileName, /*out*/ float[] dbDurInSeconds, /*out*/ String[] sError)
//    {
//        dbDurInSeconds[0] = 0.0f;
//        sError[0] = "";
//        try
//        {
//            Mpeg4FileReader reader = new Mpeg4FileReader();
//            reader.LoadFile(sFileName, /*out*/ sError);
//            MpegFileInfo info = reader.GetMp4FileInfo(/*out*/ sError).Clone();
//            dbDurInSeconds[0] = info.m_fDurationSecs;
//        }
//        catch (RuntimeException ex)
//        {
//            sError[0] = ex.getMessage();
//            return true;
//        }
//        return false;
//    }
//
//    public static boolean getAudioDuration(String sFileName, /*out*/ float[] dbDurInSeconds, /*out*/ String[] sError)
//    {
//        dbDurInSeconds[0] = 0.0f;
//        sError[0] = "";
//        try
//        {
//            Mpeg4FileReader reader = new Mpeg4FileReader();
//            reader.LoadFile(sFileName, /*out*/ sError);
//            MpegFileInfo info = reader.GetMp4FileInfo(/*out*/ sError).Clone();
//            if (info.m_bHasAudio)
//                dbDurInSeconds[0] = info.m_AudioInfo.m_trakInfo.m_fDurationSecs;
//            else
//            {
//                sError[0] = "Audio not present in file.";
//                return true;
//            }
//        }
//        catch (RuntimeException ex)
//        {
//            sError[0] = ex.getMessage();
//            return true;
//        }
//        return false;
//    }
//
//    public static boolean getVideoDuration(String sFileName, /*out*/ float[] dbDurInSeconds, /*out*/ String[] sError)
//    {
//        dbDurInSeconds[0] = 0.0f;
//        sError[0] = "";
//        try
//        {
//            Mpeg4FileReader reader = new Mpeg4FileReader();
//            reader.LoadFile(sFileName, /*out*/ sError);
//            MpegFileInfo info = reader.GetMp4FileInfo(/*out*/ sError).Clone();
//            if (info.m_bHasVideo)
//                dbDurInSeconds[0] = info.m_VideoInfo.m_trakInfo.m_fDurationSecs;
//            else
//            {
//                sError[0] = "Video not present in file.";
//                return true;
//            }
//        }
//        catch (RuntimeException ex)
//        {
//            sError[0] = ex.getMessage();
//            return true;
//        }
//        return false;
//    }
//
//    public static boolean recoverFiles(String sDirPath, /*out*/ String[] sError)
//    {
//        sError[0] = "";
//        try
//        {
//            DirectoryInfo di = new DirectoryInfo(sDirPath);
//            if (di.exists())
//            {
//                FileInfo[] arrFiles = di.getFiles("*.info");
//                for (FileInfo fi : arrFiles)
//                {
//                    // Recover file that is not properly closed
//                    recoverFile(sDirPath, fi.Name, /*out*/ sError);
//                }
//            }
//        }
//        catch (RuntimeException ex)
//        {
//            sError[0] = ex.getMessage();
//            return true;
//        }
//        return false;
//    }
//
//    static boolean validateGopInfo(String sGopInfo, /*out*/ String[] sValidGopInfo, /*out*/ String[] sError)
//    {
//        sError[0] = "";
//        sValidGopInfo[0] = "";
//        try
//        {
//            if (sGopInfo.indexOf("/root") > 0) // gopinfo string is valid
//            {
//                sValidGopInfo[0] = sGopInfo;
//            }
//            else // string is corrupt , get string up to valid GOP
//            {
//                int iIndex = sGopInfo.lastIndexOf("/gopinfo");
//                if (iIndex <= 0)
//                {
//                    sError[0] = "Unable to Validate GopInfo file. No valid gop found in file.";
//                    return true;
//                }
//
//                sValidGopInfo[0] = sGopInfo.substring((0), (0) + (iIndex - 1)); // -1 to remove "<" 
//                sValidGopInfo[0] = msString.plusEqOperator(sValidGopInfo[0], "</gopinfo>"); // Insert End of GOP 
//                sValidGopInfo[0] = msString.plusEqOperator(sValidGopInfo[0], "</root>"); // Insert End of Mp4Info
//            }
//        }
//        catch (RuntimeException ex)
//        {
//            sError[0] = ex.getMessage();
//            return true;
//        }
//        return false;
//    }
//
//    public static boolean recoverFile(String sDirPath, String sMp4InfoFileName, /*out*/ String[] sError)
//    {
//        sError[0] = "";
//        try
//        {
//            String sMp4InfoFile = msString.format("{0}\\{1}", sDirPath, sMp4InfoFileName);
//
//            // Deletes the info file 
//            FileInfo info = new FileInfo(sMp4InfoFile);
//            if (!info.exists())
//            {
//                sError[0] = msString.format("File does not exist ({0})", sMp4InfoFile);
//                return true;
//            }
//                            
//            // read text 
//            //info.Length
//            StreamReader sr = info.openText();
//            StringBuilder builder = msStringBuilder.ctor();
//            char[] arrInfo = new char[4096]; // buffer of size 4K
//            while (sr.peek() >= 0)
//            {
//                int iRead = sr.read(arrInfo, 0, arrInfo.length);
//                msStringBuilder.append(builder, arrInfo, 0, iRead);
//            }
//
//            arrInfo = null;
//            sr.close();
//
//            String sMp4Info = builder.toString();
//            
//
//            String sValidGopInfo = "";
//
//            // Validate GopInfo , if file is corrupt , it extracts maximum valid gops from the file
//            String[] referenceToSValidGopInfo = { sValidGopInfo };
//            boolean outRefCondition0 = validateGopInfo(sMp4Info, /*out*/ referenceToSValidGopInfo, /*out*/ sError);
//            sValidGopInfo = referenceToSValidGopInfo[0];
//            if (outRefCondition0)
//                return true;
//
//            sMp4Info = null;   
//           
//
//            XmlDocument doc = new XmlDocument();
//            doc.loadXml(sValidGopInfo);
//
//            XmlElement ndMp4Info = (XmlElement)doc.getDocumentElement().SelectSingleNode("mp4info");
//
//            String sFileName = ndMp4Info.getAttribute("target");
//            String sTargetFile = msString.format("{0}\\{1}", sDirPath, sFileName);
//            FileInfo mp4File = new FileInfo(sTargetFile);
//            if (!mp4File.exists())
//            {
//                sError[0] = msString.format("File does not exist ({0})", sTargetFile);
//                return true;
//            }
//
//            FileStream fs = new FileStream(sTargetFile, FileMode.OPEN, FileAccess.READ_WRITE, FileShare.READ);
//            BinaryWriter writer = new BinaryWriter(fs);
//            writer.seek(0, SeekOrigin.END);
//
//            byte[] header = new byte[8];
//            long uiFileOffset = 0;
//            long uiAtomEnd = fs.getLength();
//            /*UInt64*/long uiAtomLength = 0;
//            char[] Atom_ID = null;
//            while (uiFileOffset < uiAtomEnd)
//            {
//                fs.seek(uiFileOffset, SeekOrigin.BEGIN);
//                fs.read(header, 0, 8);
//                /*UInt64*/long[] referenceToUiAtomLength = { uiAtomLength };
//                char[][] referenceToAtom_ID = { Atom_ID };
//                Utils.readAtom(header, /*ref*/ referenceToUiAtomLength, /*out*/ referenceToAtom_ID);
//                uiAtomLength = referenceToUiAtomLength[0];
//                Atom_ID = referenceToAtom_ID[0];
//                String sAtomId = msString.ctor(Atom_ID);
//
//                if (sAtomId.compareTo("mdat") == 0 && uiAtomLength == 8)
//                {
//                    break;
//                }
//
//                uiFileOffset += (long)java.lang.Math.max(uiAtomLength, Utils.HEADER_LENGTH); ;
//            }
//
//            // File has valid mdat atom , 
//            if (uiAtomLength != 8)
//            {
//                writer.close();
//                fs.close();
//                info.Delete();
//                return false;
//            }
//
//
//            /*uint*/long uiMdatLen = (((/*uint*/long)(uiAtomEnd - uiFileOffset)) & 0xFFFFFFFFL);
//            fs.seek(uiFileOffset, SeekOrigin.BEGIN);
//            BinaryWriter[] referenceToWriter = { writer };
//            Utils.writeUint32(/*ref*/ referenceToWriter, uiMdatLen);
//            writer = referenceToWriter[0];
//            fs.seek(0, SeekOrigin.END);
//
//            MOOVAtom moov = new MOOVAtom();
//            referenceToWriter[0] = writer;
//            moov.setWriter(/*ref*/ referenceToWriter);
//            writer = referenceToWriter[0];
//            moov.setMediaInfoFile(sMp4InfoFile);
//
//
//            XmlNodeList listTracks = ndMp4Info.SelectNodes(XMLDefines.ELEMENT_TRACK);
//            for (XmlElement track : (Iterable<XmlElement>) listTracks)
//                moov.addTrack(track.OuterXml);
//
//            // recover gops info
//            moov.setGopInfo(doc.getDocumentElement().SelectNodes(XMLDefines.ELEMENT_GOPINFO));
//
//            moov.endWriting();
//            moov.generateAtom();
//
//            writer.close();
//            fs.close();
//
//            // delete the info file
//            info.Delete();
//        }
//        catch (RuntimeException ex)
//        {
//            sError[0] = ex.getMessage();
//            return true;
//        }
//        return false;
//    }
//
//    public static boolean isIFrame(byte[] dataNalUnit, int iDataLen, /*out*/ String[] sError)
//    {
//        sError[0] = "";
//        boolean bIFrame = false;
//        try
//        {
//            boolean[] referenceToBIFrame = { bIFrame };
//            Utils.parseForNalUnits(dataNalUnit, iDataLen, /*out*/ referenceToBIFrame);
//            bIFrame = referenceToBIFrame[0];
//        }
//        catch (RuntimeException ex)
//        {               
//            sError[0] = ex.getMessage();
//        }
//        return bIFrame;
//    }       
//}
//
