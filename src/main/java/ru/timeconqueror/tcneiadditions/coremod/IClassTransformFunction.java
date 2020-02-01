package ru.timeconqueror.tcneiadditions.coremod;

import org.objectweb.asm.tree.ClassNode;

public interface IClassTransformFunction {
    void transform(ClassNode clazz, boolean isObfuscated);
}
