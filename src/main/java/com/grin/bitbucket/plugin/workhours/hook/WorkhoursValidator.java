package com.grin.bitbucket.plugin.workhours.hook;

import com.atlassian.bitbucket.i18n.I18nService;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.setting.RepositorySettingsValidator;
import com.atlassian.bitbucket.setting.Settings;
import com.atlassian.bitbucket.setting.SettingsValidationErrors;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import static com.grin.bitbucket.plugin.workhours.hook.WorkhoursHook.TIME_FORMATTER;


public class WorkhoursValidator implements RepositorySettingsValidator {
    private final I18nService i18nService;

    @Autowired
    public WorkhoursValidator(@ComponentImport I18nService i18nService) {
        this.i18nService = i18nService;
    }

    @Override
    public void validate(@Nonnull Settings settings, @Nonnull SettingsValidationErrors errors, @Nonnull Repository repository) {
        LocalTime startTime = LocalTime.now(),
                endTime = LocalTime.now();
        boolean isStartTimeParsed = false,
                isEndTimeParsed = false;

        try {
            startTime = LocalTime.parse(settings.getString("startTime"), TIME_FORMATTER);
            isStartTimeParsed = true;
        } catch (DateTimeParseException e) {
            setFieldError(errors, "startTime");
        }
        try {
            endTime = LocalTime.parse(settings.getString("endTime"), TIME_FORMATTER);
            isEndTimeParsed = true;
        } catch (DateTimeParseException e) {
            setFieldError(errors, "endTime");
        }
        if (isStartTimeParsed && isEndTimeParsed && (startTime.isAfter(endTime) || startTime.equals(endTime))) {
            errors.addFormError(i18nService.getMessage("workhours.config.timeError", "Time error"));
        }
    }

    private void setFieldError(@Nonnull SettingsValidationErrors errors, String field) {
        errors.addFieldError(field, i18nService.getMessage("workhours.config.formatError." + field, "Format error"));
    }
}


