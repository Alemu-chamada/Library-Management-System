package library.model;

import java.util.Objects;

public class AuthAccount {

    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private String passwordHash;

    public AuthAccount(String username, String fullName, String email, String phone,
                       String role, String passwordHash) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.passwordHash = passwordHash;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getDisplayName() {
        return fullName == null || fullName.isBlank() ? username : fullName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthAccount that = (AuthAccount) o;
        return username.equalsIgnoreCase(that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username.toLowerCase());
    }

    @Override
    public String toString() {
        return String.format("AuthAccount[username=%s, fullName=%s, email=%s, role=%s]",
                username, fullName, email, role);
    }
}
