package com.shravan.resilientdb_engine.backup.strategy.impl;
import com.shravan.resilientdb_engine.backup.entity.BackupJob;
import com.shravan.resilientdb_engine.backup.entity.DatabaseType;
import com.shravan.resilientdb_engine.backup.strategy.BackupStrategy;
import org.springframework.stereotype.Component;
/**
 * Strategy implementation for MongoDB database backups.
 */
@Component
public class MongoBackupStrategy implements BackupStrategy {
    @Override
    public DatabaseType getSupportedType() {
        return DatabaseType.MONGODB;
    }
    @Override
    public void executeBackup(BackupJob backupJob) throws Exception {
        // Simulation of MongoDB backup execution
        System.out.println("Executing MongoDB backup for job: " + backupJob.getJobName());
        Thread.sleep(3000); // Simulate work
    }
}