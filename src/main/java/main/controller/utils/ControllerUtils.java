package main.controller.utils;

import main.dto.*;
import main.model.*;
import main.repository.TextTypeEntityRepository;
import main.service.*;
import org.apache.commons.codec.language.bm.Lang;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import javax.xml.soap.Text;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


@Component
public class ControllerUtils {


    private ControllerUtils() {

    }
    /**
     * @param users
     * @return
     * @Author GXM
     * Metodele de map sunt folosite pentru a transforma entitatea de User in UserDTO, care este trimis spre front-end, pentru a fi
     * prelucrat.
     */

    public static List<UserDTO> mapUserListToUserDtoList(List<User> users) {
        List<UserDTO> userDTOS = new ArrayList<>();
        for (User user : users) {
            UserDTO userDTO = new UserDTO();

            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setPassword(user.getPassword());
            userDTO.setRoleName(user.getRole().getName());

            userDTOS.add(userDTO);
        }
        return userDTOS;
    }

    public static UserDTO mapUserToUserDto(User user) {
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        userDTO.setRoleName(user.getRole().getName());

        return userDTO;
    }
    public static VoucherDTO mapVoucherToVoucherDTO(Voucher voucher) {
        VoucherDTO voucherDTO = new VoucherDTO();

        voucherDTO.setId(voucher.getId());
        voucherDTO.setCode(voucher.getCode());
        voucherDTO.setDiscountPercentage(voucher.getDiscountPercentage());
        voucherDTO.setVoucherUser(voucher.getPerson());

        return voucherDTO;
    }
    public static FlightDTO mapFlightToFlightDTO(Flight flight, TextTypeEntityRepository textTypeEntityRepository, ILocationTranslationService locationTranslationService, Language language) {
        FlightDTO flightDTO = new FlightDTO();

        Location departureLocation = flight.getRoute().getDepartureAirport().getLocation();
        Location arrivalLocation = flight.getRoute().getArrivalAirport().getLocation();

        List<Location> locationsList = new ArrayList<>();
        List<LocationDTO> translatedLocations;

        locationsList.add(departureLocation);
        locationsList.add(arrivalLocation);

        translatedLocations = mapLocationListToLocationDtoList(textTypeEntityRepository, locationTranslationService, language, locationsList);


        flightDTO.setId(flight.getId());
        flightDTO.setName(flight.getName());
        flightDTO.setBusinessSeats(flight.getBusinessSeats());
        flightDTO.setEconomySeats(flight.getEconomySeats());
        flightDTO.setFirstClassSeats(flight.getFirstClassSeats());
        flightDTO.setCompanyName(flight.getCompany().getName());
        flightDTO.setArrivalDate(flight.getArrivalDate());
        flightDTO.setDepartureDate(flight.getDepartureDate());
        flightDTO.setRouteId(flight.getRoute().getId());
        flightDTO.setCompanyId(flight.getCompany().getId());
        flightDTO.setRouteName(flight.getRoute().getArrivalAirport().getName(), flight.getRoute().getDepartureAirport().getName());
        flightDTO.setRemainingBusinessSeats(flight.getRemainingBusinessSeats());
        flightDTO.setRemainingEconomySeats(flight.getRemainingEconomySeats());
        flightDTO.setRemainingFirstClassSeats(flight.getRemainingFirstClassSeats());
        flightDTO.setDuration(ControllerUtils.formatDuration(flight.getDuration()));
        flightDTO.setArrivalCity(translatedLocations.get(1).getCity());
        flightDTO.setDepartureCity(translatedLocations.get(0).getCity());
        flightDTO.setBusinessPrice(flight.getBusinessPrice());
        flightDTO.setEconomyPrice(flight.getEconomyPrice());
        flightDTO.setFirstClassPrice(flight.getFirstClassPrice());
        flightDTO.setDiscountPercentage(flight.getDiscountPercentage());
        flightDTO.setDiscountedBusinessPrice(flight.getDiscountedBusinessPrice());
        flightDTO.setDiscountedEconomyPrice(flight.getDiscountedEconomyPrice());
        flightDTO.setDiscountedFirstClassPrice(flight.getDiscountedFirstClassPrice());
        flightDTO.setOccupiedSeats(mapSeatMapToOccupiedSeatsList(flight.getOccupiedSeats()));

        return flightDTO;
    }

    public static boolean[][] mapSeatMapToOccupiedSeatsList(AirplaneSeatType[][] airplaneSeats) {
        boolean[][] occupiedSeats = new boolean[airplaneSeats.length][airplaneSeats[0].length];
        for(int i = 0; i < airplaneSeats.length; i++) {
            for(int j = 0; j < airplaneSeats[i].length; j++) {
                if(airplaneSeats[i][j].equals(AirplaneSeatType.OCCUPIED) || airplaneSeats[i][j].equals(AirplaneSeatType.RESERVED))
                    occupiedSeats[i][j] = true;
            }
        }
        return occupiedSeats;
    }
    public static Page<UserDTO> mapUserListToUserDtoList(Page<User> users) {
        return users.map(user -> {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setPassword(user.getPassword());
            userDTO.setRoleName(user.getRole().getName());
            return userDTO;
        });
    }

    public static List<RoleDTO> mapRoleListToRoleDtoList(List<Role> roles) {
        List<RoleDTO> roleDTOS = new ArrayList<>();
        for (Role role : roles) {
            RoleDTO roleDTO = new RoleDTO();

            roleDTO.setId(role.getId());
            roleDTO.setName(role.getName());

            roleDTOS.add(roleDTO);
        }
        return roleDTOS;
    }

    public static void buildAirportDtoFromEntity(AirportDTO airportDTO, Airport airport) {
        airportDTO.setId(airport.getId());
        airportDTO.setName(airport.getName());
        airportDTO.setCity(airport.getLocation().getCity());
    }

    public static Page<RoleDTO> mapRoleListToRoleDtoList(Page<Role> roles) {
        return roles.map(role -> {
            RoleDTO roleDTO = new RoleDTO();

            roleDTO.setId(role.getId());
            roleDTO.setName(role.getName());
            return roleDTO;
        });
    }

    public static List<AirportDTO> mapAirportListToAirportDtoList(List<Airport> airports, TextTypeEntityRepository textTypeEntityRepository, IAirportTranslationService airportTranslationService, Language language) {
        List<AirportDTO> airportDTOS = new ArrayList<>();
        TextTypeEntity airportNameText = textTypeEntityRepository.getById((long) TextType.AIRPORT_NAME.getValue());

        for (Airport airport : airports) {
            AirportTranslation airportNameTranslation = airportTranslationService.findByTextTypeAndLanguageAndAirport(airportNameText, language, airport);

            AirportDTO airportDTO = new AirportDTO();
            airportDTO.setId(airport.getId());
            airportDTO.setName(airportNameTranslation.getText());
            airportDTO.setCity(airport.getLocation().getCity());

            airportDTOS.add(airportDTO);
        }
        return airportDTOS;
    }

    public static Page<AirportDTO> mapAirportListToAirportDtoList(Page<Airport> airports, TextTypeEntityRepository textTypeEntityRepository, IAirportTranslationService airportTranslationService, Language language) {
        TextTypeEntity airportNameText = textTypeEntityRepository.getById((long) TextType.AIRPORT_NAME.getValue());

        return airports.map(airport -> {
            AirportTranslation airportTranslation = airportTranslationService.findByTextTypeAndLanguageAndAirport(airportNameText, language, airport);

            AirportDTO airportDTO = new AirportDTO();
            airportDTO.setId(airport.getId());
            airportDTO.setName(airportTranslation.getText());
            airportDTO.setCity(airport.getLocation().getCity());
            return airportDTO;
        });
    }

    public static LocationDTO mapLocationToLocationDto(Location location, TextTypeEntityRepository textTypeEntityRepository, ILocationTranslationService locationTranslationService, Language language) {
        LocationDTO locationDTO = new LocationDTO();

        TextTypeEntity locationCityType = textTypeEntityRepository.getById((long) TextType.LOCATION_CITY.getValue());
        TextTypeEntity locationCountryType = textTypeEntityRepository.getById((long) TextType.LOCATION_COUNTRY.getValue());

        LocationTranslation locationCityTranslation = locationTranslationService.findByTextTypeAndLanguageAndLocation(locationCityType, language, location);
        LocationTranslation locationCountryTranslation = locationTranslationService.findByTextTypeAndLanguageAndLocation(locationCountryType, language, location);

        locationDTO.setId(location.getId());
        locationDTO.setCity(locationCityTranslation.getText());
        locationDTO.setLocationCode(locationCountryTranslation.getText());
        locationDTO.setCountry(location.getCountry());

        return locationDTO;
    }
    public static Location mapLocationDtoToEntity(LocationDTO locationDTO) {
        Location location = new Location();

        location.setCode(locationDTO.getLocationCode());
        location.setCity(locationDTO.getCity());
        location.setCountry(locationDTO.getCountry());

        return location;
    }
    public static List<LocationDTO> mapLocationListToLocationDtoList(TextTypeEntityRepository textTypeEntityRepository, ILocationTranslationService locationTranslationService, Language language, List<Location> locations) {
        List<LocationDTO> locationDTOS = new ArrayList<>();

        for (Location location : locations) {
            TextTypeEntity locationCityType = textTypeEntityRepository.getById((long) TextType.LOCATION_CITY.getValue());
            TextTypeEntity locationCountryType = textTypeEntityRepository.getById((long) TextType.LOCATION_COUNTRY.getValue());
            LocationDTO locationDTO = new LocationDTO();

            LocationTranslation locationCityTranslation = locationTranslationService.findByTextTypeAndLanguageAndLocation(locationCityType, language, location);
            LocationTranslation locationCountryTranslation = locationTranslationService.findByTextTypeAndLanguageAndLocation(locationCountryType, language, location);

            String locationCity;
            String locationCountry;

            if(locationCityTranslation == null) {
                locationCity = location.getCity();
            }
            else {
                locationCity = locationCityTranslation.getText();
            }
            if(locationCityTranslation == null) {
                locationCountry = location.getCountry();
            }
            else {
                locationCountry = locationCountryTranslation.getText();
            }
            locationDTO.setId(location.getId());
            locationDTO.setCity(locationCity);
            locationDTO.setCountry(locationCountry);
            locationDTO.setLocationCode(location.getCode());

            locationDTOS.add(locationDTO);
        }

        return locationDTOS;
    }

    public static Page<LocationDTO> mapLocationListToLocationDtoList(TextTypeEntityRepository textTypeEntityRepository, ILocationTranslationService locationTranslationService, Language language, Page<Location> locations) {
        return locations.map(location -> {
            TextTypeEntity locationCityType = textTypeEntityRepository.getById((long) TextType.LOCATION_CITY.getValue());
            TextTypeEntity locationCountryType = textTypeEntityRepository.getById((long) TextType.LOCATION_COUNTRY.getValue());

            LocationTranslation locationCityTranslation = locationTranslationService.findByTextTypeAndLanguageAndLocation(locationCityType, language, location);
            LocationTranslation locationCountryTranslation = locationTranslationService.findByTextTypeAndLanguageAndLocation(locationCountryType, language, location);

            String locationCity;
            String locationCountry;

            if(locationCityTranslation == null) {
                locationCity = location.getCity();
            }
            else {
                locationCity = locationCityTranslation.getText();
            }
            if(locationCityTranslation == null) {
                locationCountry = location.getCountry();
            }
            else {
                locationCountry = locationCountryTranslation.getText();
            }

            LocationDTO locationDTO = new LocationDTO();
            locationDTO.setId(location.getId());
            locationDTO.setCity(locationCity);
            locationDTO.setCountry(locationCountry);
            locationDTO.setLocationCode(location.getCode());
            return locationDTO;
        });
    }

    public static List<CompanyDTO> mapCompanyListToCompanyDtoList(List<Company> companies, TextTypeEntityRepository textTypeEntityRepository, ICompanyTranslationService companyTranslationService, Language language) {
        List<CompanyDTO> companyDTOS = new ArrayList<>();

        for (Company company : companies) {
            TextTypeEntity companyNameType = textTypeEntityRepository.getById((long) TextType.COMPANY_NAME.getValue());
            CompanyTranslation companyTranslation = companyTranslationService.findByTextTypeAndLanguageAndCompany(companyNameType, language, company);

            CompanyDTO companyDTO = new CompanyDTO();
            companyDTO.setId(company.getId());
            companyDTO.setCompanyName(companyTranslation.getText());
            companyDTO.setPhone(company.getPhone());
            companyDTO.setEmail(company.getEmail());
            companyDTO.setCompanyCode(company.getCode());

            companyDTOS.add(companyDTO);
        }

        return companyDTOS;
    }

    public static Page<CompanyDTO> mapCompanyListToCompanyDtoList(Page<Company> companies, TextTypeEntityRepository textTypeEntityRepository, ICompanyTranslationService companyTranslationService, Language language) {
        return  companies.map(company -> {
            TextTypeEntity companyNameType = textTypeEntityRepository.getById((long) TextType.COMPANY_NAME.getValue());
            CompanyTranslation companyTranslation = companyTranslationService.findByTextTypeAndLanguageAndCompany(companyNameType, language, company);

            CompanyDTO companyDTO = new CompanyDTO();

            companyDTO.setId(company.getId());
            companyDTO.setCompanyName(companyTranslation.getText());
            companyDTO.setPhone(company.getPhone());
            companyDTO.setEmail(company.getEmail());
            companyDTO.setCompanyCode(company.getCode());

            return companyDTO;
        });
    }

    public static PassengerDTO mapPassengerToPassengerDto(Passenger passenger) {
        PassengerDTO passengerDTO = new PassengerDTO();

        passengerDTO.setFirstName(passenger.getFirstName());
        passengerDTO.setLastName(passenger.getLastName());
        passengerDTO.setEmail(passenger.getEmail());
        passengerDTO.setPhoneNumber(passenger.getPhoneNumber());

        return passengerDTO;
    }
    public static List<PassengerDTO> mapPassengerListToPassengerDtoList(List<Passenger> passengers) {
        List<PassengerDTO> passengerDTOS = new ArrayList<>();

        for (Passenger passenger : passengers) {
            PassengerDTO passengerDTO = new PassengerDTO();
            passengerDTO.setId(passenger.getId());
            passengerDTO.setFirstName(passenger.getFirstName());
            passengerDTO.setLastName(passenger.getLastName());
            passengerDTO.setPhoneNumber(passenger.getPhoneNumber());
            passengerDTO.setEmail(passenger.getEmail());
            passengerDTOS.add(passengerDTO);
        }

        return passengerDTOS;
    }

    public static Page<PassengerDTO> mapPassengerListToPassengerDtoList(Page<Passenger> passengers) {
        return passengers.map(passenger -> {
            PassengerDTO passengerDTO = new PassengerDTO();
            passengerDTO.setId(passenger.getId());
            passengerDTO.setFirstName(passenger.getFirstName());
            passengerDTO.setLastName(passenger.getLastName());
            passengerDTO.setPhoneNumber(passenger.getPhoneNumber());
            passengerDTO.setEmail(passenger.getEmail());
            return passengerDTO;
        });
    }

    public static String formatDuration(Duration duration) {
        long totalSeconds = duration.getSeconds();
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;

        return hours + "h " + minutes + "m";
    }

    public static PersonDTO mapPersonToPersonDto(Person person) {
        PersonDTO personDTO = new PersonDTO();

        personDTO.setEmail(person.getEmail());
        personDTO.setPhoneNumber(person.getPhoneNumber());
        personDTO.setFirstName(person.getFirstName());
        personDTO.setLastName(person.getLastName());
        personDTO.setId(person.getId());

        return personDTO;
    }
    public static List<PersonDTO> mapPersonListToPersonDtoList(List<Person> people) {

        List<PersonDTO> personDTOS = new ArrayList<>();

        for (Person person : people) {
            PersonDTO personDTO = new PersonDTO();
            personDTO.setId(person.getId());
            personDTO.setFirstName(person.getFirstName());
            personDTO.setLastName(person.getLastName());
            personDTO.setPhoneNumber(person.getPhoneNumber());
            personDTO.setEmail(person.getEmail());
            personDTO.setBirthDate(person.getBirthDate());
            personDTOS.add(personDTO);
        }

        return personDTOS;
    }

    public static List<AirplaneDTO> mapAirplaneListToAirplaneDtoList(TextTypeEntityRepository textTypeEntityRepository, IAirplaneTranslationService airplaneTranslationService, Language language, List<Airplane> airplanes) {


        List<AirplaneDTO> airplaneDTOS = new ArrayList<>();

        for (Airplane airplane : airplanes) {
            TextTypeEntity airplaneNameTextType = textTypeEntityRepository.getById((long) TextType.AIRPLANE_NAME.getValue());
            AirplaneTranslation airplaneNameTranslation = airplaneTranslationService.findByTextTypeAndLanguageAndAirplane(airplaneNameTextType, language, airplane);

            String airplaneName;

            if(airplaneNameTranslation == null) {
                airplaneName = airplane.getName();
            }
            else {
                airplaneName = airplaneNameTranslation.getText();
            }

            AirplaneDTO airplaneDTO = new AirplaneDTO();
            airplaneDTO.setId(airplane.getId());
            airplaneDTO.setName(airplaneName);
            airplaneDTO.setColumns(airplane.getColumns());
            airplaneDTO.setRows(airplane.getRows());

            airplaneDTOS.add(airplaneDTO);
        }

        return airplaneDTOS;
    }

}
