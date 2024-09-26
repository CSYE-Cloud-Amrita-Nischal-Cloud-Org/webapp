package com.csye6225.webapp.services.impl;

import com.csye6225.webapp.services.DbConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Service
@Slf4j
public class DbConnectionImpl implements DbConnection {

    DataSource dataSource;

    @Autowired
    DbConnectionImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Boolean isDbConnected() {
        if (dataSource == null) {
            log.error("[DbConnection] -> DataSource is null");
            return null;
        }

        try (Connection ignored = dataSource.getConnection()) {
            log.info("[DbConnection] -> Database connection established");
            return true;
        } catch (SQLException ignored) {
            log.info("[DbConnection] -> Database connection failed");
            return false;
        }
    }
}
