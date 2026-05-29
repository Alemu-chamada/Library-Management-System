package library.model;

import java.util.Objects;

/**
 * Represents a registered library patron.
 * Users can borrow and return books through transactions.
 */
public class User {

    private String userId;
    private String name;
    private String email;

    public User(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    // Simpler constructor for when email isn't provided
    public User(String userId, String name) {
        this(userId, name, "");
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User other = (User) o;
        return userId.equalsIgnoreCase(other.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId.toLowerCase());
    }

    @Override
    public String toString() {
        return String.format("User[id=%s, name='%s', email='%s']", userId, name, email);
    }
}
