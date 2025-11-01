package ru.practicum.stats.analyzer;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.practicum.stats.analyzer.service.SimilarityProcessor;
import ru.practicum.stats.analyzer.service.UserActionProcessor;

@Component
@RequiredArgsConstructor
public class AnalyzerRunner implements CommandLineRunner {
    private final UserActionProcessor userActionProcessor;
    private final SimilarityProcessor similarityProcessor;

    @Override
    public void run(String... args) throws Exception {
        Thread userActionThread = new Thread(userActionProcessor);
        userActionThread.setName("UserActionHandlerThread");
        userActionThread.start();

        similarityProcessor.start();
    }
}
