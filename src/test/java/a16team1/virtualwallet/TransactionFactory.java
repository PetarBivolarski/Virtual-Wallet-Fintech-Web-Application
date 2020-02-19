package a16team1.virtualwallet;

import a16team1.virtualwallet.models.*;
import a16team1.virtualwallet.models.dtos.ExternalTransactionDto;
import a16team1.virtualwallet.models.dtos.FundingTransactionDto;
import a16team1.virtualwallet.models.dtos.PaginatedTransactionListDto;
import a16team1.virtualwallet.models.dtos.PresentableTransactionDto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionFactory {
    public static Transaction createTransaction(PaymentInstrument senderInstrument, PaymentInstrument recipientInstrument, BigDecimal amount) {
        Transaction transaction = new Transaction();
        transaction.setSenderInstrument(senderInstrument);
        transaction.setRecipientInstrument(recipientInstrument);
        transaction.setTransferAmount(amount);
        return transaction;
    }

    public static Transaction createTransaction(User sender, User recipient) {
        PaymentInstrument senderWallet = PaymentInstrumentFactory.createDefaultWalletFor(sender, 1, "Sender Wallet");
        PaymentInstrument recipientWallet = PaymentInstrumentFactory.createDefaultWalletFor(recipient, 2, "Recipient Wallet");
        return TransactionFactory.createTransaction(senderWallet, recipientWallet, BigDecimal.TEN);
    }

    public static ExternalTransactionDto createExternalTransactionDto(User sender, User recipient, Wallet wallet, BigDecimal amount) {
        ExternalTransactionDto transactionDto = new ExternalTransactionDto();
        transactionDto.setSenderUsername(sender.getUsername());
        transactionDto.setRecipientId(recipient.getId());
        transactionDto.setSenderWalletId(wallet.getId());
        transactionDto.setTransferAmount(amount);
        return transactionDto;
    }

    public static FundingTransactionDto createFundingTransactionDto(User user, Card card, Wallet wallet, BigDecimal amount, String csv) {
        FundingTransactionDto transactionDto = new FundingTransactionDto();
        transactionDto.setUserId(user.getId());
        transactionDto.setCardId(card.getId());
        transactionDto.setCsv(csv);
        transactionDto.setWalletId(wallet.getId());
        transactionDto.setTransferAmount(amount);
        return transactionDto;
    }

    public static PresentableTransactionDto createPresentableTransactionDto(Transaction transaction) {
        return new PresentableTransactionDto(
                transaction.getDateTime(),
                transaction.getTransferAmount(),
                transaction.getSenderInstrument(),
                transaction.getRecipientInstrument(),
                "");
    }

    public static List<Transaction> createTransactionList(PaymentInstrument user1Card,
                                                          PaymentInstrument user1Wallet,
                                                          PaymentInstrument user2Wallet,
                                                          PaymentInstrument user3Wallet) {
        List<Transaction> transactions = new ArrayList<>();
        Transaction transaction0 = TransactionFactory.createTransaction(user1Wallet, user2Wallet, BigDecimal.valueOf(20));
        transaction0.setDateTime(Timestamp.valueOf("2020-01-03 00:00:00"));
        transactions.add(transaction0);
        Transaction transaction1 = TransactionFactory.createTransaction(user1Wallet, user3Wallet, BigDecimal.valueOf(50));
        transaction1.setDateTime(Timestamp.valueOf("2020-01-10 00:00:00"));
        transactions.add(transaction1);
        Transaction transaction2 = TransactionFactory.createTransaction(user1Card, user1Wallet, BigDecimal.valueOf(100));
        transaction2.setDateTime(Timestamp.valueOf("2020-01-13 00:00:00"));
        transactions.add(transaction2);
        Transaction transaction3 = TransactionFactory.createTransaction(user2Wallet, user1Wallet, BigDecimal.valueOf(30));
        transaction3.setDateTime(Timestamp.valueOf("2020-02-08 00:00:00"));
        transactions.add(transaction3);
        Transaction transaction4 = TransactionFactory.createTransaction(user1Wallet, user2Wallet, BigDecimal.valueOf(10));
        transaction4.setDateTime(Timestamp.from(ZonedDateTime.now().toInstant()));
        transactions.add(transaction4);
        return transactions;
    }

//    public static List<PresentableTransactionDto> createPresentableTransactionDtoList(PaymentInstrument user1Card,
//                                                                                      PaymentInstrument user1Wallet,
//                                                                                      PaymentInstrument user2Wallet,
//                                                                                      PaymentInstrument user3Wallet) {
//        List<PresentableTransactionDto> transactionDtos = new ArrayList<>();
//        return transactionDtos;
//    }

}
