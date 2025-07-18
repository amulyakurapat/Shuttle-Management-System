package com.shuttle.SMS.service;

import com.shuttle.SMS.dto.BookingRequestDTO;
import com.shuttle.SMS.dto.RouteSuggestionDTO;
import com.shuttle.SMS.model.Stop;
import com.shuttle.SMS.repository.RouteStopRepository;
import com.shuttle.SMS.repository.StopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class RouteOptimizationService {

    private final StopRepository stopRepository;
    private final RouteStopRepository routeStopRepository; // Provides RouteStop data (route, stop, sequence)

    @Autowired
    public RouteOptimizationService(StopRepository stopRepository,
                                    RouteStopRepository routeStopRepository) {
        this.stopRepository = stopRepository;
        this.routeStopRepository = routeStopRepository;
    }

    /**
     * Computes the optimal predefined route for the given booking request.
     * In a real system, you would filter routes based on the current time slot (e.g. MORNING, EVENING, BREAK)
     * and adjust edge weights accordingly.
     */
    public RouteSuggestionDTO computeOptimalRoute(BookingRequestDTO request) {
        // Step 1: Determine the nearest pickup and destination stops.
        List<Stop> allStops = stopRepository.findAll();
        if (allStops.isEmpty()) {
            throw new RuntimeException("No stops available");
        }
        Stop pickupStop = findNearestStop(request.getCurrentLatitude(), request.getCurrentLongitude(), allStops);
        Stop destinationStop = findNearestStop(request.getDestinationLatitude(), request.getDestinationLongitude(), allStops);

        // Step 2: Build a graph from predefined routes.
        // For simplicity, assume all predefined routes are available.
        // In a real implementation, you would fetch all routes (or route segments) applicable for the current time.
        Graph graph = buildGraph(); // see below for a simple graph construction

        // Step 3: Use Dijkstra's algorithm to find the shortest path from pickupStop to destinationStop.
        List<Stop> optimalPath = graph.computeShortestPath(pickupStop, destinationStop);
        if (optimalPath.isEmpty()) {
            throw new RuntimeException("No optimal route found");
        }

        // Step 4: Calculate total cost and travel time using a cost function.
        // For demonstration, assume:
        // base cost per km = 3.0 and cost per minute = 0.5, with adjustments based on peak multipliers.
        double totalDistance = 0.0;
        for (int i = 0; i < optimalPath.size() - 1; i++) {
            totalDistance += calculateDistance(optimalPath.get(i), optimalPath.get(i+1));
        }
        double baseCost = totalDistance * 3.0;
        double baseTime = (totalDistance / 30.0) * 60; // assuming average speed 30 km/h
        // Apply a multiplier if it's peak time (example: 1.2x cost, 1.1x time)
        double peakCostMultiplier = isPeakTime(request) ? 1.2 : 1.0;
        double peakTimeMultiplier = isPeakTime(request) ? 1.1 : 1.0;
        double finalCost = baseCost * peakCostMultiplier;
        double finalTime = baseTime * peakTimeMultiplier;

        // Round the final values.
        finalCost = BigDecimal.valueOf(finalCost).setScale(1, RoundingMode.HALF_UP).doubleValue();
        finalTime = BigDecimal.valueOf(finalTime).setScale(1, RoundingMode.HALF_UP).doubleValue();

        // Create a RouteSuggestionDTO.
        RouteSuggestionDTO suggestion = new RouteSuggestionDTO();
        suggestion.setRouteId(null); // you could map a specific predefined route if needed
        suggestion.setRouteName("Optimal Route");
        List<String> stopNames = new ArrayList<>();
        for (Stop s : optimalPath) {
            stopNames.add(s.getName());
        }
        suggestion.setStops(stopNames);
        suggestion.setCost(finalCost);
        suggestion.setEstimatedTravelTime(finalTime);
        return suggestion;
    }

    // Example: determine if current time falls in peak hours.
    private boolean isPeakTime(BookingRequestDTO request) {
        // In real implementation, check current system time and/or use request's departure time.
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        // Assume peak hours in morning: 7-10 and evening: 16-19
        return (hour >= 7 && hour <= 10) || (hour >= 16 && hour <= 19);
    }

    // Helper to compute distance between two stops using the Haversine formula.
    private double calculateDistance(Stop s1, Stop s2) {
        return calculateDistance(s1.getLatitude(), s1.getLongitude(), s2.getLatitude(), s2.getLongitude());
    }

    private double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int R = 6371; // Earth radius in km.
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // Helper to find the nearest stop.
    private Stop findNearestStop(Double latitude, Double longitude, List<Stop> stops) {
        return stops.stream()
                .min((s1, s2) -> {
                    double d1 = calculateDistance(latitude, longitude, s1.getLatitude(), s1.getLongitude());
                    double d2 = calculateDistance(latitude, longitude, s2.getLatitude(), s2.getLongitude());
                    return Double.compare(d1, d2);
                })
                .orElseThrow(() -> new RuntimeException("No stops found"));
    }

    // A very simplified graph structure for demonstration.
    private Graph buildGraph() {
        Graph graph = new Graph();
        // For each stop, add a node.
        List<Stop> stops = stopRepository.findAll();
        for (Stop stop : stops) {
            graph.addNode(stop);
        }
        // For simplicity, connect stops that belong to any predefined route.
        // In a full implementation, you would load RouteStop data and add directed edges with weights.
        for (int i = 0; i < stops.size() - 1; i++) {
            Stop from = stops.get(i);
            Stop to = stops.get(i+1);
            double distance = calculateDistance(from.getLatitude(), from.getLongitude(), to.getLatitude(), to.getLongitude());
            // Use distance as the weight.
            graph.addEdge(from, to, distance);
            // Also add reverse edge if routes are bidirectional.
            graph.addEdge(to, from, distance);
        }
        return graph;
    }

    // Inner classes to represent a simple graph and perform Dijkstra's algorithm.
    private static class Graph {
        private final Map<Stop, List<Edge>> adjList = new HashMap<>();

        void addNode(Stop stop) {
            adjList.putIfAbsent(stop, new ArrayList<>());
        }

        void addEdge(Stop from, Stop to, double weight) {
            adjList.get(from).add(new Edge(from, to, weight));
        }

        // Compute shortest path from source to destination using Dijkstra's algorithm.
        List<Stop> computeShortestPath(Stop source, Stop destination) {
            Map<Stop, Double> distances = new HashMap<>();
            Map<Stop, Stop> prev = new HashMap<>();
            PriorityQueue<StopDistancePair> pq = new PriorityQueue<>(Comparator.comparingDouble(StopDistancePair::getDistance));

            for (Stop node : adjList.keySet()) {
                distances.put(node, Double.MAX_VALUE);
            }
            distances.put(source, 0.0);
            pq.offer(new StopDistancePair(source, 0.0));

            while (!pq.isEmpty()) {
                StopDistancePair pair = pq.poll();
                Stop current = pair.getStop();
                if (current.equals(destination)) {
                    break;
                }
                for (Edge edge : adjList.getOrDefault(current, new ArrayList<>())) {
                    double alt = distances.get(current) + edge.weight;
                    if (alt < distances.get(edge.to)) {
                        distances.put(edge.to, alt);
                        prev.put(edge.to, current);
                        pq.offer(new StopDistancePair(edge.to, alt));
                    }
                }
            }

            // Reconstruct path.
            List<Stop> path = new ArrayList<>();
            for (Stop at = destination; at != null; at = prev.get(at)) {
                path.add(at);
            }
            Collections.reverse(path);
            if (!path.isEmpty() && path.get(0).equals(source)) {
                return path;
            }
            return Collections.emptyList();
        }
    }

    private static class Edge {
        Stop from;
        Stop to;
        double weight;

        public Edge(Stop from, Stop to, double weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    private static class StopDistancePair {
        private final Stop stop;
        private final double distance;

        public StopDistancePair(Stop stop, double distance) {
            this.stop = stop;
            this.distance = distance;
        }

        public Stop getStop() {
            return stop;
        }

        public double getDistance() {
            return distance;
        }
    }
}
