package com.fluffb4ll.gameserver.model;

/** Класс-хранилище опкодов, используемых в пакетах, обмениваемых между сервером и клиентом. */
public final class PacketOpcodes {
    private PacketOpcodes() {}

    // опкоды client-to-server пакетов
    public static final byte OP_C2S_AUTH = 0x00;
    public static final byte OP_C2S_MOVE = 0x01;
    public static final byte OP_C2S_ATTACK = 0x02;

    // опкоды server-to-client пакетов
    public static final byte OP_S2C_WORLD_SNAPSHOT = 0x10;
    public static final byte OP_S2C_ENTITY_DEATH = 0x11;

    // опкоды типов сущностей
    public static final byte OP_ENTITY_TYPE_PLAYER = 0x00;
    public static final byte OP_ENTITY_TYPE_MUTANT = 0x01;
    public static final byte OP_ENTITY_TYPE_ANOMALY = 0x02;

    // опкоды типов мутантов
    public static final byte OP_MUTANT_TYPE_BLIND_DOG = 0x00;
    public static final byte OP_MUTANT_TYPE_FLESH = 0x01;
    public static final byte OP_MUTANT_TYPE_BLOODSUCKER = 0x02;

    // опкоды типов аномалий
    public static final byte OP_ANOMALY_TYPE_ZHARKA = 0x00;
    public static final byte OP_ANOMALY_TYPE_ELECTRO = 0x01;
    public static final byte OP_ANOMALY_TYPE_GAS_CLOUD = 0x02;
    public static final byte OP_ANOMALY_TYPE_VORTEX = 0x03;
}
