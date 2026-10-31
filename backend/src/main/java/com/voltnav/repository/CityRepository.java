package com.voltnav.repository;

import com.voltnav.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    Optional<City> findByNameIgnoreCase(String name);
    Boolean existsByNameIgnoreCase(String name);
    List<City> findByNameContainingIgnoreCaseOrStateContainingIgnoreCase(String name, String state);
    List<City> findAllByOrderByNameAsc();
}
