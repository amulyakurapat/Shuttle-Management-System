//package com.shuttle.SMS.service;
//
//import com.shuttle.SMS.dto.BookingRequestDTO;
//import com.shuttle.SMS.dto.BookingResponseDTO;
//import com.shuttle.SMS.dto.ShuttleOptionDTO;
//
//import java.util.List;
//
//public interface BookingService {
//    BookingResponseDTO bookShuttle(Long userId, BookingRequestDTO bookingRequest);
//    List<ShuttleOptionDTO> getAvailableShuttles(BookingRequestDTO bookingRequest);
//}
//



//package com.shuttle.SMS.service;
//
//import com.shuttle.SMS.dto.BookingRequestDTO;
//import com.shuttle.SMS.dto.ShuttleOptionDTO;
//import com.shuttle.SMS.dto.ConfirmBookingRequestDTO;
//import com.shuttle.SMS.dto.ConfirmedBookingResponseDTO;
//import java.util.List;
//
//public interface BookingService {
//    List<ShuttleOptionDTO> getAvailableShuttles(BookingRequestDTO bookingRequest);
//    ConfirmedBookingResponseDTO confirmBooking(ConfirmBookingRequestDTO bookingRequest);
//}


package com.shuttle.SMS.service;

import com.shuttle.SMS.dto.BookingRequestDTO;
import com.shuttle.SMS.dto.ShuttleOptionDTO;
import com.shuttle.SMS.dto.ConfirmBookingRequestDTO;
import com.shuttle.SMS.dto.ConfirmedBookingResponseDTO;
import com.shuttle.SMS.dto.TripHistoryResponseDTO;
import java.util.List;

public interface BookingService {
    List<ShuttleOptionDTO> getAvailableShuttles(BookingRequestDTO bookingRequest);
    ConfirmedBookingResponseDTO confirmBooking(ConfirmBookingRequestDTO bookingRequest);
    TripHistoryResponseDTO getTripHistory(Long userId);
}
