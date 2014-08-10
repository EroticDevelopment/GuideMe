package org.guideme.guideme.project;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.swing.Icon;
import org.guideme.guideme.Constants;
import org.guideme.guideme.model.Guide;
import org.guideme.guideme.resources.Icons;
import org.guideme.guideme.serialization.GuideSerializer;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

public class GuideProject implements Project {

    private final FileObject projectDirectory;
    private final Guide guide;

    private Lookup lookup;
    
    public GuideProject(FileObject projectDirectory) {
        this.projectDirectory = projectDirectory;
        
        this.guide = parseGuide(projectDirectory.getFileObject(Constants.GUIDE_FILE));
    }

    private Guide parseGuide(FileObject guideFile) {
        try {
            return GuideSerializer.getDefault().ReadGuide(guideFile.getInputStream());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }
    
    public Guide getGuide() {
        return guide;
    }
    
    public String getGuideName() {
        return guide.getTitle() != null ? guide.getTitle() : projectDirectory.getName();
    }
    
    @Override
    public FileObject getProjectDirectory() {
        return projectDirectory;
    }
    
    @Override
    public Lookup getLookup() {
        if (lookup == null) {
            lookup = Lookups.fixed(new Object[] {
                this,
                new Info()
            });
        }
        return lookup;
    }

    
    private final class Info implements ProjectInformation {

        @Override
        public String getName() {
            return getProjectDirectory().getName();
        }

        @Override
        public String getDisplayName() {
            return getGuideName();
        }

        @Override
        public Icon getIcon() {
            return Icons.getGuideIcon();
        }

        @Override
        public Project getProject() {
            return GuideProject.this;
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            // Not implemented yet.
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) {
            // Not implemented yet.
        }
        
    }
}
