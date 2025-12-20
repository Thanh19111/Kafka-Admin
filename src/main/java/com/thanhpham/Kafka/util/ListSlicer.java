package com.thanhpham.Kafka.util;

import java.util.List;

public class ListSlicer {
    public static <T> List<T> slice(List<T> list, int page, int pageSize) {
        int fromIndex = page * pageSize;

        if (fromIndex >= list.size()) {
            return List.of();
        }

        int toIndex = Math.min(fromIndex + pageSize, list.size());
        return list.subList(fromIndex, toIndex);
    }
}
