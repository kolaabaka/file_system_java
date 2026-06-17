package com.fx.file;

import lombok.Data;

import java.io.Serializable;

@Data
abstract public class MemoryEntity implements Serializable {

    public String name;

    public Long access;
}
