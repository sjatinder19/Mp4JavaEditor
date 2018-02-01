package mp4editor;

// ********* THIS FILE IS AUTO PORTED FORM C# USING CODEPORTING.COM *********

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;



public class Mp4AtomBase
{
    
    private String m_Atom_ID = "";
    private long m_uiFileStart = 0;
    private long m_uiFileEnd = 0;
    private long m_uiAtomSize = 8;
    private int m_uiVersion = 0;
    private ByteBuffer buffer = null;
    public RandomAccessFile m_BinReader = null;
    public RandomAccessFile m_BinWriter = null;
    Mp4AtomBase m_Parent = null;
    protected LinkedHashMap<String, Mp4AtomBase> m_dictChildAtoms;
     
    public Mp4AtomBase()
    {
        m_dictChildAtoms = new LinkedHashMap<String, Mp4AtomBase>();            
    }
    
    public void unInit()
    {
        if(m_dictChildAtoms != null){
            for (Map.Entry<String, Mp4AtomBase> entry : m_dictChildAtoms.entrySet())         
                entry.getValue().unInit();
        }
        m_dictChildAtoms.clear();
        m_dictChildAtoms = null;
    }
            
    Mp4AtomBase getChild(String sAtomID)
    {
        Mp4AtomBase atom = null;

        atom = m_dictChildAtoms.get(sAtomID);
        return atom;        
    }
    public long getFileStart(){
        return m_uiFileStart; 
    } 
    public void setFileStart(/*UInt64*/long value){
        m_uiFileStart = value; 
    }

    public int getVersion(){
        return m_uiVersion; 
    }
    
    public void setVersion(int value){
        m_uiVersion = value;        
        for (Map.Entry<String, Mp4AtomBase> entry : m_dictChildAtoms.entrySet())         
            entry.getValue().setVersion(value);
    }

    public long getFileEnd(){ 
        return m_uiFileEnd; 
    }
    public void setFileEnd(/*UInt64*/long value){
        m_uiFileEnd = value; 
    }

    public long getAtomSize(){ 
        return m_uiAtomSize; 
    }
    public void setAtomSize(/*UInt64*/long value){
        m_uiAtomSize = value; 
    }

    public String getAtomID(){ 
        return m_Atom_ID; 
    }
    public void setAtomID(String value){ 
        m_Atom_ID = value; 
    }

    public boolean parseAtoms(long uiFileStart, long uiAtomSize)
    {
        m_uiFileStart = uiFileStart;
        m_uiAtomSize = uiAtomSize;
        m_uiFileEnd = m_uiFileStart + uiAtomSize;
        return false;
    }

    public boolean generateAtom()
    {
        try {
            long uiAtomSize = getAtomSize();
            
//            uiAtomSize = ((uiAtomSize & 0x000000FF) << 24) | ((uiAtomSize & 0x0000FF00) << 16) |((uiAtomSize & 0x00FF0000) >> 16) |((uiAtomSize & 0xFF000000) >> 24);
//            byte[] buff = new byte[(int)uiAtomSize];//getBytes(uiAtomSize);
//            buffer = ByteBuffer.allocate(4);;
//            for (int iIndex = buff.length - 1; iIndex >= 0;iIndex-- ){
//                
//                buffer.put(buff[iIndex]);
//            }
            Utils.writeUint32(m_BinWriter,uiAtomSize);
//            m_BinWriter.writeInt(uiAtomSize);
            m_BinWriter.write(m_Atom_ID.getBytes());
            return true;
        } catch (IOException ex) {
            //log.error(ex);
            return false;
        }
    }

    public boolean setWriter(RandomAccessFile writer)
    {
        m_BinWriter = null;
        this.m_BinWriter = writer;
        return true;
    }

    public boolean setReader(RandomAccessFile reader)
    {
        m_BinReader = null;
        this.m_BinReader = reader;
        return true;
    }

    public boolean beginWriting()
    {
        for (Map.Entry<String, Mp4AtomBase> entry : m_dictChildAtoms.entrySet())         
            entry.getValue().beginWriting();
        return true;
    }

    public boolean endWriting()
    {
        for (Map.Entry<String, Mp4AtomBase> entry : m_dictChildAtoms.entrySet())         
            entry.getValue().endWriting();
        return true;
    }
   
    public boolean endMediaInfo()
    {
        for (Map.Entry<String, Mp4AtomBase> entry : m_dictChildAtoms.entrySet())         
            entry.getValue().endMediaInfo();
        return true;
    }

    public boolean beginMediaInfo()
    {
        for (Map.Entry<String, Mp4AtomBase> entry : m_dictChildAtoms.entrySet())         
            entry.getValue().beginMediaInfo();
        return true;
    }

    public boolean pauseWriting()
    {
       for (Map.Entry<String, Mp4AtomBase> entry : m_dictChildAtoms.entrySet())         
            entry.getValue().pauseWriting();
        return true;
    }

    public boolean resumeWriting()
    {
        for (Map.Entry<String, Mp4AtomBase> entry : m_dictChildAtoms.entrySet())         
            entry.getValue().resumeWriting();
        return true;
    }

    protected void logInfo(String sMessage, /*LogType*/int type)
    {
        if (m_Parent != null)
            m_Parent.logInfo(sMessage, type);
        //else if (log != null)
        //    log.info(sMessage);
    }
    protected void logInfo(RuntimeException ex)
    {
        if (m_Parent != null)
            m_Parent.logInfo(ex);            
        //else if(log != null)
        //    log.info(ex.getMessage());
    }
}     

