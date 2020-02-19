package a16team1.virtualwallet.utilities.mappers;

import a16team1.virtualwallet.models.PaymentInstrument;
import a16team1.virtualwallet.models.Transaction;
import a16team1.virtualwallet.models.User;
import a16team1.virtualwallet.models.Wallet;
import a16team1.virtualwallet.models.dtos.ExternalTransactionDto;
import a16team1.virtualwallet.models.dtos.FundingTransactionDto;
import a16team1.virtualwallet.models.dtos.PresentableTransactionDto;
import a16team1.virtualwallet.utilities.Constants;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionDtoMapper {

    public Transaction fromDto(FundingTransactionDto fundingTransactionDto,
                               PaymentInstrument senderInstrument,
                               PaymentInstrument recipientInstrument) {
        Transaction transaction = new Transaction();
        transaction.setTransferAmount(fundingTransactionDto.getTransferAmount());
        if (fundingTransactionDto.getWithDonation()) {
            transaction.setTransferAmount(transaction.getTransferAmount().add(Constants.DONATION_AMOUNT));
            transaction.setWithDonation(true);
        }
        transaction.setDescription(fundingTransactionDto.getDescription());
        transaction.setSenderInstrument(senderInstrument);
        transaction.setRecipientInstrument(recipientInstrument);
        return transaction;
    }


    public PresentableTransactionDto toDto(Transaction transaction) {
        return new PresentableTransactionDto(
                transaction.getDateTime(),
                transaction.getTransferAmount(),
                transaction.getSenderInstrument(),
                transaction.getRecipientInstrument(),
                transaction.getDescription());
    }
}
