package com.gym.crm.Repository;

import com.gym.crm.model.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TrainingTypeRepository extends JpaRepository<TrainingType,Long> {
    Optional<TrainingType> findByName(String TrainingTypeName);
}
