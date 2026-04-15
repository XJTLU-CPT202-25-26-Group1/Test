package com.cpt202.booking.controller;

import com.cpt202.booking.model.Specialist;
import com.cpt202.booking.service.SpecialistService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/specialists")
public class SpecialistController {

    private final SpecialistService specialistService;

    public SpecialistController(SpecialistService specialistService) {
        this.specialistService = specialistService;
    }

    @GetMapping
    public List<Specialist> getAllSpecialists() {
        return specialistService.getAllSpecialists();
    }
}
