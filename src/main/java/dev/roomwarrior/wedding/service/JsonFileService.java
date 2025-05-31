package dev.roomwarrior.wedding.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class JsonFileService {
    private static final String DATA_FILE_PATH = "wedding_data.json";
    private final ObjectMapper objectMapper;

    public JsonFileService() {
        this.objectMapper = new ObjectMapper();
        createFileIfNotExists();
    }

    private void createFileIfNotExists() {
        try {
            File file = new File(DATA_FILE_PATH);
            if (!file.exists()) {
                objectMapper.writeValue(file, new ArrayList<>());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create JSON file", e);
        }
    }

    public <T> List<T> loadData(Class<T> clazz) {
        try {
            File file = new File(DATA_FILE_PATH);
            if (!file.exists()) {
                createFileIfNotExists();
            }
            return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            log.error("Failed to load data from JSON file", e);
            return new ArrayList<>();
        }
    }

    public <T> void saveData(List<T> data) {
        try {
            File file = new File(DATA_FILE_PATH);
            if (!file.exists()) {
                createFileIfNotExists();
            }
            objectMapper.writeValue(file, data);
            log.info("successfully save data to json file. Size: {}", data.size());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save data to JSON file", e);
        }
    }
}
