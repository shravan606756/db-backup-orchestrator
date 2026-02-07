package com.shravan.resilientdb_engine.backup.strategy;
import com.shravan.resilientdb_engine.backup.entity.BackupJob;
import com.shravan.resilientdb_engine.backup.entity.DatabaseType;
import org.springframework.stereotype.Component;
/**
 * Strategy implementation for MySQL database backups.
 */
@Component
public class MySQLBackupStrategy implements BackupStrategy {
    @Override
    public boolean supports(DatabaseType databaseType) {
        return databaseType == DatabaseType.MYSQL;
    }
    @Override
    public void executeBackup(BackupJob backupJob) {
        // Simulation of backup execution
        System.out.println("Executing MySQL backup for job: " + backupJob.getJobName());
        // In a real implementation, this would trigger a mysqldump or similar command
    }
}
