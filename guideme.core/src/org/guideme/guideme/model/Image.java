package org.guideme.guideme.model;

/**
 * Image to be shown when a page is shown to the user.
 */
public class Image {
    
    private String id;
    private String ifSet;
    private String ifNotSet;
    private String set;
    private String unSet;

    /**
     * Default constructor.
     */
    public Image() {
    }

    /**
     * Constructor setting the ID of the image file.
     * 
     * @param id 
     */
    public Image(String id) {
        this.id = id;
    }

    /**
     * URL (relative to Guide folder) of the image file to be shown on the page.
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * URL (relative to Guide folder) of the image file to be shown on the page.
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Show image file only when the following flags are set.
     *
     * @return
     */
    public String getIfSet() {
        return ifSet;
    }

    /**
     * Show image file when the following flags are set.
     *
     * @param ifSet
     */
    public void setIfSet(String ifSet) {
        this.ifSet = ifSet;
    }

    /**
     * Show image file only when the following flags are NOT set.
     *
     * @return
     */
    public String getIfNotSet() {
        return ifNotSet;
    }

    /**
     * Show image file only when the following flags are NOT set.
     *
     * @param ifNotSet
     */
    public void setIfNotSet(String ifNotSet) {
        this.ifNotSet = ifNotSet;
    }

    /**
     * The flags to be set when the image file is shown.
     *
     * @return
     */
    public String getSet() {
        return set;
    }

    /**
     * The flags to be set when the image file is shown.
     *
     * @param set
     */
    public void setSet(String set) {
        this.set = set;
    }

    /**
     * The flags to be unset when the image file is shown.
     *
     * @return
     */
    public String getUnSet() {
        return unSet;
    }

    /**
     * The flags to be unset when the image file is shown.
     *
     * @param unSet
     */
    public void setUnSet(String unSet) {
        this.unSet = unSet;
    }

}
