package dev.roomwarrior.wedding.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class JsonFileService {
    private final ObjectMapper objectMapper;

    @Value("${data-path}")
    private String dataFilePath;

    public JsonFileService() {
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void postConstruct() {

        if (dataFilePath == null || dataFilePath.isBlank()) {
            log.warn("Data path is not set, using default path for local: /data");
            dataFilePath = System.getProperty("user.dir") + "/data";
        }

        File dataDir = new File(dataFilePath);
        if (!dataDir.exists()) {
            if (dataDir.mkdirs()) {
                log.info("Created data directory: {}", dataDir.getAbsolutePath());
            } else {
                log.error("Failed to create directory: {}", dataDir.getAbsolutePath());
                throw new RuntimeException("Failed to create data directory");
            }
        }

        this.dataFilePath += "/wedding_data.json";
        createFileIfNotExists();
        log.info("JsonFileService initialized with path: {}", dataFilePath);
    }

    private void createFileIfNotExists() {
        try {
            File file = new File(dataFilePath);
            if (!file.exists()) {
                objectMapper.writeValue(file, new ArrayList<>());
                log.info("Created new JSON file at: {}", dataFilePath);
            }
        } catch (IOException e) {
            log.error("Failed to create JSON file at: {}", dataFilePath, e);
            throw new RuntimeException("Failed to create JSON file", e);
        }
    }

    public <T> List<T> loadData(Class<T> clazz) {
        try {
            File file = new File(dataFilePath);
            if (!file.exists()) {
                createFileIfNotExists();
            }
            List<T> data = objectMapper.readValue(file,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
            log.info("Successfully loaded {} entries from JSON file", data.size());
            return data;
        } catch (IOException e) {
            log.error("Failed to load data from JSON file: {}", dataFilePath, e);
            return new ArrayList<>();
        }
    }

    public <T> void saveData(List<T> data) {
        try {
            File file = new File(dataFilePath);
            if (!file.exists()) {
                createFileIfNotExists();
            }
            objectMapper.writeValue(file, data);
            log.info("Successfully saved {} entries to JSON file: {}", data.size(), dataFilePath);
        } catch (IOException e) {
            log.error("Failed to save data to JSON file: {}", dataFilePath, e);
            throw new RuntimeException("Failed to save data to JSON file", e);
        }
    }
}
