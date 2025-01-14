package vn.trungtq.jobhunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.trungtq.jobhunter.domain.response.file.ResUploadFileDTO;
import vn.trungtq.jobhunter.service.FileService;
import vn.trungtq.jobhunter.util.anotation.ApiMessage;
import vn.trungtq.jobhunter.util.error.StorageException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RestController
public class FileController {
    @Value("${trungtq.upload-file.base-uri}")
    private String baseURI;
    private final FileService fileService;
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }
    @PostMapping("/files")
    @ApiMessage("Upload single file")
    public ResponseEntity<ResUploadFileDTO> upload(@RequestParam(name="file",required = false) MultipartFile file, @RequestParam("folder") String folder)
            throws URISyntaxException, IOException, StorageException {

        if (file == null || file.isEmpty()) {
            throw new StorageException("File is empty, please provide a valid file");
        }
        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx", "json");
        boolean isAllowedExtension = allowedExtensions.stream().anyMatch(extension -> fileName.toLowerCase().endsWith(extension));

        if (!isAllowedExtension) {
            throw new StorageException("Invalid file extension, only allowed: " + allowedExtensions);
        }
        this.fileService.createDirectory(baseURI+folder);

        String uploaded = this.fileService.store(file,folder);

        ResUploadFileDTO resUploadFileDTO = new ResUploadFileDTO();
        resUploadFileDTO.setFileName(uploaded);
        resUploadFileDTO.setUploadedAt(Instant.now());

        return ResponseEntity.ok().body(resUploadFileDTO);
    }
}
