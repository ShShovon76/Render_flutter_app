package com.example.job_portal_backend.dtos.searchAndPaginaton;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> items;
    private int page;
    private int size;
    private long totalItems;
    private int totalPages;

    public static <T> PageResponse<T> of(List<T> items, int page, int size, long totalItems) {
        return PageResponse.<T>builder()
                .items(items)
                .page(page)
                .size(size)
                .totalItems(totalItems)
                .totalPages((int) Math.ceil((double) totalItems / size))
                .build();
    }
}
