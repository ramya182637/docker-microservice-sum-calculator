package com.example.container2.controller;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/calculate")
public class CalculateController 
{
    @PostMapping
    public ResponseEntity<Map<String, Object>> calculate(@RequestBody Map<String, String> request) 
    {
        String file = request.get("file");
        String product = request.get("product");
        int total = 0;
        String fileName = new File(file).getName();
        try (CSVReader reader = new CSVReader(new FileReader(file))) 
        {
            String[] headers = reader.readNext();

            if (headers == null) 
            {
                return new ResponseEntity<>(createErrorResponse(fileName, "Input file not in CSV format."), HttpStatus.BAD_REQUEST);
            }

            int productIndex = findColumnIndex(headers, "product");
            int amountIndex = findColumnIndex(headers, "amount");

            if (productIndex == -1 || amountIndex == -1)
            {
                return new ResponseEntity<>(createErrorResponse(fileName, "Input file not in CSV format."), HttpStatus.BAD_REQUEST);
            }

            String[] line;
            while ((line = reader.readNext()) != null) 
            {
                if (line[productIndex].equals(product)) 
                {
                    try 
                    {
                        total += Integer.parseInt(line[amountIndex]);
                    } 
                    catch (NumberFormatException e) 
                    {
                        return new ResponseEntity<>(createErrorResponse(fileName, "Input file not in CSV format."), HttpStatus.BAD_REQUEST);
                    }
                }
            }

        } 
        catch (IOException | CsvValidationException e) 
        {
            return new ResponseEntity<>(createErrorResponse(fileName, "Input file not in CSV format."), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(createSuccessResponse(fileName, total), HttpStatus.OK);
    }

    private int findColumnIndex(String[] headers, String columnName) 
    {
        for (int i = 0; i < headers.length; i++)
         {
            if (headers[i].equals(columnName)) 
            {
                return i;
            }
        }
        return -1;
    }

    private Map<String, Object> createErrorResponse(String file, String errorMessage) 
    {
        Map<String, Object> response = new HashMap<>();
        response.put("file", file);
        response.put("error", errorMessage);
        return response;
    }

    private Map<String, Object> createSuccessResponse(String file, int sum) 
    {
        Map<String, Object> response = new HashMap<>();
        response.put("file", file);
        response.put("sum", sum);
        return response;
    }
}
