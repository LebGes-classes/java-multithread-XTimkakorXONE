package com.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelHandler {
    private final String filename;
    private final ReentrantReadWriteLock lock;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ExcelHandler(String filename) {
        this.filename = filename;
        this.lock = new ReentrantReadWriteLock();
        createFileIfNotExists();
    }

    private void createFileIfNotExists() {
        File file = new File(filename);
        if (!file.exists()) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet workerSheet = workbook.createSheet("Статистика работников");
                Row workerHeaderRow = workerSheet.createRow(0);
                workerHeaderRow.createCell(0).setCellValue("ID работника");
                workerHeaderRow.createCell(1).setCellValue("Имя");
                workerHeaderRow.createCell(2).setCellValue("Общее время работы (часы)");
                workerHeaderRow.createCell(3).setCellValue("Время простоя (часы)");
                workerHeaderRow.createCell(4).setCellValue("Количество рабочих дней");
                workerHeaderRow.createCell(5).setCellValue("Дата обновления");

                Sheet taskSheet = workbook.createSheet("Статистика задач");
                Row taskHeaderRow = taskSheet.createRow(0);
                taskHeaderRow.createCell(0).setCellValue("ID работника");
                taskHeaderRow.createCell(1).setCellValue("Название задачи");
                taskHeaderRow.createCell(2).setCellValue("Длительность (часы)");
                taskHeaderRow.createCell(3).setCellValue("Дата создания");
                
                try (FileOutputStream fileOut = new FileOutputStream(filename)) {
                    workbook.write(fileOut);
                }
            } catch (IOException e) {
                System.err.println("Ошибка при создании файла: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void saveWorkerStatistics(Worker worker) {
        lock.writeLock().lock();
        try {
            try (FileInputStream fis = new FileInputStream(filename);
                 Workbook workbook = WorkbookFactory.create(fis)) {
                
                Sheet sheet = workbook.getSheet("Статистика работников");
                if (sheet == null) {
                    sheet = workbook.createSheet("Статистика работников");
                    Row headerRow = sheet.createRow(0);
                    headerRow.createCell(0).setCellValue("ID работника");
                    headerRow.createCell(1).setCellValue("Имя");
                    headerRow.createCell(2).setCellValue("Общее время работы (часы)");
                    headerRow.createCell(3).setCellValue("Время простоя (часы)");
                    headerRow.createCell(4).setCellValue("Количество рабочих дней");
                    headerRow.createCell(5).setCellValue("Дата обновления");
                }

                int lastRowNum = sheet.getLastRowNum();
                Row row = sheet.createRow(lastRowNum + 1);
                
                row.createCell(0).setCellValue(worker.getId());
                row.createCell(1).setCellValue(worker.getName());
                row.createCell(2).setCellValue(worker.getTotalWorkTime());
                row.createCell(3).setCellValue(worker.getIdleTime());
                row.createCell(4).setCellValue(worker.getDaysWorked());
                row.createCell(5).setCellValue(LocalDateTime.now().format(DATE_FORMATTER));

                try (FileOutputStream fileOut = new FileOutputStream(filename)) {
                    workbook.write(fileOut);
                }
            } catch (IOException e) {
                System.err.println("Ошибка при сохранении статистики работника: " + e.getMessage());
                e.printStackTrace();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void saveTaskStatistics(Worker worker, Task task) {
        lock.writeLock().lock();
        try {
            try (FileInputStream fis = new FileInputStream(filename);
                 Workbook workbook = WorkbookFactory.create(fis)) {
                
                Sheet sheet = workbook.getSheet("Статистика задач");
                if (sheet == null) {
                    sheet = workbook.createSheet("Статистика задач");
                    Row headerRow = sheet.createRow(0);
                    headerRow.createCell(0).setCellValue("ID работника");
                    headerRow.createCell(1).setCellValue("Название задачи");
                    headerRow.createCell(2).setCellValue("Длительность (часы)");
                    headerRow.createCell(3).setCellValue("Дата создания");
                }

                int lastRowNum = sheet.getLastRowNum();
                Row row = sheet.createRow(lastRowNum + 1);
                
                row.createCell(0).setCellValue(worker.getId());
                row.createCell(1).setCellValue(task.getTaskName());
                row.createCell(2).setCellValue(task.getDuration());
                row.createCell(3).setCellValue(LocalDateTime.now().format(DATE_FORMATTER));

                try (FileOutputStream fileOut = new FileOutputStream(filename)) {
                    workbook.write(fileOut);
                }
            } catch (IOException e) {
                System.err.println("Ошибка при сохранении статистики задачи: " + e.getMessage());
                e.printStackTrace();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
} 