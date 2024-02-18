package pl.materus.ghrepo.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.NonNull;
import org.springframework.util.FileCopyUtils;

public class ResourceReaderUtil {

    private static ResourceLoader resourceLoader = new DefaultResourceLoader();

    public static String asString(@NonNull String resourcePath) {

        Resource resource = resourceLoader.getResource(resourcePath);
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
