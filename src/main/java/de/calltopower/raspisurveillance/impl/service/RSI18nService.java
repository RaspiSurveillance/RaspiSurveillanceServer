package de.calltopower.raspisurveillance.impl.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.calltopower.raspisurveillance.api.service.RSService;
import de.calltopower.raspisurveillance.impl.enums.RSLanguage;
import de.calltopower.raspisurveillance.impl.exception.RSNotFoundException;
import de.calltopower.raspisurveillance.impl.model.RSLanguagesModel;
import de.calltopower.raspisurveillance.impl.model.RSTranslationsModel;
import de.calltopower.raspisurveillance.impl.utils.RSFileUtils;

/**
 * Service for i18n results
 */
@Service
public class RSI18nService implements RSService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RSI18nService.class);

    private RSFileUtils fileUtils;

    private static final String I18N_FOLDER_NAME = "i18n";

    /**
     * Initializes the service
     * 
     * @param fileUtils The file utilities
     */
    @Autowired
    public RSI18nService(RSFileUtils fileUtils) {
        this.fileUtils = fileUtils;
    }

    /**
     * Returns all languages
     * 
     * @return all languages
     */
    public RSLanguagesModel getLanguages() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Returning all languages");
        }

        return RSLanguagesModel.builder()
                .languages(
                        Arrays.stream(RSLanguage.values()).collect(Collectors.toMap(l -> l.getId(), l -> l.getName())))
                .build();
    }

    /**
     * Returns a specific language file
     * 
     * @param id Language ID
     * @return a specific language file
     */
    public RSTranslationsModel getLanguageFile(String id) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Returning all languages");
        }

        String langFileName = String.format("%s/%s.json", I18N_FOLDER_NAME, id);
        try {
            return RSTranslationsModel.builder().translations(fileUtils.getResourceFileAsString(langFileName)).build();
        } catch (IOException e) {
            throw new RSNotFoundException(String.format("Language file for language with id \"%s\" not found", id));
        }
    }

}
