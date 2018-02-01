/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mp4editor.pojo;

/**
 *
 * @author Jatinder
 */
public class AttributeInfo {
    double duration = 0;
    long creationTimeMillisecs = 0;
    long modificationTimeMillisecs = 0;

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public long getCreationTimeMillisecs() {
        return creationTimeMillisecs;
    }

    public void setCreationTimeMillisecs(long creationTimeMillisecs) {
        this.creationTimeMillisecs = creationTimeMillisecs;
    }

    public long getModificationTimeMillisecs() {
        return modificationTimeMillisecs;
    }

    public void setModificationTimeMillisecs(long modificationTimeMillisecs) {
        this.modificationTimeMillisecs = modificationTimeMillisecs;
    }
}
