package org.guideme.guideme.model.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.tools.FileObject;
import org.guideme.guideme.model.Guide;

public class GuideSerializer {

    private static GuideSerializer defaultSerializer = new XmlFileGuideSerializer();
    
    public static GuideSerializer getDefault() {
        return defaultSerializer;
    }
    
    public static void setSerializer(GuideSerializer serializer) {
        defaultSerializer = serializer;
    }
    
    public Guide ReadGuide(InputStream inputStream) throws IOException {
        return defaultSerializer.ReadGuide(inputStream);
    }
    
    public void WriteGuide(Guide guide, OutputStream outputStream) throws IOException {
        defaultSerializer.WriteGuide(guide, outputStream);
    }
}
