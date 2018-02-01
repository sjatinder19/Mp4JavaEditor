/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mp4editor.pojo;

/**
 *
 * @author RAHUL
 */
public class TrackInfo {
	
    private long m_uiTrack_ID;
    private long m_uiSampleCount;
    private float m_fDurationSecs;
    private long m_uiSampleDuration;
    private long m_uiDuration;
    private long m_uiTimeScale;

    //JAVA-added structs clone 

    public void CloneTo(TrackInfo that)
    {
        that.m_uiTrack_ID = m_uiTrack_ID;
        that.m_uiSampleCount = m_uiSampleCount;
        that.m_fDurationSecs = m_fDurationSecs;
        that.m_uiSampleDuration = m_uiSampleDuration;
        that.m_uiDuration = m_uiDuration;
        that.m_uiTimeScale = m_uiTimeScale;
    }

    public TrackInfo Clone() 
    { 
        TrackInfo struct = new TrackInfo(); 
        CloneTo(struct); 
        return struct; 
    }

    public Object clone() 
    { 
        return Clone(); 
    }

    public void setM_fDurationSecs(float m_fDurationSecs) {
        this.m_fDurationSecs = m_fDurationSecs;
    }

    public void setM_uiDuration(long m_uiDuration) {
        this.m_uiDuration = m_uiDuration;
    }

    public void setM_uiSampleCount(long m_uiSampleCount) {
        this.m_uiSampleCount = m_uiSampleCount;
    }

    public void setM_uiSampleDuration(long m_uiSampleDuration) {
        this.m_uiSampleDuration = m_uiSampleDuration;
    }

    public void setM_uiTimeScale(long m_uiTimeScale) {
        this.m_uiTimeScale = m_uiTimeScale;
    }

    public void setM_uiTrack_ID(long m_uiTrack_ID) {
        this.m_uiTrack_ID = m_uiTrack_ID;
    }


    //JAVA-added Equality members 

//    private boolean EqualsByValue(org.ism.mvp.model.editing.TrackInfo that)
//    {
//        return that.m_uiTrack_ID == m_uiTrack_ID && that.m_uiSampleCount == m_uiSampleCount && that.m_fDurationSecs == m_fDurationSecs && that.m_uiSampleDuration == m_uiSampleDuration && that.m_uiDuration == m_uiDuration && that.m_uiTimeScale == m_uiTimeScale;
//    }
//
//    public boolean equals(Object obj) 
//    { 
//        assert obj != null; 
//        if (msObject.referenceEquals(null, obj)) return false; 
//        if (msObject.referenceEquals(this, obj)) return true; 
//        if (!(obj instanceof org.ism.mvp.model.editing.TrackInfo)) return false; 
//        return EqualsByValue((org.ism.mvp.model.editing.TrackInfo)obj); 
//    } 
//    
//    public static boolean equals(org.ism.mvp.model.editing.TrackInfo obj1, org.ism.mvp.model.editing.TrackInfo obj2) 
//    { 
//        return obj1.equals(obj2); 
//    }

    public float getM_fDurationSecs() {
        return m_fDurationSecs;
    }

    public long getM_uiDuration() {
        return m_uiDuration;
    }

    public long getM_uiSampleCount() {
        return m_uiSampleCount;
    }

    public long getM_uiSampleDuration() {
        return m_uiSampleDuration;
    }

    public long getM_uiTimeScale() {
        return m_uiTimeScale;
    }

    public long getM_uiTrack_ID() {
        return m_uiTrack_ID;
    }
}
