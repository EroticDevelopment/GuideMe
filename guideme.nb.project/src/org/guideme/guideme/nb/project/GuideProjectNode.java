package org.guideme.guideme.nb.project;

import java.awt.Image;
import java.util.List;
import javax.swing.Action;
import org.guideme.guideme.nb.project.resources.Icons;
import org.openide.filesystems.FileObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Utilities;

class GuideProjectNode extends AbstractNode {

    private final FileObject guideDirectory;

    public GuideProjectNode(FileObject guideDirectory) {
        super(Children.LEAF);
        this.guideDirectory = guideDirectory;
    }

    @Override
    public Image getIcon(int type) {
        return Icons.getGuideImage();
    }

    @Override
    public Image getOpenedIcon(int type) {
        return Icons.getGuideImage();
    }
    
    @Override
    public String getName() {
        return guideDirectory.getName();
    }
    
    @Override
    public String getDisplayName() {
        // TODO extract from guide.xml.
        return getName();
    }
    
            
        @Override
        public Action[] getActions(boolean context) {
            return new Action[]{};
//            List<? extends Action> actions = Utilities.actionsForPath("Actions/Guide");
//            return actions.toArray(new Action[actions.size()]);
        }
}
