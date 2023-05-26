package com.danworld.peopledbweb.data;

import com.danworld.peopledbweb.exception.StorageException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@Log4j2
@Repository
public class FileStorageRepository {

    // tells spring to find an environment variable called 'storage_folder', and take use the value of that variable
    @Value("${STORAGE_FOLDER}")
    private String storageDirectory;

    public void save(String originalFilename, InputStream inputStream) {
        // normalize will clean up any weird characters that might have been added to the originalFilename by the browser
        try {
            Path filePath = Path.of(storageDirectory).resolve(originalFilename).normalize();
            Files.copy(inputStream, filePath);
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    public Resource findByName(String filename) {
        try {
            Path filePath = Path.of(storageDirectory).resolve(filename).normalize();
            return new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new StorageException(e);
        }
    }

    public void deleteAllByName(Iterable<String> filenames) {
        for (String filename : filenames) {
            Path filePath = Path.of(storageDirectory).resolve(filename).normalize();
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                log.info("File path: " + filePath + " could not be deleted.");
            }
        }
    }
}
