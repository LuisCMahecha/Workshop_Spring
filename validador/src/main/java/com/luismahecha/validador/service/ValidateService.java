package com.luismahecha.validador.service;


import com.luismahecha.validador.model.ValidationResponse;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.regex.Pattern;

@Service
public class ValidateService {

    public ValidationResponse processCSV(MultipartFile file){

    try (Reader reader = new InputStreamReader(file.getInputStream());
         CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {

        int validLines = 0;
        int invalidLines = 0;

        String[] line;
        while ((line = csvReader.readNext()) != null) {
            if (!validateCsvRecord(line)) {
                validLines++;
            } else {
                invalidLines++;
            }
        }
        return new ValidationResponse(validLines, invalidLines);
    } catch (IOException e) {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    } catch (CsvValidationException e) {
        throw new RuntimeException(e);
    }
    }

    private boolean validateCsvRecord(String[] record) {
        if (record.length < 9) {
            return false;
        }
        String email = record[5];
        String dob = record[7];
        String jobTitle = record[8];
        System.out.println(email+" "+ validateEmail(email));
        System.out.println(dob+ " "+ validateDateOfBirth(dob));
        System.out.println(jobTitle+ " "+ validateJobTitle(jobTitle));
        System.out.println((validateEmail(email) && validateDateOfBirth(dob) && validateJobTitle(jobTitle)));
        return (validateEmail(email) && validateDateOfBirth(dob) && validateJobTitle(jobTitle));
    }


    public ValidationResponse processExcel(MultipartFile file){
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            int validLines = 0;
            int invalidLines = 0;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }

                String injuryLocation = row.getCell(0).getStringCellValue();
                String reportType = row.getCell(1).getStringCellValue();

                System.out.println(injuryLocation);
                System.out.println(reportType);

                if (validateInjuryLocation(injuryLocation) && validateReportType(reportType)) {
                    validLines++;
                } else {
                    invalidLines++;
                }
            }

            return new ValidationResponse(validLines, invalidLines);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    private boolean validateEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return Pattern.matches(emailRegex, email);
    }

    private boolean validateDateOfBirth(String dateOfbirth) {
        try {
            return dateOfbirth.compareTo("1980-01-01") > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validateJobTitle(String jobTitle) {
        String[] validJobTitles = {"Haematologist", "Phytotherapist", "Building surveyor", "Insurance account manager", "Educational psychologist"};
        for (String validTitle : validJobTitles) {
            if (validTitle.equalsIgnoreCase(jobTitle)) {
                return true;
            }
        }
        return false;
    }
    private boolean validateInjuryLocation(String injuryLocation) {
        return !injuryLocation.equals("N/A");
    }

    private boolean validateReportType(String reportType) {
        String[] validReportTypes = {"Near Miss", "Lost Time", "First Aid"};
        for (String validType : validReportTypes) {
            if (validType.equalsIgnoreCase(reportType)) {
                return true;
            }
        }
        return false;
    }
}





