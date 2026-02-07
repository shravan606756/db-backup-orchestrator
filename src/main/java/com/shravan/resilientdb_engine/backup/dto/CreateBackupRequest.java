package com.shravan.resilientdb_engine.backup.dto;
import com.shravan.resilientdb_engine.backup.entity.DatabaseType;
/**
 * DTO for creating a new backup job request.
 */
public class CreateBackupRequest {
    private String jobName;
    private DatabaseType databaseType;
    public CreateBackupRequest() {
    }
    public CreateBackupRequest(String jobName, DatabaseType databaseType) {
        this.jobName = jobName;
        this.databaseType = databaseType;
    }
    public String getJobName() {
        return jobName;
    }
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
    public DatabaseType getDatabaseType() {
        return databaseType;
    }
    public void setDatabaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }
}
