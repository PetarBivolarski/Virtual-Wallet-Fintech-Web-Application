package a16team1.virtualwallet.models;

import a16team1.virtualwallet.utilities.InstrumentType;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "payment_instruments")
public class PaymentInstrument {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "instrument_type")
    @Enumerated(EnumType.STRING)
    private InstrumentType instrumentType;

    @Column(name = "name")
    @Size(min = 1, max = 50)
    private String name;

    public PaymentInstrument() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public InstrumentType getInstrumentType() {
        return instrumentType;
    }

    public void setInstrumentType(InstrumentType instrumentType) {
        this.instrumentType = instrumentType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
