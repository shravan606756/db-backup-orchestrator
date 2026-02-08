package com.shravan.resilientdb_engine.backup.service.impl;
import com.shravan.resilientdb_engine.backup.dto.CreateBackupRequest;
import com.shravan.resilientdb_engine.backup.entity.BackupJob;
import com.shravan.resilientdb_engine.backup.entity.BackupStatus;
import com.shravan.resilientdb_engine.backup.entity.DatabaseType;
import com.shravan.resilientdb_engine.backup.repository.BackupJobRepository;
import com.shravan.resilientdb_engine.backup.service.BackupService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Service
public class BackupServiceImpl implements BackupService {
    private final BackupJobRepository backupJobRepository;
    @Value("${app.upload.dir}")
    private String uploadDir;
    public BackupServiceImpl(BackupJobRepository backupJobRepository) {
        this.backupJobRepository = backupJobRepository;
    }
    @Override
    @Transactional
    public BackupJob createBackupJob(CreateBackupRequest request) {
        BackupJob backupJob = new BackupJob();
        backupJob.setJobName(request.getJobName());
        backupJob.setDatabaseType(request.getDatabaseType());
        // Status defaults to PENDING in entity via @PrePersist
        return backupJobRepository.save(backupJob);
    }
    @Override
    public BackupJob getBackupJobById(Long id) {
        return backupJobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Backup job not found with id: " + id));
    }
    @Override
    public List<BackupJob> getAllBackupJobs() {
        return backupJobRepository.findAll();
    }
    @Override
    public List<BackupJob> getBackupJobsByStatus(BackupStatus status) {
        return backupJobRepository.findByStatus(status);
    }
    @Override
    @Transactional
    public BackupJob updateBackupStatus(Long id, BackupStatus status) {
        BackupJob backupJob = getBackupJobById(id);
        backupJob.setStatus(status);
        if (status == BackupStatus.IN_PROGRESS) {
            backupJob.setStartedAt(LocalDateTime.now());
        } else if (status == BackupStatus.COMPLETED || status == BackupStatus.FAILED) {
            backupJob.setCompletedAt(LocalDateTime.now());
        }
        return backupJobRepository.save(backupJob);
    }
    @Override
    public List<BackupJob> getLatestBackups() {
        return backupJobRepository.findTop10ByOrderByCreatedAtDesc();
    }
    @Override
    @Transactional
    public BackupJob uploadBackup(MultipartFile file, String jobName, DatabaseType databaseType) {
        // 1. Validation
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File cannot be null or empty");
        }
        try {
            // 2. Ensure upload directory exists
            Path uploadDirPath = Paths.get(uploadDir);
            if (!Files.exists(uploadDirPath)) {
                Files.createDirectories(uploadDirPath);
            }
            // 3. Sanitize filename
            String originalFilename = file.getOriginalFilename();
            String safeFilename = originalFilename != null ? Paths.get(originalFilename).getFileName().toString()
                    : "backup";
            String uniqueFilename = UUID.randomUUID() + "_" + safeFilename;
            Path targetPath = uploadDirPath.resolve(uniqueFilename);
            // 4. Save file
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            // 5. Create Entity
            BackupJob backupJob = new BackupJob();
            backupJob.setJobName(jobName);
            backupJob.setDatabaseType(databaseType);
            backupJob.setFilePath(targetPath.toAbsolutePath().toString());
            backupJob.setSizeBytes(file.getSize());
            // Status remains PENDING for further processing
            backupJob.setStatus(BackupStatus.PENDING);
            return backupJobRepository.save(backupJob);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
    @Override
    public Resource downloadBackup(Long id) {
        BackupJob backupJob = getBackupJobById(id);
        String filePathObj = backupJob.getFilePath();
        if (filePathObj == null) {
            throw new RuntimeException("File path not found for backup job id: " + id);
        }
        Path filePath = Paths.get(filePathObj);
        if (!Files.exists(filePath)) {
            throw new RuntimeException("Backup file not found on disk: " + filePathObj);
        }
        return new FileSystemResource(filePath);
    }
}