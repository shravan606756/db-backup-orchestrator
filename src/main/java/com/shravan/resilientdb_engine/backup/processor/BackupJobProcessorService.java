package com.shravan.resilientdb_engine.backup.processor;
import com.shravan.resilientdb_engine.backup.entity.BackupJob;
import com.shravan.resilientdb_engine.backup.entity.BackupStatus;
import com.shravan.resilientdb_engine.backup.entity.DatabaseType;
import com.shravan.resilientdb_engine.backup.repository.BackupJobRepository;
import com.shravan.resilientdb_engine.backup.strategy.BackupStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
@Service
public class BackupJobProcessorService {
    private static final Logger log = LoggerFactory.getLogger(BackupJobProcessorService.class);
    private final BackupJobRepository backupJobRepository;
    private final Map<DatabaseType, BackupStrategy> strategyMap;
    @Autowired
    @Lazy
    private BackupJobProcessorService self;
    public BackupJobProcessorService(BackupJobRepository backupJobRepository, List<BackupStrategy> strategies) {
        this.backupJobRepository = backupJobRepository;
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(BackupStrategy::getSupportedType, Function.identity()));
    }
    @Scheduled(fixedDelay = 5000)
    public void processPendingJobs() {
        List<BackupJob> pendingJobs = backupJobRepository.findByStatus(BackupStatus.PENDING);
        for (BackupJob job : pendingJobs) {
            // Prevent double-processing by updating status synchronously
            job.setStatus(BackupStatus.IN_PROGRESS);
            job.setStartedAt(LocalDateTime.now());
            backupJobRepository.save(job);
            log.info("Starting processing for job ID: {}", job.getId());
            self.processJob(job);
        }
    }
    @Async
    public void processJob(BackupJob job) {
        try {
            BackupStrategy strategy = strategyMap.get(job.getDatabaseType());
            if (strategy == null) {
                throw new UnsupportedOperationException(
                        "No strategy found for database type: " + job.getDatabaseType());
            }
            strategy.executeBackup(job);
            // Update status -> COMPLETED
            job.setStatus(BackupStatus.COMPLETED);
            job.setCompletedAt(LocalDateTime.now());
            backupJobRepository.save(job);
            log.info("Job ID: {} completed successfully", job.getId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            handleFailure(job, "Job interrupted");
        } catch (Exception e) {
            handleFailure(job, "Processing failed: " + e.getMessage());
        }
    }
    private void handleFailure(BackupJob job, String errorMessage) {
        log.error("Job ID: {} failed. Reason: {}", job.getId(), errorMessage);
        job.setStatus(BackupStatus.FAILED);
        job.setErrorMessage(errorMessage);
        backupJobRepository.save(job);
    }
}