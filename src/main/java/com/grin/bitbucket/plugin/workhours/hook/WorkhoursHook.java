package com.grin.bitbucket.plugin.workhours.hook;

import com.atlassian.bitbucket.hook.repository.PreRepositoryHook;
import com.atlassian.bitbucket.hook.repository.PreRepositoryHookContext;
import com.atlassian.bitbucket.hook.repository.RepositoryHookRequest;
import com.atlassian.bitbucket.hook.repository.RepositoryHookResult;
import com.atlassian.bitbucket.i18n.I18nService;
import com.atlassian.bitbucket.setting.Settings;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.*;

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
        Map<DayOfWeek, Boolean> workdays = new HashMap<DayOfWeek, Boolean>();
        workdays.put(DayOfWeek.MONDAY, context.getSettings().getBoolean("monday", false));
        workdays.put(DayOfWeek.TUESDAY, context.getSettings().getBoolean("tuesday", false));
        workdays.put(DayOfWeek.WEDNESDAY, context.getSettings().getBoolean("wednesday", false));
        workdays.put(DayOfWeek.THURSDAY, context.getSettings().getBoolean("thursday", false));
        workdays.put(DayOfWeek.FRIDAY, context.getSettings().getBoolean("friday", false));
        workdays.put(DayOfWeek.SATURDAY, context.getSettings().getBoolean("saturday", false));
        workdays.put(DayOfWeek.SUNDAY, context.getSettings().getBoolean("sunday", false));

        boolean isWorkday = workdays.get(LocalDate.now().getDayOfWeek());

        if (now.isBefore(startTime) || now.isAfter(endTime) || !isWorkday) {
            String summaryMesssage = i18nService.getMessage("workhours.summary");
            String detailedMessage = i18nService.getMessage("workhours.detailed", startTime, endTime);
            return RepositoryHookResult.rejected(summaryMesssage, detailedMessage);
        }
        return RepositoryHookResult.accepted();
    }
}
