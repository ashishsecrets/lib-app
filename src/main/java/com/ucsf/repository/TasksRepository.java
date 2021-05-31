package com.ucsf.repository;

import com.ucsf.model.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TasksRepository extends JpaRepository<Tasks, Long> {

    Tasks findByTitle(String title);
}