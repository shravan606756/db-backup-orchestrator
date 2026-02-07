package com.shravan.resilientdb_engine.backup.service.impl;
import com.shravan.resilientdb_engine.backup.dto.CreateBackupRequest;
import com.shravan.resilientdb_engine.backup.entity.BackupJob;
import com.shravan.resilientdb_engine.backup.entity.BackupStatus;
import com.shravan.resilientdb_engine.backup.entity.DatabaseType;
import com.shravan.resilientdb_engine.backup.repository.BackupJobRepository;
import com.shravan.resilientdb_engine.backup.repository.DatabaseConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class BackupServiceImplTest {
    @Mock
    private BackupJobRepository backupJobRepository;
    @Mock
    private DatabaseConfigRepository databaseConfigRepository;
    @InjectMocks
    private BackupServiceImpl backupService;
    private BackupJob backupJob;
    @BeforeEach
    void setUp() {
        backupJob = new BackupJob();
        backupJob.setId(1L);
        backupJob.setJobName("Test Job");
        backupJob.setDatabaseType(DatabaseType.POSTGRESQL);
        backupJob.setStatus(BackupStatus.PENDING);
    }
    @Test
    void createBackupJob_ShouldSaveJob() {
        // Arrange
        when(backupJobRepository.save(any(BackupJob.class))).thenReturn(backupJob);
        CreateBackupRequest request = new CreateBackupRequest("Test Job", DatabaseType.POSTGRESQL);
        // Act
        BackupJob result = backupService.createBackupJob(request);
        // Assert
        assertNotNull(result);
        assertEquals("Test Job", result.getJobName());
        verify(backupJobRepository, times(1)).save(any(BackupJob.class));
    }
    @Test
    void updateBackupStatus_ShouldUpdateStatus() {
        // Arrange
        when(backupJobRepository.findById(1L)).thenReturn(Optional.of(backupJob));
        when(backupJobRepository.save(any(BackupJob.class))).thenReturn(backupJob);
        // Act
        // PENDING -> IN_PROGRESS is valid
        BackupJob result = backupService.updateBackupStatus(1L, BackupStatus.IN_PROGRESS);
        // Assert
        assertEquals(BackupStatus.IN_PROGRESS, result.getStatus());
        verify(backupJobRepository, times(1)).save(backupJob);
    }
    @Test
    void updateBackupStatus_ShouldThrowException_WhenTransitionIsInvalid() {
        // Arrange
        backupJob.setStatus(BackupStatus.COMPLETED); // Already completed
        when(backupJobRepository.findById(1L)).thenReturn(Optional.of(backupJob));
        // Act & Assert
        // COMPLETED -> IN_PROGRESS is invalid
        assertThrows(IllegalStateException.class, () -> backupService.updateBackupStatus(1L, BackupStatus.IN_PROGRESS));
        verify(backupJobRepository, never()).save(any(BackupJob.class));
    }
    @Test
    void getBackupJobById_ShouldThrowException_WhenJobNotFound() {
        // Arrange
        when(backupJobRepository.findById(99L)).thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(RuntimeException.class, () -> backupService.getBackupJobById(99L));
    }
}