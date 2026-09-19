package io.github.roony11_1.async_laboratory.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.github.roony11_1.async_laboratory.model.Job;

public interface IJobRepository 
{
    boolean save(Job job);
    Optional<Job> findById(UUID id);
    List<Job> findAll();
}
