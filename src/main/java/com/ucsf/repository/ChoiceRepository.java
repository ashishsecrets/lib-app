package com.ucsf.repository;

import java.awt.Choice;

import org.springframework.data.repository.CrudRepository;

import com.ucsf.model.Choices;
import com.ucsf.model.ScreeningQuestions;

public interface ChoiceRepository extends CrudRepository<Choices, Long> {
}
