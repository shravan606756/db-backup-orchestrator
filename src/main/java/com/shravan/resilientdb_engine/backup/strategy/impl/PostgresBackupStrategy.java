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
    public DatabaseType getSupportedType() {
        return DatabaseType.POSTGRESQL;
    }
    @Override
    public void executeBackup(BackupJob backupJob) throws Exception {
        // Simulation of PostgreSQL backup execution
        System.out.println("Executing PostgreSQL backup for job: " + backupJob.getJobName());
        Thread.sleep(3000); // Simulate work
    }
}