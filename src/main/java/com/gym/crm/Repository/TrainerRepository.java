package com.gym.crm.Repository;

import com.gym.crm.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface TrainerRepository extends JpaRepository<Trainer,Long>{

    @Query("SELECT t from Trainer t where t.user.username = :username")
    Optional<Trainer> findByUsername(@Param("username") String username);

    @Query("select tr from Trainer tr where tr not in " +
            "(select t.trainer from Training t where t.trainee.user.username = :traineeUsername) ")
    List<Trainer> findTrainersNotAssignedToTrainee(@Param("traineeUsername") String traineeUsername);

    @Query("Select t from Trainer t")
    List<Trainer> findAll();

    @Query("SELECT t.user.username FROM Trainer t")
    List<String> findAllUsernames();
}
