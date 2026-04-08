package com.cpt202.booking.controller;

import com.cpt202.booking.model.Specialist;
import com.cpt202.booking.repository.SpecialistRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/specialists")
public class SpecialistController {

    private final SpecialistRepository specialistRepository;

    public SpecialistController(SpecialistRepository specialistRepository) {
        this.specialistRepository = specialistRepository;
    }

    @GetMapping
    public List<Specialist> getAllSpecialists() {
        return specialistRepository.findAll();
    }

    @PostMapping
    public Specialist createSpecialist(@RequestBody Specialist specialist) {
        return specialistRepository.save(specialist);
    }
}