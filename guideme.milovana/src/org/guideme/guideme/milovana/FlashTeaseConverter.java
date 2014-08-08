package org.guideme.guideme.milovana;

import java.io.IOException;
import java.net.URL;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.guideme.guideme.editor.GuideEditor;
import org.guideme.guideme.milovana.nyxparser.NyxScriptBaseListener;
import org.guideme.guideme.milovana.nyxparser.NyxScriptLexer;
import org.guideme.guideme.milovana.nyxparser.NyxScriptParser;
import org.guideme.guideme.model.Button;
import org.guideme.guideme.model.Delay;
import org.guideme.guideme.model.Guide;
import org.guideme.guideme.model.Image;
import org.guideme.guideme.model.Page;
import org.openide.util.Exceptions;

public class FlashTeaseConverter {
    
    public void loadPages(String teaseId, Guide guide) {
        try {
            String nyxScript = IOUtils.toString(new URL("http://www.milovana.com/webteases/getscript.php?id=" + teaseId), "UTF-8");
            
            parseScript(guide, nyxScript);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    

    public void parseScript(Guide guide, String nyxScript) {

        try {
            ANTLRInputStream antlrStream = new ANTLRInputStream(IOUtils.toInputStream(nyxScript, "UTF-8"));
            NyxScriptLexer lexer = new NyxScriptLexer(antlrStream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            NyxScriptParser parser = new NyxScriptParser(tokens);
            ParseTree tree = parser.guide();

            ParseTreeWalker walker = new ParseTreeWalker();
            NyxScriptGuideBuilder builder = new NyxScriptGuideBuilder(guide);

            walker.walk(builder, tree);

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private class NyxScriptGuideBuilder extends NyxScriptBaseListener {

        private final Guide guide;

        private Page currentPage;

        public NyxScriptGuideBuilder(Guide guide) {
            this.guide = guide;
        }

        public Guide getGuide() {
            return guide;
        }

        @Override
        public void enterPage(NyxScriptParser.PageContext ctx) {
            currentPage = new Page();

            TerminalNode pageId = ctx.PAGE_ID();
            if (pageId != null) {
                // if pageId still ends with a #, strip it.
                String pageIdText = StringUtils.stripEnd(pageId.getText().trim(), "#");
                currentPage.setId(pageIdText);
            }
            super.enterPage(ctx);
        }

        @Override
        public void exitPage(NyxScriptParser.PageContext ctx) {
            if (currentPage != null) {
                guide.addPage(currentPage);
                currentPage = null;
            }
            super.exitPage(ctx);
        }

        @Override
        public void enterText(NyxScriptParser.TextContext ctx) {
            if (currentPage != null) {
                TerminalNode quotedString = ctx.QUOTED_STRING();
                if (quotedString != null) {
                    // Strip surrounding quotes.
                    String textString = StringUtils.strip(quotedString.getText().trim(), "\"'’");
                    currentPage.setText(textString);
                }
            }
            super.enterText(ctx);
        }

        @Override
        public void enterMedia_pic(NyxScriptParser.Media_picContext ctx) {
            if (currentPage != null) {
                TerminalNode quotedString = ctx.QUOTED_STRING();
                if (quotedString != null) {
                    // Strip surrounding quotes.
                    String srcString = StringUtils.strip(quotedString.getText().trim(), "\"'’");
                    Image img = new Image();
                    img.setSrc(srcString);
                    currentPage.addImage(img);
                }
            }
            super.enterMedia_pic(ctx);
        }

        @Override
        public void enterAction_go(NyxScriptParser.Action_goContext ctx) {
            if (currentPage != null) {
                NyxScriptParser.Action_targetContext actionTarget = ctx.action_target();
                if (actionTarget != null) {
                    TerminalNode pageId = actionTarget.PAGE_ID();
                    if (pageId != null) {
                        // if pageId still ends with a #, strip it.
                        String pageIdText = StringUtils.stripEnd(pageId.getText().trim(), "#");
                        Button btn = GuideEditor.createContinueButton(pageIdText);
                        currentPage.addButton(btn);
                    }
                }
            }
            super.enterAction_go(ctx);
        }

        @Override
        public void enterAction_delay(NyxScriptParser.Action_delayContext ctx) {
            if (currentPage != null) {
                Delay delay = new Delay();

                NyxScriptParser.TimeContext time = ctx.time();
                if (time != null) {
                    if (time.INT().size() > 0 && time.TIME_UNIT().size() > 0) {
                        switch (time.TIME_UNIT(0).getText().trim()) {
                            case "sec":
                                delay.setPeriod("00:00:" + time.INT(0).getText().trim());
                                break;
                            case "min":
                                delay.setPeriod("00:" + time.INT(0).getText().trim() + ":00");
                                break;
                            case "hrs":
                                delay.setPeriod(time.INT(0).getText().trim() + ":00:00");
                                break;
                        }
                    }
                }
                
                TerminalNode delayStyle = ctx.DELAY_STYLE();
                if (delayStyle != null) {
                    switch (delayStyle.getText().trim()) {
                        case "hidden":
                            delay.setStyle(Delay.Style.Hidden);
                            break;
                        case "secret":
                            delay.setStyle(Delay.Style.Secret);
                            break;
                        default:
                            delay.setStyle(Delay.Style.Normal);
                            break;
                    }
                }

                NyxScriptParser.Action_targetContext actionTarget = ctx.action_target();
                if (actionTarget != null) {
                    TerminalNode pageId = actionTarget.PAGE_ID();
                    if (pageId != null) {
                        // if pageId still ends with a #, strip it.
                        String pageIdText = StringUtils.stripEnd(pageId.getText().trim(), "#");
                        delay.setTarget(pageIdText);
                    }
                }

                currentPage.addDelay(delay);
            }
            super.enterAction_delay(ctx);
        }

    }

}
