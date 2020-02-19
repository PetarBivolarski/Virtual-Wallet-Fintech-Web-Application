package a16team1.virtualwallet.models.dtos;

import java.math.BigDecimal;

public class PresentableWalletDto {

    private int id;

    private String name;

    private BigDecimal amount;

    private boolean defaultWallet;

    public PresentableWalletDto() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean isDefaultWallet() {
        return defaultWallet;
    }

    public void setDefaultWallet(boolean defaultWallet) {
        this.defaultWallet = defaultWallet;
    }
}
