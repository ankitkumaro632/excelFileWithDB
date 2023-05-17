package com.example.demo;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class DataExporter {

    private static final int BATCH_SIZE = 1000;

    @Autowired
    private YourEntityRepository yourEntityRepository;

    public void exportToExcel(HttpServletResponse response) throws IOException {
        // Set up streaming workbook
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");
        workbook.setCompressTempFiles(true); // Enable temporary file compression

        List<YourEntity> records = yourEntityRepository.findAll();

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("First Name");
        headerRow.createCell(2).setCellValue("Last Name");
        headerRow.createCell(3).setCellValue("Email");
        headerRow.createCell(4).setCellValue("Phone Number");
        headerRow.createCell(5).setCellValue("Address");

        // Populate data rows in batches
        int rowIndex = 1;
        int batchCount = 0;
        for (YourEntity record : records) {
            Row dataRow = sheet.createRow(rowIndex);
            dataRow.createCell(0).setCellValue(record.getId());
            dataRow.createCell(1).setCellValue(record.getFirstName());
            dataRow.createCell(2).setCellValue(record.getLastName());
            dataRow.createCell(3).setCellValue(record.getEmail());
            dataRow.createCell(4).setCellValue(record.getPhoneNumber());
            dataRow.createCell(5).setCellValue(record.getAddress());
            rowIndex++;
            batchCount++;

            // Flush the sheet every BATCH_SIZE records
            if (batchCount % BATCH_SIZE == 0) {
                batchCount = 0;
                ((SXSSFSheet) sheet).flushRows();
            }
        }

        // Set content type and headers for the response
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=data.xlsx");

        // Write the workbook to the response
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.dispose(); // Dispose of temporary files
        outputStream.flush();
    }
}
