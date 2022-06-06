package gg.tracer.commons.resource;

/**
 * @author Bradley Steele
 */
public interface Resource extends ResourceSection {

    ResourceReference getReference();

    ResourceHandler getHandler();

    void save();

}
