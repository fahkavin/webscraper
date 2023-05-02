package com.test.json.repository;

import com.test.json.model.DataModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JsonRepository {
    List<String> findSymbol();

    int insertSymbol(List<DataModel> dataModelList);
}
