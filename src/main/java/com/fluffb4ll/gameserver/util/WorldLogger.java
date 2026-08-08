package com.fluffb4ll.gameserver.util;

public class WorldLogger {

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String PURPLE = "\u001B[35m";

    public static void logChunkProcessing(String chunkCoord, int mutantsCount, int anomaliesCount) {
        String thread = Thread.currentThread().getName();
        System.out.printf("%s[%s]%s Чанк %s -> Мутантов: %d, Аномалий: %d%n",
                PURPLE, thread, RESET, chunkCoord, mutantsCount, anomaliesCount);
    }

    public static void logEntityMove(String entityId, float x, float y) {
        String thread = Thread.currentThread().getName();
        System.out.printf("%s[%s]%s [MOVE] %s -> Pos: (%.2f, %.2f)%n",
                GREEN, thread, RESET, entityId, x, y);
    }

    public static void logChunkMigration(String entityId, String fromChunk, String toChunk) {
        String thread = Thread.currentThread().getName();
        System.out.printf("%s[%s]%s [MIGRATION] %s перешел из чанка %s в %s%n",
                YELLOW, thread, RESET, entityId, fromChunk, toChunk);
    }

    public static void logAnomalyHit(String entityId, String anomalyType, float hpLeft) {
        String thread = Thread.currentThread().getName();
        System.out.printf("%s[%s]%s [DAMAGE] %s попал в %s! Осталось HP: %.1f%n",
                RED, thread, RESET, entityId, anomalyType, hpLeft);
    }

    public static void logTickHeader(long tickCount) {
        String thread = Thread.currentThread().getName();
        System.out.printf("%s[%s]%s Тик №%s%n",
                PURPLE, thread, RESET, tickCount);
    }
}