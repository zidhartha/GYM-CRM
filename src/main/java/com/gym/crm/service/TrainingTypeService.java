package com.gym.crm.service;

import com.gym.crm.Repository.TrainingTypeRepository;
import com.gym.crm.dto.trainingType.TrainingTypeListDto;
import com.gym.crm.dto.trainingType.TrainingTypeProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingTypeService {
    private final TrainingTypeRepository trainingTypeRepositoryRepository;

    public TrainingTypeListDto getAllTrainingTypes(){
        List<TrainingTypeProfileDto> trainingTypes= trainingTypeRepositoryRepository.findAll().stream().map(
                t -> new TrainingTypeProfileDto(t.getId(),t.getName())
        ).collect(Collectors.toList());
        return new TrainingTypeListDto(trainingTypes);
    }
}
