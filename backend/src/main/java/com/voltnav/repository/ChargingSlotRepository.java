package com.voltnav.repository;

import com.voltnav.entity.ChargingSlot;
import com.voltnav.entity.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ChargingSlotRepository extends JpaRepository<ChargingSlot, Long> {
    List<ChargingSlot> findByStationIdAndSlotDateOrderByStartTimeAsc(Long stationId, LocalDate slotDate);
    List<ChargingSlot> findByStationIdAndSlotDateAndStatusOrderByStartTimeAsc(Long stationId, LocalDate slotDate, SlotStatus status);
    List<ChargingSlot> findByStationIdOrderBySlotDateAscStartTimeAsc(Long stationId);
    long countByStatus(SlotStatus status);

    @Query("SELECT COUNT(s) FROM ChargingSlot s WHERE s.station.id = :stationId AND s.status = :status")
    long countByStationIdAndStatus(@Param("stationId") Long stationId, @Param("status") SlotStatus status);
}
