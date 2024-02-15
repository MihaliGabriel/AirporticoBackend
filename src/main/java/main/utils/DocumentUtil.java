package main.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import main.dto.PassengerDTO;
import main.dto.TicketDTO;


import java.io.FileOutputStream;

import java.util.List;
import java.util.stream.Stream;


public class DocumentUtil {

    private DocumentUtil() {

    }
    public static void generateTicketPDF(TicketDTO ticketDTO) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("ticket.pdf"));
        document.open();

        // Title for the ticket table
        Paragraph ticketTitle = new Paragraph("Ticket Information", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
        ticketTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(ticketTitle);

        PdfPTable ticketTable = new PdfPTable(4);
        PdfPTable passengerTable = new PdfPTable(4);

        List<PassengerDTO> passengerDTOS = ticketDTO.getPassengers();

        addPassengerTableHeader(passengerTable);
        addPassengerRows(passengerTable, passengerDTOS);
        addTicketTableHeader(ticketTable);
        addTicketRows(ticketTable, ticketDTO);

        document.add(ticketTable);

        // Add space between tables
        document.add(new Paragraph("\n\n"));

        // Title for the passenger table
        Paragraph passengerTitle = new Paragraph("Passenger Information", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
        passengerTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(passengerTitle);
        document.add(passengerTable);

        document.close();
    }

    private static void addTicketTableHeader(PdfPTable table) {
        Stream.of("Ticket name", "Flight name", "Ticket type", "Price")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    private static void addTicketRows(PdfPTable table, TicketDTO ticketDTO) {
        table.addCell(ticketDTO.getTicketName());
        table.addCell(ticketDTO.getFlightName());
        table.addCell(ticketDTO.getTicketType());
        table.addCell(ticketDTO.getPrice().toString());
    }

    private static void addPassengerTableHeader(PdfPTable table) {
        Stream.of("First name", "Last name", "Email", "Phone number")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    private static void addPassengerRows(PdfPTable table, List<PassengerDTO> passengers) {
        for (PassengerDTO passengerDTO : passengers) {
            table.addCell(passengerDTO.getFirstName());
            table.addCell(passengerDTO.getLastName());
            table.addCell(passengerDTO.getEmail());
            table.addCell(passengerDTO.getPhoneNumber());
        }
    }
}
