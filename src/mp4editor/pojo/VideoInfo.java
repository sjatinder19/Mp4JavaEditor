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
public class VideoInfo {
	
    private CodecInfo m_codec = new CodecInfo();
    private TrackInfo m_trakInfo = new TrackInfo();

    //JAVA-added structs clone 

    public void CloneTo(VideoInfo that)
    {
        m_codec.CloneTo(that.m_codec);
        m_trakInfo.CloneTo(that.m_trakInfo);
    }

    public VideoInfo Clone() 
    { 
        VideoInfo struct = new VideoInfo(); 
        CloneTo(struct); 
        return struct; 
    }

    public Object clone() 
    { 
        return Clone(); 
    }

    public CodecInfo getM_codec() {
        return m_codec;
    }

    public TrackInfo getM_trakInfo() {
        return m_trakInfo;
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
        final VideoInfo other = (VideoInfo) obj;
        if (!Objects.equals(this.m_codec, other.m_codec)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.m_codec);
        return hash;
    }

    
    

    //JAVA-added Equality members 

//    private boolean EqualsByValue(VideoInfo that)
//    {
//        return msObject.equals(that.m_codec, m_codec) && msObject.equals(that.m_trakInfo, m_trakInfo);
//    }
//
//    public boolean equals(Object obj) 
//    { 
//        assert obj != null; 
//        if (msObject.referenceEquals(null, obj)) return false; 
//        if (msObject.referenceEquals(this, obj)) return true; 
//        if (!(obj instanceof VideoInfo)) return false; 
//        return EqualsByValue((VideoInfo)obj); 
//    } 
//    
//    public static boolean equals(VideoInfo obj1, VideoInfo obj2) 
//    { 
//        return obj1.equals(obj2); 
//    }

    public VideoInfo() {
    }
}
