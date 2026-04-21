package com.cpt202.booking.availability;

import com.cpt202.booking.model.Specialist;
import com.cpt202.booking.repository.SpecialistRepository;
import com.cpt202.booking.service.AvailabilityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AvailabilityServiceTest {

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private SpecialistRepository specialistRepository;

    @Test
    void overlappingSlotIsRejected() {
        Specialist specialist = specialistRepository.findAll().get(0);
        LocalDate slotDate = availabilityService.getSlotsForSpecialist(specialist.getId()).get(0).getSlotDate();

        assertThrows(IllegalArgumentException.class, () -> availabilityService.createSlot(
                specialist.getId(),
                slotDate,
                LocalTime.of(10, 30),
                LocalTime.of(11, 30)
        ));
    }
}
