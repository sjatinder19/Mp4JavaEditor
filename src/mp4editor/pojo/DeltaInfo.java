/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mp4editor.pojo;

/**
 *
 * @author RAHUL
 */
public class DeltaInfo {
	
    long m_uiSampleCount;
    int m_uiSampleDelta;

    public void CloneTo(DeltaInfo that)
    {
        that.m_uiSampleCount = m_uiSampleCount;
        that.m_uiSampleDelta = m_uiSampleDelta;
    }

    public void unInit(){
        m_uiSampleCount = 0;
        m_uiSampleDelta = 0;
    }
    
    public DeltaInfo Clone() 
    { 
        DeltaInfo struct = new DeltaInfo(); 
        CloneTo(struct); 
        return struct; 
    }

    public Object clone() 
    { 
        return Clone(); 
    }

    public void setM_uiSampleCount(long m_uiSampleCount) {
        this.m_uiSampleCount = m_uiSampleCount;
    }

    public void setM_uiSampleDelta(int m_uiSampleDelta) {
        this.m_uiSampleDelta = m_uiSampleDelta;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DeltaInfo other = (DeltaInfo) obj;
        if (this.m_uiSampleCount != other.m_uiSampleCount) {
            return false;
        }
        if (this.m_uiSampleDelta != other.m_uiSampleDelta) {
            return false;
        }
        return true;
    }

    
    
//    private boolean EqualsByValue(DeltaInfo that)
//    {
//        return that.m_uiSampleCount == m_uiSampleCount && that.m_uiSampleDelta == m_uiSampleDelta;
//    }
//
//    public boolean equals(Object obj) 
//    { 
//        assert obj != null; 
//        if (msObject.referenceEquals(null, obj)) return false; 
//        if (msObject.referenceEquals(this, obj)) return true; 
//        if (!(obj instanceof DeltaInfo)) return false; 
//        return EqualsByValue((DeltaInfo)obj); 
//    } 
//    
//    public static boolean equals(DeltaInfo obj1, DeltaInfo obj2) 
//    { 
//        return obj1.equals(obj2); 
//    }

    public long getM_uiSampleCount() {
        return m_uiSampleCount;
    }

    public long getM_uiSampleDelta() {
        return m_uiSampleDelta;
    }
}
