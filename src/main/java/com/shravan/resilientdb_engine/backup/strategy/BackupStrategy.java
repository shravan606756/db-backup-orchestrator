package com.shravan.resilientdb_engine.backup.strategy;
import com.shravan.resilientdb_engine.backup.entity.BackupJob;
import com.shravan.resilientdb_engine.backup.entity.DatabaseType;
/**
 * Strategy interface for executing database backups.
 * Implementations define how to backup specific database types.
 */
public interface BackupStrategy {
    /**
     * Gets the database type supported by this strategy.
     *
     * @return the supported DatabaseType
     */
    DatabaseType getSupportedType();
    /**
     * Executes the backup operation for the given job.
     *
     * @param backupJob the backup job details
     * @throws Exception if backup execution fails
     */
    void executeBackup(BackupJob backupJob) throws Exception;
}