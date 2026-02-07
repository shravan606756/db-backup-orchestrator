package com.shravan.resilientdb_engine.backup.dto;
import com.shravan.resilientdb_engine.backup.entity.BackupJob;
import com.shravan.resilientdb_engine.backup.entity.BackupStatus;
import com.shravan.resilientdb_engine.backup.entity.DatabaseType;
import java.time.LocalDateTime;
/**
 * Response DTO for BackupJob information.
 */
public class BackupJobResponse {
    private Long id;
    private String jobName;
    private BackupStatus status;
    private DatabaseType databaseType;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    public BackupJobResponse(Long id, String jobName, BackupStatus status, DatabaseType databaseType,
                             LocalDateTime createdAt, LocalDateTime startedAt, LocalDateTime completedAt) {
        this.id = id;
        this.jobName = jobName;
        this.status = status;
        this.databaseType = databaseType;
        this.createdAt = createdAt;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
    }
    public static BackupJobResponse fromEntity(BackupJob job) {
        return new BackupJobResponse(
                job.getId(),
                job.getJobName(),
                job.getStatus(),
                job.getDatabaseType(),
                job.getCreatedAt(),
                job.getStartedAt(),
                job.getCompletedAt());
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getJobName() {
        return jobName;
    }
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
    public BackupStatus getStatus() {
        return status;
    }
    public void setStatus(BackupStatus status) {
        this.status = status;
    }
    public DatabaseType getDatabaseType() {
        return databaseType;
    }
    public void setDatabaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getStartedAt() {
        return startedAt;
    }
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}