package com.example.demo;

import org.springframework.stereotype.Component;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

    @Component
    public class ExcelImporter {

        @Autowired
        private YourEntityRepository yourEntityRepository;

        public void importFromExcel(MultipartFile file) throws IOException {
            try (InputStream inputStream = file.getInputStream()) {
//                Workbook workbook = new XSSFWorkbook(inputStream);
                Workbook workbook = WorkbookFactory.create(inputStream);
//                Workbook workbook = new SXSSFWorkbook(new XSSFWorkbook(inputStream), 100); // Set the desired row access window size
                ExecutorService executorService = Executors.newFixedThreadPool(10); // Adjust thread pool size as needed

                int batchSize = 1000; // Set the desired batch size

                for (Sheet sheet : workbook) {
                    Iterator<Row> rowIterator = sheet.iterator();
                    List<YourEntity> batch = new ArrayList<>(batchSize);

                    while (rowIterator.hasNext()) {
                        Row row = rowIterator.next();

                        // Skip the header row
                        if (row.getRowNum() == 0) {
                            continue;
                        }

                        YourEntity entity = createEntityFromRow(row);
                        batch.add(entity);

                        if (batch.size() >= batchSize) {
                            saveBatch(batch, executorService);
                            batch.clear();
                        }
                    }

                    saveBatch(batch, executorService); // Save any remaining entities in the last batch
                }

                workbook.close();
                executorService.shutdown();
            }
        }

        private YourEntity createEntityFromRow(Row row) {
            YourEntity entity = new YourEntity();
            Cell cell;

            cell = row.getCell(0);
            entity.setFirstName(cell != null ? cell.getStringCellValue() : "");

            cell = row.getCell(1);
            entity.setLastName(cell != null ? cell.getStringCellValue() : "");

            cell = row.getCell(2);
            entity.setEmail(cell != null ? cell.getStringCellValue() : "");

            cell = row.getCell(3);
            entity.setPhoneNumber(cell != null ? cell.getStringCellValue() : "");

            cell = row.getCell(4);
            entity.setAddress(cell != null ? cell.getStringCellValue() : "");

            return entity;
        }

        private void saveBatch(List<YourEntity> batch, ExecutorService executorService) {
            List<YourEntity> batchToSave = new ArrayList<>(batch);
            executorService.submit(() -> {
                yourEntityRepository.saveAll(batchToSave);
                yourEntityRepository.flush(); // Flush the batch insert to the database
            });
        }

    }

