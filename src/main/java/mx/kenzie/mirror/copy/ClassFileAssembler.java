package mx.kenzie.mirror.copy;

import mx.kenzie.mirror.note.ShouldBe;

public interface ClassFileAssembler {
    
    void append(ClassFileAssembler $0);
    
    void append(@ShouldBe("jdk.internal.reflect.ByteVector") Object $0);
    
    short getLength();
    
    int getStack();
    
    void setStack(int $0);
    
    void emitShort(short $0, short $1);
    
    void emitShort(short $0);
    
    void emitConstantPoolUTF8(String $0);
    
    short cpi();
    
    void emitConstantPoolClass(short $0);
    
    void emitConstantPoolNameAndType(short $0, short $1);
    
    void emitConstantPoolInterfaceMethodref(short $0, short $1);
    
    void emitConstantPoolMethodref(short $0, short $1);
    
    @ShouldBe("jdk.internal.reflect.ByteVector")
    Object getData();
    
    void opc_new(short $0);
    
    void opc_dup();
    
    void opc_aload_1();
    
    void opc_ifnonnull(short $0);
    
    void opc_ifnonnull(@ShouldBe("jdk.internal.reflect.Label") Object $0);
    
    void opc_invokespecial(short $0, int $1, int $2);
    
    void opc_athrow();
    
    void opc_checkcast(short $0);
    
    void opc_aload_2();
    
    void opc_ifnull(short $0);
    
    void opc_ifnull(@ShouldBe("jdk.internal.reflect.Label") Object $0);
    
    void opc_arraylength();
    
    void opc_sipush(short $0);
    
    void opc_if_icmpeq(short $0);
    
    void opc_if_icmpeq(@ShouldBe("jdk.internal.reflect.Label") Object $0);
    
    void opc_aaload();
    
    void opc_astore_2();
    
    void opc_astore_3();
    
    void opc_aload_3();
    
    void opc_instanceof(short $0);
    
    void opc_ifeq(short $0);
    
    void opc_ifeq(@ShouldBe("jdk.internal.reflect.Label") Object $0);
    
    void opc_invokevirtual(short $0, int $1, int $2);
    
    void opc_goto(short $0);
    
    void opc_goto(@ShouldBe("jdk.internal.reflect.Label") Object $0);
    
    void opc_invokestatic(short $0, int $1, int $2);
    
    void opc_invokeinterface(short $0, int $1, byte $2, int $3);
    
    void opc_aconst_null();
    
    void opc_areturn();
    
    void opc_dup_x1();
    
    void opc_swap();
    
    short getMaxLocals();
    
    void setMaxLocals(int $0);
    
    void opc_aload_0();
    
    void opc_return();
    
    void emitInt(int $0);
    
    short getMaxStack();
    
    void opc_i2l();
    
    void opc_i2f();
    
    void opc_i2d();
    
    void opc_l2f();
    
    void opc_l2d();
    
    void opc_f2d();
    
    void opc_ldc(byte $0);
    
    void opc_iload_0();
    
    void opc_iload_1();
    
    void opc_iload_2();
    
    void opc_iload_3();
    
    void opc_lload_0();
    
    void opc_lload_1();
    
    void opc_lload_2();
    
    void opc_lload_3();
    
    void opc_fload_0();
    
    void opc_fload_1();
    
    void opc_fload_2();
    
    void opc_fload_3();
    
    void opc_dload_0();
    
    void opc_dload_1();
    
    void opc_dload_2();
    
    void opc_dload_3();
    
    void opc_astore_0();
    
    void opc_astore_1();
    
    void opc_pop();
    
    void opc_ireturn();
    
    void opc_lreturn();
    
    void opc_freturn();
    
    void opc_dreturn();
    
    void opc_getstatic(short $0, int $1);
    
    void opc_putstatic(short $0, int $1);
    
    void opc_getfield(short $0, int $1);
    
    void opc_putfield(short $0, int $1);
    
    void emitByte(byte $0);
    
    void incStack();
    
    void decStack();
    
    void emitConstantPoolFieldref(short $0, short $1);
    
    void emitConstantPoolString(short $0);
    
    void emitMagicAndVersion();
    
}
