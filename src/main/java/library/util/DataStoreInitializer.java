package library.util;

import library.config.AppPaths;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Ensures CSV data files exist on first launch by copying bundled templates
 * or writing default header rows.
 */
public final class DataStoreInitializer {

    private static final String[] TEMPLATE_FILES = {
            "books.csv",
            "users.csv",
            "users_auth.csv",
            "transactions.csv",
            "removed_items.csv"
    };

    private static final String[] DEFAULT_HEADERS = {
            "bookId,title,author,genre,quantity,available",
            "userId,name,email",
            "username,fullName,email,phone,passwordHash,hintWord",
            "transactionId,bookId,bookName,userId,userName,issueDate,returnDate,status",
            "itemType,itemId,itemName,removedAt"
    };

    private DataStoreInitializer() {}

    public static void ensureDataFiles() {
        File dataDir = new File(AppPaths.getDataDirectory());
        if (!dataDir.exists() && !dataDir.mkdirs()) {
            throw new IllegalStateException("Could not create data directory: " + dataDir.getAbsolutePath());
        }
        for (int i = 0; i < TEMPLATE_FILES.length; i++) {
            File target = new File(dataDir, TEMPLATE_FILES[i]);
            if (target.exists()) {
                continue;
            }
            if (!copyTemplate(TEMPLATE_FILES[i], target)) {
                writeHeader(target, DEFAULT_HEADERS[i]);
            }
        }
    }

    private static boolean copyTemplate(String name, File target) {
        String resourcePath = "/templates/" + name;
        try (InputStream in = DataStoreInitializer.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                return false;
            }
            Files.copy(in, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            StartupErrorHandler.log("[DataStoreInitializer] Template copy failed for " + name, e);
            return false;
        }
    }

    private static void writeHeader(File target, String header) {
        try (BufferedWriter w = Files.newBufferedWriter(target.toPath(), StandardCharsets.UTF_8)) {
            w.write(header);
            w.newLine();
        } catch (IOException e) {
            throw new IllegalStateException("Could not create " + target.getAbsolutePath(), e);
        }
    }
}
