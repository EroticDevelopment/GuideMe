package org.guideme.guideme;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

import org.guideme.guideme.model.Chapter;
import org.guideme.guideme.model.Guide;
import org.guideme.guideme.model.Page;
import org.guideme.guideme.readers.XmlGuideReader;
import org.guideme.guideme.settings.AppSettings;
import org.guideme.guideme.ui.MainShell;
import org.guideme.guidme.mock.AppSettingsMock;
import org.guideme.guidme.mock.MainShellMock;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class MainLogicTest {
	private MainLogic mainLogic = MainLogic.getMainLogic();
	private Guide guide = Guide.getGuide();
	private MainShell mainShell = new MainShellMock(); 
	private AppSettings appSettings = AppSettingsMock.getAppSettings();
	private XmlGuideReader xmlGuideReader = XmlGuideReader.getXmlGuideReader();
	private String dataDirectory = "Y:\\TM\\Teases";
	private Boolean singlePage = false;
	private Boolean allGuides = true;
	private Boolean scriptedTest = false;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testDisplayPageStringBooleanGuideMainShellAppSettings() {
		if (singlePage) {
			appSettings.setDataDirectory(dataDirectory);
			xmlGuideReader.loadXML(dataDirectory + "\\Cash In My Points.xml", guide);
			mainLogic.displayPage("start", false, guide, mainShell, appSettings);
		}
		assertTrue(true);
	}

	@Test
	public void testDisplayPageStringStringBooleanGuideMainShellAppSettings() {
		if (allGuides) {
			appSettings.setDataDirectory(dataDirectory);
			File f = new File(dataDirectory);
			// wildcard filter class handles the filtering
			MainLogic.WildCardFileFilter WildCardfilter = mainLogic.new WildCardFileFilter();
			WildCardfilter.setFilePatern("*.xml");
			if (f.isDirectory()) {
				// return a list of matching files
				File[] children = f.listFiles(WildCardfilter);
				for (File file : children) {
					guide.reset(file.getName().substring(0, file.getName().length() - 4));
					xmlGuideReader.loadXML(file.getAbsolutePath(), guide);
					Set<String> chapters = guide.getChapters().keySet();
					for (String chapterId : chapters) {
						Chapter chapter = guide.getChapters().get(chapterId);
						Set<String> pages = chapter.getPages().keySet();
						for (String pageId : pages) {
							Page page = chapter.getPages().get(pageId);
							mainLogic.displayPage(chapter.getId(), page.getId(), false, guide, mainShell, appSettings);		
						}
					}
				}
			}
		}
		assertTrue(true);
	}
	
	@Test
	public void scriptedTest() {
		if (scriptedTest) {
			String script = "data\\pageScript.txt";
			appSettings.setDataDirectory(dataDirectory);
			 try {
				BufferedReader instructions = new BufferedReader(new FileReader(script));
				String guideFile = instructions.readLine();
				if (!guideFile.equals(null)) {
					guide.reset(guideFile);
					xmlGuideReader.loadXML(dataDirectory + "\\" + guideFile + ".xml", guide);
				}
				String instruction = instructions.readLine();
				String fields[];
				while (!(instruction == null)) {
					fields = instruction.split(",");
					mainLogic.displayPage(fields[0], fields[1], false, guide, mainShell, appSettings);
					instruction = instructions.readLine();
				}
				instructions.close();
			} catch (FileNotFoundException e) {
				fail("scriptedTest input file not found: " + script);
			} catch (IOException e) {
				fail("IO Error on script file " + script);
			}
		}
		assertTrue(true);
	}

}
