/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mp4editor.pojo;

import mp4editor.pojo.enm.BuffTypeEnm;

/**
 *
 * @author RAHUL
 */
public class MP4Buffer {

    private BuffTypeEnm type;            // type of data present
    private byte[] data;               // Data 
    private int iDataLength;           // Length of data
    private boolean bReorder;             // Data from board should set this flag to true
    private boolean bHasCompTime;         // Data from files should set this flag true if the file contains b frames and 
    private float fCompTimeTimeSecs;   // should pass the composition time of the sample
    private byte byNalType;            // 
    private double sampleDelta;

    public boolean isbHasCompTime() {
        return bHasCompTime;
    }

    public void setbHasCompTime(boolean bHasCompTime) {
        this.bHasCompTime = bHasCompTime;
    }

    public boolean isbReorder() {
        return bReorder;
    }

    public void setbReorder(boolean bReorder) {
        this.bReorder = bReorder;
    }

    public byte getByNalType() {
        return byNalType;
    }

    public void setByNalType(byte byNalType) {
        this.byNalType = byNalType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public float getfCompTimeTimeSecs() {
        return fCompTimeTimeSecs;
    }

    public void setfCompTimeTimeSecs(float fCompTimeTimeSecs) {
        this.fCompTimeTimeSecs = fCompTimeTimeSecs;
    }

    public int getiDataLength() {
        return iDataLength;
    }

    public void setiDataLength(int iDataLength) {
        this.iDataLength = iDataLength;
    }

    public BuffTypeEnm getType() {
        return type;
    }

    public void setType(BuffTypeEnm type) {
        this.type = type;
    }

    public double getSampleDelta() {
        return sampleDelta;
    }

    public void setSampleDelta(double sampleDelta) {
        this.sampleDelta = sampleDelta;
    }
}
