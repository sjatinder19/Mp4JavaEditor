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
public class MetaInfo {
	
    public CodecInfo m_codec = new CodecInfo();
    public TrackInfo m_trakInfo = new TrackInfo();

    //JAVA-added structs clone 

    public void CloneTo(MetaInfo that)
    {
        m_codec.CloneTo(that.m_codec);
        m_trakInfo.CloneTo(that.m_trakInfo);
    }

    public MetaInfo Clone() 
    { 
        MetaInfo struct = new MetaInfo(); 
        CloneTo(struct); 
        return struct; 
    }

    public Object clone() 
    { 
        return Clone(); 
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MetaInfo other = (MetaInfo) obj;
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
        int hash = 5;
        return hash;
    }

    public void setM_codec(CodecInfo m_codec) {
        this.m_codec = m_codec;
    }

    public void setM_trakInfo(TrackInfo m_trakInfo) {
        this.m_trakInfo = m_trakInfo;
    }


    //JAVA-added Equality members 

//    private boolean EqualsByValue(org.ism.mvp.model.editing.MetaInfo that)
//    {
//        return msObject.equals(that.m_codec, m_codec) && msObject.equals(that.m_trakInfo, m_trakInfo);
//    }
//
//    public boolean equals(Object obj) 
//    { 
//        assert obj != null; 
//        if (msObject.referenceEquals(null, obj)) return false; 
//        if (msObject.referenceEquals(this, obj)) return true; 
//        if (!(obj instanceof org.ism.mvp.model.editing.MetaInfo)) return false; 
//        return EqualsByValue((org.ism.mvp.model.editing.MetaInfo)obj); 
//    } 
//    
//    public static boolean equals(org.ism.mvp.model.editing.MetaInfo obj1, org.ism.mvp.model.editing.MetaInfo obj2) 
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
