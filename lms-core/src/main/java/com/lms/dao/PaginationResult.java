package com.lms.dao;

import java.util.List;

public class PaginationResult<T> {
    private final List<T> data;
    private final int totalRecords;
    private final int currentPage;
    private final int pageSize;
    private final int totalPages;
    
    public PaginationResult(List<T> data, int totalRecords, int currentPage, int pageSize) {
        this.data = data;
        this.totalRecords = totalRecords;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) totalRecords / pageSize);
    }
    
    // Getters
    public List<T> getData() { return data; }
    public int getTotalRecords() { return totalRecords; }
    public int getCurrentPage() { return currentPage; }
    public int getPageSize() { return pageSize; }
    public int getTotalPages() { return totalPages; }
    
    // Utility methods
    public boolean hasNextPage() { return currentPage < totalPages; }
    public boolean hasPreviousPage() { return currentPage > 1; }
    public int getStartRecord() { return (currentPage - 1) * pageSize + 1; }
    public int getEndRecord() { return Math.min(currentPage * pageSize, totalRecords); }
}
