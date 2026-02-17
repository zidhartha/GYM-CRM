package com.gym.crm.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Setter
@Getter
@ToString
public class Trainer extends User {
    private TrainingType specialization;

    public Trainer(Long id, String firstName, String lastName, String username, String password, boolean isActive,TrainingType specialization) {
        super(id, firstName, lastName, username, password, isActive);
        this.specialization = specialization;
    }
    public Trainer(){
        super();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Trainer trainer = (Trainer) o;
        return Objects.equals(specialization, trainer.specialization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), specialization);
    }
}
