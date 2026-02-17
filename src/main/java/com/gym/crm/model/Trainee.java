package com.gym.crm.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
public class Trainee extends User {
    private String address;
    private LocalDate dateOfBirth;


    public Trainee(Long id, String firstName, String lastName, String username, String password, boolean isActive,String address,LocalDate dateOfBirth) {
        super(id, firstName, lastName, username, password, isActive);
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }

    public Trainee(){
        super();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Trainee trainee = (Trainee) o;
        return Objects.equals(address, trainee.address) && Objects.equals(dateOfBirth, trainee.dateOfBirth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), address, dateOfBirth);
    }

    @Override
    public String toString() {
        return "Trainee{" +
                "address='" + address + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }
}
