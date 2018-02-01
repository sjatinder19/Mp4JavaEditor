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
public class AVC1Info {
    private int m_uiWidth;
    private int m_uiHeight;

    public AVCCInfo m_objAVCCInfo = new AVCCInfo();

    public void CloneTo(AVC1Info that)
    {
        that.m_uiWidth = m_uiWidth;
        that.m_uiHeight = m_uiHeight;
        m_objAVCCInfo.CloneTo(that.m_objAVCCInfo);
    }

    public AVC1Info Clone() 
    { 
        AVC1Info struct = new AVC1Info(); 
        CloneTo(struct); 
        return struct; 
    }

    public Object clone() 
    { 
        return Clone(); 
    }

    public void setM_objAVCCInfo(AVCCInfo m_objAVCCInfo) {
        this.m_objAVCCInfo = m_objAVCCInfo;
    }

    public void setM_uiHeight(int m_uiHeight) {
        this.m_uiHeight = m_uiHeight;
    }

    public void setM_uiWidth(int m_uiWidth) {
        this.m_uiWidth = m_uiWidth;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == null && obj == null){
            return true;
        }
        
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AVC1Info other = (AVC1Info) obj;
        if (this.m_uiWidth != other.m_uiWidth) {
            return false;
        }
        if (this.m_uiHeight != other.m_uiHeight) {
            return false;
        }
        if (!Objects.equals(this.m_objAVCCInfo, other.m_objAVCCInfo)) {
            return false;
        }
        return true;
    }

    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.m_uiWidth;
        hash = 53 * hash + this.m_uiHeight;
        hash = 53 * hash + Objects.hashCode(this.m_objAVCCInfo);
        return hash;
    }

//    private boolean EqualsByValue(AVC1Info that)
//    {
//        return that.m_uiWidth == m_uiWidth && that.m_uiHeight == m_uiHeight && msObject.equals(that.m_objAVCCInfo, m_objAVCCInfo);
//    }
//
//    public boolean equals(Object obj) 
//    { 
//        assert obj != null; 
//        if (msObject.referenceEquals(null, obj)) return false; 
//        if (msObject.referenceEquals(this, obj)) return true; 
//        if (!(obj instanceof AVC1Info)) return false; 
//        return EqualsByValue((AVC1Info)obj); 
//    } 
//    
//    public static boolean equals(AVC1Info obj1, AVC1Info obj2) 
//    { 
//        return obj1.equals(obj2); 
//    }

    public AVCCInfo getM_objAVCCInfo() {
        return m_objAVCCInfo;
    }

    public int getM_uiHeight() {
        return m_uiHeight;
    }

    public int getM_uiWidth() {
        return m_uiWidth;
    }
}
