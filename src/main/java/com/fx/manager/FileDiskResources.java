package com.fx.manager;

import com.fx.file.Directory;
import com.fx.file.File;
import com.fx.file.MemoryEntity;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.IntStream;

public class FileDiskResources {

    private static boolean initialized = false;

    private static MemoryCell[] diskStorage;

    private static List<Integer> freeIndex;

    private final Map<MemoryRecord, List<Integer>> memoryRecordListMap = new HashMap();

    public synchronized boolean initStorage(int storageSize) {
        if (!initialized) {
            diskStorage = new MemoryCell[storageSize];
            freeIndex = IntStream.range(0, storageSize).boxed().toList();
            initialized = true;
        }
        return true;
    }

    public synchronized boolean saveRecord(MemoryEntity memoryEntity) {
        MemoryRecord record = new MemoryRecord(memoryEntity);
        byte[] payload;

        if (record.getIsDirectory()) {
            var directory = (Directory) memoryEntity;
            payload = serializeList(directory.getInnerFiles());
        } else {
            var file = (File) memoryEntity;
            payload = file.getPayload().getBytes(StandardCharsets.UTF_8);
        }

        var indexes = saveResources(payload);

        this.memoryRecordListMap.put(record, indexes);

        return true;
    }

    public synchronized List<Integer> saveResources(byte[] payload) {
        List<Integer> savedIndex = new ArrayList<>();
        byte[] resultByte;
        for (int chunkStart = 0; chunkStart <= payload.length; chunkStart += 4) {
            var saveIndex = freeIndex.stream().findFirst().orElseThrow();

            int chunkEnd = Math.min(chunkStart + 4, payload.length);

            resultByte = Arrays.copyOfRange(payload, chunkStart, chunkEnd);

            MemoryCell saveCell = new MemoryCell(resultByte);

            diskStorage[saveIndex] = saveCell;

            freeIndex.remove(saveIndex);
            savedIndex.add(saveIndex);
        }
        return savedIndex;
    }

    private byte[] serializeList(List<MemoryEntity> list) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(list);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
