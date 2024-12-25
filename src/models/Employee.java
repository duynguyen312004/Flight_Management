package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Employee {
    private final StringProperty id;
    private final StringProperty name;
    private final StringProperty address;
    private final StringProperty role;

    public Employee(String id, String name, String address, String role) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.address = new SimpleStringProperty(address);
        this.role = new SimpleStringProperty(role);
    }

    public StringProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty addressProperty() {
        return address;
    }

    public StringProperty roleProperty() {
        return role;
    }

    public String getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public String getAddress() {
        return address.get();
    }

    public String getRole() {
        return role.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public void setRole(String role) {
        this.role.set(role);
    }

    public String getDetails() {
        return "Employee Details: ID=" + getId() + ", Name=" + getName() + ", Address=" + getAddress() + ", Role=" + getRole();
    }
}
