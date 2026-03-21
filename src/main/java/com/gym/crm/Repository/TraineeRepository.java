package com.gym.crm.Repository;

import com.gym.crm.dto.trainer.TrainerListItemDto;
import com.gym.crm.model.Trainee;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee,Long> {
    List<Trainee> findAll();

    @Query("SELECT t.user.username FROM Trainee t")
    List<String> findAllUsernames();

    void deleteByUserUsername(String username);

    @Query("""
    SELECT new com.gym.crm.dto.trainer.TrainerListItemDto(
        trr.user.username,
        trr.user.firstName,
        trr.user.lastName,
        trr.specialization.id
    )
    FROM Trainee tre
    JOIN tre.trainers trr
    WHERE tre.user.username = :traineeUsername
    """)
    List<TrainerListItemDto> findTraineeTrainers(@Param("traineeUsername") String traineeUsername);

    @EntityGraph(attributePaths = {"user","trainers"})
    Optional<Trainee> findByUserUsername(String username);

}
