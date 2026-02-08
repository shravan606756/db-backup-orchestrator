package com.shravan.resilientdb_engine.backup.event;
import com.shravan.resilientdb_engine.backup.entity.BackupJob;
import com.shravan.resilientdb_engine.backup.entity.BackupStatus;
import com.shravan.resilientdb_engine.backup.repository.BackupJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
/**
 * Listener for backup status change events.
 */
@Component
public class BackupStatusEventListener {
    private static final Logger log = LoggerFactory.getLogger(BackupStatusEventListener.class);
    private final BackupJobRepository backupJobRepository;
    public BackupStatusEventListener(BackupJobRepository backupJobRepository) {
        this.backupJobRepository = backupJobRepository;
    }
    @EventListener
    public void handleStatusChange(BackupStatusChangedEvent event) {
        BackupJob backupJob = event.getBackupJob();
        BackupStatus newStatus = event.getNewStatus();
        log.info("Backup job status changed from {} to {} for job: {}",
                event.getOldStatus(),
                newStatus,
                backupJob.getJobName());
        // Retry Logic
        if (newStatus == BackupStatus.FAILED) {
            // Check if retries are available
            if (backupJob.getRetryCount() < backupJob.getMaxRetries()) {
                log.warn("Retrying backup job {} attempt {}/{}",
                        backupJob.getId(),
                        backupJob.getRetryCount() + 1,
                        backupJob.getMaxRetries());
                // Increment retry count
                backupJob.setRetryCount(backupJob.getRetryCount() + 1);
                // Reset status to PENDING so it gets picked up by BackupJobProcessorService
                backupJob.setStatus(BackupStatus.PENDING);
                backupJob.setErrorMessage(null); // Clear error message for retry
                // Save updated job state
                backupJobRepository.save(backupJob);
            } else {
                log.error("Backup job {} failed permanently after {} attempts.",
                        backupJob.getJobName(),
                        backupJob.getMaxRetries());
            }
        }
    }
}