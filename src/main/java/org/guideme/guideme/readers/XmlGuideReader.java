package org.guideme.guideme.readers;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.guideme.guideme.model.Audio;
import org.guideme.guideme.model.Button;
import org.guideme.guideme.model.Chapter;
import org.guideme.guideme.model.Delay;
import org.guideme.guideme.model.Guide;
import org.guideme.guideme.model.Image;
import org.guideme.guideme.model.Metronome;
import org.guideme.guideme.model.Page;
import org.guideme.guideme.model.Timer;
import org.guideme.guideme.model.Video;
import org.guideme.guideme.settings.AppSettings;
import org.guideme.guideme.settings.ComonFunctions;
import org.guideme.guideme.settings.GuideSettings;
import org.guideme.guideme.ui.DebugShell;

public class XmlGuideReader {
	private static Logger logger = LogManager.getLogger();
	private ComonFunctions comonFunctions = ComonFunctions.getComonFunctions();
	private DebugShell debugShell;

	private static XmlGuideReader xmlGuideReader;

	private XmlGuideReader() {
	}

	public static synchronized XmlGuideReader getXmlGuideReader() {
		if (xmlGuideReader == null) {
			xmlGuideReader = new XmlGuideReader();
		}
		return xmlGuideReader;
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}


	private enum TagName
	{
		pref, Title, Author, MediaDirectory, Settings, Page, Metronome, Image, Audio, Video, Delay, Timer, Button, Text, javascript, GlobalJavascript, CSS, Include, NOVALUE;

		public static TagName toTag(String str)
		{
			try {
				return valueOf(str);
			} 
			catch (Exception ex) {
				return NOVALUE;
			}
		}   
	}	

	public String loadXML(String xmlFileName, Guide guide, AppSettings appSettings, DebugShell debugShell) {
		String strPage = "start";
		String strFlags;
		GuideSettings guideSettings;

		try {
			int intPos = xmlFileName.lastIndexOf(appSettings.getFileSeparator());
			int intPos2 = xmlFileName.lastIndexOf(".xml");
			String PresName = xmlFileName.substring(intPos + 1, intPos2);
			guide.reset(PresName);
			HashMap<String, Chapter> chapters = guide.getChapters();
			Chapter chapter = new Chapter("default");
			chapters.put("default", chapter);
			guideSettings = guide.getSettings();
			guideSettings.setPageSound(true);

			this.debugShell = debugShell;
			parseFile(xmlFileName, guide, PresName, chapter, appSettings);

			// Return to where we left off
			try {
				if (guideSettings.isForceStartPage()) {
					guideSettings.setPage("start");
				}
				strPage = guideSettings.getPage();
				strFlags = guideSettings.getFlags();
				if (strFlags != "") {
					comonFunctions.SetFlags(strFlags, guide.getFlags());
				}
			} catch (Exception e1) {
				logger.error("loadXML " + PresName + " Continue Exception " + e1.getLocalizedMessage(), e1);
			}
			guide.setSettings(guideSettings);
			guide.getSettings().saveSettings();
		} catch (Exception e) {
			logger.error("loadXML " + xmlFileName + " Exception ", e);
		}
		return strPage;
	}

	private void parseFile(String xmlFileName, Guide guide, String PresName, Chapter chapter, AppSettings appSettings) {
		GuideSettings guideSettings;
		String strTag;
		String ifSet; 
		String ifNotSet; 
		String Set;
		String UnSet;
		String pageId; 
		Page page = null;
		String strTmpTitle = "";
		String strTmpAuthor = "";

		guideSettings = guide.getSettings();

		try {
			FileInputStream fis = new FileInputStream(xmlFileName);
			UnicodeBOMInputStream ubis = new UnicodeBOMInputStream(fis);
			ubis.skipBOM();

			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader reader = factory.createXMLStreamReader(ubis);

			while (reader.hasNext()) {
				int eventType = reader.next(); 
				switch (eventType) {
				case XMLStreamConstants.START_DOCUMENT:
					logger.trace("loadXML " + PresName + " Start document " + xmlFileName);
					break;
				case XMLStreamConstants.END_DOCUMENT:
					logger.trace("loadXML " + PresName + " End document");
					break;
				case XMLStreamConstants.START_ELEMENT:
					//logger.trace("loadXML " + PresName + " Start tag " + reader.getName().getLocalPart());
					strTag = reader.getName().getLocalPart();

					switch (TagName.toTag(strTag)) {
					case pref:
						String key;
						String screen = "";
						String type;
						String value = "";
						key = reader.getAttributeValue(null, "key");
						type = reader.getAttributeValue(null, "type");
						if (! guideSettings.keyExists(key, type)) {
							screen = reader.getAttributeValue(null, "screen");
							value = reader.getAttributeValue(null, "value");
							if (type.equals("String")) {
								guideSettings.addPref(key, value, screen);
							}
							if (type.equals("Boolean")) {
								guideSettings.addPref(key, Boolean.parseBoolean(value), screen);
							}
							if (type.equals("Number")) {
								guideSettings.addPref(key, Double.parseDouble(value), screen);
							}
						}
						logger.trace("loadXML " + PresName + " pref " + key + "|" + value + "|" + screen + "|" + type);
						break;
					case Title:
						try {
							reader.next();
							if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
								strTmpTitle = reader.getText();
							} else {
								strTmpTitle = "";
							}
						} catch (Exception e1) {
							logger.error("loadXML " + PresName + " Title Exception " + e1.getLocalizedMessage(), e1);
						}
						logger.trace("loadXML " + PresName + " title " + strTmpTitle);
						break;
					case Audio:
						try {
							String strId;
							String strStartAt;
							String strStopAt;
							String strTarget;
							strTarget = reader.getAttributeValue(null, "target");
							if (strTarget == null) strTarget = "";
							strStartAt = reader.getAttributeValue(null, "start-at");
							if (strStartAt == null) strStartAt = "";
							strStopAt = reader.getAttributeValue(null, "stop-at");
							if (strStopAt == null) strStopAt = "";
							strId = reader.getAttributeValue(null, "id");
							ifSet = reader.getAttributeValue(null, "if-set");
							if (ifSet == null) ifSet = "";
							ifNotSet = reader.getAttributeValue(null, "if-not-set"); 
							if (ifNotSet == null) ifNotSet = "";
							String loops = reader.getAttributeValue(null, "loops"); 
							if (loops == null) loops = "0";
							String javascript = reader.getAttributeValue(null, "onTriggered");
							if (javascript == null) javascript = "";
							Audio audio = new Audio(strId, strStartAt, strStopAt, strTarget, ifSet, ifNotSet, "", "", loops, javascript);
							page.addAudio(audio);
							logger.trace("loadXML " + PresName + " Audio " + strId+ "|" + strStartAt+ "|" + strStopAt+ "|" + strTarget+ "|" + javascript+ "|" + ifSet+ "|" + ifNotSet);
						} catch (Exception e1) {
							logger.error("loadXML " + PresName + " Audio Exception " + e1.getLocalizedMessage(), e1);
						}
						break;
					case Author:
						try {
							int eventType2 = reader.getEventType();
							while (true) {
								if (eventType2 == XMLStreamConstants.START_ELEMENT) {
									if (reader.getName().getLocalPart().equals("Name")) {
										logger.trace("Name Tag");
										reader.next();
										if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
											strTmpAuthor = reader.getText();
										} else {
											strTmpAuthor = "";
										}
									}
								}
								eventType2 = reader.next();
								if (eventType2 == XMLStreamConstants.END_ELEMENT) {
									if (reader.getName().getLocalPart().equals("Author")) break;
								}
							}
							logger.trace("loadXML " + PresName + " Author " + strTmpAuthor);
						} catch (Exception e1) {
							logger.error("loadXML " + PresName + " Author Exception " + e1.getLocalizedMessage(), e1);
						}
						break;
					case Button:
						try {
							String strTarget;
							strTarget = reader.getAttributeValue(null, "target");
							if (strTarget == null) strTarget = "";
							Set = reader.getAttributeValue(null, "set");
							if (Set == null) Set = "";
							UnSet = reader.getAttributeValue(null, "unset");
							if (UnSet == null) UnSet = "";
							ifSet = reader.getAttributeValue(null, "if-set");
							if (ifSet == null) ifSet = "";
							ifNotSet = reader.getAttributeValue(null, "if-not-set"); 
							if (ifNotSet == null) ifNotSet = "";
							String javascript = reader.getAttributeValue(null, "onclick");
							if (javascript == null) javascript = "";
							String image = reader.getAttributeValue(null, "image"); 
							if (image == null) image = "";
							String hotKey;
							hotKey = reader.getAttributeValue(null, "hotkey");
							if (hotKey == null) hotKey = "";
							reader.next();
							String BtnText;
							if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
								BtnText = reader.getText();
							} else {
								BtnText = "";
							}
							Button button = new Button(strTarget, BtnText, ifSet, ifNotSet, Set, UnSet, javascript, image, hotKey);
							page.addButton(button);
							logger.trace("loadXML " + PresName + " Button " + strTarget+ "|" + BtnText + "|" + ifSet+ "|" + ifNotSet+ "|" + Set+ "|" + UnSet + "|" + javascript);
						} catch (Exception e1) {
							logger.error("loadXML " + PresName + " Button Exception " + e1.getLocalizedMessage(), e1);
						}
						break;
					case Delay:
						try {
							String strSeconds;
							String strStartWith;
							String strStyle;
							String strTarget;
							strTarget = reader.getAttributeValue(null, "target");
							if (strTarget == null) strTarget = "";
							strStartWith = reader.getAttributeValue(null, "start-with");
							if (strStartWith == null) strStartWith = "";
							strStyle = reader.getAttributeValue(null, "style");
							if (strStyle == null) strStyle = "";
							strSeconds = reader.getAttributeValue(null, "seconds");
							ifSet = reader.getAttributeValue(null, "if-set");
							if (ifSet == null) ifSet = "";
							ifNotSet = reader.getAttributeValue(null, "if-not-set"); 
							if (ifNotSet == null) ifNotSet = "";
							Set = reader.getAttributeValue(null, "set");
							if (Set == null) Set = "";
							UnSet = reader.getAttributeValue(null, "unset");
							if (UnSet == null) UnSet = "";
							String javascript = reader.getAttributeValue(null, "onTriggered");
							if (javascript == null) javascript = "";
							Delay delay = new Delay(strTarget, strSeconds, ifSet, ifNotSet, strStartWith, strStyle, Set, UnSet, javascript);
							page.addDelay(delay);
							logger.trace("loadXML " + PresName + " Delay " + strTarget+ "|" + strSeconds+ "|" + ifSet+ "|" + ifNotSet+ "|" + strStartWith+ "|" + strStyle+ "|" + Set+ "|" + UnSet + "|" + javascript);
						} catch (Exception e1) {
							logger.error("loadXML " + PresName + " Delay Exception " + e1.getLocalizedMessage(), e1);
						}
						break;
					case Timer:
						try {
							String strSeconds;
							strSeconds = reader.getAttributeValue(null, "seconds");
							String javascript = reader.getAttributeValue(null, "onTriggered");
							if (javascript == null) javascript = "";
							Timer timer = new Timer(strSeconds, javascript);
							page.addTimer(timer);
							logger.trace("loadXML " + PresName + " Timer " + strSeconds + "|" + javascript);
						} catch (Exception e1) {
							logger.error("loadXML " + PresName + " Timer Exception " + e1.getLocalizedMessage(), e1);
						}
						break;
					case Image:
						try {
							String strImage;
							strImage = reader.getAttributeValue(null, "id");
							if (strImage == null) strImage = "";
							ifSet = reader.getAttributeValue(null, "if-set");
							if (ifSet == null) ifSet = "";
							ifNotSet = reader.getAttributeValue(null, "if-not-set"); 
							if (ifNotSet == null) ifNotSet = "";
							if (!strImage.equals("")){
								Image image = new Image(strImage, ifSet, ifNotSet);
								page.addImage(image);
							}
							logger.trace("loadXML " + PresName + " Image " + strImage+ "|" + ifSet+ "|" + ifNotSet);
						} catch (Exception e1) {
							logger.error("loadXML " + PresName + " Image Exception " + e1.getLocalizedMessage(), e1);
						}
						break;
					case MediaDirectory:
						try {
							reader.next();
							String dir;
							if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
								dir  = reader.getText();
							} else {
								dir = "";
							}
							guide.setMediaDirectory(dir);
							logger.trace("loadXML " + PresName + " MediaDirectory " + dir);
						} catch (Exception e1) {
							logger.error("loadXML " + PresName + " MediaDirectory Exception " + e1.getLocalizedMessage(), e1);
						}
						break;
					case Metronome:
						try {
							String strbpm;
							String beats;
							String loops;
							String rhythm;
							strbpm = reader.getAttributeValue(null, "bpm");
							if (strbpm == null) strbpm = "";
							ifSet = reader.getAttributeValue(null, "if-set");
							if (ifSet == null) ifSet = "";
							ifNotSet = reader.getAttributeValue(null, "if-not-set"); 
							if (ifNotSet == null) ifNotSet = "";
							ifNotSet = reader.getAttributeValue(null, "if-not-set"); 
							if (ifNotSet == null) ifNotSet = "";
							ifNotSet = reader.getAttributeValue(null, "if-not-set"); 
							if (ifNotSet == null) ifNotSet = "";
							beats = reader.getAttributeValue(null, "beats"); 
							if (beats == null) beats = "4";
							loops = reader.getAttributeValue(null, "loops"); 
							if (loops == null) loops = "-1";
							rhythm = reader.getAttributeValue(null, "rhythm"); 
							if (rhythm == null) rhythm = "";
							if (!strbpm.equals("")) {
								Metronome metronome = new Metronome(strbpm, ifSet, ifNotSet, Integer.parseInt(beats), Integer.parseInt(loops), rhythm);
								page.addMetronome(metronome);
							}
							logger.trace("loadXML " + PresName + " Metronome " + strbpm + "|" + ifSet + "|" + ifNotSet);
						} catch (Exception e1) {
							logger.error("loadXML " + PresName + " Metronome Exception " + e1.getLocalizedMessage(), e1);
						}
						break;
					case NOVALUE:
						break;
					case Page:
						try {
							pageId = reader.getAttributeValue(null, "id");
							ifSet = reader.getAttributeValue(null, "if-set");
							if (ifSet == null) ifSet = "";
							ifNotSet = reader.getAttributeValue(null, "if-not-set"); 
							if (ifNotSet == null) ifNotSet = "";
							Set = reader.getAttributeValue(null, "set");
							if (Set == null) Set = "";
							UnSet = reader.getAttributeValue(null, "unset");
							if (UnSet == null) UnSet = "";
							page = new Page(pageId, ifSet, ifNotSet, Set, UnSet, guide.getAutoSetPage());
							debugShell.addPagesCombo(pageId);
							logger.trace("loadXML " + PresName + " Page " + pageId + "|" + ifSet + "|" + ifNotSet + "|" + Set + "|" + UnSet);
						} catch (Exception e1) {
							logger.error("loadXML " + PresName + " Page Exception " + e1.getLocalizedMessage(), e1);
						}
						break;
					case Settings:
						try {
							int eventType2 = reader.getEventType();
							while (true) {
								if (eventType2 == XMLStreamConstants.START_ELEMENT) {
									if (reader.getName().getLocalPart().equals("AutoSetPageWhenSeen")) {
										reader.next();
										if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
											guide.setAutoSetPage(Boolean.parseBoolean(reader.getText()));
										} else {
											guide.setAutoSetPage(true); 
										}
									} else if (reader.getName().getLocalPart().equals("PageSound")) {
										reader.next();
										if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
											guideSettings.setPageSound(Boolean.parseBoolean(reader.getText()));
										} else {
											guideSettings.setPageSound(true); 
										}
									} else if (reader.getName().getLocalPart().equals("ForceStartPage")) {
										reader.next();
										if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
											guideSettings.setForceStartPage(Boolean.parseBoolean(reader.getText()));
										} else {
											guideSettings.setPageSound(false); 
										}
									}	        				 
								}
								eventType2 = reader.next();
								if (eventType2 == XMLStreamConstants.END_ELEMENT) {
									if (reader.getName().getLocalPart().equals("Settings")) break;
								}
							}
						} catch (Exception e1) {
							logger.error("loadXML " + PresName + " Settings Exception " + e1.getLocalizedMessage(), e1);
						}
						break;
					case Text:
						try {
							if (reader.getName().getLocalPart().equals("Text")) {
								String text = processText(reader);
								page.setText(text);
								logger.trace("loadXML " + PresName + " Text " + text);
							}
						} catch (Exception e1) {
							logger.error("loadXML " + PresName + " Text Exception " + e1.getLocalizedMessage(), e1);
						}
						break;
					case Video:
						try {
							String strId;
							String strStartAt;
							String strStopAt;
							String strTarget;
							strTarget = reader.getAttributeValue(null, "target");
							if (strTarget == null) strTarget = "";
							strStartAt = reader.getAttributeValue(null, "start-at");
							if (strStartAt == null) strStartAt = "";
							strStopAt = reader.getAttributeValue(null, "stop-at");
							if (strStopAt == null) strStopAt = "";
							strId = reader.getAttributeValue(null, "id");
							ifSet = reader.getAttributeValue(null, "if-set");
							if (ifSet == null) ifSet = "";
							ifNotSet = reader.getAttributeValue(null, "if-not-set"); 
							if (ifNotSet == null) ifNotSet = "";
							String loops = reader.getAttributeValue(null, "loops"); 
							if (loops == null) loops = "0";
							String javascript = reader.getAttributeValue(null, "onTriggered");
							if (javascript == null) javascript = "";
							Video video = new Video(strId, strStartAt, strStopAt, strTarget, ifSet, ifNotSet, "", "", loops, javascript);
							page.addVideo(video);
							logger.trace("loadXML " + PresName + " Video " + strId + "|" + strStartAt + "|" + strStopAt + "|" + strTarget + "|" + ifSet + "|" + ifNotSet + "|" + "" + "|" + "" + "|" + loops + "|" + javascript);
						} catch (Exception e1) {
							logger.error("loadXML " + PresName + " Video Exception " + e1.getLocalizedMessage(), e1);
						}
						break;
					case javascript:
						try {
							if (reader.getName().getLocalPart().equals("javascript")) {
								String javascript = "";
								int eventType2 = reader.next();
								while (true) {
									switch (eventType2) {
									case XMLStreamConstants.START_ELEMENT:
										break;
									case XMLStreamConstants.END_ELEMENT:
										break;
									case XMLStreamConstants.CHARACTERS:
										javascript = javascript + reader.getText();
										break;
									}
									eventType2 = reader.next();
									if (eventType2 == XMLStreamConstants.END_ELEMENT) {
										if (reader.getName().getLocalPart().equals("javascript")) break;
									}
								}
								logger.trace("loadXML " + PresName + " javascript " + javascript);
								if (! javascript.equals("")) {
									page.setjScript(javascript);
								}
							}
						} catch (Exception e1) {
							logger.error("loadXML " + PresName + " Text Exception " + e1.getLocalizedMessage(), e1);
						}
						break;
					case GlobalJavascript:
						try {
							if (reader.getName().getLocalPart().equals("GlobalJavascript")) {
								String javascript = "";
								int eventType2 = reader.next();
								while (true) {
									switch (eventType2) {
									case XMLStreamConstants.START_ELEMENT:
										break;
									case XMLStreamConstants.END_ELEMENT:
										break;
									case XMLStreamConstants.CHARACTERS:
										javascript = javascript + reader.getText();
										break;
									}
									eventType2 = reader.next();
									if (eventType2 == XMLStreamConstants.END_ELEMENT) {
										if (reader.getName().getLocalPart().equals("GlobalJavascript")) break;
									}
								}
								logger.trace("loadXML " + PresName + " GlobalJavascript " + javascript);
								if (! javascript.equals("")) {
									javascript = guide.getGlobaljScript() + javascript;
									guide.setGlobaljScript(javascript);
								}
							}
						} catch (Exception e1) {
							logger.error("loadXML " + PresName + " Text Exception " + e1.getLocalizedMessage(), e1);
						}
						break;
					case CSS:
						try {
							reader.next();
							String gcss = "";
							while (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
								gcss  = gcss + reader.getText();
								reader.next();
							}
							guide.setCss(gcss);
							logger.trace("loadXML " + PresName + " CSS " + gcss);
						} catch (Exception e1) {
							logger.error("loadXML " + PresName + " CSS Exception " + e1.getLocalizedMessage(), e1);
						}
						break;
					case Include:
						try {
							String incFileName;
							String dataDirectory;
							String prefix = "";
							String fileSeparator = appSettings.getFileSeparator();
							dataDirectory = appSettings.getDataDirectory();
							if (dataDirectory.startsWith("/")) {
								prefix = "/";
							}
							dataDirectory = prefix + comonFunctions.fixSeparator(appSettings.getDataDirectory(), fileSeparator);
							String mediaDirectory = comonFunctions.fixSeparator(guide.getMediaDirectory(), fileSeparator);
							dataDirectory = dataDirectory + fileSeparator + mediaDirectory;
							if (!dataDirectory.endsWith(fileSeparator)) {
								dataDirectory = dataDirectory + fileSeparator;
							}
							incFileName = dataDirectory + comonFunctions.fixSeparator(reader.getAttributeValue(null, "file"), fileSeparator);
							parseFile(incFileName, guide, PresName, chapter, appSettings);
							logger.trace("loadXML " + PresName + " include " + incFileName);
						} catch (Exception e1) {
							logger.error("loadXML " + PresName + " include Exception " + e1.getLocalizedMessage(), e1);
						}
						break;
					default:
						break;
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					//logger.trace("loadXML End tag " + reader.getName().getLocalPart());
					try {
						if (reader.getName().getLocalPart().equals("Page")) {
							chapter = guide.getChapters().get("default");
							chapter.getPages().put(page.getId(), page);
						}
					} catch (Exception e1) {
						logger.error("loadXML " + PresName + " EndPage Exception " + e1.getLocalizedMessage(), e1);
					}
					break;
				case XMLStreamConstants.CHARACTERS:
					break;
				}
				
				//eventType = reader.next();
			}
			if (!strTmpTitle.equals("") || !strTmpAuthor.equals("")) {
				guide.setTitle(strTmpTitle + ", " + strTmpAuthor);
			}
		} catch (Exception e) {
			logger.error("loadXML " + xmlFileName + " Exception ", e);
		}

	}

	private String processText(XMLStreamReader reader) throws XMLStreamException {
		String text = "";
		ArrayList<String> tag = new ArrayList<String>();
		boolean emptyTagTest = false;
		int tagCount = -1;
		int eventType2 = reader.next();
		while (true) {
			switch (eventType2) {
			case XMLStreamConstants.START_ELEMENT:
				if (emptyTagTest) {
					text = text + ">";
				}
				emptyTagTest = true;
				tagCount++;
				if (tag.size() < tagCount + 1) {
					tag.add(reader.getName().toString());
				} else {
					tag.add(tagCount, reader.getName().toString());
				}
				text = text + "<" + tag.get(tagCount);
				for (int i=0; i < reader.getAttributeCount(); i++) {
					text = text + " " + reader.getAttributeName(i) + "=\"" + reader.getAttributeValue(i) + "\"";
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				if (emptyTagTest) {
					text = text + "/>";
				} else {
					text = text + "</"  + tag.get(tagCount) + ">";
				}
				tagCount--;
				emptyTagTest = false;
				break;
			case XMLStreamConstants.CHARACTERS:
				if (emptyTagTest) {
					text = text + ">";
				}
				emptyTagTest = false;
				text = text + reader.getText();
				break;
			}
			eventType2 = reader.next();
			if (eventType2 == XMLStreamConstants.END_ELEMENT) {
				if (reader.getName().getLocalPart().equals("Text")) break;
			}
		}
		return text;
	}
}
