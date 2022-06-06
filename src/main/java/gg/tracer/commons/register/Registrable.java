package gg.tracer.commons.register;

/**
 * @author Bradley Steele
 */
public interface Registrable {

    // internal

    void internalRegister();

    void internalUnregister();


    // api

    default void register() {}

    default void unregister() {}
}
