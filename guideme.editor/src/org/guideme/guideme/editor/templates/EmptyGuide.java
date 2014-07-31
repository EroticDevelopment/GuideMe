package org.guideme.guideme.editor.templates;

import java.io.IOException;
import java.io.OutputStream;
import org.guideme.guideme.Constants;
import org.guideme.guideme.model.Guide;
import org.guideme.guideme.serialization.GuideSerializer;
import org.openide.filesystems.FileObject;

public class EmptyGuide {

    public static void create(FileObject projectDirectory, String title) throws IOException {
        
        Guide guide = new Guide();
        guide.setTitle(title);
        try (OutputStream stream = projectDirectory.createAndOpen(Constants.GUIDE_FILE)) {
            GuideSerializer.getDefault().WriteGuide(guide, stream);
        }
        
        projectDirectory.createFolder(Constants.GM_DIR);
        projectDirectory.createFolder(Constants.IMAGES_DIR);
    }
}
