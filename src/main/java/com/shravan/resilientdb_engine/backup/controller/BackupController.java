package com.shravan.resilientdb_engine.backup.controller;
import com.shravan.resilientdb_engine.backup.dto.BackupJobResponse;
import com.shravan.resilientdb_engine.backup.dto.CreateBackupRequest;
import com.shravan.resilientdb_engine.backup.entity.BackupJob;
import com.shravan.resilientdb_engine.backup.entity.BackupStatus;
import com.shravan.resilientdb_engine.backup.entity.DatabaseType;
import com.shravan.resilientdb_engine.backup.service.BackupService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Collectors;
/**
 * REST Controller for managing backup operations.
 */
@RestController
@RequestMapping("/api/backups")
public class BackupController {
    private final BackupService backupService;

    public BackupController(BackupService backupService) {
        this.backupService = backupService;
    }

    /**
     * Create a new backup job.
     * POST /api/backups
     */
    @PostMapping
    public ResponseEntity<BackupJobResponse> createBackupJob(@RequestBody CreateBackupRequest request) {
        BackupJob createdJob = backupService.createBackupJob(request);
        return new ResponseEntity<>(BackupJobResponse.fromEntity(createdJob), HttpStatus.CREATED);
    }

    /**
     * Get a backup job by ID.
     * GET /api/backups/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<BackupJobResponse> getBackupJobById(@PathVariable Long id) {
        BackupJob backupJob = backupService.getBackupJobById(id);
        return ResponseEntity.ok(BackupJobResponse.fromEntity(backupJob));
    }

    /**
     * Get all backup jobs.
     * GET /api/backups
     */
    @GetMapping
    public ResponseEntity<List<BackupJobResponse>> getAllBackupJobs() {
        List<BackupJob> jobs = backupService.getAllBackupJobs();
        List<BackupJobResponse> responseList = jobs.stream()
                .map(BackupJobResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    /**
     * Get backup jobs by status.
     * GET /api/backups/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<BackupJobResponse>> getBackupJobsByStatus(@PathVariable BackupStatus status) {
        List<BackupJob> jobs = backupService.getBackupJobsByStatus(status);
        List<BackupJobResponse> responseList = jobs.stream()
                .map(BackupJobResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    /**
     * Update the status of a backup job.
     * PATCH /api/backups/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<BackupJobResponse> updateBackupStatus(
            @PathVariable Long id,
            @RequestParam BackupStatus status) {
        BackupJob updatedJob = backupService.updateBackupStatus(id, status);
        return ResponseEntity.ok(BackupJobResponse.fromEntity(updatedJob));
    }

    /**
     * Get the latest backup jobs.
     * GET /api/backups/latest
     */
    @GetMapping("/latest")
    public ResponseEntity<List<BackupJobResponse>> getLatestBackups() {
        List<BackupJob> latestJobs = backupService.getLatestBackups();
        List<BackupJobResponse> responseList = latestJobs.stream()
                .map(BackupJobResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    /**
     * Upload a backup file.
     * POST /api/backups/upload
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BackupJobResponse> uploadBackup(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobName") String jobName,
            @RequestParam(value = "databaseType", required = false, defaultValue = "POSTGRESQL") DatabaseType databaseType) {
        BackupJob createdJob = backupService.uploadBackup(file, jobName, databaseType);
        return new ResponseEntity<>(BackupJobResponse.fromEntity(createdJob), HttpStatus.CREATED);
    }

    /**
     * Download a backup file.
     * GET /api/backups/download/{id}
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadBackup(@PathVariable Long id) {
        Resource resource = backupService.downloadBackup(id);
        String filename = resource.getFilename();
        if (filename == null) {
            filename = "download";
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}