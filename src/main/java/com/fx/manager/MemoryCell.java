package com.fx.manager;

import lombok.Data;

@Data
public class MemoryCell {
    private byte[] storeInfo = new byte[4];

    public MemoryCell(byte [] cellInfo){
        if(cellInfo.length > 4){
            throw new RuntimeException();
        }
        this.storeInfo = cellInfo;
    }
}
