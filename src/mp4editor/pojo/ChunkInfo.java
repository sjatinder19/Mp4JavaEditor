/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mp4editor.pojo;

/**
 *
 * @author RAHUL
 */
public class ChunkInfo {
	
    long m_uiChunkNo;
    long m_uiSampleCount;
//    long m_uiDescIndex;
//    long m_uiFirstSampleNo;
//    long m_uiLastSampleNo;

    //JAVA-added structs clone 

    public void CloneTo(ChunkInfo that)
    {
        that.m_uiChunkNo = m_uiChunkNo;
        that.m_uiSampleCount = m_uiSampleCount;
//        that.m_uiDescIndex = m_uiDescIndex;
//        that.m_uiFirstSampleNo = m_uiFirstSampleNo;
//        that.m_uiLastSampleNo = m_uiLastSampleNo;
    }
    
    public void unInit(){
        m_uiChunkNo = 0;
        m_uiSampleCount = 0;
//        m_uiDescIndex = 0;
//        m_uiFirstSampleNo = 0;
//        m_uiLastSampleNo = 0;
    }

    public ChunkInfo Clone() 
    { 
        ChunkInfo struct = new ChunkInfo(); 
        CloneTo(struct); 
        return struct; 
    }

    public Object clone() 
    { 
        return Clone(); 
    }

    public void setM_uiChunkNo(long m_uiChunkNo) {
        this.m_uiChunkNo = m_uiChunkNo;
    }

//    public void setM_uiDescIndex(long m_uiDescIndex) {
//        this.m_uiDescIndex = m_uiDescIndex;
//    }
//
//    public void setM_uiFirstSampleNo(long m_uiFirstSampleNo) {
//        this.m_uiFirstSampleNo = m_uiFirstSampleNo;
//    }
//
//    public void setM_uiLastSampleNo(long m_uiLastSampleNo) {
//        this.m_uiLastSampleNo = m_uiLastSampleNo;
//    }

    public void setM_uiSampleCount(long m_uiSampleCount) {
        this.m_uiSampleCount = m_uiSampleCount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ChunkInfo other = (ChunkInfo) obj;
        if (this.m_uiChunkNo != other.m_uiChunkNo) {
            return false;
        }
        if (this.m_uiSampleCount != other.m_uiSampleCount) {
            return false;
        }
//        if (this.m_uiDescIndex != other.m_uiDescIndex) {
//            return false;
//        }
//        if (this.m_uiFirstSampleNo != other.m_uiFirstSampleNo) {
//            return false;
//        }
//        if (this.m_uiLastSampleNo != other.m_uiLastSampleNo) {
//            return false;
//        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (int) (this.m_uiChunkNo ^ (this.m_uiChunkNo >>> 32));
        hash = 79 * hash + (int) (this.m_uiSampleCount ^ (this.m_uiSampleCount >>> 32));
//        hash = 79 * hash + (int) (this.m_uiDescIndex ^ (this.m_uiDescIndex >>> 32));
//        hash = 79 * hash + (int) (this.m_uiFirstSampleNo ^ (this.m_uiFirstSampleNo >>> 32));
//        hash = 79 * hash + (int) (this.m_uiLastSampleNo ^ (this.m_uiLastSampleNo >>> 32));
        return hash;
    }

    

//    private boolean EqualsByValue(ChunkInfo that)
//    {
//        return that.m_uiChunkNo == m_uiChunkNo && that.m_uiSampleCount == m_uiSampleCount && that.m_uiDescIndex == m_uiDescIndex && that.m_uiFirstSampleNo == m_uiFirstSampleNo && that.m_uiLastSampleNo == m_uiLastSampleNo;
//    }
//
//    public boolean equals(Object obj) 
//    { 
//        assert obj != null; 
//        if (msObject.referenceEquals(null, obj)) return false; 
//        if (msObject.referenceEquals(this, obj)) return true; 
//        if (!(obj instanceof ChunkInfo)) return false; 
//        return EqualsByValue((ChunkInfo)obj); 
//    } 
//    
//    public static boolean equals(ChunkInfo obj1, ChunkInfo obj2) 
//    { 
//        return obj1.equals(obj2); 
//    }

    public long getM_uiChunkNo() {
        return m_uiChunkNo;
    }

//    public long getM_uiDescIndex() {
//        return m_uiDescIndex;
//    }
//
//    public long getM_uiFirstSampleNo() {
//        return m_uiFirstSampleNo;
//    }
//
//    public long getM_uiLastSampleNo() {
//        return m_uiLastSampleNo;
//    }

    public long getM_uiSampleCount() {
        return m_uiSampleCount;
    }
}
