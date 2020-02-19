package a16team1.virtualwallet.models.dtos;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import static a16team1.virtualwallet.utilities.Constants.VALUE_CANNOT_BE_EMPTY;

public class NewWalletDto {

    private static final String INVALID_WALLET_NAME = "Wallet name must be between 3 and 50 characters";

    @Valid
    private NewPaymentInstrumentDto paymentInstrumentDto;

    @NotEmpty(message = VALUE_CANNOT_BE_EMPTY)
    @Size(min = 3, max = 50, message = INVALID_WALLET_NAME)
    private String name;

    private int ownerId;

    public NewWalletDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public NewPaymentInstrumentDto getPaymentInstrumentDto() {
        return paymentInstrumentDto;
    }

    public void setPaymentInstrumentDto(NewPaymentInstrumentDto paymentInstrumentDto) {
        this.paymentInstrumentDto = paymentInstrumentDto;
    }
}
