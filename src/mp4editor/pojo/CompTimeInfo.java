/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mp4editor.pojo;

/**
 *
 * @author RAHUL
 */
public class CompTimeInfo {
	
    private long uiSampleCount;
    private int uiOffset;

    public void CloneTo(CompTimeInfo that)
    {
        that.uiSampleCount = uiSampleCount;
        that.uiOffset = uiOffset;
    }
    
    public void unInit(){
        uiSampleCount = 0;
        uiOffset = 0;
    }

    public CompTimeInfo Clone() 
    { 
        CompTimeInfo struct = new CompTimeInfo(); 
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
        final CompTimeInfo other = (CompTimeInfo) obj;
        if (this.uiSampleCount != other.uiSampleCount) {
            return false;
        }
        if (this.uiOffset != other.uiOffset) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int) (this.uiSampleCount ^ (this.uiSampleCount >>> 32));
        hash = 83 * hash + (int) (this.uiOffset ^ (this.uiOffset >>> 32));
        return hash;
    }

    public void setUiOffset(int uiOffset) {
        this.uiOffset = uiOffset;
    }

    public void setUiSampleCount(long uiSampleCount) {
        this.uiSampleCount = uiSampleCount;
    }


//    //JAVA-added Equality members 
//
//    private boolean EqualsByValue(CompTimeInfo that)
//    {
//        return that.uiSampleCount == uiSampleCount && that.uiOffset == uiOffset;
//    }
//
//    public boolean equals(Object obj) 
//    { 
//        assert obj != null; 
//        if (msObject.referenceEquals(null, obj)) return false; 
//        if (msObject.referenceEquals(this, obj)) return true; 
//        if (!(obj instanceof CompTimeInfo)) return false; 
//        return EqualsByValue((CompTimeInfo)obj); 
//    } 
//    
//    public static boolean equals(CompTimeInfo obj1, CompTimeInfo obj2) 
//    { 
//        return obj1.equals(obj2); 
//    }

    public int getUiOffset() {
        return uiOffset;
    }

    public long getUiSampleCount() {
        return uiSampleCount;
    }
}
