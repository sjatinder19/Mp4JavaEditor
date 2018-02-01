/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mp4editor;

import mp4editor.pojo.VideoInfo;
import java.util.Objects;
import mp4editor.pojo.AudioInfo;
import mp4editor.pojo.HintInfo;
import mp4editor.pojo.MetaInfo;

/**
 *
 * @author RAHUL
 */
public class MpegFileInfo {
    
    private boolean m_bHasVideo;
    private boolean m_bHasAudio;
    private boolean m_bHasHint;
    private boolean m_bHasMeta;
    private float m_fFrameRate;
    private float m_fDurationSecs;
    private VideoInfo m_VideoInfo = new VideoInfo();
    private AudioInfo m_AudioInfo = new AudioInfo();
    private HintInfo m_HintInfo = new HintInfo();
    private MetaInfo m_MetaInfo = new MetaInfo();
    private long m_uiTimeScale;
    private long iSampleIndex = 0;
    private long totalSamples = 0;
    private long m_uiDuration;

    //JAVA-added structs clone 

    public void CloneTo(MpegFileInfo that)
    {
        that.m_bHasVideo = m_bHasVideo;
        that.m_bHasAudio = m_bHasAudio;
        that.m_bHasHint = m_bHasHint;
        that.m_bHasMeta = m_bHasMeta;
        that.m_fFrameRate = m_fFrameRate;
        that.m_fDurationSecs = m_fDurationSecs;
        m_VideoInfo.CloneTo(that.m_VideoInfo);
        m_AudioInfo.CloneTo(that.m_AudioInfo);
        m_HintInfo.CloneTo(that.m_HintInfo);
        m_MetaInfo.CloneTo(that.m_MetaInfo);
        that.m_uiTimeScale = m_uiTimeScale;
        that.m_uiDuration = m_uiDuration;
        that.iSampleIndex = iSampleIndex;
        that.totalSamples = totalSamples;
    }

    public MpegFileInfo Clone() 
    { 
        if(this != null){
            MpegFileInfo struct = new MpegFileInfo(); 
            CloneTo(struct); 
            return struct; 
        }
        return null;
    }

    public Object clone() 
    { 
        return Clone(); 
    }

    public void setM_AudioInfo(AudioInfo m_AudioInfo) {
        this.m_AudioInfo = m_AudioInfo;
    }

    public void setM_HintInfo(HintInfo m_HintInfo) {
        this.m_HintInfo = m_HintInfo;
    }

    public void setM_MetaInfo(MetaInfo m_MetaInfo) {
        this.m_MetaInfo = m_MetaInfo;
    }

    public void setM_VideoInfo(VideoInfo m_VideoInfo) {
        this.m_VideoInfo = m_VideoInfo;
    }

    public void setM_bHasAudio(boolean m_bHasAudio) {
        this.m_bHasAudio = m_bHasAudio;
    }

    public void setM_bHasHint(boolean m_bHasHint) {
        this.m_bHasHint = m_bHasHint;
    }

    public void setM_bHasMeta(boolean m_bHasMeta) {
        this.m_bHasMeta = m_bHasMeta;
    }

    public void setM_bHasVideo(boolean m_bHasVideo) {
        this.m_bHasVideo = m_bHasVideo;
    }

    public void setM_fDurationSecs(float m_fDurationSecs) {
        this.m_fDurationSecs = m_fDurationSecs;
    }

    public void setM_fFrameRate(float m_fFrameRate) {
        this.m_fFrameRate = m_fFrameRate;
    }

    public void setM_uiDuration(long m_uiDuration) {
        this.m_uiDuration = m_uiDuration;
    }

    public void setM_uiTimeScale(long m_uiTimeScale) {
        this.m_uiTimeScale = m_uiTimeScale;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MpegFileInfo other = (MpegFileInfo) obj;
        if (this.m_bHasVideo != other.m_bHasVideo) {
            return false;
        }
        if (this.m_bHasAudio != other.m_bHasAudio) {
            return false;
        }
        //KBL: case no 10870 for Expanding the SameFormate List No the ThreshHold of 2 has been added for the Different formate Media file for FrameRate.
        float frameRateDifference = this.m_fFrameRate - other.m_fFrameRate;
        if (Math.abs(frameRateDifference)>2) {
            return false;
        }
        if (this.m_bHasVideo == true) {
            if (!Objects.equals(this.m_VideoInfo, other.m_VideoInfo)) {
                return false;
            }
        }
        if (this.m_bHasAudio == true) {
            if (!Objects.equals(this.m_AudioInfo, other.m_AudioInfo)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.m_bHasVideo ? 1 : 0);
        hash = 97 * hash + (this.m_bHasAudio ? 1 : 0);
        hash = 97 * hash + Float.floatToIntBits(this.m_fFrameRate);
        hash = 97 * hash + Objects.hashCode(this.m_VideoInfo);
        hash = 97 * hash + Objects.hashCode(this.m_AudioInfo);
        return hash;
    }

   

    

    //JAVA-added Equality members 

//    private boolean EqualsByValue(MpegFileInfo that)
//    {
//        return that.m_bHasVideo == m_bHasVideo && that.m_bHasAudio == m_bHasAudio && that.m_bHasHint == m_bHasHint && that.m_bHasMeta == m_bHasMeta && that.m_fFrameRate == m_fFrameRate && that.m_fDurationSecs == m_fDurationSecs && equals(that.m_VideoInfo, m_VideoInfo) && equals(that.m_AudioInfo, m_AudioInfo) && equals(that.m_HintInfo, m_HintInfo) && equals(that.m_MetaInfo, m_MetaInfo) && that.m_uiTimeScale == m_uiTimeScale && that.m_uiDuration == m_uiDuration;
//    }
//
//    public boolean equals(Object obj) 
//    { 
//        assert obj != null; 
//        if (msObject.referenceEquals(null, obj)) return false; 
//        if (msObject.referenceEquals(this, obj)) return true; 
//        if (!(obj instanceof MpegFileInfo)) return false; 
//        return EqualsByValue((MpegFileInfo)obj); 
//    } 
//    
//    public static boolean equals(MpegFileInfo obj1, MpegFileInfo obj2) 
//    { 
//        return obj1.equals(obj2); 
//    }

    public AudioInfo getM_AudioInfo() {
        return m_AudioInfo;
    }

    public HintInfo getM_HintInfo() {
        return m_HintInfo;
    }

    public MetaInfo getM_MetaInfo() {
        return m_MetaInfo;
    }

    public VideoInfo getM_VideoInfo() {
        return m_VideoInfo;
    }

    public boolean isM_bHasAudio() {
        return m_bHasAudio;
    }

    public boolean isM_bHasHint() {
        return m_bHasHint;
    }

    public boolean isM_bHasMeta() {
        return m_bHasMeta;
    }

    public boolean isM_bHasVideo() {
        return m_bHasVideo;
    }

    public float getM_fDurationSecs() {
        return m_fDurationSecs;
    }

    public float getM_fFrameRate() {
        return m_fFrameRate;
    }

    public long getM_uiDuration() {
        return m_uiDuration;
    }

    public long getM_uiTimeScale() {
        return m_uiTimeScale;
    }

    public long getiSampleIndex() {
        return iSampleIndex;
    }

    public void setiSampleIndex(long iSampleIndex) {
        this.iSampleIndex = iSampleIndex;
    }

    public long getTotalSamples() {
        return totalSamples;
    }

    public void setTotalSamples(long totalSamples) {
        this.totalSamples = totalSamples;
    }

}
