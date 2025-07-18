# Shuttle Management System

A smart campus transit solution that streamlines shuttle operations and provides students with a seamless way to book rides, check wallet balances, view trip history, and receive optimal route suggestions based on their travel patterns.

---

## Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [Project Structure](#project-structure)
- [API Endpoints and Sample JSON](#api-endpoints-and-sample-json)
- [Database Seeding](#database-seeding)
- [Getting Started](#getting-started)
- [Demo](#demo)
- [License](#license)

---

## Features

- **User Registration & Login:**  
  - Students register using their university email (ending with `@lpu.in`).  
  - Every new student is given a default wallet balance of 100 rupees.
- **Wallet Management:**  
  - Students can view their wallet balance.  
  - Only an admin can recharge a student's wallet.
- **Shuttle Booking:**  
  - **Get Available Shuttle Options:** The system calculates and displays the top 4 shuttle options based on the student's current location and destination.
  - **Confirm Booking:** Upon selection, the system deducts the corresponding fare from the student's wallet and confirms the booking.
- **Trip History & Route Suggestions:**  
  - Students can view their past trip history, including ride date, start and end stops, fare, and points deducted.
  - The system analyzes past travel patterns to suggest optimal routes.

---

## Technologies

- **Backend:** Java, Spring Boot, Spring Data JPA, Hibernate
- **Database:** PostgreSQL
- **Frontend:** HTML, Bootstrap 4, jQuery
- **Build Tool:** Graddle

---

## Project Structure

```plaintext
ShuttleManagementSystem/
├── src/
│   ├── main/
│   │   ├── java/com/shuttle/SMS/
│   │   │   ├── config/                  # Configuration (e.g., security)
│   │   │   ├── controller/              # REST controllers (UserController, BookingController, WalletController, etc.)
│   │   │   ├── dto/                     # Data Transfer Objects (UserRegistrationDTO, LoginDTO, UserWalletResponseDTO, etc.)
│   │   │   ├── model/                   # JPA Entities (User, Booking, Route, Shuttle, Stop, etc.)
│   │   │   ├── repository/              # Spring Data JPA repositories
│   │   │   ├── service/                 # Business logic services (UserService, BookingService, RouteOptimizationService, etc.)
│   │   │   └── ShuttleManagementSystemApplication.java  # Main Spring Boot Application
│   ├── resources/
│   │   ├── application.properties       # Application configuration (DB connection, etc.)
│   │   ├── data.sql                     # SQL script for database seeding (sample data for stops, shuttles, routes, bookings, etc.)
│   │   └── static/
│   │       ├── index.html               # Frontend landing page and UI
│   │       └── index.js                 # Custom JavaScript for the UI
├── pom.xml                              # Maven configuration and dependencies
└── README.md                            # This file

```
# API Endpoints Overview

This document describes all the key API endpoints used in the Shuttle Management System project. It explains the purpose of each endpoint, the HTTP method used, and a brief description of its functionality.

---

## 1. User Registration

- **Endpoint:** `POST /api/auth/register`
- **Purpose:**  
  Registers a new user. The endpoint accepts user details such as full name, email, and password.  
- **Key Points:**  
  - The email must be a valid university email ending with `@lpu.in`.  
  - New users are automatically assigned a default wallet balance of 100 rupees.  
- **Usage:**  
  Send a POST request with user details in the request body.

---

## 2. User Login

- **Endpoint:** `POST /api/auth/login`
- **Purpose:**  
  Authenticates an existing user using their credentials.  
- **Key Points:**  
  - Requires user ID, email, and password.  
  - On success, returns user details (excluding the password) along with the current wallet balance.
- **Usage:**  
  Send a POST request with the user ID, email, and password in the request body.

---

## 3. Wallet Balance

- **Endpoint:** `GET /api/user/{userId}/wallet`
- **Purpose:**  
  Retrieves the wallet balance of the specified user.  
- **Key Points:**  
  - No request body is required; the user ID is included in the URL.  
  - Returns a JSON object with the user's ID and current balance.
- **Usage:**  
  Send a GET request by replacing `{userId}` with the actual user ID (e.g., `/api/user/6/wallet`).

---

## 4. Get Available Shuttle Options

- **Endpoint:** `POST /api/booking/options`
- **Purpose:**  
  Calculates and returns available shuttle options based on the student's current location and destination.  
- **Key Points:**  
  - The system determines the nearest pickup and destination stops.  
  - It calculates the distance, cost, and estimated travel time for each shuttle option.  
  - Up to four shuttle options are returned, sorted by cost.
- **Usage:**  
  Send a POST request with a JSON body containing user ID and the coordinates (current and destination).

---

## 5. Confirm Booking

- **Endpoint:** `POST /api/booking/confirm`
- **Purpose:**  
  Confirms a shuttle booking for the user.  
- **Key Points:**  
  - Accepts user details, current location, destination, and the selected shuttle's ID.  
  - The system verifies that the user has sufficient wallet funds and automatically deducts the fare upon confirmation.  
  - Returns the booking details if successful or a failure message if the wallet balance is insufficient.
- **Usage:**  
  Send a POST request with a JSON body containing user ID, current/destination coordinates, and the selected shuttle ID.

---

## 6. Trip History

- **Endpoint:** `GET /api/booking/history?userId={userId}`
- **Purpose:**  
  Retrieves the past trip history for the specified user.  
- **Key Points:**  
  - Returns a list of past bookings with details such as booking time, start and end stops, fare, and points deducted.  
  - May also include frequent route suggestions based on historical data.
- **Usage:**  
  Send a GET request with the user ID as a query parameter (e.g., `/api/booking/history?userId=6`).


---

This document should help you test and understand the API endpoints used in the Shuttle Management System. Use these explanations as a guide for how to interact with the system via tools like Postman or directly from your frontend.

