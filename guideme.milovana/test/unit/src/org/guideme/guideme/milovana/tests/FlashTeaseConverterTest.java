package org.guideme.guideme.milovana.tests;

import org.guideme.guideme.milovana.FlashTeaseConverter;
import org.guideme.guideme.model.*;
import org.guideme.guideme.model.Delay.Style;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class FlashTeaseConverterTest {

    private FlashTeaseConverter sut;
    private Guide guide;

    @Before
    public void setUp() throws Exception {
        sut = new FlashTeaseConverter();
        guide = new Guide();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void emptyPageWithId() {
        sut.parseScript(guide, "start#page()");

        Page page = guide.getPages().get(0);
        assertEquals("start", page.getId());
    }

    @Test
    public void pageIdStartingWithTheLetterE() {
        // PageIds starting with the letter E were a problem for the old TeaseMe downloader.
        sut.parseScript(guide, "e1#page()");

        assertNotNull(guide.findPage("e1"));
    }
    
    @Test
    public void twoPages() {
        sut.parseScript(guide, "start#page()\np2#page()\n");

        assertEquals(2, guide.getPages().size());
        assertEquals("p2", guide.getPages().get(1).getId());
    }

    @Test
    public void simpleText() {
        sut.parseScript(guide, "start#page(text:'hello')");

        Page page = guide.getPages().get(0);
        assertEquals("hello", page.getText());
    }

    @Test
    public void textWithMarkup() {
        String markupText = "<TEXTFORMAT LEADING=\"2\"><P ALIGN=\"CENTER\"><FONT FACE=\"FontSans\" SIZE=\"24\" COLOR=\"#FFFFFF\" LETTERSPACING=\"0\" KERNING=\"0\">Welcome to this tease.</FONT></P></TEXTFORMAT>";
        sut.parseScript(guide, "start#page(text:'" + markupText + "')");

        Page page = guide.getPages().get(0);
        assertEquals(markupText, page.getText());
    }

    @Test
    public void textWithQuotes() {
        sut.parseScript(guide, "start#page(text:'hello \"stranger\".')");

        Page page = guide.getPages().get(0);
        assertEquals("hello \"stranger\".", page.getText());
    }

    @Test
    public void simpleImage() {
        sut.parseScript(guide, "start#page(media:pic(id:\"path/to/image.png\"))");

        Image img = guide.getPages().get(0).getImages().get(0);
        assertEquals("path/to/image.png", img.getSrc());
    }

    @Test
    public void actionGo() {
        sut.parseScript(guide, "start#page(action:go(target:page2#))");

        Button btn = guide.getPages().get(0).getButtons().get(0);
        assertEquals("page2", btn.getTarget());
        assertEquals("Continue", btn.getText());
    }

    @Test
    public void combinedTextPicActionGo() {
        String line = "start#page(text:'<TEXTFORMAT LEADING=\"2\"><P ALIGN=\"CENTER\"><FONT FACE=\"FontSans\" SIZE=\"24\" COLOR=\"#FFFFFF\" LETTERSPACING=\"0\" KERNING=\"0\">Welcome to this tease.</FONT></P></TEXTFORMAT><TEXTFORMAT LEADING=\"2\"><P ALIGN=\"CENTER\"><FONT FACE=\"FontSans\" SIZE=\"24\" COLOR=\"#FF0000\" LETTERSPACING=\"0\" KERNING=\"0\">Click the &quot;Continue&quot; button.</FONT></P></TEXTFORMAT>',media:pic(id:\"pics01.jpg\"),action:go(target:page2#))";
        sut.parseScript(guide, line);

        Page page = guide.getPages().get(0);
        assertNotNull(page.getText());
        assertEquals(1, page.getImages().size());
        assertEquals(1, page.getButtons().size());
    }

    @Test
    public void actionDelayInSecondsNoStyle() {
        sut.parseScript(guide, "page3#page(action:delay(time:90sec,target:page7#))");
        
        Delay delay = guide.getPages().get(0).getDelays().get(0);
        assertEquals(90, delay.getPeriodInSeconds());
        assertEquals("page7", delay.getTarget());
        assertEquals(Delay.DEFAULT_STYLE, delay.getStyle());
    }

    @Test
    public void actionDelayInMinutes() {
        sut.parseScript(guide, "page3#page(action:delay(time:2min,target:page13#))");
        
        Delay delay = guide.getPages().get(0).getDelays().get(0);
        assertEquals(2*60, delay.getPeriodInSeconds());
    }
    
    @Test
    public void actionDelayInHours() {
        sut.parseScript(guide, "page3#page(action:delay(time:2hrs,target:page13#))");
        
        Delay delay = guide.getPages().get(0).getDelays().get(0);
        assertEquals(2*60*60, delay.getPeriodInSeconds());
    }

    @Test
    public void actionDelayInSecondSecret() {
        sut.parseScript(guide, "page3#page(action:delay(time:40sec,target:page4#,style:secret))");
        
        Delay delay = guide.getPages().get(0).getDelays().get(0);
        assertEquals(40, delay.getPeriodInSeconds());
        assertEquals("page4", delay.getTarget());
        assertEquals(Style.Secret, delay.getStyle());
    }
    
}
