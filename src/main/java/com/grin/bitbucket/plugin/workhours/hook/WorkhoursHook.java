package com.grin.bitbucket.plugin.workhours.hook;

import com.atlassian.bitbucket.hook.repository.PreRepositoryHook;
import com.atlassian.bitbucket.hook.repository.PreRepositoryHookContext;
import com.atlassian.bitbucket.hook.repository.RepositoryHookRequest;
import com.atlassian.bitbucket.hook.repository.RepositoryHookResult;
import com.atlassian.bitbucket.i18n.I18nService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;

public class WorkhoursHook implements PreRepositoryHook<RepositoryHookRequest> {
    private final I18nService i18nService;
    final static DateTimeFormatter TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.HOUR_OF_DAY, 1, 2, SignStyle.NOT_NEGATIVE)
            .appendLiteral(":")
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .toFormatter();

    @Autowired
    public WorkhoursHook(@ComponentImport I18nService i18nService) {
        this.i18nService = i18nService;
    }

    @Nonnull
    @Override
    public RepositoryHookResult preUpdate(PreRepositoryHookContext context, RepositoryHookRequest request) {
        LocalTime startTime = LocalTime.parse(context.getSettings().getString("startTime"), TIME_FORMATTER);
        LocalTime endTime = LocalTime.parse(context.getSettings().getString("endTime"), TIME_FORMATTER);
        LocalTime now = LocalTime.now();

        if (now.isBefore(startTime) || now.isAfter(endTime)) {
            String summaryMesssage = i18nService.getMessage("workhours.summary");
            String detailedMessage = i18nService.getMessage("workhours.detailed", startTime, endTime);
            return RepositoryHookResult.rejected(summaryMesssage, detailedMessage);
        }
        return RepositoryHookResult.accepted();
    }
}
