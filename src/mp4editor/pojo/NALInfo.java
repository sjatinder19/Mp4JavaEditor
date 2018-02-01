/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mp4editor.pojo;

import java.util.Arrays;

/**
 *
 * @author RAHUL
 */
public class NALInfo {
	
    public long m_uiLength;
    public byte[] m_data;


    public void CloneTo(NALInfo that)
    {
        that.m_uiLength = m_uiLength;
        that.m_data = m_data;
    }

    public NALInfo Clone() 
    { 
        NALInfo struct = new NALInfo(); 
        CloneTo(struct); 
        return struct; 
    }

    public Object clone() 
    { 
        return Clone(); 
    }

    public void setM_data(byte[] m_data) {
        this.m_data = m_data;
    }

    public void setM_uiLength(long m_uiLength) {
        this.m_uiLength = m_uiLength;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NALInfo other = (NALInfo) obj;
        if (this.m_uiLength != other.m_uiLength) {
            return false;
        }
        if (!Arrays.equals(this.m_data, other.m_data)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (int) (this.m_uiLength ^ (this.m_uiLength >>> 32));
        hash = 97 * hash + Arrays.hashCode(this.m_data);
        return hash;
    }

//    private boolean EqualsByValue(NALInfo that)
//    {
//        return that.m_uiLength == m_uiLength && msObject.equals(that.m_data, m_data);
//    }
//
//    public boolean equals(Object obj) 
//    { 
//        assert obj != null; 
//        if (msObject.referenceEquals(null, obj)) return false; 
//        if (msObject.referenceEquals(this, obj)) return true; 
//        if (!(obj instanceof NALInfo)) return false; 
//        return EqualsByValue((NALInfo)obj); 
//    } 
//    
//    public static boolean equals(NALInfo obj1, NALInfo obj2) 
//    { 
//        return obj1.equals(obj2); 
//    }

    public byte[] getM_data() {
        return m_data;
    }

    public long getM_uiLength() {
        return m_uiLength;
    }
}
