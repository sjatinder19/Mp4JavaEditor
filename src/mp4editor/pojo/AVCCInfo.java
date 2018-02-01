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
public class AVCCInfo {
    public AVCCInfo(){}
	      
    public byte m_AVCProfileIndication;
    public byte m_Profile_compatibility;
    public byte m_AVCLevelIndication;
    public byte m_LenMinusOne;
    public NALInfo[] m_sequenceParameterSetNALUnit;
    public NALInfo[] m_pictureParameterSetNALUnit;

    //JAVA-added structs clone 

    public void CloneTo(AVCCInfo that)
    {
        that.m_AVCProfileIndication = m_AVCProfileIndication;
        that.m_Profile_compatibility = m_Profile_compatibility;
        that.m_AVCLevelIndication = m_AVCLevelIndication;
        that.m_LenMinusOne = m_LenMinusOne;
        that.m_sequenceParameterSetNALUnit = m_sequenceParameterSetNALUnit;
        that.m_pictureParameterSetNALUnit = m_pictureParameterSetNALUnit;
    }

    public AVCCInfo Clone() 
    { 
        AVCCInfo struct = new AVCCInfo(); 
        CloneTo(struct); 
        return struct; 
    }

    public Object clone() 
    { 
        return Clone(); 
    }

    public void setM_AVCLevelIndication(byte m_AVCLevelIndication) {
        this.m_AVCLevelIndication = m_AVCLevelIndication;
    }

    public void setM_AVCProfileIndication(byte m_AVCProfileIndication) {
        this.m_AVCProfileIndication = m_AVCProfileIndication;
    }

    public void setM_LenMinusOne(byte m_LenMinusOne) {
        this.m_LenMinusOne = m_LenMinusOne;
    }

    public void setM_Profile_compatibility(byte m_Profile_compatibility) {
        this.m_Profile_compatibility = m_Profile_compatibility;
    }

    public void setM_pictureParameterSetNALUnit(NALInfo[] m_pictureParameterSetNALUnit) {
        this.m_pictureParameterSetNALUnit = m_pictureParameterSetNALUnit;
    }

    public void setM_sequenceParameterSetNALUnit(NALInfo[] m_sequenceParameterSetNALUnit) {
        this.m_sequenceParameterSetNALUnit = m_sequenceParameterSetNALUnit;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AVCCInfo other = (AVCCInfo) obj;
        if (this.m_AVCProfileIndication != other.m_AVCProfileIndication) {
            return false;
        }
        if (this.m_Profile_compatibility != other.m_Profile_compatibility) {
            return false;
        }
        if (this.m_AVCLevelIndication != other.m_AVCLevelIndication) {
            return false;
        }
        if (this.m_LenMinusOne != other.m_LenMinusOne) {
            return false;
        }
        if (!Arrays.deepEquals(this.m_sequenceParameterSetNALUnit, other.m_sequenceParameterSetNALUnit)) {
            return false;
        }
        if (!Arrays.deepEquals(this.m_pictureParameterSetNALUnit, other.m_pictureParameterSetNALUnit)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.m_AVCProfileIndication;
        hash = 67 * hash + this.m_Profile_compatibility;
        hash = 67 * hash + this.m_AVCLevelIndication;
        hash = 67 * hash + this.m_LenMinusOne;
        hash = 67 * hash + Arrays.deepHashCode(this.m_sequenceParameterSetNALUnit);
        hash = 67 * hash + Arrays.deepHashCode(this.m_pictureParameterSetNALUnit);
        return hash;
    }

    

    

//    private boolean EqualsByValue(AVCCInfo that)
//    {
//        return that.m_AVCProfileIndication == m_AVCProfileIndication && that.m_Profile_compatibility == m_Profile_compatibility && that.m_AVCLevelIndication == m_AVCLevelIndication && that.m_LenMinusOne == m_LenMinusOne && msObject.equals(that.m_sequenceParameterSetNALUnit, m_sequenceParameterSetNALUnit) && msObject.equals(that.m_pictureParameterSetNALUnit, m_pictureParameterSetNALUnit);
//    }
//
//    public boolean equals(Object obj) 
//    { 
//        assert obj != null; 
//        if (msObject.referenceEquals(null, obj)) return false; 
//        if (msObject.referenceEquals(this, obj)) return true; 
//        if (!(obj instanceof AVCCInfo)) return false; 
//        return EqualsByValue((AVCCInfo)obj); 
//    } 
//    
//    public static boolean equals(AVCCInfo obj1, AVCCInfo obj2) 
//    { 
//        return obj1.equals(obj2); 
//    }

    public byte getM_AVCLevelIndication() {
        return m_AVCLevelIndication;
    }

    public byte getM_AVCProfileIndication() {
        return m_AVCProfileIndication;
    }

    public byte getM_LenMinusOne() {
        return m_LenMinusOne;
    }

    public byte getM_Profile_compatibility() {
        return m_Profile_compatibility;
    }

    public NALInfo[] getM_pictureParameterSetNALUnit() {
        return m_pictureParameterSetNALUnit;
    }

    public NALInfo[] getM_sequenceParameterSetNALUnit() {
        return m_sequenceParameterSetNALUnit;
    }
}
