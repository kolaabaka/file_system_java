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

    public int getFreeSize(){
        return freeIndex.size();
    }

    public synchronized boolean initStorage(int storageSize) {
        if (!initialized) {
            diskStorage = new MemoryCell[storageSize];
            this.freeIndex = new ArrayList<>(IntStream.range(0, storageSize).boxed().toList());
            initialized = true;
        }
        return true;
    }

    public boolean deleteRecord(String name) {
        List<Integer> indexes = new ArrayList<>();
        for (var entity : memoryRecordListMap.keySet()) {
            if (entity.getName().equals(name)) {
                indexes = memoryRecordListMap.get(entity);
                memoryRecordListMap.remove(entity);
                break;
            }
        }
        if (indexes.isEmpty()) {
            throw new RuntimeException("Resources not found for name");
        }

        freeIndex.addAll(indexes);

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

    public byte[] getResourcesByName(String name) {
        List<Integer> indexes = new ArrayList<>();

        for (var entity : memoryRecordListMap.keySet()) {
            if (entity.getName().equals(name)) {
                indexes = memoryRecordListMap.get(entity);
                break;
            }
        }

        if (indexes.isEmpty()) {
            throw new RuntimeException("Resources not found for name");
        }

        List<Byte> resultPayload = new ArrayList<>();

        for (var i : indexes) {
            byte[] storeInfo = diskStorage[i].getStoreInfo();
            for (int j = 0; j < storeInfo.length; j++) {
                resultPayload.add(storeInfo[j]);
            }
        }

        byte[] array = new byte[resultPayload.size()];
        for (int i = 0; i < resultPayload.size(); i++) {
            array[i] = resultPayload.get(i);
        }

        return array;
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
