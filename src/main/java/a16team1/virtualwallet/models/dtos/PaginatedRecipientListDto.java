package a16team1.virtualwallet.models.dtos;

import java.util.List;

public class PaginatedRecipientListDto extends PaginatedList {

    private List<RecipientDto> list;

    public PaginatedRecipientListDto() {
    }

    public List<RecipientDto> getList() {
        return list;
    }

    public void setList(List<RecipientDto> list) {
        this.list = list;
    }
}
