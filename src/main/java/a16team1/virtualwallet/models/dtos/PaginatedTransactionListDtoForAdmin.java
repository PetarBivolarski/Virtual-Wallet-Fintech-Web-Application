package a16team1.virtualwallet.models.dtos;

import java.sql.Date;
import java.util.List;

public class PaginatedTransactionListDtoForAdmin extends PaginatedList {

    private List<PresentableTransactionDto> list;

    private List<String> sortCriteria;

    private Date startDate;

    private Date endDate;

    public PaginatedTransactionListDtoForAdmin() {
    }

    public List<PresentableTransactionDto> getList() {
        return list;
    }

    public void setList(List<PresentableTransactionDto> list) {
        this.list = list;
    }

    public List<String> getSortCriteria() {
        return sortCriteria;
    }

    public void setSortCriteria(List<String> sortCriteria) {
        this.sortCriteria = sortCriteria;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

}
