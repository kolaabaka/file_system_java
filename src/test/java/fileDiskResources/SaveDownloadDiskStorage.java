package fileDiskResources;

import com.fx.file.File;
import com.fx.manager.FileDiskResources;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SaveDownloadDiskStorage {

    private String CONSTANT_STRING = "String FOR teSt!";

    private static FileDiskResources fileDiskResources;

    @BeforeAll
    public static void initResources() {
        fileDiskResources = new FileDiskResources();
        fileDiskResources.initStorage(50);
    }

    @Test
    public void testSave() {
        File file = new File();
        file.setName("FILE_NAME");
        file.setAccess(0L);
        file.setPayload(CONSTANT_STRING);

        fileDiskResources.saveRecord(file);

        byte[] result = fileDiskResources.getResourcesByName("FILE_NAME");
        var savedString = new String(result);

        Assertions.assertEquals(savedString, CONSTANT_STRING);
    }
}
