package com.project.services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;

public class TransformCsvToXlsx {

    public void convert() {
        String csvFile = ServiceS3.csvName;

        try (Workbook workbook = new XSSFWorkbook();
             BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            Sheet spreadsheets = workbook.createSheet("data");
            String line;
            int lineIndex = 0;

            if ((line = br.readLine()) != null) {
                Row header = spreadsheets.createRow(lineIndex++);
                String[] columnNames = line.split(",");
                for (int i = 0; i < columnNames.length; i++) {
                    header.createCell(i).setCellValue(columnNames[i].trim());
                }
            }

            while ((line = br.readLine()) != null) {
                Row currentLine = spreadsheets.createRow(lineIndex++);
                String[] column = line.split(",");

                for (int i = 0; i < column.length; i++) {
                    currentLine.createCell(i).setCellValue(column[i].trim());
                }
            }

            String datasetName = new File(csvFile).getName();
            String xlsxName = datasetName.substring(0, datasetName.lastIndexOf('.')) + ".xlsx";

            try (FileOutputStream writer = new FileOutputStream("src/" + xlsxName)) {
                workbook.write(writer);
                System.out.println("file XLSX created successfully: src/" + xlsxName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

