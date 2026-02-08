package com.shravan.resilientdb_engine.backup.strategy.impl;
import com.shravan.resilientdb_engine.backup.entity.BackupJob;
import com.shravan.resilientdb_engine.backup.entity.DatabaseType;
import com.shravan.resilientdb_engine.backup.strategy.BackupStrategy;
import org.springframework.stereotype.Component;
/**
 * Strategy implementation for MySQL database backups.
 */
@Component
public class MySQLBackupStrategy implements BackupStrategy {
    @Override
    public DatabaseType getSupportedType() {
        return DatabaseType.MYSQL;
    }
    @Override
    public void executeBackup(BackupJob backupJob) throws Exception {
        // Simulation of backup execution
        System.out.println("Executing MySQL backup for job: " + backupJob.getJobName());
        Thread.sleep(3000); // Simulate work
    }
}