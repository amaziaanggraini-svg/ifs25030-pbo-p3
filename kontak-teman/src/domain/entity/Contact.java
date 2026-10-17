package domain.entity;

public class Contact {
    private final int id;
    private String name;
    private String phone;
    private String email;

    public Contact(int id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public void changeName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
    }

    public void changePhone(String phone) {
        if (phone != null && !phone.trim().isEmpty()) {
            this.phone = phone;
        }
    }

    public void changeEmail(String email) {
        if (email != null && !email.trim().isEmpty()) {
            this.email = email;
        }
    }
}