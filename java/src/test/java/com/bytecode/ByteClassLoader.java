package com.bytecode;

import java.util.HashMap;
import java.util.Map;

public class ByteClassLoader extends ClassLoader {
    private final Map<String, byte[]> extraClassDefs;

    public ByteClassLoader(ClassLoader parent, Map<String, byte[]> extraClassDefs) {
        super(parent);
        this.extraClassDefs = extraClassDefs;
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        System.out.println("findclass name " + name);
        byte[] classBytes = this.extraClassDefs.remove(name);
        if (classBytes != null) {
            return defineClass(name, classBytes, 0, classBytes.length);
        }
        return super.findClass(name);
    }

}
