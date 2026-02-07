package com.shravan.resilientdb_engine.backup.strategy;
import com.shravan.resilientdb_engine.backup.entity.BackupJob;
import com.shravan.resilientdb_engine.backup.entity.DatabaseType;
/**
 * Strategy interface for executing database backups.
 * Implementations define how to backup specific database types.
 */
public interface BackupStrategy {
    /**
     * Checks if the strategy supports the given database type.
     *
     * @param databaseType the database type to check
     * @return true if supported, false otherwise
     */
    boolean supports(DatabaseType databaseType);
    /**
     * Executes the backup operation for the given job.
     *
     * @param backupJob the backup job details
     */
    void executeBackup(BackupJob backupJob);
}
