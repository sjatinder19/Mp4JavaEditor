/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mp4editor;

/**
 *
 * @author RAHUL
 */
public class IsmMp4EditInfo {
    MpegFileInfo mpegFileInfo = null;
    String arrSources = null;
    String sXML = null;
    double dbInPointSecs = 0.0;
    double dbOutPointSecs = 0.0;

    public String getArrSources() {
        return arrSources;
    }

    public void setArrSources(String arrSources) {
        this.arrSources = arrSources;
    }

    public double getDbInPointSecs() {
        return dbInPointSecs;
    }

    public void setDbInPointSecs(double dbInPointSecs) {
        this.dbInPointSecs = dbInPointSecs;
    }

    public double getDbOutPointSecs() {
        return dbOutPointSecs;
    }

    public void setDbOutPointSecs(double dbOutPointSecs) {
        this.dbOutPointSecs = dbOutPointSecs;
    }

    public MpegFileInfo getMpegFileInfo() {
        return mpegFileInfo;
    }

    public void setMpegFileInfo(MpegFileInfo mpegFileInfo) {
        this.mpegFileInfo = mpegFileInfo;
    }

    public String getsXML() {
        return sXML;
    }

    public void setsXML(String sXML) {
        this.sXML = sXML;
    }
}
