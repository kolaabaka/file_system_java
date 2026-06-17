package com.fx.manager;


import com.fx.file.Directory;
import com.fx.file.MemoryEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

@Getter
@EqualsAndHashCode
public class MemoryRecord implements Serializable {

    private Boolean isDirectory;
    private Long access;
    private String name;

    public MemoryRecord(MemoryEntity memoryEntity) {
        this.isDirectory = memoryEntity instanceof Directory;
        this.access = memoryEntity.getAccess();
        this.name = memoryEntity.getName();
    }
}
