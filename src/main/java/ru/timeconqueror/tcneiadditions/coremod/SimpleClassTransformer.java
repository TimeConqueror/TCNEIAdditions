package ru.timeconqueror.tcneiadditions.coremod;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashMap;
import java.util.Map;

public abstract class SimpleClassTransformer implements IClassTransformer {
    public static final Map<String, IClassTransformFunction> CLASS_TRANSFORMERS = new HashMap<>();

    public SimpleClassTransformer() {
        init();
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        IClassTransformFunction transformer = CLASS_TRANSFORMERS.get(transformedName);
        if (transformer == null) return basicClass;

        boolean isObfuscated = !name.equals(transformedName);

        ClassNode node = new ClassNode();
        ClassReader reader = new ClassReader(basicClass);
        reader.accept(node, 0);

        transformer.transform(node, isObfuscated);

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        node.accept(writer);
        return writer.toByteArray();
    }

    public abstract void init();

    protected void addTransformer(String className, IClassTransformFunction transformer) {
        CLASS_TRANSFORMERS.put(className, transformer);
    }
}
