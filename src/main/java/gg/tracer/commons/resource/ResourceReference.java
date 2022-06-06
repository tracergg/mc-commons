package gg.tracer.commons.resource;

import com.google.common.io.Files;

import java.io.File;

/**
 * @author Bradley Steele
 */
public class ResourceReference {

    public final String parent;
    public final String child;
    public final String ext;
    public final String path;
    public final File file;

    public ResourceReference(String parent, String child, String ext) {
        if (parent == null) {
            parent = "";
        }

        this.parent = parent;
        this.child = child;
        this.ext = ext.replaceAll("^\\.", "");
        this.path = parent + (!parent.isEmpty() ? File.separatorChar : "") + child;
        this.file = new File(path);
    }

    public ResourceReference(String child, String ext) {
        this(null, child, ext);
    }

    public ResourceReference(String child) {
        this(child, Files.getFileExtension(child));
    }

    public ResourceReference(File file) {
        this(file.getParent(), file.getName(), Files.getFileExtension(file.getName()));
    }

    @Override
    public String toString() {
        return "ResourceReference{parent='" + parent + "', child='" + child + "', ext='" + ext + "'}";
    }
}
