package com.tool.craft.service.storage;

public interface StorageService {

    String save(byte[] file, Long size, String extension);

    void delete(String fileNameWithExtension);
}
