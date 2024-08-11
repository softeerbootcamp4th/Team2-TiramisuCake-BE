package com.softeer.backend.global.staticresources.util;

import com.softeer.backend.fo_domain.fcfs.domain.FcfsSetting;
import com.softeer.backend.global.staticresources.domain.StaticResources;
import com.softeer.backend.global.staticresources.repository.StaticResourcesRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StaticResourcesUtil {

    private final StaticResourcesRepository staticResourcesRepository;

    private final Map<String, String> s3Urls = new HashMap<>();

    @PostConstruct
    public void init() {
        loadInitialData();
    }

    public void loadInitialData() {
        List<StaticResources> staticResourcesList = staticResourcesRepository.findAll();

        s3Urls.putAll(
                staticResourcesList.stream()
                        .collect(Collectors.toMap(
                                StaticResources::getFileName, // Key mapper
                                StaticResources::getFileUrl    // Value mapper
                        ))
        );
    }

    public String getFileUrl(String filename) {
        return s3Urls.get(filename);
    }
}
