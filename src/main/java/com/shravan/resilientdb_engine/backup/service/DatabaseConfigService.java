package com.shravan.resilientdb_engine.backup.service;
import com.shravan.resilientdb_engine.backup.entity.DatabaseConfig;
import com.shravan.resilientdb_engine.backup.repository.DatabaseConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * Service for managing Database Configurations.
 */
@Service
public class DatabaseConfigService {
    private final DatabaseConfigRepository databaseConfigRepository;
    public DatabaseConfigService(DatabaseConfigRepository databaseConfigRepository) {
        this.databaseConfigRepository = databaseConfigRepository;
    }
    /**
     * Registers a new database configuration.
     *
     * @param config the configuration to save
     * @return the saved configuration
     */
    @Transactional
    public DatabaseConfig registerDatabase(DatabaseConfig config) {
        return databaseConfigRepository.save(config);
    }
    /**
     * Retrieves all registered database configurations.
     *
     * @return list of all database configurations
     */
    public java.util.List<DatabaseConfig> getAll() {
        return databaseConfigRepository.findAll();
    }
}