package a16team1.virtualwallet.services;

import a16team1.virtualwallet.models.dtos.*;
import a16team1.virtualwallet.utilities.TransactionDirection;

import java.sql.Date;
import java.util.List;

public interface DtoListsMediatorService {
    PaginatedTransactionListDto getPresentableTransactionsWithPagination(Date startDate,
                                                                         Date endDate,
                                                                         String loggedUserUsername,
                                                                         String counterpartyUsername,
                                                                         TransactionDirection direction,
                                                                         String amount,
                                                                         String date,
                                                                         int page,
                                                                         int pageSize);

    PaginatedTransactionListDtoForAdmin getPresentableTransactionsForAdminWithPagination(Date startDate,
                                                                                         Date endDate,
                                                                                         String senderUsername,
                                                                                         String recipientUsername,
                                                                                         String amount,
                                                                                         String date,
                                                                                         int page,
                                                                                         int pageSize);

    PaginatedRecipientListDto getRecipientsWithPagination(String contactType,
                                                          String contactInformation,
                                                          int page,
                                                          int pageSize);

    PaginatedUserListDto getPresentableUsersWithPagination(String contactType,
                                                           String contactInformation,
                                                           int page,
                                                           int pageSize);

    List<PresentableCardDto> getPresentableCardDtos(int userId);

    List<PresentableWalletDto> getPresentableWalletDtos(String ownerUsername);
}
