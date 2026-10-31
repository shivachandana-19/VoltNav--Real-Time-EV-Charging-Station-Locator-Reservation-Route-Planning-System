package com.voltnav.config;

import com.voltnav.entity.*;
import com.voltnav.repository.*;
import com.voltnav.util.QRCodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final LocationRepository locationRepository;
    private final ChargingStationRepository stationRepository;
    private final ChargingSlotRepository slotRepository;
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AdminRepository adminRepository,
                           UserRepository userRepository,
                           CityRepository cityRepository,
                           LocationRepository locationRepository,
                           ChargingStationRepository stationRepository,
                           ChargingSlotRepository slotRepository,
                           BookingRepository bookingRepository,
                           PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.cityRepository = cityRepository;
        this.locationRepository = locationRepository;
        this.stationRepository = stationRepository;
        this.slotRepository = slotRepository;
        this.bookingRepository = bookingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (adminRepository.count() > 0) {
            log.info("Database already initialized. Skipping seeding.");
            return;
        }

        log.info("Starting initial seed data population for VoltNav...");

        // 1. Seed Admin
        Admin admin = new Admin();
        admin.setName("VoltNav Admin");
        admin.setEmail("admin@voltnav.com");
        admin.setPassword(passwordEncoder.encode("Admin@123"));
        admin.setPhone("+91 9876543210");
        admin.setRole(Role.ROLE_ADMIN);
        adminRepository.save(admin);

        // 2. Seed Users
        User user1 = new User("John Doe", "user@voltnav.com", passwordEncoder.encode("User@123"), "+91 9811223344");
        User user2 = new User("Rahul Sharma", "rahul@gmail.com", passwordEncoder.encode("Rahul@123"), "+91 9822334455");
        User user3 = new User("Priya Patel", "priya@gmail.com", passwordEncoder.encode("Priya@123"), "+91 9833445566");
        userRepository.saveAll(List.of(user1, user2, user3));

        // 3. Seed Cities
        City mumbai = new City("Mumbai", "Maharashtra", "India", "Financial capital of India with comprehensive EV charging network", 19.0760, 72.8777, "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?w=600");
        City delhi = new City("New Delhi", "Delhi NCR", "India", "Capital hub with dense DC ultra-fast EV charging infrastructure", 28.6139, 77.2090, "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=600");
        City bengaluru = new City("Bengaluru", "Karnataka", "India", "Silicon Valley of India, pioneering EV innovation and smart chargers", 12.9716, 77.5946, "https://images.unsplash.com/photo-1596176530529-78163a4f7af2?w=600");
        City hyderabad = new City("Hyderabad", "Telangana", "India", "Major tech corridor with high-speed highway and city EV stations", 17.3850, 78.4867, "https://images.unsplash.com/photo-1605007493699-ce65834f8a00?w=600");
        City pune = new City("Pune", "Maharashtra", "India", "Automobile and tech hub with expansive green charging corridors", 18.5204, 73.8567, "https://images.unsplash.com/photo-1592635196078-9fe3d55f233d?w=600");
        cityRepository.saveAll(List.of(mumbai, delhi, bengaluru, hyderabad, pune));

        // 4. Seed Locations
        // Mumbai
        Location bkc = new Location(mumbai, "Bandra Kurla Complex (BKC)", "G Block, BKC, Bandra East, Mumbai", 19.0657, 72.8687, "400051");
        Location lowerParel = new Location(mumbai, "Lower Parel", "Senapati Bapat Marg, Lower Parel, Mumbai", 18.9950, 72.8258, "400013");
        Location andheri = new Location(mumbai, "Andheri West", "Link Road, Andheri West, Mumbai", 19.1363, 72.8277, "400053");

        // Delhi
        Location cp = new Location(delhi, "Connaught Place", "Radial Road 1, Inner Circle, Connaught Place, New Delhi", 28.6315, 77.2167, "110001");
        Location aerocity = new Location(delhi, "Indira Gandhi Airport Aerocity", "Hospitality District, Aerocity, New Delhi", 28.5494, 77.1215, "110037");
        Location saket = new Location(delhi, "Saket District Centre", "Press Enclave Marg, Saket, New Delhi", 28.5244, 77.2177, "110017");

        // Bengaluru
        Location indiranagar = new Location(bengaluru, "Indiranagar", "100 Feet Road, Indiranagar, Bengaluru", 12.9784, 77.6408, "560038");
        Location whitefield = new Location(bengaluru, "Whitefield Tech Hub", "ITPB Road, Whitefield, Bengaluru", 12.9856, 77.7314, "560066");
        Location koramangala = new Location(bengaluru, "Koramangala", "80 Feet Road, 4th Block, Koramangala, Bengaluru", 12.9352, 77.6245, "560034");

        // Hyderabad
        Location hitecCity = new Location(hyderabad, "Hitec City Cyber Towers", "Madhapur, Hitec City, Hyderabad", 17.4504, 78.3809, "500081");
        Location gachibowli = new Location(hyderabad, "Gachibowli Financial District", "ISB Road, Financial District, Gachibowli, Hyderabad", 17.4334, 78.3440, "500032");

        // Pune
        Location hinjewadi = new Location(pune, "Hinjewadi Infotech Park", "Phase 1, Hinjewadi, Pune", 18.5913, 73.7389, "411057");
        Location vimanNagar = new Location(pune, "Viman Nagar", "Symbiosis Road, Viman Nagar, Pune", 18.5679, 73.9143, "411014");

        locationRepository.saveAll(List.of(
                bkc, lowerParel, andheri, cp, aerocity, saket, indiranagar, whitefield, koramangala,
                hitecCity, gachibowli, hinjewadi, vimanNagar
        ));

        // 5. Seed Charging Stations
        List<ChargingStation> stations = new ArrayList<>();

        // Mumbai Stations
        stations.add(new ChargingStation(bkc, "Tata Power EZ Charge - BKC Hub", "Plot C-22, G Block, BKC, Bandra East", 19.0665, 72.8695, 6, 4, "CCS2, Type 2 AC", "60 kW DC Fast", "24/7", StationStatus.ACTIVE, 18.50, "+91 22 6665 8282"));
        stations.add(new ChargingStation(bkc, "Jio-bp pulse Mega Charging Station", "One BKC Parking Bay, G Block, BKC", 19.0640, 72.8660, 8, 7, "CCS2, CHAdeMO, Type 2", "150 kW Ultra Fast", "24/7", StationStatus.ACTIVE, 19.50, "+91 1800 891 9000"));
        stations.add(new ChargingStation(lowerParel, "ChargeZone High Street Phoenix Hub", "Phoenix Palladium Basement 2, Lower Parel", 18.9942, 72.8249, 4, 3, "CCS2, Type 2", "50 kW DC Fast", "08:00 AM - 11:30 PM", StationStatus.ACTIVE, 17.80, "+91 8000 123 456"));
        stations.add(new ChargingStation(andheri, "Ather Grid & Zeon Fast Charge - Infinity Mall", "Infinity Mall Parking, New Link Rd, Andheri West", 19.1412, 72.8310, 4, 4, "CCS2, Type 2, GB/T", "50 kW DC Fast", "24/7", StationStatus.ACTIVE, 18.00, "+91 9820 998 877"));

        // Delhi Stations
        stations.add(new ChargingStation(cp, "Tata Power Supercharge CP Inner Circle", "Block B Inner Circle, Connaught Place", 28.6328, 77.2195, 6, 5, "CCS2, Type 2", "120 kW Ultra Fast", "24/7", StationStatus.ACTIVE, 19.00, "+91 11 2334 1122"));
        stations.add(new ChargingStation(aerocity, "Fortum Charge & Drive Worldmark Aerocity", "Worldmark 2 Ground Lot, Aerocity", 28.5510, 77.1230, 8, 6, "CCS2, CHAdeMO, Type 2", "150 kW Ultra Fast", "24/7", StationStatus.ACTIVE, 20.00, "+91 8000 456 789"));
        stations.add(new ChargingStation(saket, "Shell Recharge Select Citywalk", "Select Citywalk Mall South Entry, Saket", 28.5280, 77.2190, 4, 3, "CCS2, Type 2", "60 kW DC Fast", "09:00 AM - 11:00 PM", StationStatus.ACTIVE, 18.50, "+91 11 4059 9999"));

        // Bengaluru Stations
        stations.add(new ChargingStation(indiranagar, "Zeon Fast Charging - 100ft Road", "Near Metro Station, 100 Feet Rd, Indiranagar", 12.9790, 77.6415, 6, 5, "CCS2, Type 2", "60 kW DC Fast", "24/7", StationStatus.ACTIVE, 18.25, "+91 80 4123 5566"));
        stations.add(new ChargingStation(whitefield, "Jio-bp pulse ITPL Superhub", "ITPL Main Gate Visitor Bay, Whitefield", 12.9865, 77.7325, 8, 6, "CCS2, CHAdeMO", "150 kW Ultra Fast", "24/7", StationStatus.ACTIVE, 19.00, "+91 1800 891 9000"));
        stations.add(new ChargingStation(koramangala, "Tata Power EZ Charge Forum South Mall", "Hosur Main Road, Koramangala", 12.9340, 77.6230, 4, 4, "CCS2, Type 2", "50 kW DC Fast", "24/7", StationStatus.ACTIVE, 18.50, "+91 80 2200 3344"));

        // Hyderabad Stations
        stations.add(new ChargingStation(hitecCity, "ChargeZone Cyber Gateway Hub", "Cyber Gateway Main Plaza, Hitec City", 17.4490, 78.3795, 6, 5, "CCS2, Type 2", "60 kW DC Fast", "24/7", StationStatus.ACTIVE, 18.00, "+91 40 2311 0099"));
        stations.add(new ChargingStation(gachibowli, "Tata Power EZ Charge WaveRock", "WaveRock SEZ South Gate, Financial District", 17.4320, 78.3425, 6, 5, "CCS2, Type 2", "120 kW Ultra Fast", "24/7", StationStatus.ACTIVE, 18.75, "+91 40 6655 4433"));

        // Pune Stations
        stations.add(new ChargingStation(hinjewadi, "Zeon Charging Hinjewadi Phase 1", "Blue Ridge Township Gate 2, Hinjewadi", 18.5905, 73.7375, 6, 4, "CCS2, Type 2", "60 kW DC Fast", "24/7", StationStatus.ACTIVE, 18.00, "+91 20 6711 2233"));
        stations.add(new ChargingStation(vimanNagar, "Jio-bp pulse Phoenix Marketcity", "Phoenix Marketcity Parking Level P1, Viman Nagar", 18.5685, 73.9150, 6, 5, "CCS2, Type 2", "120 kW Ultra Fast", "24/7", StationStatus.ACTIVE, 19.00, "+91 1800 891 9000"));

        stationRepository.saveAll(stations);

        // 6. Seed Charging Slots (Today and Tomorrow)
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        List<ChargingSlot> allSlots = new ArrayList<>();

        LocalTime[] slotStarts = {
                LocalTime.of(8, 0), LocalTime.of(9, 30), LocalTime.of(11, 0),
                LocalTime.of(13, 0), LocalTime.of(14, 30), LocalTime.of(16, 0),
                LocalTime.of(18, 0), LocalTime.of(20, 0)
        };

        for (ChargingStation station : stations) {
            // Generate slots for today
            for (int i = 0; i < slotStarts.length; i++) {
                LocalTime start = slotStarts[i];
                LocalTime end = start.plusMinutes(75);
                ChargingSlot slot1 = new ChargingSlot(station, "Charger Bay 1 (" + station.getChargerType().split(",")[0].trim() + ")", today, start, end, SlotStatus.AVAILABLE, 250.0);
                ChargingSlot slot2 = new ChargingSlot(station, "Charger Bay 2 (" + station.getChargerType().split(",")[0].trim() + ")", today, start, end, SlotStatus.AVAILABLE, 250.0);
                allSlots.add(slot1);
                allSlots.add(slot2);
            }
            // Generate slots for tomorrow
            for (int i = 0; i < slotStarts.length; i++) {
                LocalTime start = slotStarts[i];
                LocalTime end = start.plusMinutes(75);
                ChargingSlot slot1 = new ChargingSlot(station, "Charger Bay 1 (" + station.getChargerType().split(",")[0].trim() + ")", tomorrow, start, end, SlotStatus.AVAILABLE, 250.0);
                ChargingSlot slot2 = new ChargingSlot(station, "Charger Bay 2 (" + station.getChargerType().split(",")[0].trim() + ")", tomorrow, start, end, SlotStatus.AVAILABLE, 250.0);
                allSlots.add(slot1);
                allSlots.add(slot2);
            }
        }

        List<ChargingSlot> savedSlots = slotRepository.saveAll(allSlots);

        // 7. Seed Sample Confirmed Bookings with QR Codes
        if (!savedSlots.isEmpty()) {
            ChargingSlot bookedSlot1 = savedSlots.get(0);
            bookedSlot1.setStatus(SlotStatus.BOOKED);
            slotRepository.save(bookedSlot1);

            String bookingId1 = "VN-20261031-A189";
            String qrToken1 = "VNQR-SEED-A1B2C3D4E5F6G7H8";
            String qrPayload1 = String.format("{\"app\":\"VoltNav\",\"bookingId\":\"%s\",\"token\":\"%s\",\"station\":\"%s\",\"date\":\"%s\",\"time\":\"%s - %s\"}",
                    bookingId1, qrToken1, bookedSlot1.getStation().getName(), bookedSlot1.getSlotDate(), bookedSlot1.getStartTime(), bookedSlot1.getEndTime());
            String qrBase64_1 = QRCodeGenerator.generateQRCodeBase64(qrPayload1, 300, 300);

            Booking b1 = new Booking(
                    bookingId1, user1, bookedSlot1.getStation(), bookedSlot1,
                    bookedSlot1.getSlotDate(), bookedSlot1.getStartTime(), bookedSlot1.getEndTime(),
                    bookedSlot1.getPrice(), BookingStatus.CONFIRMED, qrToken1, qrBase64_1, "MH-02-EE-4501"
            );

            ChargingSlot bookedSlot2 = savedSlots.get(1);
            bookedSlot2.setStatus(SlotStatus.BOOKED);
            slotRepository.save(bookedSlot2);

            String bookingId2 = "VN-20261031-B942";
            String qrToken2 = "VNQR-SEED-9876543210ABCDEF";
            String qrPayload2 = String.format("{\"app\":\"VoltNav\",\"bookingId\":\"%s\",\"token\":\"%s\",\"station\":\"%s\",\"date\":\"%s\",\"time\":\"%s - %s\"}",
                    bookingId2, qrToken2, bookedSlot2.getStation().getName(), bookedSlot2.getSlotDate(), bookedSlot2.getStartTime(), bookedSlot2.getEndTime());
            String qrBase64_2 = QRCodeGenerator.generateQRCodeBase64(qrPayload2, 300, 300);

            Booking b2 = new Booking(
                    bookingId2, user2, bookedSlot2.getStation(), bookedSlot2,
                    bookedSlot2.getSlotDate(), bookedSlot2.getStartTime(), bookedSlot2.getEndTime(),
                    bookedSlot2.getPrice(), BookingStatus.CONFIRMED, qrToken2, qrBase64_2, "KA-01-EV-2024"
            );

            bookingRepository.saveAll(List.of(b1, b2));
        }

        log.info("VoltNav Database seeded successfully!");
        log.info("Default Admin: admin@voltnav.com / Admin@123");
        log.info("Default User:  user@voltnav.com / User@123");
    }
}
