package org.guideme.guideme.filesupport;

import java.io.File;

/**
 * One item of the recently closed guides history. Comparable by the time
 * field, ascending from most recent to older items.
 */
public class RecentGuide implements Comparable<RecentGuide> {
    int id;
    private final String path;
    private final String guideTitle;
    private String fileName;

    RecentGuide(int id, String path, String guideTitle) {
        this.path = path;
        this.id = id;
        this.guideTitle = guideTitle;
    }

    public String getPath() {
        return path;
    }
    
    public boolean fileExists() {
        return new File(path).exists();
    }

    public String getGuideTitle() {
        return guideTitle;
    }

    public String getFileName() {
        if (fileName == null) {
            int pos = path.lastIndexOf(File.separatorChar);
            if ((pos != -1) && (pos < path.length())) {
                fileName = path.substring(pos + 1);
            } else {
                fileName = path;
            }
        }
        return fileName;
    }

    @Override
    public int compareTo(RecentGuide o) {
        return this.id - o.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RecentGuide) {
            return ((RecentGuide) obj).getPath().equals(path);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.path != null ? this.path.hashCode() : 0);
        return hash;
    }

}
