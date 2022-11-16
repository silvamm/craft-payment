package com.tool.craft.service.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface StorageService {

    InputStream get(String key);

    String save(MultipartFile file);

    void delete(String key);
}
