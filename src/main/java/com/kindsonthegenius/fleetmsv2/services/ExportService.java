package com.kindsonthegenius.fleetmsv2.services;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;
import com.kindsonthegenius.fleetmsv2.security.models.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ExportService {

    public ByteArrayInputStream usersToExcel(List<User> users) throws IOException {
        String[] columns = {"ID", "Username", "Email", "First Name", "Last Name"};
        
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Users");
            Row headerRow = sheet.createRow(0);

            // Create header row
            for (int col = 0; col < columns.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columns[col]);
            }

            // Fill data rows
            int rowIdx = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(user.getId());
                row.createCell(1).setCellValue(user.getUsername());
                row.createCell(2).setCellValue(user.getEmail());
                row.createCell(3).setCellValue(user.getFirstname());
                row.createCell(4).setCellValue(user.getLastname());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public ByteArrayInputStream usersToPdf(List<User> users) throws DocumentException {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        // Add title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Users List", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" ")); // Add spacing

        // Create table
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);

        // Add header cells
        Stream.of("ID", "Username", "Email", "First Name", "Last Name")
            .forEach(columnTitle -> {
                PdfPCell header = new PdfPCell();
                header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                header.setBorderWidth(2);
                header.setPhrase(new Phrase(columnTitle));
                table.addCell(header);
            });

        // Add data rows
        for (User user : users) {
            table.addCell(String.valueOf(user.getId()));
            table.addCell(user.getUsername());
            table.addCell(user.getEmail());
            table.addCell(user.getFirstname());
            table.addCell(user.getLastname());
        }

        document.add(table);
        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }
}
