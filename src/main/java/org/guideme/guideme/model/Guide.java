package org.guideme.guideme.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.guideme.guideme.MainLogic;
import org.guideme.guideme.settings.AppSettings;
import org.guideme.guideme.settings.GuideSettings;

import uk.co.caprica.vlcj.logger.Logger;

public class Guide {
	private String title;
	private String authorName;
	private List<String> keywords = new ArrayList<String>();
	private String description;
	private String originalUrl;
	private String authorUrl;
	private Image thumbnail;
	private HashMap<String, Chapter> chapters = new HashMap<String, Chapter>();
	
	private String mediaDirectory; //Media subdirectory for current xml file
	private String delStyle; //style for currently running delay
	private String delTarget; //target for currently running delay
	private ArrayList<String> flags = new ArrayList<String>(); //current flags
	private Boolean autoSetPage;
	private String delaySet; //flags to set for currently running delay
	private String delayUnSet; //flags to clear for currently running delay
	private int delStartAtOffSet; //offset for currently running delay
	private String id; //name for current xml that is running
	private GuideSettings settings = new GuideSettings("startup"); //state for the currently running xml
	private String jScript;
	private String delayjScript;
	private String globaljScript;
	private static Guide guide;
	private String css; // css style sheet
	private Boolean inPrefGuide;
	
	private Guide() {
		
	}

	public static synchronized Guide getGuide() {
		if (guide == null) {
			guide = new Guide();
		}
		return guide;
	}
	
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	
	public Collection<String> getKeywords() {
		return keywords;
	}
	public void setKeywords(Collection<String> keywords) {
		this.keywords.clear();
		this.keywords.addAll(keywords);
	}
	
	public void setKeywords(String... keywords) {
		setKeywords(Arrays.asList(keywords));
	}
	public void setKeywordsString(String keywords) {
		this.keywords.clear();
		String[] tmp = keywords.split(",");
		for (int i = 0; i < tmp.length; i++) {
			this.keywords.add(tmp[i].trim());
		}
	}
	public String getKeywordsString() {
		String tmp = this.keywords.toString();
		return tmp.substring(1, tmp.length()-1);
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getOriginalUrl() {
		return originalUrl;
	}
	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}
	
	public String getAuthorUrl() {
		return authorUrl;
	}
	public void setAuthorUrl(String authorUrl) {
		this.authorUrl = authorUrl;
	}
	
	public Image getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(Image thumbnail) {
		this.thumbnail = thumbnail;
	}
	
	public HashMap<String, Chapter> getChapters() {
		return chapters;
	}
	
	public void setChapters(HashMap<String, Chapter> chapters) {
		this.chapters = chapters;
	}
	
	public String getMediaDirectory() {
		return mediaDirectory;
	}

	public void setMediaDirectory(String mediaDirectory) {
		this.mediaDirectory = mediaDirectory;
	}

	public String getDelStyle() {
		return delStyle;
	}

	public void setDelStyle(String delStyle) {
		this.delStyle = delStyle;
	}

	public String getDelTarget() {
		return delTarget;
	}

	public void setDelTarget(String delTarget) {
		this.delTarget = delTarget;
	}

	public ArrayList<String> getFlags() {
		return flags;
	}

	public void setFlags(ArrayList<String> flags) {
		this.flags = flags;
	}

	public Boolean getAutoSetPage() {
		return autoSetPage;
	}

	public void setAutoSetPage(Boolean autoSetPage) {
		this.autoSetPage = autoSetPage;
	}

	public String getDelaySet() {
		return delaySet;
	}

	public void setDelaySet(String delaySet) {
		this.delaySet = delaySet;
	}

	public String getDelayUnSet() {
		return delayUnSet;
	}

	public void setDelayUnSet(String delayUnSet) {
		this.delayUnSet = delayUnSet;
	}

	public int getDelStartAtOffSet() {
		return delStartAtOffSet;
	}

	public void setDelStartAtOffSet(int delStartAtOffSet) {
		this.delStartAtOffSet = delStartAtOffSet;
	}

	public String getId() {
		return id;
	}

	//we are loading a new xml so clear old settings
	public void reset(String id) {
		Logger.trace("Guide reset id: " + id);
		this.id = id;
		settings = new GuideSettings(id);
		mediaDirectory = "";
		delStyle = "";
		delTarget = "";
		flags = new ArrayList<String>();
		autoSetPage = true;
		delaySet = "";
		delayUnSet = "";
		title = "";
		chapters = new HashMap<String, Chapter>(); 
		delStartAtOffSet = 0;
		jScript = "";
		css = "";
		inPrefGuide = false;
		globaljScript = "";
	}

	public GuideSettings getSettings() {
		return settings;
	}
	
	public String getjScript() {
		return jScript;
	}
	
	public void setjScript(String jScript) {
		this.jScript = jScript;
	}
	
	public void setSettings(GuideSettings settings) {
		this.settings = settings;
	}

	public String getDelayjScript() {
		return delayjScript;
	}

	public void setDelayjScript(String delayjScript) {
		this.delayjScript = delayjScript;
	}

	public String getCss() {
		return css;
	}

	public void setCss(String css) {
		String mediaPath;
		AppSettings appSettings = AppSettings.getAppSettings();
		MainLogic mainLogic = MainLogic.getMainLogic();
		mediaPath = mainLogic.getMediaFullPath("", appSettings.getFileSeparator(), appSettings, guide);
		mediaPath = mediaPath.replace("\\", "/");
		//mediaPath = "file:///" + mediaPath;
		css = css.replace("\\MediaDir\\", mediaPath);
		this.css = css;
	}

	public Boolean getInPrefGuide() {
		return inPrefGuide;
	}

	public void setInPrefGuide(Boolean inPrefGuide) {
		this.inPrefGuide = inPrefGuide;
	}

	public String getGlobaljScript() {
		return globaljScript;
	}

	public void setGlobaljScript(String globaljScript) {
		this.globaljScript = globaljScript;
	}

	
}
