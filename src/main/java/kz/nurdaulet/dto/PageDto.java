package kz.nurdaulet.dto;

import java.util.Collections;
import java.util.List;

public class PageDto<T> {
    private final List<T> content;
    private final int currentPage;
    private final int totalPages;
    private final int pageSize;
    private final int totalItems;

    private PageDto(List<T> content, int currentPage, int totalPages, int pageSize, int totalItems) {
        this.content = content;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
    }

    public static <T> PageDto<T> of(List<T> items, int requestedPage, int pageSize) {
        int totalItems = items == null ? 0 : items.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalItems / pageSize));
        int currentPage = Math.min(Math.max(requestedPage, 1), totalPages);

        if (totalItems == 0) {
            return new PageDto<>(Collections.emptyList(), 1, 1, pageSize, 0);
        }

        int fromIndex = (currentPage - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalItems);

        return new PageDto<>(items.subList(fromIndex, toIndex), currentPage, totalPages, pageSize, totalItems);
    }

    public List<T> getContent() {
        return content;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public boolean isHasPrevious() {
        return currentPage > 1;
    }

    public boolean isHasNext() {
        return currentPage < totalPages;
    }
}
