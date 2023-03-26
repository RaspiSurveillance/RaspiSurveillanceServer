package de.calltopower.raspisurveillance.impl.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.calltopower.raspisurveillance.api.controller.RSController;
import de.calltopower.raspisurveillance.impl.dto.RSLanguagesDto;
import de.calltopower.raspisurveillance.impl.dto.RSTranslationsDto;
import de.calltopower.raspisurveillance.impl.dtoservice.RSLanguagesDtoService;
import de.calltopower.raspisurveillance.impl.dtoservice.RSTranslationsDtoService;
import de.calltopower.raspisurveillance.impl.service.RSI18nService;
import jakarta.validation.constraints.NotNull;

/**
 * I18n controller
 */
@RestController
@RequestMapping(path = RSI18nController.PATH)
public class RSI18nController implements RSController {

    /**
     * The controller path
     */
    public static final String PATH = APIPATH + "/i18n";

    private static final Logger LOGGER = LoggerFactory.getLogger(RSI18nController.class);

    private RSI18nService i18nService;
    private RSLanguagesDtoService languagesDtoService;
    private RSTranslationsDtoService translationsDtoService;

    /**
     * Initializes the controller
     * 
     * @param userDtoService         Injected service
     * @param languagesDtoService    Injected languages DTO service
     * @param translationsDtoService Injected translations DTO service
     */
    @Autowired
    public RSI18nController(RSI18nService i18nService, RSLanguagesDtoService languagesDtoService,
            RSTranslationsDtoService translationsDtoService) {
        this.i18nService = i18nService;
        this.languagesDtoService = languagesDtoService;
        this.translationsDtoService = translationsDtoService;
    }

    @SuppressWarnings("javadoc")
    @GetMapping(path = "/languages", produces = MediaType.APPLICATION_JSON_VALUE)
    public RSLanguagesDto getLanguages() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Requested languages");
        }

        return languagesDtoService.convert(i18nService.getLanguages());
    }

    @SuppressWarnings("javadoc")
    @GetMapping(path = "/languages/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public RSTranslationsDto getTranslations(@NotNull @PathVariable(name = "id") String id) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Requested language");
        }

        return translationsDtoService.convert(i18nService.getLanguageFile(id));
    }

    @Override
    public String getPath() {
        return PATH;
    }

}
