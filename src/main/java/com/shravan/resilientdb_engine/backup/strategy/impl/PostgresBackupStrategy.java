package com.shravan.resilientdb_engine.backup.strategy.impl;
import com.shravan.resilientdb_engine.backup.entity.BackupJob;
import com.shravan.resilientdb_engine.backup.entity.DatabaseType;
import com.shravan.resilientdb_engine.backup.strategy.BackupStrategy;
import org.springframework.stereotype.Component;
/**
 * Strategy implementation for PostgreSQL database backups.
 */
@Component
public class PostgresBackupStrategy implements BackupStrategy {
    @Override
    public boolean supports(DatabaseType databaseType) {
        return databaseType == DatabaseType.POSTGRESQL;
    }
    @Override
    public void executeBackup(BackupJob backupJob) {
        // Simulation of PostgreSQL backup execution
        System.out.println("Executing PostgreSQL backup for job: " + backupJob.getJobName());
    }
}