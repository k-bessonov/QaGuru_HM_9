package tests;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FilesTest {

    private final ClassLoader cl = FilesTest.class.getClassLoader();

    @Test
    @DisplayName("Проверка наличия файлов в архиве")
    void checkZipNotNull() throws Exception {
        InputStream stream = Objects.requireNonNull(
                cl.getResourceAsStream("QaGuru_HM_9_files.zip"),
                "Файл <<QaGuru_HM_9_files.zip>> не найден!"
        );

        Charset charset = Charset.forName("CP866");

        try (ZipInputStream zis = new ZipInputStream(stream, charset)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                System.out.println("Нашли файл: " + entry.getName());
            }
        }
    }


    @Test
    @DisplayName("Проверка PDF файла из архива")
    void checkPdfFile() throws Exception {
        Charset charset = Charset.forName("CP866");
        boolean pdfFileFound = false;

        try (ZipInputStream zis = new ZipInputStream(Objects.requireNonNull
                (cl.getResourceAsStream("QaGuru_HM_9_files.zip")), charset)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".pdf")) {
                    pdfFileFound = true;
                    PDF pdf = new PDF(zis);
                    Assertions.assertTrue(
                            pdf.text.contains("k-bessonov"),
                            "PDF не содержит нужный текст"
                    );
                    break;
                }
            }
        }
        Assertions.assertTrue(pdfFileFound, "В архиве отсутствуют PDF-файлы");

    }

    @Test
    @DisplayName("Проверка XLSX файла из архива")
    void checkXlsxFile() throws Exception {
        Charset charset = Charset.forName("CP866");
        boolean xlsxFound = false;

        try (ZipInputStream zis = new ZipInputStream(Objects.requireNonNull
                (cl.getResourceAsStream("QaGuru_HM_9_files.zip")), charset)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".xlsx")) {
                    xlsxFound = true;
                    XLS xls = new XLS(zis);
                    String actualValue = xls.excel.getSheetAt(0).getRow(1).getCell(1).getStringCellValue();

                    Assertions.assertTrue(actualValue.contains("Два два"));
                    break;
                }
            }
        }
        Assertions.assertTrue(xlsxFound, "В архиве отсутствуют PDF-файлы");

    }

    @Test
    @DisplayName("Проверка CSV-файла в архиве")
    void checkCsvInZip() throws Exception {
        Charset charset = Charset.forName("CP866");
        boolean csvFound = false;

        try (ZipInputStream zis = new ZipInputStream(Objects.requireNonNull
                (cl.getResourceAsStream("QaGuru_HM_9_files.zip")),charset)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".csv")) {
                    csvFound = true;
                    try (CSVReader reader = new CSVReader(new InputStreamReader(zis))) {
                        List<String[]> rows = reader.readAll();
                        Assertions.assertEquals(2, rows.size());
                        Assertions.assertArrayEquals(
                                new String[]{"first", "second"},
                                rows.get(0)
                        );
                        Assertions.assertArrayEquals(
                                new String[]{"one", "two"},
                                rows.get(1)
                        );
                    }
                    break;
                }

            }
        }
        Assertions.assertTrue(csvFound, "В архиве отсутствуют PDF-файлы");
    }
}
