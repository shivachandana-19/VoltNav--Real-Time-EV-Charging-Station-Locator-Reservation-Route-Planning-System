package com.voltnav.repository;

import com.voltnav.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByCityIdOrderByNameAsc(Long cityId);
    List<Location> findByNameContainingIgnoreCaseOrAddressContainingIgnoreCase(String name, String address);
}
