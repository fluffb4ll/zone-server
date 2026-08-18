package com.fluffb4ll.gameserver.util;

import java.nio.ByteBuffer;
import java.util.UUID;

public class ByteParser {
    public static UUID parseUUID(ByteBuffer buffer) {
        try {
            StringBuilder sb = new StringBuilder();
            int cachePos = buffer.position();
            for (int i = cachePos; i < cachePos + 36; i++)
                sb.append((char) buffer.get());

            return UUID.fromString(sb.toString());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        }

    }
}
