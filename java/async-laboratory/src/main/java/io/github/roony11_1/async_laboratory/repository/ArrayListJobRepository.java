package io.github.roony11_1.async_laboratory.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import io.github.roony11_1.async_laboratory.model.Job;

@Repository 
public class ArrayListJobRepository implements IJobRepository
{
    private final List<Job> jobs = new ArrayList<>();

    @Override
    public boolean save(Job job)
    {
        int index = jobs.indexOf(job);

        if (index == -1)
            return jobs.add(job);

        jobs.set(index, job);
        return true;
    }

    @Override
    public Optional<Job> findById(UUID id)
    {
        return jobs.stream()
            .filter(j -> j.getId().equals(id))
            .findFirst();
    }

    @Override
    public List<Job> findAll()
    {
        return jobs;
    }
}
