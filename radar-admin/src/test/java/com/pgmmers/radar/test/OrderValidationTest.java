package com.pgmmers.radar.test;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.junit.Test;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.json.JSONObject;

@Component
public class OrderValidationTest {

    @Test
    public void testIsPhoneValid() {
//        String mobile = "7778867687";
//        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
//        try {
//            Phonenumber.PhoneNumber phoneNumberProto = phoneUtil.parse(mobile, "US");
//            System.out.println("isvalid: " + phoneUtil.isValidNumber(phoneNumberProto));
//        } catch (NumberParseException e) {
//            System.err.println("NumberParseException was thrown: " + e.toString());
//        }


        String filePath = "/Users/ethan/Dev/Workspace_Onerway/radar/radar-admin/src/main/resources/data/800320-20240722-0730.xlsx"; // 替换为你的文件路径
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // 假设数据在第一个sheet

            Row headerRow = sheet.getRow(0);
            Cell resultHeaderCell = headerRow.createCell(53); // 假设新列
            resultHeaderCell.setCellValue("Phone验证结果");

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    // 跳过标题行
                    continue;
                }

                String countryCode = row.getCell(48).getStringCellValue(); // 国家列
                String phoneNumberStr = row.getCell(45).getStringCellValue(); // 电话列

                Cell resultCell = row.createCell(53); // 新列用于存储验证结果


                //System.out.println("Country code: " + countryCode+" , "+"Phone Number" + phoneNumberStr);
                if (phoneNumberStr != null && phoneNumberStr.trim().length() > 0) {
                    try {
                        Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(phoneNumberStr, countryCode);
                        Boolean isPossiblePhoneNumber = phoneNumberUtil.isPossibleNumber(phoneNumber);
                        Boolean isValidPhoneNumber = phoneNumberUtil.isValidNumber(phoneNumber);
                        if (isValidPhoneNumber || isPossiblePhoneNumber) {
                            System.out.println(phoneNumberStr + " is a valid number.");
                            resultCell.setCellValue("True");
                        } else {
                            System.out.println(phoneNumberStr + " is not a valid number.");
                            resultCell.setCellValue("False");
                        }
                    } catch (NumberParseException e) {
                        System.out.println("Error parsing number " + phoneNumberStr + ": " + e.getMessage());
                        resultCell.setCellValue("无效手机号");
                    }
                }
            }

            // 写回Excel文件
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddress() throws IOException {
        String apiUrlTemplate = "http://192.168.197.154:4000/v1/search?text=%s";

        //System.out.println(getApiResponse("http://192.168.197.154:4000/v1"));

        String filePath = "/Users/ethan/Dev/Workspace_Onerway/radar/radar-admin/src/main/resources/data/800320-20240722-0730.xlsx"; // 替换为你的文件路径
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // 假设数据在第一个sheet

            Row headerRow = sheet.getRow(0);
            Cell resultHeaderCell = headerRow.createCell(54);
            resultHeaderCell.setCellValue("Address Confidence");

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    // 跳过标题行
                    continue;
                }

                String countryCode = row.getCell(48).getStringCellValue(); // 国家列

                String addressStr = row.getCell(49).getStringCellValue();
                String cityStr = row.getCell(46).getStringCellValue();

                Cell resultCell = row.createCell(54); // 新列用于存储验证结果
//                Cell countryResultCell = row.createCell(55); // 新列用于存储验证结果

                if (countryCode.trim().equals("DE") && addressStr.trim() != null) {
                    String encodedAddress = URLEncoder.encode(addressStr, StandardCharsets.UTF_8.toString());
                    String encodedCity = URLEncoder.encode(cityStr, StandardCharsets.UTF_8.toString());

                    String apiUrl = String.format(apiUrlTemplate, encodedAddress.replace(" ", "%20")+","+encodedCity.replace(" ", "%20"));


                    try {
                        String response = getApiResponse(apiUrl);

                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray features = jsonResponse.getJSONArray("features");
                        if (features.length() > 0) {
                            JSONObject properties = features.getJSONObject(0).getJSONObject("properties");
                            double confidence = properties.optDouble("confidence", 0.0);
                            resultCell.setCellValue(confidence);
                        } else {
                            resultCell.setCellValue("无结果");
                        }

//                        if (jsonResponse.has("confidence")) {
//                            double confidence = jsonResponse.getDouble("confidence");
//                            resultCell.setCellValue(confidence);
//                        } else {
//                            resultCell.setCellValue("No confidence value");
//                        }
                    } catch (IOException e) {
                        resultCell.setCellValue("API error: " + e.getMessage());
                    }
                }
            }

            // 写回Excel文件
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getApiResponse(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        // Check if connect is made
        int responseCode = conn.getResponseCode();

        // 200 OK
        if (responseCode != 200) {
            throw new IOException("HTTP response code: " + responseCode);
        }

        // Scanner functionality will read the data from the stream
        Scanner scanner = new Scanner(url.openStream());
        StringBuilder inline = new StringBuilder();
        while (scanner.hasNext()) {
            inline.append(scanner.nextLine());
        }
        scanner.close();

        return inline.toString();
    }
}



