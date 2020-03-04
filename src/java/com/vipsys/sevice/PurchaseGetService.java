package com.vipsys.sevice;

import com.vipsys.dto.PurchaseDTO;
import com.vipsys.model.PurchaseHis;

import java.util.List;

public interface PurchaseGetService {
    List<PurchaseHis> get(String where, PurchaseDTO phDTO) throws Exception;
    List<PurchaseHis> getAll();
}
