package com.shravan.resilientdb_engine.backup.service;
import com.shravan.resilientdb_engine.backup.entity.BackupJob;
import com.shravan.resilientdb_engine.backup.entity.BackupStatus;
import com.shravan.resilientdb_engine.backup.repository.BackupJobRepository;
import com.shravan.resilientdb_engine.backup.strategy.BackupStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
/**
 * Service dedicated to executing backup jobs asynchronously.
 */
@Service
public class BackupExecutionService {
    private static final Logger log = LoggerFactory.getLogger(BackupExecutionService.class);
    private final BackupJobRepository backupJobRepository;
    private final List<BackupStrategy> backupStrategies;
    public BackupExecutionService(BackupJobRepository backupJobRepository, List<BackupStrategy> backupStrategies) {
        this.backupJobRepository = backupJobRepository;
        this.backupStrategies = backupStrategies;
    }
    /**
     * Executes the backup job asynchronously.
     * Finds the appropriate strategy and runs the backup.
     * Updates the job status to IN_PROGRESS, then COMPLETED or FAILED.
     *
     * @param backupJob the backup job to execute
     */
    @Async
    public void executeBackupAsync(BackupJob backupJob) {
        log.info("Starting backup execution for job id {}", backupJob.getId());
        try {
            // Update to IN_PROGRESS
            updateStatus(backupJob, BackupStatus.IN_PROGRESS, null);
            // Find strategy
            BackupStrategy strategy = backupStrategies.stream()
                    .filter(s -> s.supports(backupJob.getDatabaseType()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No strategy found for database type: " + backupJob.getDatabaseType()));
            log.info("Using strategy {} for database {}", strategy.getClass().getSimpleName(),
                    backupJob.getDatabaseType());
            // Simulate execution execution delay (e.g. 5 seconds)
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Backup execution interrupted", e);
            }
            // Execute actual strategy logic
            strategy.executeBackup(backupJob);
            // Update to COMPLETED
            updateStatus(backupJob, BackupStatus.COMPLETED, null);
            log.info("Backup completed successfully for job id {}", backupJob.getId());
        } catch (Exception e) {
            log.error("Backup failed for job id {}", backupJob.getId(), e);
            // Update to FAILED
            updateStatus(backupJob, BackupStatus.FAILED, e.getMessage());
        }
    }
    private void updateStatus(BackupJob backupJob, BackupStatus status, String errorMessage) {
        // Fetch fresh entity to ensure we modify the latest state
        BackupJob currentJob = backupJobRepository.findById(backupJob.getId())
                .orElse(backupJob);
        currentJob.setStatus(status);
        if (status == BackupStatus.IN_PROGRESS) {
            currentJob.setStartedAt(LocalDateTime.now());
        } else if (status == BackupStatus.COMPLETED || status == BackupStatus.FAILED) {
            currentJob.setCompletedAt(LocalDateTime.now());
        }
        if (errorMessage != null) {
            currentJob.setErrorMessage(errorMessage);
        }
        backupJobRepository.save(currentJob);
    }
}
