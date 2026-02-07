package com.shravan.resilientdb_engine.backup.service;
import com.shravan.resilientdb_engine.backup.dto.CreateBackupRequest;
import com.shravan.resilientdb_engine.backup.entity.BackupJob;
import com.shravan.resilientdb_engine.backup.entity.BackupStatus;
import com.shravan.resilientdb_engine.backup.entity.DatabaseType;
import java.util.List;
/**
 * Service interface for managing backup jobs.
 * Defines business logic for creating, retrieving, and updating backup
 * operations.
 */
public interface BackupService {
    /**
     * Initiates a new backup job.
     *
     * @param request the backup creation request
     * @return the created BackupJob entity
     */
    BackupJob createBackupJob(CreateBackupRequest request);
    /**
     * Retrieves a backup job by its ID.
     *
     * @param id the unique identifier of the backup job
     * @return the found BackupJob
     * @throws RuntimeException if the job is not found (specific exception to be
     *                          defined)
     */
    BackupJob getBackupJobById(Long id);
    /**
     * Retrieves all backup jobs in the system.
     *
     * @return a list of all backup jobs
     */
    List<BackupJob> getAllBackupJobs();
    /**
     * Retrieves backup jobs filtered by their status.
     *
     * @param status the status to filter by
     * @return a list of matching backup jobs
     */
    List<BackupJob> getBackupJobsByStatus(BackupStatus status);
    /**
     * Updates the status of an existing backup job.
     *
     * @param id     the ID of the backup job to update
     * @param status the new status to set
     * @return the updated BackupJob entity
     */
    BackupJob updateBackupStatus(Long id, BackupStatus status);
    /**
     * Retrieves the most recent backup jobs.
     * Useful for dashboard or monitoring views.
     *
     * @return a list of the latest backup jobs
     */
    List<BackupJob> getLatestBackups();
}
