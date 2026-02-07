package com.shravan.resilientdb_engine.backup.service.impl;
import com.shravan.resilientdb_engine.backup.dto.CreateBackupRequest;
import com.shravan.resilientdb_engine.backup.entity.BackupJob;
import com.shravan.resilientdb_engine.backup.entity.BackupStatus;
import com.shravan.resilientdb_engine.backup.repository.BackupJobRepository;
import com.shravan.resilientdb_engine.backup.service.BackupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
@Service
public class BackupServiceImpl implements BackupService {
    private final BackupJobRepository backupJobRepository;
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
}