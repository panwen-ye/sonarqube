package org.sonarsource.plugins.example.check;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.config.Configuration;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonarsource.plugins.example.rules.JavaRulesDefinition;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class MySensor implements Sensor {

    private static final Logger LOGGER = Loggers.get(MySensor.class);
    private final FileSystem fileSystem;
    private final Configuration configuration;

    public MySensor(FileSystem fileSystem, Configuration configuration) {
        this.fileSystem = fileSystem;
        this.configuration = configuration;
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("My Sensor");
        descriptor.onlyOnLanguage("java");
        descriptor.createIssuesForRuleRepositories(JavaRulesDefinition.REPOSITORY);
    }

    @Override
    public void execute(SensorContext context) {
        String searchStr = "nihao"; // 要搜索的字符串
        LOGGER.info("Searching for '{}'", searchStr);

        fileSystem.inputFiles(fileSystem.predicates().hasLanguage("java")).forEach(file -> {
            LOGGER.info("Analyzing {}", file.relativePath());
            try {
                String content = new String(Files.readAllBytes(file.path()), StandardCharsets.UTF_8);
                if (content.contains(searchStr)) {
                    LOGGER.info("Found '{}' in {}", searchStr, file.relativePath());
                    NewIssue issue = context.newIssue().forRule(JavaRulesDefinition.RULE_ON_LINE_1);
                    NewIssueLocation location = issue.newLocation().on(file).message("Found '" + searchStr + "'");
                    issue.at(location).save();
                }
            } catch (IOException e) {
                LOGGER.error("Error while reading file {}", file.relativePath(), e);
            }
        });
    }
}
