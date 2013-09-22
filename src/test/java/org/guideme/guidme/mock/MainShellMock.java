package org.guideme.guidme.mock;

import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.guideme.guideme.model.Button;
import org.guideme.guideme.model.Guide;
import org.guideme.guideme.ui.MainShell;

public class MainShellMock extends MainShell {
	private Shell shell;
	private Display myDisplay;
	private Calendar calCountDown;
	private static Logger logger = LogManager.getLogger();

	@Override
	public Shell createShell(Display display) {
		logger.debug("MainShellMock Enter CreateShell");
		myDisplay = display;
		shell = new Shell(myDisplay);
		logger.debug("MainShellMock Exit CreateShell");
		return shell;
	}

	@Override
	public Calendar getCalCountDown() {
		logger.debug("MainShellMock getCalCountDown:" + calCountDown);
		return calCountDown;
	}

	@Override
	public void setCalCountDown(Calendar calCountDown) {
		this.calCountDown = calCountDown;
		logger.debug("MainShellMock setCalCountDown:" + this.calCountDown);
	}

	@Override
	public void setLblLeft(String lblLeft) {
		logger.debug("MainShellMock setLblLeft:" + lblLeft);
	}

	@Override
	public void setLblCentre(String lblCentre) {
		logger.debug("MainShellMock setLblCentre:" + lblCentre);
	}

	@Override
	public void setLblRight(String lblRight) {
		logger.debug("MainShellMock setLblRight:" + lblRight);
	}

	@Override
	public void setImageLabel(String imgPath, String strImage) {
		logger.debug("MainShellMock setImageLabel Path:" + imgPath + " Image:" + strImage);
	}

	@Override
	public void playVideo(String video, int startAt, int stopAt, int loops,
			String target) {
		logger.debug("MainShellMock playVideo video:" + video + " startAt:" + startAt + " stopAt:" + stopAt + " loops:" + loops + " target:" + target);
	}

	@Override
	public void clearImage() {
		logger.debug("MainShellMock clearImage");
	}

	@Override
	public void playAudio(String audio, int startAt, int stopAt, int loops,
			String target) {
		logger.debug("MainShellMock playAudio audio:" + audio + " startAt:" + startAt + " stopAt:" + stopAt + " loops:" + loops + " target:" + target);
	}

	@Override
	public void setBrwsText(String brwsText) {
		logger.debug("MainShellMock brwsText:" + brwsText);
	}

	@Override
	public void removeButtons() {
		logger.debug("MainShellMock removeButtons");
	}

	@Override
	public void addDelayButton(Guide guide) {
		logger.debug("MainShellMock addDelayButton style:" + guide.getDelStyle() + " StartAtOffSet:" + guide.getDelStartAtOffSet() + " Target:" + guide.getDelTarget() + " Set:" + guide.getDelaySet() + " UnSet:" + guide.getDelayUnSet());
	}

	@Override
	public void addButton(Button button) {
		logger.debug("MainShellMock addButton text:" + button.getText() + " Set:" + button.getSet() + " UnSet:" + button.getUnSet());
	}

	@Override
	public void layoutButtons() {
		logger.debug("MainShellMock layoutButtons");
	}

	@Override
	public void setMetronomeBPM(int metronomeBPM, int instrument, int loops,
			int resolution, String Rhythm) {
		logger.debug("MainShellMock setMetronomeBPM metronomeBPM:" + metronomeBPM + " instrument:" + instrument + " loops:" + loops + " resolution:" + resolution + " Rhythm:" + Rhythm);
	}

	@Override
	public void displayPage(String target) {
		logger.debug("MainShellMock displayPage target:" + target);
	}

	@Override
	public void stopMetronome() {
		logger.debug("MainShellMock stopMetronome");
	}

	@Override
	public void stopAudio() {
		logger.debug("MainShellMock stopAudio");
	}

	@Override
	public void stopVideo() {
		logger.debug("MainShellMock stopVideo");
	}

	@Override
	public void stopDelay() {
		logger.debug("MainShellMock stopDelay");
	}

	@Override
	public void stopAll() {
		logger.debug("MainShellMock stopAll");
	}

}
