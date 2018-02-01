/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mp4editor.pojo;

import java.util.Objects;


/**
 *
 * @author RAHUL
 */
public class CodecInfo {
	
    private int m_codec;
    private Object m_codecInfo;

    //JAVA-added structs clone 

    public void CloneTo(CodecInfo that)
    {
        that.m_codec = m_codec;
        that.m_codecInfo = m_codecInfo;
    }

    public CodecInfo Clone() 
    { 
        CodecInfo struct = new CodecInfo(); 
        CloneTo(struct); 
        return struct; 
    }

    public Object clone() 
    { 
        return Clone(); 
    }

    public void setM_codec(int m_codec) {
        this.m_codec = m_codec;
    }

    public void setM_codecInfo(Object m_codecInfo) {
        this.m_codecInfo = m_codecInfo;
    }

    
    public int getM_codec() {
        return m_codec;
    }

    public Object getM_codecInfo() {
        return m_codecInfo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final CodecInfo other = (CodecInfo) obj;
        
        if(this.m_codec != other.m_codec)
            return false;
        
        if (this.m_codec == CodecType.AVC_1) {
            if(!((AVC1Info)this.m_codecInfo).equals((AVC1Info)other.m_codecInfo)){
//            if (!Objects.equals(((AVC1Info) this.m_codecInfo), ((AVC1Info) other.m_codecInfo))) {
                return false;
            }
        }
        if (this.m_codec == CodecType.MP_4_A) {
            if (!((MP4AInfo) this.m_codecInfo).equals((MP4AInfo)other.m_codecInfo)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.m_codecInfo);
        return hash;
    }
    
    
}
