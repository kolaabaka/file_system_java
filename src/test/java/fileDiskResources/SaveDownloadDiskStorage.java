package fileDiskResources;

import com.fx.file.File;
import com.fx.manager.FileDiskResources;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

public class SaveDownloadDiskStorage {

    private String DEFAULT_FILENAME = "FILE_NAME";
    private String CONSTANT_STRING = "String FOR teSt!";
    private static int STORAGE_SIZE = 50;

    private static FileDiskResources fileDiskResources;

    @BeforeAll
    public static void initResources() {
        fileDiskResources = new FileDiskResources();
        fileDiskResources.initStorage(STORAGE_SIZE);
    }

    @Test
    @Order(1)
    public void testSave() {
        File file = new File();
        file.setName(DEFAULT_FILENAME);
        file.setAccess(0L);
        file.setPayload(CONSTANT_STRING);

        fileDiskResources.saveRecord(file);

        byte[] result = fileDiskResources.getResourcesByName(DEFAULT_FILENAME);
        var savedString = new String(result);

        Assertions.assertEquals(savedString, CONSTANT_STRING);
    }

    @Test
    @Order(2)
    public void testDelete(){
        fileDiskResources.deleteRecord(DEFAULT_FILENAME);
        Assertions.assertEquals(STORAGE_SIZE, fileDiskResources.getFreeSize());
    }
}
