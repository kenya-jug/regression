package com.kenyajug.regression.controllers;
/*
 * MIT License
 *
 * Copyright (c) 2025 Kenya JUG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import com.kenyajug.regression.resources.LogsFilterResource;
import com.kenyajug.regression.services.HourlyLogStats;
import com.kenyajug.regression.services.LogChartData;
import com.kenyajug.regression.services.RetrievalService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
@Controller
public class LogsController {
    private final RetrievalService retrievalService;
    public LogsController(RetrievalService retrievalService) {
        this.retrievalService = retrievalService;
    }
    @GetMapping("/logs")
    public String listLogs(Model model){
        var filterDate = LocalDate.now();
        List<Long> infoCounts = retrievalService.composeChartDataBySeverity(filterDate,"INFO");
        List<Long> warnCounts = retrievalService.composeChartDataBySeverity(filterDate,"WARN");
        List<Long> errorCounts = retrievalService.composeChartDataBySeverity(filterDate,"ERROR");
        List<HourlyLogStats> stats = List.of(
                new HourlyLogStats("INFO", infoCounts),
                new HourlyLogStats("WARN", warnCounts),
                new HourlyLogStats("ERROR", errorCounts)
        );
        model.addAttribute("logChartData", new LogChartData(stats));

        var allApps = retrievalService.listAllApplications();
        var logs = retrievalService.listAllTodayLogs();
        var dataSourceList = retrievalService.listAllDataSources();
        var filter = new LogsFilterResource(filterDate,"All","","");
        model.addAttribute("selectedSeverity", "INFO");
        model.addAttribute("logs",logs);
        model.addAttribute("applications",allApps);
        model.addAttribute("selectedDate", filterDate);
        model.addAttribute("selectedAppId", "");
        model.addAttribute("selectedSource", "");
        model.addAttribute("logsFilter", filter);
        model.addAttribute("dataSources", dataSourceList);
        return "logs-list";
    }
    @GetMapping("/logs/filtered")
    public String listFilteredLogs(
            @Valid @ModelAttribute("logsFilter") LogsFilterResource filterResource,
            BindingResult bindingResult,
            Model model) {
        var filterDate = filterResource.selectedDate();
        switch (filterResource.selectedSeverity()){
            case "INFO" -> {
                List<Long> infoCounts = retrievalService.composeChartDataBySeverity(filterDate,"INFO");
                List<HourlyLogStats> stats = List.of(
                        new HourlyLogStats("INFO", infoCounts)
                );
                model.addAttribute("logChartData", new LogChartData(stats));
            }
            case "WARN" -> {
                List<Long> infoCounts = retrievalService.composeChartDataBySeverity(filterDate,"WARN");
                List<HourlyLogStats> stats = List.of(
                        new HourlyLogStats("WARN", infoCounts)
                );
                model.addAttribute("logChartData", new LogChartData(stats));
            }
            case "ERROR" -> {
                List<Long> infoCounts = retrievalService.composeChartDataBySeverity(filterDate,"ERROR");
                List<HourlyLogStats> stats = List.of(
                        new HourlyLogStats("ERROR", infoCounts)
                );
                model.addAttribute("logChartData", new LogChartData(stats));
            }
            default -> {
                List<Long> infoCounts = retrievalService.composeChartDataBySeverity(filterDate,"INFO");
                List<Long> warnCounts = retrievalService.composeChartDataBySeverity(filterDate,"WARN");
                List<Long> errorCounts = retrievalService.composeChartDataBySeverity(filterDate,"ERROR");
                List<HourlyLogStats> stats = List.of(
                        new HourlyLogStats("INFO", infoCounts),
                        new HourlyLogStats("WARN", warnCounts),
                        new HourlyLogStats("ERROR", errorCounts)
                );
                model.addAttribute("logChartData", new LogChartData(stats));
            }
        }
        var allApps = retrievalService.listAllApplications();
        var dataSourceList = retrievalService.listAllDataSources();
        model.addAttribute("applications",allApps);
        model.addAttribute("dataSources", dataSourceList);
        model.addAttribute("selectedAppId", filterResource.selectedAppId());
        model.addAttribute("selectedSource", filterResource.selectedSourceId());
        model.addAttribute("selectedSeverity", filterResource.selectedSeverity());
        model.addAttribute("logsFilter", filterResource);
        var logs = retrievalService.listLogs(
                filterResource.selectedDate(),
                filterResource.selectedSeverity(),
                filterResource.selectedAppId(),
                filterResource.selectedSourceId());
        model.addAttribute("logs",logs);
        model.addAttribute("selectedDate", filterResource.selectedDate());
        return "logs-list";
    }
    @GetMapping("/logs/{id}")
    public String logsDetailed(@PathVariable("id") String logId, Model model){
        var optionalLog = retrievalService.findLogsById(logId);
        if (optionalLog.isEmpty()) throw new RuntimeException("Invalid log id " + logId);
        var metadata = retrievalService.findMetadataByLogId(logId);
        model.addAttribute("log",optionalLog.get());
        model.addAttribute("metadata",metadata);
        return "logs-detailed";
    }




}
