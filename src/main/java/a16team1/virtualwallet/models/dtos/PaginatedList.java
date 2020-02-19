package a16team1.virtualwallet.models.dtos;

public class PaginatedList {

    private int beginIndex;

    private int endIndex;

    private int page;

    private int pageSize;

    private int totalPages;

    public PaginatedList() {
    }

    public PaginatedList(int beginIndex, int endIndex, int page, int pageSize, int totalPages) {
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
    }

    public int getBeginIndex() {
        return beginIndex;
    }

    public void setBeginIndex(int beginIndex) {
        this.beginIndex = beginIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
