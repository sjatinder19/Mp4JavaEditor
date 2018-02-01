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
public class HintInfo {
	
    public CodecInfo m_codec = new CodecInfo();
    public TrackInfo m_trakInfo = new TrackInfo();

    public void CloneTo(HintInfo that)
    {
        m_codec.CloneTo(that.m_codec);
        m_trakInfo.CloneTo(that.m_trakInfo);
    }

    public HintInfo Clone() 
    { 
        HintInfo struct = new HintInfo(); 
        CloneTo(struct); 
        return struct; 
    }

    public Object clone() 
    { 
        return Clone(); 
    }

    public void setM_codec(CodecInfo m_codec) {
        this.m_codec = m_codec;
    }

    public void setM_trakInfo(TrackInfo m_trakInfo) {
        this.m_trakInfo = m_trakInfo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HintInfo other = (HintInfo) obj;
        if (!Objects.equals(this.m_codec, other.m_codec)) {
            return false;
        }
        if (!Objects.equals(this.m_trakInfo, other.m_trakInfo)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.m_codec);
        hash = 97 * hash + Objects.hashCode(this.m_trakInfo);
        return hash;
    }

    //JAVA-added Equality members 

//    private boolean EqualsByValue(org.ism.mvp.model.editing.HintInfo that)
//    {
//        return msObject.equals(that.m_codec, m_codec) && msObject.equals(that.m_trakInfo, m_trakInfo);
//    }
//
//    public boolean equals(Object obj) 
//    { 
//        assert obj != null; 
//        if (msObject.referenceEquals(null, obj)) return false; 
//        if (msObject.referenceEquals(this, obj)) return true; 
//        if (!(obj instanceof org.ism.mvp.model.editing.HintInfo)) return false; 
//        return EqualsByValue((org.ism.mvp.model.editing.HintInfo)obj); 
//    } 
//    
//    public static boolean equals(org.ism.mvp.model.editing.HintInfo obj1, org.ism.mvp.model.editing.HintInfo obj2) 
//    { 
//        return obj1.equals(obj2); 
//    }

    public CodecInfo getM_codec() {
        return m_codec;
    }

    public TrackInfo getM_trakInfo() {
        return m_trakInfo;
    }
}
