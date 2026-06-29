package com.smartparking.service;

import com.smartparking.entity.ParkingSlot;
import com.smartparking.repository.ParkingSlotRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingSlotService {

    private final ParkingSlotRepository repository;

    public ParkingSlotService(ParkingSlotRepository repository) {
        this.repository = repository;
    }

    public List<ParkingSlot> getAvailableSlots() {
        return repository.findByStatus("AVAILABLE");
    }
}