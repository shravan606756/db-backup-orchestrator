package com.shravan.resilientdb_engine.backup.strategy.impl;
import com.shravan.resilientdb_engine.backup.entity.BackupJob;
import com.shravan.resilientdb_engine.backup.entity.DatabaseType;
import com.shravan.resilientdb_engine.backup.strategy.BackupStrategy;
import org.springframework.stereotype.Component;
/**
 * Strategy implementation for Oracle database backups.
 */
@Component
public class OracleBackupStrategy implements BackupStrategy {
    @Override
    public DatabaseType getSupportedType() {
        return DatabaseType.ORACLE;
    }
    @Override
    public void executeBackup(BackupJob backupJob) throws Exception {
        // Simulation of Oracle backup execution
        System.out.println("Executing Oracle database backup for job: " + backupJob.getJobName());
        Thread.sleep(3000); // Simulate work
    }
}