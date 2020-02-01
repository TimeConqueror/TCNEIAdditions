package ru.timeconqueror.tcneiadditions.coremod;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class MappingThreadTransformer extends SimpleClassTransformer {
    @Override
    public void init() {
        addTransformer("thaumcraft.client.gui.MappingThread", this::transformMappingThread);
    }

    private void transformMappingThread(ClassNode clazz, boolean isObfuscated) {
        final String name = "run";
        final String descriptor = "()V";

        MethodNode runMethod = null;
        for (MethodNode method : clazz.methods) {
            if (method.name.equals(name) && method.desc.equals(descriptor)) {
                runMethod = method;
                break;
            }
        }

        if (runMethod == null)
            throw new NullPointerException("Can't find " + name + descriptor + " method at thaumcraft.client.gui.MappingThread");


        transformMethodHead(runMethod);
        transformWhileLoopStart(runMethod);
        transformRunMethodTail(runMethod);
    }

    private void transformMethodHead(MethodNode runMethod) {
        /*  methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, "thaumcraft/client/gui/MappingThread", "idMappings", "Ljava/util/Map;");
            methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "size", "()I", true);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "ru/timeconqueror/tcneiadditions/client/ThaumcraftHooks", "setTotalToLoad", "(I)V", false);
         */
        InsnList insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList.add(new FieldInsnNode(Opcodes.GETFIELD, "thaumcraft/client/gui/MappingThread", "idMappings", "Ljava/util/Map;"));
        insnList.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/Map", "size", "()I", true));
        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/timeconqueror/tcneiadditions/client/ThaumcraftHooks", "setTotalToLoad", "(I)V", false));

        runMethod.instructions.insert(insnList);
    }

    private void transformWhileLoopStart(MethodNode runMethod) {
        /*  methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
            Label label5 = new Label();
            methodVisitor.visitJumpInsn(IFEQ, label5);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
            methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            methodVisitor.visitVarInsn(ASTORE, 2);
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(28, label0);
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            */
        ListIterator<AbstractInsnNode> iterator = runMethod.instructions.iterator();
        AbstractInsnNode whileLoopStart = null;
        while (iterator.hasNext()) {
            AbstractInsnNode first = iterator.next();
            if (first.getOpcode() == Opcodes.ALOAD) {
                AbstractInsnNode second = first.getNext();
                if (second.getOpcode() == Opcodes.INVOKEINTERFACE && ((MethodInsnNode) second).owner.equals("java/util/Iterator") && ((MethodInsnNode) second).name.equals("hasNext")) {
                    AbstractInsnNode third = second.getNext();
                    if (third.getOpcode() == Opcodes.IFEQ) {
                        AbstractInsnNode fourth = third.getNext();
                        if (fourth.getOpcode() == Opcodes.ALOAD) {
                            AbstractInsnNode fifth = fourth.getNext();
                            if (fifth.getOpcode() == Opcodes.INVOKEINTERFACE && ((MethodInsnNode) fifth).owner.equals("java/util/Iterator") && ((MethodInsnNode) fifth).name.equals("next")) {
                                AbstractInsnNode six = fifth.getNext();
                                if (six.getOpcode() == Opcodes.CHECKCAST && ((TypeInsnNode) six).desc.equals("java/lang/Integer")) {
                                    AbstractInsnNode seven = six.getNext();
                                    if (seven.getOpcode() == Opcodes.ASTORE && ((VarInsnNode) seven).var == 2) {
                                        whileLoopStart = seven;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        if (whileLoopStart == null)
            throw new NullPointerException("Can't find node at the start of root while-loop at thaumcraft.client.gui.MappingThread#" + runMethod.name + runMethod.desc);

        //methodVisitor.visitMethodInsn(INVOKESTATIC, "ru/timeconqueror/tcneiadditions/client/ThaumcraftHooks", "incrementLoadedObjects", "()V", false);
        runMethod.instructions.insert(whileLoopStart, new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/timeconqueror/tcneiadditions/client/ThaumcraftHooks", "incrementLoadedObjects", "()V", false));
    }

    private void transformRunMethodTail(MethodNode runMethod) {
        ListIterator<AbstractInsnNode> iterator = runMethod.instructions.iterator();

        AbstractInsnNode returnNode = null;
        while (iterator.hasNext()) {
            AbstractInsnNode node = iterator.next();
            if (node.getOpcode() == Opcodes.RETURN) {
                returnNode = node;
                break;
            }
        }

        if (returnNode == null)
            throw new NullPointerException("Can't find return opcode at thaumcraft.client.gui.MappingThread#" + runMethod.name + runMethod.desc);

        /*
         * methodVisitor.visitMethodInsn(INVOKESTATIC, "ru/timeconqueror/tcneiadditions/client/ThaumcraftHooks", "onItemStackAspectDataLoaded", "()V", false);
         */
        MethodInsnNode hookNode = new MethodInsnNode(Opcodes.INVOKESTATIC, "ru/timeconqueror/tcneiadditions/client/ThaumcraftHooks", "onItemStackAspectDataLoaded", "()V", false);
        runMethod.instructions.insertBefore(returnNode, hookNode);
    }

    private AbstractInsnNode nextWithSkip(AbstractInsnNode node) {
        while (node.getNext() != null) {
            AbstractInsnNode next = node.getNext();
            if (next instanceof LineNumberNode || next instanceof LabelNode) {
                node = next;
            } else return next;
        }

        return node;
    }
}
