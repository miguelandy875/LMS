package com.lms.service;

import com.lms.dao.*;
import com.lms.model.*;
import com.lms.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ReservationService {
    private ReservationDAO reservationDAO;
    private SeatDAO seatDAO;
    private SeatReservationDAO seatReservationDAO;
    private BookDAO bookDAO;
    private UserDAO userDAO;
    
    // Configuration constants
    private static final int DEFAULT_RESERVATION_HOURS = 72; // 3 days
    private static final int MAX_RESERVATIONS_PER_USER = 3;
    private static final int MAX_SEAT_RESERVATION_HOURS = 4;
    
    public ReservationService() {
        this.reservationDAO = new ReservationDAO();
        this.seatDAO = new SeatDAO();
        this.seatReservationDAO = new SeatReservationDAO();
        this.bookDAO = new BookDAO();
        this.userDAO = new UserDAO();
    }
    
    // ========== BOOK RESERVATION METHODS ==========
    
    public Reservation createBookReservation(int userId, int bookId) throws ServiceException {
        Connection connection = null;
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            
            // Validate user exists and is active member
            User member = userDAO.findById(userId);
            if (member == null || !"MEMBER".equals(member.getRole()) || !"ACTIVE".equals(member.getStatus())) {
                throw new ServiceException("Invalid or inactive member");
            }
            
            // Validate book exists
            Book book = bookDAO.findById(bookId);
            if (book == null) {
                throw new ServiceException("Book not found");
            }
            
            // Check if book is available or if user already has active reservation
            if ("AVAILABLE".equals(book.getStatus())) {
                throw new ServiceException("Book is currently available. Please borrow it directly instead of making a reservation.");
            }
            
            if (reservationDAO.hasActiveReservation(userId, bookId)) {
                throw new ServiceException("You already have an active reservation for this book");
            }
            
            // Check reservation limit
            List<Reservation> userReservations = reservationDAO.findReservationsByUser(userId);
            long activeReservations = userReservations.stream().filter(Reservation::isActive).count();
            if (activeReservations >= MAX_RESERVATIONS_PER_USER) {
                throw new ServiceException("Maximum reservation limit reached (" + MAX_RESERVATIONS_PER_USER + " books)");
            }
            
                       // Create reservation
            Reservation reservation = new Reservation();
            reservation.setUserId(userId);
            reservation.setBookId(bookId);
            reservation.setReservationDate(LocalDateTime.now());
            reservation.setExpiryDate(LocalDateTime.now().plusHours(DEFAULT_RESERVATION_HOURS));
            reservation.setStatus("ACTIVE");
            
            reservationDAO.save(reservation);
            
            // Update book status to RESERVED if it was ISSUED
            if ("ISSUED".equals(book.getStatus())) {
                book.setStatus("RESERVED");
                bookDAO.update(book);
            }
            
            connection.commit();
            
            return reservationDAO.findById(reservation.getReservationId());
            
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    throw new ServiceException("Transaction rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw new ServiceException("Database error during reservation creation: " + e.getMessage());
        } catch (ServiceException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    throw new ServiceException("Transaction rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw e;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
    
    public void cancelBookReservation(int reservationId) throws ServiceException {
        Connection connection = null;
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            
            Reservation reservation = reservationDAO.findById(reservationId);
            if (reservation == null) {
                throw new ServiceException("Reservation not found");
            }
            
            if (!"ACTIVE".equals(reservation.getStatus())) {
                throw new ServiceException("Cannot cancel a non-active reservation");
            }
            
            // Update reservation status
            reservation.setStatus("CANCELLED");
            reservationDAO.update(reservation);
            
            // Check if we need to update book status
            Book book = bookDAO.findById(reservation.getBookId());
            if (book != null && "RESERVED".equals(book.getStatus())) {
                // Check if there are other active reservations for this book
                List<Reservation> bookReservations = reservationDAO.findReservationsByBook(reservation.getBookId());
                boolean hasOtherActiveReservations = bookReservations.stream()
                    .anyMatch(r -> r.getReservationId() != reservationId && r.isActive());
                
                if (!hasOtherActiveReservations) {
                    book.setStatus("ISSUED"); // Back to issued status
                    bookDAO.update(book);
                }
            }
            
            connection.commit();
            
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    throw new ServiceException("Transaction rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw new ServiceException("Database error during reservation cancellation: " + e.getMessage());
        } catch (ServiceException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    throw new ServiceException("Transaction rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw e;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
    
    public void fulfillBookReservation(int reservationId) throws ServiceException {
        Connection connection = null;
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            
            Reservation reservation = reservationDAO.findById(reservationId);
            if (reservation == null) {
                throw new ServiceException("Reservation not found");
            }
            
            if (!"ACTIVE".equals(reservation.getStatus())) {
                throw new ServiceException("Cannot fulfill a non-active reservation");
            }
            
            // Update reservation status
            reservation.setStatus("FULFILLED");
            reservationDAO.update(reservation);
            
            // Book status will be updated when the loan is actually issued
            
            connection.commit();
            
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    throw new ServiceException("Transaction rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw new ServiceException("Database error during reservation fulfillment: " + e.getMessage());
        } catch (ServiceException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    throw new ServiceException("Transaction rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw e;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    // ========== SEAT RESERVATION METHODS ==========
    
    public SeatReservation createSeatReservation(int userId, int seatId, LocalDateTime startTime, LocalDateTime endTime) throws ServiceException {
        Connection connection = null;
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            
            // Validate user exists and is active member
            User member = userDAO.findById(userId);
            if (member == null || !"MEMBER".equals(member.getRole()) || !"ACTIVE".equals(member.getStatus())) {
                throw new ServiceException("Invalid or inactive member");
            }
            
            // Validate seat exists and is available
            Seat seat = seatDAO.findById(seatId);
            if (seat == null) {
                throw new ServiceException("Seat not found");
            }
            
            if (!"AVAILABLE".equals(seat.getSeatStatus())) {
                throw new ServiceException("Seat is not available");
            }
            
            // Validate time range
            if (startTime.isBefore(LocalDateTime.now())) {
                throw new ServiceException("Cannot reserve seat for past time");
            }
            
            if (endTime.isBefore(startTime)) {
                throw new ServiceException("End time must be after start time");
            }
            
            long hours = java.time.temporal.ChronoUnit.HOURS.between(startTime, endTime);
            if (hours > MAX_SEAT_RESERVATION_HOURS) {
                throw new ServiceException("Maximum reservation duration is " + MAX_SEAT_RESERVATION_HOURS + " hours");
            }
            
            // Check if seat is available for the requested time slot
            if (!seatReservationDAO.isSeatAvailable(seatId, startTime, endTime)) {
                throw new ServiceException("Seat is not available for the requested time slot");
            }
            
            // Check if user already has a reservation for this time period
            List<SeatReservation> userReservations = seatReservationDAO.findReservationsByUser(userId);
            boolean hasConflict = userReservations.stream()
                .filter(SeatReservation::isActive)
                .anyMatch(r -> (startTime.isBefore(r.getReserveEnd()) && endTime.isAfter(r.getReserveStart())));
            
            if (hasConflict) {
                throw new ServiceException("You already have a seat reservation during this time period");
            }
            
            // Create seat reservation
            SeatReservation reservation = new SeatReservation();
            reservation.setSeatId(seatId);
            reservation.setUserId(userId);
            reservation.setReserveStart(startTime);
            reservation.setReserveEnd(endTime);
            reservation.setStatus("ACTIVE");
            
            seatReservationDAO.save(reservation);
            
            connection.commit();
            
            return seatReservationDAO.findBySeatAndUser(seatId, userId);
            
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    throw new ServiceException("Transaction rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw new ServiceException("Database error during seat reservation: " + e.getMessage());
        } catch (ServiceException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    throw new ServiceException("Transaction rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw e;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
    
    public void cancelSeatReservation(int seatId, int userId) throws ServiceException {
        Connection connection = null;
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            
            SeatReservation reservation = seatReservationDAO.findBySeatAndUser(seatId, userId);
            if (reservation == null) {
                throw new ServiceException("Seat reservation not found");
            }
            
            if (!"ACTIVE".equals(reservation.getStatus())) {
                throw new ServiceException("Cannot cancel a non-active reservation");
            }
            
            // Update reservation status
            reservation.setStatus("CANCELLED");
            seatReservationDAO.update(reservation);
            
            connection.commit();
            
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    throw new ServiceException("Transaction rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw new ServiceException("Database error during seat reservation cancellation: " + e.getMessage());
        } catch (ServiceException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    throw new ServiceException("Transaction rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw e;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    // ========== QUERY METHODS ==========
    
    public List<Reservation> getAllBookReservations() {
        return reservationDAO.findAll();
    }
    
    public List<Reservation> getActiveBookReservations() {
        return reservationDAO.findActiveReservations();
    }
    
    public List<Reservation> getBookReservationsByUser(int userId) {
        return reservationDAO.findReservationsByUser(userId);
    }
    
    public List<Reservation> getBookReservationsByBook(int bookId) {
        return reservationDAO.findReservationsByBook(bookId);
    }
    
    public List<SeatReservation> getAllSeatReservations() {
        return seatReservationDAO.findAll();
    }
    
    public List<SeatReservation> getActiveSeatReservations() {
        return seatReservationDAO.findActiveReservations();
    }
    
    public List<SeatReservation> getSeatReservationsByUser(int userId) {
        return seatReservationDAO.findReservationsByUser(userId);
    }
    
    public List<SeatReservation> getSeatReservationsBySeat(int seatId) {
        return seatReservationDAO.findReservationsBySeat(seatId);
    }
    
    public List<Seat> getAllSeats() {
        return seatDAO.findAll();
    }
    
    public List<Seat> getAvailableSeats() {
        return seatDAO.findAvailableSeats();
    }
    
    public List<Seat> getSeatsByType(String type) {
        return seatDAO.findByType(type);
    }
    
    public ReservationStatistics getStatistics() {
        ReservationStatistics stats = new ReservationStatistics();
        
        // Book reservation statistics
        List<Reservation> allBookReservations = reservationDAO.findAll();
        stats.setTotalBookReservations(allBookReservations.size());
        stats.setActiveBookReservations((int) allBookReservations.stream().filter(Reservation::isActive).count());
        stats.setExpiredBookReservations((int) allBookReservations.stream().filter(r -> "EXPIRED".equals(r.getStatus()) || r.isExpired()).count());
        stats.setFulfilledBookReservations((int) allBookReservations.stream().filter(r -> "FULFILLED".equals(r.getStatus())).count());
        
        // Seat reservation statistics
        List<SeatReservation> allSeatReservations = seatReservationDAO.findAll();
        stats.setTotalSeatReservations(allSeatReservations.size());
        stats.setActiveSeatReservations((int) allSeatReservations.stream().filter(SeatReservation::isActive).count());
        stats.setOngoingSeatReservations((int) allSeatReservations.stream().filter(SeatReservation::isOngoing).count());
        stats.setUpcomingSeatReservations((int) allSeatReservations.stream().filter(SeatReservation::isUpcoming).count());
        
        // Seat statistics
        List<Seat> allSeats = seatDAO.findAll();
        stats.setTotalSeats(allSeats.size());
        stats.setAvailableSeats(seatDAO.countSeatsByStatus("AVAILABLE"));
        stats.setOccupiedSeats(seatDAO.countSeatsByStatus("OCCUPIED"));
        stats.setMaintenanceSeats(seatDAO.countSeatsByStatus("MAINTENANCE"));
        
        return stats;
    }
    
    public List<Reservation> searchBookReservations(String searchTerm, String status, int limit, int offset) {
        return reservationDAO.searchReservations(searchTerm, status, limit, offset);
    }
    
    public int countBookReservationSearchResults(String searchTerm, String status) {
        return reservationDAO.countSearchResults(searchTerm, status);
    }
    
    public List<SeatReservation> searchSeatReservations(String searchTerm, String status, int limit, int offset) {
        return seatReservationDAO.searchSeatReservations(searchTerm, status, limit, offset);
    }
    
    public int countSeatReservationSearchResults(String searchTerm, String status) {
        return seatReservationDAO.countSearchResults(searchTerm, status);
    }
    
    // ========== UTILITY METHODS ==========
    
    public void expireOldReservations() {
        List<Reservation> allReservations = reservationDAO.findAll();
        for (Reservation reservation : allReservations) {
            if (reservation.isExpired() && "ACTIVE".equals(reservation.getStatus())) {
                try {
                    reservation.setStatus("EXPIRED");
                    reservationDAO.update(reservation);
                } catch (Exception e) {
                    System.err.println("Error updating expired reservation: " + e.getMessage());
                }
            }
        }
    }
    
    public boolean canReserveBook(int userId, int bookId) {
        try {
            User member = userDAO.findById(userId);
            if (member == null || !"MEMBER".equals(member.getRole()) || !"ACTIVE".equals(member.getStatus())) {
                return false;
            }
            
            Book book = bookDAO.findById(bookId);
            if (book == null || "AVAILABLE".equals(book.getStatus())) {
                return false;
            }
            
            if (reservationDAO.hasActiveReservation(userId, bookId)) {
                return false;
            }
            
            List<Reservation> userReservations = reservationDAO.findReservationsByUser(userId);
            long activeReservations = userReservations.stream().filter(Reservation::isActive).count();
            
            return activeReservations < MAX_RESERVATIONS_PER_USER;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean canReserveSeat(int userId, int seatId, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            User member = userDAO.findById(userId);
            if (member == null || !"MEMBER".equals(member.getRole()) || !"ACTIVE".equals(member.getStatus())) {
                return false;
            }
            
            Seat seat = seatDAO.findById(seatId);
            if (seat == null || !"AVAILABLE".equals(seat.getSeatStatus())) {
                return false;
            }
            
            if (startTime.isBefore(LocalDateTime.now()) || endTime.isBefore(startTime)) {
                return false;
            }
            
            long hours = java.time.temporal.ChronoUnit.HOURS.between(startTime, endTime);
            if (hours > MAX_SEAT_RESERVATION_HOURS) {
                return false;
            }
            
            if (!seatReservationDAO.isSeatAvailable(seatId, startTime, endTime)) {
                return false;
            }
            
            List<SeatReservation> userReservations = seatReservationDAO.findReservationsByUser(userId);
            boolean hasConflict = userReservations.stream()
                .filter(SeatReservation::isActive)
                .anyMatch(r -> (startTime.isBefore(r.getReserveEnd()) && endTime.isAfter(r.getReserveStart())));
            
            return !hasConflict;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Configuration getters
    public static int getDefaultReservationHours() {
        return DEFAULT_RESERVATION_HOURS;
    }
    
    public static int getMaxReservationsPerUser() {
        return MAX_RESERVATIONS_PER_USER;
    }
    
    public static int getMaxSeatReservationHours() {
        return MAX_SEAT_RESERVATION_HOURS;
    }
}