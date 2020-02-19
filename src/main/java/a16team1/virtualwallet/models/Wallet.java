package a16team1.virtualwallet.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import org.hibernate.annotations.Formula;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "wallets")
@ApiModel(value = "Wallet", description = "Details about a wallet composition returned by GET request of user by ID")
@JsonPropertyOrder({"id", "name", "saldo"})
public class Wallet {
    @Id
    @Column(name = "id")
    private int id;

    @Formula("(SELECT pi.name FROM payment_instruments AS pi WHERE pi.id = id)")
    private String name;

    @Column(name = "saldo")
    private BigDecimal saldo;

    @JsonIgnore
    @Column(name = "deleted")
    private boolean deleted;

    public Wallet() {
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
