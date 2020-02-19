package a16team1.virtualwallet.models.dtos;

import a16team1.virtualwallet.utilities.InstrumentType;

import javax.validation.constraints.Size;

// To be used when creating a new payment instrument
public class NewPaymentInstrumentDto {

    @Size(min = 3, max = 50)
    private String name;

    private InstrumentType instrumentType;

    private int ownerId;

    public NewPaymentInstrumentDto() { }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InstrumentType getInstrumentType() {
        return instrumentType;
    }

    public void setInstrumentType(InstrumentType instrumentType) {
        this.instrumentType = instrumentType;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

}
