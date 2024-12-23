package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Passenger {
    private final StringProperty id;
    private final StringProperty name;
    private final StringProperty email;
    private final StringProperty phone;

    public Passenger(String id, String name, String email, String phone) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.phone = new SimpleStringProperty(phone);
    }
    public StringProperty idProperty() {
        return id;
    }
    public StringProperty nameProperty() {
        return name;
    }
    public StringProperty emailProperty() {
        return email;
    }
    public StringProperty phoneProperty() {
        return phone;
    }
    public String getId() {
        return id.get();
    }
    public String getName() {
        return name.get();
    }
    public String getEmail() {
        return email.get();
    }
    public String getPhone() {
        return phone.get();
    }
    public void setId(String id) {
        this.id.set(id);
    }
    public void setName(String name) {
        this.name.set(name);
    }
    public void setEmail(String email) {
        this.email.set(email);
    }
    public void setPhone(String phone) {
        this.phone.set(phone);
    }
    public String getTickets() {
      
        return "Tickets linked to Passenger: " + getId();
    }
}
