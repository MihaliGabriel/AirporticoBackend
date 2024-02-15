package main.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import main.dto.FlightDTO;
import main.model.*;
import main.repository.*;
import org.apache.commons.codec.language.bm.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.xml.bind.ValidationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.awt.Color;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.stream.Stream;


@Service
public class DocumentService {

    private FlightRepository flightRepository;
    private RouteRepository routeRepository;
    private CompanyRepository companyRepository;
    private LanguageRepository languageRepository;
    private AirplaneRepository airplaneRepository;
    private LocationRepository locationRepository;
    private AirportRepository airportRepository;

    private TextTypeEntityRepository textTypeEntityRepository;
    private FlightTranslationRepository flightTranslationRepository;
    private LocationTranslationRepository locationTranslationRepository;
    private CompanyTranslationRepository companyTranslationRepository;
    private AirportTranslationRepository airportTranslationRepository;
    private AirplaneTranslationRepository airplaneTranslationRepository;
    private CloudService cloudService;

    @Autowired
    public DocumentService(FlightRepository flightRepository, RouteRepository routeRepository, CompanyRepository companyRepository, LanguageRepository languageRepository, AirplaneRepository airplaneRepository, LocationRepository locationRepository, AirportRepository airportRepository, TextTypeEntityRepository textTypeEntityRepository, FlightTranslationRepository flightTranslationRepository, LocationTranslationRepository locationTranslationRepository, CompanyTranslationRepository companyTranslationRepository, AirportTranslationRepository airportTranslationRepository, AirplaneTranslationRepository airplaneTranslationRepository) {
        this.flightRepository = flightRepository;
        this.routeRepository = routeRepository;
        this.companyRepository = companyRepository;
        this.languageRepository = languageRepository;
        this.airplaneRepository = airplaneRepository;
        this.locationRepository = locationRepository;
        this.airportRepository = airportRepository;
        this.textTypeEntityRepository = textTypeEntityRepository;
        this.flightTranslationRepository = flightTranslationRepository;
        this.locationTranslationRepository = locationTranslationRepository;
        this.companyTranslationRepository = companyTranslationRepository;
        this.airportTranslationRepository = airportTranslationRepository;
        this.airplaneTranslationRepository = airplaneTranslationRepository;
    }
    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);
    private static final String INVALID = "Invalid";
    private static final String ACTION = "Action";

    public void exportAirportsToPdf(List<Airport> airports) throws FileNotFoundException, DocumentException {
        logger.info("Exporting airport to pdf..");
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("airports.pdf"));
        document.open();

        // Title for the ticket table
        Paragraph ticketTitle = new Paragraph("Airport information", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
        ticketTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(ticketTitle);


        PdfPTable airportTable = new PdfPTable(2);

        addAirportTableHeader(airportTable);
        addAirportRows(airportTable, airports);
        document.add(airportTable);

        document.close();

    }

    private void addAirportTableHeader(PdfPTable table) {
        Stream.of("Airport name", "Airport city")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    private void addAirportRows(PdfPTable table, List<Airport> airports) {
        for (Airport airport : airports) {
            table.addCell(airport.getName());
            table.addCell(airport.getLocation().getCity());
        }
    }

    public void exportFlightsToPdf(List<FlightDTO> flightDTOS) throws FileNotFoundException, DocumentException {
        logger.info("Exporting flight to pdf...");
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("flights.pdf"));
        document.open();

        Paragraph ticketTitle = new Paragraph("Flights information", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
        ticketTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(ticketTitle);


        PdfPTable flightTable = new PdfPTable(11);
        addFlightTableHeader(flightTable);
        addFlightRows(flightTable, flightDTOS);
        document.add(flightTable);

        document.close();

    }

    private void addFlightTableHeader(PdfPTable table) {
        Stream.of("Route name", "Flight name", "Company", "Business seats", "Economy seats", "First class seats", "Departure date", "Arrival date", "Departure city",
                        "Arrival city", "Business price", "Economy price", " First class price")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    private void addFlightRows(PdfPTable table, List<FlightDTO> flightDTOS) {
        for (FlightDTO flightDTO : flightDTOS) {
            table.addCell(flightDTO.getRouteName());
            table.addCell(flightDTO.getName());
            table.addCell(flightDTO.getCompanyName());
            table.addCell(flightDTO.getBusinessSeats().toString());
            table.addCell(flightDTO.getFirstClassSeats().toString());
            table.addCell(flightDTO.getEconomySeats().toString());
            table.addCell(flightDTO.getDepartureDate().toString());
            table.addCell(flightDTO.getArrivalDate().toString());
            table.addCell(flightDTO.getDepartureCity());
            table.addCell(flightDTO.getArrivalCity());
            table.addCell(flightDTO.getBusinessPrice().toString());
            table.addCell(flightDTO.getEconomyPrice().toString());
            table.addCell(flightDTO.getFirstClassPrice().toString());
        }
    }

    public void exportFlightsToExcel(List<Flight> flights) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Flights");

        CreationHelper createHelper = workbook.getCreationHelper();

        XSSFCellStyle noRemainingSeatsIdStyle = workbook.createCellStyle();
        noRemainingSeatsIdStyle.setFillForegroundColor(new XSSFColor(new Color(217, 7, 7), null));
        noRemainingSeatsIdStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFCellStyle remainingSeatsIdStyle = workbook.createCellStyle();
        remainingSeatsIdStyle.setFillForegroundColor(new XSSFColor(new Color(217, 7, 7), null));
        remainingSeatsIdStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFCellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
        dateStyle.setFillForegroundColor(new XSSFColor(new Color(173, 216, 230), null));
        dateStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFCellStyle noRemainingSeats = workbook.createCellStyle();
        noRemainingSeats.setFillForegroundColor(new XSSFColor(new Color(255, 182, 182), null));
        noRemainingSeats.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFCellStyle remainingSeats = workbook.createCellStyle();
        remainingSeats.setFillForegroundColor(new XSSFColor(new Color(173, 216, 230), null));
        remainingSeats.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFCellStyle actionLegend = workbook.createCellStyle();
        actionLegend.setFillForegroundColor(new XSSFColor(new Color(163, 238, 157), null));
        actionLegend.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Nr crt.");
        headerRow.createCell(1).setCellValue("Flight Name");
        headerRow.createCell(2).setCellValue("Flight business price");
        headerRow.createCell(3).setCellValue("Flight economy price");
        headerRow.createCell(4).setCellValue("Flight first class price");
        headerRow.createCell(5).setCellValue("Flight business seats");
        headerRow.createCell(6).setCellValue("Flight economy seats");
        headerRow.createCell(7).setCellValue("Flight first class seats");
        headerRow.createCell(8).setCellValue("Remaining business seats");
        headerRow.createCell(9).setCellValue("Remaining economy seats");
        headerRow.createCell(10).setCellValue("Remaining first class seats");
        headerRow.createCell(11).setCellValue("Company nr crt.");
        headerRow.createCell(12).setCellValue("Route nr crt.");
        headerRow.createCell(13).setCellValue("Arrival Date");
        headerRow.createCell(14).setCellValue("Departure Date");
        headerRow.createCell(15).setCellValue(ACTION);
        headerRow.createCell(17).setCellValue("Legend");
        headerRow.createCell(18).setCellValue("Info");


        int rowNum = 1;
        for (Flight flight : flights) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(flight.getId());
            row.createCell(1).setCellValue(flight.getName());
            row.createCell(2).setCellValue(flight.getBusinessPrice());
            row.createCell(3).setCellValue(flight.getEconomyPrice());
            row.createCell(4).setCellValue(flight.getFirstClassPrice());
            row.createCell(5).setCellValue(flight.getBusinessSeats());
            row.createCell(6).setCellValue(flight.getEconomySeats());
            row.createCell(7).setCellValue(flight.getFirstClassSeats());
            row.createCell(8).setCellValue(flight.getRemainingBusinessSeats());
            row.createCell(9).setCellValue(flight.getRemainingEconomySeats());
            row.createCell(10).setCellValue(flight.getRemainingFirstClassSeats());
            row.createCell(11).setCellValue(flight.getCompany().getId());
            row.createCell(12).setCellValue(flight.getRoute().getId());
            row.createCell(13).setCellValue(flight.getArrivalDate());
            row.createCell(14).setCellValue(flight.getDepartureDate());
            row.createCell(15).setCellValue("e");

            XSSFCellStyle currentIdStyle = flight.getRemainingBusinessSeats() == 0 && flight.getRemainingEconomySeats() == 0 && flight.getRemainingFirstClassSeats() == 0 ? noRemainingSeatsIdStyle : remainingSeatsIdStyle;
            row.getCell(0).setCellStyle(currentIdStyle);
            row.getCell(1).setCellStyle(currentIdStyle);
            row.getCell(8).setCellStyle(currentIdStyle);
            row.getCell(9).setCellStyle(currentIdStyle);
            row.getCell(10).setCellStyle(currentIdStyle);
            XSSFCellStyle currentStyle = flight.getRemainingBusinessSeats() == 0 && flight.getRemainingEconomySeats() == 0 && flight.getRemainingFirstClassSeats() == 0 ? noRemainingSeats : remainingSeats;
            for (int i = 2; i < 13; i++) {
                if (i != 8 && i != 9 && i != 10)
                    row.getCell(i).setCellStyle(currentStyle);
            }
            row.getCell(13).setCellStyle(dateStyle);
            row.getCell(14).setCellStyle(dateStyle);
            row.getCell(15).setCellStyle(currentStyle);
        }
        setLegendRows(sheet, 1, "Do not modify", remainingSeatsIdStyle);
        setLegendRows(sheet, 2,"Flights that are available", remainingSeats);
        setLegendRows(sheet, 3,"Flight that have no seats remaining", noRemainingSeats);
        setActionLegendRows(sheet, 4, "i", "i in action for inserting new data in database", actionLegend);
        setActionLegendRows(sheet, 5, "u", "u in action for updating data in database", actionLegend);
        setActionLegendRows(sheet, 6, "d", "d in action for deleting data in database", actionLegend);

        sheet.autoSizeColumn(17);
        sheet.autoSizeColumn(18);
        for (int i = 0; i < 15; i++) {
            sheet.autoSizeColumn(i);
        }
        try (FileOutputStream fileOut = new FileOutputStream("flights.xlsx")) {
            workbook.write(fileOut);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        workbook.close();
    }

    private static void setActionLegendRows(Sheet sheet, int i, String text1, String text, XSSFCellStyle actionLegend) {
        Row row;
        if (sheet.getRow(i) != null) {
            row = sheet.getRow(i);
            row.createCell(17).setCellValue(text1);
            row.createCell(18).setCellValue(text);
            row.getCell(17).setCellStyle(actionLegend);
            row.getCell(18).setCellStyle(actionLegend);
        } else {
            row = sheet.createRow(4);
            row.createCell(17).setCellValue(text1);
            row.createCell(18).setCellValue(text);
            row.getCell(17).setCellStyle(actionLegend);
            row.getCell(18).setCellStyle(actionLegend);
        }
    }

    private static void setLegendRows(Sheet sheet, int i, String text, XSSFCellStyle remainingSeatsIdStyle) {
        Row row;
        if (sheet.getRow(i) != null) {
            row = sheet.getRow(i);
            row.createCell(17).setCellValue(text);
            row.getCell(17).setCellStyle(remainingSeatsIdStyle);
        } else {
            row = sheet.createRow(i);
            row.createCell(17).setCellValue(text);
            row.getCell(17).setCellStyle(remainingSeatsIdStyle);
        }
    }

    public void exportAirportsToExcel(List<Airport> airports) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Airports");

        CreationHelper createHelper = workbook.getCreationHelper();
        short dateFormat = createHelper.createDataFormat().getFormat("yyyy-MM-dd");

        XSSFCellStyle defaultStyle = workbook.createCellStyle();
        defaultStyle.setDataFormat(dateFormat);

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Airport ID");
        headerRow.createCell(1).setCellValue("Airport Name");
        headerRow.createCell(2).setCellValue("Location ID");

        int rowNum = 1;
        for (Airport airport : airports) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(airport.getId().toString());
            row.createCell(1).setCellValue(airport.getName());
            row.createCell(2).setCellValue(airport.getLocation().getId().toString());
        }

        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fileOut = new FileOutputStream("airports.xlsx")) {
            workbook.write(fileOut);
        }
        catch (Exception e) {
            logger.error("Error exporting airports to excel: {}", e.getMessage(), e);
        }
        finally {
            workbook.close();
        }
    }


    public Map<Long, List<String>> getTranslations(Language language, TextTypeEntity textTypeEntity) {
        Map<Long, List<String>> translationsMap = new HashMap<>();

        switch (textTypeEntity.getName()) {
            case "AIRPLANE_NAME":
                List<Airplane> airplanes = airplaneRepository.findAll();
                for (Airplane airplane : airplanes) {
                    Optional<AirplaneTranslation> airplaneTranslation = airplaneTranslationRepository.findByTextTypeAndLanguageAndAirplane(textTypeEntity, language, airplane);
                    airplaneTranslation.ifPresent(translation -> translationsMap.computeIfAbsent(airplane.getId(), k -> new ArrayList<>()).add(translation.getText()));
                }
                break;
            case "AIRPORT_NAME":
                List<Airport> airports = airportRepository.findAll();
                for (Airport airport : airports) {
                    Optional<AirportTranslation> airportTranslation = airportTranslationRepository.findByTextTypeAndLanguageAndAirport(textTypeEntity, language, airport);
                    airportTranslation.ifPresent(translation -> translationsMap.computeIfAbsent(airport.getId(), k -> new ArrayList<>()).add(translation.getText()));
                }
                break;
            case "COMPANY_NAME":
                List<Company> companies = companyRepository.findAll();
                for (Company company : companies) {
                    Optional<CompanyTranslation> companyTranslation = companyTranslationRepository.findByTextTypeAndLanguageAndCompany(textTypeEntity, language, company);
                    companyTranslation.ifPresent(translation -> translationsMap.computeIfAbsent(company.getId(), k -> new ArrayList<>()).add(translation.getText()));
                }
                break;
            case "FLIGHT_NAME":
                List<Flight> flights = flightRepository.findAll();
                for (Flight flight : flights) {
                    Optional<FlightTranslation> flightTranslation = flightTranslationRepository.findByTextTypeAndLanguageAndFlight(textTypeEntity, language, flight);
                    flightTranslation.ifPresent(translation -> translationsMap.computeIfAbsent(flight.getId(), k -> new ArrayList<>()).add(translation.getText()));
                }
                break;
            case "LOCATION_CITY":
            case "LOCATION_COUNTRY":
                List<Location> locations = locationRepository.findAll();
                for (Location location : locations) {
                    Optional<LocationTranslation> locationTranslation = locationTranslationRepository.findByTextTypeAndLanguageAndLocation(textTypeEntity, language, location);
                    locationTranslation.ifPresent(translation -> translationsMap.computeIfAbsent(location.getId(), k -> new ArrayList<>()).add(translation.getText()));
                }
                break;
            default:

        }

        return translationsMap;
    }

    @Transactional
    public void exportTranslationToExcel() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();;
        try (FileOutputStream fileOut = new FileOutputStream("translations.xlsx")) {
            Sheet sheet = workbook.createSheet("Translations");

            List<Language> applicationLanguages = languageRepository.findAll();
            Class<TextType> textTypeClass = TextType.class;
            TextType[] textTypes = textTypeClass.getEnumConstants();

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("ENTITY_ID");
            headerRow.createCell(2).setCellValue("TEXT_TYPE");
            headerRow.createCell(3).setCellValue("ro");
            headerRow.createCell(4).setCellValue("fr");
            headerRow.createCell(5).setCellValue("en");
            headerRow.createCell(6).setCellValue("Action");
            headerRow.createCell(10).setCellValue("Legend");
            headerRow.createCell(11).setCellValue("Info");

            //contor pt randurile din excel
            int i = 1;
            Row row;
            TextTypeEntity textTypeEntity;

            for (TextType textType : textTypes) {
                textTypeEntity = textTypeEntityRepository.getById((long) textType.getValue());

                //contor pentru limbile din aplicatie, incepe de la 2 pt ca coloanele pt limba din excel incep de la 2
                //contor pentru randuri, acesta creste odata cu introducerea traducerilor pt o limba pe coloana in jos.
                int j = i;
                int languageCounter = 3;
                for (Language language : applicationLanguages) {
                    Map<Long, List<String>> translationsMap = getTranslations(language, textTypeEntity);
                    for (Map.Entry<Long, List<String>> entry : translationsMap.entrySet()) {
                        Long id = entry.getKey();
                        List<String> translations = entry.getValue();

                        for (String translation : translations) {
                            if (sheet.getRow(j) == null) {
                                row = sheet.createRow(j);
                                row.createCell(0).setCellValue(textTypeEntity.getId());
                                row.createCell(1).setCellValue(id);
                                row.createCell(2).setCellValue(textTypeEntity.getName());
                                row.createCell(languageCounter).setCellValue(translation);
                                j++;
                            } else {
                                row = sheet.getRow(j);
                                row.createCell(languageCounter).setCellValue(translation);
                                j++;
                            }
                        }
                    }
                    //aici se reseteaza j pt ca trecem la o noua coloana si se introduc traducerile pt o noua limba incepand de la primul rand
                    j = i;
                    languageCounter++;
                }
                if(sheet.getRow(i) != null) {
                    row = sheet.getRow(i);
                    row.createCell(6).setCellValue("e");
                }
                i++;
            }
            if(sheet.getRow(i) != null) {
                row = sheet.getRow(i);
                row.createCell(6).setCellValue("e");
            }
            createLegendAndInfoColumn(sheet);

            for (int j = 0; j < 12; j++) {
                sheet.autoSizeColumn(j);
            }

            workbook.write(fileOut);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            workbook.close();
        }
    }

    private static void createLegendAndInfoColumn(Sheet sheet) {
        Row row;
        if(sheet.getRow(1) != null) {
            row = sheet.getRow(1);
            row.createCell(10).setCellValue("u");
            row.createCell(11).setCellValue("u for updating traslations in database");
        }
        else {
            row = sheet.createRow(1);
            row.createCell(10).setCellValue("u");
            row.createCell(11).setCellValue("u for updating traslations in database");
        }
        if(sheet.getRow(2) != null) {
            row = sheet.getRow(2);
            row.createCell(10).setCellValue("e");
            row.createCell(11).setCellValue("e for no changes");
        }
        else {
            row = sheet.createRow(2);
            row.createCell(10).setCellValue("e");
            row.createCell(11).setCellValue("e for no changes");
        }
    }

    public void importTranslationsFromExcel(String filename) throws IOException, ValidationException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(filename))) {
            Sheet sheet = workbook.getSheet("Translations");

            int nrRows = sheet.getPhysicalNumberOfRows();
            String textType;
            long textTypeId;
            String translation;
            AirplaneTranslation airplaneTranslation;
            LocationTranslation locationTranslation;
            AirportTranslation airportTranslation;
            CompanyTranslation companyTranslation;
            FlightTranslation flightTranslation;
            long entityId;
            for(int i = 1; i < nrRows; i++) {
                Row row = sheet.getRow(i);

                if(isStringValid(row.getCell(6), "Action", i).equals("u")) {
                    textType = isStringValid(row.getCell(2), "TEXT_TYPE", i);
                    entityId = isLongValid(row.getCell(1), "ENTITY_ID");
                    textTypeId  = isLongValid(row.getCell(0), "ID");
                    TextTypeEntity textTypeEntity = textTypeEntityRepository.getById(textTypeId);
                    List<Language> applicationLanguages = languageRepository.findAll();
                    applicationLanguages.sort(Comparator.comparing(Language::getId));
                    int languageCounter = 3;
                    String languageName;
                    for(Language language: applicationLanguages) {
                        languageName = language.getName();
                        translateRows(textType, entityId, i, row, textTypeEntity, languageCounter, language, languageName);
                        languageCounter++;
                    }
                }

            }
        }
    }

    private void translateRows(String textType, long entityId, int i, Row row, TextTypeEntity textTypeEntity, int languageCounter, Language language, String languageName) throws ValidationException {
        LocationTranslation locationTranslation;
        AirplaneTranslation airplaneTranslation;
        AirportTranslation airportTranslation;
        CompanyTranslation companyTranslation;
        FlightTranslation flightTranslation;
        String translation;

        switch (textType) {
            case "AIRPLANE_NAME":
                Airplane airplane = airplaneRepository.getById(entityId);
                Optional<AirplaneTranslation> airplaneTranslationOptional = airplaneTranslationRepository.findByTextTypeAndLanguageAndAirplane(textTypeEntity, language, airplane);
                if(airplaneTranslationOptional.isPresent()) {
                    airplaneTranslation = airplaneTranslationOptional.get();
                    translation = isStringValid(row.getCell(languageCounter), language.getName(), i);
                    airplaneTranslation.setText(translation);
                    airplaneTranslationRepository.save(airplaneTranslation);
                }
                break;
            case "AIRPORT_NAME":
                Airport airport = airportRepository.getById(entityId);
                Optional<AirportTranslation> airportTranslationOptional = airportTranslationRepository.findByTextTypeAndLanguageAndAirport(textTypeEntity, language, airport);
                if (airportTranslationOptional.isPresent()) {
                    airportTranslation = airportTranslationOptional.get();
                    translation = isStringValid(row.getCell(languageCounter), language.getName(), i);
                    airportTranslation.setText(translation);
                    airportTranslationRepository.save(airportTranslation);
                }
                break;

            case "COMPANY_NAME":
                Company company = companyRepository.getById(entityId);
                Optional<CompanyTranslation> companyTranslationOptional = companyTranslationRepository.findByTextTypeAndLanguageAndCompany(textTypeEntity, language, company);
                if (companyTranslationOptional.isPresent()) {
                    companyTranslation = companyTranslationOptional.get();
                    translation = isStringValid(row.getCell(languageCounter), language.getName(), i);
                    companyTranslation.setText(translation);
                    companyTranslationRepository.save(companyTranslation);
                }
                break;

            case "FLIGHT_NAME":
                Flight flight = flightRepository.getById(entityId);
                Optional<FlightTranslation> flightTranslationOptional = flightTranslationRepository.findByTextTypeAndLanguageAndFlight(textTypeEntity, language, flight);
                if (flightTranslationOptional.isPresent()) {
                    flightTranslation = flightTranslationOptional.get();
                    translation = isStringValid(row.getCell(languageCounter), language.getName(), i);
                    flightTranslation.setText(translation);
                    flightTranslationRepository.save(flightTranslation);
                }
                break;

            case "LOCATION_CITY":
            case "LOCATION_COUNTRY":
                Location location = locationRepository.getById(entityId);
                Optional<LocationTranslation> locationTranslationOptional = locationTranslationRepository.findByTextTypeAndLanguageAndLocation(textTypeEntity, language, location);
                logger.info("Location translations: {}", locationTranslationOptional.get());
                if (locationTranslationOptional.isPresent()) {
                    locationTranslation = locationTranslationOptional.get();
                    translation = isStringValid(row.getCell(languageCounter), language.getName(), i);
                    logger.info("Translation {} in column {} ", translation, languageCounter);
                    locationTranslation.setText(translation);
                    locationTranslationRepository.save(locationTranslation);
                }
                break;
            default:
        }
    }

    public void importFlightsFromExcel(String fileName) throws IOException, ValidationException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(fileName))) {
            Sheet sheet = workbook.getSheet("Flights");
            Flight flight;
            for (int i = 1; i <= flightRepository.count(); i++) {
                Row row = sheet.getRow(i);

                LocalDateTime arrivalDate = isDateValid(row.getCell(13), "Arrival date");
                LocalDateTime departureDate = isDateValid(row.getCell(14), "Departure date");

                if (!departureDate.isBefore(arrivalDate)) {
                    throw new ValidationException("Departure date should be before arrival date at row " + i);
                }
                logger.info("DATES: {} {}", arrivalDate, departureDate);
                if (isRouteValid(isLongValid(row.getCell(12), "Route ID")) && isCompanyValid(isLongValid(row.getCell(11), "Company ID"))) {
                    Route route = routeRepository.getById(isLongValid(row.getCell(12), "Route ID"));
                    Company company = companyRepository.getById(isLongValid(row.getCell(11), "Company ID"));

                    Long flightId = Long.valueOf(0L);
                    if (isStringValid(row.getCell(15), ACTION, 12).equals("u") || isStringValid(row.getCell(15), ACTION, 12).equals("d")) {
                        flightId = isLongValid(row.getCell(0), "Flight ID");
                    }
                    int departureHour = departureDate.getHour();
                    int departureMinute = departureDate.getMinute();

                    String formattedHour = String.format("%02d", departureHour);
                    String formattedMinute = String.format("%02d", departureMinute);

                    String formattedTime = formattedHour + formattedMinute;
                    flight = getFlight(row, arrivalDate, departureDate, route, company, formattedTime);
                    logger.info("Flight entity in excel: {}", flight);
                    testActionField(flight, row, flightId);
                } else {
                    throw new ValidationException("Route/company/flight is not valid");
                }
            }
        }
    }

    private void testActionField(Flight flight, Row row, Long flightId) throws ValidationException {
        if (isStringValid(row.getCell(15), ACTION, 12).equals("u")) {
            flight.setId(flightId);
            flightRepository.save(flight);
        }
        if (isStringValid(row.getCell(15), ACTION, 12).equals("i")) {
            flightRepository.save(flight);
        }
        if (isStringValid(row.getCell(15), ACTION, 12).equals("d")) {
            flightRepository.deleteById(flightId);
        }
    }

    private Flight getFlight(Row row, LocalDateTime arrivalDate, LocalDateTime departureDate, Route route, Company company, String formattedTime) throws ValidationException {
        Flight flight;
        flight = new Flight();
        flight.setName(company.getCode() + route.getDepartureAirport().getLocation().getCode() + route.getArrivalAirport().getLocation().getCode() + formattedTime);
        flight.setBusinessPrice(isDoubleValid(row.getCell(2), "Business price"));
        flight.setEconomyPrice(isDoubleValid(row.getCell(3), "Economy price"));
        flight.setFirstClassPrice(isDoubleValid(row.getCell(4), "First class price"));
        flight.setBusinessSeats(isIntegerValid(row.getCell(5), "Business seats"));
        flight.setEconomySeats(isIntegerValid(row.getCell(6), "Economy seats"));// Set the flight seats
        flight.setFirstClassSeats(isIntegerValid(row.getCell(7), "First class seats"));
        flight.setRemainingBusinessSeats(isIntegerValid(row.getCell(8), "Remaining business seats"));
        flight.setRemainingEconomySeats(isIntegerValid(row.getCell(9), "Remaining economy seats"));
        flight.setRemainingFirstClassSeats(isIntegerValid(row.getCell(10), "Remaining first class seats"));
        flight.setArrivalDate(arrivalDate);
        flight.setDepartureDate(departureDate);
        flight.setRoute(route);
        flight.setCompany(company);
        flight.computeDuration();
        flight.computeDiscount();
        return flight;
    }

    public boolean isRouteValid(Long routeId) {
        try {
            Optional<Route> route = routeRepository.findById(routeId);
            if (!route.isPresent())
                return false;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean isCompanyValid(Long companyId) {
        Optional<Company> company = companyRepository.findById(companyId);
        return company.isPresent();
    }

    private Long isLongValid(Cell cell, String fieldName) throws ValidationException {
        if (cell.getCellType() == CellType.STRING) {
            try {
                return Long.parseLong(cell.getStringCellValue());
            }
            catch (NumberFormatException ex) {
                throw new ValidationException(INVALID + fieldName + ": " + cell.getStringCellValue());
            }
        } else {
            double numericValue = cell.getNumericCellValue();
            long longValue = (long) numericValue;
            if (numericValue != longValue || longValue < 1) {
                throw new ValidationException(INVALID + fieldName + ": " + numericValue);
            }
            return longValue;
        }
    }

    private Integer isIntegerValid(Cell cell, String fieldName) throws ValidationException {
        if (cell.getCellType() == CellType.STRING) {
            try {
                return Integer.parseInt(cell.getStringCellValue());
            }
            catch (NumberFormatException ex) {
                throw new ValidationException(INVALID + fieldName + ": " + cell.getStringCellValue());
            }
        } else {
            double numericValue = cell.getNumericCellValue();
            int intValue = (int) numericValue;
            if (numericValue != intValue || intValue < 1) {
                throw new ValidationException(INVALID + fieldName + ": " + numericValue);
            }
            return intValue;
        }
    }

    private Double isDoubleValid(Cell cell, String fieldName) throws ValidationException {
        if (cell.getCellType() == CellType.STRING) {
            try {
                return Double.parseDouble(cell.getStringCellValue());
            }
            catch (NumberFormatException ex) {
                throw new ValidationException(INVALID + fieldName + ": " + cell.getStringCellValue());
            }
        } else {
            return cell.getNumericCellValue();
        }
    }

    private String isStringValid(Cell cell, String columnName, int rowIndex) throws ValidationException {
        if (cell == null || cell.getCellType() != CellType.STRING || cell.getStringCellValue().trim().isEmpty()) {
            throw new ValidationException(columnName + " in row " + rowIndex + " must be a non-empty string.");
        }
        return cell.getStringCellValue();
    }

    private LocalDateTime isDateValid(Cell cell, String fieldName) throws ValidationException {
        if (cell == null || cell.getCellType() != CellType.NUMERIC) {
            throw new ValidationException(fieldName + " is required and should be a valid date.");
        }
        LocalDateTime dateValue = cell.getLocalDateTimeCellValue();
        if (dateValue == null) {
            throw new ValidationException(fieldName + " cannot be empty.");
        }
        return dateValue;
    }

}
