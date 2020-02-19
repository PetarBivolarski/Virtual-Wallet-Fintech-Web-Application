package a16team1.virtualwallet.repositories.contracts;

import a16team1.virtualwallet.models.Card;

import java.math.BigDecimal;

public interface ExternalCardRepository {
    boolean withdraw(BigDecimal amount, String description, Card card, String csv, String idempotencyKey);
}
