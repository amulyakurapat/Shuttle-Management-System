package com.shuttle.SMS.controller;

import com.shuttle.SMS.dto.BookingRequestDTO;
import com.shuttle.SMS.dto.ShuttleOptionDTO;
import com.shuttle.SMS.dto.ConfirmBookingRequestDTO;
import com.shuttle.SMS.dto.ConfirmedBookingResponseDTO;
import com.shuttle.SMS.dto.TripHistoryResponseDTO;
import com.shuttle.SMS.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService){
        this.bookingService = bookingService;
    }

    @PostMapping("/options")
    public ResponseEntity<List<ShuttleOptionDTO>> getAvailableShuttleOptions(@RequestBody BookingRequestDTO bookingRequest) {
        List<ShuttleOptionDTO> options = bookingService.getAvailableShuttles(bookingRequest);
        return ResponseEntity.ok(options);
    }

    @PostMapping("/confirm")
    public ResponseEntity<ConfirmedBookingResponseDTO> confirmBooking(@RequestBody ConfirmBookingRequestDTO request) {
        ConfirmedBookingResponseDTO response = bookingService.confirmBooking(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<TripHistoryResponseDTO> getTripHistory(@RequestParam Long userId) {
        TripHistoryResponseDTO response = bookingService.getTripHistory(userId);
        return ResponseEntity.ok(response);
    }
}
