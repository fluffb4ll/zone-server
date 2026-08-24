package com.fluffb4ll.gameserver.model.enums;

import com.fluffb4ll.gameserver.model.PacketOpcodes;

public enum AnomalyType {
    ZHARKA(PacketOpcodes.OP_ANOMALY_TYPE_ZHARKA, "Zharka", 9999, 0f, 10,
            AnomalyElementalType.THERMAL, 5.0f, 0.5f,0f),
    ELECTRO(PacketOpcodes.OP_ANOMALY_TYPE_ELECTRO, "Electro", 9999, 0f, 20,
            AnomalyElementalType.ELECTRICAL, 8.0f, 2f, 0f),
    GAS_CLOUD(PacketOpcodes.OP_ANOMALY_TYPE_GAS_CLOUD, "Gas Cloud", 9999, 0f, 15,
            AnomalyElementalType.CHEMICAL, 4.0f, 0f, 0f),
    VORTEX(PacketOpcodes.OP_ANOMALY_TYPE_VORTEX, "Vortex", 9999, 0f, 50,
            AnomalyElementalType.GRAVITATIONAL, 5.0f, 3f, 1f);

    private final byte opcode;
    private final String displayName;
    private final int maxHealth;
    private final float speed;
    private final int baseDamage;
    private final AnomalyElementalType elementalType;
    private final float radius;
    private final float cooldownTime;
    private final float chargeTime;

    AnomalyType(byte opcode,
                String displayName,
                int maxHealth,
                float speed,
                int baseDamage,
                AnomalyElementalType elementalType,
                float radius,
                float cooldownTime,
                float chargeTime) {
        this.opcode = opcode;
        this.displayName = displayName;
        this.maxHealth = maxHealth;
        this.speed = speed;
        this.baseDamage = baseDamage;
        this.elementalType = elementalType;
        this.radius = radius;
        this.cooldownTime = cooldownTime;
        this.chargeTime = chargeTime;
    }


    public String getDisplayName() {
        return displayName;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public float getSpeed() {
        return speed;
    }

    public int getBaseDamage() {
        return baseDamage;
    }

    public AnomalyElementalType getElementalType() {
        return elementalType;
    }

    public float getRadius() {
        return radius;
    }

    public float getCooldownTime() {
        return cooldownTime;
    }

    public float getChargeTime() {
        return chargeTime;
    }

    public byte getOpcode() {
        return opcode;
    }
}
