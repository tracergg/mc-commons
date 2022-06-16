package gg.tracer.commons.util;

import gg.tracer.commons.logging.StaticLog;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * The {@link Reflection} class contains safe helper methods
 * for manipulation of java's reflection. These methods are
 * considered safe as they catch exceptions and return null
 * in most cases.
 *
 * @author Bradley Steele
 */
public final class Reflection {

    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[] {};
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[] {};

    private Reflection() {}

    // package

    /**
     * Returns the {@link ClassLoader} for a class.
     *
     * @param clazz the class
     * @return the class loader of the provided class or null
     */
    public static ClassLoader getClassLoader(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        ClassLoader loader = clazz.getClassLoader();

        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();

            while (loader != null && loader.getParent() != null) {
                loader = loader.getParent();
            }
        }

        return loader;
    }

    /**
     * Returns the absolute file path to the provided class.
     *
     * @param clazz the class
     * @return the class' file path
     */
    public static String getClassFileLocation(Class<?> clazz) {
        ClassLoader loader = getClassLoader(clazz);

        if (loader != null) {
            String name = clazz.getCanonicalName().replace(".", "/");
            URL resource = loader.getResource(name + ".class");

            if (resource != null) {
                return resource.getFile();
            }
        }

        return null;
    }

    /**
     * Returns the absolute file path of the jar file that
     * contains the provided class.
     *
     * @param clazz the class
     * @return the jar location containing the class
     */
    public static String getClassJarLocation(Class<?> clazz) {
        try {
            return clazz.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        } catch (URISyntaxException e) {
            return null;
        } catch (SecurityException e) {
            // missing permission: getProtectionDomain
            URL resource = getClassLoader(clazz).getResource("");

            if (resource == null) {
                return null;
            }

            return resource.getPath()
                    .replaceAll("^file:/", "")
                    .replaceAll("!/(.*)$", "");
        }
    }

    /**
     * Returns a {@link Set} of classes of a specific type that are
     * within a package.
     *
     * @param paths an array containing paths of where to look
     * @param packageName the containing package name
     * @param type the type of classes
     * @return the classes within a package
     */
    public static <T> Set<Class<? extends T>> getClassesFromPackage(String[] paths, String packageName, Class<T> type) {
        Set<Class<? extends T>> classes = new HashSet<>();
        String packagePrefix = packageName.replace(".", "/");

        for (String path : paths) {
            if (path.endsWith(".jar")) {
                try {
                    JarInputStream stream = new JarInputStream(new FileInputStream(path));
                    JarEntry jarEntry;

                    do {
                        jarEntry = stream.getNextJarEntry();

                        if (jarEntry == null) {
                            break;
                        }

                        String name = jarEntry.getName();

                        if (name.startsWith(packagePrefix) && name.endsWith(".class")) {
                            Class<? extends T> clazz;

                            try {
                                clazz = getClassFromPackagePath(name, type);
                            } catch (Throwable e) {
                                continue;
                            }

                            if (clazz != null) {
                                classes.add(clazz);
                            }
                        }
                    } while (true);
                } catch (Exception e) {
                    StaticLog.exception(e);
                }
            } else {
                File[] files;

                if (path.endsWith(".class")) {
                    files = new File[] { new File(path) };
                } else {
                    File base = new File(path + File.separatorChar + packagePrefix);
                    files = base.listFiles();
                }

                if (files == null) {
                    continue;
                }

                for (File file : files) {
                    String name = file.getName();

                    if (name.endsWith(".class")) {
                        Class<? extends T> clazz;

                        try {
                            clazz = getClassFromPackagePath(packageName + '.' + name, type);
                        } catch (Throwable t) {
                            continue;
                        }

                        if (clazz != null) {
                            classes.add(clazz);
                        }
                    }
                }
            }
        }

        return classes;
    }

    public static <T> Set<Class<? extends T>> getClassesFromPackage(String packageName, Class<T> type) {
        return getClassesFromPackage(System.getProperty("java.class.path").split(System.getProperty("path.separator")), packageName, type);
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<? extends T> getClassFromPackagePath(String name, Class<T> type) throws ClassNotFoundException {
        String classPath = name.replaceAll("\\.class$", "")
                .replace("/", ".");

        Class<?> clazz = Class.forName(classPath);

        if (type.isAssignableFrom(clazz)) {
            return (Class<? extends T>) clazz;
        }

        return null;
    }


    // class

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClass(String name, boolean initialize, ClassLoader loader) {
        Class<T> clazz = null;

        try {
            if (loader == null) {
                clazz = (Class<T>) Class.forName(name);
            } else {
                clazz = (Class<T>) Class.forName(name, initialize, loader);
            }
        } catch (Exception e) {
            // ignored
        }

        return clazz;
    }

    public static <T> Class<T> getClass(String name, ClassLoader loader) {
        return getClass(name, true, loader);
    }

    public static <T> Class<T> getClass(String name) {
        return getClass(name, null);
    }


    // instance

    public static <T> T newInstance(Class<T> clazz, Class<?>[] parameterTypes, Object... args) {
        T instance = null;

        try {
            Constructor<T> constructor = getConstructor(clazz, parameterTypes);
            instance = constructor.newInstance(args);
        } catch (Exception e) {
            // ignored
        }

        return instance;
    }

    public static <T> T newInstance(Class<T> clazz, Object... args) {
        return newInstance(clazz, EMPTY_CLASS_ARRAY, args);
    }

    public static <T> T newInstance(Class<T> clazz) {
        return newInstance(clazz, EMPTY_OBJECT_ARRAY);
    }


    // constructor

    public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... parameterTypes) {
        Constructor<T> constructor = null;

        try {
            constructor = clazz.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
        } catch (Exception e) {
            // ignored
        }

        return constructor;
    }

    public static <T> Constructor<T> getConstructor(Class<T> clazz) {
        return getConstructor(clazz, EMPTY_CLASS_ARRAY);
    }


    // method

    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Object object, Method method, Object... args) {
        if (method == null) {
            return null;
        }

        T result = null;

        try {
            result = (T) method.invoke(object, args);
        } catch (Exception e) {
            // ignored
        }

        return result;
    }

    public static <T> T invokeMethod(Object object, Method method) {
        return invokeMethod(object, method, EMPTY_OBJECT_ARRAY);
    }

    public static <T> T invokeMethod(Object object, String methodName, Object... args) {
        return invokeMethod(object, getMethod(object.getClass(), methodName), args);
    }

    public static <T> T invokeMethod(Object object, String methodName) {
        return invokeMethod(object, getMethod(object.getClass(), methodName));
    }

    private static final String GET1 = "get";
    private static final String GET2 = "getInstance";

    public static boolean isSingleton(Class<?> clazz) {
        return hasMethod(clazz, GET1) || hasMethod(clazz, GET2);
    }

    public static <T> T getSingleton(Class<T> clazz) {
        if (!isSingleton(clazz)) {
            return null;
        }

        Method get = getMethod(clazz, GET1);

        if (get == null) {
            get = getMethod(clazz, GET2);
        }

        return invokeMethod(null, get);
    }

    public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        Method method = null;
        Class<?> c = clazz;

        while (true) {
            if (c == null || c == Object.class) {
                break;
            }

            try {
                method = c.getDeclaredMethod(name, parameterTypes);
                setAccessible(method, true);
                break;
            } catch (Exception e) {
                // ignore
            }

            c = c.getSuperclass();
        }

        return method;
    }

    public static Method getMethod(Class<?> clazz, String name) {
        return getMethod(clazz, name, EMPTY_CLASS_ARRAY);
    }

    public static boolean hasMethod(Object object, String name, Class<?>... parameterTypes) {
        if (object == null) {
            return false;
        }

        Class<?> clazz = object.getClass();
        Method method = getMethod(clazz, name, parameterTypes);

        if (method == null) {
            return false;
        }

        return method.getDeclaringClass().equals(clazz);
    }

    public static boolean hasMethod(Object object, String name) {
        return hasMethod(object, name, EMPTY_CLASS_ARRAY);
    }

    public static boolean hasMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        return getMethod(clazz, name, parameterTypes) != null;
    }

    public static boolean hasMethod(Class<?> clazz, String name) {
        return hasMethod(clazz, name, EMPTY_CLASS_ARRAY);
    }


    // field

    public static boolean hasField(Class<?> clazz, String name) {
        return getField(clazz, name) != null;
    }

    public static Field getField(Class<?> clazz, String name) {
        Field field = null;
        Class<?> c = clazz;

        while (true) {
            if (c == null || c == Object.class) {
                break;
            }

            try {
                field = c.getDeclaredField(name);
                setAccessible(field, true);
                break;
            } catch (Exception e) {
                // ignore
            }

            c = c.getSuperclass();
        }

        return field;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Field field, Object object) {
        if (field == null) {
            return null;
        }

        setAccessible(field, true);

        T value = null;

        try {
            value = (T) field.get(object);
        } catch (Exception e) {
            // ignored
        }

        return value;
    }

    public static <T> T getFieldValue(Class<?> clazz, String name, Object object) {
        return getFieldValue(getField(clazz, name), object);
    }

    public static <T> T getFieldValue(Class<?> clazz, String name, Object object, T fallback) {
        T value = getFieldValue(clazz, name, object);
        return value != null ? value : fallback;
    }


    // setters

    public static void setAccessible(Field field, boolean accessible) {
        try {
            field.setAccessible(accessible);
        } catch (Exception e) {
            // ignored
        }
    }

    public static void setAccessible(Method method, boolean accessible) {
        try {
            method.setAccessible(accessible);
        } catch (Exception e) {
            // ignored
        }
    }

    public static void setFieldValue(Object object, Field field, Object value) {
        if (field == null) {
            return;
        }

        setAccessible(field, true);

        try {
            field.set(object, value);
        } catch (Exception e) {
            // ignored
        }
    }

    public static void setFieldValue(Object object, String fieldName, Object value) {
        if (fieldName == null) {
            return;
        }

        setFieldValue(object, getField(object.getClass(), fieldName), value);
    }

    // enum

    public static <T extends Enum<T>> T matchEnum(Class<T> clazz, String str, T fallback) {
        if (str == null) {
            return fallback;
        }

        try {
            return Enum.valueOf(clazz, str);
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }

    public static <T extends Enum<T>> T matchEnum(Class<T> clazz, String str) {
        return matchEnum(clazz, str, null);
    }
}
