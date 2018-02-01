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
public class MP4AInfo {
	
    public long m_uiChannels;
    public long m_uiSampleSize;
    public long m_uiSampleRate;
    public byte[] m_arrDecoderCfgData;

    public void CloneTo(MP4AInfo that)
    {
        that.m_uiChannels = m_uiChannels;
        that.m_uiSampleSize = m_uiSampleSize;
        that.m_uiSampleRate = m_uiSampleRate;
        that.m_arrDecoderCfgData = m_arrDecoderCfgData;
    }

    public MP4AInfo Clone() 
    { 
        MP4AInfo struct = new MP4AInfo(); 
        CloneTo(struct); 
        return struct; 
    }

    public Object clone() 
    { 
        return Clone(); 
    }

    public void setM_arrDecoderCfgData(byte[] m_arrDecoderCfgData) {
        this.m_arrDecoderCfgData = m_arrDecoderCfgData;
    }

    public void setM_uiChannels(long m_uiChannels) {
        this.m_uiChannels = m_uiChannels;
    }

    public void setM_uiSampleRate(long m_uiSampleRate) {
        this.m_uiSampleRate = m_uiSampleRate;
    }

    public void setM_uiSampleSize(long m_uiSampleSize) {
        this.m_uiSampleSize = m_uiSampleSize;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MP4AInfo other = (MP4AInfo) obj;
        if (this.m_uiChannels != other.m_uiChannels) {
            return false;
        }
        if (this.m_uiSampleSize != other.m_uiSampleSize) {
            return false;
        }
        if (this.m_uiSampleRate != other.m_uiSampleRate) {
            return false;
        }
        if (!Arrays.equals(this.m_arrDecoderCfgData, other.m_arrDecoderCfgData)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (int) (this.m_uiChannels ^ (this.m_uiChannels >>> 32));
        hash = 29 * hash + (int) (this.m_uiSampleSize ^ (this.m_uiSampleSize >>> 32));
        hash = 29 * hash + (int) (this.m_uiSampleRate ^ (this.m_uiSampleRate >>> 32));
        hash = 29 * hash + Arrays.hashCode(this.m_arrDecoderCfgData);
        return hash;
    }

    
//    private boolean EqualsByValue(MP4AInfo that)
//    {
//        return that.m_uiChannels == m_uiChannels && that.m_uiSampleSize == m_uiSampleSize && that.m_uiSampleRate == m_uiSampleRate && msObject.equals(that.m_arrDecoderCfgData, m_arrDecoderCfgData);
//    }
//
//    public boolean equals(Object obj) 
//    { 
//        assert obj != null; 
//        if (msObject.referenceEquals(null, obj)) return false; 
//        if (msObject.referenceEquals(this, obj)) return true; 
//        if (!(obj instanceof MP4AInfo)) return false; 
//        return EqualsByValue((MP4AInfo)obj); 
//    } 
//    
//    public static boolean equals(MP4AInfo obj1, MP4AInfo obj2) 
//    { 
//        return obj1.equals(obj2); 
//    }

    public byte[] getM_arrDecoderCfgData() {
        return m_arrDecoderCfgData;
    }

    public long getM_uiChannels() {
        return m_uiChannels;
    }

    public long getM_uiSampleRate() {
        return m_uiSampleRate;
    }

    public long getM_uiSampleSize() {
        return m_uiSampleSize;
    }
}
