package com.example.demo;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;


@RestController
public class ExportController {

    @Autowired
    private DataExporter dataExporter;

    @Autowired
    private ExcelImporter excelImporter;

    @Autowired
    private YourEntityRepository yourEntityRepository;

    @GetMapping("/export")
    public void exportData(HttpServletResponse response) throws IOException, SQLException {
        dataExporter.exportToExcel(response);
    }

    @PostMapping("/upload")
    public String uploadExcelFile(@RequestParam("file") MultipartFile file) {
        try {
            excelImporter.importFromExcel(file);
            return "redirect:/success";
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/error";
        }
    }



//    @PostMapping("/upload")
//    public String uploadExcelFile(@RequestParam("file") MultipartFile file) {
//        try {
//            // Load the Excel file
//            Workbook workbook = new XSSFWorkbook(file.getInputStream());
//
//            // Get the first sheet from the workbook
//            Sheet sheet = workbook.getSheetAt(0);
//
//            // Iterate through each row
//            for (Row row : sheet) {
//                // Skip the header row
//                if (row.getRowNum() == 0) {
//                    continue;
//                }
//
//                // Create a new entity object
//                YourEntity entity = new YourEntity();
//
//                // Set the entity fields from the row data
//                entity.setFirstName(row.getCell(0).getStringCellValue());
//                entity.setLastName(row.getCell(1).getStringCellValue());
//                entity.setEmail(row.getCell(2).getStringCellValue());
//                entity.setPhoneNumber(row.getCell(3).getStringCellValue());
//                entity.setAddress(row.getCell(4).getStringCellValue());
//
//                // Save the entity to the database
//                yourEntityRepository.save(entity);
//            }
//
//            // Close the workbook
//            workbook.close();
//
//            // Redirect to a success page or return a success response
//            return "redirect:/success";
//        } catch (IOException e) {
//            e.printStackTrace();
//            // Handle the exception and return an error response
//            return "redirect:/error";
//        }
//    }


}
