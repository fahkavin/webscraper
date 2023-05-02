package com.test.json.repository.Impl;

import com.test.json.model.DataModel;
import com.test.json.repository.JsonRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Repository(value = "JsonRepository")
public class JsonRepositoryImpl implements JsonRepository {
    private JdbcTemplate jdbcTemplate;

    public JsonRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<String> findSymbol() {

        String sql = "  SELECT SYMBOL FROM MS_SET   ";

        return this.jdbcTemplate.query(sql, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                String model;
                int cols = 1;
                model = rs.getString(cols++);
                return model;
            }
        });
    }

    @Override
    public int insertSymbol(List<DataModel> dataModelList) {
        List<Object> params = new ArrayList<>();
        String sql = "INSERT INTO TR_TRANSACTION\n" +
                "(SYMBOL, TRANSACTION_DATE, OPEN_PRICE, MAX_PRICE, MIN_PRICE, CLOSE_PRICE, CHANGE_PRICE,\n" +
                "                CHANGE_RATIO, NO_OF_STOCK, VOLUME, REASON, STATUS, BATCH_ID)\n" +
                "VALUES\n";

        StringJoiner stringJoiner = new StringJoiner(",");
        for (DataModel dataModel: dataModelList) {
            stringJoiner.add("(?,?,?,?,?,?,?,?,?,?,?,?,?)");
            params.add(dataModel.getSymbol());
            params.add(dataModel.getTransactionDate());
            params.add(dataModel.getOpenPrice());
            params.add(dataModel.getMaxPrice());
            params.add(dataModel.getMinPrice());
            params.add(dataModel.getClosePrice());
            params.add(dataModel.getChangePrice());
            params.add(dataModel.getChangeRatio());
            params.add(dataModel.getNoOfStock());
            params.add(dataModel.getVolume());
            params.add(dataModel.getReason());
            params.add(dataModel.getStatus());
            params.add(dataModel.getBatchId());
        }
        sql += stringJoiner.toString();
        return this.jdbcTemplate.update(sql, params.toArray());
    }

}
