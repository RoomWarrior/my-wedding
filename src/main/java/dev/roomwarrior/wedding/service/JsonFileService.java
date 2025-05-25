package dev.roomwarrior.wedding.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class JsonFileService {
    private static final String DATA_FILE_PATH = "wedding_data.json";
    private final ObjectMapper objectMapper;
    private final ExecutorService executorService;

    public JsonFileService() {
        this.objectMapper = new ObjectMapper();
        this.executorService = Executors.newSingleThreadExecutor();
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
            return new ArrayList<>();
        }
    }

    public <T> void saveData(List<T> data) {
        executorService.submit(() -> {
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
        });
    }
} 
