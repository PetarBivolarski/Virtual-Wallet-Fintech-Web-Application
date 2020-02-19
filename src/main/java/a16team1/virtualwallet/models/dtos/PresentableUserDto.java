package a16team1.virtualwallet.models.dtos;

public class PresentableUserDto {

    private int id;

    private String username;

    private String phoneNumber;

    private String email;

    private boolean blocked;

    public PresentableUserDto() {
    }

    public PresentableUserDto(int id, String username, String phoneNumber, String email, boolean blocked) {
        this.id = id;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.blocked = blocked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}
