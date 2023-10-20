package kz.moderation.server.controller;


import kz.moderation.server.entity.Resume;
import kz.moderation.server.entity.User;
import kz.moderation.server.repository.ResumeRepository;
import kz.moderation.server.service.ResumeService;
import kz.moderation.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ResumeController {
    private static final String UPLOAD_DIR = "uploads/";
    private final ResumeRepository resumeRepository;
    private final ResumeService resumeService;


    @PostMapping("/upload-resume")
    public Map<String, String> uploadResume(
            @RequestParam("iin") String iin,
            @RequestPart("file") MultipartFile file
    ) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("No file uploaded");
            }

            // Generate a unique ID for the uploaded resume
            String resumeId = UUID.randomUUID().toString();

            String fileName = resumeId + "-" + file.getOriginalFilename();

            // Create an object with resume details
            Resume resumeDetails = new Resume();
            resumeDetails.setId(resumeId);
            resumeDetails.setIin(iin);
            resumeDetails.setFileName(file.getOriginalFilename());
            resumeDetails.setFilePath(UPLOAD_DIR + fileName);

            // Save the uploaded file
            File uploadDir = Paths.get(System.getProperty("user.dir"), UPLOAD_DIR).toFile();
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            File destFile = new File(uploadDir, fileName);
            file.transferTo(destFile);

            resumeRepository.save(resumeDetails);

            Map<String, String> response = new HashMap<>();
            response.put("resumeId", resumeId);
            return response;

        } catch (IOException | IllegalArgumentException e) {
            throw new RuntimeException("Internal server error: " + e.getMessage());
        }
    }

    @GetMapping("/download-resume/{resumeId}")
    public ResponseEntity<Resource> downloadResume(@PathVariable String resumeId) {
        // find for a resume by ID
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        // Preparing the path to the resume file
        File resumeFile = new File(Paths.get(System.getProperty("user.dir"), resume.getFilePath()).toString());
        if (!resumeFile.exists()) {
            throw new RuntimeException("File not found");
        }

        // Http headers for response
        HttpHeaders headers = new HttpHeaders();

        try {
            // Encode the filename to make it safe for HTTP headers
            String encodedFileName = URLEncoder.encode(resume.getFileName(), "UTF-8");
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + encodedFileName);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error encoding the filename.");
        }

        // Preparing a resource for downloading
        Resource resource = new FileSystemResource(resumeFile);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resumeFile.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/download-resume-iin/{iin}")
    public ResponseEntity<Resource> downloadResumeByIin(@PathVariable String iin) {
        // find for a resume by ID
        Resume resume = resumeRepository.findByIin(iin)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        // Preparing the path to the resume file
        File resumeFile = new File(Paths.get(System.getProperty("user.dir"), resume.getFilePath()).toString());
        if (!resumeFile.exists()) {
            throw new RuntimeException("File not found");
        }

        // Http headers for response
        HttpHeaders headers = new HttpHeaders();

        try {
            // Encode the filename to make it safe for HTTP headers
            String encodedFileName = URLEncoder.encode(resume.getFileName(), "UTF-8");
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + encodedFileName);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error encoding the filename.");
        }

        // Preparing a resource for downloading
        Resource resource = new FileSystemResource(resumeFile);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resumeFile.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }



    @DeleteMapping("/delete-resume/{resumeId}")
    public Map<String, String> deleteResume(@PathVariable String resumeId) {
        try {
            // find for a resume by ID
            Resume resume = resumeRepository.findById(resumeId)
                    .orElseThrow(() -> new RuntimeException("Resume Not Found"));

            // delete file resume
            File resumeFile = new File(Paths.get(System.getProperty("user.dir"), resume.getFilePath()).toString());
            if (resumeFile.exists()) {
                resumeFile.delete();
            }

            // Deleting a resume entry from the repository
            resumeRepository.deleteById(resumeId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Resume Succesfully deleted");
            return response;
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while deleting your resume.: " + e.getMessage());
        }
    }

    @GetMapping("/user-resume/{iin}")
    public ResponseEntity<?> getUserResumes(@PathVariable String iin) {
        ResponseEntity<?> responseEntity = resumeService.getResumeForUser(iin);
        return responseEntity;
    }

}
