package gg.tracer.commons.resource;

import java.io.File;

/**
 * @author Bradley Steele
 */
public abstract class AbstractResource<T extends Resource> implements Resource {

    public final ResourceReference reference;
    public final ResourceHandler<T> handler;

    public AbstractResource(ResourceReference reference, ResourceHandler<T> handler) {
        this.reference = reference;
        this.handler = handler;
    }

    public AbstractResource(File file, ResourceHandler<T> handler) {
        this(new ResourceReference(file), handler);
    }

    @Override
    public ResourceReference getReference() {
        return reference;
    }

    @Override
    public ResourceHandler<T> getHandler() {
        return handler;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void save() {
        handler.save((T) this);
    }

    public abstract Object getConfiguration();

    public abstract void setConfiguration(Object configuration);
}
