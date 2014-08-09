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
import org.guideme.guideme.model.Audio;
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
                String pageIdText = StringUtils.stripEnd(pageId.getText(), "#");
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
                    String textString = StringUtils.strip(quotedString.getText(), "\"'’");
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
                    String srcString = StringUtils.strip(quotedString.getText(), "\"'’");
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
                        String pageIdText = StringUtils.stripEnd(pageId.getText(), "#");
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
                        switch (time.TIME_UNIT(0).getText()) {
                            case "sec":
                                delay.setPeriod(String.format("00:00:%s", time.INT(0).getText()));
                                break;
                            case "min":
                                delay.setPeriod(String.format("00:%s:00", time.INT(0).getText()));
                                break;
                            case "hrs":
                                delay.setPeriod(String.format("%s:00:00", time.INT(0).getText()));
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
                    if (actionTarget.PAGE_ID() != null) {
                        // if pageId still ends with a #, strip it.
                        delay.setTarget(StringUtils.stripEnd(actionTarget.PAGE_ID().getText(), "#"));
                    }
                }

                currentPage.addDelay(delay);
            }
            super.enterAction_delay(ctx);
        }

        @Override
        public void enterButton(NyxScriptParser.ButtonContext ctx) {
            if (currentPage != null) {
                Button button = new Button();

                if (ctx.PAGE_ID() != null) {
                    // if pageId still ends with a #, strip it.
                    button.setTarget(StringUtils.stripEnd(ctx.PAGE_ID().getText(), "#"));
                }
                
                if (ctx.QUOTED_STRING() != null) {
                    button.setText(StringUtils.strip(ctx.QUOTED_STRING().getText(), "\"'’"));
                }

                currentPage.addButton(button);
            }
            super.enterButton(ctx);
        }

        @Override
        public void enterAction_yn(NyxScriptParser.Action_ynContext ctx) {
            if (currentPage != null) {
                if (ctx.yes_button() != null) {
                    if (ctx.yes_button().PAGE_ID() != null) {
                        String yesTarget = StringUtils.stripEnd(ctx.yes_button().PAGE_ID().getText(), "#");
                        currentPage.addButton(GuideEditor.createYesButton(yesTarget));
                    }
                }
                if (ctx.no_button() != null) {
                    if (ctx.no_button().PAGE_ID() != null) {
                        String noTarget = StringUtils.stripEnd(ctx.no_button().PAGE_ID().getText(), "#");
                        currentPage.addButton(GuideEditor.createNoButton(noTarget));
                    }
                }
            }
            super.enterAction_yn(ctx);
        }

        @Override
        public void enterHidden_sound(NyxScriptParser.Hidden_soundContext ctx) {
            if (currentPage != null) {
                Audio audio = new Audio();
                
                if (ctx.QUOTED_STRING() != null) {
                    audio.setSrc(StringUtils.strip(ctx.QUOTED_STRING().getText(), "\"'’"));
                }
                if (ctx.INT() != null) {
                    audio.setLoops(Integer.valueOf(ctx.INT().getText()));
                }
                
                currentPage.addAudio(audio);
            }
            super.enterHidden_sound(ctx);
        }

        
    }

}
