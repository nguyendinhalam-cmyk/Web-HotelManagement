package com.hotel.util;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Sinh mã nghiệp vụ dạng PREFIX001. Dùng cho ID VARCHAR(20). */
public final class MaCodeGenerator {
    private MaCodeGenerator() {}

    public static String nextId(List<?> entities, String fieldName, String prefix) {
        return nextId(entities, fieldName, prefix, Set.of());
    }

    public static String nextId(List<?> entities, String fieldName, String prefix, Set<String> reserved) {
        int max = 0;
        if (entities != null) {
            for (Object entity : entities) {
                String id = readId(entity, fieldName);
                if (id == null || !id.startsWith(prefix)) continue;
                String number = id.substring(prefix.length());
                try { max = Math.max(max, Integer.parseInt(number)); } catch (NumberFormatException ignored) { }
            }
        }
        Set<String> used = new HashSet<>(reserved);
        String candidate;
        do { candidate = prefix + String.format("%03d", ++max); } while (used.contains(candidate));
        if (candidate.length() > 20) throw new IllegalStateException("Không thể sinh mã trong giới hạn 20 ký tự.");
        return candidate;
    }

    private static String readId(Object entity, String fieldName) {
        try {
            Field f = entity.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            Object value = f.get(entity);
            return value == null ? null : value.toString();
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Không tìm thấy field mã: " + fieldName, e);
        }
    }
}
