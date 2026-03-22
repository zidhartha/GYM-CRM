package com.gym.crm.Repository;

import com.gym.crm.dto.trainee.TraineeListItemDto;
import com.gym.crm.model.Trainer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TrainerRepository extends JpaRepository<Trainer,Long>{
    @Query("select tr from Trainer tr where tr not in " +
            "(select t.trainer from Training t where t.trainee.user.username = :traineeUsername) ")
    List<Trainer> findTrainersNotAssignedToTrainee(@Param("traineeUsername") String traineeUsername);

    @EntityGraph(value = "Trainer.withTrainees")
    List<Trainer> findAll();

    @EntityGraph(attributePaths = {"user", "specialization"})
    Optional<Trainer> findByUserUsername(String username);

    @Query("SELECT t.user.username FROM Trainer t")
    List<String> findAllUsernames();

    List<Trainer> findAllByUserUsernameIn(Set<String> usernames);

}
