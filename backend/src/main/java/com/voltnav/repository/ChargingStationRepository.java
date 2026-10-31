package com.voltnav.repository;

import com.voltnav.entity.ChargingStation;
import com.voltnav.entity.StationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChargingStationRepository extends JpaRepository<ChargingStation, Long> {
    List<ChargingStation> findByLocationId(Long locationId);
    List<ChargingStation> findByLocationCityId(Long cityId);
    List<ChargingStation> findByLocationCityIdAndStatus(Long cityId, StationStatus status);
    List<ChargingStation> findByStatus(StationStatus status);
    long countByStatus(StationStatus status);

    @Query("SELECT s FROM ChargingStation s WHERE " +
           "(:cityId IS NULL OR s.location.city.id = :cityId) AND " +
           "(:locationId IS NULL OR s.location.id = :locationId) AND " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:query IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.chargerType) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.chargingSpeed) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<ChargingStation> searchStations(@Param("cityId") Long cityId,
                                         @Param("locationId") Long locationId,
                                         @Param("status") StationStatus status,
                                         @Param("query") String query);
}
