//package com.shuttle.SMS.service;
//
//import com.shuttle.SMS.dto.BookingRequestDTO;
//import com.shuttle.SMS.dto.ShuttleOptionDTO;
//import com.shuttle.SMS.dto.ConfirmBookingRequestDTO;
//import com.shuttle.SMS.dto.ConfirmedBookingResponseDTO;
//import com.shuttle.SMS.model.Booking;
//import com.shuttle.SMS.model.Route;
//import com.shuttle.SMS.model.Shuttle;
//import com.shuttle.SMS.model.Stop;
//import com.shuttle.SMS.model.User;
//import com.shuttle.SMS.repository.BookingRepository;
//import com.shuttle.SMS.repository.RouteRepository;
//import com.shuttle.SMS.repository.ShuttleRepository;
//import com.shuttle.SMS.repository.StopRepository;
//import com.shuttle.SMS.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.Comparator;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class BookingServiceImpl implements BookingService {
//
//    private final StopRepository stopRepository;
//    private final ShuttleRepository shuttleRepository;
//    private final UserRepository userRepository;
//    private final BookingRepository bookingRepository;
//    private final RouteRepository routeRepository;
//
//    @Autowired
//    public BookingServiceImpl(StopRepository stopRepository,
//                              ShuttleRepository shuttleRepository,
//                              UserRepository userRepository,
//                              BookingRepository bookingRepository,
//                              RouteRepository routeRepository) {
//        this.stopRepository = stopRepository;
//        this.shuttleRepository = shuttleRepository;
//        this.userRepository = userRepository;
//        this.bookingRepository = bookingRepository;
//        this.routeRepository = routeRepository;
//    }
//
//    @Override
//    public List<ShuttleOptionDTO> getAvailableShuttles(BookingRequestDTO bookingRequest) {
//        // Validate that the user exists.
//        User user = userRepository.findById(bookingRequest.getUserId())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // Retrieve all stops (for demo purposes; in production, use spatial queries)
//        List<Stop> stops = stopRepository.findAll();
//        if (stops.isEmpty()) {
//            throw new RuntimeException("No shuttle stops available");
//        }
//
//        // Determine the nearest pickup and destination stops based on provided coordinates.
//        Stop startStop = findNearestStop(bookingRequest.getCurrentLatitude(),
//                bookingRequest.getCurrentLongitude(), stops);
//        Stop endStop = findNearestStop(bookingRequest.getDestinationLatitude(),
//                bookingRequest.getDestinationLongitude(), stops);
//
//        // Calculate route distance (in km) using the Haversine formula.
//        double routeDistance = calculateDistance(startStop.getLatitude(), startStop.getLongitude(),
//                endStop.getLatitude(), endStop.getLongitude());
//
//        // Define cost factors.
//        double costPerKm = 3.0;          // Base cost per kilometer.
//        double costPerMinute = 0.5;      // Cost per minute of travel.
//        double defaultSpeed = 30.0;      // Default shuttle speed (km/h) if avgSpeed not provided.
//        double waitingTimeFactor = 5.0;  // Additional waiting time (minutes per km of pickup distance).
//
//        double baseCost = routeDistance * costPerKm;
//
//        // Retrieve all shuttles.
//        List<Shuttle> shuttles = shuttleRepository.findAll();
//        if (shuttles.isEmpty()) {
//            throw new RuntimeException("No shuttle available");
//        }
//
//        // Filter shuttles within 1 km of the user's pickup location, compute cost and travel time,
//        // sort by cost, and return only the top 4.
//        List<ShuttleOptionDTO> options = shuttles.stream()
//                .filter(shuttle -> {
//                    if (shuttle.getCurrentLatitude() == null || shuttle.getCurrentLongitude() == null) {
//                        return false;
//                    }
//                    double pickupDistance = calculateDistance(
//                            bookingRequest.getCurrentLatitude(),
//                            bookingRequest.getCurrentLongitude(),
//                            shuttle.getCurrentLatitude(),
//                            shuttle.getCurrentLongitude());
//                    return pickupDistance <= 1.0;
//                })
//                .map(shuttle -> {
//                    double pickupDistance = calculateDistance(
//                            bookingRequest.getCurrentLatitude(),
//                            bookingRequest.getCurrentLongitude(),
//                            shuttle.getCurrentLatitude(),
//                            shuttle.getCurrentLongitude());
//                    // Additional waiting time based on pickup distance.
//                    double additionalWaitTime = pickupDistance * waitingTimeFactor;
//                    // Use shuttle's avgSpeed if available; otherwise, use defaultSpeed.
//                    double shuttleSpeed = (shuttle.getAvgSpeed() != null ? shuttle.getAvgSpeed() : defaultSpeed);
//                    // Calculate base travel time along the route (in minutes).
//                    double baseTravelTime = (routeDistance / shuttleSpeed) * 60;
//                    double totalTravelTime = baseTravelTime + additionalWaitTime;
//                    double travelTimeCost = totalTravelTime * costPerMinute;
//                    double finalCost = baseCost + travelTimeCost;
//
//                    double roundedCost = BigDecimal.valueOf(finalCost)
//                            .setScale(1, RoundingMode.HALF_UP)
//                            .doubleValue();
//                    double roundedTravelTime = BigDecimal.valueOf(totalTravelTime)
//                            .setScale(1, RoundingMode.HALF_UP)
//                            .doubleValue();
//
//                    ShuttleOptionDTO option = new ShuttleOptionDTO();
//                    option.setShuttleId(shuttle.getId());
//                    option.setShuttleVehicleNumber(shuttle.getVehicleNumber());
//                    option.setCost(roundedCost);
//                    option.setEstimatedTravelTime(roundedTravelTime);
//                    return option;
//                })
//                .sorted(Comparator.comparingDouble(ShuttleOptionDTO::getCost))
//                .limit(4)
//                .collect(Collectors.toList());
//
//        if (options.isEmpty()) {
//            throw new RuntimeException("No nearby shuttle available");
//        }
//        return options;
//    }
//
//    @Override
//    public ConfirmedBookingResponseDTO confirmBooking(ConfirmBookingRequestDTO bookingRequest) {
//        // Validate that the user exists.
//        User user = userRepository.findById(bookingRequest.getUserId())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // Validate that the selected shuttle exists.
//        Shuttle shuttle = shuttleRepository.findById(bookingRequest.getSelectedShuttleId())
//                .orElseThrow(() -> new RuntimeException("Shuttle not found"));
//
//        // Retrieve all stops.
//        List<Stop> stops = stopRepository.findAll();
//        if (stops.isEmpty()) {
//            throw new RuntimeException("No shuttle stops available");
//        }
//
//        // Determine the nearest pickup and destination stops.
//        Stop startStop = findNearestStop(bookingRequest.getCurrentLatitude(),
//                bookingRequest.getCurrentLongitude(), stops);
//        Stop endStop = findNearestStop(bookingRequest.getDestinationLatitude(),
//                bookingRequest.getDestinationLongitude(), stops);
//
//        // Calculate route distance using the Haversine formula.
//        double routeDistance = calculateDistance(startStop.getLatitude(), startStop.getLongitude(),
//                endStop.getLatitude(), endStop.getLongitude());
//
//        double costPerKm = 3.0;
//        double costPerMinute = 0.5;
//        double defaultSpeed = 30.0;
//        double waitingTimeFactor = 5.0;
//        double baseCost = routeDistance * costPerKm;
//
//        double pickupDistance = calculateDistance(
//                bookingRequest.getCurrentLatitude(),
//                bookingRequest.getCurrentLongitude(),
//                shuttle.getCurrentLatitude(),
//                shuttle.getCurrentLongitude());
//        double additionalWaitTime = pickupDistance * waitingTimeFactor;
//        double shuttleSpeed = (shuttle.getAvgSpeed() != null ? shuttle.getAvgSpeed() : defaultSpeed);
//        double baseTravelTime = (routeDistance / shuttleSpeed) * 60;
//        double totalTravelTime = baseTravelTime + additionalWaitTime;
//        double travelTimeCost = totalTravelTime * costPerMinute;
//        double finalCost = baseCost + travelTimeCost;
//        double roundedCost = BigDecimal.valueOf(finalCost).setScale(1, RoundingMode.HALF_UP).doubleValue();
//        double roundedTravelTime = BigDecimal.valueOf(totalTravelTime).setScale(1, RoundingMode.HALF_UP).doubleValue();
//
//        // If user's wallet balance is insufficient, return a failure response.
//        if (user.getWalletBalance().compareTo(BigDecimal.valueOf(roundedCost)) < 0) {
//            ConfirmedBookingResponseDTO response = new ConfirmedBookingResponseDTO();
//            response.setUserId(user.getId());
//            response.setStatus("FAILED: Insufficient wallet balance");
//            return response;
//        }
//
//        // Deduct the fare from the user's wallet.
//        user.setWalletBalance(user.getWalletBalance().subtract(BigDecimal.valueOf(roundedCost)));
//        userRepository.save(user);
//
//        // For the booking, assign a default route. (In a real system, this could be determined dynamically.)
//        Route route = routeRepository.findAll().stream().findFirst()
//                .orElseThrow(() -> new RuntimeException("No route available"));
//
//        // Create and save the booking record.
//        Booking booking = Booking.builder()
//                .user(user)
//                .shuttle(shuttle)
//                .startStop(startStop)
//                .endStop(endStop)
//                .route(route)
//                .bookingTime(LocalDateTime.now())
//                .travelDate(LocalDate.now())
//                .fare(BigDecimal.valueOf(roundedCost))
//                .pointsDeducted(BigDecimal.valueOf(roundedCost))
//                .status("CONFIRMED")
//                .build();
//        booking = bookingRepository.save(booking);
//
//        // Prepare the response.
//        ConfirmedBookingResponseDTO response = new ConfirmedBookingResponseDTO();
//        response.setBookingId(booking.getId());
//        response.setUserId(user.getId());
//        response.setShuttleId(shuttle.getId());
//        response.setShuttleVehicleNumber(shuttle.getVehicleNumber());
//        response.setCost(roundedCost);
//        response.setEstimatedTravelTime(roundedTravelTime);
//        response.setBookingTime(booking.getBookingTime());
//        response.setStatus(booking.getStatus());
//        return response;
//    }
//
//    // Helper method: Calculate distance using the Haversine formula (returns distance in kilometers).
//    private double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
//        final int R = 6371; // Earth's radius in km.
//        double latDistance = Math.toRadians(lat2 - lat1);
//        double lonDistance = Math.toRadians(lon2 - lon1);
//        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
//                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
//                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//        return R * c;
//    }
//
//    // Helper method: Find the nearest stop from a list of stops based on the provided latitude and longitude.
//    private Stop findNearestStop(Double latitude, Double longitude, List<Stop> stops) {
//        return stops.stream()
//                .min((s1, s2) -> {
//                    double d1 = calculateDistance(latitude, longitude, s1.getLatitude(), s1.getLongitude());
//                    double d2 = calculateDistance(latitude, longitude, s2.getLatitude(), s2.getLongitude());
//                    return Double.compare(d1, d2);
//                })
//                .orElseThrow(() -> new RuntimeException("No stops found"));
//    }
//}




package com.shuttle.SMS.service;

import com.shuttle.SMS.dto.BookingRequestDTO;
import com.shuttle.SMS.dto.ShuttleOptionDTO;
import com.shuttle.SMS.dto.ConfirmBookingRequestDTO;
import com.shuttle.SMS.dto.ConfirmedBookingResponseDTO;
import com.shuttle.SMS.dto.TripHistoryResponseDTO;
import com.shuttle.SMS.dto.TripHistoryDTO;
import com.shuttle.SMS.dto.FrequentRouteSuggestionDTO;
import com.shuttle.SMS.model.Booking;
import com.shuttle.SMS.model.Route;
import com.shuttle.SMS.model.Shuttle;
import com.shuttle.SMS.model.Stop;
import com.shuttle.SMS.model.User;
import com.shuttle.SMS.repository.BookingRepository;
import com.shuttle.SMS.repository.RouteRepository;
import com.shuttle.SMS.repository.ShuttleRepository;
import com.shuttle.SMS.repository.StopRepository;
import com.shuttle.SMS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final StopRepository stopRepository;
    private final ShuttleRepository shuttleRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final RouteRepository routeRepository;

    @Autowired
    public BookingServiceImpl(StopRepository stopRepository,
                              ShuttleRepository shuttleRepository,
                              UserRepository userRepository,
                              BookingRepository bookingRepository,
                              RouteRepository routeRepository) {
        this.stopRepository = stopRepository;
        this.shuttleRepository = shuttleRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.routeRepository = routeRepository;
    }

    @Override
    public List<ShuttleOptionDTO> getAvailableShuttles(BookingRequestDTO bookingRequest) {
        // Validate that the user exists.
        User user = userRepository.findById(bookingRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Retrieve all stops (for demo purposes; in production, use spatial queries)
        List<Stop> stops = stopRepository.findAll();
        if (stops.isEmpty()) {
            throw new RuntimeException("No shuttle stops available");
        }

        // Determine the nearest pickup and destination stops based on provided coordinates.
        Stop startStop = findNearestStop(bookingRequest.getCurrentLatitude(),
                bookingRequest.getCurrentLongitude(), stops);
        Stop endStop = findNearestStop(bookingRequest.getDestinationLatitude(),
                bookingRequest.getDestinationLongitude(), stops);

        // Calculate route distance (in km) using the Haversine formula.
        double routeDistance = calculateDistance(startStop.getLatitude(), startStop.getLongitude(),
                endStop.getLatitude(), endStop.getLongitude());

        // Define cost factors.
        double costPerKm = 3.0;          // Base cost per kilometer.
        double costPerMinute = 0.5;      // Cost per minute of travel.
        double defaultSpeed = 30.0;      // Default shuttle speed (km/h) if avgSpeed not provided.
        double waitingTimeFactor = 5.0;  // Additional waiting time (minutes per km of pickup distance).

        double baseCost = routeDistance * costPerKm;

        // Retrieve all shuttles.
        List<Shuttle> shuttles = shuttleRepository.findAll();
        if (shuttles.isEmpty()) {
            throw new RuntimeException("No shuttle available");
        }

        // Filter shuttles within 1 km of the user's pickup location, compute cost and travel time,
        // sort by cost, and return only the top 4.
        List<ShuttleOptionDTO> options = shuttles.stream()
                .filter(shuttle -> {
                    if (shuttle.getCurrentLatitude() == null || shuttle.getCurrentLongitude() == null) {
                        return false;
                    }
                    double pickupDistance = calculateDistance(
                            bookingRequest.getCurrentLatitude(),
                            bookingRequest.getCurrentLongitude(),
                            shuttle.getCurrentLatitude(),
                            shuttle.getCurrentLongitude());
                    return pickupDistance <= 1.0;
                })
                .map(shuttle -> {
                    double pickupDistance = calculateDistance(
                            bookingRequest.getCurrentLatitude(),
                            bookingRequest.getCurrentLongitude(),
                            shuttle.getCurrentLatitude(),
                            shuttle.getCurrentLongitude());
                    // Additional waiting time based on pickup distance.
                    double additionalWaitTime = pickupDistance * waitingTimeFactor;
                    // Use shuttle's avgSpeed if available; otherwise, use defaultSpeed.
                    double shuttleSpeed = (shuttle.getAvgSpeed() != null ? shuttle.getAvgSpeed() : defaultSpeed);
                    // Base travel time along the route (in minutes).
                    double baseTravelTime = (routeDistance / shuttleSpeed) * 60;
                    double totalTravelTime = baseTravelTime + additionalWaitTime;
                    double travelTimeCost = totalTravelTime * costPerMinute;
                    double finalCost = baseCost + travelTimeCost;
                    double roundedCost = BigDecimal.valueOf(finalCost).setScale(1, RoundingMode.HALF_UP).doubleValue();
                    double roundedTravelTime = BigDecimal.valueOf(totalTravelTime).setScale(1, RoundingMode.HALF_UP).doubleValue();

                    ShuttleOptionDTO option = new ShuttleOptionDTO();
                    option.setShuttleId(shuttle.getId());
                    option.setShuttleVehicleNumber(shuttle.getVehicleNumber());
                    option.setCost(roundedCost);
                    option.setEstimatedTravelTime(roundedTravelTime);
                    return option;
                })
                .sorted(Comparator.comparingDouble(ShuttleOptionDTO::getCost))
                .limit(4)
                .collect(Collectors.toList());

        if (options.isEmpty()) {
            throw new RuntimeException("No nearby shuttle available");
        }
        return options;
    }

    @Override
    public ConfirmedBookingResponseDTO confirmBooking(ConfirmBookingRequestDTO bookingRequest) {
        // Validate that the user exists.
        User user = userRepository.findById(bookingRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate that the selected shuttle exists.
        Shuttle shuttle = shuttleRepository.findById(bookingRequest.getSelectedShuttleId())
                .orElseThrow(() -> new RuntimeException("Shuttle not found"));

        // Retrieve all stops.
        List<Stop> stops = stopRepository.findAll();
        if (stops.isEmpty()) {
            throw new RuntimeException("No shuttle stops available");
        }

        // Determine the nearest pickup and destination stops.
        Stop startStop = findNearestStop(bookingRequest.getCurrentLatitude(),
                bookingRequest.getCurrentLongitude(), stops);
        Stop endStop = findNearestStop(bookingRequest.getDestinationLatitude(),
                bookingRequest.getDestinationLongitude(), stops);

        // Calculate route distance using Haversine formula.
        double routeDistance = calculateDistance(startStop.getLatitude(), startStop.getLongitude(),
                endStop.getLatitude(), endStop.getLongitude());

        double costPerKm = 3.0;
        double costPerMinute = 0.5;
        double defaultSpeed = 30.0;
        double waitingTimeFactor = 5.0;
        double baseCost = routeDistance * costPerKm;

        double pickupDistance = calculateDistance(
                bookingRequest.getCurrentLatitude(),
                bookingRequest.getCurrentLongitude(),
                shuttle.getCurrentLatitude(),
                shuttle.getCurrentLongitude());
        double additionalWaitTime = pickupDistance * waitingTimeFactor;
        double shuttleSpeed = (shuttle.getAvgSpeed() != null ? shuttle.getAvgSpeed() : defaultSpeed);
        double baseTravelTime = (routeDistance / shuttleSpeed) * 60;
        double totalTravelTime = baseTravelTime + additionalWaitTime;
        double travelTimeCost = totalTravelTime * costPerMinute;
        double finalCost = baseCost + travelTimeCost;
        double roundedCost = BigDecimal.valueOf(finalCost).setScale(1, RoundingMode.HALF_UP).doubleValue();
        double roundedTravelTime = BigDecimal.valueOf(totalTravelTime).setScale(1, RoundingMode.HALF_UP).doubleValue();

        // If user's wallet balance is insufficient, return a failure response.
        if (user.getWalletBalance().compareTo(BigDecimal.valueOf(roundedCost)) < 0) {
            ConfirmedBookingResponseDTO response = new ConfirmedBookingResponseDTO();
            response.setUserId(user.getId());
            response.setStatus("FAILED: Insufficient wallet balance");
            return response;
        }

        // Deduct the cost from the user's wallet.
        user.setWalletBalance(user.getWalletBalance().subtract(BigDecimal.valueOf(roundedCost)));
        userRepository.save(user);

        // For the booking, assign a default route. Here, we fetch the first available route.
        Route route = routeRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("No route available"));

        // Create and save the booking record.
        Booking booking = Booking.builder()
                .user(user)
                .shuttle(shuttle)
                .startStop(startStop)
                .endStop(endStop)
                .route(route)
                .bookingTime(LocalDateTime.now())
                .travelDate(LocalDate.now())
                .fare(BigDecimal.valueOf(roundedCost))
                .pointsDeducted(BigDecimal.valueOf(roundedCost))
                .status("CONFIRMED")
                .build();
        booking = bookingRepository.save(booking);

        // Prepare the response.
        ConfirmedBookingResponseDTO response = new ConfirmedBookingResponseDTO();
        response.setBookingId(booking.getId());
        response.setUserId(user.getId());
        response.setShuttleId(shuttle.getId());
        response.setShuttleVehicleNumber(shuttle.getVehicleNumber());
        response.setCost(roundedCost);
        response.setEstimatedTravelTime(roundedTravelTime);
        response.setBookingTime(booking.getBookingTime());
        response.setStatus(booking.getStatus());
        return response;
    }

    @Override
//    public TripHistoryResponseDTO getTripHistory(Long userId) {
//        // Fetch all bookings for the given user.
//        List<Booking> bookings = bookingRepository.findByUserId(userId);
//        if (bookings.isEmpty()) {
//            throw new RuntimeException("No bookings found for user: " + userId);
//        }
//        // Map bookings to trip history DTOs.
//        List<TripHistoryDTO> tripHistory = bookings.stream().map(b -> {
//            TripHistoryDTO dto = new TripHistoryDTO();
//            dto.setBookingId(b.getId());
//            dto.setBookingTime(b.getBookingTime());
//            dto.setTravelDate(b.getTravelDate());
//            dto.setStartStopName(b.getStartStop().getName());
//            dto.setEndStopName(b.getEndStop().getName());
//            dto.setFare(b.getFare());
//            dto.setPointsDeducted(b.getPointsDeducted());
//            return dto;
//        }).collect(Collectors.toList());
//
//        // Group bookings by route to determine frequent routes.
//        Map<Route, Long> routeCount = bookings.stream()
//                .filter(b -> b.getRoute() != null)
//                .collect(Collectors.groupingBy(Booking::getRoute, Collectors.counting()));
//
//        // Create frequent route suggestions DTOs (limit to top 3).
//        List<FrequentRouteSuggestionDTO> frequentRoutes = routeCount.entrySet().stream()
//                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
//                .limit(3)
//                .map(entry -> {
//                    FrequentRouteSuggestionDTO dto = new FrequentRouteSuggestionDTO();
//                    dto.setRouteId(entry.getKey().getId());
//                    dto.setRouteName(entry.getKey().getName());
//                    dto.setBookingCount(entry.getValue());
//                    return dto;
//                })
//                .collect(Collectors.toList());
//
//        TripHistoryResponseDTO response = new TripHistoryResponseDTO();
//        response.setTripHistory(tripHistory);
//        response.setFrequentRoutes(frequentRoutes);
//        return response;
//    }



    public TripHistoryResponseDTO getTripHistory(Long userId) {
        // Fetch all bookings for the given user.
        List<Booking> bookings = bookingRepository.findByUserId(userId);

        // Map bookings to trip history DTOs (this will be an empty list if no bookings found)
        List<TripHistoryDTO> tripHistory = bookings.stream().map(b -> {
            TripHistoryDTO dto = new TripHistoryDTO();
            dto.setBookingId(b.getId());
            dto.setBookingTime(b.getBookingTime());
            dto.setTravelDate(b.getTravelDate());
            dto.setStartStopName(b.getStartStop().getName());
            dto.setEndStopName(b.getEndStop().getName());
            dto.setFare(b.getFare());
            dto.setPointsDeducted(b.getPointsDeducted());
            return dto;
        }).collect(Collectors.toList());

        // Group bookings by route to determine frequent routes.
        Map<Route, Long> routeCount = bookings.stream()
                .filter(b -> b.getRoute() != null)
                .collect(Collectors.groupingBy(Booking::getRoute, Collectors.counting()));

        List<FrequentRouteSuggestionDTO> frequentRoutes = routeCount.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(3)
                .map(entry -> {
                    FrequentRouteSuggestionDTO dto = new FrequentRouteSuggestionDTO();
                    dto.setRouteId(entry.getKey().getId());
                    dto.setRouteName(entry.getKey().getName());
                    dto.setBookingCount(entry.getValue());
                    return dto;
                })
                .collect(Collectors.toList());

        TripHistoryResponseDTO response = new TripHistoryResponseDTO();
        response.setTripHistory(tripHistory);
        response.setFrequentRoutes(frequentRoutes);
        return response;
    }


    // Helper method: Calculate distance using the Haversine formula (returns distance in km).
    private double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int R = 6371; // Earth's radius in km.
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // Helper method: Find the nearest stop from a list of stops based on given latitude and longitude.
    private Stop findNearestStop(Double latitude, Double longitude, List<Stop> stops) {
        return stops.stream()
                .min((s1, s2) -> {
                    double d1 = calculateDistance(latitude, longitude, s1.getLatitude(), s1.getLongitude());
                    double d2 = calculateDistance(latitude, longitude, s2.getLatitude(), s2.getLongitude());
                    return Double.compare(d1, d2);
                })
                .orElseThrow(() -> new RuntimeException("No stops found"));
    }
}
