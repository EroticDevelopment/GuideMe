package org.guideme.guideme.player;

import org.guideme.guideme.model.Button;
import org.guideme.guideme.model.Page;

/**
 * Player specific wrapper around a page to keep the model clean.
 */
public class PageDecorator {

    private final Page page;

    public PageDecorator(Page page) {
        this.page = page;
    }

    public Page getPage() {
        return page;
    }

    public boolean hasAvailableButton() {
        return !page.getButtons().isEmpty();
    }

    public Button getAvailableButton() {
        // Quick-n-Dirty to make it work.
        return hasAvailableButton() ? page.getButtons().get(0) : null;
    }
}
