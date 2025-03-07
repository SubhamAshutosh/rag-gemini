package com.bip.OpenSearch.Service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class ExcelService {
    private final OpenSearchClient openSearchClient;

    public ExcelService(OpenSearchClient openSearchClient) {
        this.openSearchClient = openSearchClient;
    }

    public void processExcel(MultipartFile file) throws IOException {
        // Extract text from Excel file
        String extractedText = extractTextFromExcel(file);

        if (extractedText.isEmpty()) {
            throw new IOException("Excel file is empty or could not be read.");
        }

        // Store in OpenSearch
        Map<String, Object> document = new HashMap<>();
        document.put("content", extractedText);

        IndexRequest<Map<String, Object>> request = IndexRequest.of(i -> i
                .index("documents") // Store Excel data in a separate index
                .document(document)
        );

        openSearchClient.index(request);
    }

    private String extractTextFromExcel(MultipartFile file) throws IOException {
        StringBuilder textContent = new StringBuilder();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = getWorkbook(inputStream, file.getOriginalFilename())) {

            for (Sheet sheet : workbook) { // Iterate through sheets
                textContent.append("Sheet: ").append(sheet.getSheetName()).append("\n");

                for (Row row : sheet) { // Iterate through rows
                    for (Cell cell : row) { // Iterate through cells
                        textContent.append(getCellValue(cell)).append(" ");
                    }
                    textContent.append("\n"); // New line for each row
                }
                textContent.append("\n");
            }
        }

        return textContent.toString().trim();
    }

    private Workbook getWorkbook(InputStream inputStream, String fileName) throws IOException {
        if (fileName.endsWith(".xlsx")) {
            return new XSSFWorkbook(inputStream); // For modern .xlsx files
        } else if (fileName.endsWith(".xls")) {
            return new HSSFWorkbook(inputStream); // For older .xls files
        } else {
            throw new IllegalArgumentException("Unsupported file format: " + fileName);
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return "";
        }
    }
}
