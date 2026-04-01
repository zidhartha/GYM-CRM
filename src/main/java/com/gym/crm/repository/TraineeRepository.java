package com.gym.crm.repository;

import com.gym.crm.model.Trainee;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee,Long> {
    List<Trainee> findAll();

    @Query("SELECT t.user.username FROM Trainee t")
    List<String> findAllUsernames();

    void deleteByUserUsername(String username);

    @EntityGraph(attributePaths = {"user","trainers"})
    Optional<Trainee> findByUserUsername(String username);

}
