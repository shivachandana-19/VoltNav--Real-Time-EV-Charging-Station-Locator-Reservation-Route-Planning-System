package com.voltnav.repository;

import com.voltnav.entity.Booking;
import com.voltnav.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingId(String bookingId);
    Optional<Booking> findByQrToken(String qrToken);
    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Booking> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, BookingStatus status);
    List<Booking> findAllByOrderByCreatedAtDesc();
    long countByStatus(BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE " +
           "(:status IS NULL OR b.status = :status) AND " +
           "(:cityId IS NULL OR b.station.location.city.id = :cityId) AND " +
           "(:stationId IS NULL OR b.station.id = :stationId) AND " +
           "(:bookingDate IS NULL OR b.bookingDate = :bookingDate) AND " +
           "(:query IS NULL OR LOWER(b.bookingId) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.user.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.user.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.vehicleNumber) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY b.createdAt DESC")
    List<Booking> searchBookings(@Param("status") BookingStatus status,
                                 @Param("cityId") Long cityId,
                                 @Param("stationId") Long stationId,
                                 @Param("bookingDate") LocalDate bookingDate,
                                 @Param("query") String query);

    @Query("SELECT b.bookingDate, COUNT(b) FROM Booking b GROUP BY b.bookingDate ORDER BY b.bookingDate ASC")
    List<Object[]> countBookingsGroupedByDate();

    @Query("SELECT b.station.name, COUNT(b) FROM Booking b GROUP BY b.station.name ORDER BY COUNT(b) DESC")
    List<Object[]> countBookingsGroupedByStation();

    @Query("SELECT b.station.location.city.name, COUNT(b) FROM Booking b GROUP BY b.station.location.city.name ORDER BY COUNT(b) DESC")
    List<Object[]> countBookingsGroupedByCity();
}
