package com.fluffb4ll.gameserver.model.terrains;

import com.fluffb4ll.gameserver.engine.EventBus;
import com.fluffb4ll.gameserver.model.entities.LivingEntity;
import com.fluffb4ll.gameserver.model.records.EntityDeathEvent;
import com.fluffb4ll.gameserver.util.Vector2D;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SpawnerTerrain extends Terrain {
    private final EventBus eventBus;
    private final float spawnCooldown;
    private final int entityLimit;

    private final Map<String, LivingEntity> entities = new ConcurrentHashMap<>();

    private float timer;

    public SpawnerTerrain(String id,
                          String displayName,
                          Vector2D position,
                          float radius,
                          float spawnCooldown,
                          int entityLimit,
                          EventBus eventBus) {
        super(id, displayName, position, radius);
        timer = this.spawnCooldown = spawnCooldown;
        this.entityLimit = entityLimit;

        this.eventBus = eventBus;
        eventBus.subscribe(EntityDeathEvent.class, this::onEntityDeath);
    }

    public float getSpawnCooldown() {
        return spawnCooldown;
    }

    public int getEntityLimit() {
        return entityLimit;
    }

    public float getTimer() {
        return timer;
    }

    public void setTimer(float time) {
        timer = time;
    }

    public List<LivingEntity> getEntities() {
        return List.copyOf(entities.values());
    }

    public boolean addEntity(LivingEntity entity) {
        return entities.put(entity.getId(), entity) == null;
    }

    public boolean removeEntity(LivingEntity entity) {
        return entities.remove(entity.getId()) != null;
    }

    public void onEntityDeath(EntityDeathEvent event) {
        entities.remove(event.id());
    }

    public void update(float deltaTime) {
        if (entities.size() >= entityLimit)
            return;

        timer -= deltaTime;
        if (timer <= 0f)
            spawn();
    }

    protected void spawn() {}
}
