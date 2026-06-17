package com.fx.file;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Directory extends MemoryEntity {
    public List<MemoryEntity> innerFiles = new ArrayList<>();
}
